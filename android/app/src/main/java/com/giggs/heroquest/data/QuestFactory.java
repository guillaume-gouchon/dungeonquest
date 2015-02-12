package com.giggs.heroquest.data;

import com.giggs.heroquest.activities.games.TutorialQuest;
import com.giggs.heroquest.data.characters.MonsterFactory;
import com.giggs.heroquest.data.dungeons.DecorationFactory;
import com.giggs.heroquest.data.items.ItemFactory;
import com.giggs.heroquest.models.Quest;
import com.giggs.heroquest.models.Reward;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume ON 10/6/14.
 */
public class QuestFactory {

    public static List<Quest> getAll() {
        List<Quest> lst = new ArrayList<>();
        lst.add(buildTutorial());
        lst.add(buildMaze());
        lst.add(buildTrial());
        lst.add(buildWarlord());
        lst.add(buildMagnus());
        lst.add(buildLegacy());
        lst.add(buildStone());
        lst.add(buildSpirit());
        lst.add(buildBarak());
        return lst;
    }

    public static Quest buildTutorial() {
        Quest quest = new Quest.Builder(0, "tutorial", "tutorial")
                .setAvailable(true)
                .setBoss(MonsterFactory.buildGoblin())
                .setRelic(DecorationFactory.buildSmallChest(new Reward("tr_gold", null, null, 10)))
                .setActivityClass(TutorialQuest.class)
                .build();
        return quest;
    }

    public static Quest buildMaze() {
        Quest quest = new Quest.Builder(1, "maze_quest", "maze")
                .setAvailable(true)
                .setIntro("maze_quest_intro")
                .setOutro("maze_quest_outro")
                .build();
        return quest;
    }

    public static Quest buildTrial() {
        Quest quest = new Quest.Builder(2, "trial_quest", "trial")
                .setAvailable(true)
                .setIntro("trial_quest_intro")
                .setOutro("trial_quest_outro")
                .setBoss(MonsterFactory.buildVerag())
                .build();
        return quest;
    }

    public static Quest buildWarlord() {
        Quest quest = new Quest.Builder(3, "warlord_quest", "warlord")
                .setIntro("warlord_quest_intro")
                .setOutro("warlord_quest_outro")
                .setReward(new Reward(null, null, null, 100))
                .setBoss(MonsterFactory.buildUlag())
                .setRelic(DecorationFactory.buildSmallChest(new Reward("tr_gold", null, null, 150)))
                .build();
        return quest;
    }

    public static Quest buildMagnus() {
        Quest quest = new Quest.Builder(4, "magnus_quest", "magnus")
                .setIntro("magnus_quest_intro")
                .setOutro("magnus_quest_outro")
                .setReward(new Reward(null, null, null, 200))
                .setBoss(MonsterFactory.buildGulthor())
                .build();
        return quest;
    }

    public static Quest buildLegacy() {
        Quest quest = new Quest.Builder(5, "legacy_quest", "legacy")
                .setIntro("legacy_quest_intro")
                .setOutro("legacy_quest_outro")
                .setBoss(MonsterFactory.buildGrak())
                .build();
        return quest;
    }

    public static Quest buildStone() {
        Quest quest = new Quest.Builder(6, "stone_quest", "stone")
                .setIntro("stone_quest_intro")
                .setOutro("stone_quest_outro")
                .setBoss(MonsterFactory.buildKarlen())
                .setRelic(DecorationFactory.buildBookcase(new Reward("protection_talisman", null, ItemFactory.buildProtectionTalisman(), 0)))
                .build();
        return quest;
    }

    public static Quest buildSpirit() {
        Quest quest = new Quest.Builder(7, "spirit_quest", "spirit")
                .setIntro("spirit_quest_intro")
                .setOutro("spirit_quest_outro")
                .setRelic(DecorationFactory.buildStatue(new Reward("spirit_blade", null, ItemFactory.buildSpiritBlade(), 0)))
                .build();
        return quest;
    }

    public static Quest buildBarak() {
        Quest quest = new Quest.Builder(8, "barak_quest", "barak")
                .setIntro("barak_quest_intro")
                .setOutro("barak_quest_outro")
                .setBoss(MonsterFactory.buildWitchLord())
                .build();
        return quest;
    }

}
