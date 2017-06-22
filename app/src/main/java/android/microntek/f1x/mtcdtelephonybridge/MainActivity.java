package android.microntek.f1x.mtcdtelephonybridge;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if ( ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG ) == PackageManager.PERMISSION_GRANTED) {
            startServiceAndFinish();
        } else {
            ActivityCompat.requestPermissions( this, new String[]{ Manifest.permission.WRITE_CALL_LOG }, READ_CALL_LOG_PERMISSION_REQUEST_ID);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode == READ_CALL_LOG_PERMISSION_REQUEST_ID && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startServiceAndFinish();
        }
    }

    private void startServiceAndFinish() {
        startService(new Intent(this, TelephonyBridgeService.class));
        Toast.makeText(this, "Service is now active.", Toast.LENGTH_LONG).show();
        finish();
    }

    final static private int READ_CALL_LOG_PERMISSION_REQUEST_ID = 1435;
}
