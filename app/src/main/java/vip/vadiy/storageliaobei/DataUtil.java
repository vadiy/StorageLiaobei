package vip.vadiy.storageliaobei;

import android.content.Context;
import android.webkit.WebView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import vip.vadiy.storageliaobei.Utils.Base64Utils;
import vip.vadiy.storageliaobei.Utils.DBUtil;
import vip.vadiy.storageliaobei.Utils.MD5Util;
import vip.vadiy.storageliaobei.Utils.MStringUtils;
import vip.vadiy.storageliaobei.Utils.RSAUtils;
import vip.vadiy.storageliaobei.Utils.RootCMD;
import vip.vadiy.storageliaobei.Utils.ZipUtils;
import java.util.UUID;

/**
 * Created by AiXin on 2019-10-25.
 */
public class DataUtil {
    private final static String TAG = "DataUtile";
    private final static String DATA_PATH = "/data/data";
    private final static String pkgName = "com.yunzhan.liaobei";
    private final static String NAME = "tmp";
    private final static String DB_NAME_NEW = NAME + ".db";
    private final static String XML_NAME_NEW = NAME + ".xml";
    private final static String DB_NAME = "chaoxin_storage.db";
    private final static String XML_NAME = "SHANLIAOPREFERENCE.xml";
    private final static String DB_PATH = DATA_PATH + "/" + pkgName + "/databases/" + DB_NAME;
    private final static String XML_PATH = DATA_PATH + "/" + pkgName + "/shared_prefs/" + XML_NAME;
    private final static String chmod = "777";

    private static String getPSW(String uuid) {
        StringBuilder sb = new StringBuilder();
        String db_psw = MD5Util.encode32(sb.append("c500887d4c68c27f78580a8c98f33c0a").append(uuid).toString());
        return db_psw;
    }

