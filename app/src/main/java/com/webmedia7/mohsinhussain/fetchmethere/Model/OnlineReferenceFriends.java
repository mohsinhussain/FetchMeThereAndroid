package com.webmedia7.mohsinhussain.fetchmethere.Model;

/**
 * Created by mohsinhussain on 4/27/15.
 */
public class OnlineReferenceFriends {

    String mobile;
    String userId;
    String name;
    String profileImageString;

    public OnlineReferenceFriends(){}

    public OnlineReferenceFriends(String mobile, String userId, String name, String profileImageString){
        this.mobile = mobile;
        this.userId = userId;
        this.name = name;
        this.profileImageString = profileImageString;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileImageString() {
        return profileImageString;
    }

    public void setProfileImageString(String profileImageString) {
        this.profileImageString = profileImageString;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
