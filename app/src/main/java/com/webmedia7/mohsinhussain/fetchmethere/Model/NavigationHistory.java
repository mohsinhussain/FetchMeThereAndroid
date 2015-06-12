package com.webmedia7.mohsinhussain.fetchmethere.Model;

/**
 * Created by mohsinhussain on 5/24/15.
 */
public class NavigationHistory {

    String id;
    String action;
    String distanceCovered;
    String friendAddress;
    String friendId;
    String friendLang;
    String friendLat;
    String friendLocName;
    String friendName;
    String friendProfileImageString = "";
    String myAddress;
    String myDisplayName;
    String myLang;
    String myLat;
    String myUserId;
    String myProfileImageString = "";
    String navEndTime;
    String navStartTime;
    String status;
    String TotalDistance;
    String TotalDuration;
    String mode = "";

    public NavigationHistory(){

    }

    public NavigationHistory(String id, String action, String distanceCovered, String friendAddress, String friendId, String friendLang, String friendLat, String friendLocName,
                             String friendName, String myAddress, String myDisplayName, String myLang, String myLat, String myUserId, String navEndTime, String navStartTime,
                             String status, String TotalDistance, String TotalDuration, String friendProfileImageString, String myProfileImageString){
        this.id = id;
        this.action = action;
        this.distanceCovered = distanceCovered;
        this.friendAddress = friendAddress;
        this.friendId = friendId;
        this.friendLang = friendLang;
        this.friendLat = friendLat;
        this.friendLocName = friendLocName;
        this.friendName = friendName;
        this.myAddress = myAddress;
        this.myDisplayName = myDisplayName;
        this.myLang = myLang;
        this.myLat = myLat;
        this.myUserId = myUserId;
        this.navEndTime = navEndTime;
        this.navStartTime = navStartTime;
        this.status = status;
        this.TotalDistance = TotalDistance;
        this.TotalDuration = TotalDuration;
        this.myProfileImageString = myProfileImageString;
        this.friendProfileImageString = friendProfileImageString;

    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getFriendProfileImageString() {
        return friendProfileImageString;
    }

    public void setFriendProfileImageString(String friendProfileImageString) {
        this.friendProfileImageString = friendProfileImageString;
    }

    public String getMyProfileImageString() {
        return myProfileImageString;
    }

    public void setMyProfileImageString(String myProfileImageString) {
        this.myProfileImageString = myProfileImageString;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDistanceCovered() {
        return distanceCovered;
    }

    public void setDistanceCovered(String distanceCovered) {
        this.distanceCovered = distanceCovered;
    }

    public String getFriendAddress() {
        return friendAddress;
    }

    public void setFriendAddress(String friendAddress) {
        this.friendAddress = friendAddress;
    }

    public String getFriendId() {
        return friendId;
    }

    public void setFriendId(String friendId) {
        this.friendId = friendId;
    }

    public String getFriendLang() {
        return friendLang;
    }

    public void setFriendLang(String friendLang) {
        this.friendLang = friendLang;
    }

    public String getFriendLat() {
        return friendLat;
    }

    public void setFriendLat(String friendLat) {
        this.friendLat = friendLat;
    }

    public String getFriendLocName() {
        return friendLocName;
    }

    public void setFriendLocName(String friendLocName) {
        this.friendLocName = friendLocName;
    }

    public String getFriendName() {
        return friendName;
    }

    public void setFriendName(String friendName) {
        this.friendName = friendName;
    }

    public String getMyAddress() {
        return myAddress;
    }

    public void setMyAddress(String myAddress) {
        this.myAddress = myAddress;
    }

    public String getMyDisplayName() {
        return myDisplayName;
    }

    public void setMyDisplayName(String myDisplayName) {
        this.myDisplayName = myDisplayName;
    }

    public String getMyLang() {
        return myLang;
    }

    public void setMyLang(String myLang) {
        this.myLang = myLang;
    }

    public String getMyLat() {
        return myLat;
    }

    public void setMyLat(String myLat) {
        this.myLat = myLat;
    }

    public String getMyUserId() {
        return myUserId;
    }

    public void setMyUserId(String myUserId) {
        this.myUserId = myUserId;
    }

    public String getNavEndTime() {
        return navEndTime;
    }

    public void setNavEndTime(String navEndTime) {
        this.navEndTime = navEndTime;
    }

    public String getNavStartTime() {
        return navStartTime;
    }

    public void setNavStartTime(String navStartTime) {
        this.navStartTime = navStartTime;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTotalDistance() {
        return TotalDistance;
    }

    public void setTotalDistance(String totalDistance) {
        TotalDistance = totalDistance;
    }

    public String getTotalDuration() {
        return TotalDuration;
    }

    public void setTotalDuration(String totalDuration) {
        TotalDuration = totalDuration;
    }
}
