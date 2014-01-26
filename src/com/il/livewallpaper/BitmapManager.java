package com.il.livewallpaper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.util.SparseArray;

public class BitmapManager {
    private static final String TAG = BitmapManager.class.getSimpleName();
    private static final boolean DEBUG = true;
    private SparseArray<Bitmap> mData = new SparseArray<Bitmap>();
    private static BitmapManager mInstance = null;
    private static final Object mLock = new Object();
    private Context mContext;

    private BitmapManager(Context context) {
        mContext = context;
    }

    public static BitmapManager getInstance(Context context) {
        if (mInstance == null) {
            synchronized (mLock) {
                if (mInstance == null) {
                    mInstance = new BitmapManager(context);
                }
            }
        }
        return mInstance;
    }

    public Bitmap getBitmap(int id) {
        return getBitmap(id, -1, -1);
    }

    /**
     * Not thread safe!
     */
    public Bitmap getBitmap(int id, int scaleToWidth, int scaleToHeight) {
        String name;
        if (DEBUG) {
            name = mContext.getResources().getResourceName(id);
        }
        Bitmap b = mData.get(id);
        final boolean noScale = scaleToHeight == -1 || scaleToWidth == -1;
        if (DEBUG && noScale) {
            Log.v(TAG, "Scaling disabled.");
        }

        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inScaled = false;

        boolean bitmap_updated = true;

        if (b == null) {
            if (DEBUG) {
                Log.v(TAG, "Miss! Loading: "+name);
            }
            b = BitmapFactory.decodeResource(mContext.getResources(), id, options);
        } else if ((b.getWidth() > scaleToWidth || b.getHeight() > scaleToHeight) && !noScale) {
            b.recycle();
            if (DEBUG) {
                Log.v(TAG, "Size changed! Reloading: "+name);
            }
            b = BitmapFactory.decodeResource(mContext.getResources(), id, options);
        } else {
            if (DEBUG) {
                Log.v(TAG, "Skipping, bitmap already loaded");
            }
            bitmap_updated = false;
        }
        // Do scaling only if original bitmap is bigger than needed dimensions
        if ((b.getWidth() > scaleToWidth || b.getHeight() > scaleToHeight) && !noScale) {
            if (DEBUG) {
                Log.v(TAG, "Size does not match! Scaling.");
            }
            Bitmap scaled = Bitmap.createScaledBitmap(b, scaleToWidth, scaleToHeight, false);
            b.recycle();
            b = scaled;
        }

        if (bitmap_updated) {
            mData.put(id, b);
        }

        return b;
    }

    public void clear() {
        for(int i = 0; i < mData.size(); i++) {
            Bitmap b = mData.valueAt(i);
            b.recycle();
        }
        mData.clear();
    }
}
