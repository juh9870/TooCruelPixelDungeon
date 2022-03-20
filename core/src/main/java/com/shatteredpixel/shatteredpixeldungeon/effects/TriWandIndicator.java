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

import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.tcpd.TriWand;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.ui.Icons;
import com.watabou.noosa.BitmapText;
import com.watabou.noosa.Game;
import com.watabou.noosa.Group;
import com.watabou.noosa.Image;
import com.watabou.noosa.Resizable;
import com.watabou.utils.GameMath;
import com.watabou.utils.PointF;
import com.watabou.utils.function.Supplier;

public class TriWandIndicator extends Group {

	private enum Phase {
		FADE_IN, STATIC, FADE_OUT
	}

	private static final int SIZE = 17;
	private static final int OFFSET = 2;
	private static final int TRIANGLES = 32;

	private static final float APPEAR_DURATION = 0.2f;
	private static final float FADE_DURATION = 0.2f;
	float progress;

	private final CircleArc neutralEffect;
	private final CircleArc firstEffect;
	private final CircleArc secondEffect;
	private final BitmapText neutralText;
	private final BitmapText firstText;
	private final BitmapText secondText;
	private final Image overlay;
	private Supplier<PointF> target;
	private TriWand wand;
	private Phase phase;

	private float elapsed = 0;

	public TriWandIndicator() {
		neutralText = new BitmapText( PixelScene.pixelFont );
		firstText = new BitmapText( PixelScene.pixelFont );
		secondText = new BitmapText( PixelScene.pixelFont );

		neutralEffect = new CircleArc( TRIANGLES, SIZE / 2f );
		firstEffect = new CircleArc( TRIANGLES, SIZE / 2f );
		secondEffect = new CircleArc( TRIANGLES, SIZE / 2f );

		overlay = Icons.GOLDEN_RING.get();

		add( neutralText );
		add( firstText );
		add( secondText );

		add( neutralEffect );
		add( firstEffect );
		add( secondEffect );

		add( overlay );
	}

	private void arc( CircleArc arc, TriWand.WandEffect effect ) {
		arc.revive();
		int color = effect.indicatorColor();
		arc.alpha( 1 );
		arc.color( color, false );
	}

	private void reset( TriWand wand, Supplier<PointF> target ) {
		this.wand = wand;
		elapsed = 0;
		this.target = target;
		revive();

		neutralText.revive();
		firstText.revive();
		secondText.revive();

		arc( neutralEffect, wand.neutralEffect );
		arc( firstEffect, wand.firstEffect );
		arc( secondEffect, wand.secondEffect );
		overlay.revive();

		switchPhase( Phase.FADE_IN, false );
		moveToTarget();
	}

	private void sweep( float progress ) {
		progress = GameMath.gate( 0, progress, 1 );
		float angle = 0;
		float sweep = 0;
		float total = wand.weightFirst() + wand.weightSecond() + wand.weightNeutral();

		// Center neutral at bottom
		sweep = wand.weightNeutral() * progress / total * 360;
		angle = 180 - (sweep / 2);
		angle += sweep;
		// acts as background
		neutralEffect.setSweep( 1 );
		neutralEffect.angle = angle;

		sweep = wand.weightFirst() * progress / total * 360;
		angle += sweep;
		firstEffect.setSweep( sweep / 360 );
		firstEffect.angle = angle;

		sweep = wand.weightSecond() * progress / total * 360;
		angle += sweep;
		secondEffect.setSweep( sweep / 360 );
		secondEffect.angle = angle;
	}

	private void updateText( float progress ) {
		float total = wand.weightFirst() + wand.weightSecond() + wand.weightNeutral();
		float neutralProgress = wand.weightNeutral() * progress / total;
		showValue( neutralText, neutralProgress + (1 - neutralProgress) * (1 - progress) );
		showValue( firstText, wand.weightFirst() * progress / total );
		showValue( secondText, wand.weightSecond() * progress / total );
	}

	public void showValue( BitmapText text, float value ) {
		text.text( (int) Math.round( value * 100 ) + "%" );
	}

	protected void moveToTarget() {
		PointF center = target.get();

		if ( center != null ) {
			neutralEffect.point( center );
			firstEffect.point( center );
			secondEffect.point( center );
			overlay.point( PointF.diff( center, new PointF( overlay.width() / 2, overlay.height() / 2 ) ) );
			moveText( neutralText, 0 );
			moveText( firstText, 1 );
			moveText( secondText, 2 );
		}
	}

	private void moveText( BitmapText text, int line ) {
		text.x = overlay.x + overlay.width() + 1;
		text.y = overlay.y + overlay.height() / 3 * line;
	}

	private void scale( float mult ) {
		neutralText.scale.set( mult * 0.75f );
		firstText.scale.set( mult * 0.75f );
		secondText.scale.set( mult * 0.75f );
		neutralEffect.scale.set( mult );
		firstEffect.scale.set( mult );
		secondEffect.scale.set( mult );
		overlay.scale.set( mult );
	}

	@Override
	public void update() {
		super.update();
		if ( phase == Phase.FADE_IN ) {
			progress = GameMath.gate( 0, elapsed / APPEAR_DURATION, 1 );
			scale( progress );
			sweep( progress );
			updateText( progress );
			if ( progress >= 1 ) {
				switchPhase( Phase.STATIC, false );
			}
		} else if ( phase == Phase.FADE_OUT ) {
			progress = GameMath.gate( 0, elapsed / FADE_DURATION, 1 );
			scale( 1 - progress );
			sweep( 1 - progress );
			updateText( 1 - progress );
			if ( progress >= 1 ) {
				kill();
			}
		}
		moveToTarget();
		elapsed += Game.elapsed;
	}

	public void switchPhase( Phase phase, boolean keepProgress ) {
		this.phase = phase;
		elapsed = 0;
		switch (phase) {
			case FADE_IN:
				if ( keepProgress ) {
					elapsed = APPEAR_DURATION * progress;
				} else {
					progress = 0;
				}
				scale( progress );
				sweep( progress );
				updateText( progress );
				break;
			case STATIC:
			case FADE_OUT:
				if ( keepProgress ) {
					elapsed = FADE_DURATION * (1 - progress);
				} else {
					progress = 0;
				}
				scale( 1 - progress );
				sweep( 1 - progress );
				updateText( 1 - progress );
		}
		moveToTarget();
	}

	public void hide() {
		switchPhase( Phase.FADE_OUT, true );
	}

	public static TriWandIndicator show( Hero hero, TriWand wand ) {
		TriWandIndicator indicator = GameScene.recycleOverFog( TriWandIndicator.class );
		indicator.reset( wand, fromChar( hero ) );
		return indicator;
	}

	public static Supplier<PointF> fromChar( Char ch ) {
		return () -> ch.sprite != null ? ch.sprite.center().offset( 0, -ch.sprite.height() / 2 - OFFSET - SIZE / 2f ) : null;
	}
}
