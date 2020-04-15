package com.example.lu;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.os.Bundle;
@SuppressWarnings("all")
public class MainActivity extends AppCompatActivity {
    TextView textView;
    Button send, stop;
    LocationManager lm;
    boolean flag = true;
    String strProvider = LocationManager.GPS_PROVIDER;
    Location mlocation = new Location(strProvider);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.tv);
        send = findViewById(R.id.bt2);
        stop = findViewById(R.id.bt3);
        lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        LocationProvider provider = lm.getProvider(strProvider);
        if (provider != null) {
            lm.addTestProvider(provider.getName(),
                    provider.requiresNetwork(),
                    provider.requiresSatellite(),
                    provider.requiresCell(),
                    provider.hasMonetaryCost(),
                    provider.supportsAltitude(),
                    provider.supportsSpeed(),
                    provider.supportsBearing(),
                    provider.getPowerRequirement(),
                    provider.getAccuracy());
        } else {
            lm.addTestProvider(strProvider, true, true
                    , true, false, true, true, true, Criteria.POWER_MEDIUM, Criteria.ACCURACY_FINE);
        }
        lm.setTestProviderEnabled(strProvider, true);
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                flag = true;
                mocklocationChanger();
                mockListener();
            }
        });
    }

    private void mocklocationChanger() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mlocation.setLatitude(Math.random() * 60);
                mlocation.setLongitude(Math.random() * 180);
                mlocation.setAltitude(Math.random() * 100);
                mlocation.setTime(System.currentTimeMillis());
                mlocation.setAccuracy(5);
                mlocation.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
                //lm.setTestProviderStatus(strProvider, 100, null, System.currentTimeMillis());
                lm.setTestProviderLocation(strProvider, mlocation);
            }
        }).start();
    }

    private Location mockListener() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return null;
        }
        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, mockListener);
        return mlocation;
    }

    LocationListener mockListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updataTextView(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void updataTextView(Location location) {
        StringBuilder sb = new StringBuilder();
        sb.append("纬度：" + location.getLatitude());
        sb.append("\n经度：" + location.getLongitude());
        sb.append("\n海拔：" + location.getAltitude());
        textView.setText(sb.toString());
    }
}
