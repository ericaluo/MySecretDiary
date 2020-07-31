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

public class CreatePWActivity extends AppCompatActivity {

    EditText enterPW1,enterPW2;
    Button confirmButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_pw);

        enterPW1 = (EditText) findViewById(R.id.enterPW1);
        enterPW2 = (EditText) findViewById(R.id.enterPW2);
        confirmButton = (Button) findViewById(R.id.confirmButton);

        CircularImageView imageView;
        imageView = findViewById(R.id.image_view);
        Uri uri = Uri.parse("android.resource://carleton.xlproject.mysecretediary/drawable/icons");
        Glide.with(this).load(String.valueOf(uri)).into(imageView);

        confirmButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                String pw1 = enterPW1.getText().toString();
                String pw2 = enterPW2.getText().toString();

                if(pw1.equals("") ||pw2.equals("")){
                    //no password
                    Toast.makeText(CreatePWActivity.this,"Didn't set up a password",Toast.LENGTH_SHORT).show();
                }
                else{
                    if(pw1.equals(pw2)){
                        //save the password
                        SharedPreferences settings = getSharedPreferences("prefs",0);
                        SharedPreferences.Editor editor = settings.edit();
                        try {
                            pw1 = getMD5Str(pw1);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        editor.putString("password",pw1);
                        editor.apply();

                        // enter the app
                        Intent intent = new Intent(getApplicationContext(), SplashMap.class);
                        startActivity(intent);
                        finish();
                    }
                    else{
                        Toast.makeText(CreatePWActivity.this,"Password doesn't match",Toast.LENGTH_SHORT).show();
                    }
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
