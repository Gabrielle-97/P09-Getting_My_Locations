package sg.edu.rp.c346.id19020303.p09_gettingmylocations;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.PermissionChecker;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.location.Location;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.File;
import java.io.FileWriter;

public class MainActivity extends AppCompatActivity {

    Button btnUpdate, btnRemove, btnCheck;
    TextView tvlong, tvlat;
    private GoogleMap map;
    String folderLocation;

    FusedLocationProviderClient client;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnCheck = findViewById(R.id.btnCheckRecords);
        btnRemove = findViewById(R.id.btnRemoveLocationUpdate);
        btnUpdate = findViewById(R.id.btnGetLocationUpdate);
        tvlat = findViewById(R.id.tvLat);
        tvlong = findViewById(R.id.tvLong);

        client = LocationServices.getFusedLocationProviderClient(MainActivity.this);


        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 0);

        FragmentManager fm = getSupportFragmentManager();
        SupportMapFragment mapFragment = (SupportMapFragment)
                fm.findFragmentById(R.id.map);


        mapFragment.getMapAsync(new OnMapReadyCallback(){
            @Override
            public void onMapReady(GoogleMap googleMap) {
                map = googleMap;

                UiSettings ui = map.getUiSettings();
                ui.setCompassEnabled(true);
                ui.setZoomControlsEnabled(true);

                map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                    @Override
                    public boolean onMarkerClick(Marker marker) {
                        Toast.makeText(MainActivity.this, marker.getTitle(), Toast.LENGTH_SHORT).show();
                        return false;
                    }
                });

                if (checkPermission()) {
                    map.setMyLocationEnabled(true);
                }



            }
        });



        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (checkPermission()) {


                    mLocationRequest = LocationRequest.create();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(10000);
                    mLocationRequest.setFastestInterval(5000);
                    mLocationRequest.setSmallestDisplacement(100);

                    folderLocation = getFilesDir().getAbsolutePath() + "/Folder";

                    File folder = new File(folderLocation);
                    if (folder.exists() == false) {
                        boolean result = folder.mkdir();
                        if (result == true) {
                            Log.d("File Read/Write", "Folder created");
                        }


                    }

                    mLocationCallback = new LocationCallback() {
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if (locationResult != null) {
                                Location data = locationResult.getLastLocation();
                                double lat = data.getLatitude();
                                double lng = data.getLongitude();

                                map.clear();

                                LatLng updatedLocation = new LatLng(lat,lng);

                                Marker updatedLoc = map.addMarker(new
                                        MarkerOptions()
                                        .position(updatedLocation)
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));


                                tvlat.setText("Latitude: " + lat);
                                tvlong.setText("Longitude: " + lng);

                                try {
                                    String folderLocation_I= getFilesDir().getAbsolutePath() + "/Folder";
                                    File targetFile_I = new File(folderLocation_I, "data.txt");
                                    FileWriter writer_I = new FileWriter(targetFile_I, true);
                                    writer_I.write("Latitude: "+ lat + " Longitude: " + lng +"\n");
                                    writer_I.flush();
                                    writer_I.close();
                                } catch (Exception e) {
                                    Toast.makeText(MainActivity.this, "Failed to write!", Toast.LENGTH_LONG).show();
                                    e.printStackTrace();
                                }
                            }


                        }

                        ;
                    };
                    client.requestLocationUpdates(mLocationRequest, mLocationCallback, null);

                }

            }
        });

        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                client.removeLocationUpdates(mLocationCallback);

            }
        });


    }

    private boolean checkPermission() {
        int permissionCheck_Coarse = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION);
        int permissionCheck_Fine = ContextCompat.checkSelfPermission(
                MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION);

        if (permissionCheck_Coarse == PermissionChecker.PERMISSION_GRANTED
                || permissionCheck_Fine == PermissionChecker.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }
}