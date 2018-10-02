package com.example.dell.newsreader;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class DownloadTask extends AsyncTask<String,Void,String> {
    String data="";




    @Override
    protected String doInBackground(String... strings) {
        URL url;
        HttpURLConnection urlConnection = null;
        MainActivity.emptyTable();
        try{
            url = new URL(strings[0]);
            urlConnection = (HttpURLConnection)url.openConnection();
            InputStream in = urlConnection.getInputStream();
            //InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while (line!=null){
                line=bufferedReader.readLine();
                data += line;
            }
            JSONArray jsonArray = new JSONArray(data);
            ArrayList<String> articleID = new ArrayList<String>();
            for(int a=0;a<jsonArray.length();a++){
                articleID.add(jsonArray.getString(a));
            }
            URL url02 = new URL(strings[1]);
            HttpURLConnection urlConnection02 = (HttpURLConnection)url02.openConnection();
            InputStream in02 = urlConnection02.getInputStream();
            BufferedReader bufferedReader02 = new BufferedReader(new InputStreamReader(in02));
            String a01="";
            String line01="";
            while(line01!=null){
                line01 = bufferedReader02.readLine();
                a01+=line01;
            }
            JSONArray jsonArray01 = new JSONArray(a01);
            for(int a=0;a<jsonArray01.length();a++){
                articleID.add(jsonArray01.getString(a));
            }

            for (int i=0;i<articleID.size();i++){
                URL articleurl;String articledata="";
                HttpURLConnection articleurlConnection = null;


                    articleurl = new URL("https://hacker-news.firebaseio.com/v0/item/"+articleID.get(i)+".json?print=pretty");
                    articleurlConnection = (HttpURLConnection)articleurl.openConnection();
                    InputStream articlein = articleurlConnection.getInputStream();
                    BufferedReader bufferedReader2 = new BufferedReader(new InputStreamReader(articlein));
                    String linea="";
                    while (linea!=null){
                        linea=bufferedReader2.readLine();
                        articledata+=linea;
                    }
                JSONObject jsonObject = new JSONObject(articledata);
                    if(!jsonObject.isNull("title")&&!jsonObject.isNull("url")) {
                        String title = jsonObject.getString("title");
                        String link = jsonObject.getString("url");
                        String sourceData="";
                        URL sourceUrl = new URL(link);
                        HttpURLConnection sourceConn = (HttpURLConnection)sourceUrl.openConnection();
                        InputStream sourceIn = sourceConn.getInputStream();
                        BufferedReader bufferedReader3 = new BufferedReader(new InputStreamReader(sourceIn));
                        String linec="";
                        while(linec!=null){
                            linec=bufferedReader3.readLine();
                            sourceData +=linec;
                        }

                        MainActivity.insert(title,link,sourceData);
                    }
            }


        }catch (Exception e){e.printStackTrace();}

        return null;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        Log.i("statusquo","done");
        MainActivity.sync();
        MainActivity.textView2.setVisibility(View.INVISIBLE);

    }
}

