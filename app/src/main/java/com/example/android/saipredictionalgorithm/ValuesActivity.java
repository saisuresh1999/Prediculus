package com.example.android.saipredictionalgorithm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ValuesActivity extends AppCompatActivity {
TextView t1,t2,t3;
ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_values);
t1=(TextView) findViewById(R.id.stateid);
  t2=(TextView) findViewById(R.id.monthid);
  t3=(TextView) findViewById(R.id.yearid);
        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("data");
        String month= intent.getStringExtra("numofmonth");
        String year= intent.getStringExtra("numofyears");
        String state= intent.getStringExtra("nameofstate");
listView=(ListView) findViewById(R.id.list);
        int date=2019+Integer.parseInt(year);

        t1.setText(state);
        t2.setText(month);
        t3.setText("2019-"+""+date);

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(jsonString);
            String[] originalDataPoint = jsonObject.get("final").toString().split(" ");
         int j=1;
         String[] val=new String[Integer.parseInt(year)+1];

         List<String> arrayList = new ArrayList<>();
            Collections.addAll(arrayList, originalDataPoint);

            for(int i=0;i<originalDataPoint.length-Integer.parseInt(year);i++)
            {
                arrayList.remove(0);
            }

            Log.v("val"," "+arrayList.size());
            //   listView.setAdapter(new ArrayAdapter<String>(C.this, R.layout.item_tag, topics));
for (int i=0;i<arrayList.size();i++)
{int dat=2019+i;
   String s=arrayList.get(i);
   int a=s.indexOf('.')+2;
   s=s.substring(0,a);
   s="Year: "+dat+"        AMT: "+s+" mm";
arrayList.set(i,s);
}
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>
                    (this, android.R.layout.simple_list_item_1, arrayList);
            listView.setAdapter(arrayAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }
}
