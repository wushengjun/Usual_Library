package sj.usual.lib.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

/**
 * 运行时权限管理类
 * Created by WuShengjun on 2017/10/22.
 */

public class PermissionUtils {
    private Activity mActivity;
    private String[] mPermissions;
    private int[] mRequestCodes;

    public static PermissionUtils getInstance() {
        return new PermissionUtils();
    }

    /**
     * 检查运行时权限
     * @param context
     * @param permission
     * @return true已授权，false未授权
     */
    public static boolean checkPermission(Context context, String permission) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true; // 6.0以下默认已申请
        }
        if(ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 申请权限
     * @param activity
     * @param permission
     * @param requestCode
     */
    public void requestPermission(Activity activity, String permission, int requestCode) {
        if(ActivityCompat.shouldShowRequestPermissionRationale(activity, permission)) {
//            MyLg.e("requestPermission", "shouldShowRequestPermissionRationale> " + permission);
            if(onReqPermissionResultListener != null) {
                onReqPermissionResultListener.shouldShowRequestPermissionRationale(activity, permission, requestCode);
            }
        } else {
            ActivityCompat.requestPermissions(activity, new String[] { permission }, requestCode);
        }
    }

    /**
     *
     * @param activity
     * @param permissions
     * @param requestCodes
     * @return 所有权限都授权返回true，否则返回false
     */
    public boolean checkAndRequestPermissions(Activity activity, String[] permissions, int[] requestCodes) {
        mActivity = activity;
        mPermissions = permissions;
        mRequestCodes = requestCodes;
        boolean grant = true;
        for(int i=0; i<permissions.length; i++) {
            String permission = permissions[i];
            if(!checkAndRequestPermission(activity, permission, requestCodes[i])) {
                grant = false;
            }
        }
        return grant;
    }

    public boolean checkAndRequestPermission(Activity activity, String permission, int requestCode) {
        if (!checkPermission(activity, permission)) { // 未授权
//            MyLg.e("checkAndRequestPermissions", permission + " 未授权");
            requestPermission(activity, permission, requestCode);
            return false;
        }
        return true;
    }

    /**
     * 接收申请权限结果，需放在Activity或Fragment的重写方法onRequestPermissionsResult里面
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    public void receiveResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(onReqPermissionResultListener != null) {
            String perssion = permissions.length > 0 ? permissions[0] : "";
            boolean allow = grantResults.length > 0 ? grantResults[0] == PackageManager.PERMISSION_GRANTED: false;
            onReqPermissionResultListener.onReqPermissionResult(requestCode, perssion, allow, permissions, grantResults);
        }
        if(mRequestCodes != null && mPermissions != null
                && mActivity != null && !mActivity.isFinishing()) {
            for(int i=0; i<mRequestCodes.length-1; i++) { // 循环申请多个权限
                if(requestCode == mRequestCodes[i] && !checkPermission(mActivity, mPermissions[i+1])) {
                    requestPermission(mActivity, mPermissions[i+1], mRequestCodes[i+1]);
                    break;
                }
            }
        }
    }

    /**
     * 申请权限结果回调接口
     */
    private OnReqPermissionResultListener onReqPermissionResultListener;

    public OnReqPermissionResultListener getOnReqPermissionResultListener() {
        return onReqPermissionResultListener;
    }

    public void setOnReqPermissionResultListener(OnReqPermissionResultListener onReqPermissionResultListener) {
        this.onReqPermissionResultListener = onReqPermissionResultListener;
    }

    /**
     * 申请权限结果回调接口
     */
    public interface OnReqPermissionResultListener {
        /**
         * 申请权限结果回调
         * @param requestCode
         * @param permissions
         * @param grantResults
         */
        void onReqPermissionResult(int requestCode, String permission, boolean allow, @NonNull String[] permissions, @NonNull int[] grantResults);

        /**
         * 第一次已拒绝权限，下次再请求时的回调处理
         * @param activity
         * @param permission
         */
        void shouldShowRequestPermissionRationale(Activity activity, String permission, int requestCode);
    }
}
