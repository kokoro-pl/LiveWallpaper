package com.il.livewallpaper;

import android.util.Log;

public class Renderer extends Thread {
    private final static String TAG = Renderer.class.getSimpleName();
    private final static boolean DEBUG = false;

    public interface RendererCallback {
        public void render();
    }

    private boolean mRunning;
    private boolean mStopping = false;
    private RendererCallback mCallback = null;
    int mFrameTime = 1000 / 60;

    public Renderer(RendererCallback callback) {
        mCallback = callback;
    }

    public boolean isRunning() {
        return mRunning;
    }

    public boolean isStopping() {
        return mStopping;
    }

    public void switchOn(){
        if (DEBUG) Log.i(TAG, "switchOn()");
        mRunning = true;
        start();
    }

    public void switchOff() {
        if (DEBUG) Log.i(TAG, "switchOff()");
        mRunning = false;
        mStopping = true;
    }

    @Override
    public void run() {
        if (DEBUG) Log.v(TAG, "run");
        while(mRunning) {
            long b = System.currentTimeMillis();
            if (mCallback != null) {
                mCallback.render();
            }
            long diff = mFrameTime-(System.currentTimeMillis() - b);
            if (diff > 1) {
                try {
                    if (DEBUG) Log.i(TAG, "Sleeping for:" + diff);
                    Thread.sleep(diff);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
        mStopping = false;
    }
}
