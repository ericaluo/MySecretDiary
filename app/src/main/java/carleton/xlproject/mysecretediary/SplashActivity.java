package carleton.xlproject.mysecretediary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.Gravity;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    String userPW;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        //load the password
        SharedPreferences settings = getSharedPreferences("prefs",0);
        userPW = settings.getString("password","");

        Handler mhandler = new Handler();
        mhandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if(userPW.equals("")) {
                    //if there is no password
                    Intent intent = new Intent(getApplicationContext(), CreatePWActivity.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    //if there is a password
                    Intent intent = new Intent(getApplicationContext(), EnterPWActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 2000);
    }
}
