package vip.vadiy.storageliaobei.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.security.SecurityPermission;

/**
 * Created by AiXin on 2019-10-26.
 */
public class FileUtil {
    /**
     * 根据文件路径拷贝文件
     *
     * @param srcPath  源文件路径
     * @param destPath 目标文件路径
     * @return boolean 成功true、失败false
     */
    public static boolean copyFile(String srcPath, String destPath) {
        boolean result = false;
        if ((srcPath.isEmpty()) || (destPath.isEmpty())) {
            return result;
        }
        File src = new File(srcPath);//源文件不存在
        if (!src.exists()) {
            return result;
        }
        File dest = new File(destPath);
        if (dest != null && dest.exists()) {//目标文件存在则删除目标文件
            dest.delete(); // delete file
        }
        if (dest!=null && !dest.exists()){
            dest.getParentFile().mkdirs();
        }
        try {
            dest.createNewFile();
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }

        FileChannel srcChannel = null;
        FileChannel dstChannel = null;

        try {
            srcChannel = new FileInputStream(src).getChannel();
            dstChannel = new FileOutputStream(dest).getChannel();
            srcChannel.transferTo(0, srcChannel.size(), dstChannel);
            result = true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            return result;
        }
        try {
            srcChannel.close();
            dstChannel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return result;
    }
}
