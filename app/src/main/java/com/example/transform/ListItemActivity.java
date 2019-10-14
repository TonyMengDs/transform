package com.example.transform;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ListItemActivity extends ListActivity implements Runnable, AdapterView.OnItemClickListener {

    Handler handler;
    private ArrayList<HashMap<String,String>> listItems;
    private SimpleAdapter listItemAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initListView();

        //MyAdapter myAdapter = new MyAdapter(this,R.layout.activity_list_item,listItems);//可注释掉并下一行更改为listItemAdapter
        //this.setListAdapter(myAdapter);
        this.setListAdapter(listItemAdapter);
        Thread t = new Thread(this);
        t.start();

        handler = new Handler(){
            @Override
            public void handleMessage(Message msg){
                if(msg.what==7){
                    List<HashMap<String,String>> list2 = (List<HashMap<String, String>>)msg.obj;
                    listItemAdapter = new SimpleAdapter(ListItemActivity.this,list2,R.layout.activity_list_item,new String[]{"ItemTitle","ItemDetail"},
                            new int[]{R.id.itemTitle,R.id.itemDetail});
                    setListAdapter(listItemAdapter);

                }
                super.handleMessage(msg);
            }
        };
        getListView().setOnItemClickListener(this);
    }

    private void initListView()
    {
        listItems = new ArrayList<HashMap<String,String>>();
        for(int i=0;i<10;i++)
        {
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("ItemTitle","Rate: " + i);
            map.put("ItemDetail","detail" + i);
            listItems.add(map);
        }

        listItemAdapter = new SimpleAdapter(this,listItems,R.layout.activity_list_item,new String[]{"ItemTitle","ItemDetail"},
                new int[]{R.id.itemTitle,R.id.itemDetail});
    }

    @Override
    public void run() {
        List<HashMap<String,String>> retList = new ArrayList<HashMap<String, String>>();
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
            HashMap<String,String> map = new HashMap<String,String>();
            map.put("ItemTitle",str1);
            map.put("ItemDetail",val);
            retList.add(map);
        }

        Message msg = handler.obtainMessage(7);
        msg.obj = retList;
        handler.sendMessage(msg);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        HashMap<String,String> map = (HashMap<String,String>) getListView().getItemAtPosition(position);
        String titleStr = map.get("ItemTitle");
        String detailStr = map.get("ItemDetail");
        TextView title = (TextView) view.findViewById(R.id.itemTitle);
        TextView detail = (TextView) view.findViewById(R.id.itemDetail);
        String title2 = String.valueOf(title.getText());
        String detail2 = String.valueOf(detail.getText());

        Intent rateCalc = new Intent(this,RateCalcActivity.class);
        rateCalc.putExtra("title",titleStr);
        rateCalc.putExtra("rate",Float.parseFloat(detailStr));
        startActivity(rateCalc);

    }
}
