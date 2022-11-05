package com.example.gymbuddy;

public class upload {
    private String myName;
    private String myImageURI;
    public upload() {
          // empty constructor needed
    }
    public upload(String N,String U) {
         myImageURI = U;
         myName = N;
    }

   public String getMyName(){
        return myName;
   }
    public void setMyName(String N){
         myName = N;
    }
    public String getMyURI(){
        return myImageURI;
    }
    public void setMyImageURI(String N){
        myImageURI = N;
    }

}
