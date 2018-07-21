package com.example.dell.newsreaderapp;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity {
ArrayList<String> Titles = new ArrayList<>();
ArrayList<String> Content = new ArrayList<>();
ArrayAdapter arrayAdapter;
SQLiteDatabase database;

public void Updateview(){
    Cursor c = database.rawQuery("SELECT * FROM articles",null);
    int contentid = c.getColumnIndex("content");
    int titleid = c.getColumnIndex("title");
    if(c.moveToFirst()){
        Titles.clear();
        Content.clear();
        do {
            Titles.add(c.getString(titleid));
            Content.add(c.getString(contentid));
        }while (c.moveToNext());
        arrayAdapter.notifyDataSetChanged();
    }
}

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ListView listView = (ListView)findViewById(R.id.lst);
        arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, Titles);
        listView.setAdapter(arrayAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(getApplicationContext(), Articleactivity.class);
                intent.putExtra("content", Content.get(i));

                startActivity(intent);
            }
        });
        database = this.openOrCreateDatabase("Articles", MODE_PRIVATE, null);
        database.execSQL("CREATE TABLE IF NOT EXISTS articles(id INTEGER PRIMARY KEY, articleid INTEGER,title VARCHAR, content VARCHAR)");
        downloadnews news = new downloadnews();
        news.execute("https://hacker-news.firebaseio.com/v0/topstories.json?print=pretty");
        Updateview();

    }

 public class downloadnews extends AsyncTask<String, Void, String>{
     @Override
     protected String doInBackground(String... strings) {
        String result = " ";
         URL url = null;
         HttpsURLConnection connection = null;
         InputStream in;
         InputStreamReader reader;
         int data;
         try {
             url = new URL(strings[0]);
             connection = (HttpsURLConnection)url.openConnection();
              in = connection.getInputStream();
              reader = new InputStreamReader(in);
              data= reader.read();
             while (data != -1){
                 char ch = (char)data;
                 result += ch;
                 data = reader.read();
             }
         } catch (MalformedURLException e) {
             e.printStackTrace();
         }catch (IOException e){
             e.printStackTrace();
         }

         try {
             JSONArray jsonArray = new JSONArray(result);
             int maxarticle = 20;
            database.execSQL("DELETE FROM articles ");
             if(jsonArray.length() < 20)
                 maxarticle = jsonArray.length();

             for (int i = 0;i < maxarticle;i++){
                 String articleid = jsonArray.getString(i);
                 url = new URL("https://hacker-news.firebaseio.com/v0/item/"+articleid+".json?print=pretty");
                 connection = (HttpsURLConnection)url.openConnection();
                 in = connection.getInputStream();
                 reader = new InputStreamReader(in);
                 data = reader.read();
                 String articleInfo = "";
                 while (data != -1){
                     char current = (char)data;
                     articleInfo += current;
                     data = reader.read();
                 }

                 JSONObject jsonObject = new JSONObject(articleInfo);
                 if(!jsonObject.isNull("title") && !jsonObject.isNull("url") ){
                     String title = jsonObject.getString("title");
                     String nurl = jsonObject.getString("url");
                     HttpURLConnection nconnection;
                     url = new URL(nurl);
                     nconnection = (HttpURLConnection) url.openConnection();
                     in = nconnection.getInputStream();
                     reader = new InputStreamReader(in);
                     data = reader.read();
                     String articlecontent = "";
                     while (data != -1){
                         char current = (char)data;
                         articlecontent += current;
                         data = reader.read();
                     }
                     Log.i("hello", "vaibhav");
                     String sql = "INSERT INTO articles (articleid, title, content) VALUES (?, ?, ?)";
                     SQLiteStatement statement = database.compileStatement(sql);
                     statement.bindString(1, articleid);
                     statement.bindString(2, title);
                     statement.bindString(3, articlecontent);
                     statement.execute();
                 }

             }
         } catch (JSONException e) {
             e.printStackTrace();
         } catch (MalformedURLException e) {
             e.printStackTrace();
         } catch (IOException e) {
             e.printStackTrace();
         }
         return result;
     }

     @Override
     protected void onPostExecute(String s) {
         super.onPostExecute(s);
         Updateview();
     }
 }
}
