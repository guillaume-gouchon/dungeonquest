package com.giggs.heroquest.data.characters;

import com.giggs.heroquest.data.items.ItemFactory;
import com.giggs.heroquest.models.characters.Ally;
import com.giggs.heroquest.models.characters.Unit;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume ON 10/6/14.
 */
public class AllyFactory {

    public static List<Unit> getAll() {
        List<Unit> lst = new ArrayList<>();
        lst.add(buildWerewolf());
        lst.add(buildRat());
        lst.add(buildScout());
        lst.add(buildHalberdier());
        lst.add(buildCrossbowman());
        lst.add(buildSwordsman());
        return lst;
    }

    public static Unit buildWerewolf() {
        Unit unit = new Ally("werewolf", 3, 4, 3, 2, 4);
        return unit;
    }

    public static Unit buildRat() {
        Unit unit = new Ally("rat", 2, 1, 1, 3, 10);
        return unit;
    }

    public static Unit buildScout() {
        Unit unit = new Ally("scout", 1, 2, 3, 2, 9);
        unit.addItem(ItemFactory.buildToolbox());
        return unit;
    }

    public static Unit buildSwordsman() {
        Unit unit = new Ally("swordsman", 1, 4, 5, 2, 4);
        return unit;
    }

    public static Unit buildCrossbowman() {
        Unit unit = new Ally("crossbowman", 1, 2, 3, 2, 6);
        return unit;
    }

    public static Unit buildHalberdier() {
        Unit unit = new Ally("halberdier", 1, 3, 3, 2, 6);
        return unit;
    }

    public static Unit buildMirrorImage() {
        Unit unit = new Ally("gnome", 1, 1, 2, 7, 4);
        return unit;
    }
}
