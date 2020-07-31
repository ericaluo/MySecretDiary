package carleton.xlproject.mysecretediary;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.text.SimpleDateFormat;
import java.util.Date;
import android.util.Base64;
import javax.crypto.spec.IvParameterSpec;

public class DiaryEditorActivity extends AppCompatActivity {

    private String action;
    private EditText editor;
    private String noteFilter;
    private String oldText;
    private static final String CipherMode = "AES/CFB/NoPadding";
    // the key is random generated online
    private static final String cipherKey = "0nfwWRwq8+aELX4F";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diary_editor);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        editor = findViewById(R.id.editDiary);

        Intent intent = getIntent();
        Uri uri = intent.getParcelableExtra(DiaryTaker.CONTENT_ITEM_TYPE);

        if(uri ==null){
            action = Intent.ACTION_INSERT;
            setTitle("New Diary");
        }
        //load the old string
        else{
            action = Intent.ACTION_EDIT;
            noteFilter = DBOpenhelper.DIARY_ID + "=" + uri.getLastPathSegment();

            Cursor cursor = getContentResolver().query(uri,DBOpenhelper.ALL_COLUMN, noteFilter,null,null);
            cursor.moveToFirst();
            oldText = cursor.getString(cursor.getColumnIndex(DBOpenhelper.DIARY_TEXT));
            try {
                oldText = decodeText(oldText);
            } catch (Exception e) {
                e.printStackTrace();
            }
            editor.setText(oldText);
            editor.requestFocus();

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
                try {
                    finishEdit();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case R.id.action_delete:
                deleteDiary();
                break;
        }
        return true;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(action.equals(Intent.ACTION_EDIT)){
            getMenuInflater().inflate(R.menu.menu_editor,menu);
        }
        return true;
    }

    private void finishEdit() throws Exception {
        String diaryText = editor.getText().toString().trim();

        switch (action){
            case Intent.ACTION_INSERT:
                if(diaryText.length() ==0){
                    setResult(RESULT_CANCELED);
                }
                else{
                    insertDiary(diaryText);
                }
                break;
            case Intent.ACTION_EDIT:
                if(diaryText.length() ==0){
                    //delete diary
                    deleteDiary2();
                } else if(oldText.equals(diaryText)){
                    setResult(RESULT_CANCELED);
                } else{
                    updateDiary(diaryText);
                }
        }
        finish();
    }

    private void deleteDiary2() {
        getContentResolver().delete(DiaryTaker.CONTENT_URI, noteFilter, null);
        Toast.makeText(this, "Diary Deleted", Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
        finish();
    }

    private void updateDiary(String diaryText) throws Exception {
        ContentValues values = new ContentValues();
        diaryText = encodeText(diaryText);
        values.put(DBOpenhelper.DIARY_TEXT, diaryText);
        getContentResolver().update(DiaryTaker.CONTENT_URI,values,noteFilter,null);
        Toast.makeText(this,"Diary Update",Toast.LENGTH_SHORT).show();
        setResult(RESULT_OK);
    }

    private void deleteDiary() {
        DialogInterface.OnClickListener dialogClickListen =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if (button == DialogInterface.BUTTON_POSITIVE) {
                            getContentResolver().delete(DiaryTaker.CONTENT_URI, noteFilter, null);
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                };
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
        deleteBuilder.setMessage("Are you sure to delete this diary?").setPositiveButton(getString(android.R.string.yes),dialogClickListen).setNegativeButton(getString(android.R.string.no),dialogClickListen).show();
        //Toast.makeText(this, "Diary Deleted", Toast.LENGTH_SHORT).show();
    }

    private void insertDiary(String diaryText) throws Exception {
        ContentValues values = new ContentValues();

        //AES encryption for the diaryText
        diaryText = encodeText(diaryText);
        values.put(DBOpenhelper.DIARY_TEXT, diaryText);
        getContentResolver().insert(DiaryTaker.CONTENT_URI,values);
        setResult(RESULT_OK);
    }

    @Override
    public void onBackPressed(){
        try {
            finishEdit();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // create AES encryption
    public static String encodeText(String encodeText) throws Exception {
        try {
            Cipher cipher = Cipher.getInstance(CipherMode);
            SecretKeySpec keyspec = new SecretKeySpec(cipherKey.getBytes(), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, keyspec, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            byte[] encrypted = cipher.doFinal(encodeText.getBytes());
            return Base64.encodeToString(encrypted, Base64.DEFAULT);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //decode AES encryption
    public static String decodeText(String encodedText) throws Exception{
        try {
            byte[] encrypted1 = Base64.decode(encodedText.getBytes(), Base64.DEFAULT);
            Cipher cipher = Cipher.getInstance(CipherMode);
            SecretKeySpec keyspec = new SecretKeySpec(cipherKey.getBytes(), "AES");
            cipher.init(Cipher.DECRYPT_MODE, keyspec, new IvParameterSpec(
                    new byte[cipher.getBlockSize()]));
            byte[] original = cipher.doFinal(encrypted1);
            return new String(original, "UTF-8");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

