package vip.vadiy.storageliaobei;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;

import java.util.Map;

/**
 * Created by AiXin on 2019-10-25.
 */
public class SharePreferenceConfig {
    private final static String TAG = "SharePreferenceConfig";

    //可写的SharedPreferences对象，这个可以使用在插件自己的apk里面使用
    private SharedPreferences sharedPreferences = null;

    private SharedPreferences getBindSharePreference() {
        if (sharedPreferences != null) {
            return sharedPreferences;
        }
        throw new IllegalStateException("not inited");
    }

    public SharePreferenceConfig(Context context, String configName) {
        sharedPreferences = context.getSharedPreferences(configName, Context.MODE_PRIVATE);
    }

    public String getString(String key) {
        return getBindSharePreference().getString(key, "");
    }

    public void putString(String key, String value) {
        getBindSharePreference().edit().putString(key, value).apply();
    }

    public String getShowAll() {
        Map<String, String> xmlMap = (Map<String, String>) getBindSharePreference().getAll();
        JSONObject jsonObj = new JSONObject();
        try {
            for (Map.Entry<String, String> m : xmlMap.entrySet()) {
                jsonObj.put(m.getKey(), m.getValue());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return jsonObj.toString();
    }

}
