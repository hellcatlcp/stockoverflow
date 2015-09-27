package info.longlost.stockoverflow.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import info.longlost.stockoverflow.util.SelectionBuilder;

import info.longlost.stockoverflow.data.StockContract.StockEntry;
import info.longlost.stockoverflow.data.StockContract.PortfolioEntry;
import info.longlost.stockoverflow.data.StockContract.PortfolioStockMap;
import info.longlost.stockoverflow.data.StockContract.PriceEntry;
import info.longlost.stockoverflow.data.StockContract.LatestPriceEntry;
import static info.longlost.stockoverflow.data.StockContract.LATEST_LOCATION;
import static info.longlost.stockoverflow.data.StockContract.STOCKS_LOCATION;
import static info.longlost.stockoverflow.data.StockContract.PORTFOLIOS_LOCATION;
import static info.longlost.stockoverflow.data.StockContract.PRICE_LOCATION;
import static info.longlost.stockoverflow.data.StockContract.CACHE_LOCATION;

/**
 * Created by ldenison on 02/08/2015.
 */
public class StockProvider extends ContentProvider {

    // The URI Matcher used by this content provider.
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private StockDBHelper mOpenHelper;

    static final int STOCK = 100;
    static final int STOCK_ID = 101;
    static final int STOCK_ID_LATEST_PRICE = 102;
    static final int STOCK_ID_PRICE_FROM_TO = 103;
    static final int STOCK_ID_PRICE_FROM_TO_CACHE = 104;

    static final int PORTFOLIO = 200;
    static final int PORTFOLIO_ID = 201;
    static final int PORTFOLIO_ID_STOCK_LATEST_PRICE = 202;
    static final int PORTFOLIO_ID_STOCK_ID = 203;
    static final int PORTFOLIO_ID_PRICE_FROM_TO = 204;
    static final int PORTFOLIO_ID_PRICE_FROM_TO_CACHE = 205;


    @Override
    public boolean onCreate() {
        mOpenHelper = new StockDBHelper(getContext());
        return true;
    }

    static UriMatcher buildUriMatcher() {
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = StockContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, STOCKS_LOCATION, STOCK);
        matcher.addURI(authority, STOCKS_LOCATION + "/*", STOCK_ID);
        matcher.addURI(authority, STOCKS_LOCATION + "/*/" + LATEST_LOCATION, STOCK_ID_LATEST_PRICE);
        matcher.addURI(authority, STOCKS_LOCATION + "/*/" + PRICE_LOCATION + "/from/*/to/*",
                STOCK_ID_PRICE_FROM_TO);
        matcher.addURI(authority, STOCKS_LOCATION + "/*/" + PRICE_LOCATION + "/from/*/to/*/" +
                CACHE_LOCATION, STOCK_ID_PRICE_FROM_TO_CACHE);

        matcher.addURI(authority, PORTFOLIOS_LOCATION, PORTFOLIO);
        matcher.addURI(authority, PORTFOLIOS_LOCATION + "/*", PORTFOLIO_ID);
        matcher.addURI(authority, PORTFOLIOS_LOCATION + "/*/" + STOCKS_LOCATION + "/" +
                LATEST_LOCATION, PORTFOLIO_ID_STOCK_LATEST_PRICE);
        matcher.addURI(authority, PORTFOLIOS_LOCATION + "/*/" + STOCKS_LOCATION + "/*",
                PORTFOLIO_ID_STOCK_ID);
        matcher.addURI(authority, PORTFOLIOS_LOCATION + "/*/" + PRICE_LOCATION + "/from/*/to/*",
                PORTFOLIO_ID_PRICE_FROM_TO);
        matcher.addURI(authority, PORTFOLIOS_LOCATION + "/*/" + PRICE_LOCATION + "/from/*/to/*/" +
                CACHE_LOCATION, PORTFOLIO_ID_PRICE_FROM_TO_CACHE);

