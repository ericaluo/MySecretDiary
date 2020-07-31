package carleton.xlproject.mysecretediary;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

public class DiaryCursorAdaptor extends CursorAdapter {

    public DiaryCursorAdaptor(Context context, Cursor cursor, int flags){
        super(context,cursor,flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.diary_list,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        String DiaryText = cursor.getString(cursor.getColumnIndex(DBOpenhelper.DIARY_CREATED));
        int pos = DiaryText.indexOf(8);
        if(pos!=-1){DiaryText = DiaryText.substring(0,pos)+" ...";}

        TextView tv = view.findViewById(R.id.tvDiary);
        tv.setText(DiaryText);
    }

}
