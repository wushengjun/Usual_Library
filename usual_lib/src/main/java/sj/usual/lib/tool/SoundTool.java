package sj.usual.lib.tool;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.support.annotation.RawRes;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import sj.usual.lib.R;
import sj.usual.lib.log.MyLg;
import sj.usual.lib.util.ToastUtils;

/**
 * Created by WuShengjun on 2017/12/6.
 */

public class SoundTool {
    private Context mContext;
    private AudioManager manager;
    public SoundPool.Builder soundPoolBuilder;
    public SoundPool soundPool;
    private int mMaxStreams = 20;
    private Map<Integer, Integer> soundMap = new HashMap<>();

    private final static int CPU_COUNT = Runtime.getRuntime().availableProcessors();
    private ExecutorService mExecutorService;

    public SoundTool(Context context) {
        releaseSoundPool();
        this.mContext = context;
        manager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mExecutorService = Executors.newFixedThreadPool(CPU_COUNT + 1);
    }

    /**
     * 加载声音源
     * @param soundID 播放声音是唯一ID
     * @param rawSoundResId raw声音资源
     * @param priority 声音优先级，0为最低
     */
    public SoundTool loadSound(final int soundID, final @RawRes int rawSoundResId, final int priority) {
        if(soundPool == null) {
            soundPoolBuilder = new SoundPool.Builder()
                    .setMaxStreams(mMaxStreams);
            soundPool = soundPoolBuilder.build(); // soundPool = new SoundPool(10, AudioManager.STREAM_MUSIC, 5); // 已过时
        }
        if(mExecutorService != null) {
            mExecutorService.execute(new Runnable() {
                @Override
                public void run() {
                    soundMap.put(soundID, soundPool.load(mContext, rawSoundResId, priority));
                    MyLg.e("soundPool", "Load soundId " + soundID + " successfully");
                }
            });
        }
        return this;
    }

    public SoundTool setMaxStreams(int maxStreams) {
        if(soundPoolBuilder != null) {
            mMaxStreams = maxStreams;
            soundPoolBuilder.setMaxStreams(maxStreams);
        }
        return this;
    }

    /**
     * 播放声音
     * @param soundID 对应loadSound时传入的soundID
     */
    public void playSound(int soundID) {
        playSound(soundID, 0);
    }

    /**
     * 播放声音
     * @param soundID 对应loadSound时传入的soundID
     * @param loop  循环次数：0为不循环，-1为无限循环
     */
    public void playSound(int soundID, int loop) {
        playSound(soundID, loop, 1);
    }

    /**
     * 播放声音
     * @param soundID 对应loadSound时传入的soundID
     * @param loop 循环次数：0为不循环，-1为无限循环
     * @param rate 回放速度：该值在0.5-2.0之间，1为正常速度
     */
    public void playSound(int soundID, int loop, float rate) {
        playSound(soundID, 1, loop, rate);
    }

    /**
     * 播放声音
     * @param soundID 对应loadSound时传入的soundID
     * @param priority 优先级：0为最低
     * @param loop 循环次数：0为不循环，-1为无限循环
     * @param rate 回放速度：该值在0.5-2.0之间，1为正常速度
     */
    public void playSound(int soundID, int priority, int loop, float rate) {
        if(soundPool == null) {
            ToastUtils.showCenter(mContext, getStr(R.string.usuallib_no_load_sound));
            return;
        }
        // 获得当前AudioManager对象最大音量值
        float audioMaxVol = manager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 获得当前AudioManager对象音量值
        float audioCurrentVol = manager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 获得相对音量值
        float volRatio = audioCurrentVol / audioMaxVol;
        try {
            int sound = soundMap.get(soundID);
//            MyLg.e("playSound", "soundId=" + sound);
            soundPool.play(sound,
                    volRatio, // 左声道音量
                    volRatio, // 右声道音量
                    priority, // 优先级：0为最低
                    loop, // 循环次数：0为不循环，-1为无限循环
                    rate // 回放速度：该值在0.5-2.0之间，1为正常速度
            );
        } catch (Exception e) {
            e.printStackTrace();
            MyLg.e("playSound", "Exception=" + e);
        }
    }

    /**
     * 释放声音池资源，退出程序时调用
     */
    public SoundTool releaseSoundPool() {
        soundMap.clear();
        if (soundPool != null) {
            soundPool.release();
            soundPool = null;
            MyLg.e("soundPool", "release soundpool successfully");
        }
        return this;
    }

    public SoundTool closeThreadPool() {
        if(mExecutorService != null) {
            mExecutorService.shutdown();
            mExecutorService = null;
            MyLg.e("threadPool", "close threadPool successfully");
        }
        return this;
    }

    private String getStr(int resId) {
        return mContext.getString(resId);
    }
}
