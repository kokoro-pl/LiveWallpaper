package com.il.livewallpaper;

import android.graphics.Bitmap;

public class VerticalObject extends SwimmingObject {

	public VerticalObject(Bitmap spriteBitmap, int fps, int frameCount) {
		super(spriteBitmap, fps, frameCount);
		setVisibility(LEFT_INVISIBLE|TOP_INVISIBLE|RIGHT_INVISIBLE);
	}

	@Override
	public void resetSwimmingObject(float translateX, int displayWidth,
			int displayHeight) {
		super.resetSwimmingObject(translateX, displayWidth, displayHeight);
		int x = (int) (Math.random()*(displayWidth-getWidth()-translateX));
		int y = (int) (displayHeight);
		setYPos(y);
		setXPos(x);
	}
	
	@Override
	protected void swim() {
		mPosition.x += mSpeed*Math.sin(Math.PI/(180/mAngle));
		mPosition.y -= mSpeed*Math.cos(Math.PI/(180/mAngle));
	}
}
