package com.example.quadeer.irsm;

import android.Manifest;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quadeer.irsm.R;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.Result;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class ShopActivity extends AppCompatActivity {
    List<Items> itemsList = new ArrayList<>();
    ListView listView;
    MyCustomListAdapter adapter;
    int total = 1;

    public class DownloadTask extends AsyncTask<String,Void,String>{

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
                ArrayList<String> list = new ArrayList<String>();

                //initializing objects
                listView = findViewById(R.id.listView);

                JSONArray jsonList1 = jsonObject.getJSONArray("Details");
                System.out.println(jsonList1);

                Log.i("Product",jsonList1.getString(0));
                Log.i("Product",jsonList1.getString(1));
                Log.i("Product",jsonList1.getString(2));
                Log.i("Product",jsonList1.getString(3));
                Log.i("Product",jsonList1.getString(4));
                String x = jsonList1.getString(2);
                System.out.println("New item "+x);
                itemsList.add(new Items(jsonList1.getString(2), jsonList1.getInt(3), jsonList1.getString(4)));
                Toast.makeText(ShopActivity.this, "Item added successfully", Toast.LENGTH_SHORT).show();
//                System.out.println();
                adapter = new MyCustomListAdapter(ShopActivity.this, R.layout.my_list_items, itemsList);
                listView.setAdapter(adapter);

                SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                Gson gson = new Gson();
                String json = gson.toJson(itemsList);
                editor.putString("task list", json);
                editor.apply();

                //creating the adapter

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop);

        Button buttonSave = findViewById(R.id.btnSave);
        Button buttonBar = findViewById(R.id.btnBar);
        TextView textViewBill = findViewById(R.id.textBill);
        Button buttonCheck = findViewById(R.id.btnCheckout);
        String result;

        loadData();

        final DownloadTask task = new DownloadTask();

        buttonBar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ShopActivity.this,ScanActivity.class);
                startActivity(i);
//                Intent in = getIntent();
//                result[0] = in.getStringExtra("result");
            }
        });

        System.out.println(itemsList);

        buttonCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ShopActivity.this,PaymentActivity.class);
                i.putExtra("LIST", (Serializable) itemsList);
                i.putExtra("total", total);
                startActivity(i);
            }
        });

        Intent in = getIntent();
        result = in.getStringExtra("result");

        System.out.println(result);

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveData();
            }
        });

        Button buttonLoad = findViewById(R.id.btnLoad);
        buttonLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textViewBill = findViewById(R.id.textBill);
                Items resultList;
                int totalBill = 0;
                for (int i = 0; i<itemsList.size(); i++) {
                    resultList = itemsList.get(i);
                    totalBill += resultList.getQty() * resultList.getPrice();
                    total = totalBill;
                    System.out.println(resultList.getProduct()+" "+resultList.getPrice()+" "+resultList.getQuantity());
                }
                textViewBill.setText("Rs. "+Integer.toString(totalBill));
            }
        });

        final EditText editText = findViewById(R.id.editText);
        Button mButton = findViewById(R.id.mButton);
        if (result!= null){
            editText.setText(result);
        }
//        final String result = editText.getText().toString();
//        Log.i("message",result);

        mButton.setOnClickListener(
                new View.OnClickListener()
                {
                    public void onClick(View view)
                    {
                        task.execute("https://recommendationapi.herokuapp.com/api/get_details/"+editText.getText().toString());
                        System.out.println(itemsList);
                    }
                });

    }

    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(itemsList);
        editor.putString("task list", json);
        editor.apply();
    }

    private void loadData() {
        SharedPreferences sharedPreferences = getSharedPreferences("shared preferences", MODE_PRIVATE);
        Gson gson = new Gson();
        String json = sharedPreferences.getString("task list", null);
        Type type = new TypeToken<ArrayList<Items>>() {}.getType();
        itemsList = gson.fromJson(json, type);
        if (itemsList == null){
            itemsList = new ArrayList<>();
        }
        System.out.println(itemsList);
        adapter = new MyCustomListAdapter(ShopActivity.this, R.layout.my_list_items, itemsList);
        listView = findViewById(R.id.listView);
        listView.setAdapter(adapter);

        TextView textViewBill = findViewById(R.id.textBill);
        Items resultList;
        int totalBill = 0;
        for (int i = 0; i<itemsList.size(); i++) {
            resultList = itemsList.get(i);
            totalBill += resultList.getQty() * resultList.getPrice();
            System.out.println(resultList.getProduct()+" "+resultList.getPrice()+" "+resultList.getQuantity());
        }
        textViewBill.setText("Rs. "+Integer.toString(totalBill));

    }

}
