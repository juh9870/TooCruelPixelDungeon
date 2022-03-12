package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.watabou.noosa.Game;
import com.watabou.noosa.Image;

public class FadingImage extends Image {
	private enum Phase {
		FADE_IN, STATIC, FADE_OUT
	}

	public static final float FADE_IN_TIME = 0.2f;
	public static final float STATIC_TIME = 0.8f;
	public static final float FADE_OUT_TIME = 0.4f;
	private Phase phase;
	private float duration;
	private float passed;

	private float[] durations;

	public void reset( Image image ) {
		copy( image );
		origin.set( width / 2, height / 2 );

		phase = Phase.FADE_IN;

		duration = durations[0];
		passed = 0;
	}

	@Override
	public void update() {
		super.update();

		if ( phase == null ) {
			return;
		}

		switch (phase) {
			case FADE_IN:
				alpha( passed / duration );
				scale.set( passed / duration );
				break;
			case STATIC:
				break;
			case FADE_OUT:
				alpha( 1 - passed / duration );
				break;
		}

		if ( (passed += Game.elapsed) > duration ) {
			switch (phase) {
				case FADE_IN:
					phase = Phase.STATIC;
					duration = durations[1];
					break;
				case STATIC:
					phase = Phase.FADE_OUT;
					duration = durations[2];
					break;
				case FADE_OUT:
					kill();
					break;
			}

			passed = 0;
		}
	}

	public void show( Image image ) {
		durations = new float[]{FADE_IN_TIME, STATIC_TIME, FADE_OUT_TIME};
		reset( image );
		revive();
	}

	public void show( Image image, float fadeIn, float fadeStatic, float fadeOut ) {
		durations = new float[]{fadeIn, fadeStatic, fadeOut};
		reset( image );
		revive();
	}
}
