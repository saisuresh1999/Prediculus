package com.example.android.saipredictionalgorithm;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.android.saipredictionalgorithm.R;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.PointsGraphSeries;
import com.jjoe64.graphview.series.Series;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class GraphActivity extends AppCompatActivity {

    PointsGraphSeries<DataPoint> originalDataSeries, finalDataSeries;
    GraphView graphView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_graph);

        Intent ii=getIntent();
        String sss=ii.getStringExtra("numofyears");
        Log.v("ccccccccccccc",sss);
        originalDataSeries = new PointsGraphSeries<>();
        finalDataSeries = new PointsGraphSeries<>();
        graphView = (GraphView) findViewById(R.id.graph);

        Intent intent = getIntent();
        String jsonString = intent.getStringExtra("data");
        try {
            JSONObject jsonObject = new JSONObject(jsonString);
            String[] originalDataPoint = jsonObject.get("original").toString().split(" ");
            String[] finalDataPoint = jsonObject.get("final").toString().split(" ");
            int j=2010,k=2015;
            for(int i=originalDataPoint.length-10;i<originalDataPoint.length;i++){
                originalDataSeries.appendData(new DataPoint(i+1901,Double.parseDouble(originalDataPoint[i])), true, originalDataPoint.length);
           j+=1; }
Log.v("ccecece",finalDataPoint.length+" ");
            for(int i=finalDataPoint.length-Integer.parseInt(sss);i<finalDataPoint.length;i++){

                finalDataSeries.appendData(new DataPoint(i+1901,Double.parseDouble(finalDataPoint[i])), true, finalDataPoint.length);
            k+=1;}
            finalDataSeries.setColor(Color.GREEN);
            originalDataSeries.setColor(Color.RED);
            graphView.addSeries(finalDataSeries);
            graphView.addSeries(originalDataSeries);
            graphView.getViewport().setScalable(true);
            graphView.getViewport().setScalableY(true);

            finalDataSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {
                    Toast.makeText(getApplicationContext(), "On Data Point clicked: "+dataPoint, Toast.LENGTH_SHORT).show();
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
