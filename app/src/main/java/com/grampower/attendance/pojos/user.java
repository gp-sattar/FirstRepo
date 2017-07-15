package com.grampower.attendance.pojos;

/**
 * Created by samdroid on 26/5/17.
 */

public class user {

    String   mobile, email, name, password,gender,profileUrl;

    public user(String mobile, String email, String name, String password, String gender) {
        this.mobile = mobile;
        this.email = email;
        this.name = name;
        this.password = password;
        this.gender = gender;

        if(gender.equals("Male")){
            this.profileUrl="https://firebasestorage.googleapis.com/v0/b/attendacemodule.appspot.com/o/profilePics%2Fmale_icon.png?alt=media&token=5cd7e2cf-b82a-4631-a171-5d83aec8dfde";
        }else{
            this.profileUrl="https://firebasestorage.googleapis.com/v0/b/attendacemodule.appspot.com/o/profilePics%2Ffemale_icon.png?alt=media&token=f8bf30ff-b99f-4d47-8b48-f73a126fd65d";
        }
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
}
