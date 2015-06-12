package com.webmedia7.mohsinhussain.fetchmethere.Model;

/**
 * Created by mohsinhussain on 3/22/15.
 */
public class UserOptionModel {
    private String title;
    private String description;
    private int iconImage;

    public UserOptionModel(String title, String description, int iconImage){
        this.title = title;
        this.description = description;
        this.iconImage = iconImage;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public int getIconImage() {
        return iconImage;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIconImage(int iconImage) {
        this.iconImage = iconImage;
    }
}
