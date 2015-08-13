package info.longlost.stockoverflow.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import info.longlost.stockoverflow.data.StockContract.StockEntry;
import info.longlost.stockoverflow.data.StockContract.PortfolioEntry;
import info.longlost.stockoverflow.data.StockContract.PortfolioStockMap;


/**
 * Created by helenparsons on 8/2/15.
 */


public class StockDBHelper extends SQLiteOpenHelper{

    // If you change the database schema, you must increment the database version.
    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "stocks.db";

    public StockDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_PORTFOLIOS_TABLE = "CREATE TABLE " + PortfolioEntry.TABLE_NAME + " (" +
                PortfolioEntry._ID + " INTEGER PRIMARY KEY," +
                PortfolioEntry.COLUMN_PORTFOLIO_NAME + " TEXT UNIQUE NOT NULL);";

        db.execSQL(SQL_CREATE_PORTFOLIOS_TABLE);

        final String SQL_CREATE_STOCKS_TABLE = "CREATE TABLE " + StockEntry.TABLE_NAME + " (" +
                StockEntry._ID + " INTEGER PRIMARY KEY," +
                StockEntry.COLUMN_TICKER + " TEXT UNIQUE NOT NULL);";

        db.execSQL(SQL_CREATE_STOCKS_TABLE);

        final String SQL_CREATE_STOCKS_MAP = "CREATE TABLE " + PortfolioStockMap.TABLE_NAME + " (" +
                PortfolioStockMap._ID + " INTEGER PRIMARY KEY," +
                PortfolioStockMap.COLUMN_PORTFOLIO_ID + " INTEGER NOT NULL," +
                PortfolioStockMap.COLUMN_STOCK_ID + " INTEGER NOT NULL," +
                PortfolioStockMap.COLUMN_STOCK_AMOUNT + " INTEGER NOT NULL);";

        db.execSQL(SQL_CREATE_STOCKS_MAP);

        final String SQL_CREATE_STOCKS_VIEW = "CREATE VIEW " + PortfolioStockMap.STOCKS_VIEW +
                " AS SELECT * FROM " + StockEntry.TABLE_NAME + " LEFT OUTER JOIN " +
                PortfolioStockMap.TABLE_NAME + " ON " +
                StockEntry.TABLE_NAME + "." + StockEntry._ID + " = " +
                PortfolioStockMap.TABLE_NAME + "." + PortfolioStockMap.COLUMN_STOCK_ID;

        db.execSQL(SQL_CREATE_STOCKS_VIEW);

        // Create static 'My Portfolio' Entry
        ContentValues values = new ContentValues();
        values.put(PortfolioEntry.COLUMN_PORTFOLIO_NAME, "My Portfolio");
        long portfolio_id = db.insert(PortfolioEntry.TABLE_NAME, null, values);

        // Create dummy GOOG data
        values.clear();
        values.put(StockEntry.COLUMN_TICKER, "GOOG");
        long stock_id = db.insert(StockEntry.TABLE_NAME, null, values);
        values.clear();
        values.put(PortfolioStockMap.COLUMN_PORTFOLIO_ID, portfolio_id);
        values.put(PortfolioStockMap.COLUMN_STOCK_ID, stock_id);
        values.put(PortfolioStockMap.COLUMN_STOCK_AMOUNT, 10);
        db.insert(PortfolioStockMap.TABLE_NAME, null, values);

        // Create dummy GOOGL data
        values.clear();
        values.put(StockEntry.COLUMN_TICKER, "GOOGL");
        stock_id = db.insert(StockEntry.TABLE_NAME, null, values);
        values.clear();
        values.put(PortfolioStockMap.COLUMN_PORTFOLIO_ID, portfolio_id);
        values.put(PortfolioStockMap.COLUMN_STOCK_ID, stock_id);
        values.put(PortfolioStockMap.COLUMN_STOCK_AMOUNT, 10);
        db.insert(PortfolioStockMap.TABLE_NAME, null, values);

        // Create dummy APPL data
        values.clear();
        values.put(StockEntry.COLUMN_TICKER, "APPL");
        stock_id = db.insert(StockEntry.TABLE_NAME, null, values);
        values.clear();
        values.put(PortfolioStockMap.COLUMN_PORTFOLIO_ID, portfolio_id);
        values.put(PortfolioStockMap.COLUMN_STOCK_ID, stock_id);
        values.put(PortfolioStockMap.COLUMN_STOCK_AMOUNT, 10);
        db.insert(PortfolioStockMap.TABLE_NAME, null, values);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //TODO (helenparsons): decide how do deals with manually entered historical data onUpgrade 
    }
}
