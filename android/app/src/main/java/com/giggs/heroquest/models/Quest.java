package com.giggs.heroquest.models;

import android.content.res.Resources;
import android.util.Log;

import com.giggs.heroquest.activities.games.GameActivity;
import com.giggs.heroquest.data.characters.MonsterFactory;
import com.giggs.heroquest.data.dungeons.DecorationFactory;
import com.giggs.heroquest.data.dungeons.TrapFactory;
import com.giggs.heroquest.game.base.GameElement;
import com.giggs.heroquest.models.characters.Monster;
import com.giggs.heroquest.models.characters.Ranks;
import com.giggs.heroquest.models.characters.Unit;
import com.giggs.heroquest.models.dungeons.Tile;
import com.giggs.heroquest.models.dungeons.decorations.Decoration;
import com.giggs.heroquest.models.dungeons.decorations.Door;
import com.giggs.heroquest.models.dungeons.decorations.Stairs;

import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXProperty;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.extension.tmx.TMXTiledMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume ON 10/3/14.
 */
public class Quest extends StorableResource {

    private static final long serialVersionUID = -4662020495531736056L;

    private static final String TAG = "Quest";

    private final int id;
    private final String introText;
    private final String outroText;
    private final Class activityClass;
    private final String tmxName;
    private final Reward reward;

    private boolean isDone;
    private boolean isAvailable;
    private Tile[][] tiles = null;
    private transient List<GameElement> objects;
    private List<Unit> queue = new ArrayList<>();
    private Tile entranceTile;
    private Decoration relic;
    private Monster boss;

    private Quest(Builder builder) {
        super(builder.identifier);
        this.id = builder.id;
        this.introText = builder.intro;
        this.outroText = builder.outro;
        this.activityClass = builder.activityClass;
        this.isDone = false;
        this.isAvailable = builder.isAvailable;
        this.tmxName = builder.tmxName;
        this.reward = builder.reward;
        this.boss = builder.boss;
        this.relic = builder.relic;
    }

    public int getIntroText(Resources resources) {
        return StorableResource.getResource(resources, introText, false);
    }

    public int getOutroText(Resources resources) {
        return StorableResource.getResource(resources, outroText, false);
    }

    public int getId() {
        return id;
    }

    public boolean isDone() {
        return isDone;
    }

    public void setDone() {
        isDone = true;
    }

    public boolean isAvailable() {
        return isAvailable;
    }

    public void setAvailable(boolean isAvailable) {
        this.isAvailable = isAvailable;
    }

    public Class getActivityClass() {
        return activityClass;
    }

    public Reward getReward() {
        return reward;
    }

    public static class Builder {

        private final int id;
        private final String identifier;
        private final String tmxName;
        private Class activityClass;
        private String intro;
        private String outro;
        private boolean isAvailable;
        private Reward reward;
        private Decoration relic;
        private Monster boss;

        public Builder(int id, String identifier, String tmxName) {
            this.id = id;
            this.identifier = identifier;
            this.tmxName = tmxName;
            this.intro = "";
            this.outro = "";
            this.activityClass = GameActivity.class;
            this.isAvailable = false;
        }

        public Builder setIntro(String intro) {
            this.intro = intro;
            return this;
        }

        public Builder setOutro(String outro) {
            this.outro = outro;
            return this;
        }

        public Builder setReward(Reward reward) {
            this.reward = reward;
            return this;
        }

        public Builder setRelic(Decoration relic) {
            this.relic = relic;
            return this;
        }

        public Builder setBoss(Monster boss) {
            this.boss = boss;
            return this;
        }


        public Builder setActivityClass(Class activityClass) {
            this.activityClass = activityClass;
            return this;
        }

        public Builder setAvailable(boolean isAvailable) {
            this.isAvailable = isAvailable;
            return this;
        }

        public Quest build() {
            return new Quest(this);
        }

    }

    public Tile[][] getTiles() {
        return tiles;
    }

    public String getTmxName() {
        return tmxName;
    }

    public List<Unit> getQueue() {
        return queue;
    }

    public List<GameElement> getObjects() {
        return objects;
    }

    public void addGameElement(GameElement gameElement, Tile tile) {
        addGameElement(gameElement, tile, false);
    }

    public void addGameElement(GameElement gameElement, Tile tile, boolean addTopQueue) {
        gameElement.setTilePosition(tile);
        if (gameElement.isVisible() && !queue.contains(gameElement) && gameElement instanceof Unit) {
            if (addTopQueue) {
                queue.add(0, (Unit) gameElement);
            } else {
                queue.add((Unit) gameElement);
            }
            Log.d(TAG, "queue size =" + queue.size());
        }

        if (!objects.contains(gameElement)) {
            objects.add(gameElement);
        }
    }

    public synchronized void removeElement(GameElement gameElement) {
        objects.remove(gameElement);
        if (gameElement instanceof Unit) {
            queue.remove(gameElement);
        }
    }