        return matcher;
    }

    @Override
    public String getType(Uri uri) {
        // Use the Uri Matcher to determine what kind of URI this is.
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case STOCK:
                return StockEntry.CONTENT_TYPE;
            case STOCK_ID:
                return StockEntry.CONTENT_ITEM_TYPE;
            case STOCK_ID_LATEST_PRICE:
                return LatestPriceEntry.CONTENT_ITEM_TYPE;
            case PORTFOLIO:
                return PortfolioEntry.CONTENT_TYPE;
            case PORTFOLIO_ID:
                return PortfolioEntry.CONTENT_ITEM_TYPE;
            case PORTFOLIO_ID_STOCK_LATEST_PRICE:
                return PortfolioStockMap.CONTENT_TYPE;
            case PORTFOLIO_ID_STOCK_ID:
                return PortfolioStockMap.CONTENT_ITEM_TYPE;
            case STOCK_ID_PRICE_FROM_TO:
                return PriceEntry.CONTENT_TYPE;
            case STOCK_ID_PRICE_FROM_TO_CACHE:
                return PriceEntry.CACHE_CONTENT_TYPE;
            case PORTFOLIO_ID_PRICE_FROM_TO:
                return PriceEntry.CONTENT_TYPE;
            case PORTFOLIO_ID_PRICE_FROM_TO_CACHE:
                return PriceEntry.CACHE_CONTENT_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            // "/stock"
            case STOCK: {
                retCursor = db.query(StockEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            }
            // "/stock/*"
            case STOCK_ID: {
                SelectionBuilder builder = new SelectionBuilder(selection, selectionArgs);
                builder.add(StockEntry._ID + "=?", new String[] { StockEntry.getStockId(uri) });

                retCursor = db.query(StockEntry.TABLE_NAME, projection, builder.build(),
                        builder.getSelectionArgs(), null, null, sortOrder);
                break;
            }
            // "/portfolio"
            case PORTFOLIO: {
                retCursor = db.query(PortfolioEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            }
            // "/portfolio/*"
            case PORTFOLIO_ID: {
                SelectionBuilder builder = new SelectionBuilder(selection, selectionArgs);
                builder.add(PortfolioEntry._ID + "=?",
                        new String[] { PortfolioEntry.getPortfolioId(uri) });

                retCursor = db.query(PortfolioEntry.TABLE_NAME, projection, builder.build(),
                        builder.getSelectionArgs(), null, null, sortOrder);
                break;
            }
            // "/portfolio/*/stock"
            case PORTFOLIO_ID_STOCK_LATEST_PRICE: {
                SelectionBuilder builder = new SelectionBuilder(selection, selectionArgs);
                builder.add(PortfolioEntry._ID + "=?",
                        new String[] { PortfolioEntry.getPortfolioId(uri) });

                retCursor = db.query(PortfolioStockMap.PORTFOLIO_LATEST_PRICE_VIEW, projection,
                        builder.build(), builder.getSelectionArgs(), null, null, sortOrder);
                break;
            }
            // "/stocks/*/price/from/*/to/*"
            case STOCK_ID_PRICE_FROM_TO: {
                SelectionBuilder builder = new SelectionBuilder(selection, selectionArgs);
                builder.add(PriceEntry.COLUMN_STOCK_ID + "=?",
                        new String[] { PriceEntry.getStockPriceId(uri) })
                    .add(PriceEntry.COLUMN_DATE + ">=? AND " + PriceEntry.COLUMN_DATE + "<=?",
                        new String[] { PriceEntry.getStockPriceFromDate(uri),
                                PriceEntry.getStockPriceToDate(uri) });

                retCursor = db.query(PriceEntry.TABLE_NAME, projection, builder.build(),
                        builder.getSelectionArgs(), null, null, sortOrder);
                break;
            }
            // "/stocks/*/price/from/*/to/*/cache"
            case STOCK_ID_PRICE_FROM_TO_CACHE: {
                SelectionBuilder builder = new SelectionBuilder(selection, selectionArgs);
                builder.add(PriceEntry.COLUMN_STOCK_ID + "=?",
                        new String[] { PriceEntry.getStockPriceId(uri) })
                        .add(PriceEntry.COLUMN_START + "<=? AND " + PriceEntry.COLUMN_END + ">=?",
                                new String[]{PriceEntry.getStockPriceFromDate(uri),
                                        PriceEntry.getStockPriceToDate(uri)});

                retCursor = db.query(PriceEntry.CACHE_TABLE_NAME, projection, builder.build(),
                        builder.getSelectionArgs(), null, null, sortOrder);
                break;
            }
            // "/portfolio/*/price/from/*/to/*"
            case PORTFOLIO_ID_PRICE_FROM_TO: {
                SelectionBuilder builder = new SelectionBuilder(selection, selectionArgs);
                builder.add(PriceEntry.COLUMN_STOCK_ID + "=?",
                        new String[] { PriceEntry.getStockPriceId(uri) })
                        .add(PriceEntry.COLUMN_DATE + ">=? AND " + PriceEntry.COLUMN_DATE + "<=?",
                                new String[] { PriceEntry.getStockPriceFromDate(uri),
                                        PriceEntry.getStockPriceToDate(uri) });

                retCursor = db.query(PortfolioStockMap.PORTFOLIO_PRICE_VIEW, projection,
                        builder.build(), builder.getSelectionArgs(), null, null, sortOrder);
                break;
            }
            // "/portfolio/*/price/from/*/to/*/cache"
            case PORTFOLIO_ID_PRICE_FROM_TO_CACHE: {
                SelectionBuilder builder = new SelectionBuilder(selection, selectionArgs);
                builder.add(PriceEntry.COLUMN_STOCK_ID + "=?",
                        new String[] { PriceEntry.getStockPriceId(uri) })
                        .add(PriceEntry.COLUMN_START + "<=? AND " + PriceEntry.COLUMN_END + ">=?",
                                new String[]{PriceEntry.getStockPriceFromDate(uri),
                                        PriceEntry.getStockPriceToDate(uri)});

                retCursor = db.query(PortfolioStockMap.PORTFOLIO_PRICE_CACHE_VIEW, projection,
                        builder.build(), builder.getSelectionArgs(), null, null, sortOrder);
                break;
            }
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final int match = sUriMatcher.match(uri);
        Uri returnUri;

        switch (match) {
            case STOCK: {
                long id = simpleInsert(StockEntry.TABLE_NAME, values);
                returnUri = StockEntry.buildStockUri(id);
                break;
            }
            case PORTFOLIO: {
                long id = simpleInsert(PortfolioEntry.TABLE_NAME, values);
                returnUri = PortfolioEntry.buildPortfolioUri(id);
                break;
            }
            case PORTFOLIO_ID_STOCK_ID:
                simpleInsert(PortfolioStockMap.TABLE_NAME, values);
                returnUri = PortfolioStockMap.buildPortfolioIdStockIdUri(
                        values.getAsLong(PortfolioStockMap.COLUMN_PORTFOLIO_ID),
                        values.getAsLong(PortfolioStockMap.COLUMN_STOCK_ID));
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    private long simpleInsert(String tableName, ContentValues values) {
        long id = mOpenHelper.getWritableDatabase().insert(tableName, null, values);
        if (id > 0)
            return id;
        else
            throw new SQLException("Failed to insert row into " + tableName);
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
            case STOCK_ID_LATEST_PRICE:
                rowsDeleted = db.delete(LatestPriceEntry.TABLE_NAME, selection, selectionArgs);
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
        int count = 0;

        switch (match) {
            case STOCK:
                db.beginTransaction();
                try {
                    count = simpleBulkInsert(db, StockEntry.TABLE_NAME, values);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case PORTFOLIO:
                db.beginTransaction();
                try {
                    count = simpleBulkInsert(db, PortfolioEntry.TABLE_NAME, values);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            case STOCK_ID_PRICE_FROM_TO: {
                db.beginTransaction();
                try {
                    count = simpleBulkInsert(db, PriceEntry.TABLE_NAME, values);

                    ContentValues cacheValues = new ContentValues();
                    cacheValues.put(PriceEntry.COLUMN_STOCK_ID, PriceEntry.getStockPriceId(uri));
                    cacheValues.put(PriceEntry.COLUMN_START, PriceEntry.getStockPriceFromDate(uri));
                    cacheValues.put(PriceEntry.COLUMN_END, PriceEntry.getStockPriceToDate(uri));
                    db.insert(PriceEntry.CACHE_TABLE_NAME, null, cacheValues);
                    db.setTransactionSuccessful();
                } finally {
                    db.endTransaction();
                }
                break;
            }
            default:
                return super.bulkInsert(uri, values);
        }

        if (count > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    private int simpleBulkInsert(SQLiteDatabase db, String tableName, ContentValues[] values) {
        int count = 0;
        for (ContentValues value : values) {
            long _id = db.insert(PortfolioEntry.TABLE_NAME, null, value);
            if (_id != -1) {
                count++;
            }
        }
        return count;
    }
}
