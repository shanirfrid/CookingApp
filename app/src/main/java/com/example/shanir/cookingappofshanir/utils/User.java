package com.example.shanir.cookingappofshanir.utils;

import java.util.ArrayList;

public class User {
    private String id;
    private String firstname;
    private String lastname;
    private String bitmap;
    private String phone;
    private String email;
    private String pass;
    private String idkey;
    private ArrayList<String> listIngredient;

    public User(String firstname, String lastname, String id, String pass,
                String phone, String email, ArrayList<String> listIngredient, String bitmap) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.id = id;
        this.pass = pass;
        this.phone = phone;
        this.email = email;
        this.listIngredient = listIngredient;
        this.bitmap = bitmap;
    }

    public User() {
    }

    public String getBitmap() {
        return bitmap;
    }

    public void setBitmap(String bitmap) {
        this.bitmap = bitmap;
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

    public void setEmail(String email) { this.email = email; }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getIdkey() {
        return this.idkey;
    }

    public void setIdkey(String idkey) {
        this.idkey = idkey;
    }

    @Override
    public String toString() {
        return
                "Email: " + email + "\n" + "\n" +
                        "First name: " + firstname + "\n" + "\n" +
                        "Last name: " + lastname + "\n" + "\n" +
                        "id: " + id + "\n" + "\n" +
                        "Phone number: " + phone + "\n"
                ;
    }
}
