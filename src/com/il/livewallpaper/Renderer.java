package com.il.livewallpaper;

import android.util.Log;

public class Renderer extends Thread {
	
	public interface RendererCallback {
		public void render();
	}

	private boolean mRunning;
	private boolean mStopping = false;
	private RendererCallback mCallback = null;
	int mFrameTime = 1000 / 60;
	
	public Renderer() {
	}

	public boolean isRunning() {
		return mRunning;
	}
	
	public boolean isStopping() {
		return mStopping;
	}
	
    public void switchOn(){
    	Log.i("IL", "switchOn()");
        mRunning = true;
        start();
    }

    public void switchOff() {
    	Log.i("IL", "switchOff()");
        mRunning = false;
        mStopping = true;
        synchronized(this){
            notify();
        }
    }
    
    public void setCallback(RendererCallback c) {
    	mCallback = c;
    }

    @Override
    public void run() {
    	Log.v("IL", "run");
    	while(mRunning) {
    		long b = System.currentTimeMillis();
    		if (mCallback != null) {
				mCallback.render();
    		}
    		long diff = mFrameTime-(System.currentTimeMillis() - b);
    		if (diff > 1) {
    			try {
//    				Log.i("IL", "Sleeping for:"+diff);
					Thread.sleep(diff);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
    		}
    	}
    }
}
