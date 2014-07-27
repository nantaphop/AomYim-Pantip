package com.nantaphop.pantipfanapp.utils;

import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.response.Forum;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by nantaphop on 27-Jul-14.
 */
public class RESTUtils {

    public static Forum parseForum(String resp){
        try {
            return BaseApplication.getGson().fromJson(new JSONObject(resp).get("item").toString(), Forum.class);
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
    }
}
