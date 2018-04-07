package com.suda.yzune.wakeupschedule.utils;

import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

/**
 * Created by yzune on 2018/3/28.
 */

public class JsonUtils {

    public static boolean isGoodJson(String json) {
        if (json.equals("")) {
            return false;
        }
        try {
            new JsonParser().parse(json);
            return true;
        } catch (JsonParseException e) {
            return false;
        }
    }
}
