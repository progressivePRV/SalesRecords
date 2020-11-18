package com.example.salesrecordapp;

import java.io.Serializable;

public class User implements Serializable {
    String firstName, lastName, email, _id, gender;

    @Override
    public String toString() {
        return "User{" +
                "firstName='" + firstName + '\'' +
                ", lastName='" + lastName + '\'' +
                ", email='" + email + '\'' +
                ", _id='" + _id + '\'' +
                ", gender='" + gender + '\'' +
//                ", token='" + token + '\'' +
                '}';
    }
}
