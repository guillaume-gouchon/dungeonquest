package com.giggs.heroquest.data.items;

import com.giggs.heroquest.models.effects.BuffEffect;
import com.giggs.heroquest.models.effects.DamageEffect;
import com.giggs.heroquest.models.effects.HeroicEffect;
import com.giggs.heroquest.models.effects.LifeStealEffect;
import com.giggs.heroquest.models.items.Characteristics;
import com.giggs.heroquest.models.items.consumables.Potion;

import org.andengine.util.color.Color;

/**
 * Created by guillaume ON 10/6/14.
 */
public class PotionFactory {

    public static Potion buildStrengthPotion() {
        Potion item = new Potion("strength_potion", new BuffEffect(null, Characteristics.ATTACK, 2, 6, null, 0, Color.RED), 70);
        return item;
    }

    public static Potion buildHealingPotion() {
        Potion item = new Potion("healing_potion", new DamageEffect(null, 4, 0), 50);
        return item;
    }

    public static Potion buildHeroicBrew() {
        Potion item = new Potion("heroic_brew", new HeroicEffect("curse.png"), 100);
        return item;
    }

    public static Potion buildBatPotion() {
        Potion item = new Potion("bat_potion", new LifeStealEffect("blood.png", 1, 5, 0), 80);
        return item;
    }

    public static Potion buildResiliencePotion() {
        Potion item = new Potion("resilience_potion", new BuffEffect(null, Characteristics.DEFENSE, 2, 6, null, 0, Color.YELLOW), 60);
        return item;
    }

    public static Potion buildHolyWater() {
        Potion item = new Potion("holy_water", null, 40);
        return item;
    }

    public static Potion buildSpeedPotion() {
        Potion item = new Potion("speed_potion", new BuffEffect(null, Characteristics.MOVEMENT, 7, 5, null, 0, Color.BLUE), 40);
        return item;
    }

}
