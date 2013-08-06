package com.il.livewallpaper;

import java.util.ArrayList;
import java.util.Random;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.service.wallpaper.WallpaperService;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;

public class LiveWallpaperService extends WallpaperService implements SharedPreferences.OnSharedPreferenceChangeListener {
    private MyEngine mEngine;

    @Override
    public void onCreate() {
        super.onCreate();
        getSharedPreferences(getPackageName(), Context.MODE_PRIVATE).registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public Engine onCreateEngine() {
        mEngine = new MyEngine();
        return mEngine;
    }

    /**
     * Настройки
     *
     * @param sharedPreferences
     * @param s
     */
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        Random rand = new Random();
        int num;
        int mRnd;

        if (!sharedPreferences.getBoolean(LiveWallpaperSettings.FON_ENABLED_CHECKBOX_KEY, true)) {
            mEngine.enableFon( sharedPreferences.getBoolean(LiveWallpaperSettings.FON_ENABLED_CHECKBOX_KEY, true) );
        }

        if (!sharedPreferences.getBoolean(LiveWallpaperSettings.V_ENABLED_CHECKBOX_KEY, true)) {
            mEngine.setObjectsCount(0, SwimmingObject.TYPE_VERTICAL_FRONT);
            mEngine.setObjectsCount(0, SwimmingObject.TYPE_VERTICAL_BACK);
        } else {
            num = sharedPreferences.getInt(LiveWallpaperSettings.VF_COUNT_LIST_KEY, 4);
            mEngine.setObjectsCount(num, SwimmingObject.TYPE_VERTICAL_FRONT);

            num = sharedPreferences.getInt(LiveWallpaperSettings.VB_COUNT_LIST_KEY, 4);
            mEngine.setObjectsCount(num, SwimmingObject.TYPE_VERTICAL_BACK);
        }

        if (!sharedPreferences.getBoolean(LiveWallpaperSettings.H_ENABLED_CHECKBOX_KEY, true)) {
            mEngine.setObjectsCount(0, SwimmingObject.TYPE_LEFT_BACK);
            mEngine.setObjectsCount(0, SwimmingObject.TYPE_RIGHT_BACK);
            mEngine.setObjectsCount(0, SwimmingObject.TYPE_LEFT_FRONT);
            mEngine.setObjectsCount(0, SwimmingObject.TYPE_RIGHT_FRONT);
        } else {
            num = sharedPreferences.getInt(LiveWallpaperSettings.HB_COUNT_LIST_KEY, 8);
            mRnd = rand.nextInt(num) + 1;
            mEngine.setObjectsCount(mRnd, SwimmingObject.TYPE_LEFT_BACK);
            mEngine.setObjectsCount(num - mRnd, SwimmingObject.TYPE_RIGHT_BACK);

            num = sharedPreferences.getInt(LiveWallpaperSettings.HF_COUNT_LIST_KEY, 8);
            mRnd = rand.nextInt(num) + 1;
            mEngine.setObjectsCount(mRnd, SwimmingObject.TYPE_LEFT_FRONT);
            mEngine.setObjectsCount(num - mRnd, SwimmingObject.TYPE_RIGHT_FRONT);
        }

        if (!sharedPreferences.getBoolean(LiveWallpaperSettings.H_TOUCH_ENABLED_CHECKBOX_KEY, true)) {
            //@todo: реализовать взрывающиеся объекты
        }
    }

