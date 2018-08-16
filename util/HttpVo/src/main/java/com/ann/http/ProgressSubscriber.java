package com.ann.http;

import android.content.Context;

import com.ann.http.iml.SubscriberOnNextListener;
import com.ann.http.util.NetworkUtils;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import java.net.SocketTimeoutException;

import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import retrofit2.HttpException;

/**
 * Created by anliyuan on 2017/11/10.
 */

public class ProgressSubscriber<T> implements Observer<T> {
    private SubscriberOnNextListener mSubscriberOnNextListener;
    private ProgressDialogHandler mProgressDialogHandler;
    private Disposable d = null;
    private Context mContext;

    /**
     * 封装了ProgressDialog的观察者类，在开始之前显示progress，完成之后dismiss progress
     *
     * @param context                   context
     * @param mSubscriberOnNextListener onNext方法的回调接口
     * @param cancelable                点击progress是否可取消
     * @param progressText              进度条要显示的文字
     */
    public ProgressSubscriber(Context context, SubscriberOnNextListener mSubscriberOnNextListener, boolean
            cancelable, boolean show, String progressText) {
        this.mContext = context;
        this.mSubscriberOnNextListener = mSubscriberOnNextListener;
        if (show) {
            mProgressDialogHandler = new ProgressDialogHandler(context, cancelable, true);
            mProgressDialogHandler.setTipMsg(progressText);
        }
    }


    /**
     * 显示progress
     */
    private void showProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.SHOW_PROGRESS_DIALOG).sendToTarget();
        }
    }

    /**
     * dismiss progress
     */
    private void dismissProgressDialog() {
        if (mProgressDialogHandler != null) {
            mProgressDialogHandler.obtainMessage(ProgressDialogHandler.DISMISS_PROGRESS_DIALOG).sendToTarget();
            mProgressDialogHandler = null;
            unSubscribe();
        }
    }


    @Override
    public void onSubscribe(Disposable d) {
        this.d = d;
        showProgressDialog();
    }


    private void unSubscribe() {
        if (d.isDisposed()) d.dispose();
    }

    @Override
    public void onNext(T t) {
        if (mSubscriberOnNextListener != null) {
            try {
                dismissProgressDialog();
                mSubscriberOnNextListener.onNext(t);
            } catch (Exception e) {
                System.out.print(e.toString());
            }
        }
    }

    @Override
    public void onError(Throwable e) {
        e.printStackTrace();
        try {
            dismissProgressDialog();
            if (mSubscriberOnNextListener != null) {
                if (!NetworkUtils.isAvailable(mContext)) {
                    mSubscriberOnNextListener.onError(404, "当前网络不可用～");
                } else {
                    if (e instanceof HttpException && ((HttpException) e).code() == 404) {
                        mSubscriberOnNextListener.onError(405, "网络连接失败，请稍后重试");
                    } else if (e instanceof SocketTimeoutException) {
                        mSubscriberOnNextListener.onError(405, "网络连接超时，请稍后重试");
                    } else if (e instanceof JsonSyntaxException || e instanceof MalformedJsonException || (e instanceof HttpException && ((HttpException) e).code() < 600 && (((HttpException) e).code() >= 500))) {
                        mSubscriberOnNextListener.onError(503, "服务器异常～");
                    } else {
                        mSubscriberOnNextListener.onError(405, "网络异常，请稍后重试～");
                    }
                }
                unSubscribe();
            }
        } catch (Exception e1) {
            e1.printStackTrace();
            System.out.print(e.getMessage());
        }
    }

    @Override
    public void onComplete() {
        dismissProgressDialog();
    }
}
