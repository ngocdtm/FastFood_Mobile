package com.example.foodnhanh.model;

import java.io.Serializable;

public class MainModel implements Serializable {
    String name,img_url;
           int price;
String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public MainModel(String name, String img_url, int price) {
        this.name = name;
        this.img_url = img_url;
        this.price = price;
    }

    public MainModel() {
    }
}
