package carleton.xlproject.mysecretediary;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = DiaryTaker.class.getSimpleName();
    private static final int EditorRequestCode = 1001 ;
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        editNewDiary();

        // using an adaptor to display
        cursorAdapter = new DiaryCursorAdaptor(this,null,0);

        ListView list = findViewById(android.R.id.list);
        list.setAdapter(cursorAdapter);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DiaryEditorActivity.class);
                Uri uri = Uri.parse(DiaryTaker.CONTENT_URI +"/" +id);
                intent.putExtra(DiaryTaker.CONTENT_ITEM_TYPE, uri);
                startActivityForResult(intent, EditorRequestCode);
            }
        });

        getSupportLoaderManager().initLoader(0, null, this);
    }

    private void editNewDiary() {
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),DiaryEditorActivity.class);
                startActivityForResult(intent,EditorRequestCode);
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == EditorRequestCode && resultCode == RESULT_OK) {
            restartLoader();
        }
    }

   /* private void insertDiary(String diaryText) {
        ContentValues values = new ContentValues();
        ContentValues dateValues = new ContentValues();
        Date date=new Date();
        SimpleDateFormat dateFormat=new SimpleDateFormat("YYYY-MM-dd");

        values.put(DBOpenhelper.DIARY_TEXT, diaryText);
        dateValues.put(DBOpenhelper.DIARY_DATE, dateFormat.format(date));

        Uri diaryUri = getContentResolver().insert(DiaryTaker.CONTENT_URI,values);
        Uri diaryUri2 = getContentResolver().insert(DiaryTaker.CONTENT_URI,dateValues);

        Log.d(TAG,"insert diary: " + diaryUri.getLastPathSegment());
    }*/

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.set_location:
                Intent intent = new Intent(getApplicationContext(),GetLocation.class);
                startActivityForResult(intent,EditorRequestCode);
                break;
            case R.id.action_delete_data:
                deleteAllDiary();
                break;
        }

        return super.onOptionsItemSelected(item);

    }


    private void restartLoader() {
        getSupportLoaderManager().restartLoader(0,null,this);
    }

    private void deleteAllDiary() {
        DialogInterface.OnClickListener dialogClickListen =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int button) {
                        if(button == DialogInterface.BUTTON_POSITIVE){
                            //to delete everything
                            getContentResolver().delete(DiaryTaker.CONTENT_URI,null,null);
                            //refresh everything
                            restartLoader();
                            Toast.makeText(MainActivity.this, "All Diaries delete",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                };
        AlertDialog.Builder deleteBuilder = new AlertDialog.Builder(this);
        deleteBuilder.setMessage("Are you sure to delete all diaries?").setPositiveButton(getString(android.R.string.yes),dialogClickListen).setNegativeButton(getString(android.R.string.no),dialogClickListen).show();

    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        return new CursorLoader(this, DiaryTaker.CONTENT_URI,null,null,null,null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

        cursorAdapter.swapCursor(null);
    }
}
