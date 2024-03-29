package com.example.transform;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.ListView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MyListActivity extends AppCompatActivity implements Runnable, AdapterView.OnItemClickListener {
    List<String> data = new ArrayList<String>();
    ArrayAdapter adapter;
    Handler handler;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_list);

        final ListView listView = findViewById(R.id.mylist);


        for(int i=1;i<100;i++){
            data.add("item" + i);
        }
        adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,data);
        listView.setAdapter(adapter);
        listView.setEmptyView(findViewById(R.id.nodata));
        listView.setOnItemClickListener(this);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==7){
                    List<String> list2 = (List<String>)msg.obj;
                    ListAdapter adapter = new ArrayAdapter<String>(MyListActivity.this,android.R.layout.simple_list_item_1,list2);
                    listView.setAdapter(adapter);

                }
                super.handleMessage(msg);
            }
        };
    }

    @Override
    public void onItemClick(AdapterView<?> listv, View view, int position, long id){
        adapter.remove(listv.getItemAtPosition((position)));
        //adapter.notifyDataSetChanged();
    }

    @Override
    public void run() {
        List<String> retList = new ArrayList<String>();
        String url = "http://www.usd-cny.com/bankofchina.htm";
        Document doc = null;
        try {
            Thread.sleep(3000);
            doc = Jsoup.connect(url).get();
            Log.i(TAG,"run:" + doc.title());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Elements tables = doc.getElementsByTag("table");
        /*for(Element table : tables){
            Log.i(TAG,"run:tables["+i+"]=" + table);
            i++;
        }*/
        Element table1 = tables.get(0);
        Log.i(TAG,"run:table1" + table1);
        Elements tds = table1.getElementsByTag("td");
        for(int i=0;i<tds.size();i+=6) {
            Element td1 = tds.get(i);
            Element td2 = tds.get(i + 5);
            Log.i(TAG, "run:" + td1.text() + "==>" + td2.text());
            String str1 = td1.text();
            String val = td2.text();
            retList.add(str1 + "==>" + val);
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = retList;
        handler.sendMessage(msg);
    }

}
