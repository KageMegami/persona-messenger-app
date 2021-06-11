package entity;

import org.json.JSONException;
import org.json.JSONObject;

public class User {
    public String id;
    public String name;
    public String photoUrl;

    public User(JSONObject user) throws JSONException {
        id = user.getString("id");
        name = user.getString("name");
        photoUrl = user.getString("photoUrl");
    }
}
