package vip.vadiy.storageliaobei;

import android.content.Context;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import net.sqlcipher.Cursor;
import net.sqlcipher.database.SQLiteDatabase;
import net.sqlcipher.database.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vip.vadiy.storageliaobei.Utils.DBUtil;

/**
 * Created by AiXin on 2019-10-23.
 */
public class StorageDBHelper extends SQLiteOpenHelper {

    private final static String TAG = StorageDBHelper.class.getSimpleName();
    private SQLiteDatabase db;
    private String DB_PSW;

    public StorageDBHelper(Context context, String name, String psw) {
        super(context, name, null, 10);
        SQLiteDatabase.loadLibs(context);
        DB_PSW = psw;
        getDb();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    protected synchronized SQLiteDatabase getDb() {
        if (db == null) {
            db = getWritableDatabase(DB_PSW);
        }
        return db;
    }

    public boolean execSql(String sql) {

        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = getDb();
            if (sQLiteDatabase == null) {
                return false;
            }

            sQLiteDatabase.execSQL(sql);
            return true;
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("execSql:");
            stringBuilder.append(sql);
            stringBuilder.append("\nError:");
            stringBuilder.append(e.getMessage());
            DBUtil.log(TAG, stringBuilder.toString());

        }
        return false;
    }

    //SELECT `value` FROM `liaobei_user_registry_757403101` WHERE `key`=70
    public List<String> getValue(String str) {
        ArrayList arrayList = new ArrayList();
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = getDb();
            if (sQLiteDatabase == null) {
                return arrayList;
            }

            Cursor rawQuery = sQLiteDatabase.rawQuery(str, new String[0]);
            String[] columnNames = rawQuery.getColumnNames();
            while (rawQuery.moveToNext()) {
                if (columnNames.length > 0) {
                    arrayList.add(rawQuery.getString(rawQuery.getColumnIndex(columnNames[0])));
                }
            }
            rawQuery.close();
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("execSql:");
            stringBuilder.append(str);
            stringBuilder.append("\nError:");
            stringBuilder.append(e.getMessage());
            DBUtil.log(TAG, stringBuilder.toString());
            arrayList.add(stringBuilder.toString());
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] ses = e.getStackTrace();
            for (StackTraceElement se : ses) {
                sb.append(se.toString()).append("\n");
            }
            arrayList.add(sb.toString());
            DBUtil.log(TAG, sb.toString());

        }
        return arrayList;
    }

    /**
     * @param sql 查询语句
     * @return list 行数据  map 列数据
     */
    public List<Map<String, String>> rawQuery(String sql) {
        ArrayList arrayList = new ArrayList();
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = getDb();
            if (sQLiteDatabase == null) {
                return arrayList;
            }
            Cursor rawQuery = sQLiteDatabase.rawQuery(sql, new String[0]);
            String[] columnNames = rawQuery.getColumnNames();
            while (rawQuery.moveToNext()) {
                HashMap hashMap = new HashMap();
                for (String str2 : columnNames) {
                    hashMap.put(str2, rawQuery.getString(rawQuery.getColumnIndex(str2)));
                }
                arrayList.add(hashMap);
            }
            rawQuery.close();
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("execSql:");
            stringBuilder.append(sql);
            stringBuilder.append("\nError:");
            stringBuilder.append(e.getMessage());
            DBUtil.log(TAG, stringBuilder.toString());

        }
        return arrayList;
    }

    public String rawQueryJson(String sql) {
        String json = "";
        JSONArray root = new JSONArray();
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = getDb();
            if (sQLiteDatabase == null) {
                return "";
            }
            Cursor rawQuery = sQLiteDatabase.rawQuery(sql, new String[0]);
            String[] columnNames = rawQuery.getColumnNames();//得到列名数量

            while (rawQuery.moveToNext()) {
                if (columnNames.length > 0) {
                    JSONObject jo = new JSONObject();
                    for (String col : columnNames) {//遍历每列的值
                        jo.put(col, rawQuery.getString(rawQuery.getColumnIndex(col)));
                    }
                    root.add(jo);
                    //arrayList.add(rawQuery.getString(rawQuery.getColumnIndex(columnNames[0])));
                }
            }
            rawQuery.close();
            json = root.toString();
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("execSql:");
            stringBuilder.append(sql);
            stringBuilder.append("\nError:");
            stringBuilder.append(e.getMessage());
            DBUtil.log(TAG, stringBuilder.toString());
            json = stringBuilder.toString();
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] ses = e.getStackTrace();
            for (StackTraceElement se : ses) {
                sb.append(se.toString()).append("\n");
            }
            json += (sb.toString());
            DBUtil.log(TAG, sb.toString());

        }
        return json;
    }

    //{"cols":"a,b,c,d...","key":"value"}
    public String rawQueryJson2(String sql) {
        String json = "";
        String cols = "";
        JSONObject root = new JSONObject();
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = getDb();
            if (sQLiteDatabase == null) {
                return "";
            }
            Cursor rawQuery = sQLiteDatabase.rawQuery(sql, new String[0]);
            String[] columnNames = rawQuery.getColumnNames();//得到列名数量
            for (int i = 0; i < columnNames.length; i++) {
                if (i != columnNames.length - 1) {
                    cols += columnNames[i] + ",";
                } else {
                    cols += columnNames[i];
                }
            }
            root.put("cols", cols);
            while (rawQuery.moveToNext()) {
                if (columnNames.length > 0) {
                    JSONObject jo = new JSONObject();
                    String key = "";
                    String value = "";
                    for (int i = 0; i < columnNames.length; i++) {
                        if (i == 0) {
                            key = rawQuery.getString(i);
                        } else {
                            if (i != columnNames.length - 1) {
                                value += rawQuery.getString(i) + ",";
                            } else {
                                value += rawQuery.getString(i);
                            }
                        }
                    }
                    if ("".equals(key) == false) {
                        root.put(key, value);
                    }
                }
            }
            rawQuery.close();
            json = root.toString();
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("execSql:");
            stringBuilder.append(sql);
            stringBuilder.append("\nError:");
            stringBuilder.append(e.getMessage());
            DBUtil.log(TAG, stringBuilder.toString());
            json = stringBuilder.toString();
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] ses = e.getStackTrace();
            for (StackTraceElement se : ses) {
                sb.append(se.toString()).append("\n");
            }
            json += (sb.toString());
            DBUtil.log(TAG, sb.toString());

        }
        return json;
    }


    //SELECT `value` FROM `liaobei_user_registry_757403101` WHERE `key`=70
    public String getValue(String table, String key) {
        String value = "";
        String sql = "SELECT value FROM " + table + " WHERE key ='" + key + "'";
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = getDb();
            if (sQLiteDatabase == null) {
                return value;
            }
            Cursor rawQuery = sQLiteDatabase.rawQuery(sql, new String[0]);
            String[] columnNames = rawQuery.getColumnNames();
            while (rawQuery.moveToNext()) {
                if (columnNames.length > 0) {
                    value = (rawQuery.getString(rawQuery.getColumnIndex(columnNames[0])));
                    break;
                }
            }
            rawQuery.close();
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("execSql:");
            stringBuilder.append(sql);
            stringBuilder.append("\nError:");
            stringBuilder.append(e.getMessage());
            DBUtil.log(TAG, stringBuilder.toString());
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] ses = e.getStackTrace();
            for (StackTraceElement se : ses) {
                sb.append(se.toString()).append("\n");
            }
            DBUtil.log(TAG, sb.toString());

        }
        return value;
    }

    public boolean insert(String table, String key, String value) {
        boolean result = false;
        String sql = "INSERT OR REPLACE INTO " + table + "(`key`,`value`) VALUES(" + key + ",'" + value + "')";
        //DBUtil.log(TAG,sql);
        SQLiteDatabase sQLiteDatabase = null;
        try {
            sQLiteDatabase = getDb();
            if (sQLiteDatabase == null) {
                return result;
            }
            sQLiteDatabase.execSQL(sql);
            result = true;
        } catch (Exception e) {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("execSql:");
            stringBuilder.append(sql);
            stringBuilder.append("\nError:");
            stringBuilder.append(e.getMessage());
            DBUtil.log(TAG, stringBuilder.toString());
            StringBuilder sb = new StringBuilder();
            StackTraceElement[] ses = e.getStackTrace();
            for (StackTraceElement se : ses) {
                sb.append(se.toString()).append("\n");
            }
            DBUtil.log(TAG, sb.toString());

        }
        return result;
    }

}
