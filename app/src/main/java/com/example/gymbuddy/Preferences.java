package com.example.gymbuddy;

import com.google.android.gms.maps.model.LatLng;

import java.util.Arrays;

public class Preferences {
    private LatLng address;
    private int distance;
    private int[] age = new int[2];
    private String sex;

    public Preferences() {
    }

    public Preferences(LatLng address, int distance, int[] age, String sex) {
        this.address = address;
        this.distance = distance;
        this.age = age;
        this.sex = sex;
    }

    public LatLng getAddress() {
        return address;
    }

    public void setAddress(LatLng address) {
        this.address = address;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int[] getAge() {
        return age;
    }

    //make sure array is exactly length of 2
    public void setAge(int[] age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    @Override
    public String toString() {
        return "Preferences{" +
                "address=" + address.toString() +
                ", distance=" + distance +
                ", age=" + Arrays.toString(age) +
                ", sex='" + sex + '\'' +
                '}';
    }
}
