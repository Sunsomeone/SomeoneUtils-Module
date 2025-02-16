package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/11/16 22:42
 */

import androidx.annotation.NonNull;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Iterator;

public class JSONUtils {

    public static JSONData createJSONData() {
        return new JSONData(new JSONObject());
    }

    @NonNull
    public static String[] getKeyArray(JSONObject jsonObject) {
        String[] keyArray = new String[jsonObject.length()];
        Iterator<String> keys = jsonObject.keys();
        int index = 0;
        while (keys.hasNext()) {
            keyArray[index] = keys.next();
            index++;
        }
        return keyArray;
    }

    public static JSONData createJSONData(JSONObject jsonObject) {
        return new JSONData(jsonObject);
    }

    public static class JSONData {
        private final JSONObject rootObject;

        public JSONData(JSONObject jsonObject) {
            rootObject = jsonObject;
        }

        public JSONData() {
            rootObject = new JSONObject();
        }

        public JSONObject getJsonObject() {
            return rootObject;
        }

        public JSONData put(String key, Object value) throws JSONException {
            rootObject.put(key, value);
            if (value instanceof JSONObject) {
                return new JSONData((JSONObject) value);
            }
            return this;
        }

        public Object get(String key) throws JSONException {
            Object value = rootObject.get(key);
            if (value instanceof JSONObject) {
                new JSONData((JSONObject) value);
            }
            return value;
        }

        @NonNull
        @Override
        public String toString() {
            return rootObject.toString();
        }

        public String toStringFormatted() throws JSONException {
            return rootObject.toString(4);
        }
    }

}
