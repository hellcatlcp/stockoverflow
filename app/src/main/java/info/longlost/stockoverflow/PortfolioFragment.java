package info.longlost.stockoverflow;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Displays collated info about all stocks in this portfolio.
 */
public class PortfolioFragment extends Fragment {

    private static final String ARG_PORTFOLIO_ID = "portfolio_id";

    private long mPortfolioId;

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

    public PortfolioFragment() {
    }

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
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
