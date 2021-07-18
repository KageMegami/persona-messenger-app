package entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Locale;

public class Message {
    public String message;
    public String senderId;
    public Date date;

    public Message(String message, String senderId, Long date) {
        this.message = message;
        this.senderId = senderId;
        this.date = new Date(date);
    }

    public Message(String message, String senderId, JSONObject date) throws JSONException {
        this.date = new Date(date.getInt("_seconds") * 1000L);
        this.message = message;
        this.senderId = senderId;
    }
}
