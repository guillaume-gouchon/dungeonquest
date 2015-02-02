package com.giggs.heroquest.models.characters;


import com.giggs.heroquest.data.items.ItemFactory;
import com.giggs.heroquest.data.items.PotionFactory;
import com.giggs.heroquest.models.items.equipments.Equipment;
import com.giggs.heroquest.models.items.equipments.Ring;
import com.giggs.heroquest.models.items.requirements.HeroRequirement;
import com.giggs.heroquest.models.items.requirements.Requirement;
import com.giggs.heroquest.models.skills.ActiveSkill;
import com.giggs.heroquest.models.skills.Skill;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by guillaume ON 10/2/14.
 */
public class Hero extends Unit {

    private static final long serialVersionUID = -5970005172767341685L;
    private final HeroTypes heroType;
    protected List<String> frags = new ArrayList<>();
    private String heroName;

    private int movementDie1;
    private int movementDie2;

    public Hero(String identifier, Ranks ranks, int hp, int currentHP, int attack, int defense, int spirit, HeroTypes heroType) {
        super(identifier, ranks, hp, currentHP, attack, defense, spirit, 0);
        this.heroType = heroType;

        // add some healing potions
        addItem(PotionFactory.buildHealingPotion());
        addItem(PotionFactory.buildHealingPotion());
    }

    public List<String> getFrags() {
        return frags;
    }

    public void addFrag(String type) {
        frags.add(type);
    }

    public void addGold(int goldAmount) {
        gold += goldAmount;
    }

    private int getInitialSkillPoints() {
        switch (heroType) {
            case STR:
                return 1;
            case DEX:
                return 2;
            case SPI:
                return 3;
            case STR_DEX:
                return 1;
            case STR_SPI:
                return 2;
            default:
                return 2;
        }
    }

    @Override
    public void initNewTurn() {
        super.initNewTurn();
        Random r = new Random();
        movementDie1 = 1 + r.nextInt(6);

        // breast plate modifier
        if (!hasItem(ItemFactory.buildBreastPlate())) {
            movementDie2 = 1 + r.nextInt(6);
        } else {
            movementDie2 = 0;
        }
    }

    @Override
    public int calculateMovement() {
        return movementDie1 + movementDie2;
    }

    public String getHeroName() {
        return heroName;
    }

    public void setHeroName(String heroName) {
        this.heroName = heroName;
    }

    public void reset() {
        for (Skill skill : skills) {
            if (skill instanceof ActiveSkill) {
                ((ActiveSkill) skill).reset();
            }
        }
        currentHP = hp;
        buffs = new ArrayList<>();
    }

    public boolean canEquipItem(Equipment equipment) {
        boolean canEquip = isEquipmentSuitable(equipment);
        if (!canEquip) {
            return false;
        }

        return true;
    }

    public boolean isEquipmentSuitable(Equipment equipment) {
        if (equipment instanceof Ring) {
            return true;
        }
        for (Requirement requirement : equipment.getRequirements()) {
            if (requirement instanceof HeroRequirement && heroType == ((HeroRequirement) requirement).getHeroType()) {
                return true;
            }
        }
        return false;
    }

    public HeroTypes getHeroType() {
        return heroType;
    }

    public enum HeroTypes {
        STR, DEX, SPI, STR_DEX, STR_SPI, DEX_SPI
    }

    public int getMovementDie1() {
        return movementDie1;
    }

    public int getMovementDie2() {
        return movementDie2;
    }

}
