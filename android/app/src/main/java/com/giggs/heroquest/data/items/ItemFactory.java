package com.giggs.heroquest.data.items;

import com.giggs.heroquest.data.characters.AllyFactory;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.models.effects.PermanentEffect;
import com.giggs.heroquest.models.items.Characteristics;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.models.items.Mercenary;
import com.giggs.heroquest.models.items.equipments.weapons.RangeWeapon;
import com.giggs.heroquest.models.items.equipments.weapons.Weapon;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume ON 10/6/14.
 */
public class ItemFactory {

    public static List<Item> getAvailableItemsInShop(Hero.HeroTypes heroType) {
        List<Item> items = new ArrayList<>();
        switch (heroType) {
            case STR:
                items.add(buildBattleAxe());
                items.add(buildBroadSword());
                items.add(buildCrossbow());
                items.add(buildShield());
                items.add(buildHelmet());
                items.add(buildChainmail());
                items.add(buildBreastPlate());
                break;
            case SPI:
                items.add(buildWizardStaff());
                items.add(buildBracers());
                items.add(buildCloak());
                break;
        }
        items.add(buildToolbox());
        items.add(PotionFactory.buildHealingPotion());
        items.add(PotionFactory.buildHolyWater());
        items.add(PotionFactory.buildStrengthPotion());
        items.add(PotionFactory.buildResiliencePotion());
        items.add(PotionFactory.buildSpeedPotion());
        items.add(buildScout());
        items.add(buildHalberdier());
        items.add(buildCrossbowman());
        items.add(buildSwordsman());
        return items;
    }

    public static Item buildBattleAxe() {
        Weapon item = new Weapon("battle_axe", 0, 0, 0, 400);
        item.addEffect(new PermanentEffect(Characteristics.ATTACK, 4, null, 0));
        return item;
    }

    public static Item buildBroadSword() {
        Weapon item = new Weapon("broad_sword", 0, 0, 0, 250);
        item.addEffect(new PermanentEffect(Characteristics.ATTACK, 3, null, 0));
        return item;
    }

    public static Item buildWizardStaff() {
        Weapon item = new Weapon("wizard_staff", 0, 0, 0, 120);
        item.addEffect(new PermanentEffect(Characteristics.ATTACK, 2, null, 0));
        return item;
    }

    public static Item buildCrossbow() {
        Weapon item = new RangeWeapon("crossbow", 0, 0, 0, 350);
        item.addEffect(new PermanentEffect(Characteristics.ATTACK, 3, null, 0));
        return item;
    }

    public static Item buildShield() {
        Weapon item = new Weapon("shield", 0, 0, 0, 100);
        item.addEffect(new PermanentEffect(Characteristics.DEFENSE, 1, null, 0));
        return item;
    }

    public static Item buildHelmet() {
        Weapon item = new Weapon("helmet", 0, 0, 0, 120);
        item.addEffect(new PermanentEffect(Characteristics.DEFENSE, 1, null, 0));
        return item;
    }

    public static Item buildChainmail() {
        Weapon item = new Weapon("chainmail", 0, 0, 0, 350);
        item.addEffect(new PermanentEffect(Characteristics.DEFENSE, 3, null, 0));
        return item;
    }

    public static Item buildBreastPlate() {
        Weapon item = new Weapon("breast_plate", 0, 0, 0, 650);
        item.addEffect(new PermanentEffect(Characteristics.DEFENSE, 4, null, 0));
        return item;
    }

    public static Item buildBracers() {
        Weapon item = new Weapon("bracers", 0, 0, 0, 250);
        item.addEffect(new PermanentEffect(Characteristics.DEFENSE, 1, null, 0));
        return item;
    }

    public static Item buildCloak() {
        Weapon item = new Weapon("cloak", 0, 0, 0, 300);
        item.addEffect(new PermanentEffect(Characteristics.DEFENSE, 1, null, 0));
        return item;
    }

    public static Item buildToolbox() {
        Weapon item = new Weapon("toolbox", 0, 0, 0, 200);
        return item;
    }

    public static Item buildSpiritBlade() {
        Weapon item = new Weapon("spirit_blade", 0, 0, 0, 1000);
        item.addEffect(new PermanentEffect(Characteristics.ATTACK, 3, null, 0));
        return item;
    }

    public static Item buildProtectionTalisman() {
        Weapon item = new Weapon("protection_talisman", 0, 0, 0, 600);
        item.addEffect(new PermanentEffect(Characteristics.DEFENSE, 1, null, 0));
        return item;
    }

    public static Item buildScout() {
        Item item = new Mercenary("scout", 50, AllyFactory.buildScout());
        return item;
    }

    public static Item buildHalberdier() {
        Item item = new Mercenary("halberdier", 50, AllyFactory.buildHalberdier());
        return item;
    }

    public static Item buildCrossbowman() {
        Item item = new Mercenary("crossbowman", 75, AllyFactory.buildCrossbowman());
        return item;
    }

    public static Item buildSwordsman() {
        Item item = new Mercenary("swordsman", 75, AllyFactory.buildSwordsman());
        return item;
    }

}
