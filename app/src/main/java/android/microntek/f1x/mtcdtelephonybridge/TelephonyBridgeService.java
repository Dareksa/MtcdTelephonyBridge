package android.microntek.f1x.mtcdtelephonybridge;

import android.Manifest;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.provider.CallLog;
import android.support.annotation.Nullable;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by COMPUTER on 2017-06-22.
 */

public class TelephonyBridgeService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mServiceInitialized = false;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (mServiceInitialized) {
            unregisterReceiver(mPhonecallBroadcastReceiver);
        }

        mServiceInitialized = false;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        if (!mServiceInitialized) {
            IntentFilter intentFilter = new IntentFilter();
            intentFilter.addAction(ACTION_BOOTCHECK);
            registerReceiver(mPhonecallBroadcastReceiver, intentFilter);
            mServiceInitialized = true;
        }

        return START_STICKY;
    }

    private boolean mServiceInitialized;
    private final BroadcastReceiver mPhonecallBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_BOOTCHECK.equals(intent.getAction())) {
                final String className = intent.getStringExtra(BOOTCHECK_CLASS_EXTRA);

                if (BOOTCHECK_PHONECALLOUT.equals(className)) {
                    final List<String> callHistoryList = getCallHistoryList();

                    if (!callHistoryList.isEmpty()) {
                        addEntry(callHistoryList.get(callHistoryList.size() - 1));
                    }
                }
            }
        }
    };

    void addEntry(String entry) {
        final String entryFields[] = entry.split("\\^");

        if (entryFields.length < 6) {
            return;
        }

        final ContentValues values = new ContentValues();
        values.put(CallLog.Calls.NUMBER, entryFields[1]);
        values.put(CallLog.Calls.DATE, System.currentTimeMillis());
        values.put(CallLog.Calls.DURATION, 0);
        values.put(CallLog.Calls.TYPE, Integer.parseInt(entryFields[5]));
        values.put(CallLog.Calls.NEW, 1);
        values.put(CallLog.Calls.CACHED_NAME, "");
        values.put(CallLog.Calls.CACHED_NUMBER_TYPE, 0);
        values.put(CallLog.Calls.CACHED_NUMBER_LABEL, "");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED) {
            getContentResolver().insert(CallLog.Calls.CONTENT_URI, values);
        }
    }



    List<String> getCallHistoryList() {
        String content = Settings.System.getString(getContentResolver(), "MTC.BT.logList");
        return content == null ? new ArrayList<String>() : Arrays.asList(content.split("\n"));
    }

    private static final String ACTION_BOOTCHECK = "com.microntek.bootcheck";
    private static final String BOOTCHECK_CLASS_EXTRA = "class";
    private static final String BOOTCHECK_PHONECALLOUT = "phonecallout";
}
