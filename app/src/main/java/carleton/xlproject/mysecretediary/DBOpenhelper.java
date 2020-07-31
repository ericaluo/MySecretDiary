package carleton.xlproject.mysecretediary;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;

public class DBOpenhelper extends SQLiteOpenHelper {

    //db name and version
    private static final String DATABASE_NAME = "diary.db";
    private static final int DATABASE_VERSION = 1;

    //identifying table and columns
    public static final String TABLE_DIARY = "diary";
    public static final String DIARY_ID = "_id";
    public static final String DIARY_TEXT = "diaryContent";
    public static final String DIARY_CREATED = "diaryCreated";

    public static final String[] ALL_COLUMN =
            {DIARY_ID, DIARY_TEXT, DIARY_CREATED};

    //SQL to create table
    private static final String TABLE_CREATE ="CREATE TABLE " + TABLE_DIARY +" (" + DIARY_ID +" INTEGER PRIMARY KEY AUTOINCREMENT, "  + DIARY_TEXT +" CONTENT, " + DIARY_CREATED + " TEXT default CURRENT_TIMESTAMP" +")";

    public DBOpenhelper(@Nullable Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DIARY);
        onCreate(db);
    }
}
