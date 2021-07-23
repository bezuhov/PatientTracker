package com.example.myapplication;

public class User {
    public String fullname,email;
    public Double latitude,longitude;
    public String kullanici_tipi,Uid,tel,photoUrl;


    public User(){

    }
    public User(String fullname,String email,String kullanici_tipi,String Uid,String tel){
        this.fullname=fullname;
        this.email=email;
        this.kullanici_tipi=kullanici_tipi;
        this.Uid=Uid;
        this.tel=tel;
    }




}
