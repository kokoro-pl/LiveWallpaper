package com.il.livewallpaper;

import android.graphics.Bitmap;
import android.graphics.Point;

public class RightObject extends SwimmingObject {

	public RightObject(Bitmap spriteBitmap, int fps, int frameCount, String type) {
		super(spriteBitmap, fps, frameCount, type);
		setVisibility(RIGHT_INVISIBLE|TOP_INVISIBLE|BOTTOM_INVISIBLE);
	}

	@Override
	public void resetSwimmingObject(float translateX, int displayWidth,
			int displayHeight) {
		super.resetSwimmingObject(translateX, displayWidth, displayHeight);
		int y = (int) (Math.random()*(displayHeight-getHeight()));
    	setYPos(y);
		int x = (int) (-getWidth()-translateX);
		setXPos(x);
	}

	@Override
	protected void swim() {
		mPosition.x += mSpeed*Math.cos(Math.PI/(180/mAngle));
		mPosition.y += mSpeed*Math.sin(Math.PI/(180/mAngle));
	}

}
