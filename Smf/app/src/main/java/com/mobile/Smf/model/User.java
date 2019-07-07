package com.mobile.Smf.model;

public class User {

    private int id;
    private String userName;
    private String password;
    private String email;
    private String country;
    private int countryID;
    private int birthYear;

    public User(int id,String userName,String password,String email,String country,int birthYear,int countryID ){
        this.id = id;
        this.userName = userName;
        this.password = password;
        this.email = email;
        this.country = country;
        this.birthYear = birthYear;
        this.countryID = countryID;
    }

    public String getBirthYearAsString(){
        return "" + birthYear;
    }

    public String getCountry() {
        return country;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public int getBirthYear() {
        return birthYear;
    }

    public String getEmail() {
        return email;
    }

    public int getCountryID() {return countryID;}

}
