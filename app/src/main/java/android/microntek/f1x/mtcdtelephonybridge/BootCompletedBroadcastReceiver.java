package android.microntek.f1x.mtcdtelephonybridge;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by COMPUTER on 2017-06-22.
 */

public class BootCompletedBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        if(intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            context.startService(new Intent(context, TelephonyBridgeService.class));
        }
    }
}
