package carleton.xlproject.mysecretediary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

public class SplashMap extends AppCompatActivity {

    String userLat, userLog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_map);

        //load the user's lat/log
        SharedPreferences reciLat = getSharedPreferences("userLatitude",0);
        SharedPreferences reciLog = getSharedPreferences("userLongitude",0);
        userLat = reciLat.getString("Latitude","");
        userLog = reciLog.getString("Longitude","");

        Handler mhandler = new Handler();
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if((!userLat.equals("")) &&(!userLog.equals(""))) {
                    //if there is a password
                    Intent intent = new Intent(getApplicationContext(), CheckLocation.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //if there is no password
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }

}
