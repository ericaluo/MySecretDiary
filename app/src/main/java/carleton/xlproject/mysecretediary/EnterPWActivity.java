package carleton.xlproject.mysecretediary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.github.siyamed.shapeimageview.CircularImageView;

import java.math.BigInteger;
import java.security.MessageDigest;

public class EnterPWActivity extends AppCompatActivity {

    EditText enterPW;
    Button confirmButton;

    String password;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_pw);

        //load the password
        SharedPreferences setting = getSharedPreferences("prefs",0);
        password = setting.getString("password","");

        enterPW = (EditText) findViewById(R.id.enterPW);
        confirmButton = (Button)findViewById(R.id.confirmButton);
        CircularImageView imageView;
        imageView = findViewById(R.id.image_view);
        Uri uri = Uri.parse("android.resource://carleton.xlproject.mysecretediary/drawable/icons");
        Glide.with(this).load(String.valueOf(uri)).into(imageView);

        confirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String text = enterPW.getText().toString();
                try {
                    text = getMD5Str(text);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                if(text.equals(password)){

                    //enter the app
                    Intent intent = new Intent(getApplicationContext(), SplashMap.class);
                    startActivity(intent);
                    finish();
                }
                else{
                    Toast.makeText(EnterPWActivity.this,"Wrong password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
    public static String getMD5Str(String str) throws Exception {
        try {
            // generate a MD5 generator
            MessageDigest md = MessageDigest.getInstance("MD5");
            // calculate MD5
            md.update(str.getBytes());

            return new BigInteger(1, md.digest()).toString(16);
        } catch (Exception e) {
            throw new Exception("MD5 encryption error"+e.toString());
        }
    }
}
