package io.charon.connect.util;

import com.google.gson.Gson;

/**
 *
 */
public class GsonUtil {

    public static final Gson GSON = new Gson();

    /**
     * @param object
     * @return
     */
    public static String gsonStr(Object object) {
        String gsonString = null;
        if (GSON != null) {
            gsonString = GSON.toJson(object);
        }
        return gsonString;
    }

    public static <T> T GsonToBean(String gsonString, Class<T> cls) {
        T t = null;
        if (GSON != null) {
            t = GSON.fromJson(gsonString, cls);
        }
        return t;
    }
}
