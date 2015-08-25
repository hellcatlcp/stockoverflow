package info.longlost.stockoverflow;

import android.database.Cursor;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBarActivity;
import android.app.Activity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.SimpleCursorTreeAdapter;
import android.widget.Toast;

import info.longlost.stockoverflow.data.StockContract.PortfolioEntry;
import info.longlost.stockoverflow.data.StockContract.StockEntry;
import info.longlost.stockoverflow.data.StockContract.PortfolioStockMap;

/**
 * Fragment used for managing interactions for and presentation of a navigation drawer.
 * See the <a href="https://developer.android.com/design/patterns/navigation-drawer.html#Interaction">
 * design guidelines</a> for a complete explanation of the behaviors implemented here.
 */
public class NavigationDrawerFragment extends Fragment implements
        LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Remember the position of the selected item.
     */
    private static final String STATE_SELECTED_PORTFOLIO_ID = "selected_portfolio_id";
    private static final String STATE_SELECTED_STOCK_ID = "selected_stock_id";

    /**
     * Per the design guidelines, you should show the drawer on launch until the user manually
     * expands it. This shared preference tracks this.
     */
    private static final String PREF_USER_LEARNED_DRAWER = "navigation_drawer_learned";

    private static final int PORTFOLIO_LOADER = 100;
    private static final int STOCK_MAP_LOADER = 200;

    private static final String ARG_PORTFOLIO_ID = "arg_portfolio_id";

    /**
     * A pointer to the current callbacks instance (the Activity).
     */
    private NavigationDrawerCallbacks mCallbacks;

    /**
     * Helper component that ties the action bar to the navigation drawer.
     */
    private ActionBarDrawerToggle mDrawerToggle;

    private DrawerLayout mDrawerLayout;
    private ExpandableListView mDrawerListView;
    private SimpleCursorTreeAdapter mDrawerListAdapter;
    private View mFragmentContainerView;

    private long mSelectedPortfolioId = -1;
    private long mSelectedStockId = -1;

    private boolean mFromSavedInstanceState;
    private boolean mUserLearnedDrawer;

    public NavigationDrawerFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Read in the flag indicating whether or not the user has demonstrated awareness of the
        // drawer. See PREF_USER_LEARNED_DRAWER for details.
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(getActivity());
        mUserLearnedDrawer = sp.getBoolean(PREF_USER_LEARNED_DRAWER, false);

        long selectedPortfolioId = -1;
        long selectedStockId = -1;

        if (savedInstanceState != null) {
            selectedPortfolioId = savedInstanceState.getLong(STATE_SELECTED_PORTFOLIO_ID, -1);
            selectedStockId = savedInstanceState.getLong(STATE_SELECTED_STOCK_ID, -1);
            mFromSavedInstanceState = true;
        }

        // Select either the default item (0) or the last selected item.
        updateSelection(selectedPortfolioId, selectedStockId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Indicate that this fragment would like to influence the set of actions in the action bar.
        setHasOptionsMenu(true);
        getLoaderManager().initLoader(PORTFOLIO_LOADER, Bundle.EMPTY, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mDrawerListView = (ExpandableListView) inflater.inflate(
                R.layout.fragment_navigation_drawer, container, false);

        mDrawerListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int portfolioPos, long portfolioId) {
                setSelection(portfolioId, portfolioPos, -1, -1);
                // TODO (helenparsons): Default behaviour is to collapse the clicked group if it is
                // TODO (helenparsons)  open and expand it if it is closed.  Instead it should
                // TODO (helenparsons)  always ensure that the clicked group is expanded and that
                // TODO (helenparsons)  any other expanded group is collapsed (for this you should
                // TODO (helenparsons)  also investigate AbsListView.CHOICE_MODE_SINGLE and
                // TODO (helenparsons)  AbsListView.CHOICE_MODE_MULTIPLE to understand what the
                // TODO (helenparsons)  default behaviour is).
                // return false so that the ExpandableListView handles expanding / collapsing.
                return false;
            }
        });

        mDrawerListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int portfolioPos, int stockPos, long stockMapId) {
                int flatListPos =
                        parent.getFlatListPosition(
                                ExpandableListView.getPackedPositionForGroup(portfolioPos));
                long portfolioId = parent.getItemIdAtPosition(flatListPos);
                setSelection(portfolioId, portfolioPos, stockMapId, stockPos);
                return true;
            }
        });

        mDrawerListAdapter = new SimpleCursorTreeAdapter(this.getActivity(), null,
                android.R.layout.simple_list_item_1,
                android.R.layout.simple_list_item_activated_1,
                new String[] {PortfolioEntry.COLUMN_PORTFOLIO_NAME},
                new int[] {android.R.id.text1},
                android.R.layout.simple_list_item_1,
                android.R.layout.simple_list_item_1,
                new String[] {StockEntry.COLUMN_TICKER},
                new int[] {android.R.id.text1}) {
            @Override
            protected Cursor getChildrenCursor(Cursor portfolioCursor) {
                Bundle args = new Bundle();
                int portfolioIdIdx = portfolioCursor.getColumnIndex(PortfolioEntry._ID);
                args.putLong(ARG_PORTFOLIO_ID, portfolioCursor.getLong(portfolioIdIdx));

                if (getLoaderManager().getLoader(STOCK_MAP_LOADER) == null) {
                    getLoaderManager().initLoader(STOCK_MAP_LOADER, args, NavigationDrawerFragment.this);
                } else {
                    getLoaderManager().restartLoader(STOCK_MAP_LOADER, args, NavigationDrawerFragment.this);
                }
                return null;
            }
        };

        mDrawerListView.setAdapter(mDrawerListAdapter);
        return mDrawerListView;
    }

    public boolean isDrawerOpen() {
        return mDrawerLayout != null && mDrawerLayout.isDrawerOpen(mFragmentContainerView);
    }

    /**
     * Users of this fragment must call this method to set up the navigation drawer interactions.
     *
     * @param fragmentId   The android:id of this fragment in its activity's layout.
     * @param drawerLayout The DrawerLayout containing this fragment's UI.
     */
    public void setUp(int fragmentId, DrawerLayout drawerLayout) {
        mFragmentContainerView = getActivity().findViewById(fragmentId);
        mDrawerLayout = drawerLayout;

        // set a custom shadow that overlays the main content when the drawer opens
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        // set up the drawer's list view with items and click listener

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        // ActionBarDrawerToggle ties together the the proper interactions
        // between the navigation drawer and the action bar app icon.
        mDrawerToggle = new ActionBarDrawerToggle(
                getActivity(),                    /* host Activity */
                mDrawerLayout,                    /* DrawerLayout object */
                R.drawable.ic_drawer,             /* nav drawer image to replace 'Up' caret */
                R.string.navigation_drawer_open,  /* "open drawer" description for accessibility */
                R.string.navigation_drawer_close  /* "close drawer" description for accessibility */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
                if (!isAdded()) {
                    return;
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                if (!isAdded()) {
                    return;
                }

                if (!mUserLearnedDrawer) {
                    // The user manually opened the drawer; store this flag to prevent auto-showing
                    // the navigation drawer automatically in the future.
                    mUserLearnedDrawer = true;
                    SharedPreferences sp = PreferenceManager
                            .getDefaultSharedPreferences(getActivity());
                    sp.edit().putBoolean(PREF_USER_LEARNED_DRAWER, true).apply();
                }

                getActivity().supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // If the user hasn't 'learned' about the drawer, open it to introduce them to the drawer,
        // per the navigation drawer design guidelines.
        if (!mUserLearnedDrawer && !mFromSavedInstanceState) {
            mDrawerLayout.openDrawer(mFragmentContainerView);
        }

        // Defer code dependent on restoration of previous instance state.
        mDrawerLayout.post(new Runnable() {
            @Override
            public void run() {
                mDrawerToggle.syncState();
            }
        });

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private void updateSelection(long selectedPortfolioId, long selectedStockId) {
        int portfolioPos = -1;
        int stockPos = -1;

        // Validate selected portfolio and stock IDs.
        // Set the position values based on the selected IDs.
        if (mDrawerListAdapter != null) {
            Cursor portfolioCursor = mDrawerListAdapter.getCursor();
            int portfolioIdIdx = portfolioCursor.getColumnIndex(PortfolioEntry._ID);

            for (portfolioCursor.moveToFirst(); !portfolioCursor.isAfterLast(); portfolioCursor.moveToNext()) {
                if (portfolioCursor.getLong(portfolioIdIdx) == selectedPortfolioId) {
                    portfolioPos = portfolioCursor.getPosition();
                    break;
                }
            }

            if (portfolioPos >= 0) {
                Cursor stockCursor = mDrawerListAdapter.getChild(portfolioPos, 0);

                if (stockCursor != null && stockCursor.getCount() > 0 && selectedStockId >= 0) {
                    int stockIdIdx = stockCursor.getColumnIndex(PortfolioStockMap._ID);

                    for (stockCursor.moveToFirst(); !stockCursor.isAfterLast(); stockCursor.moveToNext()) {
                        if (stockCursor.getLong(stockIdIdx) == selectedStockId) {
                            stockPos = stockCursor.getPosition();
                            break;
                        }
                    }
                }

                setSelection(selectedPortfolioId, portfolioPos, selectedStockId, stockPos);
            }
        }
    }

    private void setSelection(long selectedPortfolioId,
                              int selectedPortfolioPos,
                              long selectedStockId,
                              int selectedStockPos) {
        // update checked item if possible
        if (mDrawerListView != null) {
            int flatPos;

            if (selectedStockId >= 0) {
                flatPos = mDrawerListView.getFlatListPosition(
                        ExpandableListView.getPackedPositionForChild(selectedPortfolioPos,
                                selectedStockPos));
            } else {
                flatPos = mDrawerListView.getFlatListPosition(
                        ExpandableListView.getPackedPositionForGroup(selectedPortfolioPos));
            }

            mDrawerListView.setItemChecked(flatPos, true);
        }

        // callback if necessary
        if (mCallbacks != null) {
            if (selectedStockId >= 0 && selectedStockId != mSelectedStockId) {
                mCallbacks.onStockSelected(selectedStockId);
            } else if (selectedStockId < 0 &&
                    selectedPortfolioId >= 0 &&
                    selectedPortfolioId != mSelectedPortfolioId) {
                mCallbacks.onPortfolioSelected(selectedPortfolioId);
            }
        }

        // update member variables
        mSelectedPortfolioId = selectedPortfolioId;
        mSelectedStockId = selectedStockId;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallbacks = (NavigationDrawerCallbacks) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException("Activity must implement NavigationDrawerCallbacks.");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallbacks = null;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(STATE_SELECTED_PORTFOLIO_ID, mSelectedPortfolioId);
        outState.putLong(STATE_SELECTED_STOCK_ID, mSelectedStockId);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Forward the new configuration the drawer toggle component.
        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // If the drawer is open, show the global app actions in the action bar. See also
        // showGlobalContextActionBar, which controls the top-left area of the action bar.
        if (mDrawerLayout != null && isDrawerOpen()) {
            inflater.inflate(R.menu.global, menu);
            showGlobalContextActionBar();
        }
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        if (item.getItemId() == R.id.action_example) {
            Toast.makeText(getActivity(), "Example action.", Toast.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Per the navigation drawer design guidelines, updates the action bar to show the global app
     * 'context', rather than just what's in the current screen.
     */
    private void showGlobalContextActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setTitle(R.string.app_name);
    }

    private ActionBar getActionBar() {
        return ((ActionBarActivity) getActivity()).getSupportActionBar();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        switch (id) {
            case PORTFOLIO_LOADER:
                return new CursorLoader(getActivity(),
                        PortfolioEntry.CONTENT_URI,
                        new String[]{
                                PortfolioEntry._ID,
                                PortfolioEntry.COLUMN_PORTFOLIO_NAME
                        },
                        null,
                        null,
                        null);
            case STOCK_MAP_LOADER:
                if (!args.isEmpty()) {
                    long portfolioId = args.getLong(ARG_PORTFOLIO_ID, -1);

                    return new CursorLoader(getActivity(),
                            PortfolioStockMap.CONTENT_URI,
                            new String[] {
                                    PortfolioStockMap._ID,
                                    PortfolioStockMap.COLUMN_PORTFOLIO_ID,
                                    PortfolioStockMap.COLUMN_STOCK_ID,
                                    StockEntry.COLUMN_TICKER
                            },
                            PortfolioStockMap.COLUMN_PORTFOLIO_ID + "=?",
                            new String[] { Long.toString(portfolioId) },
                            null);
                }
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        switch (loader.getId()) {
            case PORTFOLIO_LOADER:
                mDrawerListAdapter.setGroupCursor(data);
                // defer this to the message loop
                mDrawerLayout.post(new Runnable() {
                    @Override
                    public void run() {
                        updateSelection(mSelectedPortfolioId, mSelectedStockId);
                    }
                });
                break;
            case STOCK_MAP_LOADER:
                Cursor portfolioCursor = mDrawerListAdapter.getCursor();

                if (data.getCount() > 0 && portfolioCursor != null) {
                    int portfolioIdIdx = data.getColumnIndex(PortfolioStockMap.COLUMN_PORTFOLIO_ID);
                    int position = portfolioCursor.getPosition();

                    data.moveToFirst();
                    if (data.getLong(portfolioIdIdx) == mDrawerListAdapter.getGroupId(position)) {
                        mDrawerListAdapter.setChildrenCursor(position, data);
                    }

                    // defer this to the message loop
                    mDrawerLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            updateSelection(mSelectedPortfolioId, mSelectedStockId);
                        }
                    });
                }
                break;
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        switch (loader.getId()) {
            case PORTFOLIO_LOADER:
                mDrawerListAdapter.setGroupCursor(null);
                mDrawerListAdapter.notifyDataSetChanged();
                break;
            case STOCK_MAP_LOADER:
                mDrawerListAdapter.notifyDataSetInvalidated();
                break;
        }
    }

    /**
     * Callbacks interface that all activities using this fragment must implement.
     */
    public static interface NavigationDrawerCallbacks {
        /**
         * Called when an item in the navigation drawer is selected.
         */
        void onPortfolioSelected(long portfolioId);

        void onStockSelected(long stockId);

    }
}
