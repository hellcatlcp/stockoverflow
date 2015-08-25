package info.longlost.stockoverflow.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.SimpleTimeZone;
import java.util.Vector;

import info.longlost.stockoverflow.R;
import info.longlost.stockoverflow.data.StockContract.StockEntry;
import info.longlost.stockoverflow.data.StockContract.LatestPriceEntry;
import info.longlost.stockoverflow.sync.yql.Contract;

import static java.util.SimpleTimeZone.*;

/**
 * Created by ldenison on 20/08/2015.
 */
public class PriceSyncAdapter extends AbstractThreadedSyncAdapter {

    public static final String TAG = PriceSyncAdapter.class.getSimpleName();
    public static final SimpleTimeZone UTC = new SimpleTimeZone(UTC_TIME, "UTC");

    // Interval at which to sync with the prices, in seconds.
    public static final int SYNC_INTERVAL = 60 * 5;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL/3;

    public PriceSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }
    
    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        Log.d(TAG, "Start price sync.");

        Map<String, Long> tickerMap = getTickerMap();
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        try {
            URL url = Contract.getQueryUrl(tickerMap.keySet().toArray(new String[] {}));

            // Create the request to OpenWeatherMap, and open the connection
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }

            Vector<ContentValues> cVVector = Contract.getPriceDataFromJson(
                    buffer.toString(), tickerMap);
            storePriceData(cVVector);
        } catch (IOException e) {
            Log.e(TAG, "Error fetching price data:", e);
        } catch (JSONException|ParseException e) {
            Log.e(TAG, "Error parsing price data:", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(TAG, "Error closing stream when syncing price data:", e);
                }
            }

            Log.d(TAG, "Finish price sync.");
        }
        return;
    }

    private Map<String, Long> getTickerMap() {
        Map<String, Long> result = new HashMap<String, Long>();


        Cursor tickerCursor = getContext().getContentResolver().query(
                StockEntry.CONTENT_URI,
                new String[] { StockEntry._ID, StockEntry.COLUMN_TICKER },
                null,
                null,
                null);

        int idIdx = tickerCursor.getColumnIndex(StockEntry._ID);
        int tickerIdx = tickerCursor.getColumnIndex(StockEntry.COLUMN_TICKER);

        for (tickerCursor.moveToFirst(); !tickerCursor.isAfterLast(); tickerCursor.moveToNext()) {
            result.put(tickerCursor.getString(tickerIdx), tickerCursor.getLong(idIdx));
        }

        return result;
    }

    /**
     * Store a cVVector of latest price data.
     */
    private void storePriceData(Vector<ContentValues> cVVector) {

        Date now = new Date();
        Calendar calendar = new GregorianCalendar(UTC);
        calendar.setTime(now);
        calendar.add(Calendar.DAY_OF_MONTH, -1);

        int inserted = 0;
        // add to database
        if ( cVVector.size() > 0 ) {
            ContentValues[] cvArray = new ContentValues[cVVector.size()];
            cVVector.toArray(cvArray);
            getContext().getContentResolver().bulkInsert(LatestPriceEntry.CONTENT_URI, cvArray);

            // delete old data so we don't build up an endless history
            getContext().getContentResolver().delete(LatestPriceEntry.CONTENT_URI,
                    LatestPriceEntry.COLUMN_LAST_UPDATED + " <= ?",
                    new String[] {Long.toString(calendar.getTimeInMillis())});
        }

        Log.d(TAG, "Sync Complete. " + cVVector.size() + " Inserted");
    }



    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if ( null == accountManager.getPassword(newAccount) ) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    // TODO (helenparsons): This configures the device to automatically start syncing in the
    // TODO (helenparsons)  background every 5 hours after there is an appropriate account
    // TODO (helenparsons)  on the device.  This should probably be removed since we don't want
    // TODO (helenparsons)  to sync in the background at all.
    private static void onAccountCreated(Account newAccount, Context context) {
        /*
         * Since we've created an account
         */
        PriceSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        /*
         * Without calling setSyncAutomatically, our periodic sync will not be enabled.
         */
        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        /*
         * Finally, let's do a sync to get things started
         */
        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}
