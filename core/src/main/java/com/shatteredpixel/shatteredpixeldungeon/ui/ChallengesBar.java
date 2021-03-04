package com.shatteredpixel.shatteredpixeldungeon.ui;

import com.shatteredpixel.shatteredpixeldungeon.Chrome;
import com.shatteredpixel.shatteredpixeldungeon.Modifiers;
import com.shatteredpixel.shatteredpixeldungeon.utils.Difficulty;
import com.watabou.noosa.NinePatch;
import com.watabou.noosa.ui.Component;
import com.watabou.utils.ColorMath;

public class ChallengesBar extends Component {
	private HealthBar bar;
	private NinePatch foreground;
	
	@Override
	protected void createChildren() {
		bar = new HealthBar();
		foreground = Chrome.get(Chrome.Type.TOAST_EMPTY);
		
		add(bar);
		add(foreground);
	}
	
	@Override
	protected void layout() {
		foreground.x = x;
		foreground.y = y;
		foreground.size(width, height);
		
		bar.setRect(x+2, y+2, width - 4, height - 4);
	}
	
	public void update(Modifiers modifiers) {
		float margin = Difficulty.IMPOSSIBLE_6.margin;
		float difficulty = Difficulty.calculateDifficulty(modifiers);
		
		float shielding = Math.max(difficulty - margin, 0);
		float hp = difficulty - shielding;
		
		float max = Math.max(margin, shielding + hp);
		
		bar.level(hp / max, (shielding + hp) / max);
		
		int color = 0xFF000000 + ColorMath.interpolate(hp / margin, 0x80ff00, 0x00ffff, 0xffff00, 0x800000, 0x800080, 0x0000FF, 0x000040);
		int shield = 0xFF400000;
//		int shield = 0xFFFFFFFF;
		int bg = 0xFF3E4039;
		
		bar.color(bg,color,shield);
	}
}
