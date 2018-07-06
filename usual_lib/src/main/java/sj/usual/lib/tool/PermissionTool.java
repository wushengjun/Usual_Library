package sj.usual.lib.tool;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import sj.usual.lib.log.MyLg;

/**
 * 运行时权限管理类
 * Created by WuShengjun on 2017/10/22.
 */

public class PermissionTool {
    private Context mContext;
    private Activity mActivity;
    private String[] mPermissions;
    private int[] mRequestCodes;
    private int permissionIndex;
    
    public PermissionTool(Context mContext) {
        this.mContext = mContext;
        if(mContext instanceof Activity) {
            mActivity = (Activity) mContext;
        }
    }

    public static PermissionTool getInstance(Context mContext) {
        return new PermissionTool(mContext);
    }

    /**
     * 检查运行时权限
     * @param permission
     * @return true已授权，false未授权
     */
    public boolean checkPermission(String permission) {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true; // 6.0以下默认已申请
        }
        if(ContextCompat.checkSelfPermission(mContext, permission) == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 上次是否拒绝过权限
     * @param permission
     * @return
     */
    public boolean shouldShowRequestPermission(String permission) {
        return ActivityCompat.shouldShowRequestPermissionRationale(mActivity, permission);
    }

    /**
     * 申请权限
     * @param permission
     * @param requestCode
     */
    public void requestOnlyPermission(String permission, int requestCode) {
        ActivityCompat.requestPermissions(mActivity, new String[] { permission }, requestCode);
    }

    /**
     *
     * @param permissions
     * @param requestCodes
     * @return 所有权限都授权返回true，否则返回false
     */
    public boolean checkAndRequestPermissions(String[] permissions, int[] requestCodes) {
        mPermissions = permissions;
        mRequestCodes = requestCodes;
        boolean grant = true;
        permissionIndex = 0;

        for(int i=0; i<permissions.length; i++) {
            String permission = permissions[i];
            if(!checkPermission(permission)) {
                requestOnlyPermission(permission, requestCodes[i]);
                grant = false;
                break;
            }
        }
        return grant;
    }

    public boolean checkAndRequestPermission(String permission, int requestCode) {
        if (!checkPermission(permission)) { // 未授权
//            MyLg.e("checkAndRequestPermissions", permission + " 未授权");
            requestOnlyPermission(permission, requestCode);
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
            String permission = permissions.length > 0 ? permissions[0] : "";
            boolean allow = grantResults.length > 0 ? grantResults[0] == PackageManager.PERMISSION_GRANTED: false;
            onReqPermissionResultListener.onReqPermissionResult(requestCode, permission, allow, permissions, grantResults);
        }

        permissionIndex++;
        if(mRequestCodes != null && mPermissions != null
                && mActivity != null && !mActivity.isDestroyed() && !mActivity.isFinishing()) {
//            if(permissionIndex < mPermissions.length && !checkPermission(mActivity, mPermissions[permissionIndex])) {
//                 requestOnlyPermission(mActivity, mPermissions[permissionIndex], mRequestCodes[permissionIndex]);
//            }
            for(int i=0; i<mRequestCodes.length-1; i++) { // 循环申请多个权限
                if(requestCode == mRequestCodes[i] && !checkPermission(mPermissions[i+1])) {
                    MyLg.e("re", "" + mPermissions[i+1]);
                    requestOnlyPermission(mPermissions[i+1], mRequestCodes[i+1]);
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
         * @param permission
         */
        void shouldShowRequestPermissionRationale(String permission, int requestCode);
    }
}
