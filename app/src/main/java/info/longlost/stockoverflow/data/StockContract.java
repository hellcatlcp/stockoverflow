package info.longlost.stockoverflow.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by helenparsons on 8/1/15.
 */
public class StockContract {

    public static final String CONTENT_AUTHORITY = "info.longlost.stockoverflow";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String STOCKS_LOCATION =  "stock";
    public static final String PORTFOLIOS_LOCATION =  "portfolios";

    //Create database table for a set of stocks

    public static final class StockEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(STOCKS_LOCATION).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + STOCKS_LOCATION;
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + STOCKS_LOCATION;
        public static final String TABLE_NAME = "stocks";
        public static final String COLUMN_TICKER = "ticker";


        public static Uri buildStockUri(String ticker) {
            return CONTENT_URI.buildUpon().appendPath(ticker).build();
        }

        public static String getTickerFromUri(Uri uri) {
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


        public static Uri buildPortfolioUri(String  portfolio ) {
            return CONTENT_URI.buildUpon().appendPath(portfolio).build();
        }

        public static String getPortfolioFromUri(Uri uri) {
            return uri.getPathSegments().get(1);
        }

    }


}


