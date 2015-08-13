package info.longlost.stockoverflow.util;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.util.SparseIntArray;

/**
 * Created by ldenison on 12/08/2015.
 */
public class SimpleEqualsFilter extends FilterCursor.Filter {

    private String mColumn;
    private Object mValue;

    public SimpleEqualsFilter(String column, Object value) {
        mColumn = column;
        mValue = value;
    }

    @Override
    public void buildIndex(Cursor cursor, String[] orderingHint) {
        cursor.moveToFirst();
        mReverseIdx = new SparseIntArray(cursor.getCount());
        mForwardIdx = new int[cursor.getCount()];


        int columnIdx = cursor.getColumnIndex(mColumn);
        int columnType = cursor.getType(columnIdx);
        int count = 0;

        for (; !cursor.isAfterLast(); cursor.moveToNext()) {
            switch (columnType) {
            case Cursor.FIELD_TYPE_NULL:
                if (mValue == null) {
                    mReverseIdx.append(cursor.getPosition(), count);
                    count++;
                }
                break;
            case Cursor.FIELD_TYPE_INTEGER:
                if (mValue != null && mValue.equals(cursor.getLong(columnIdx))) {
                    mReverseIdx.append(cursor.getPosition(), count);
                    count++;
                }
                break;
            case Cursor.FIELD_TYPE_STRING:
                if (mValue != null && mValue.equals(cursor.getString(columnIdx))) {
                    mReverseIdx.append(cursor.getPosition(), count);
                    count++;
                }
                break;
            case Cursor.FIELD_TYPE_FLOAT:
                if (mValue != null && mValue.equals(cursor.getFloat(columnIdx))) {
                    mReverseIdx.append(cursor.getPosition(), count);
                    count++;
                }
                break;
            case Cursor.FIELD_TYPE_BLOB:
                if (mValue != null && mValue.equals(cursor.getBlob(columnIdx))) {
                    mReverseIdx.append(cursor.getPosition(), count);
                    count++;
                }
                break;
            }

        }

        mForwardIdx = new int[count];
        for (int i = 0; i < count; i++) {
            mForwardIdx[i] = mReverseIdx.keyAt(i);
        }
    }
}
