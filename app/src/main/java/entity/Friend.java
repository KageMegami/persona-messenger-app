package entity;

import org.json.JSONException;
import org.json.JSONObject;

public class Friend {
    public String id;
    public String name;
    public String photoUrl;

    public Friend(JSONObject friend) throws JSONException {
        id = friend.getString("id");
        name = friend.getString("name");
        photoUrl = friend.getString("photoUrl");
    }
}
