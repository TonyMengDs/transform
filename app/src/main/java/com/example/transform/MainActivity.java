package com.example.transform;

import androidx.appcompat.app.AppCompatActivity;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,Runnable{

    private static String TAG = "Main";


    TextView show;
    Handler handler;

    float dollarRate;
    float euroRate;
    float wonRate;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        show = (TextView)findViewById(R.id.txt);

        SharedPreferences sharedPreferences = getSharedPreferences("myrate",Activity.MODE_PRIVATE);
        PreferenceManager.getDefaultSharedPreferences(this);
        dollarRate = sharedPreferences.getFloat("dollar_rate",0.0f);
        euroRate = sharedPreferences.getFloat("euro_rate",0.0f);
        wonRate = sharedPreferences.getFloat("won_rate",0.0f);

        Button btn1 = findViewById(R.id.btn1);
        btn1.setOnClickListener(this);
        Button btn2 = findViewById(R.id.btn2);
        btn2.setOnClickListener(this);
        Button btn3 = findViewById(R.id.btn3);
        btn3.setOnClickListener(this);

        Thread t = new Thread(this);
        t.start();

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 5) {
                    Bundle bdl = (Bundle) msg.obj;
                    dollarRate = bdl.getFloat("dollar-rate");
                    euroRate = bdl.getFloat("euro-rate");
                    wonRate = bdl.getFloat("won-rate");

                    Log.i(TAG, "handleMessage:dollarRate:" + dollarRate );
                    Log.i(TAG, "handleMessage:euroRate:" + euroRate );
                    Log.i(TAG, "handleMessage:wonRate:" + wonRate );

                    Toast.makeText(MainActivity.this,"汇率已更新",Toast.LENGTH_SHORT).show();
                }
                super.handleMessage(msg);
            }
        };

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu1,menu);
        return  true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        EditText inp = findViewById(R.id.inp);
        String str = inp.getText()+"";
        double num = Double.parseDouble(str);
        if(item.getItemId()==R.id.Item1)
        {
            num = num *dollarRate;
            inp.setText(num+"");
        }
        else if(item.getItemId()==R.id.Item2)
        {
            num = num *euroRate;
            inp.setText(num+"");
        }
        else if(item.getItemId()==R.id.Item3)
        {
            num = num *wonRate;
            inp.setText(num+"");
        }
        return super.onOptionsItemSelected(item);
    }

        @Override
    public void onClick(View view) {

        EditText inp = findViewById(R.id.inp);
        String str = inp.getText()+"";
        double num = Double.parseDouble(str);
        switch (view.getId())
        {
            case R.id.btn1:
                num = num *dollarRate;
                inp.setText(num+"");
                break;
            case R.id.btn2:
                num = num *euroRate;
                inp.setText(num+"");
                break;
            case R.id.btn3:
                num = num *wonRate;
                inp.setText(num+"");
                break;
            case R.id.btn4:
                Intent intent = new Intent();
                intent.setClass(MainActivity.this, MainActivity2.class);
                Bundle bundle = new Bundle();
                intent.putExtras(bundle);//将Bundle添加到Intent,也可以在Bundle中添加相应数据传递给下个页面,例如：bundle.putString("abc", "bbb");
                startActivityForResult(intent, 0);
            default:
                break;
        }
    }
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0 && resultCode == Activity.RESULT_OK) {
            Bundle bundle = data.getExtras();
            dollarRate = bundle.getFloat("dollar_rate_key",0.0f);
            euroRate = bundle.getFloat("euro_rate_key",0.0f);
            wonRate= bundle.getFloat("won_rate_key",0.0f);
            Log.i(TAG, "onActivityResult:dollarRate" + dollarRate);
            Log.i(TAG, "onActivityResult:euroRate" + euroRate);
            Log.i(TAG, "onActivityResult:wonRate" + wonRate);
        }

        SharedPreferences sp = getSharedPreferences("myrate",Activity.MODE_PRIVATE);

        SharedPreferences.Editor editor = sp.edit();
        editor.putFloat("dollar_rate",  dollarRate);
        editor.putFloat("euro_rate",  euroRate);
        editor.putFloat("won_rate",  wonRate);
        editor.apply();

    }

    @Override
    public void run() {
        Log.i(TAG, "run: run()......");
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


        Bundle bundle = new Bundle();




       /* URL url = null;
        try {
            url = new URL("http://www.usd-cny.com/icbc.htm");
            HttpURLConnection http = (HttpURLConnection) url.openConnection();
            InputStream in = http.getInputStream();

            String html = inputStream2String(in);
            Log.i(TAG, "run: html=" + html);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        String url = "http://www.usd-cny.com/bankofchina.htm";
        Document doc = null;
        try {
            doc = Jsoup.connect(url).get();
            Log.i(TAG,"run:" + doc.title());
        } catch (IOException e) {
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
        for(int i=0;i<tds.size();i+=6){
            Element td1 = tds.get(i);
            Element td2 = tds.get(i+5);
            Log.i(TAG,"run:" + td1.text() + "==>" + td2.text());
            String str1 = td1.text();
            String val = td2.text();
            if("美元".equals(str1))
            {
                bundle.putFloat("dollar-rate",100f/Float.parseFloat(val));
            }
            else if("韩元".equals(str1))
            {
                bundle.putFloat("won-rate",100f/Float.parseFloat(val));
            }
            else if("欧元".equals(str1))
            {
                bundle.putFloat("euro-rate",100f/Float.parseFloat(val));
            }
            Message msg = handler.obtainMessage(5);
            msg.obj = bundle;
            handler.sendMessage(msg);

        }

    }

    private String inputStream2String(InputStream inputStream) throws IOException {
        final int bufferSize = 1024;
        final char[] buffer = new char[bufferSize];
        final StringBuilder out = new StringBuilder();
        Reader in = new InputStreamReader(inputStream, "gb2312");
        for (;;){
            int rsz = in.read(buffer, 0, buffer.length);
            if (rsz < 0)
                break;
            out.append(buffer, 0, rsz);
        }
        return out.toString();
    }
}
