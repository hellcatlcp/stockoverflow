package info.longlost.stockoverflow;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class StockMapFragment extends BaseFragment {

    private static final String ARG_STOCK_MAP_ID = "stock_map_id";

    private long mStockMapId;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param stockId Stock ticker to display.
     * @return A new instance of fragment StockMapFragment.
     */
    public static StockMapFragment newInstance(long stockId) {
        StockMapFragment fragment = new StockMapFragment();
        Bundle args = new Bundle();
        args.putLong(ARG_STOCK_MAP_ID, stockId);
        fragment.setArguments(args);
        return fragment;
    }

    public StockMapFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mStockMapId = getArguments().getLong(ARG_STOCK_MAP_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_stock_map, container, false);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        mActionBarListener.onUpdateActionBar(getString(R.string.title_stock));
    }
}
