package com.il.livewallpaper;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Point;
import android.graphics.Rect;

public abstract class SwimmingObject {

	private final String mType;
	public final static String TYPE_VERTICAL_FRONT = "vert_f";
	public final static String TYPE_VERTICAL_BACK = "vert_b";
	public final static String TYPE_RIGHT_BACK = "right_b";
	public final static String TYPE_RIGHT_FRONT = "right_f";
	public final static String TYPE_LEFT_BACK = "left_b";
	public final static String TYPE_LEFT_FRONT = "left_f";

	/**
	 * Private fields
	 */
	private final Bitmap mCurrentSpriteBitmap;
	private Rect mDrawRect = new Rect(0,0,0,0);
	private final int mFps;
	private final int mNoOfFrames;
	private int mCurrentFrame = 0;
	private long mTimer = 0;
	private final int mSpriteWidth;
	private final int mSpriteHeight;
	protected Point mPosition = new Point(0,0);
	protected int mAngle = 0;
	protected int mSpeed = 0;
	private int mNeededVisibility;

	public SwimmingObject(Bitmap spriteBitmap, int fps, int frameCount, String type) {
		mCurrentSpriteBitmap = spriteBitmap;
		mSpriteHeight = spriteBitmap.getHeight();
		mSpriteWidth = spriteBitmap.getWidth() / frameCount;
		mDrawRect = new Rect(0,0, mSpriteWidth, mSpriteHeight);
		mFps = 1000 / fps;
		mNoOfFrames = frameCount;
		mAngle = (int) (Math.random()*60-30);
		mSpeed = (int) (Math.random()*5+5);
		mType = type;
	}

	public String getType() {
		return mType;
	}

	private void update(long currentTime) {
		if(currentTime > mTimer + mFps ) {
			mTimer = currentTime;
			mCurrentFrame +=1;

			if(mCurrentFrame >= mNoOfFrames) {
				mCurrentFrame = 0;
			}
		}

		mDrawRect.left = mCurrentFrame * mSpriteWidth;
		mDrawRect.right = mDrawRect.left + mSpriteWidth;
	}

	public void render(Canvas canvas, long currentTime) {

		update(currentTime);

		Rect dest = new Rect(getXPos(), getYPos(), getXPos() + mSpriteWidth,
				getYPos() + mSpriteHeight);

		swim();

		canvas.drawBitmap(mCurrentSpriteBitmap, mDrawRect, dest, null);
	}

	protected abstract void swim();

	public Point getPosition() {
		return mPosition;
	}

	public void setPosition(Point position) {
		mPosition = position;
	}

	public int getYPos() {
		return mPosition.y;
	}

	public int getXPos() {
		return mPosition.x;
	}

	public void setYPos(int y) {
		mPosition.y = y;
	}

	public void setXPos(int x) {
		mPosition.x = x;
	}

	public int getWidth(){
		return mSpriteWidth;
	}

	public int getHeight(){
		return mSpriteHeight;
	}

	public void resetSwimmingObjectIfNeeded(float translateX,
			int displayWidth, int displayHeight) {
		if ((isVisible(translateX, displayWidth, displayHeight) & mNeededVisibility) != 0) {
			resetSwimmingObject(translateX, displayWidth, displayHeight);
		}
	}

	public void resetSwimmingObject(float translateX, int displayWidth, int displayHeight) {
		mAngle = (int) (Math.random()*60-30);
		if (mAngle == 0) {
			mAngle = 1;
		}
		mSpeed = (int) (Math.random()*5+5);
	}

	protected void setVisibility(int v) {
		mNeededVisibility = v;
	}

	protected int VISIBLE = 0x0;
	protected int RIGHT_INVISIBLE = 0x0001;
	protected int LEFT_INVISIBLE = 0x0002;
	protected int TOP_INVISIBLE = 0x0004;
	protected int BOTTOM_INVISIBLE = 0x0008;

	public int isVisible(float translateX, int displayWidth, int displayHeight) {
		if (getXPos() >= displayWidth-translateX) {
			return RIGHT_INVISIBLE; // right
		} else if (getXPos()+getWidth() <= -translateX) {
			return LEFT_INVISIBLE; // left
		} else if (getYPos() + getHeight() <= 0) {
			return TOP_INVISIBLE;
		} else if (getYPos() > displayHeight) {
			return BOTTOM_INVISIBLE;
		}

		return VISIBLE;
	}
}