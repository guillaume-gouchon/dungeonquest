package com.giggs.heroquest.data.characters;

import com.giggs.heroquest.models.effects.BuffEffect;
import com.giggs.heroquest.models.effects.CamouflageEffect;
import com.giggs.heroquest.models.effects.DamageEffect;
import com.giggs.heroquest.models.effects.Effect;
import com.giggs.heroquest.models.effects.InvocationEffect;
import com.giggs.heroquest.models.effects.StunEffect;
import com.giggs.heroquest.models.items.Characteristics;
import com.giggs.heroquest.models.skills.ActiveSkill;
import com.giggs.heroquest.models.skills.Skill;

import org.andengine.util.color.Color;

/**
 * Created by guillaume ON 10/6/14.
 */
public class SkillFactory {

    public static Skill buildCamouflage() {
        Effect effect = new CamouflageEffect(null, 0, 0);
        return new ActiveSkill("camouflage", 0, true, 0, effect);
    }


    public static Skill buildCourage() {
        Effect effect = new BuffEffect(null, Characteristics.ATTACK, 2, 5, null, 0, Color.RED);
        return new ActiveSkill("courage", 0, true, 0, effect);
    }

    public static Skill buildFireWrath() {
        Effect effect = new DamageEffect("fireball.png", -1, null, 0);
        return new ActiveSkill("fire_of_wrath", 0, false, 0, effect);
    }

    public static Skill buildHealingWater() {
        Effect effect = new DamageEffect("poison.png", 4, 0);
        return new ActiveSkill("healing_water", 0, true, 0, effect);
    }

    public static Skill buildDarknessWinds() {
        Effect effect = new BuffEffect(null, Characteristics.DEFENSE, 4, 4, null, 0, Color.BLACK);
        return new ActiveSkill("darkness_winds", 0, true, 0, effect);
    }

    public static Skill buildFireball() {
        Effect effect = new DamageEffect("fireball.png", -1, null, 0);
        return new ActiveSkill("fireball", 0, false, 1, effect);
    }

    public static Skill buildSleep() {
        Effect effect = new StunEffect("curse.png", Characteristics.SPIRIT, 1, 0);
        return new ActiveSkill("sleep", 0, false, 0, effect);
    }

    public static Skill buildRockSkin() {
        Effect effect = new BuffEffect(null, Characteristics.DEFENSE, 2, 5, null, 0, new Color(0.65f, 0.24f, 0.08f, 1.0f));
        return new ActiveSkill("rock_skin", 0, true, 0, effect);
    }

    public static Skill buildHealBody() {
        Effect effect = new DamageEffect("poison.png", 4, 0);
        return new ActiveSkill("heal_body", 0, true, 0, effect);
    }

    public static Skill buildSwiftWind() {
        Effect effect = new BuffEffect(null, Characteristics.MOVEMENT, 0, 5, null, 0, Color.BLUE);
        return new ActiveSkill("swift_wind", 0, true, 0, effect);
    }

    public static Skill buildMedicalPlants() {
        Effect effect = new DamageEffect("poison.png", 3, 0);
        return new ActiveSkill("medical_plants", 0, true, 0, effect);
    }

    public static Skill buildWiggy() {
        return new ActiveSkill("wiggy", 0, true, 0, new InvocationEffect("poison.png", AllyFactory.buildWerewolf()));
    }

    public static Skill buildZiggy() {
        return new ActiveSkill("ziggy", 0, true, 0, new InvocationEffect("poison.png", AllyFactory.buildRat()));
    }

    public static Skill buildMirrorImage() {
        return new ActiveSkill("mirror_image", 0, true, 0, new InvocationEffect("poison.png", AllyFactory.buildMirrorImage()));
    }

    public static Skill buildTerror() {
        Effect effect = new StunEffect("curse.png", Characteristics.SPIRIT, 1, 0);
        return new ActiveSkill("terror", 0, false, 1, effect);
    }

    public static Skill buildNightmare() {
        Effect effect = new DamageEffect("curse.png", -1, null, 0);
        return new ActiveSkill("nightmare", 0, false, 0, effect);
    }

}
