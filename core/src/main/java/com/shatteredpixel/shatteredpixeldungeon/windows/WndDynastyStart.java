package com.shatteredpixel.shatteredpixeldungeon.windows;

import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Rankings;
import com.shatteredpixel.shatteredpixeldungeon.SPDSettings;
import com.shatteredpixel.shatteredpixeldungeon.ShatteredPixelDungeon;
import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.scenes.HeroSelectScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.PixelScene;
import com.shatteredpixel.shatteredpixeldungeon.scenes.RankingsScene;
import com.shatteredpixel.shatteredpixeldungeon.utils.NameGen;
import com.watabou.noosa.Game;
import com.watabou.utils.Random;

public class WndDynastyStart extends WndOptions {

    private static NameGen namesGenerator;

    private Rankings.Dynasty current;

    public WndDynastyStart() {
        super(Messages.get(WndDynastyStart.class, "title_new"), Messages.get(WndDynastyStart.class, "message_new"), Messages.get(WndDynastyStart.class, "no"), Messages.get(WndDynastyStart.class, "regular"), Messages.get(WndDynastyStart.class, "epic"));
    }

    public WndDynastyStart(Rankings.Dynasty current) {
        super(Messages.get(WndDynastyStart.class, "title"), Messages.get(WndDynastyStart.class, Rankings.INSTANCE.records.get(Rankings.INSTANCE.lastRecord).win ? "message" : "message_fail"), Messages.get(WndDynastyStart.class, "continue"));
        this.current = current;
    }

    @Override
    protected void onSelect(int index) {
        initNamegen();
        Rankings.Record cur = Rankings.INSTANCE.records.get(Rankings.INSTANCE.lastRecord);
        boolean newDynasty = false;
        if (current == null) {
            newDynasty=true;
            boolean epic = false;
            switch (index) {
                case 0:
                    Game.switchScene(RankingsScene.class);
                    return;
                case 1:
                    epic = false;
                    break;
                case 2:
                    epic = true;
                    break;
            }
            current = new Rankings.Dynasty();
            current.epic = epic;
            current.surface = Dungeon.depth == 1;
            current.records.add(cur);
        }
        if (!cur.win) {
            ShatteredPixelDungeon.switchScene(RankingsScene.class);
        }

        SPDSettings.modifiers(Dungeon.modifiers);
        current.name=name(current.epic);
        if(newDynasty) {
            Rankings.INSTANCE.dynasties.put(current.id,current);
        }
        Rankings.INSTANCE.save();
        SPDSettings.modifiers(SPDSettings.modifiers().setDynasty(current.id));
        ShatteredPixelDungeon.switchScene(HeroSelectScene.class);
    }

    private static void initNamegen(){
        if(namesGenerator!=null)return;
        namesGenerator = new NameGen(5,12);
        for (String name : Messages.get(WndDynastyStart.class, "names").split(",")) {
            namesGenerator.addName(name);
        }
    }

    public static String name(boolean epic){
        initNamegen();
        String surname = Messages.titleCase(namesGenerator.getName());
        String title = String.format(Random.element(Messages.get(WndDynastyStart.class, "dyn_names").split(",")),surname);
        if(!epic)return title;
        String god = Random.element(Messages.get(WndDynastyStart.class, "title_first").split(","));
        String action = Random.element(Messages.get(WndDynastyStart.class, "title_second").split("\\|"));
        return String.format(action,title,god);
    }
}
