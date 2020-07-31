package carleton.xlproject.mysecretediary;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.*;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.CircularImageView;
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

public class GetLocation extends AppCompatActivity {
    //Initialize var
    Button btLocation, btnConfirm;
    TextView longitude, latitude, country, street, description;
    FusedLocationProviderClient fusedLocationProviderClient;
    List<Address> address;
    private LocationRequest mLocationRequest;
    private LocationCallback mLocationCallback;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_location);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        btLocation = findViewById(R.id.get_current_location);
        latitude = findViewById(R.id.latitude);
        longitude = findViewById(R.id.longitude);
        country = findViewById(R.id.country);
        street = findViewById(R.id.street);
        description = findViewById(R.id.description);
        btnConfirm = findViewById(R.id.confirm_button);
        description.setVisibility(View.INVISIBLE);
        btnConfirm.setVisibility(View.INVISIBLE);

        CircularImageView imageView;
        imageView = findViewById(R.id.image_view);
        Uri uri = Uri.parse("android.resource://carleton.xlproject.mysecretediary/drawable/icons");
        Glide.with(this).load(String.valueOf(uri)).into(imageView);

        //initialize fusedLocationProviderclinet
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        btLocation.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                //check permission
               // if(checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
                if(ActivityCompat.checkSelfPermission(GetLocation.this, Manifest.permission.ACCESS_FINE_LOCATION)== PackageManager.PERMISSION_GRANTED){
                    //when permisssion granted
                    getLocation();
                    description.setVisibility(View.VISIBLE);
                    btnConfirm.setVisibility(View.VISIBLE);
                }
                else{
                //when permission denied
                    ActivityCompat.requestPermissions(GetLocation.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);

                }
            }
        });

        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogInterface.OnClickListener dialogClickListen =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int button) {
                                if (button == DialogInterface.BUTTON_POSITIVE) {
                                    confirmLocation();
                                }
                            }
                        };
                AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(GetLocation.this);
                deleteBuilder.setMessage("Confirm to set current location as secure location?").setPositiveButton(getString(android.R.string.yes), dialogClickListen).setNegativeButton(getString(android.R.string.no), dialogClickListen).show();
            }
    });
    }


    private void confirmLocation() {
        SharedPreferences settingsLat = getSharedPreferences("userLatitude",0);
        SharedPreferences settingLong = getSharedPreferences("userLongitude",0);
        SharedPreferences.Editor editorLat = settingsLat.edit();
        SharedPreferences.Editor editorLong = settingLong.edit();

        Double longitudeD = address.get(0).getLongitude();
        Double latitudeD = address.get(0).getLatitude();
        editorLat.putString("Latitude",latitudeD.toString());
        editorLong.putString("Longitude",longitudeD.toString());
        editorLat.apply();
        editorLong.apply();
        Toast.makeText(GetLocation.this, "Location Confirmed!",
                Toast.LENGTH_SHORT).show();
        finish();
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
                        Geocoder geocoder = new Geocoder(GetLocation.this, Locale.getDefault());
                        //init address list
                        address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);

                        //set textview
                        //  Double longitudeD = address.get(0).getLongitude();
                        //   Double latitudeD = address.get(0).getLatitude();
                        Double longitudeD = location.getLongitude();
                        Double latitudeD = location.getLatitude();
                        latitude.setText("Latitude " + latitudeD.toString());
                        longitude.setText("Longitude " + longitudeD.toString());
                        country.setText(address.get(0).getCountryName());
                        street.setText(address.get(0).getAddressLine(0));
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
                                    Geocoder geocoder = new Geocoder(GetLocation.this, Locale.getDefault());
                                    try {
                                        address = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(),1);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    latitude.setText("Latitude " + latitudeD.toString());
                                    longitude.setText("Longitude " + longitudeD.toString());
                                    country.setText(address.get(0).getCountryName());
                                    country.setText(address.get(0).getCountryName());
                                    street.setText(address.get(0).getAddressLine(0));
                                    }

                                }
                            }
                    };
                    //fusedLocationProviderClient.requestLocationUpdates(mLocationRequest, mLocationCallback, Looper.getMainLooper());
                }
            }

        });
            }


}
