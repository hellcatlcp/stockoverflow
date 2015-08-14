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
import android.widget.TextView;

import info.longlost.stockoverflow.data.StockContract.PortfolioEntry;

/**
 * Displays collated info about all stocks in this portfolio.
 */
public class PortfolioFragment extends BaseFragment implements
        LoaderManager.LoaderCallbacks<Cursor>{

    private static final String ARG_PORTFOLIO_ID = "portfolio_id";

    private static final int PORTFOLIO_LOADER = 100;

    private long mPortfolioId;
    private String mPortfolioName;

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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_portfolio, container, false);
        return rootView;
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
                        new String[] {
                                PortfolioEntry._ID,
                                PortfolioEntry.COLUMN_PORTFOLIO_NAME
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
        int nameIdx = data.getColumnIndex(PortfolioEntry.COLUMN_PORTFOLIO_NAME);
        data.moveToFirst();
        mPortfolioName = data.getString(nameIdx);
        mActionBarListener.onUpdateActionBar(mPortfolioName);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mPortfolioName = null;
    }
}
