package com.example.dell.newsreader;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
      public static ArrayAdapter<String> arrayAdapter;
      public static ListView listView;
      public static ArrayList<String> titlearray;
      public static ArrayList<String> urlsarray;
      public static ArrayList<String> sourcesarray;
      public static SQLiteDatabase database;
      public static TextView textView2;
      boolean about =true;
      TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        database = this.openOrCreateDatabase("Articles",MODE_PRIVATE,null);
        database.execSQL("CREATE TABLE IF NOT EXISTS article (title VARCHAR,urls VARCHAR,source VARCHAR)");

        listView=(ListView)findViewById(R.id.listView);
        textView = (TextView)findViewById(R.id.textView);
        textView2 = (TextView)findViewById(R.id.textView2);
        titlearray = new ArrayList<String>();
        urlsarray = new ArrayList<String>();
        sourcesarray = new ArrayList<String>();
        arrayAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,titlearray);
        listView.setAdapter(arrayAdapter);
        sync();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(),NewsActivity.class);

                intent.putExtra("position",position);startActivity(intent);

            }
        });



    }

    public static void insert(String title,String urls,String source) {
        try {
            String execInsert="INSERT INTO article(title,urls,source) VALUES(?,?,?)";
            SQLiteStatement sqLiteStatement = database.compileStatement(execInsert);
            sqLiteStatement.bindString(1,title);
            sqLiteStatement.bindString(2,urls);
            sqLiteStatement.bindString(3,source);
            sqLiteStatement.execute();;
        } catch (Exception e) {
            Log.i("Insertion","Failed");
            e.printStackTrace();
        }
    }
    public static void emptyTable(){
        database.execSQL("DELETE FROM article");
        database.execSQL("VACUUM");
    }

    public static void sync(){
        try {
            Cursor c=database.rawQuery("SELECT * FROM article",null);
            int titleindex = c.getColumnIndex("title");
            int urlsindex = c.getColumnIndex("urls");
            int sourceindex = c.getColumnIndex("source");

            if(c.moveToFirst()){
                titlearray.clear();
                urlsarray.clear();
                sourcesarray.clear();
                do{
                    titlearray.add(c.getString(titleindex));
                    urlsarray.add(c.getString(urlsindex));
                    sourcesarray.add(c.getString(sourceindex));
                }while(c.moveToNext());
                arrayAdapter.notifyDataSetChanged();
            }

        }catch(Exception e){e.printStackTrace();
        Log.i("SyncFaild","failed");
        }

        arrayAdapter.notifyDataSetChanged();
    }

    public void onRefresh(View view){
        titlearray.clear();
        urlsarray.clear();
        sourcesarray.clear();
        arrayAdapter.notifyDataSetChanged();
        DownloadTask task = new DownloadTask();
        try{
            task.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty","https://hacker-news.firebaseio.com/v0/newstories.json?print=pretty");
        }catch (Exception e){e.printStackTrace();}
        textView2.setVisibility(View.VISIBLE);
        textView.setVisibility(View.INVISIBLE);this.about=true;

    }
    public void onAbout(View view){
        if(this.about == true){
            textView.setVisibility(View.VISIBLE);this.about=false;
        }else{
            textView.setVisibility(View.INVISIBLE);this.about=true;
        }
    }
}
