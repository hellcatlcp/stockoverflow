package info.longlost.stockoverflow;

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
    // TODO (helenparsons)  PriceSyncAdapter.configurePeriodicSync() to start syncing every 5
    // TODO (helenparsons)  minutes (in onStart) and cancels the sync in onStop.
    @Override
    public void onPortfolioSelected(long portfolioId) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();

        // TODO (helenparsons): Add a member variable to this class which stores the most
        // TODO (helenparsons)  recently selected portfolio.  We will use this to figure out which
        // TODO (helenparsons)  portfolio to add a new stock to.
        // TODO (helenparsons)  We don't need to update the portfolio when a stock is selected
        // TODO (helenparsons)  because our UI won't allow you to select a stock without first
        // TODO (helenparsons)  selecting the portfolio it is in.  If we ever change this assumption
        // TODO (helenparsons)  in our UI we would need to change onStockSelected.

        fragmentManager.beginTransaction()
                .replace(R.id.container, PortfolioFragment.newInstance(portfolioId))
                .commit();
    }

    @Override
    public void onStockSelected(long stockId) {
        // update the main content by replacing fragments
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
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        // TODO (helenparsons): We want to handle the 'Add Stock' menu item here.  This will look
        // TODO (helenparsons)  something like:
        // TODO (helenparsons)
        // TODO (helenparsons)  switch (id) {
        // TODO (helenparsons)      case R.id.add_stock:
        // TODO (helenparsons)          // ... do stuff
        // TODO (helenparsons)          break;
        // TODO (helenparsons)  }
        // TODO (helenparsons)
        // TODO (helenparsons)  Rather than actually add the stock, to test that we hooked up the
        // TODO (helenparsons)  menu item properly, we should use AlertDialog to display an alert
        // TODO (helenparsons)  with the portfolio ID and just an OK button that does nothing.

        return super.onOptionsItemSelected(item);
    }
}
