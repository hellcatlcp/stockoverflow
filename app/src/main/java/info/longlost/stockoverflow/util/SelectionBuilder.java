package info.longlost.stockoverflow.util;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by ldenison on 09/08/2015.
 */
public class SelectionBuilder {

    private List<String> mSelectionAtoms = new ArrayList<String>();
    private List<String> mSelectionArgs = new ArrayList<String>();

    public SelectionBuilder(String selection, String[] selectionArgs) {
        super();

        add(selection, selectionArgs);
    }

    public SelectionBuilder add(String selection, String[] selectionArgs) {
        if (TextUtils.isEmpty(selection)) {
            return this;
        }

        mSelectionAtoms.add(selection);

        if (selectionArgs != null) {
            Collections.addAll(mSelectionArgs, selectionArgs);
        }

        return this;
    }

    public String build() {
        if (mSelectionAtoms.size() > 0) {
            StringBuilder selection = new StringBuilder(mSelectionAtoms.get(0).length() + 2);

            for (String atom : mSelectionAtoms) {
                if (selection.length() > 0) {
                    selection.append(" AND ");
                }

                selection.append("(").append(atom).append(")");
            }

            return selection.toString();
        } else {
            return "";
        }
    }

    public String[] getSelectionArgs() {
        return mSelectionArgs.toArray(new String[]{});
    }
}
