package info.longlost.stockoverflow.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ldenison on 20/08/2015.
 */
public class LatestPriceSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static LatestPriceSyncAdapter sLatestPriceSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sLatestPriceSyncAdapter == null) {
                sLatestPriceSyncAdapter = new LatestPriceSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sLatestPriceSyncAdapter.getSyncAdapterBinder();
    }
}
