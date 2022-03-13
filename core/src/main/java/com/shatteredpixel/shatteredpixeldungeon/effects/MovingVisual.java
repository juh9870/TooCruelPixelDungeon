/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015 Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2021 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */

package com.shatteredpixel.shatteredpixeldungeon.effects;

import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.watabou.noosa.Game;
import com.watabou.noosa.Image;
import com.watabou.noosa.TextureFilm;
import com.watabou.utils.GameMath;
import com.watabou.utils.Misc;
import com.watabou.utils.PointF;
import com.watabou.utils.function.Supplier;

import java.util.HashMap;

public class MovingVisual extends Image {

	public static final int SIZE = 16;

	private enum Phase {
		FADE_IN, STATIC, FADE_OUT
	}

	public static final float FADE_IN_TIME = 0.2f;
	public static final float STATIC_TIME = 0.8f;
	public static final float FADE_OUT_TIME = 0.4f;

	private static TextureFilm film;

	private Supplier<PointF> target;

	private Phase phase;
	private float duration;
	private float passed;

	private float[] durations;

	private PointF initialOffset = new PointF( 0, 0 );
	private PointF targetOffset = initialOffset.clone();
	private float offsetFactor = 0;
	private PointF scaleMult = new PointF( 1, 1 );

	public void reset( Image image ) {
		copy( image );
		origin.set( width / 2, height / 2 );
		PointF targetP = target.get();
		if ( targetP != null ) {
			x = targetP.x - width / 2 + initialOffset.x;
			y = targetP.y + initialOffset.y;
		}
		offsetFactor = 0;

		phase = Phase.FADE_IN;

		duration = durations[0];
		passed = 0;
	}

	@Override
	public void update() {
		super.update();

		PointF targetP = target.get();
		if ( targetP != null ) {
			PointF offset = PointF.interpolate( initialOffset, targetOffset, offsetFactor );
			x = targetP.x - width / 2 + offset.x;
			y = targetP.y + offset.y;
		}

		if ( phase == null ) {
			return;
		}

		float progress;
		if ( passed == 0 && duration == 0 ) {
			progress = 1;
		} else {
			progress = GameMath.gate( 0, passed / duration, 1 );
		}

		switch (phase) {
			case FADE_IN:
				alpha( progress );
				scale.set( progress ).scale( scaleMult );
				break;
			case STATIC:
				break;
			case FADE_OUT:
				alpha( 1 - progress );
				offsetFactor = progress;
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

	public static void show( Supplier<PointF> target, Image image, float angle ) {
		show( target, image, angle );
	}

	public static void show( Supplier<PointF> target, Image image, float angle, float[] durations, PointF initialOffset, PointF finalOffset, PointF scaleMult ) {

		MovingVisual sprite = GameScene.movingVisual();
		sprite.target = target;
		sprite.durations = Misc.or( durations, new float[]{FADE_IN_TIME, STATIC_TIME, FADE_OUT_TIME} );
		sprite.initialOffset = Misc.or( initialOffset, new PointF( 0, 0 ) );
		sprite.targetOffset = Misc.or( finalOffset, new PointF( 0, 0 ) );
		sprite.scaleMult = Misc.or( scaleMult, new PointF( 1, 1 ) );
		sprite.angle = angle;
		sprite.reset( image );
		sprite.revive();
	}

	public static Supplier<PointF> fromChar( Char ch ) {
		return () -> ch.sprite == null ? null : ch.sprite.center();
	}
}
