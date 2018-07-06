package sj.usual.lib.okhttp;

import android.os.Handler;
import android.os.Looper;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * Created by WuShengjun on 2017/10/20.
 * 包装响应体，用于处理提示下载进度
 */

public abstract class ProgressResponseBody extends ResponseBody {

    //实际待包装的响应体
    private final ResponseBody responseBody;

    //包装完成的BufferedSource
    private BufferedSource bufferedSource;

    //传递下载进度到主线程
    private Handler mHandler = new Handler(Looper.getMainLooper());

    public ProgressResponseBody(ResponseBody responseBody) {
        this.responseBody = responseBody;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    /**
     * 读取，回调进度接口
     *
     * @return
     */
    private Source source(Source source) {
        return new ForwardingSource(source) {
            //读取当前获取的字节数
            long totalBytesHadRead = 0L;

            @Override
            public long read(Buffer sink, final long byteCount) throws IOException {
                final long byteRead = super.read(sink, byteCount);
                //增加当前读取的字节数，如果读取完成则返回-1
                totalBytesHadRead += byteRead != -1 ? byteRead : 0;
                //回调，若是contentLength()不知道长度，则返回-1
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        updateProgress(totalBytesHadRead, contentLength());
                    }
                });
                return byteRead;
            }
        };
    }

    public abstract void updateProgress(long downloadedSize, long totalSize);
}
