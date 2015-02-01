package com.giggs.heroquest.models;

import android.content.ContentValues;
import android.database.Cursor;

import com.giggs.heroquest.data.characters.AllyFactory;
import com.giggs.heroquest.data.characters.MonsterFactory;
import com.giggs.heroquest.data.dungeons.DecorationFactory;
import com.giggs.heroquest.game.base.GameElement;
import com.giggs.heroquest.game.graphics.GraphicHolder;
import com.giggs.heroquest.game.graphics.SpriteHolder;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.utils.providers.ByteSerializerHelper;
import com.giggs.heroquest.utils.providers.DatabaseResource;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by guillaume ON 10/2/14.
 */
public class Game extends DatabaseResource {

    public static final String TABLE_NAME = "game";
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " (_id INTEGER PRIMARY KEY, " + Columns.HERO + " BLOB, " + Columns.QUESTS_DONE +
            " BLOB, " + Columns.QUEST + " BLOB);";

    private Map<Integer, Integer> booksDone = new HashMap<>();
    private Hero hero;
    private Quest quest;

    public static Game fromCursor(Cursor cursor) {
        Game game = new Game();
        game.setId(cursor.getLong(0));

        if (cursor.getColumnCount() > 1) {
            game.setHero((Hero) ByteSerializerHelper.getObjectFromByte(cursor.getBlob(1)));
            if (cursor.getColumnCount() > 2) {
                game.setBooksDone((Map<Integer, Integer>) ByteSerializerHelper.getObjectFromByte(cursor.getBlob(2)));
                if (cursor.getBlob(3) != null) {
                    game.setQuest((Quest) ByteSerializerHelper.getObjectFromByte(cursor.getBlob(3)));
                }
            }
        }

        return game;
    }

    @Override
    public ContentValues toContentValues() {
        ContentValues content = new ContentValues(Columns.values().length + 1);
        if (id > 0) {
            content.put(Game.COLUMN_ID, id);
        }
        content.put(Columns.HERO.toString(), ByteSerializerHelper.toByteArray(hero));
        content.put(Columns.QUESTS_DONE.toString(), ByteSerializerHelper.toByteArray((Serializable) booksDone));
        content.put(Columns.QUEST.toString(), ByteSerializerHelper.toByteArray(quest));
        return content;
    }

    public Quest getQuest() {
        return quest;
    }

    public void setQuest(Quest quest) {
        this.quest = quest;
    }

    public Hero getHero() {
        return hero;
    }

    public void setHero(Hero hero) {
        this.hero = hero;
    }

    public void finishQuest() {
        quest.setDone();
    }

    public List<GraphicHolder> getGraphicsToLoad() {
        List<GraphicHolder> toLoad = new ArrayList<>();

        // load hero
        toLoad.add(hero);

        // load monsters
        for (GameElement element : MonsterFactory.getAll()) {
            toLoad.add(element);
        }

        // load allies
        for (GameElement element : AllyFactory.getAll()) {
            toLoad.add(element);
        }

        // load decorations
        for (GameElement element : DecorationFactory.getAll()) {
            toLoad.add(element);
        }

        toLoad.add(new SpriteHolder("stairs.png", 30, 30, 2, 1));
        toLoad.add(new SpriteHolder("selection.png", 64, 64, 1, 1));
        toLoad.add(new SpriteHolder("blood.png", 300, 50, 6, 1));
        toLoad.add(new SpriteHolder("slash.png", 250, 50, 5, 1));
        toLoad.add(new SpriteHolder("poison.png", 300, 50, 6, 1));
        toLoad.add(new SpriteHolder("frost.png", 300, 50, 6, 1));
        toLoad.add(new SpriteHolder("curse.png", 300, 50, 6, 1));
        toLoad.add(new SpriteHolder("elec.png", 300, 50, 6, 1));
        toLoad.add(new SpriteHolder("ground_slam.png", 300, 50, 6, 1));
        toLoad.add(new SpriteHolder("charm.png", 300, 50, 6, 1));
        toLoad.add(new SpriteHolder("fireball.png", 200, 100, 4, 2));

        return toLoad;
    }

    public List<String> getSoundEffectsToLoad() {
        List<String> toLoad = new ArrayList<>();

        toLoad.add("block");
        toLoad.add("close_combat_attack");
        toLoad.add("coins");
        toLoad.add("damage_hero");
        toLoad.add("damage_monster");
        toLoad.add("death");
        toLoad.add("magic");
        toLoad.add("new_level");
        toLoad.add("range_attack");
        toLoad.add("search");

        return toLoad;
    }

    public Map<Integer, Integer> getBooksDone() {
        return booksDone;
    }

    public void setBooksDone(Map<Integer, Integer> booksDone) {
        this.booksDone = booksDone;
    }

    public enum Columns {
        HERO("hero"), QUESTS_DONE("quests_done"), QUEST("dungeon");

        private final String columnName;

        private Columns(String columnName) {
            this.columnName = columnName;
        }

        @Override
        public String toString() {
            return columnName;
        }
    }

}
