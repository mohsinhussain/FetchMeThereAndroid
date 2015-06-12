package com.webmedia7.mohsinhussain.fetchmethere.Model;

/**
 * Created by mohsinhussain on 4/27/15.
 */
public class Contacts {
    String name;
    String mobileNumber;
    String userId;
    String profilePicture;
    boolean friend;
    String profileImageString = "";
    String sortFriend;

    public Contacts(){

    }

    public Contacts(String name, String mobileNumber, String userId, String profilePicture, boolean friend, String profileImageString){
        this.name = name;
        this.mobileNumber = mobileNumber;
        this.userId = userId;
        this.profilePicture = profilePicture;
        this.friend = friend;
        this.profileImageString = profileImageString;
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

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
