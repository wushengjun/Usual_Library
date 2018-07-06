package sj.usual.lib.tool;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESedeKeySpec;

/**
 * 对字符串加密
 * Created by WuShengjun on 2017/11/9.
 */

public class EncryptStringTool {
    /**
     * 保存需加密的String
     * @param context
     * @param spfKey
     * @param spfValue
     */
    public static void encryptSaveStr(Context context, String spfKey, String spfValue) {
        // 对数据进行加密
        // 得到key
        SecretKey key = readKey(getPath(context, spfKey));
        if (key == null) {
            key = get3DESKey();
            // 保存key
            saveKey(key, getPath(context, spfKey));
        }
        // 对str进行加密
        byte[] passwordByte = encrypt3DES(spfValue, key);
        spfValue = Base64.encodeToString(passwordByte, Base64.DEFAULT);
        Log.e("加密", spfKey + "加密后>>>>" + spfValue);

        SharedPreferences spf = getSpf(context);
        SharedPreferences.Editor editor = spf.edit();
        editor.putString(spfKey, spfValue);
        editor.commit();
    }

    /**
     *
     * @param context
     * @param spfKey
     * @return
     */
    public static String readEncryptStr(Context context, String spfKey) {
        return readEncryptStr(context, spfKey, "");
    }

    // 读取
    public static String readEncryptStr(Context context, String spfKey, String defVal) {
        String str = getSpf(context).getString(spfKey, defVal);
        if(TextUtils.isEmpty(str)) {
            return "";
        }
        byte[] bytes = Base64.decode(str, Base64.DEFAULT);
        return decode3DES(bytes, readKey(getPath(context, spfKey)));
    }

    // 保存key
    private static boolean saveKey(SecretKey key, String path) {
        try {
            FileOutputStream fileOutputStream1 = new FileOutputStream(path);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(
                    fileOutputStream1);
            objectOutputStream.writeObject(key);
            objectOutputStream.flush();
            objectOutputStream.close();
            return true;
        } catch (Exception e) {
            Log.d("保存key", e.toString());
        }
        return false;
    }

    // 读取key
    private static SecretKey readKey(String path) {
        SecretKey key = null;
        try {
            ObjectInputStream objectInputStream = new ObjectInputStream(
                    new FileInputStream(path));
            key = (SecretKey) objectInputStream.readObject();
            objectInputStream.close();
        } catch (Exception e) {
            Log.d("读取key", e.toString());
        }
        return key;
    }

    // 获取路径
    private static String getPath(Context context, String fileName) {
        if (TextUtils.isEmpty(fileName)) {
            return null;
        }
        File file = context.getDir("Usuallib_Encryption", Context.MODE_PRIVATE);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }

        file = new File(file, fileName);
        if (!file.exists() || !file.isFile()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                Log.d("加密文件创建失败：", e.toString());
                return null;
            }
        }
        return file.getAbsolutePath();
    }

    /**
     * 数据加解密3DES所需要的Key
     */
    private static SecretKey get3DESKey() {
        try {
            // 生成key
            KeyGenerator keyGenerator = KeyGenerator.getInstance("DESede");
            keyGenerator.init(168);// can 168 or 112/new SecureRandom()
            SecretKey secretKey = keyGenerator.generateKey();
            byte[] bytesKey = secretKey.getEncoded();

            // 转化key
            DESedeKeySpec deSedeKeySpec = new DESedeKeySpec(bytesKey);
            SecretKeyFactory factory = SecretKeyFactory.getInstance("DESede");
            SecretKey generateSecret = factory.generateSecret(deSedeKeySpec);

            return generateSecret;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d("测试", e.toString());
        }
        return null;
    }

    /**
     * 数据加密3DES
     */
    private static byte[] encrypt3DES(String str, SecretKey generateSecret) {
        try {
            // 加密
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.ENCRYPT_MODE, generateSecret);
            byte[] result = cipher.doFinal(str.getBytes("utf-8"));

            return result;
        } catch (Exception e) {
            System.out.println("加密出错：" + e.getMessage());
        }
        return null;
    }

    /**
     * 数据解密3DES
     */
    private static String decode3DES(byte[] str, SecretKey generateSecret) {
        try {
            // 加密
            Cipher cipher = Cipher.getInstance("DESede/ECB/PKCS5Padding");
            cipher.init(Cipher.DECRYPT_MODE, generateSecret);
            System.out.println(2);
            byte[] result = cipher.doFinal(str);
            System.out.println(3);

            return new String(result, "utf-8");
        } catch (Exception e) {
            Log.e("解密出错", e.getMessage());
        }
        return "";
    }

    private static SharedPreferences getSpf(Context context) {
        return context.getSharedPreferences("usuallib_encryption", Context.MODE_PRIVATE);
    }
}