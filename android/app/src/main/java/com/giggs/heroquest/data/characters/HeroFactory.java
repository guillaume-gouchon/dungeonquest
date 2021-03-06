package com.giggs.heroquest.data.characters;

import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.models.characters.Ranks;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume ON 10/6/14.
 */
public class HeroFactory {

    public static List<Hero> getAll() {
        List<Hero> lst = new ArrayList<>();
        lst.add(buildBarbarian());
        lst.add(buildElf());
        lst.add(buildWizard());
        lst.add(buildDwarf());
        lst.add(buildThief());
        lst.add(buildDrow());
        lst.add(buildWoodElf());
        lst.add(buildGnome());
        return lst;
    }

    public static Hero buildBarbarian() {
        Hero hero = new Hero("barbarian", Ranks.ME, 8, 8, 3, 2, 2, Hero.HeroTypes.STR);
        return hero;
    }

    public static Hero buildElf() {
        Hero hero = new Hero("elf", Ranks.ME, 6, 6, 2, 2, 4, Hero.HeroTypes.STR);
        hero.getSkills().add(SkillFactory.buildCourage());
        hero.getSkills().add(SkillFactory.buildFireWrath());
        hero.getSkills().add(SkillFactory.buildHealingWater());
        return hero;
    }

    public static Hero buildWizard() {
        Hero hero = new Hero("wizard", Ranks.ME, 4, 4, 1, 2, 6, Hero.HeroTypes.SPI);
        hero.getSkills().add(SkillFactory.buildFireball());
        hero.getSkills().add(SkillFactory.buildSleep());
        hero.getSkills().add(SkillFactory.buildRockSkin());
        hero.getSkills().add(SkillFactory.buildHealBody());
        hero.getSkills().add(SkillFactory.buildFireWrath());
        hero.getSkills().add(SkillFactory.buildDarknessWinds());
        hero.getSkills().add(SkillFactory.buildSwiftWind());
        return hero;
    }

    public static Hero buildDwarf() {
        Hero hero = new Hero("dwarf", Ranks.ME, 6, 6, 2, 2, 4, Hero.HeroTypes.STR);
        return hero;
    }

    public static Hero buildThief() {
        Hero hero = new Hero("thief", Ranks.ME, 5, 5, 1, 2, 4, Hero.HeroTypes.STR);
        hero.getSkills().add(SkillFactory.buildCamouflage());
        hero.getSkills().add(SkillFactory.buildCamouflage());
        hero.getSkills().add(SkillFactory.buildCamouflage());
        return hero;
    }

    public static Hero buildWoodElf() {
        Hero hero = new Hero("wood_elf", Ranks.ME, 5, 5, 2, 2, 5, Hero.HeroTypes.SPI);
        hero.getSkills().add(SkillFactory.buildMedicalPlants());
        hero.getSkills().add(SkillFactory.buildZiggy());
        hero.getSkills().add(SkillFactory.buildWiggy());
        return hero;
    }

    public static Hero buildDrow() {
        Hero hero = new Hero("drow", Ranks.ME, 5, 5, 2, 2, 4, Hero.HeroTypes.STR);
        return hero;
    }

    public static Hero buildGnome() {
        Hero hero = new Hero("gnome", Ranks.ME, 3, 3, 1, 2, 7, Hero.HeroTypes.SPI);
        hero.getSkills().add(SkillFactory.buildNightmare());
        hero.getSkills().add(SkillFactory.buildTerror());
        hero.getSkills().add(SkillFactory.buildMirrorImage());
        hero.getSkills().add(SkillFactory.buildMirrorImage());
        return hero;
    }

}
