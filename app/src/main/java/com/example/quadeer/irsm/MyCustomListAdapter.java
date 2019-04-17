package com.example.quadeer.irsm;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

//import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MyCustomListAdapter extends ArrayAdapter<Items> {

    Context mCtx;
    int resource;
    List<Items> itemsList;
    View view;


    public MyCustomListAdapter(ShopActivity mCtx, int resource, List<Items> itemsList){
        super(mCtx, resource, itemsList);

        this.mCtx = mCtx;
        this.resource = resource;
        this.itemsList = itemsList;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mCtx);
        view = inflater.inflate(R.layout.my_list_items,null);

        final TextView textViewName = view.findViewById(R.id.textViewName);
        final TextView textViewPrice = view.findViewById(R.id.textViewPrice);
        TextView textViewUnits = view.findViewById(R.id.textViewUnit);
        final Items item = itemsList.get(position);
        final TextView textViewQty = view.findViewById(R.id.qtyView);
        textViewName.setText(item.getProduct());
        textViewQty.setText(Integer.toString(item.getQty()));
        textViewPrice.setText("Rs. "+Integer.toString(item.getPrice()* item.getQty()));
        textViewUnits.setText(item.getQuantity());
        System.out.println();
//        textViewQty.setText(Integer.toString(item.getQty()));


        view.findViewById(R.id.incBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.addToQty();
                Log.i("msg",Integer.toString(item.getPrice()));
                textViewQty.setText(Integer.toString(item.getQty()));
                Log.i("quantiy",Integer.toString(item.getQty()));
                int new_price = item.getPrice() * item.getQty();
                textViewPrice.setText("Rs. "+Integer.toString(new_price));
                notifyDataSetChanged();
            }});
        view.findViewById(R.id.decBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.removeFromQuantity();
                textViewQty.setText(Integer.toString(item.getQty()));
                Log.i("quantiy",Integer.toString(item.getQty()));
                int new_price = item.getPrice() * item.getQty();
                textViewPrice.setText("Rs. "+Integer.toString(new_price));
                notifyDataSetChanged();
            }
        });

        view.findViewById(R.id.btnDelete).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                removeItems(position);
            }
        });


        return view;
    }

    private void removeItems(final int i){
        AlertDialog.Builder builder = new AlertDialog.Builder(mCtx);
        builder.setTitle("Are you sure you want to delete?");

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                itemsList.remove(i);
                notifyDataSetChanged();
            }
        });

        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
