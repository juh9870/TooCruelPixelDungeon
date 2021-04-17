package com.shatteredpixel.shatteredpixeldungeon.utils;

import com.shatteredpixel.shatteredpixeldungeon.messages.Messages;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndDynastyStart;
import com.watabou.utils.Random;

public class NamesGenerator {
    private static MarkovNameGen namesGenerator;

    private static void initNamegen(){
        if(namesGenerator!=null)return;
        namesGenerator = new MarkovNameGen(5,12);
        for (String name : Messages.get(NamesGenerator.class, "names").split(",")) {
            namesGenerator.addName(name);
        }
    }
    public static String dynastyName(boolean epic){
        initNamegen();
        String surname = Messages.titleCase(namesGenerator.getName());
        String title = String.format(Random.element(Messages.get(NamesGenerator.class, "dyn_names").split(",")),surname);
        if(!epic)return title;
        String god = Random.element(Messages.get(NamesGenerator.class, "title_first").split(","));
        String action = Random.element(Messages.get(NamesGenerator.class, "title_second").split("\\|"));
        return String.format(action,title,god);
    }
}
