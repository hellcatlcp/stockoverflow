package info.longlost.stockoverflow.util;

import android.database.Cursor;
import android.database.DatabaseUtils;

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

        int columnIdx = cursor.getColumnIndex(mColumn);
        String dump = DatabaseUtils.dumpCursorToString(cursor);
        int columnType = cursor.getType(columnIdx);
        int count = 0;

        switch (columnType) {
            case Cursor.FIELD_TYPE_NULL:
                return;
            case Cursor.FIELD_TYPE_INTEGER:
                for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                    if (mValue.equals(cursor.getInt(columnIdx))) {
                        mReverseIdx.append(cursor.getPosition(), count);
                        count++;
                    }
                }
                break;
            case Cursor.FIELD_TYPE_STRING:
                for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                    if (mValue.equals(cursor.getString(columnIdx))) {
                        mReverseIdx.append(cursor.getPosition(), count);
                        count++;
                    }
                }
                break;
            case Cursor.FIELD_TYPE_FLOAT:
                for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                    if (mValue.equals(cursor.getFloat(columnIdx))) {
                        mReverseIdx.append(cursor.getPosition(), count);
                        count++;
                    }
                }
                break;
            case Cursor.FIELD_TYPE_BLOB:
                for (; !cursor.isAfterLast(); cursor.moveToNext()) {
                    if (mValue.equals(cursor.getBlob(columnIdx))) {
                        mReverseIdx.append(cursor.getPosition(), count);
                        count++;
                    }
                }
                break;
        }

        mForwardIdx = new int[count];
        for (int i = 0; i < count; i++) {
            mForwardIdx[i] = mReverseIdx.keyAt(i);
        }
    }
}