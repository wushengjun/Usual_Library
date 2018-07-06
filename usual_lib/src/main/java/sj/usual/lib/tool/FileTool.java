package sj.usual.lib.tool;

import android.os.Environment;
import android.util.Log;

import java.io.File;

import sj.usual.lib.log.MyLg;

/**
 * Created by WuShengjun on 2017/11/3.
 */

public class FileTool {
    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param filePath 将要删除的文件或目录路径
     * @return boolean Returns "true" if all deletions were successful.
     * If a deletion fails, the method stops attempting to
     * delete and returns "false".
     * @return
     */
    public static boolean deleteDir(String filePath) {
        File file = new File(filePath);
        return deleteDir(file);
    }

    /**
     * 递归删除目录下的所有文件及子目录下所有文件
     * @param file 将要删除的文件或目录
     * @return
     */
    public static boolean deleteDir(File file) {
        if(file == null || !file.exists())
            return false;
        if (file.isDirectory()) { // 若为目录
            File[] childFiles = file.listFiles(); // 遍历里面所有文件和目录
            // 递归删除目录中的子目录下
            if(childFiles != null) {
                for (int i = 0; i < childFiles.length; i++) {
//                    MyLg.e("directoryFile", "childFilePath=" + childFiles[i].getName());
                    boolean succ = deleteDir(childFiles[i]);
                    if (!succ)
                        return false;
                }
            }
        }
        // file为文件或目录此时为空，可以删除
//        MyLg.e("deleteFile", "filePath=" + file.getName());
        return file.delete();
    }

    /**
     * 判断SDcard是否可用
     * @return
     */
    public static boolean sdcardExists() {
        return Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState());
    }

    /**
     * 获取SDcard的总存储量,返回-1则不存在
     * @return
     */
    public static long getSDCardTotalSpace() {
        File file = Environment.getExternalStorageDirectory();
        return getFileTotalSpace(file);
    }

    /**
     * 获取SDcard的剩余存储量,返回-1则不存在
     * @return
     */
    public static long getSDCardUsableSpace() {
        File file = Environment.getExternalStorageDirectory();
        return getFileUsableSpace(file);
    }

    /**
     * 获取文件夹总空间，-1为不存在
     * @param file
     * @return
     */
    public static long getFileTotalSpace(File file) {
        if (file != null && file.exists()) {
            return file.getTotalSpace(); // 文件的总大小（此方法应用于8以上，需要在此方法打上NewApi的注解）
        } else {
            return -1;
        }
    }

    /**
     * 获取文件夹剩余空间，-1为不存在
     * @return
     */
    public static long getFileUsableSpace(File file) {
        if (file != null && file.exists()) {
            return file.getUsableSpace(); // 文件的总大小（此方法应用于8以上，需要在此方法打上NewApi的注解）
        } else {
            return -1;
        }
    }
}
