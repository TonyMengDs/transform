package com.example.transform;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.math.BigDecimal;

public class MainActivity2 extends MainActivity{
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        Intent intent = getIntent();
        float dollar2 = (float) intent.getDoubleExtra("dollar_rate_key",0.0f);
        float euro2 = (float) intent.getDoubleExtra("euro_rate_key",0.0f);
        float won2 = (float) intent.getDoubleExtra("won_rate_key",0.0f);
        Button btn5 = findViewById(R.id.btn5);
        btn5.setOnClickListener(this);
    }

    public void onClick(View v)
    {
        EditText inp1 = findViewById(R.id.inp1);
        EditText inp2 = findViewById(R.id.inp2);
        EditText inp3 = findViewById(R.id.inp3);
        String str1 = inp1.getText()+"";
        if(str1.equals(""))
        {
            inp1.setHint("请输入美元汇率，不用写0");
            return;
        }
        String str2 = inp2.getText()+"";
        if(str2.equals(""))
        {
            inp2.setHint("请输入欧元汇率，不用写0");
            return;
        }
        String str3 = inp3.getText()+"";
        if(str3.equals(""))
        {
            inp3.setHint("请输入韩元汇率，不用写0");
            return;
        }
        float dollar2 = (float) Double.parseDouble(str1);
        float euro2 = (float) Double.parseDouble(str2);
        float won2 = (float) Double.parseDouble(str3);
        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();
        bundle.putFloat("dollar_rate_key", dollar2);//添加要返回给页面1的数据
        bundle.putFloat("euro_rate_key", euro2);//添加要返回给页面1的数据
        bundle.putFloat("won_rate_key", won2);//添加要返回给页面1的数据
        intent.putExtras(bundle);
        this.setResult(Activity.RESULT_OK, intent);//返回页面1
        this.finish();
    }
}
