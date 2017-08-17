/**
 * 
 */
package com.android.volley.display;

import android.graphics.Bitmap;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * @author Mr.Yang
 *
 */
public class FadeInBitmapDisplayer implements BitmapDisplayer {

	private final int durationMillis;

	/**
	 * @param durationMillis Duration of "fade-in" animation (in milliseconds)
	 */
	public FadeInBitmapDisplayer(int durationMillis) {
		this.durationMillis = durationMillis;
	}
	
	/* (non-Javadoc)
	 * @see com.android.volley.toolbox.BitmapDisplayer#display(android.graphics.Bitmap, android.widget.ImageView)
	 */
	@Override
	public void display(Bitmap bitmap, ImageView imageView) {
		imageView.setImageBitmap(bitmap);
		animate(imageView, durationMillis);
	}

	/**
	 * Animates {@link ImageView} with "fade-in" effect
	 *
	 * @param imageView      {@link ImageView} which display image in
	 * @param durationMillis The length of the animation in milliseconds
	 */
	public static void animate(View imageView, int durationMillis) {
		if (imageView != null) {
			AlphaAnimation fadeImage = new AlphaAnimation(0, 1);
			fadeImage.setDuration(durationMillis);
			fadeImage.setInterpolator(new DecelerateInterpolator());
			imageView.startAnimation(fadeImage);
		}
	}

}
