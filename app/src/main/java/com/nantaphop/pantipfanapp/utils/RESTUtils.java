package com.nantaphop.pantipfanapp.utils;

import android.util.Log;
import com.nantaphop.pantipfanapp.BaseApplication;
import com.nantaphop.pantipfanapp.response.Forum;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Formatter;

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

    public static String escapeUnicode(String input) {

        long start = System.currentTimeMillis();

        StringBuilder b = new StringBuilder(input.length());
        Formatter f = new Formatter(b);
        for (char c : input.toCharArray()) {
            if (c < 128) {
                b.append(c);
            } else {
                f.format("\\u0e[0-9a-z][0-9a-z]", (int) c);
            }
        }

        long duration = (System.currentTimeMillis()-start);
        return b.toString();
    }

}
