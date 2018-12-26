package com.example.hasib.foodserver.Model;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by HASIB on 12/16/2017.
 */

public class Sender {

    private String to;
    private HashMap<String,String> notification;

    public Sender(String token, Map<String, String> content) {

    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public HashMap<String, String> getNotification() {
        return notification;
    }

    public void setNotification(HashMap<String, String> notification) {
        this.notification = notification;
    }

    public Sender(String to, HashMap<String, String> notification) {
        this.to = to;
        this.notification = notification;
    }
/* private Notification notification;
    public Sender(String token, Notification notification) {
    }
*/

}
