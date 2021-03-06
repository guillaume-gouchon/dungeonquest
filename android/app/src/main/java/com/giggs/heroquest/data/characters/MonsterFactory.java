package com.giggs.heroquest.data.characters;

import com.giggs.heroquest.models.characters.Monster;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by guillaume ON 10/6/14.
 */
public class MonsterFactory {

    public static List<Monster> getAll() {
        List<Monster> lst = new ArrayList<>();
        lst.add(buildGoblin());
        lst.add(buildOrc());
        lst.add(buildTroll());
        lst.add(buildGargoyle());
        lst.add(buildChaosWarrior());
        lst.add(buildSkeleton());
        lst.add(buildZombie());
        lst.add(buildMummy());
        lst.add(buildStoneWarrior());
        return lst;
    }

    public static Monster getWanderingMonster() {
        Random r = new Random();
        int random = r.nextInt(18);
        switch (random) {
            case 0:
            case 1:
            case 2:
            case 3:
                return buildGoblin();
            case 4:
            case 5:
            case 6:
            case 7:
                return buildOrc();
            case 8:
            case 9:
                return buildTroll();
            case 10:
            case 11:
                return buildSkeleton();
            case 12:
            case 13:
                return buildZombie();
            case 14:
                return buildMummy();
            case 15:
                return buildChaosWarrior();
            case 16:
                return buildStoneWarrior();
            default:
                return buildGargoyle();
        }
    }

    public static Monster buildGoblin() {
        Monster monster = new Monster("goblin", 1, 2, 1, 2, 10);
        return monster;
    }

    public static Monster buildOrc() {
        Monster monster = new Monster("orc", 1, 3, 2, 2, 8);
        return monster;
    }

    public static Monster buildTroll() {
        Monster monster = new Monster("troll", 1, 3, 3, 1, 6);
        return monster;
    }

    public static Monster buildChaosWarrior() {
        Monster monster = new Monster("chaos_warrior", 1, 3, 4, 3, 6);
        return monster;
    }

    public static Monster buildGargoyle() {
        Monster monster = new Monster("gargoyle", 1, 4, 4, 4, 6);
        return monster;
    }

    public static Monster buildSkeleton() {
        Monster monster = new Monster("skeleton", 1, 2, 2, 0, 6);
        return monster;
    }

    public static Monster buildZombie() {
        Monster monster = new Monster("zombie", 1, 2, 3, 0, 4);
        return monster;
    }

    public static Monster buildMummy() {
        Monster monster = new Monster("mummy", 1, 3, 4, 0, 4);
        return monster;
    }

    public static Monster buildStoneWarrior() {
        Monster monster = new Monster("stone_warrior", 1, 3, 5, 2, 6);
        return monster;
    }

    public static Monster buildVerag() {
        Monster monster = new Monster("verag", 2, 4, 4, 4, 6);
        return monster;
    }

    public static Monster buildUlag() {
        Monster monster = new Monster("ulag", 2, 4, 5, 3, 6);
        return monster;
    }


    public static Monster buildGulthor() {
        Monster monster = new Monster("gulthor", 3, 5, 4, 2, 4);
        return monster;
    }

    public static Monster buildGrak() {
        Monster monster = new Monster("grak", 2, 4, 4, 3, 6);
        return monster;
    }

    public static Monster buildKarlen() {
        Monster monster = new Monster("karlen", 3, 2, 3, 8, 4);
        monster.getSkills().add(SkillFactory.buildTerror());
        monster.getSkills().add(SkillFactory.buildFireWrath());
        monster.getSkills().add(SkillFactory.buildFireWrath());
        monster.getSkills().add(SkillFactory.buildRockSkin());
        return monster;
    }

    public static Monster buildWitchLord() {
        Monster monster = new Monster("karlen", 5, 4, 6, 10, 4);
        monster.getSkills().add(SkillFactory.buildNightmare());
        monster.getSkills().add(SkillFactory.buildSleep());
        monster.getSkills().add(SkillFactory.buildFireWrath());
        monster.getSkills().add(SkillFactory.buildFireWrath());
        return monster;
    }

}
