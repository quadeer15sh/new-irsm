package com.example.quadeer.irsm;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class PaymentActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    String text;

    public class PaymentTask extends AsyncTask<String,Void,String>{

        @Override
        protected String doInBackground(String... urls) {
            String result = "";
            URL url;
            HttpURLConnection urlConnection = null;

            try{
                url = new URL(urls[0]);
                urlConnection = (HttpURLConnection) url.openConnection();
                InputStream in = urlConnection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;

            } catch (Exception e){
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            try {
                Log.i("JSON",s);
//                Log.i("message",links);
                JSONObject jsonObject = new JSONObject(s);
                System.out.println(jsonObject);

                String status = jsonObject.getString("Status");
                System.out.println(status);

                if (status.equals("true")){
                    Toast.makeText(PaymentActivity.this, "Transaction Successful", Toast.LENGTH_LONG).show();
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        TextView textViewAmt = findViewById(R.id.textAmt);
        Spinner spinner = findViewById(R.id.spinnerPayment);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.paymentType,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);

        Intent in = getIntent();
        final int total = in.getIntExtra("total",0);
        Bundle bundle = getIntent().getExtras();
        List<Items> itemsList = (List<Items>) in.getSerializableExtra("LIST");
        System.out.println(itemsList.get(0).getPrice());
        textViewAmt.setText("Total Amount: Rs. "+Integer.toString(total));
        Log.i("Total Amount",Integer.toString(total));
        Toast.makeText(this,Integer.toString(total), Toast.LENGTH_SHORT).show();

        findViewById(R.id.btnPay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PaymentTask task = new PaymentTask();
                task.execute("https://recommendationapi.herokuapp.com/send_transaction/1/"+total+"/"+text);
            }
        });
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        text = parent.getItemAtPosition(position).toString();
        Toast.makeText(this, "Payment Option: "+text, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

