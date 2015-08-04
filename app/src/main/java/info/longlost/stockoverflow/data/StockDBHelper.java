package info.longlost.stockoverflow.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import info.longlost.stockoverflow.data.StockContract.StockEntry;
import info.longlost.stockoverflow.data.StockContract.PortfolioEntry;


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
        final String SQL_CREATE_STOCKS_TABLE = "CREATE TABLE " + PortfolioEntry.TABLE_NAME + " (" +
                StockEntry._ID + " INTEGER PRIMARY KEY," +
                StockEntry.COLUMN_TICKER + " TEXT UNIQUE NOT NULL);";

        db.execSQL(SQL_CREATE_STOCKS_TABLE);

        final String SQL_CREATE_PORTFOLIOS_TABLE = "CREATE TABLE " + PortfolioEntry.TABLE_NAME + " (" +
                PortfolioEntry._ID + " INTEGER PRIMARY KEY," +
                PortfolioEntry.COLUMN_PORTFOLIO_NAME + " TEXT UNIQUE NOT NULL);";

        db.execSQL(SQL_CREATE_PORTFOLIOS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        //TODO (helenparsons): decide how do deals with manually entered historical data onUpgrade 
    }
}
