package ir.alimojahed.general.elasticwrapper.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.json.JSONArray;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created By F.Khojasteh on 4/24/2020
 */

public class GsonUtil {
    static Gson gson;

    static {
        GsonBuilder b = new GsonBuilder();
        b.registerTypeAdapterFactory(HibernateProxyTypeAdapter.FACTORY);
        gson = b.create();
    }


    public static String getJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T fromJson(String json, Class<T> classOfT) {
        return gson.fromJson(json, classOfT);
    }

    public static <T> List<T> fromJson(String json) {
        Type listType = new TypeToken<List<T>>() {
        }.getType();

        return gson.fromJson(json, listType);
    }

    public static <T> List<T> fromJson(JsonElement object) {
        return gson.fromJson(object, new TypeToken<ArrayList<T>>() {
        }.getType());
    }

    public static <T> Object getObject(String json, Class<T> tClass) {
        Object jsonResponse = new JSONTokener(json).nextValue();

        if (jsonResponse instanceof JSONArray) {
            return fromJson(json);
        } else {
            return fromJson(json, tClass);
        }
    }

}
