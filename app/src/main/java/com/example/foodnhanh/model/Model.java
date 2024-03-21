package com.example.foodnhanh.model;

public class Model {
    String mDrinkName;
    String mDrinkDetail;
    int mDrinkPhoto;

    public Model() {
    }

    public Model(String mDrinkName, String mDrinkDetail, int mDrinkPhoto) {
        this.mDrinkName = mDrinkName;
        this.mDrinkDetail = mDrinkDetail;
        this.mDrinkPhoto = mDrinkPhoto;
    }

    public String getmDrinkName() {
        return mDrinkName;
    }

    public void setmDrinkName(String mDrinkName) {
        this.mDrinkName = mDrinkName;
    }

    public String getmDrinkDetail() {
        return mDrinkDetail;
    }

    public void setmDrinkDetail(String mDrinkDetail) {
    }

    public int getmDrinkPhoto() {
        return mDrinkPhoto;
    }

    public void setmDrinkPhoto(int mDrinkPhoto) {
        this.mDrinkPhoto = mDrinkPhoto;
    }
}
