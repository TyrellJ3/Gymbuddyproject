package com.example.gymbuddy.Cards;


import android.preference.Preference;

import com.example.gymbuddy.Preferences;

public class cards {
    private String userId;
    private String name;
    private String profileImageUrl;
    private int age;
    private Preferences p;

    private String bio;
    private String goal;
    private Long interestA;
    private  Long interestB;
    private Long interestC;
    private Long skillLevel;

    public cards(){}

    public cards(String userId, String name, String profileImageUrl, int age, Preferences p,
                 String bio, String goal, Long interestA, Long interestB, Long interestC,
                 Long skillLevel) {
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.age = age;
        this.p = p;
        this.bio = bio;
        this.goal = goal;
        this.interestA = interestA;
        this.interestB = interestB;
        this.interestC = interestC;
        this.skillLevel = skillLevel;
    }

    public cards (String userId, String name, String profileImageUrl, int age, Preferences p){
        this.userId = userId;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.age = age;
        this.p = p;
    }

    public String getUserId(){
        return userId;
    }
    public void setUserID(String userID){
        this.userId = userId;
    }

    public String getName(){
        return name;
    }
    public void setName(String name){
        this.name = name;
    }

    public String getProfileImageUrl(){
        return profileImageUrl;
    }
    public void setProfileImageUrl(String profileImageUrl){
        this.profileImageUrl = profileImageUrl;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public Preferences getP() {
        return p;
    }

    public void setP(Preferences p) {
        this.p = p;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getGoal() {
        return goal;
    }

    public void setGoal(String goal) {
        this.goal = goal;
    }

    public Long getInterestA() {
        return interestA;
    }

    public void setInterestA(Long interestA) {
        this.interestA = interestA;
    }

    public Long getInterestB() {
        return interestB;
    }

    public void setInterestB(Long interestB) {
        this.interestB = interestB;
    }

    public Long getInterestC() {
        return interestC;
    }

    public void setInterestC(Long interestC) {
        this.interestC = interestC;
    }

    public Long getSkillLevel() {
        return skillLevel;
    }

    public void setSkillLevel(Long skillLeverl) {
        this.skillLevel = skillLeverl;
    }
}
