package com.example.android.saipredictionalgorithm;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.saipredictionalgorithm.GraphActivity;
import com.example.android.saipredictionalgorithm.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class RainFallActivity extends AppCompatActivity {

    Spinner mySpinnerStates, mySpinnerMonths;
    EditText noOfYears;
    String month, state, number , stringLine="";
    Button mainPredictButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rain_fall);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mySpinnerStates =(Spinner) findViewById(R.id.stateSpinner);
        mySpinnerMonths =(Spinner) findViewById(R.id.monthSpinner);
        noOfYears = (EditText) findViewById(R.id.numberOfYear);
        mainPredictButton = (Button) findViewById(R.id.button);

        ArrayAdapter<String> myAdapter=new ArrayAdapter<String>(RainFallActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.states));
        myAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinnerStates.setAdapter(myAdapter);
        mySpinnerStates.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long arg3) {
                state = mySpinnerStates.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        ArrayAdapter<String> myAdapterTwo=new ArrayAdapter<String>(RainFallActivity.this,
                android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.months));
        myAdapterTwo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mySpinnerMonths.setAdapter(myAdapterTwo);
        mySpinnerMonths.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                                       int pos, long arg3) {
                month = mySpinnerMonths.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
            }
        });


        mainPredictButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = noOfYears.getText().toString();
                if(!month.isEmpty() && !state.isEmpty() && !number.isEmpty()){
                    new CollectData().execute();
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    private class CollectData extends AsyncTask<String, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "AI started preprocessing Data",
                    Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(String... strings) {
            InputStream inputStream = null;
            Scanner sc = null;
            try {
                Log.v("doinbg","funct");
                inputStream = getResources().openRawResource(R.raw.rainfall);
                sc = new Scanner(inputStream, "UTF-8");
                while (sc.hasNextLine()) {
                    stringLine = stringLine + sc.nextLine() + "\\r\\n";
                }
                if (sc.ioException() != null) {
                    throw sc.ioException();
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (sc != null) {
                    sc.close();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getApplicationContext(), "preprocessing COMPLETED!", Toast.LENGTH_LONG).show();
            new MakeNetworkCall().execute("http://192.168.43.204:5051/predict");
        }
    }

    @SuppressLint("StaticFieldLeak")
    private class MakeNetworkCall extends AsyncTask<String, Void, Void> {

        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(getApplicationContext(), "AI started predicting", Toast.LENGTH_LONG).show();
        }

        @Override
        protected Void doInBackground(String... string) {

            try {

                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpPost getRequest = new HttpPost(string[0]);
                StringEntity input = new StringEntity("{\"file\":\""+stringLine+"\",\"month\":\""+month+"\",\"state\":\""+state+"\",\"number\":\""+number+"\"}");
                input.setContentType("application/json");
                getRequest.setEntity(input);
                HttpResponse response = httpClient.execute(getRequest);

                if (response.getStatusLine().getStatusCode() != 200) {
                    throw new RuntimeException("Failed : HTTP error code : "
                            + response.getStatusLine().getStatusCode());
                }

                BufferedReader br = new BufferedReader(
                        new InputStreamReader((response.getEntity().getContent())));

                String output;
                while ((output = br.readLine()) != null) {
                    Intent intent = new Intent(getApplicationContext(),GraphActivity.class);
                    intent.putExtra("data", output);
                    intent.putExtra("numofyears",number);
                    intent.putExtra("nameofstate",state);
                    intent.putExtra("numofmonth",month);



                    startActivity(intent);
                }

                httpClient.getConnectionManager().shutdown();

            } catch (IOException e) {

                e.printStackTrace();
            }


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Toast.makeText(getApplicationContext(), "Prediction COMPLETED!", Toast.LENGTH_LONG).show();
        }
    }
}
