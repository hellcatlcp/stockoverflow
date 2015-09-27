package info.longlost.stockoverflow.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by helenparsons on 8/1/15.
 */
public class StockContract {

    public static final String CONTENT_AUTHORITY = "info.longlost.stockoverflow";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String STOCKS_LOCATION =  "stock";
    public static final String PORTFOLIOS_LOCATION =  "portfolio";
    public static final String PRICE_LOCATION = "price";
    public static final String LATEST_LOCATION = "latest";
    public static final String FROM_LOCATION = "from";
    public static final String TO_LOCATION = "to";

    // TODO (helenparsons): Add constant strings to represent that a stock is either unverified,
    // TODO (helenparsons)  verified or invalid called UNVERIFIED_VALUE, VERIFIED_VALUE and
    // TODO (helenparsons)  INVALID_VALUE.

    //Create database table for a set of stocks

    public static final class StockEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(STOCKS_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + STOCKS_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + STOCKS_LOCATION;
        public static final String TABLE_NAME = "stocks";
        public static final String COLUMN_TICKER = "ticker";

        // TODO (helenparsons): Add a status column to StockEntry.


        public static Uri buildStockUri(long stock_id) {
            return ContentUris.withAppendedId(CONTENT_URI, stock_id);
        }

        public static String getStockId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    //create full URI for a stock

    public static final class PortfolioEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PORTFOLIOS_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PORTFOLIOS_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PORTFOLIOS_LOCATION;
        public static final String TABLE_NAME = "portfolios";
        public static final String COLUMN_PORTFOLIO_NAME = "portfolio";


        public static Uri buildPortfolioUri(long portfolio_id) {
            return ContentUris.withAppendedId(CONTENT_URI, portfolio_id);
        }

        public static String getPortfolioId(Uri uri) {
            return uri.getPathSegments().get(1);
        }
    }

    public static final class PriceEntry implements BaseColumns {

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + STOCKS_LOCATION + "/" + PRICE_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + STOCKS_LOCATION + "/" + PRICE_LOCATION;
        public static final String TABLE_NAME = "prices";
        public static final String COLUMN_STOCK_ID = "stock_id";
        public static final String COLUMN_DATE = "price_date";
        public static final String COLUMN_OPEN = "open";
        public static final String COLUMN_HIGH = "high";
        public static final String COLUMN_LOW = "low";
        public static final String COLUMN_CLOSE = "close";
        public static final String COLUMN_VOLUME = "volume";

        // /stock/*/price/[from/*/to/*]

        public static String getStockPriceId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getStockPriceFromDate(Uri uri) {
            return uri.getPathSegments().get(4);
        }

        public static String getStockPriceToDate(Uri uri) {
            return uri.getPathSegments().get(6);
        }
    }

    public static final class LatestPriceEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon()
                .appendPath(STOCKS_LOCATION).appendPath(LATEST_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PRICE_LOCATION + "/" + LATEST_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PRICE_LOCATION + "/" + LATEST_LOCATION;
        public static final String TABLE_NAME = "latest_prices";
        public static final String COLUMN_STOCK_ID = "stock_id";
        public static final String COLUMN_LAST_UPDATED = "last_updated";
        public static final String COLUMN_LAST_TRADE_PRICE = "last_trade_price";
        public static final String COLUMN_DAY_HIGH = "day_high";
        public static final String COLUMN_DAY_LOW = "day_low";
        public static final String COLUMN_CHANGE = "change";
        public static final String COLUMN_DAY_RANGE = "day_range";
        public static final String COLUMN_VOLUME = "volume";
        public static final String COLUMN_AVG_DAY_VOLUME = "avg_day_volume";

        public static Uri buildStockLatestPriceUri(long stock_id) {
            return ContentUris.withAppendedId(StockEntry.CONTENT_URI, stock_id).buildUpon()
                    .appendPath(LATEST_LOCATION).build();
        }

        public static String getStockLatestPriceId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }

    public static final class PortfolioStockMap implements BaseColumns {

        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PORTFOLIOS_LOCATION + "-" + STOCKS_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PORTFOLIOS_LOCATION + "-" + STOCKS_LOCATION;

        public static final String TABLE_NAME = "portfolio_map";
        public static final String PORTFOLIO_LATEST_PRICE_VIEW = "portfolio_latest_price_view";
        public static final String PORTFOLIO_PRICE_VIEW = "portfolio_price_view";
        public static final String COLUMN_PORTFOLIO_ID = "portfolio_id";
        public static final String COLUMN_STOCK_ID = "stock_id";
        public static final String COLUMN_STOCK_AMOUNT = "stock_amount";

        public static Uri buildPortfolioIdStockLatestPriceUri(Long portfolioId) {
            return ContentUris.withAppendedId(PortfolioEntry.CONTENT_URI, portfolioId)
                    .buildUpon().appendPath(STOCKS_LOCATION).appendPath(LATEST_LOCATION).build();
        }

        public static Uri buildPortfolioIdStockIdUri(Long portfolioId, Long stockId) {
            return ContentUris.withAppendedId(PortfolioEntry.CONTENT_URI, portfolioId)
                    .buildUpon().appendPath(STOCKS_LOCATION)
                    .appendPath(stockId.toString()).build();
        }

        // /portfolio/*/price/[from/*/to/*]
        public static Uri buildPortfolioIdPriceFromToUri(Long portfolioId, Long from, Long to) {
            return ContentUris.withAppendedId(PortfolioEntry.CONTENT_URI, portfolioId)
                    .buildUpon().appendPath(PRICE_LOCATION).appendPath(FROM_LOCATION)
                    .appendPath(from.toString()).appendPath(TO_LOCATION)
                    .appendPath(to.toString()).build();
        }

        public static String getPortfolioPriceId(Uri uri) {
            return uri.getPathSegments().get(1);
        }

        public static String getPortfolioPriceFromDate(Uri uri) {
            return uri.getPathSegments().get(4);
        }

        public static String getPortfolioPriceToDate(Uri uri) {
            return uri.getPathSegments().get(6);
        }
    }
}


