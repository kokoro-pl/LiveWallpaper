package com.il.livewallpaper;

import java.util.ArrayList;
import java.util.Random;

import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

public class LiveWallpaperService extends WallpaperService {

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public Engine onCreateEngine() {
        return new MyEngine();
    }

    class MyEngine extends Engine implements Renderer.RendererCallback, SharedPreferences.OnSharedPreferenceChangeListener {
        private final String TAG = MyEngine.class.getSimpleName();
        private final boolean DEBUG = true;
        private SurfaceHolder mSurfaceHolder;
        private final Object mHolderLock = new Object();
        private Renderer mRenderer;
        private ArrayList<SwimmingObject> mSwimmingObjects = new ArrayList<SwimmingObject>();
        private Bitmap backgrounds[] = new Bitmap[3];

        // Actual offset
        private float mOffset;
        // Offset that will be used for drawing
        private float mDrawOffset;
        // Onetime, first offset change should not create animation
        private boolean mInitDrawOffsetOnce = true;

        private int mSurfaceWidth;
        private int mSurfaceHeight;

        private int mObjectFps = 12;
        private Rect mDestinationBackgroundRect = new Rect();
        private Rect mSourceBackgroundRect = new Rect();
        private boolean mDrawBackground;

        @Override
        public void onOffsetsChanged(float xOffset, float yOffset,
                float xOffsetStep, float yOffsetStep,
                int xPixelOffset, int yPixelOffset) {
            mOffset = xOffset;
            if (mInitDrawOffsetOnce) {
                mInitDrawOffsetOnce = false;
                mDrawOffset = xOffset;
            }
            super.onOffsetsChanged(xOffset, yOffset, xOffsetStep, yOffsetStep,
                    xPixelOffset, yPixelOffset);
        }

        public MyEngine() {
            addGeneratedSwimingObjects(SwimmingObject.TYPE_RIGHT_BACK, 1);
            addGeneratedSwimingObjects(SwimmingObject.TYPE_LEFT_BACK, 1);
            addGeneratedSwimingObjects(SwimmingObject.TYPE_VERTICAL_BACK, 4);
            addGeneratedSwimingObjects(SwimmingObject.TYPE_RIGHT_FRONT, 1);
            addGeneratedSwimingObjects(SwimmingObject.TYPE_LEFT_FRONT, 1);
            addGeneratedSwimingObjects(SwimmingObject.TYPE_VERTICAL_FRONT, 4);
        }

        /**
         * Добавляем плавающие объекты
         *
         * @param imgGroupName
         * @param objectCount
         */
        private void addGeneratedSwimingObjects(String imgGroupName, int objectCount) {
            Random rand = new Random();
            int mRnd = rand.nextInt(objectCount) + 1;
            BitmapManager bm = BitmapManager.getInstance(LiveWallpaperService.this);
            for (int i = 1; i <= mRnd; i++) {
                int resId = getResources().getIdentifier(imgGroupName + i, "drawable", getPackageName());
                if (resId == 0) break;
                if (imgGroupName.equals(SwimmingObject.TYPE_RIGHT_BACK)) {
                    mSwimmingObjects.add(new RightObject(bm.getBitmap(resId),
                            mObjectFps, 6, SwimmingObject.TYPE_RIGHT_BACK));
                } else if (imgGroupName.equals(SwimmingObject.TYPE_LEFT_BACK)) {
                    mSwimmingObjects.add(new LeftObject(bm.getBitmap(resId),
                            mObjectFps, 6, SwimmingObject.TYPE_LEFT_BACK));
                } else if (imgGroupName.equals(SwimmingObject.TYPE_VERTICAL_BACK)) {
                    mSwimmingObjects.add(new VerticalObject(bm.getBitmap(resId),
                            mObjectFps, 4, SwimmingObject.TYPE_VERTICAL_BACK));
                } else if (imgGroupName.equals(SwimmingObject.TYPE_RIGHT_FRONT)) {
                    mSwimmingObjects.add(new RightObject(bm.getBitmap(resId),
                            mObjectFps, 6, SwimmingObject.TYPE_RIGHT_FRONT));
                } else if (imgGroupName.equals(SwimmingObject.TYPE_LEFT_FRONT)) {
                    mSwimmingObjects.add(new LeftObject(bm.getBitmap(resId),
                            mObjectFps, 6, SwimmingObject.TYPE_LEFT_FRONT));
                } else if (imgGroupName.equals(SwimmingObject.TYPE_VERTICAL_FRONT)) {
                    mSwimmingObjects.add(new VerticalObject(bm.getBitmap(resId),
                            mObjectFps, 4, SwimmingObject.TYPE_VERTICAL_FRONT));
                }
            }
        }

        float getScaleDimension(int pw, int ph) {
            float scaleTo = mSurfaceHeight / (float) ph;
            if (DEBUG) Log.v(TAG, pw+" "+ph+" "+scaleTo);
            if (pw * scaleTo < mSurfaceWidth * 2) {
                scaleTo = mSurfaceWidth * 2 / (float) pw;
            }

            return scaleTo;
        }

