package com.webmedia7.mohsinhussain.fetchmethere.Model;

/**
 * Created by mohsinhussain on 4/27/15.
 */
public class Contacts {
    String name;
    String mobileNumber;
    String userId;
    boolean friend;
    String profileImageString = "";
    String sortFriend;
    boolean hasApp = false;

    public Contacts(){

    }

    public Contacts(String name, String mobileNumber, String userId, boolean friend, String profileImageString, boolean hasApp){
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.userId = userId;
        this.friend = friend;
        this.profileImageString = profileImageString;
        this.hasApp = hasApp;
    }

    public boolean isHasApp() {
        return hasApp;
    }

    public void setHasApp(boolean hasApp) {
        this.hasApp = hasApp;
    }

    public String getProfileImageString() {
        return profileImageString;
    }

    public void setProfileImageString(String profileImageString) {
        this.profileImageString = profileImageString;
    }

    public String getSortFriend() {
        return sortFriend;
    }

    public void setSortFriend(String sortFriend) {
        this.sortFriend = sortFriend;
    }

    public boolean isFriend() {
        return friend;
    }

    public void setFriend(boolean friend) {
        this.friend = friend;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
