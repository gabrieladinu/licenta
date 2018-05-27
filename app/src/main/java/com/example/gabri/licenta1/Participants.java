package com.example.gabri.licenta1;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.HashMap;
import java.util.Map;


public class Participants {

    private String age;
    private String barCode;
    private String checkIn;
    private String checkOut;
    private String details;
    private String firstName;
    private String lastName;
    private String mail;
    private String numberChekIn;
    private String register;
    private String sex;
    private String phone ;
    private String qrCodegenerated ;


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public Participants() {}

    public Participants(String age, String barCode, String checkIn, String checkOut, String details, String firstName, String lastName, String mail, String numberChekIn, String register, String sex, String phone , String qrCodegenerated) {
        this.age = age;
        this.barCode = barCode;
        this.checkIn = checkIn;
        this.checkOut = checkOut;
        this.details = details;
        this.firstName = firstName;
        this.lastName = lastName;
        this.mail = mail;
        this.numberChekIn = numberChekIn;
        this.register = register;
        this.sex = sex;
        this.qrCodegenerated =qrCodegenerated ;

    }

    public String getqrCodegenerated() {
        return qrCodegenerated;
    }

    public void setqrCodegenerated(String qrCodegenerated) { this.qrCodegenerated = qrCodegenerated;  }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getBarCode() {
        return barCode;
    }

    public void setBarCode(String barCode) {
        this.barCode = barCode;
    }

    public String getCheckIn() {
        return checkIn;
    }

    public void setCheckIn(String checkIn) {
        this.checkIn = checkIn;
    }

    public String getCheckOut() {
        return checkOut;
    }

    public void setCheckOut(String checkOut) {
        this.checkOut = checkOut;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getNumberChekIn() {
        return numberChekIn;
    }

    public void setNumberChekIn(String numberChekIn) {
        this.numberChekIn = numberChekIn;
    }

    public String getRegister() {
        return register;
    }

    public void setRegister(String register) {
        this.register = register;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Participants{" +
                "age='" + age + '\'' +
                ", barCode='" + barCode + '\'' +
                ", checkIn='" + checkIn + '\'' +
                ", checkOut='" + checkOut + '\'' +
                ", details='" + details + '\'' +
                ", firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", mail='" + mail + '\'' +
                ", numberChekIn='" + numberChekIn + '\'' +
                ", register='" + register + '\'' +
                ", sex='" + sex + '\'' +
                ", phone='" + phone + '\'' +
                " , qrCodegenerated " + qrCodegenerated + '\'' +
                '}';
    }
}