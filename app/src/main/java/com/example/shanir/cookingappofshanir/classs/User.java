package com.example.shanir.cookingappofshanir.classs;

import android.graphics.Bitmap;

import java.util.ArrayList;

/**
 * Created by STUDENT on 20/03/2018.
 */

public class User {
    private String id;
    private  String firstname;
    private  String lastname;
    private String bitmap;
    private  String phone;
    private  String email;
    private  String pass;
    private ArrayList<String> listIngredient;

    public User(String firstname,String lastname,String id, String pass,
                String phone, String email, ArrayList<String> listIngredient)
    {
        this.firstname=firstname;
        this.lastname=lastname;
        this.id=id;
        this.pass=pass;
        this.phone=phone;
        this.email=email;
        this.listIngredient=listIngredient;
    }
    public User(String firstname,String lastname,String id, String pass,
                String phone, String email, ArrayList<String> listIngredient,String bitmap)
    {
        this.firstname=firstname;
        this.lastname=lastname;
        this.id=id;
        this.pass=pass;
        this.phone=phone;
        this.email=email;
        this.listIngredient=listIngredient;
        this.bitmap=bitmap;
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
    }

    public User()
    {

    }


    public ArrayList<String> getListIngredient() {
        return listIngredient;
    }

    public void setListIngredient(ArrayList<String> listIngredient) {
        this.listIngredient = listIngredient;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    private String idkey;


    public String getIdkey()
    {
        return this.idkey;
    }
    public void setIdkey(String  idkey)
    {
        this.idkey=idkey;
    }

    @Override
    public String toString() {
        return "פרטי המשתמש הם: " +"\n"+"\n"+
                "תעודת זהות: " + id + "\n" +
                "שם פרטי: " + firstname + "\n" +
                "שם משפחה: " + lastname + "\n" +
                "פלאפון: " + phone + "\n" +
                "מייל: " + email + "\n" +
                "סיסמא: " + pass + "\n"
               ;
    }
}
