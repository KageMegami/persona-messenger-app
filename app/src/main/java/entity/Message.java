package entity;

import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class Message {
    public String message;
    public String senderId;
    public LocalDateTime dateTime;
    public String formatedDateTime;

    public Message(String message, String senderId) {
      /*  LocalDateTime dateTime = LocalDateTime.ofEpochSecond(seconds, 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE,MMMM d,yyyy h:mm,a", Locale.ENGLISH);
        String formattedDate = dateTime.format(formatter);*/
        //System.out.println(formattedDate);
        this.message = message;
        this.senderId = senderId;
    }

    public Message(String message, String senderId, JSONObject date) throws JSONException {
        dateTime = LocalDateTime.ofEpochSecond(date.getInt("_seconds"), 0, ZoneOffset.UTC);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE,MMMM d,yyyy h:mm,a", Locale.ENGLISH);
        formatedDateTime = dateTime.format(formatter);
        this.message = message;
        this.senderId = senderId;
    }
}
