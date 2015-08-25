package info.longlost.stockoverflow.sync.yql;

import android.content.ContentValues;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Vector;

import info.longlost.stockoverflow.data.StockContract.LatestPriceEntry;

/**
 * Created by ldenison on 20/08/2015.
 */
public class Contract {

    public static final String TAG = "yql." + Contract.class.getSimpleName();

    public static final SimpleDateFormat DATE_FORMAT =
            new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");

    public static final String PRICE_QUERY_FMT = "select from yahoo.finance.quote where symbol " +
            "in (s%)";
    public static final String YQL_BASE_URL = "https://query.yahooapis.com/v1/public/yql?";
    public static final String QUERY_PARAM = "q";

    // These are the names of the JSON objects that need to be extracted.

    // JSON field information
    public static final String YQL_QUERY = "query";
    public static final String YQL_COUNT = "count";
    public static final String YQL_CREATED = "created";
    public static final String YQL_RESULTS = "results";

    public static final String YQL_QUOTE = "quote";

    public static final String YQL_SYMBOL = "Symbol";
    public static final String YQL_AVG_DAY_VOLUME = "AverageDailyVolume";
    public static final String YQL_CHANGE = "Change";
    public static final String YQL_DAY_LOW = "DaysLow";
    public static final String YQL_DAY_HIGH = "DaysHigh";
    public static final String YQL_LAST_TRADE = "LastTradePriceOnly";
    public static final String YQL_DAY_RANGE = "DaysRange";
    public static final String YQL_VOLUME = "Volume";

    public static URL getQueryUrl(String[] tickers) throws MalformedURLException {
        if (tickers.length > 0) {
            String tickerStr = "\"" + TextUtils.join("\",\"", tickers) + "\"";
            String priceQuery = String.format(PRICE_QUERY_FMT, tickerStr);

            Uri builtUri = Uri.parse(YQL_BASE_URL).buildUpon()
                    .appendQueryParameter(QUERY_PARAM, priceQuery)
                    .build();

            return new URL(builtUri.toString());
        } else {
            return null;
        }
    }

    /**
     *
     */
    public static Vector<ContentValues> getPriceDataFromJson(String jsonStr,
                                                             Map<String, Long> tickerMap)
            throws JSONException, ParseException {

        // Now we have a String representing the price data in JSON Format.
        // Fortunately parsing is easy: constructor takes the JSON string and converts it
        // into an Object hierarchy for us.

        JSONObject yqlJson = new JSONObject(jsonStr);
        JSONObject queryJson = yqlJson.getJSONObject(YQL_QUERY);
        JSONObject resultsJson = queryJson.getJSONObject(YQL_RESULTS);
        JSONArray quoteArray = new JSONArray();
        JSONObject resultJson;

        int count = queryJson.getInt(YQL_COUNT);
        Vector<ContentValues> cVVector = new Vector<ContentValues>(count);

        if (count == 1) {
            resultJson = resultsJson.getJSONObject(YQL_QUOTE);
            quoteArray.put(resultJson);
        } else if (count > 1) {
            quoteArray = resultsJson.getJSONArray(YQL_QUOTE);
        }

        for(int i = 0; i < quoteArray.length(); i++) {
            // These are the values that will be collected.
            String createdStr;
            Date createdDate;
            Long stockId;
            long created;
            String ticker;
            long avgDayVolume;
            double change;
            double dayLow;
            double dayHigh;
            double lastTrade;
            String dayRange;
            long volume;


            // Get the JSON object representing a symbol
            JSONObject quoteJson = quoteArray.getJSONObject(i);

            ticker = quoteJson.getString(YQL_SYMBOL);
            stockId = tickerMap.get(ticker);

            if (stockId != null) {
                // Get the update date
                createdStr = quoteJson.getString(YQL_CREATED);
                createdDate = DATE_FORMAT.parse(createdStr);
                created = createdDate.getTime();

                avgDayVolume = quoteJson.getLong(YQL_AVG_DAY_VOLUME);
                change = quoteJson.getDouble(YQL_CHANGE);
                dayLow = quoteJson.getDouble(YQL_DAY_LOW);
                dayHigh = quoteJson.getDouble(YQL_DAY_HIGH);
                lastTrade = quoteJson.getDouble(YQL_LAST_TRADE);
                dayRange = quoteJson.getString(YQL_DAY_RANGE);
                volume = quoteJson.getLong(YQL_VOLUME);

                ContentValues stockValues = new ContentValues();

                stockValues.put(LatestPriceEntry.COLUMN_STOCK_ID, stockId);
                stockValues.put(LatestPriceEntry.COLUMN_LAST_UPDATED, created);
                stockValues.put(LatestPriceEntry.COLUMN_LAST_TRADE_PRICE, lastTrade);
                stockValues.put(LatestPriceEntry.COLUMN_CHANGE, change);
                stockValues.put(LatestPriceEntry.COLUMN_DAY_LOW, dayLow);
                stockValues.put(LatestPriceEntry.COLUMN_DAY_HIGH, dayHigh);
                stockValues.put(LatestPriceEntry.COLUMN_DAY_RANGE, dayRange);
                stockValues.put(LatestPriceEntry.COLUMN_VOLUME, volume);
                stockValues.put(LatestPriceEntry.COLUMN_AVG_DAY_VOLUME, avgDayVolume);

                cVVector.add(stockValues);
            } else {
                Log.w(TAG, "Skipping unknown ticker: " + ticker);
            }
        }

        return cVVector;
    }
}
