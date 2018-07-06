package com.example.admin.contentprovider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    EditText name,composer,masterpiece,type;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void onClickAddName(View view) {
        ContentValues values = new ContentValues();
        values.put(SongsProvider.NAME, name.getText().toString());
        values.put(SongsProvider.COMPOSER, composer.getText().toString());
        values.put(SongsProvider.MASTERPIECE, type.getText().toString());
        values.put(SongsProvider.TYPE, masterpiece.getText().toString());

    }

    public void onClickRetrieveSongs(View view) {
        String URL = "content//com.example.admin.contentprovider.SongsProvider";
        Uri songs = Uri.parse(URL);
        Cursor c = managedQuery(songs, null, null, null, "name");
        if (c.moveToFirst()) {
            do {
                Toast.makeText(this, c.getString(c.getColumnIndex(SongsProvider._ID) + c.getColumnIndex(SongsProvider.NAME)), Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
    }


}