    public boolean isSafe() {
        for (Unit unit : queue) {
            if (unit.getRank() == Ranks.ENEMY) {
                return false;
            }
        }
        return true;
    }

    public void initMap(TMXTiledMap tiledMap) {
        if (tiles != null && objects == null) {
            // build load game
            objects = new ArrayList<>();

            // recreate tiles
            Tile[][] newTiles = new Tile[tiledMap.getTileRows()][tiledMap.getTileColumns()];
            TMXLayer groundLayer = tiledMap.getTMXLayers().get(1);
            Tile tile;
            for (TMXTile[] tmxTiles : groundLayer.getTMXTiles()) {
                for (TMXTile tmxTile : tmxTiles) {
                    tile = new Tile(tmxTile, tiledMap);
                    Tile oldTile = tiles[tmxTile.getTileRow()][tmxTile.getTileColumn()];
                    tile.setContent(oldTile.getContent());
                    tile.setGround(oldTile.getGround());
                    tile.setSubContent(oldTile.getSubContent());
                    newTiles[tmxTile.getTileRow()][tmxTile.getTileColumn()] = tile;

                    if (tile.getContent() != null) {
                        tile.getContent().setTilePosition(tile);
                        objects.add(tile.getContent());
                    }
                    tile.setVisible(false);
                }
            }
            tiles = newTiles;
            tiledMap.getTMXLayers().get(1).setAlwaysVisible(false);
            tiledMap.getTMXLayers().get(1).setTMXTiles(tiles);
            tiledMap.getTMXLayers().get(3).setVisible(false);
        } else if (tiles == null) {
            // create new room
            tiles = new Tile[tiledMap.getTileRows()][tiledMap.getTileColumns()];
            objects = new ArrayList<>();

            // add ground tiles
            TMXLayer groundLayer = tiledMap.getTMXLayers().get(1);
            Tile tile;
            for (TMXTile[] tmxTiles : groundLayer.getTMXTiles()) {
                for (TMXTile tmxTile : tmxTiles) {
                    tile = new Tile(tmxTile, tiledMap);
                    tile.setVisible(false);
                    tiles[tmxTile.getTileRow()][tmxTile.getTileColumn()] = tile;
                }
            }

            // add objects
            TMXLayer objectsLayer = tiledMap.getTMXLayers().get(3);
            objectsLayer.setVisible(false);
            TMXProperty object;
            for (TMXTile[] tmxTiles : objectsLayer.getTMXTiles()) {
                for (TMXTile tmxTile : tmxTiles) {
                    if (tmxTile.getTMXTileProperties(tiledMap) != null && tmxTile.getTMXTileProperties(tiledMap).size() > 0) {
                        object = tmxTile.getTMXTileProperties(tiledMap).get(0);
                        if (object.getName().equals(ObjectsTiles.ENTRANCE.name())) {
                            tiles[tmxTile.getTileRow()][tmxTile.getTileColumn()].getSubContent().add(new Stairs(false));
                            entranceTile = tiles[tmxTile.getTileRow()][tmxTile.getTileColumn()];
                        } else if (object.getName().equals(ObjectsTiles.EXIT.name())) {
                            tiles[tmxTile.getTileRow()][tmxTile.getTileColumn()].getSubContent().add(new Stairs(true));
                        } else if (object.getName().equals(ObjectsTiles.DOOR.name())) {
                            tiles[tmxTile.getTileRow()][tmxTile.getTileColumn()].getSubContent().add(new Door(object.getValue().equals("1")));
                        } else if (object.getName().equals(ObjectsTiles.TRAP.name())) {
                            addGameElement(TrapFactory.getTrap(), tiles[tmxTile.getTileRow()][tmxTile.getTileColumn()]);
                        } else if (object.getName().equals(ObjectsTiles.DECO.name())) {
                            addGameElement(DecorationFactory.getRandomSearchable(), tiles[tmxTile.getTileRow()][tmxTile.getTileColumn()]);
                        } else if (object.getName().equals(ObjectsTiles.MONSTER.name())) {
                            addGameElement(MonsterFactory.getWanderingMonster(), tiles[tmxTile.getTileRow()][tmxTile.getTileColumn()]);
                        } else if (object.getName().equals(ObjectsTiles.BOSS.name())) {
                            addGameElement(boss, tiles[tmxTile.getTileRow()][tmxTile.getTileColumn()]);
                        } else if (object.getName().equals(ObjectsTiles.RELIC.name())) {
                            addGameElement(relic, tiles[tmxTile.getTileRow()][tmxTile.getTileColumn()]);
                        }
                    }
                }
            }

            tiledMap.getTMXLayers().get(1).setAlwaysVisible(false);
            tiledMap.getTMXLayers().get(1).setTMXTiles(tiles);
        }
    }

    private enum ObjectsTiles {
        ENTRANCE, EXIT, DOOR, TRAP, MONSTER, DECO, BOSS, RELIC
    }

    public Tile getEntranceTile() {
        return entranceTile;
    }

}
