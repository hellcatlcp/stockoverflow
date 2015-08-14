package info.longlost.stockoverflow;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class StockFragment extends BaseFragment {

    private static final String ARG_STOCK_ID = "stock_id";

    private long mStockId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param stockId Stock ticker to display.
     * @return A new instance of fragment StockFragment.
     */
    public static StockFragment newInstance(long stockId) {
        StockFragment fragment = new StockFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_STOCK_ID, stockId);
        fragment.setArguments(args);
        return fragment;
    }

    public StockFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStockId = getArguments().getLong(ARG_STOCK_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stock, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActionBarListener.onUpdateActionBar(getString(R.string.title_stock));
    }
}
