package info.longlost.stockoverflow.util;

import android.database.Cursor;
import android.database.CursorWrapper;
import android.util.SparseIntArray;

/**
 * Created by ldenison on 12/08/2015.
 */
public class FilterCursor extends CursorWrapper {

    private Filter mFilter;

    /**
     * Creates a cursor wrapper.
     *
     * @param cursor The underlying cursor to wrap.
     */
    public FilterCursor(Cursor cursor, Filter filter) {
        this(cursor, filter, null);
    }

    public FilterCursor(Cursor cursor, Filter filter, String[] orderingHint) {
        super(cursor);
        mFilter = filter;
        mFilter.buildIndex(cursor, orderingHint);
    }


    @Override
    public int getCount() {
        return mFilter.mForwardIdx.length;
    }

    @Override
    public int getPosition() {
        return mFilter.mReverseIdx.get(super.getPosition());
    }

    @Override
    public boolean moveToPosition(int position) {
        return super.moveToPosition(mFilter.mForwardIdx[position]);
    }

    @Override
    public boolean moveToFirst() {
        return super.moveToPosition(mFilter.mForwardIdx[0]);
    }

    @Override
    public boolean moveToLast() {
        return super.moveToPosition(mFilter.mForwardIdx[mFilter.mForwardIdx.length - 1]);
    }

    @Override
    public boolean isFirst() {
        return mFilter.mReverseIdx.get(super.getPosition()) == 0;
    }

    @Override
    public boolean isLast() {
        return mFilter.mReverseIdx.get(super.getPosition()) == mFilter.mForwardIdx.length - 1;
    }

    @Override
    public boolean isBeforeFirst() {
        return super.getPosition() < mFilter.mReverseIdx.get(0);
    }

    @Override
    public boolean isAfterLast() {
        return super.getPosition() >= getCount();
    }

    public static abstract class Filter {
        public int[] mForwardIdx;
        public SparseIntArray mReverseIdx;

        public abstract void buildIndex(Cursor cursor, String[] orderingHint);
    }
}
