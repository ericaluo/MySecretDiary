package carleton.xlproject.mysecretediary;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class DiaryTaker extends ContentProvider {

    private static final String AUTHORITY = "carleton.xlproject.mysecretediary.DiaryTaker";

    //represent entire data set
    private static final String BASE_PATH = "diary";


    public static Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY +"/" + BASE_PATH);

    //constants to identity the requested operation
    private static final int DIARY = 1;
    private static final int DIARY_ID = 2;
    public static final String CONTENT_ITEM_TYPE ="Diary";

    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
    static{
        // it will execute the first time anything is called from this class
        uriMatcher.addURI(AUTHORITY, BASE_PATH, DIARY);
        uriMatcher.addURI(AUTHORITY, BASE_PATH + "/#", DIARY_ID);
    }


    private SQLiteDatabase database;

    @Override
    public boolean onCreate() {
        DBOpenhelper Diaryhelper = new DBOpenhelper(getContext());
        database = Diaryhelper.getWritableDatabase();
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        if(uriMatcher.match(uri) == DIARY_ID){
            selection = DBOpenhelper.DIARY_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(DBOpenhelper.TABLE_DIARY, DBOpenhelper.ALL_COLUMN, selection,
                null,null,null, DBOpenhelper.DIARY_CREATED + " DESC");
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        long id = database.insert(DBOpenhelper.TABLE_DIARY,null, values);
        return Uri.parse(BASE_PATH +"/"+id);
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.delete(DBOpenhelper.TABLE_DIARY,selection,selectionArgs);
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        return database.update(DBOpenhelper.TABLE_DIARY,values,selection,selectionArgs);
    }
}
