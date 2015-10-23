package info.longlost.stockoverflow;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.EditText;

/**
 * Created by ldenison on 23/10/2015.
 */

public class AddStockDialogFragment extends DialogFragment {

    private EditText mStockTicker;

    public AddStockDialogFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_stock, container);
        mStockTicker = (EditText) view.findViewById(R.id.stock_ticker);
        getDialog().setTitle(getString(R.string.add_stock));

        // Show soft keyboard automatically
        mStockTicker.requestFocus();
        getDialog().getWindow().setSoftInputMode(
                LayoutParams.SOFT_INPUT_STATE_VISIBLE);

        return view;
    }

}