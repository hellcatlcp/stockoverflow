package info.longlost.stockoverflow;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.androidplot.xy.LineAndPointFormatter;
import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYPlot;
import com.androidplot.xy.XYSeries;

import java.util.Arrays;

import info.longlost.stockoverflow.data.StockContract.PortfolioStockMap;
import info.longlost.stockoverflow.data.StockContract.StockEntry;
import info.longlost.stockoverflow.data.StockContract.PortfolioEntry;

/**
 * Displays collated info about all stocks in this portfolio.
 */
public class PortfolioFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_PORTFOLIO_ID = "portfolio_id";

    private static final int PORTFOLIO_LOADER = 100;
    private static final int STOCK_MAP_LOADER = 101;
    private static final int PRICE_LOADER = 102;

    private long mStartDate;
    private long mEndDate;
    private long mPortfolioId;
    private String mPortfolioName;

    private ListView mStockListView;
    private SimpleCursorAdapter mStockListAdapter;

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static PortfolioFragment newInstance(long portfolioId) {
        PortfolioFragment fragment = new PortfolioFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_PORTFOLIO_ID, portfolioId);
        fragment.setArguments(args);
        return fragment;
    }

    public PortfolioFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mPortfolioId = getArguments().getLong(ARG_PORTFOLIO_ID);
        }


    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        //setHasOptionsMenu(true);
        getLoaderManager().initLoader(STOCK_MAP_LOADER, Bundle.EMPTY, this);
        getLoaderManager().initLoader(PORTFOLIO_LOADER, Bundle.EMPTY, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_portfolio, container, false);

        // initialize our XYPlot
        initPlot(rootView);

        mStockListView = (ListView) rootView.findViewById(R.id.stock_list);
        mStockListAdapter = new SimpleCursorAdapter(getActivity(),
                R.layout.stock_list_item,
                null,
                new String[] { StockEntry.COLUMN_TICKER, PortfolioStockMap.COLUMN_STOCK_AMOUNT },
                new int[] { R.id.stock_name, R.id.stock_amount },
                0);
        mStockListView.setAdapter(mStockListAdapter);

        return rootView;
    }

    private void initPlot(View rootView) {
        XYPlot plot = (XYPlot) rootView.findViewById(R.id.mySimpleXYPlot);

        // Create a couple arrays of y-values to plot:
        Number[] series1Numbers = {1, 8, 5, 2, 7, 4};
        Number[] series2Numbers = {4, 6, 3, 8, 2, 10};

        // Turn the above arrays into XYSeries':
        XYSeries series1 = new SimpleXYSeries(
                Arrays.asList(series1Numbers),          // SimpleXYSeries takes a List so turn our array into a List
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, // Y_VALS_ONLY means use the element index as the x value
                "Series1");                             // Set the display title of the series

        // same as above
        XYSeries series2 = new SimpleXYSeries(Arrays.asList(series2Numbers), SimpleXYSeries.ArrayFormat.Y_VALS_ONLY, "Series2");

        // Create a formatter to use for drawing a series using LineAndPointRenderer
        // and configure it from xml:
        LineAndPointFormatter series1Format = new LineAndPointFormatter();
        series1Format.configure(getActivity(),
                R.xml.line_point_formatter);
        series1Format.setPointLabeler(null);

        // add a new series' to the xyplot:
        plot.addSeries(series1, series1Format);

        // same as above:
        LineAndPointFormatter series2Format = new LineAndPointFormatter();
        series2Format.configure(getActivity(),
                R.xml.line_point_formatter);
        plot.addSeries(series2, series2Format);

        // reduce the number of range labels
        plot.setTicksPerRangeLabel(3);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActionBarListener.onUpdateActionBar(getString(R.string.title_portfolio));
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PORTFOLIO_LOADER:
                return new CursorLoader(getActivity(),
                        PortfolioEntry.buildPortfolioUri(mPortfolioId),
                        new String[]{
                                PortfolioEntry._ID,
                                PortfolioEntry.COLUMN_PORTFOLIO_NAME
                        },
                        null,
                        null,
                        null);
            case STOCK_MAP_LOADER:
                return new CursorLoader(getActivity(),
                        PortfolioStockMap.buildPortfolioIdStockLatestPriceUri(mPortfolioId),
                        new String[] {
                                PortfolioStockMap._ID,
                                PortfolioStockMap.COLUMN_PORTFOLIO_ID,
                                PortfolioStockMap.COLUMN_STOCK_ID,
                                StockEntry.COLUMN_TICKER,
                                PortfolioStockMap.COLUMN_STOCK_AMOUNT
                        },
                        null,
                        null,
                        null);
            case PRICE_LOADER:
                return new CursorLoader(getActivity(),
                        PortfolioStockMap.buildPortfolioIdPriceFromToUri(mPortfolioId, mStartDate,
                                mEndDate),
                        new String[] {
                                PortfolioStockMap._ID,
                                PortfolioStockMap.COLUMN_PORTFOLIO_ID,
                                PortfolioStockMap.COLUMN_STOCK_ID,
                                StockEntry.COLUMN_TICKER,
                                PortfolioStockMap.COLUMN_STOCK_AMOUNT
                        },
                        null,
                        null,
                        null);
            default:
                return null;
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case PORTFOLIO_LOADER:
                int nameIdx = data.getColumnIndex(PortfolioEntry.COLUMN_PORTFOLIO_NAME);
                data.moveToFirst();
                mPortfolioName = data.getString(nameIdx);
                mActionBarListener.onUpdateActionBar(mPortfolioName);
                break;
            case STOCK_MAP_LOADER:
                mStockListAdapter.swapCursor(data);
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case PORTFOLIO_LOADER:
                mPortfolioName = null;
                break;
            case STOCK_MAP_LOADER:
                mStockListAdapter.swapCursor(null);
                break;
        }
    }
}
