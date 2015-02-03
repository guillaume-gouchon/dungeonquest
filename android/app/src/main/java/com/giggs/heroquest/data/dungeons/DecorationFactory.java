package com.giggs.heroquest.data.dungeons;

import com.giggs.heroquest.data.items.ItemFactory;
import com.giggs.heroquest.data.items.PotionFactory;
import com.giggs.heroquest.models.Reward;
import com.giggs.heroquest.models.dungeons.decorations.Decoration;
import com.giggs.heroquest.models.dungeons.decorations.Searchable;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by guillaume ON 10/6/14.
 */
public class DecorationFactory {

    public static List<Decoration> getAll() {
        List<Decoration> lst = new ArrayList<>();
        lst.add(buildTable(null));
        lst.add(buildSmallChest(null));
        lst.add(buildStatue(null));
        lst.add(buildRack(null));
        lst.add(buildChair(null));
        lst.add(buildBookcase(null));
        lst.add(buildEmptyBookcase(null));
        lst.add(buildTonnels(null));
        return lst;
    }

    public static Decoration buildTable(Reward reward) {
        return new Searchable("table", reward, 85, 70, 1, 1);
    }

    public static Decoration buildSmallChest(Reward reward) {
        return new Searchable("small_chest", reward, 37, 34, 1, 1);
    }

    public static Decoration buildStatue(Reward reward) {
        return new Searchable("statue", reward, 40, 82, 1, 1);
    }

    public static Decoration buildRack(Reward reward) {
        return new Searchable("rack", reward, 40, 80, 1, 1);
    }

    public static Decoration buildChair(Reward reward) {
        return new Searchable("chair", reward, 40, 70, 1, 1);
    }

    public static Decoration buildBookcase(Reward reward) {
        return new Searchable("bookcase", reward, 40, 73, 1, 1);
    }

    public static Decoration buildEmptyBookcase(Reward reward) {
        return new Searchable("empty_bookcase", reward, 40, 73, 1, 1);
    }

    public static Decoration buildTonnels(Reward reward) {
        return new Searchable("tonnels", reward, 70, 53, 1, 1);
    }

    public static Decoration getRandomSearchable() {
        // create random reward
        Reward reward;
        Random r = new Random();
        int random = r.nextInt(24);
        switch (random) {
            case 0:
            case 1:
            case 2:
                reward = new Reward("tr_wandering_monster", null, null, 0);
                break;
            case 3:
                reward = new Reward("tr_treasure_horde", null, null, 100);
                break;
            case 4:
                reward = new Reward("tr_gem", null, null, 35);
                break;
            case 5:
                reward = new Reward("tr_jewles", null, null, 50);
                break;
            case 6:
                reward = new Reward("tr_gold", "tr_gold_1_description", null, 10);
                break;
            case 7:
                reward = new Reward("tr_gold", "tr_gold_2_description", null, 25);
                break;
            case 8:
                reward = new Reward("tr_gold", "tr_gold_3_description", null, 20);
                break;
            case 9:
                reward = new Reward("tr_gold", "tr_gold_4_description", null, 50);
                break;
            case 10:
                reward = new Reward("tr_gold", "tr_gold_5_description", null, 25);
                break;
            case 11:
            case 22:
                reward = new Reward("tr_nothing", null, null, 0);
                break;
            case 12:
                reward = new Reward("tr_trap_1", null, null, 0);
                break;
            case 13:
                reward = new Reward("tr_trap_2", null, null, 0);
                break;
            case 14:
                reward = new Reward("tr_magic_trap", null, null, 0);
                break;
            case 15:
            case 16:
                reward = new Reward(null, null, PotionFactory.buildHealingPotion(), 0);
                break;
            case 17:
                reward = new Reward(null, null, PotionFactory.buildBatPotion(), 0);
                break;
            case 18:
                reward = new Reward(null, null, PotionFactory.buildStrengthPotion(), 0);
                break;
            case 19:
                reward = new Reward(null, null, PotionFactory.buildResiliencePotion(), 0);
                break;
            case 20:
                reward = new Reward(null, null, PotionFactory.buildHeroicBrew(), 0);
                break;
            case 21:
                reward = new Reward(null, null, PotionFactory.buildSpeedPotion(), 0);
                break;
            default:
                reward = new Reward(null, null, ItemFactory.buildHolyWater(), 0);
                break;
        }

        random = r.nextInt(8);
        switch (random) {
            case 0:
                return buildSmallChest(reward);
            case 1:
                return buildTable(reward);
            case 2:
                return buildStatue(reward);
            case 3:
                return buildRack(reward);
            case 4:
                return buildBookcase(reward);
            case 5:
                return buildEmptyBookcase(reward);
            case 6:
                return buildTonnels(reward);
            default:
                return buildChair(reward);
        }
    }

}
