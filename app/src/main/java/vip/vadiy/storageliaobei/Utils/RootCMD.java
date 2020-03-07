package vip.vadiy.storageliaobei.Utils;

import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by AiXin on 2019-10-24.
 */
public class RootCMD {

    private final static String TAG = "RootCMD";

    /**
     * 通过  isRooted 判断并获取root权限
     *
     * @param pkgName
     * @return
     */
    public static boolean stopApp(String pkgName) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "am force-stop " + pkgName;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                if (process != null) {
                    process.destroy();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * 应用程序运行命令获取 Root权限，设备必须已破解(获得ROOT权限)
     * 通过  isRooted 判断并获取root权限
     *
     * @return 应用程序是/否获取Root权限
     */
    public static boolean upgradeRootPermission(String pkgCodePath) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + pkgCodePath;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;

    }

    /**
     * 复制单个文件
     * 通过  isRooted 判断并获取root权限
     *
     * @param oldPath$Name String 原文件路径+文件名 如：data/user/0/com.test/files/abc.txt
     * @param newPath$Name String 复制后路径+文件名 如：data/user/0/com.test/cache/abc.txt
     * @return <code>true</code> if and only if the file was copied;
     * <code>false</code> otherwise
     */
    public static boolean copyFile(String oldPath$Name, String newPath$Name, String newChmod) {
        Process process = null;
        DataOutputStream os = null;
        try {
            mkFileDirs(newPath$Name);
            String cmd = "";
            if (newChmod == null || "".equals(newChmod)) {
                cmd = "cp " + oldPath$Name + " " + newPath$Name + "\n";
            } else {
                cmd = "cp " + oldPath$Name + " " + newPath$Name + "\n" + "chmod " + newChmod + " " + newPath$Name;
            }
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    /**
     * @param path 单纯的目录
     */
    public static void mkDirs(String path) {
        File filedirs = new File(path);
        if (!filedirs.exists()) {
            filedirs.mkdirs();
        }
    }

    /**
     * @param pathFile 文件路径
     */
    public static void mkFileDirs(String pathFile) {
        File filedirs = new File(pathFile);
        if (!filedirs.exists()) {
            filedirs.getParentFile().mkdirs();
        }
    }

    /**
     * 通过  isRooted 判断并获取root权限
     *
     * @param path$Name
     * @return
     */
    public static boolean delFile(String path$Name) {
        Process process = null;
        DataOutputStream os = null;
        try {
            String cmd = "chmod 777 " + path$Name + "\n" + "rm " + path$Name;
            process = Runtime.getRuntime().exec("su"); //切换到root帐号
            os = new DataOutputStream(process.getOutputStream());
            os.writeBytes(cmd + "\n");
            os.writeBytes("exit\n");
            os.flush();
            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (os != null) {
                    os.close();
                }
                process.destroy();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    public static boolean isRooted() {
        //检测是否ROOT过
        DataInputStream stream;
        boolean flag = false;
        try {
            stream = Terminal("ls /data/");
            //目录哪都行，不一定要需要ROOT权限的
            byte[] b = new byte[1024];
            if (stream.read(b) > 0)
                flag = true;
            //根据是否有返回来判断是否有root权限
        } catch (Exception e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        return flag;
    }

    public static DataInputStream Terminal(String command) throws Exception {
        Process process = Runtime.getRuntime().exec("su");
        //执行到这，Superuser会跳出来，选择是否允许获取最高权限
        OutputStream outstream = process.getOutputStream();
        DataOutputStream DOPS = new DataOutputStream(outstream);
        InputStream instream = process.getInputStream();
        DataInputStream DIPS = new DataInputStream(instream);
        String temp = command + "\n";
        //加回车
        DOPS.writeBytes(temp);
        //执行
        DOPS.flush();
        //刷新，确保都发送到outputstream
        DOPS.writeBytes("exit\n");
        //退出
        DOPS.flush();
        process.waitFor();
        return DIPS;
    }

    // 执行命令并且输出结果

    public static String execRootCmd(String cmd) {
        String result = "";
        DataOutputStream dos = null;
        DataInputStream dis = null;

        try {
            Process p = Runtime.getRuntime().exec("su");// 经过Root处理的android系统即有su命令
            dos = new DataOutputStream(p.getOutputStream());
            dis = new DataInputStream(p.getInputStream());

            DBUtil.log(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            String line = null;
            while ((line = dis.readLine()) != null) {
                Log.d("result", line);
                result += line;
            }
            p.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (dis != null) {
                try {
                    dis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }

    // 执行命令但不关注结果输出

    public static int execRootCmdSilent(String cmd) {
        int result = -1;
        DataOutputStream dos = null;

        try {
            Process p = Runtime.getRuntime().exec("su");
            dos = new DataOutputStream(p.getOutputStream());

            DBUtil.log(TAG, cmd);
            dos.writeBytes(cmd + "\n");
            dos.flush();
            dos.writeBytes("exit\n");
            dos.flush();
            p.waitFor();
            result = p.exitValue();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return result;
    }


}
