package com.webmedia7.mohsinhussain.fetchmethere.Model;

/**
 * Created by mohsinhussain on 6/9/15.
 */
public class Chat {
    String message;
    boolean fromFriend;

    public Chat(){
        message = "";
        fromFriend = false;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isFromFriend() {
        return fromFriend;
    }

    public void setFromFriend(boolean fromFriend) {
        this.fromFriend = fromFriend;
    }
}
