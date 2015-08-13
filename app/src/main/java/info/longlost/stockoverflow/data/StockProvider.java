package info.longlost.stockoverflow.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

import info.longlost.stockoverflow.util.SelectionBuilder;

import static info.longlost.stockoverflow.data.StockContract.StockEntry;
import static info.longlost.stockoverflow.data.StockContract.PortfolioEntry;
import static info.longlost.stockoverflow.data.StockContract.PortfolioStockMap;
import static info.longlost.stockoverflow.data.StockContract.STOCKS_LOCATION;
import static info.longlost.stockoverflow.data.StockContract.PORTFOLIOS_LOCATION;

/**
 * Created by ldenison on 02/08/2015.
 */
public class StockProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private StockDBHelper mOpenHelper;

    static final int STOCK_ID = 100;
    static final int STOCK = 101;

    static final int PORTFOLIO_ID = 200;
    static final int PORTFOLIO = 201;
    static final int PORTFOLIO_STOCKS = 202;
    static final int PORTFOLIO_ID_STOCKS = 203;
    static final int PORTFOLIO_STOCKS_ID = 204;

    @Override
    public boolean onCreate() {
        mOpenHelper = new StockDBHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = StockContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, STOCKS_LOCATION + "/*", STOCK_ID);
        matcher.addURI(authority, STOCKS_LOCATION, STOCK);

        matcher.addURI(authority, PORTFOLIOS_LOCATION + "/*", PORTFOLIO_ID);
        matcher.addURI(authority, PORTFOLIOS_LOCATION, PORTFOLIO);
        matcher.addURI(authority, PORTFOLIOS_LOCATION + "-" + STOCKS_LOCATION, PORTFOLIO_STOCKS);
        matcher.addURI(authority, PORTFOLIOS_LOCATION + "/*/" + STOCKS_LOCATION,
                PORTFOLIO_ID_STOCKS);
        matcher.addURI(authority, PORTFOLIOS_LOCATION + "/*/" + STOCKS_LOCATION + "/*",
                PORTFOLIO_STOCKS_ID);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case STOCK_ID:
                return StockEntry.CONTENT_ITEM_TYPE;
            case STOCK:
                return StockEntry.CONTENT_TYPE;
            case PORTFOLIO_ID:
                return PortfolioEntry.CONTENT_ITEM_TYPE;
            case PORTFOLIO:
                return PortfolioEntry.CONTENT_TYPE;
            case PORTFOLIO_STOCKS:
                return PortfolioStockMap.CONTENT_TYPE;
            case PORTFOLIO_STOCKS_ID:
                return PortfolioStockMap.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        Cursor retCursor;
        String tableName;
        SelectionBuilder builder = new SelectionBuilder(selection, selectionArgs);

        switch (sUriMatcher.match(uri)) {
            // "/stock/*"
            case STOCK_ID: {
                tableName = StockEntry.TABLE_NAME;
                builder.add(StockEntry._ID + "=?",
                        new String[] { StockEntry.getStockId(uri) });
                break;
            }
            // "/stock"
            case STOCK: {
                tableName = StockEntry.TABLE_NAME;
                break;
            }
            // "/portfolio/*"
            case PORTFOLIO_ID: {
                tableName = PortfolioEntry.TABLE_NAME;
                builder.add(PortfolioEntry._ID + "=?",
                        new String[] { PortfolioEntry.getPortfolioId(uri) });
                break;
            }
            // "/portfolio"
            case PORTFOLIO: {
                tableName = PortfolioEntry.TABLE_NAME;
                break;
            }
            // "/portfolio/*/stock"
            case PORTFOLIO_STOCKS: {
                tableName = PortfolioStockMap.STOCKS_VIEW;
                break;
            }
            case PORTFOLIO_ID_STOCKS: {
                tableName = PortfolioStockMap.STOCKS_VIEW;
                builder.add(PortfolioEntry._ID + "=?",
                        new String[] { PortfolioEntry.getPortfolioId(uri) });
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor = mOpenHelper.getReadableDatabase().query(
                tableName,
                projection,
                builder.build(),
                builder.getSelectionArgs(),
                null,
                null,
                sortOrder
        );

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case STOCK: {
                long _id = db.insert(StockEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = StockEntry.buildStockUri(values.getAsLong(StockEntry._ID));
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            case PORTFOLIO: {
                long _id = db.insert(PortfolioEntry.TABLE_NAME, null, values);
                if ( _id > 0 )
                    returnUri = PortfolioEntry.buildPortfolioUri(values.getAsLong(
                        PortfolioEntry._ID));
                else
                    throw new SQLException("Failed to insert row into " + uri);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsDeleted;

        // this makes delete all rows return the number of rows deleted
        if ( null == selection ) selection = "1";

        switch (match) {
            case STOCK:
                rowsDeleted = db.delete(StockEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PORTFOLIO:
                rowsDeleted = db.delete(PortfolioEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because a null deletes all rows
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int rowsUpdated;

        switch (match) {
            case STOCK:
                rowsUpdated = db.update(StockEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PORTFOLIO:
                rowsUpdated = db.update(PortfolioEntry.TABLE_NAME, values, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @Override
    public int bulkInsert(Uri uri, ContentValues[] values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int returnCount = 0;
        switch (match) {
            case STOCK:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(StockEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            case PORTFOLIO:
                db.beginTransaction();
                try {
                    for (ContentValues value : values) {
                        long _id = db.insert(PortfolioEntry.TABLE_NAME, null, value);
                        if (_id != -1) {
                            returnCount++;
                        }
                    }
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                getContext().getContentResolver().notifyChange(uri, null);
                return returnCount;
            default:
                return super.bulkInsert(uri, values);
        }
    }
}
