package carleton.xlproject.mysecretediary;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import com.google.android.gms.location.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

public class CheckLocation extends AppCompatActivity {

    FusedLocationProviderClient fusedLocationProviderClient;
    TextView notification, currentLat, currentLog, strLat, strLog;
    List<Address> address;
    Location currentLoc;
    String prevLat, prevLog;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_location);
        //load latitude and longitude
        SharedPreferences reciLat = getSharedPreferences("userLatitude",0);
        SharedPreferences reciLog = getSharedPreferences("userLongitude",0);
        prevLat = reciLat.getString("Latitude","");
        prevLog = reciLog.getString("Longitude","");

        //init
        notification = findViewById(R.id.notification1);
        currentLat = findViewById(R.id.current_lat);
        currentLog = findViewById(R.id.current_log);
        strLat = findViewById(R.id.stro_lat);
        strLog = findViewById(R.id.stro_log);

        fusedLocationProviderClient = getFusedLocationProviderClient(this);
        if(ActivityCompat.checkSelfPermission(CheckLocation.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
           //when permisssion granted
            getLocation();
        }
        else{
            //when permission denied
            ActivityCompat.requestPermissions(CheckLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
    }

    private void getLocation() {
        fusedLocationProviderClient.getLastLocation().addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                //init location
                Location location = task.getResult();
                if(location !=null){
                    try {
                        //init getcoder
                        Geocoder geocoder = new Geocoder(CheckLocation.this, Locale.getDefault());
                        //init address list
                        address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                      //  Double longitudeD = address.get(0).getLongitude();
                     //   Double latitudeD = address.get(0).getLatitude();
                        Double longitudeD = location.getLongitude();
                        Double latitudeD = location.getLatitude();
                        Double prevLatitudeD = Double.valueOf(prevLat);
                        Double PreLongitudeD = Double.valueOf(prevLog);
                        if((longitudeD>=PreLongitudeD-0.0001) && (longitudeD<=PreLongitudeD+0.0001) && (latitudeD>=prevLatitudeD-0.0001) &&(latitudeD<=prevLatitudeD+0.0001)){
                        //if((prevLat.equals(latitudeD.toString())) && (prevLog.equals(longitudeD.toString()))){
                            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            notification.setText("Sorry you are not on the secure location\n Please close the app and reopen on the secure location");
                            currentLat.setText("Current Latitude " + latitudeD.toString());
                            currentLog.setText("Current Logitude " + longitudeD.toString());
                            strLat.setText("Previous Latitude " + prevLat);
                            strLog.setText("Previous Latitude " + prevLog);
                            Toast.makeText(CheckLocation.this,"Sorry, you're not on the secure location",Toast.LENGTH_SHORT).show();
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else{
                    mLocationRequest = LocationRequest.create();
                    mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    mLocationRequest.setInterval(20 * 1000);
                    mLocationCallback = new LocationCallback(){
                        @Override
                        public void onLocationResult(LocationResult locationResult) {
                            if(locationResult == null) return;
                            for(Location location :locationResult.getLocations()){
                                if(location != null){
                                    Double longitudeD = location.getLongitude();
                                    Double latitudeD = location.getLatitude();
                                    Double prevLatitudeD = Double.valueOf(prevLat);
                                    Double PreLongitudeD = Double.valueOf(prevLog);
                                    if((longitudeD>=PreLongitudeD-0.0001) && (longitudeD<=PreLongitudeD+0.0001) && (latitudeD>=prevLatitudeD-0.0001) &&(latitudeD<=prevLatitudeD+0.0001)){
                                        //if((prevLat.equals(latitudeD.toString())) && (prevLog.equals(longitudeD.toString()))){
                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                    else{
                                        notification.setText("Sorry you are not on the secure location\n Please close the app and reopen on the secure location");
                                        currentLat.setText("Current Latitude " + latitudeD.toString());
                                        currentLog.setText("Current Logitude " + longitudeD.toString());
                                        strLat.setText("Previous Latitude " + prevLat);
                                        strLog.setText("Previous Latitude " + prevLog);
                                        Toast.makeText(CheckLocation.this,"Sorry, you're not on the secure location",Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    };
                    fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
                }
            }

        });
    }
}
