package com.webmedia7.mohsinhussain.fetchmethere.Model;

/**
 * Created by mohsinhussain on 4/18/15.
 */
public class NavDrawerItem {

    private String title;
    private int icon;
    private String profilePicUrl;
    private String displayName;
    private String chatCount = "0";
    private String navCount = "0";
    boolean isTitle = false;
//    private String count = "0";
//    // boolean to set visiblity of the counter
//    private boolean isCounterVisible = false;

    public NavDrawerItem(){}

    public NavDrawerItem(String title, int icon){
        this.title = title;
        this.icon = icon;
        this.isTitle = false;


    }

    public NavDrawerItem(Boolean isTitle, String profilePicUrl, String displayName, String chatCount, String navCount){
        this.isTitle = isTitle;
        this.profilePicUrl = profilePicUrl;
        this.displayName = displayName;
        this.chatCount = chatCount;
        this.navCount = navCount;
    }

    public void setProfilePicUrl(String profilePicUrl) {
        this.profilePicUrl = profilePicUrl;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void setChatCount(String chatCount) {
        this.chatCount = chatCount;
    }

    public void setNavCount(String navCount) {
        this.navCount = navCount;
    }

    public void setIsTitle(boolean isTitle) {
        this.isTitle = isTitle;
    }

    public String getProfilePicUrl() {
        return profilePicUrl;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getChatCount() {
        return chatCount;
    }

    public String getNavCount() {
        return navCount;
    }

    public boolean isTitle() {
        return isTitle;
    }

    public String getTitle(){
        return this.title;
    }

    public int getIcon(){
        return this.icon;
    }

//    public String getCount(){
//        return this.count;
//    }
//
//    public boolean getCounterVisibility(){
//        return this.isCounterVisible;
//    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setIcon(int icon){
        this.icon = icon;
    }

//    public void setCount(String count){
//        this.count = count;
//    }
//
//    public void setCounterVisibility(boolean isCounterVisible){
//        this.isCounterVisible = isCounterVisible;
//    }
}