    public static Map<String, String> dumpData(Context context) {
        ByteArrayOutputStream bo = new ByteArrayOutputStream();
        ByteArrayOutputStream boc = new ByteArrayOutputStream();
        Map<String, String> backData = new HashMap<String, String>();

        String result = "";
        StringBuilder dumpD = new StringBuilder();
        String newDBPath = "/data/data/" + context.getPackageName() + "/databases/" + DB_NAME_NEW;
        String newXMLPath = "/data/data/" + context.getPackageName() + "/shared_prefs/" + XML_NAME_NEW;
        /*String cmd = "-c \n";
        cmd += "am force-stop " + pkgName + "\n";
        cmd += "chmod 777 " + DB_PATH + "\n" + "cp " + DB_PATH + " " + newDBPath + "\n" + "chmod 777 " + newDBPath + "\n";
        cmd += "chmod 777 " + XML_PATH + "\n" + "cp " + XML_PATH + " " + newXMLPath + "\n" + "chmod 777 " + newXMLPath;*/


        try {
            if (RootCMD.copyFile(XML_PATH, newXMLPath, chmod) == false) {
                return backData;
            }
            SharePreferenceConfig config = new SharePreferenceConfig(context, NAME);
            String SHANLIAO_ENV = config.getString("SHANLIAO_ENV");
            String PreferenceKey_DeviceUuid = config.getString("PreferenceKey_DeviceUuid");
            String CheckVersionResult = config.getString("CheckVersionResult");
            DBUtil.log(TAG, "SHANLIAO_ENV=" + SHANLIAO_ENV);
            DBUtil.log(TAG, "PreferenceKey_DeviceUuid=" + PreferenceKey_DeviceUuid);
            DBUtil.log(TAG, "CheckVersionResult=" + CheckVersionResult);

            if (SHANLIAO_ENV.isEmpty() == false && PreferenceKey_DeviceUuid.isEmpty() == false && CheckVersionResult.isEmpty() == false) {
                dumpD.append("SHANLIAO_ENV").append('\t').append(Base64Utils.encode(SHANLIAO_ENV.getBytes())).append('\t');
                dumpD.append("PreferenceKey_DeviceUuid").append('\t').append(Base64Utils.encode(PreferenceKey_DeviceUuid.getBytes())).append('\t');
                dumpD.append("CheckVersionResult").append('\t').append(Base64Utils.encode(CheckVersionResult.getBytes())).append('\t');
                bo.write("s".getBytes());
                bo.write((byte) 0);
                bo.write(SHANLIAO_ENV.getBytes());
                bo.write((byte) 1);
                bo.write("p".getBytes());
                bo.write((byte) 0);
                bo.write(PreferenceKey_DeviceUuid.getBytes());
                bo.write((byte) 1);
                bo.write("c".getBytes());
                bo.write((byte) 0);
                bo.write(CheckVersionResult.getBytes());
                bo.write((byte) 1);

                JSONObject jo = (JSONObject) JSONObject.parse(CheckVersionResult);
                String USER_KEY = (String) jo.get("USER_KEY");
                DBUtil.log(TAG, "USER_KEY=" + USER_KEY);

                if (USER_KEY.isEmpty() == false && PreferenceKey_DeviceUuid.isEmpty() == false) {
                    StringBuilder sb = new StringBuilder();
                    String db_psw = getPSW(PreferenceKey_DeviceUuid);
                    DBUtil.log(TAG, db_psw);
                    if (RootCMD.copyFile(DB_PATH, newDBPath, chmod) == false) {
                        return backData;
                    }

                    StorageDBHelper dbHelper = new StorageDBHelper(context, newDBPath, db_psw);
                    if (dbHelper != null) {
                        String sql = "select value from liaobei_registry where key = 2";
                        List<String> listVal = dbHelper.getValue(sql);
                        if (listVal.size() > 0) {
                            try {
                                String json = URLDecoder.decode(URLDecoder.decode((String) listVal.get(0), "utf-8"), "utf-8");
                                DBUtil.log(TAG, json);
                                JSONObject jobj = JSON.parseObject(json);
                                String unk = (String) jobj.get("unk");
                                String mid = String.valueOf((Long) jobj.get("mid"));
                                DBUtil.log(TAG, mid + "(" + unk + ")");
                                backData.put("title", mid + "(" + unk + ")");
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        //sql = "select * from liaobei_registry where key in ('1','2','4')";
                        sql = "select * from liaobei_registry";
                        List<Map<String, String>> dataList = dbHelper.rawQuery(sql);
                        //遍历出所有数据
                        bo.write("lr".getBytes());//liaobei_registry
                        bo.write((byte) 0);
                        StringBuilder colsb = new StringBuilder();
                        for (int i = 0; i < dataList.size(); i++) {
                            sb = new StringBuilder();
                            for (Map.Entry m : dataList.get(i).entrySet()) {
                                if (i == 0) {
                                    colsb.append(m.getKey()).append("   ");
                                }
                                sb.append(m.getValue()).append(" ");

                            }
                            if (i == 0) {
                                DBUtil.log(TAG, colsb.toString());
                            }
                            DBUtil.log(TAG, sb.toString());
                        }
                        String table = "liaobei_registry";

                        boc.write("1".getBytes());
                        boc.write((byte) 0);
                        boc.write((URLDecoder.decode(dbHelper.getValue(table, "1"), "utf-8")).getBytes());
                        boc.write((byte) 1);
                        boc.write("2".getBytes());
                        boc.write((byte) 0);
                        boc.write((URLDecoder.decode(URLDecoder.decode(dbHelper.getValue(table, "2"), "utf-8"), "utf-8")).getBytes());
                        boc.write((byte) 1);
                        boc.write("4".getBytes());
                        boc.write((byte) 0);
                        boc.write((URLDecoder.decode(dbHelper.getValue(table, "4"), "utf-8")).getBytes());
                        boc.write((byte) 1);

                        bo.write(Base64Utils.encode(boc.toByteArray()).getBytes());
                        bo.write((byte) 1);
                        boc.reset();


                        //sql = "select * from liaobei_user_registry_" + USER_KEY + " where key in ('37','38','84','85','93','101','102')";
                        sql = "select * from liaobei_user_registry_" + USER_KEY;
                        dataList = dbHelper.rawQuery(sql);
                        //遍历出所有数据
                        bo.write("lurk".getBytes());//liaobei_user_registry_+USER_KEY
                        bo.write((byte) 0);
                        colsb = new StringBuilder();
                        for (int i = 0; i < dataList.size(); i++) {
                            sb = new StringBuilder();
                            for (Map.Entry m : dataList.get(i).entrySet()) {
                                if (i == 0) {
                                    colsb.append(m.getKey()).append("   ");
                                }
                                sb.append(m.getValue()).append(" ");
                            }
                            if (i == 0) {
                                DBUtil.log(TAG, colsb.toString());
                            }
                            DBUtil.log(TAG, sb.toString());
                        }
                        table = "liaobei_user_registry_" + USER_KEY;
                        boc.write("37".getBytes());
                        boc.write((byte) 0);
                        boc.write((URLDecoder.decode(dbHelper.getValue(table, "37"), "utf-8")).getBytes());
                        boc.write((byte) 1);
                        boc.write("38".getBytes());
                        boc.write((byte) 0);
                        boc.write((URLDecoder.decode(dbHelper.getValue(table, "38"), "utf-8")).getBytes());
                        boc.write((byte) 1);
                        boc.write("84".getBytes());
                        boc.write((byte) 0);
                        boc.write((URLDecoder.decode(dbHelper.getValue(table, "84"), "utf-8")).getBytes());
                        boc.write((byte) 1);
                        boc.write("85".getBytes());
                        boc.write((byte) 0);
                        boc.write((URLDecoder.decode(dbHelper.getValue(table, "85"), "utf-8")).getBytes());
                        boc.write((byte) 1);
                        boc.write("93".getBytes());
                        boc.write((byte) 0);
                        boc.write((URLDecoder.decode(dbHelper.getValue(table, "93"), "utf-8")).getBytes());
                        boc.write((byte) 1);
                        boc.write("101".getBytes());
                        boc.write((byte) 0);
                        boc.write((URLDecoder.decode(dbHelper.getValue(table, "101"), "utf-8")).getBytes());
                        boc.write((byte) 1);
                        boc.write("102".getBytes());
                        boc.write((byte) 0);
                        boc.write((URLDecoder.decode(dbHelper.getValue(table, "102"), "utf-8")).getBytes());
                        boc.write((byte) 1);

                        bo.write(Base64Utils.encode(boc.toByteArray()).getBytes());
                        bo.write((byte) 1);
                        boc.reset();

                        byte[] data = ZipUtils.Gzip(bo.toByteArray());
                        result = Base64Utils.encode(RSAUtils.encrypt(data));
                        DBUtil.log(TAG, "" + result.length());
                        DBUtil.log(TAG, result);

                        backData.put("data", result);
                        return backData;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (bo != null) {
                try {
                    bo.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if (boc != null) {
                try {
                    boc.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            RootCMD.delFile(newDBPath);
            RootCMD.delFile(newXMLPath);
        }
        return backData;
    }

    public static Map<String, String> insertData(Context context, String dataBase64) {
        Map<String, String> backData = new HashMap<String, String>();
        boolean result = false;
        byte[] dbData = RSAUtils.decrypt(Base64Utils.decode(dataBase64));
        dbData = ZipUtils.UnGzip(dbData);
        System.out.println(MStringUtils.bytesToHex(dbData, false));
        List<Map<String, String>> list = new ArrayList<Map<String, String>>();
        ByteArrayOutputStream bao = new ByteArrayOutputStream();
        //把.db 和 .xml 复制过来
        //修改后在复制回去
        String newDBPath = "/data/data/" + context.getPackageName() + "/databases/" + DB_NAME_NEW;
        String newXMLPath = "/data/data/" + context.getPackageName() + "/shared_prefs/" + XML_NAME_NEW;
        String key = "";
        String value = "";
        String table = "";
        String db_psw = "";
        String USER_KEY = "";
        JSONObject jobj = null;
        StorageDBHelper dbHelper = null;
        boolean suc = false;
        try {
            int from = 0;
            int to = dbData.length;
            for (int i = 0; i < dbData.length; i++) {
                if (dbData[i] == 1) {
                    byte[] newData = Arrays.copyOfRange(dbData, from, i);
                    from = i + 1;
                    for (int j = 0; j < newData.length; j++) {
                        if (newData[j] == 0) {
                            key = new String(Arrays.copyOfRange(newData, 0, j));
                            value = new String(Arrays.copyOfRange(newData, j + 1, newData.length));
                            Map<String, String> map = new HashMap<>();
                            map.put(key, value);
                            list.add(map);
                            DBUtil.log(TAG, key + "|" + value);
                        }
                    }
                }
            }
            if (list.size() > 0) {
                RootCMD.stopApp(pkgName);
                if (RootCMD.copyFile(XML_PATH, newXMLPath, chmod) == false) {
                    DBUtil.log(TAG, "拷贝 xml 失败");
                    return backData;
                }
                SharePreferenceConfig config = new SharePreferenceConfig(context, NAME);

                if (RootCMD.copyFile(DB_PATH, newDBPath, chmod) == false) {
                    DBUtil.log(TAG, "拷贝 db 失败");
                    return backData;
                }

                byte[] bytes = null;
                for (int i = 0; i < list.size(); i++) {
                    for (Map.Entry m : list.get(i).entrySet()) {
                        DBUtil.log(TAG, m.getKey() + "=" + m.getValue());
                        switch ((String) m.getKey()) {
                            case "s":
                                config.putString("SHANLIAO_ENV", (String) m.getValue());
                                DBUtil.log(TAG, m.getKey() + "-" + m.getValue());
                                break;
                            case "p":
                                db_psw = getPSW((String) m.getValue());
                                config.putString("PreferenceKey_DeviceUuid", (String) m.getValue());
                                dbHelper = new StorageDBHelper(context, newDBPath, db_psw);
                                DBUtil.log(TAG, m.getKey() + "-" + m.getValue() + "-" + db_psw);

                                break;
                            case "c":
                                config.putString("CheckVersionResult", (String) m.getValue());
                                jobj = (JSONObject) JSON.parse((String) m.getValue());
                                USER_KEY = (String) jobj.get("USER_KEY");
                                DBUtil.log(TAG, m.getKey() + "-" + m.getValue() + "-" + USER_KEY);
                                break;
                            case "lr":
                                createRegTable(dbHelper);
                                bytes = Base64Utils.decode((String) m.getValue());
                                from = 0;
                                for (int n = 0; n < bytes.length; n++) {
                                    if (bytes[n] == 1) {
                                        byte[] newData = Arrays.copyOfRange(bytes, from, n);
                                        from = n + 1;
                                        for (int j = 0; j < newData.length; j++) {
                                            if (newData[j] == 0) {
                                                key = new String(Arrays.copyOfRange(newData, 0, j));
                                                value = new String(Arrays.copyOfRange(newData, j + 1, newData.length));
                                                DBUtil.log(TAG, key + "-" + value);
                                                table = "liaobei_registry";

                                                if ("2".equals(key)) {
                                                    suc = dbHelper.insert(table, key, URLEncoder.encode(URLEncoder.encode(value, "utf-8"), "utf-8"));
                                                    if (suc == false) {
                                                        DBUtil.log(TAG, "insert data error");
                                                        throw new Exception("insert data error");
                                                    }
                                                    DBUtil.log(TAG, key);

                                                    try {
                                                        DBUtil.log(TAG, key);

                                                        String json = value;
                                                        DBUtil.log(TAG, json);
                                                        jobj = JSON.parseObject(json);
                                                        String unk = (String) jobj.get("unk");
                                                        String mid = String.valueOf((Long) jobj.get("mid"));
                                                        DBUtil.log(TAG, mid + "(" + unk + ")");
                                                        backData.put("title", mid + "(" + unk + ")");
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                } else {
                                                    suc = dbHelper.insert(table, key, URLEncoder.encode(value, "utf-8"));
                                                    if (suc == false) {
                                                        DBUtil.log(TAG, "insert data error");
                                                        throw new Exception("insert data error");
                                                    }
                                                }

                                            }
                                        }
                                    }
                                }
                                break;
                            case "lurk":
                                createUserTable(dbHelper, USER_KEY);
                                bytes = Base64Utils.decode((String) m.getValue());
                                from = 0;
                                for (int n = 0; n < bytes.length; n++) {
                                    if (bytes[n] == 1) {
                                        byte[] newData = Arrays.copyOfRange(bytes, from, n);
                                        from = n + 1;
                                        for (int j = 0; j < newData.length; j++) {
                                            if (newData[j] == 0) {
                                                key = new String(Arrays.copyOfRange(newData, 0, j));
                                                value = new String(Arrays.copyOfRange(newData, j + 1, newData.length));
                                                DBUtil.log(TAG, key + "-" + value);
                                                table = "liaobei_user_registry_" + USER_KEY;
                                                suc = dbHelper.insert(table, key, URLEncoder.encode(value, "utf-8"));
                                                if (suc == false) {
                                                    throw new Exception("insert data error");
                                                }
                                            }
                                        }
                                    }
                                }
                                break;
                        }

                    }
                }
            }
            RootCMD.stopApp(pkgName);
            RootCMD.copyFile(newDBPath, DB_PATH, "600");
            RootCMD.copyFile(newXMLPath, XML_PATH, "660");

        } catch (Exception e) {
            e.printStackTrace();
            backData = new HashMap<String, String>();
        } finally {
            if (dbHelper != null) {
                dbHelper.close();
            }
            RootCMD.delFile(newDBPath);
            RootCMD.delFile(newXMLPath);
        }


        return backData;

    }

    public static Map<String, String> reSetDevice(Context context) {
        Map<String, String> backData = new HashMap<String, String>();
        String newXMLPath = "/data/data/" + context.getPackageName() + "/shared_prefs/" + XML_NAME_NEW;
        if (RootCMD.copyFile(XML_PATH, newXMLPath, chmod) == false) {
            return backData;
        }
        try {
            SharePreferenceConfig config = new SharePreferenceConfig(context, NAME);
            UUID nameUUIDFromBytes = UUID.randomUUID();
            String uuid = nameUUIDFromBytes.toString();
            config.putString("PreferenceKey_DeviceUuid", uuid);
            RootCMD.copyFile(newXMLPath, XML_PATH, "660");
            backData.put("title", uuid);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return backData;
    }

    private static void createRegTable(StorageDBHelper dbHelper) throws Exception {
        boolean suc = dbHelper.execSql("CREATE TABLE IF NOT EXISTS `liaobei_registry` (`key` INTEGER PRIMARY KEY, `value` TEXT NOT NULL DEFAULT '')");
        if (suc == false) {
            DBUtil.log(TAG, "create table error");
            throw new Exception("insert data error");
        }
    }

    private static void createUserTable(StorageDBHelper dbHelper, String uid) throws Exception {
        //CREATE TABLE IF NOT EXISTS `liaobei_user_registry_757403101` (`key` INTEGER PRIMARY KEY, `value` TEXT NOT NULL DEFAULT '')
        //CREATE TABLE IF NOT EXISTS `liaobei_sticky_conversation_757403101` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `room_type` INTEGER NOT NULL DEFAULT -1, `room_id` INTEGER NOT NULL DEFAULT -1, `timestamp` INTEGER NOT NULL DEFAULT -1)
        //CREATE TABLE IF NOT EXISTS `liaobei_bot_settings_757403101` (`key` INTEGER PRIMARY KEY, `value` TEXT NOT NULL DEFAULT '')
        //CREATE TABLE IF NOT EXISTS `liaobei_member_read_cursor_initialized_group_757403101` (`value` INTEGER PRIMARY KEY)
        boolean suc = false;
        suc = dbHelper.execSql("CREATE TABLE IF NOT EXISTS `liaobei_user_registry_" + uid + "` (`key` INTEGER PRIMARY KEY, `value` TEXT NOT NULL DEFAULT '')");
        if (suc == false) {
            DBUtil.log(TAG, "create table error");
            throw new Exception("insert data error");
        }
        suc = dbHelper.execSql("CREATE TABLE IF NOT EXISTS `liaobei_sticky_conversation_" + uid + "` (`id` INTEGER PRIMARY KEY AUTOINCREMENT, `room_type` INTEGER NOT NULL DEFAULT -1, `room_id` INTEGER NOT NULL DEFAULT -1, `timestamp` INTEGER NOT NULL DEFAULT -1)");
        if (suc == false) {
            DBUtil.log(TAG, "create table error");
            throw new Exception("insert data error");
        }
        suc = dbHelper.execSql("CREATE TABLE IF NOT EXISTS `liaobei_bot_settings_" + uid + "` (`key` INTEGER PRIMARY KEY, `value` TEXT NOT NULL DEFAULT '')");
        if (suc == false) {
            DBUtil.log(TAG, "create table error");
            throw new Exception("insert data error");
        }
        suc = dbHelper.execSql("CREATE TABLE IF NOT EXISTS `liaobei_member_read_cursor_initialized_group_" + uid + "` (`value` INTEGER PRIMARY KEY)");
        if (suc == false) {
            DBUtil.log(TAG, "create table error");
            throw new Exception("insert data error");
        }

    }
}