    class MyEngine extends Engine implements Renderer.RendererCallback {
        SurfaceHolder mSurfaceHolder;
        Renderer mRenderer;
        ArrayList<SwimmingObject> swimmingObjects = new ArrayList<SwimmingObject>();
        Bitmap backgrouns[] = new Bitmap[3];
        float mOffset;
        private int mDisplayWidth;
        private int mDisplayHeight;
        private boolean mShouldResetSwimmingObject;
        String mLock = "render";
        private boolean mPaused;
        int mObjectFps = 12;

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                                     float xOffsetStep, float yOffsetStep, int xPixelOffset,
                                     int yPixelOffset) {
            mOffset = xOffset;
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
                    xPixelOffset, yPixelOffset);
        }

        public MyEngine() {
            super();
            DisplayMetrics dm = getResources().getDisplayMetrics();
            mDisplayWidth = dm.widthPixels;
            mDisplayHeight = dm.heightPixels;

            addGeneratedSwimingObjects(SwimmingObject.TYPE_RIGHT_BACK, 4);
            addGeneratedSwimingObjects(SwimmingObject.TYPE_LEFT_BACK, 4);
            addGeneratedSwimingObjects(SwimmingObject.TYPE_VERTICAL_BACK, 4);
            addGeneratedSwimingObjects(SwimmingObject.TYPE_RIGHT_FRONT, 4);
            addGeneratedSwimingObjects(SwimmingObject.TYPE_LEFT_FRONT, 4);
            addGeneratedSwimingObjects(SwimmingObject.TYPE_VERTICAL_FRONT, 4);

            backgrouns[0] = decodeSampledBitmapFromResource(getResources(), R.drawable.back);
            backgrouns[1] = decodeSampledBitmapFromResource(getResources(), R.drawable.med);
            backgrouns[2] = decodeSampledBitmapFromResource(getResources(), R.drawable.front);

        }

        /**
         * Добавляем плавающие объекты
         *
         * @param mImgGroupName
         * @param mObjectCount
         */
        private void addGeneratedSwimingObjects(String mImgGroupName, int mObjectCount) {
            Random rand = new Random();
            int mRnd = rand.nextInt(mObjectCount) + 1;
            for (int i = 1; i <= mRnd; i++) {
                int resId = getResources().getIdentifier(mImgGroupName + i, "drawable", getPackageName());
                if (resId == 0) break;
                if (mImgGroupName.equals(SwimmingObject.TYPE_RIGHT_BACK)) {
                    swimmingObjects.add(new RightObject(BitmapFactory.decodeResource(getResources(), resId), mObjectFps, 6, SwimmingObject.TYPE_RIGHT_BACK));
                } else if (mImgGroupName.equals(SwimmingObject.TYPE_LEFT_BACK)) {
                    swimmingObjects.add(new LeftObject(BitmapFactory.decodeResource(getResources(), resId), mObjectFps, 6, SwimmingObject.TYPE_LEFT_BACK));
                } else if (mImgGroupName.equals(SwimmingObject.TYPE_VERTICAL_BACK)) {
                    swimmingObjects.add(new VerticalObject(BitmapFactory.decodeResource(getResources(), resId), mObjectFps, 4, SwimmingObject.TYPE_VERTICAL_BACK));
                } else if (mImgGroupName.equals(SwimmingObject.TYPE_RIGHT_FRONT)) {
                    swimmingObjects.add(new RightObject(BitmapFactory.decodeResource(getResources(), resId), mObjectFps, 6, SwimmingObject.TYPE_RIGHT_FRONT));
                } else if (mImgGroupName.equals(SwimmingObject.TYPE_LEFT_FRONT)) {
                    swimmingObjects.add(new LeftObject(BitmapFactory.decodeResource(getResources(), resId), mObjectFps, 6, SwimmingObject.TYPE_LEFT_FRONT));
                } else if (mImgGroupName.equals(SwimmingObject.TYPE_VERTICAL_FRONT)) {
                    swimmingObjects.add(new VerticalObject(BitmapFactory.decodeResource(getResources(), resId), mObjectFps, 4, SwimmingObject.TYPE_VERTICAL_FRONT));
                }
            }
        }

        float getScaleDimension(BitmapFactory.Options options, int pw, int ph) {
            float p = pw / ph;
            float scaleTo = mDisplayHeight / (float) ph;
            //			Log.i("IL", pw+" "+ph+" "+scaleTo);
            if (pw * scaleTo < mDisplayWidth * 2) {
                scaleTo = mDisplayWidth * 2 / (float) pw;
            }

            return scaleTo;
        }

        public Bitmap decodeSampledBitmapFromResource(Resources res, int resId) {

            // First decode with inJustDecodeBounds=true to check dimensions
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(res, resId, options);

            // Raw height and width of image
            final int height = options.outHeight;
            final int width = options.outWidth;
            int inSampleSize = 1;

            float scaleTo = getScaleDimension(options, width, height);
            int reqWidth = (int) (width * scaleTo);
            int reqHeight = (int) (height * scaleTo);

            if (height > reqHeight || width > reqWidth) {
                int heightRatio = Math.round((float) height / (float) reqHeight);
                int widthRatio = Math.round((float) width / (float) reqWidth);
                inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
            }
            options.inSampleSize = inSampleSize;

            // Decode bitmap with inSampleSize set
            options.inJustDecodeBounds = false;
            options.inDither = false;
            options.inPurgeable = true;
            Bitmap roughBitmap = BitmapFactory.decodeResource(res, resId, options);

            Bitmap scaledBitmap;
            while (true) {
                try {
                    scaledBitmap = Bitmap.createScaledBitmap(roughBitmap, reqWidth, reqHeight, true);
                    break;
                } catch (OutOfMemoryError e) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e2) {
                        e.printStackTrace();
                    }
                }
            }
            roughBitmap.recycle();
            roughBitmap = null;

            return scaledBitmap;
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            mPaused = false;
            Log.i("IL", "onVisibilityChanged(" + visible + " " + this);
            if (visible) {
                if (mRenderer != null && mRenderer.isStopping()) {
                    try {
                        mRenderer.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                if (mRenderer == null || !mRenderer.isRunning()) {
                    initThread();
                    mRenderer.switchOn();
                }
            } else {
                mPaused = true;
                if (mRenderer != null && mRenderer.isRunning()) {
                    mRenderer.switchOff();
                    try {
                        mRenderer.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            super.onVisibilityChanged(visible);
        }

        public void initThread() {
            mRenderer = new Renderer();
            mRenderer.setCallback(this);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                                     int width, int height) {

            super.onSurfaceChanged(holder, format, width, height);
            //Log.i("IL", "onSurfaceChanged "+this);
            mSurfaceHolder = holder;
            mShouldResetSwimmingObject = true;
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            Log.i("IL", "onSurfaceCreated " + this);
            mSurfaceHolder = holder;
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            Log.v("IL", "onSurfaceDestroyed");
            mPaused = true;
        }

        public void enableFon( Boolean isEnable )
        {
         /*   synchronized (mSurfaceHolder) {
            if( isEnable )
            {
                backgrouns[0] = decodeSampledBitmapFromResource(getResources(), R.drawable.back);
                backgrouns[1] = decodeSampledBitmapFromResource(getResources(), R.drawable.med);
                backgrouns[2] = decodeSampledBitmapFromResource(getResources(), R.drawable.front);
            }
            else
            {
                backgrouns[0] = null;
                backgrouns[1] = null;
                backgrouns[2] = null;
            }
            }*/

        }

        /**
         * Устанавливаем количество объектов
         *
         * @param count
         * @param type
         */
        public void setObjectsCount(int count, String type) {
            synchronized (mSurfaceHolder) {
                ArrayList<SwimmingObject> old_objects = new ArrayList<SwimmingObject>();
                for (SwimmingObject o : swimmingObjects) {
                    if (o.getType() == type) {
                        old_objects.add(o);
                    }
                }
                if (count == old_objects.size()) {
                    return;
                }
                swimmingObjects.removeAll(old_objects);
                if (count > old_objects.size()) {
                    addGeneratedSwimingObjects(type, count - old_objects.size());
                } else if (count < old_objects.size()) {
                    while (count != old_objects.size()) {
                        old_objects.remove(0);
                    }
                }
                swimmingObjects.addAll(old_objects);
                Log.i("IL", "=" + count);
            }
        }

        @Override
        public void render() {

            //Log.i("IL", "render "+ this);
            if (mSurfaceHolder == null) {
                return;
            }
            Canvas canvas = null;
            synchronized (mSurfaceHolder) {
                try {
                    canvas = mSurfaceHolder.lockCanvas(null);
                    if (canvas == null) {
                        return;
                    }
                    //		            Log.i("IL", canvas.getWidth()+" "+canvas.getHeight());
                    canvas.drawRGB(0, 0, 0);
                    if (backgrouns[0] != null) {
                        canvas.save();
                        canvas.translate(-mDisplayWidth * mOffset, 0);
                        canvas.drawBitmap(backgrouns[0], 0, 0, null);
                        canvas.restore();
                    }
                    if (backgrouns[1] != null) {
                        canvas.save();
                        canvas.translate(mDisplayWidth * (mOffset - 1f), 0);
                        canvas.drawBitmap(backgrouns[1], 0, 0, null);
                        canvas.restore();
                    }
                    if (backgrouns[2] != null) {
                        canvas.save();
                        canvas.translate(-mDisplayWidth * mOffset, 0);
                        canvas.drawBitmap(backgrouns[2], 0, 0, null);
                        canvas.restore();
                    }

                    float translateX = -(mDisplayWidth * 2) * mOffset;
                    if (mShouldResetSwimmingObject) {
                        for (SwimmingObject o : swimmingObjects) {
                            o.resetSwimmingObject(translateX, mDisplayWidth, mDisplayHeight);
                        }
                        mShouldResetSwimmingObject = false;
                    }
                    canvas.translate(translateX, 0);
                    long curTime = System.currentTimeMillis();
                    for (SwimmingObject o : swimmingObjects) {
                        o.render(canvas, curTime);
                        o.resetSwimmingObjectIfNeeded(translateX, mDisplayWidth, mDisplayHeight);
                    }
                    canvas.restore();

                } finally {
                    if (canvas != null) {
                        try {
                            mSurfaceHolder.unlockCanvasAndPost(canvas);
                        } catch (Exception e) {
                            Log.e("IL", "error: " + e);
                        }
                    }
                }
            }
        }
    }

}
