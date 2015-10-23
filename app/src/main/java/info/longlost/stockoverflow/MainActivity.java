package info.longlost.stockoverflow;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.widget.DrawerLayout;


public class MainActivity extends ActionBarActivity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        BaseFragment.OnActionBarListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private long mSelectedPortfolioId;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
    }

    // TODO (helenparsons): Add an onStart() / onStop() pair to this activity which uses
    // TODO (helenparsons)  LatestPriceSyncAdapter.configurePeriodicSync() to start syncing every 5
    // TODO (helenparsons)  minutes (in onStart) and cancels the sync in onStop.
    @Override
    public void onPortfolioSelected(long portfolioId) {
        // update the portfolio content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        mSelectedPortfolioId = portfolioId;

        fragmentManager.beginTransaction()
                .replace(R.id.container, PortfolioFragment.newInstance(portfolioId))
                .commit();
    }

    @Override
    public void onStockSelected(long stockId) {
        // update the portfolio content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        fragmentManager.beginTransaction()
                .replace(R.id.container, StockMapFragment.newInstance(stockId))
                .commit();
    }

    @Override
    public void onUpdateActionBar(String title) {
        if (title != null) {
            mTitle = title;
            restoreActionBar();
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return false;
    }
}
