package com.giggs.heroquest.models.characters;


import com.giggs.heroquest.data.items.ItemFactory;
import com.giggs.heroquest.data.items.PotionFactory;
import com.giggs.heroquest.models.effects.Effect;
import com.giggs.heroquest.models.items.Characteristics;
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
    private List<Integer> movementDice = new ArrayList<>(6);

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

    @Override
    public void initNewTurn() {
        super.initNewTurn();
        movementDice = new ArrayList<>();
        Random r = new Random();
        movementDice.add(1 + r.nextInt(6));

        // breast plate modifier
        if (!hasItem(ItemFactory.buildBreastPlate())) {
            movementDice.add(1 + r.nextInt(6));
        }

        for (Effect buff : buffs) {
            if (buff.getTarget() == Characteristics.MOVEMENT) {
                movementDice.add(1 + r.nextInt(6));
            }
        }
    }

    @Override
    public int calculateMovement() {
        int result = 0;
        for (int die : movementDice) {
            result += die;
        }
        return result;
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
        currentSpirit = spirit;
        buffs = new ArrayList<>();
    }

    public HeroTypes getHeroType() {
        return heroType;
    }

    public enum HeroTypes {
        STR, DEX, SPI, STR_DEX, STR_SPI, DEX_SPI
    }

    public List<Integer> getMovementDice() {
        return movementDice;
    }

}
