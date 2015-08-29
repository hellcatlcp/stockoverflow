package info.longlost.stockoverflow.sync;

import android.app.IntentService;
import android.content.Intent;

/**
 * Created by ldenison on 26/08/2015.
 */
public class LatestPriceIntentService extends IntentService {

    /**
     * Creates an IntentService.  Invoked by your subclass's constructor.
     *
     * @param name Used to name the worker thread, important only for debugging.
     */
    public LatestPriceIntentService(String name) {
        super(name);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        // TODO (helenparsons): Extract the ticker symbol we are getting price data for, from the
        // TODO (helenparsons)  Intent extras.

        // TODO (helenparsons): By the time this service is used the relevant stock should have been
        // TODO (helenparsons)  added to the stock table so we can use getTickerMap() (see other
        // TODO (helenparsons)  todo) to fetch the map of stocks to IDs.

        // TODO (helenparsons): Use info.longlost.stockoverflow.sync.yql.Contract.getQueryUrl to
        // TODO (helenparsons)  create the url that we need to query to get the price data for our
        // TODO (helenparsons)  ticker.

        // TODO (helenparsons): Copy code from LatestPriceSyncAdapter.onPerformSync to query the url and
        // TODO (helenparsons)  read the resulting data into a buffer.

        // TODO (helenparsons): Use .sync.yql.Contract.getPriceDataFromJson to convert the json to
        // TODO (helenparsons)  a Vector<ColumnValues> which can be entered into the database.

        // TODO (helenparsons): If we got a vector with 0 results in it, it probably means that
        // TODO (helenparsons)  yahoo thinks the stock doesn't exist.  So we update the status of
        // TODO (helenparsons)  the stock entry to be StockContract.INVALID_VALUE

        // TODO (helenparsons): Use getContext().getContentResolver().insert(LatestPriceEntry.CONTENT_URI, ...)
        // TODO (helenparsons)  to insert the price data into the database.

        // TODO (helenparsons): If we got this far without an exception then we update the status
        // TODO (helenparsons)  in the stock table to be StockContract.VERIFIED_VALUE

        // TODO (helenparsons): If we got an IOException or JsonException then we likely had a
        // TODO (helenparsons)  network failure or some kind of auth failure.  In this case we
        // TODO (helenparsons)  still don't know if the stock is valid so we leave it in its current
        // TODO (helenparsons)  status (ie. UNVERIFIED).
    }

}
