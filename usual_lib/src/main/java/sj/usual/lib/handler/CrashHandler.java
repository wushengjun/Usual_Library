package sj.usual.lib.handler;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;

import sj.usual.lib.util.DateUtils;

/**
 * 自定义未捕捉异常处理
 * Created by WuShengjun on 2017/10/21.
 */

public abstract class CrashHandler implements Thread.UncaughtExceptionHandler {

    /** 系统默认的UncaughtException处理类 **/
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    /** Context上下文 **/
    private static Context context;

    /** 程序出错提示信息 **/
    private String mTipMsg;

    /** 保存crash文件的时间 **/
    private static String saveTime;

    /** 生成的crash文件 **/
//    private File crashFile;

    public void init(Context context) {
        init(context, null);
    }

    public void init(Context context, String toastMsg) {
        this.context = context;
        this.mTipMsg = toastMsg;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if(mDefaultHandler != null && !handleException(ex)) {
            mDefaultHandler.uncaughtException(thread, ex);
            doSomething(ex);
        } else {
            sleep(3000); // 程序休眠3s后退出
            doSomething(ex); // 重写方法，处理操作
            sleep(600);
            android.os.Process.killProcess(android.os.Process.myPid()); // 该杀死崩溃的进程

//            Intent intent = new Intent(context, LoginActivity.class);
//            PendingIntent restartIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//            // 退出程序
//            AlarmManager mgr = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//            mgr.set(AlarmManager.RTC, System.currentTimeMillis(), restartIntent); // 1秒钟后重启应用
        }
    }

    private void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean handleException(Throwable ex) {
        if(ex == null) {
            return false;
        } else {
            if(!TextUtils.isEmpty(mTipMsg))
                new Thread(new Runnable() {
                    public void run() { // 弹出窗口提示信息
                        Looper.prepare();
                        if(!TextUtils.isEmpty(mTipMsg)) {
                            Toast.makeText(context, mTipMsg, Toast.LENGTH_LONG).show();
                        }
                        Looper.loop();
                    }
                }).start();
            return true;
        }
    }

    /**
     * @param ex
     */
    public abstract void doSomething(Throwable ex);

    /**
     * 出错提示信息
     * @param tipMsg
     */
    public void setErrorMsg(String tipMsg) {
        mTipMsg = tipMsg;
    }

    /**
     * 保存crash到文件
     * @param ex
     * @param crashFilePath
     * @return crashMessage
     */
    public static String saveCrashFile(Throwable ex, String crashFilePath) {
        File file = new File(crashFilePath);
        return saveCrashFile(ex, file);
    }

    /**
     * 保存crash到文件
     * @param ex
     * @param crashPathFile
     * @return crashMessage
     */
    public static String saveCrashFile(Throwable ex, File crashPathFile) {
        if(!crashPathFile.exists()) { // 创建文件夹
            crashPathFile.mkdirs();
        }
        StringBuffer sb = new StringBuffer();
        saveTime = DateUtils.getCurrFormatDate(DateUtils.DATEFORMAT_FULL);
        sb.append("DateTime: " + saveTime + "\n");
        sb.append("DeviceInfo: " + Build.MANUFACTURER + " " + Build.MODEL + "\n");
        sb.append("AppVersion: " + getPackageInfo().versionName + "\n");

        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();
        String result = writer.toString();
        sb.append("Excetpion: \n");
        sb.append(result);

        Log.e("CrashHandler", result); // 打印输出，方便开发调试
        saveToCrashFile(sb.toString(), crashPathFile); // 记录异常到特定文件中
        return result;
    }

    /**
     * 写入文本
     * @param crashText
     */
    private static void saveToCrashFile(String crashText, File crashPathFile) {
        File crashFile = new File(crashPathFile, "log_v" + getPackageInfo().versionName + "(" + saveTime + ").txt");
        crashFile.setReadOnly();
        FileWriter writer;
        try {
            writer = new FileWriter(crashFile);
            writer.write(crashText);
            writer.flush();
            writer.close();
        } catch (IOException e) {
            Log.e("saveToCrashFile", "" + e.getMessage());
        }
    }

    private static PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
        } catch (PackageManager.NameNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        if(info == null) {
            info = new PackageInfo();
        }
        return info;
    }
}