        public Bitmap getBackgroundBitmap(Resources res, int resId) {
            final BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            options.inScaled = false;
            BitmapFactory.decodeResource(res, resId, options);

            float scaleTo = getScaleDimension(options.outWidth, options.outHeight);
            final int reqWidth = (int) (options.outWidth * scaleTo);
            final int reqHeight = (int) (options.outHeight * scaleTo);

            final BitmapManager bm = BitmapManager.getInstance(LiveWallpaperService.this);

            return bm.getBitmap(resId, reqWidth, reqHeight);
        }

        @Override
        public void onVisibilityChanged(boolean visible) {
            if (DEBUG) Log.v(TAG, "onVisibilityChanged(): " + visible);
            if (visible) {
                // Crash here in that case. It better to fix logic instead of making any workaround.
                if (DEBUG && mRenderer != null && mRenderer.isRunning()) {
                    throw new IllegalStateException("Thread was not stopped!");
                }
                if (mRenderer != null && mRenderer.isStopping()) {
                    try {
                        mRenderer.join();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                initThread();
                mRenderer.switchOn();
            } else {
                if (mRenderer != null && mRenderer.isRunning()) {
                    mRenderer.switchOff();
                }
            }
            super.onVisibilityChanged(visible);
        }

        public void initThread() {
            mRenderer = new Renderer(this);
        }

        @Override
        public void onSurfaceChanged(SurfaceHolder holder, int format,
                int width, int height) {
            super.onSurfaceChanged(holder, format, width, height);

            if (DEBUG) Log.i(TAG, "onSurfaceChanged()");
            mSurfaceHolder = holder;
            mSurfaceWidth = width;
            mSurfaceHeight = height;

            if (mDrawBackground) {
                loadBackground();
            }

            final float translateX = -(mSurfaceWidth * 2) * mDrawOffset;
            for (SwimmingObject o : mSwimmingObjects) {
                o.resetSwimmingObject(translateX, mSurfaceWidth, mSurfaceHeight);
            }
        }

        private void loadBackground() {
            backgrounds[0] = getBackgroundBitmap(getResources(), R.drawable.back);
            backgrounds[1] = getBackgroundBitmap(getResources(), R.drawable.med);
            backgrounds[2] = getBackgroundBitmap(getResources(), R.drawable.front);

            mDestinationBackgroundRect.set(0, 0, mSurfaceWidth * 2, mSurfaceHeight);
        }

        @Override
        public void onSurfaceCreated(SurfaceHolder holder) {
            super.onSurfaceCreated(holder);
            if (DEBUG) Log.i(TAG, "onSurfaceCreated()");
            mSurfaceHolder = holder;

            SharedPreferences sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
            sp.registerOnSharedPreferenceChangeListener(this);
            onSharedPreferenceChanged(sp, null);
        }

        @Override
        public void onSurfaceDestroyed(SurfaceHolder holder) {
            super.onSurfaceDestroyed(holder);
            if (DEBUG) Log.v(TAG, "onSurfaceDestroyed()");
            mSurfaceHolder = null;

            SharedPreferences sp = getSharedPreferences(getPackageName(), MODE_PRIVATE);
            sp.unregisterOnSharedPreferenceChangeListener(this);
        }

        public void setBackgroundEnabled(Boolean isEnable)
        {
            if (DEBUG) Log.v(TAG, "setBackgroundEnabled: "+isEnable);

            mDrawBackground = isEnable;
            if (isEnable && mSurfaceHolder != null && !mSurfaceHolder.isCreating()) {
                loadBackground();
            }
        }

        /**
         * Устанавливаем количество объектов
         *
         * @param count
         * @param type
         */
        public void setObjectsCount(int count, String type) {
            synchronized (mHolderLock) {
                ArrayList<SwimmingObject> old_objects = new ArrayList<SwimmingObject>();
                for (SwimmingObject o : mSwimmingObjects) {
                    if (o.getType().equals(type)) {
                        old_objects.add(o);
                    }
                }
                if (DEBUG) Log.v(TAG, "setObjectsCount: count = " + count
                        + ", old_objects.size() = " + old_objects.size());
                if (count == old_objects.size()) {
                    return;
                }
                if (count > old_objects.size()) {
                    addGeneratedSwimingObjects(type, count - old_objects.size());
                } else if (count < old_objects.size()) {
                    while (count != old_objects.size()) {
                        mSwimmingObjects.remove(old_objects.remove(0));
                    }
                }
            }
        }

        @Override
        public void render() {
            if (mSurfaceHolder == null) {
                Log.e(TAG, "render was called when surface is not ready!");
                return;
            }
            if (DEBUG) Log.v(TAG, "render start: "+ System.currentTimeMillis());

            Canvas canvas = null;
            synchronized (mHolderLock) {
                try {
                    canvas = mSurfaceHolder.lockCanvas();
                    if (canvas == null) {
                        Log.w(TAG, "render: lockCanvas returned null");
                        return;
                    }
                    canvas.drawRGB(0, 0, 0);

                    // Smoothing movements
                    final float diff = mOffset - mDrawOffset;
                    if (diff != 0f) {
                        mDrawOffset += diff / 3f;
                    }
                    if (mDrawBackground) {
                        if (backgrounds[0] != null) {
                            final int xStart = (int) (mSurfaceWidth * mDrawOffset);
                            mSourceBackgroundRect.set(xStart, 0, xStart + mSurfaceWidth, backgrounds[0].getHeight());
                            canvas.drawBitmap(backgrounds[0], mSourceBackgroundRect, mDestinationBackgroundRect, null);
                        }
                        if (backgrounds[1] != null) {
                            final int xStart = (int) (mSurfaceWidth * (1f - mDrawOffset));
                            mSourceBackgroundRect.set(xStart, 0, xStart + mSurfaceWidth, backgrounds[0].getHeight());
                            canvas.drawBitmap(backgrounds[1], mSourceBackgroundRect, mDestinationBackgroundRect, null);
                        }
                        if (backgrounds[2] != null) {
                            final int xStart = (int) (mSurfaceWidth * mDrawOffset);
                            mSourceBackgroundRect.set(xStart, 0, xStart + mSurfaceWidth, backgrounds[0].getHeight());
                            canvas.drawBitmap(backgrounds[2], mSourceBackgroundRect, mDestinationBackgroundRect, null);
                        }
                    }

                    final float translateX = -(mSurfaceWidth * 2) * mDrawOffset;
                    canvas.save();
                    canvas.translate(translateX, 0);
                    long curTime = System.currentTimeMillis();
                    for (SwimmingObject o : mSwimmingObjects) {
                        o.render(canvas, curTime);
                        o.resetSwimmingObjectIfNeeded(translateX, mSurfaceWidth, mSurfaceHeight);
                    }
                    canvas.restore();
                } finally {
                    mSurfaceHolder.unlockCanvasAndPost(canvas);

                    if (DEBUG) Log.v(TAG, "render end: "+ System.currentTimeMillis());
                }
            }
        }

        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            Random rand = new Random();
            int num;
            int mRnd;

            setBackgroundEnabled(sharedPreferences.getBoolean(LiveWallpaperSettings.BACKGROUND_ENABLED_CHECKBOX_KEY, true));

            if (!sharedPreferences.getBoolean(LiveWallpaperSettings.V_ENABLED_CHECKBOX_KEY, true)) {
                setObjectsCount(0, SwimmingObject.TYPE_VERTICAL_FRONT);
                setObjectsCount(0, SwimmingObject.TYPE_VERTICAL_BACK);
            } else {
                num = sharedPreferences.getInt(LiveWallpaperSettings.VF_COUNT_LIST_KEY, 4);
                setObjectsCount(num, SwimmingObject.TYPE_VERTICAL_FRONT);

                num = sharedPreferences.getInt(LiveWallpaperSettings.VB_COUNT_LIST_KEY, 4);
                setObjectsCount(num, SwimmingObject.TYPE_VERTICAL_BACK);
            }

            if (!sharedPreferences.getBoolean(LiveWallpaperSettings.H_ENABLED_CHECKBOX_KEY, true)) {
                setObjectsCount(0, SwimmingObject.TYPE_LEFT_BACK);
                setObjectsCount(0, SwimmingObject.TYPE_RIGHT_BACK);
                setObjectsCount(0, SwimmingObject.TYPE_LEFT_FRONT);
                setObjectsCount(0, SwimmingObject.TYPE_RIGHT_FRONT);
            } else {
                num = sharedPreferences.getInt(LiveWallpaperSettings.HB_COUNT_LIST_KEY, 2);
                /**
                 * FIXME: condition here was wrong. It cause 2 exceptions:
                 * 1. IllegalArgumentException when num is 0.
                 * 2. num - mRnd will be -1 if we try to use previous formula.
                 *
                 * Fix this according to specifications.
                 */
                mRnd = num > 0 ? rand.nextInt(num) : 0;
                setObjectsCount(mRnd, SwimmingObject.TYPE_LEFT_BACK);
                setObjectsCount(num - mRnd, SwimmingObject.TYPE_RIGHT_BACK);

                // FIXME: see comments above.
                num = sharedPreferences.getInt(LiveWallpaperSettings.HF_COUNT_LIST_KEY, 2);
                mRnd = num > 0 ? rand.nextInt(num) : 0;
                setObjectsCount(mRnd, SwimmingObject.TYPE_LEFT_FRONT);
                setObjectsCount(num - mRnd, SwimmingObject.TYPE_RIGHT_FRONT);
            }

            if (!sharedPreferences.getBoolean(LiveWallpaperSettings.VF_TOUCH_ENABLED_CHECKBOX_KEY, true)) {
                //@todo: реализовать взрывающиеся объекты
            }
        }
    }

}
