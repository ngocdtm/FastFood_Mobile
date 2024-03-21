package com.example.foodnhanh.model;

public class ReadWriteUserDetails
{
    public  String fullName, birthday, gender, mobile;

    //Constuctor
    public ReadWriteUserDetails()
    {};

    public ReadWriteUserDetails (String txtfullName, String txtBirthday, String txtGender, String txtPhone)
    {
        this.fullName = txtfullName;
        this.birthday = txtBirthday;
        this.gender = txtGender;
        this.mobile = txtPhone;
    }
}
