package com.example.quadeer.irsm;

import java.io.Serializable;

public class Items implements Serializable {

    String product, quantity;
    int price, qty;


    public Items(String product, int price, String quantity) {
        this.product = product;
        this.price = price;
        this.quantity = quantity;
        this.qty = 1;
    }

    public String getProduct() {
        return product;
    }

    public int getPrice() {
        return price;
    }

    public String getQuantity() {
        return quantity;
    }

    public void updatePrice(int price){
        this.price = price;
    }


    public int getQty() {
        return qty;
    }

    public void addToQty(){
        this.qty += 1;
    }

    public void removeFromQuantity(){
        if(this.qty > 1){
            this.qty -= 1;
        }
    }
}
