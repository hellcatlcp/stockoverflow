package info.longlost.stockoverflow.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by ldenison on 20/08/2015.
 */
public class PriceSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static PriceSyncAdapter sPriceSyncAdapter = null;

    @Override
    public void onCreate() {
        synchronized (sSyncAdapterLock) {
            if (sPriceSyncAdapter == null) {
                sPriceSyncAdapter = new PriceSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return sPriceSyncAdapter.getSyncAdapterBinder();
    }
}
