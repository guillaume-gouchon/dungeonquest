package com.giggs.heroquest.game;

import android.content.DialogInterface;
import android.util.Log;

import com.giggs.heroquest.R;
import com.giggs.heroquest.activities.games.GameActivity;
import com.giggs.heroquest.data.characters.HeroFactory;
import com.giggs.heroquest.data.characters.MonsterFactory;
import com.giggs.heroquest.data.items.ItemFactory;
import com.giggs.heroquest.game.base.GameElement;
import com.giggs.heroquest.game.base.InputManager;
import com.giggs.heroquest.game.base.interfaces.OnActionExecuted;
import com.giggs.heroquest.game.base.interfaces.UserActionListener;
import com.giggs.heroquest.game.graphics.ActionTile;
import com.giggs.heroquest.game.graphics.UnitSprite;
import com.giggs.heroquest.models.Actions;
import com.giggs.heroquest.models.FightResult;
import com.giggs.heroquest.models.Reward;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.models.characters.Monster;
import com.giggs.heroquest.models.characters.Ranks;
import com.giggs.heroquest.models.characters.Unit;
import com.giggs.heroquest.models.dungeons.Directions;
import com.giggs.heroquest.models.dungeons.Tile;
import com.giggs.heroquest.models.dungeons.decorations.Door;
import com.giggs.heroquest.models.dungeons.decorations.ItemOnGround;
import com.giggs.heroquest.models.dungeons.decorations.Searchable;
import com.giggs.heroquest.models.dungeons.decorations.Stairs;
import com.giggs.heroquest.models.dungeons.traps.Trap;
import com.giggs.heroquest.models.effects.BuffEffect;
import com.giggs.heroquest.models.effects.DamageEffect;
import com.giggs.heroquest.models.effects.Effect;
import com.giggs.heroquest.models.effects.InvocationEffect;
import com.giggs.heroquest.models.effects.PoisonEffect;
import com.giggs.heroquest.models.effects.RecoveryEffect;
import com.giggs.heroquest.models.effects.StunEffect;
import com.giggs.heroquest.models.items.Characteristics;
import com.giggs.heroquest.models.skills.ActiveSkill;
import com.giggs.heroquest.models.skills.Skill;
import com.giggs.heroquest.utils.pathfinding.AStar;
import com.giggs.heroquest.utils.pathfinding.MathUtils;

import org.andengine.engine.handler.timer.ITimerCallback;
import org.andengine.engine.handler.timer.TimerHandler;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.sprite.Sprite;
import org.andengine.extension.tmx.TMXTile;
import org.andengine.util.color.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by guillaume on 10/14/14.
 */
public class ActionsDispatcher implements UserActionListener {

    private static final String TAG = "ActionDispatcher";

    private final GameActivity mGameActivity;
    private final GUIManager mGUIManager;
    private final InputManager mInputManager;
    private Scene mScene;
    private boolean isMoving = false;
    private boolean inputDisabled = false;
    private TimerHandler animationHandler;
    private ActiveSkill activatedSkill = null;

    public ActionsDispatcher(GameActivity gameActivity, Scene scene) {
        mGameActivity = gameActivity;
        mInputManager = mGameActivity.getInputManager();
        mGUIManager = mGameActivity.getGUIManager();
        mScene = scene;
    }

    @Override
    public void onMove(float x, float y) {
    }

    @Override
    public void onTap(float x, float y) {
        Log.d(TAG, "onTap received, disabled ? " + inputDisabled);
        if (inputDisabled) return;

        Tile tile = getTileAtCoordinates(x, y);
        if (tile != null) {
            if (activatedSkill != null) {
                useSkill(tile);
            } else if (tile.getContent() != null && tile.getContent() == mGameActivity.getActiveCharacter() && tile.getSubContent().size() == 0) {
                // end movement
                mGameActivity.nextTurn();
            } else if (!isMoving && tile.getAction() != null) {
                executeAction(tile);
            } else if (mGameActivity.getQuest().isSafe() && mGameActivity.getActiveCharacter().getRank() == Ranks.ME && mGameActivity.getActiveCharacter().getTilePosition() == tile && tile.getSubContent().size() > 0
                    && tile.getSubContent().get(0) instanceof Stairs) {
                enterStairs();
            }
        }
    }

    @Override
    public void onCancel(float x, float y) {
    }

    @Override
    public void onPinchZoom(float zoomFactor) {
    }

    private void executeAction(Tile tile) {
        switch (tile.getAction()) {
            case MOVE:
                move(tile);
                break;
            case ATTACK:
                attack(tile);
                break;
            case SEARCH:
                search(tile);
                break;
        }
    }

    private void move(Tile tile) {
        Log.d(TAG, "move to " + tile.getX() + "," + tile.getY());
        setInputEnabled(false);

        List<Tile> path = new AStar<Tile>().search(mGameActivity.getQuest().getTiles(), mGameActivity.getActiveCharacter().getTilePosition(), tile, false, mGameActivity.getActiveCharacter());
        if (path != null) {
            isMoving = true;
            animateMove(path, new OnActionExecuted() {
                private boolean done = false;

                @Override
                public void onActionDone(boolean success) {
                    isMoving = false;
                    if (!done) {
                        done = true;
                        if ((mGameActivity.getActiveCharacter().getRank() == Ranks.ME || mGameActivity.getActiveCharacter().getRank() == Ranks.ALLY) && mGameActivity.getActiveCharacter().getTilePosition().getSubContent().size() > 0 && mGameActivity.getActiveCharacter().getTilePosition().getSubContent().get(0) instanceof Door) {
                            // open doors
                            ((Door) mGameActivity.getActiveCharacter().getTilePosition().getSubContent().get(0)).open();
                        } else if (mGameActivity.getQuest().isSafe() && mGameActivity.getActiveCharacter().getRank() == Ranks.ME && mGameActivity.getActiveCharacter().getTilePosition().getSubContent().size() > 0
                                && mGameActivity.getActiveCharacter().getTilePosition().getSubContent().get(0) instanceof Stairs) {
                            enterStairs();
                        }

                        // detect traps
                        if (mGameActivity.getActiveCharacter().getRank() == Ranks.ME || mGameActivity.getActiveCharacter().getRank() == Ranks.ALLY) {
                            Set<Tile> tiles = MathUtils.getAdjacentNodes(mGameActivity.getQuest().getTiles(), mGameActivity.getActiveCharacter().getTilePosition(), mGameActivity.getActiveCharacter().getSpirit(), true, null);
                            int factor = mGameActivity.getActiveCharacter().getIdentifier().equals(HeroFactory.buildThief().getIdentifier()) ? 2 : 1;
                            int spirit = mGameActivity.getActiveCharacter().getSpirit();
                            boolean hasDetectTraps = false;
                            for (Tile t : tiles) {
                                if (t.getSubContent().size() > 0 && t.getSubContent().get(0) instanceof Trap && !((Trap) t.getSubContent().get(0)).isRevealed()) {
                                    if (Math.random() < 0.1 * spirit * factor) {
                                        ((Trap) t.getSubContent().get(0)).reveal();
                                        hasDetectTraps = true;
                                    }
                                }
                            }
                            if (hasDetectTraps) {
                                showAnimatedText(mGameActivity.getActiveCharacter(), mGameActivity.getString(R.string.detect_trap));
                            }
                        }

                        mGameActivity.nextTurn();
                    }
                }
            });
        } else {
            Log.d(TAG, "path is null");
            hideActionTiles();
            if (mGameActivity.getActiveCharacter().getRank() == Ranks.ME || mGameActivity.getActiveCharacter().getRank() == Ranks.ALLY) {
                setInputEnabled(true);
            }
        }
    }

    private void enterStairs() {
        hideActionTiles();
        showSpecialActions();

        mGameActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (((Stairs) mGameActivity.getActiveCharacter().getTilePosition().getSubContent().get(0)).isDownStairs()) {
                    mGUIManager.showFinishQuestConfirmDialog();
                } else {
                    mGUIManager.showLeaveQuestConfirmDialog();
                }
            }
        });
    }

    public void attack(final Tile tile) {
        Log.d(TAG, "attack");
        setInputEnabled(false);

        OnActionExecuted callback = new OnActionExecuted() {
            @Override
            public void onActionDone(boolean success) {
                isMoving = false;
                if (success && (mGameActivity.getActiveCharacter().isRangeAttack() || mGameActivity.getActiveCharacter().isNextTo(tile))) {
                    final Unit target = (Unit) tile.getContent();
                    final FightResult fightResult = mGameActivity.getActiveCharacter().attack(target);
                    rollFightDice(fightResult, target);
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            animateFight(mGameActivity.getActiveCharacter(), target, fightResult, new OnActionExecuted() {
                                @Override
                                public void onActionDone(boolean success) {
                                    if (target.isDead()) {
                                        if (mGameActivity.getActiveCharacter().getRank() == Ranks.ME) {
                                            ((Hero) mGameActivity.getActiveCharacter()).addFrag(target.getIdentifier());
                                        }

                                        animateDeath(target, new OnActionExecuted() {
                                            @Override
                                            public void onActionDone(boolean success) {
                                                if (target.getRank() != Ranks.ME) {
                                                    mGameActivity.removeElement(target);
                                                }
                                                mGameActivity.nextTurn();
                                            }
                                        });
                                    } else {
                                        mGameActivity.nextTurn();
                                    }
                                }
                            });
                        }
                    }, 600);

                } else {
                    mGameActivity.nextTurn();
                }
            }
        };

        if (mGameActivity.getActiveCharacter().isRangeAttack()) {
            callback.onActionDone(true);
        } else {
            goCloserTo(tile, callback);
        }
    }

    private void search(final Tile tile) {
        Log.d(TAG, "search");
        setInputEnabled(false);

        goCloserTo(tile, new OnActionExecuted() {
            @Override
            public void onActionDone(boolean success) {
                if (success && mGameActivity.getActiveCharacter().isNextTo(tile)) {
                    mGameActivity.playSound("search", false);

                    Searchable searchable;
                    if (tile.getContent() != null && tile.getContent() instanceof Searchable) {
                        searchable = (Searchable) tile.getContent();
                    } else {
                        if (tile.getSubContent().get(0) instanceof Stairs) {
                            searchable = (Searchable) tile.getSubContent().get(1);
                        } else {
                            searchable = (Searchable) tile.getSubContent().get(0);
                        }
                    }
                    final Reward reward = searchable.search();
                    if (searchable instanceof ItemOnGround) {
                        mGameActivity.removeElement(searchable);
                    }

                    if (reward != null) {
                        if (reward.getItem() != null) {
                            mGameActivity.getHero().addItem(reward.getItem());
                        }
                        mGameActivity.getHero().addGold(reward.getGold());
                    }

                    // show reward popup
                    mGUIManager.showReward(reward, new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialogInterface) {
                            if (reward != null) {
                                animateReward(reward);

                                if (reward.getIdentifier() != null) {
                                    if (reward.getIdentifier().equals("tr_wandering_monster")) {
                                        // wandering monster
                                        Monster monster = MonsterFactory.getWanderingMonster();
                                        Tile adjacentTile;
                                        Set<Tile> adjacentTiles = MathUtils.getAdjacentNodes(mGameActivity.getQuest().getTiles(), tile, 2, true, monster);
                                        List<Tile> shuffle = new ArrayList<>(adjacentTiles);
                                        Collections.shuffle(shuffle);
                                        if (shuffle.get(0).getContent() == null) {
                                            mGameActivity.getQuest().addGameElement(monster, shuffle.get(0), true);
                                            mGameActivity.addElementToScene(monster);
                                        }
                                    } else if (reward.getIdentifier().equals("tr_trap_1") || reward.getIdentifier().equals("tr_trap_2")) {
                                        // traps
                                        applyEffect(new DamageEffect("ground_slam.png", -1, 0), mGameActivity.getActiveCharacter().getTilePosition(), false);
                                    } else if (reward.getIdentifier().equals("tr_magic_trap")) {
                                        // magical trap
                                        Set<Tile> zone = MathUtils.getAdjacentNodes(mGameActivity.getQuest().getTiles(), tile, 2, true, null);
                                        for (Tile t : zone) {
                                            applyEffect(new DamageEffect("fireball.png", -1, 0), t, false);
                                        }
                                    }
                                }
                            }

                            mGameActivity.nextTurn();
                        }
                    });
                } else {
                    mGameActivity.nextTurn();
                }
                isMoving = false;
            }
        });
    }

    private void goCloserTo(Tile tile, OnActionExecuted callback) {
        if (mGameActivity.getActiveCharacter().isNextTo(tile)) {
            callback.onActionDone(true);
            return;
        }

        List<Tile> shortestPath = null;
        if (mGameActivity.getActiveCharacter().canMoveIn(tile)) {
            shortestPath = new AStar<Tile>().search(mGameActivity.getQuest().getTiles(), mGameActivity.getActiveCharacter().getTilePosition(), tile, false, mGameActivity.getActiveCharacter());
        } else {
            Set<Tile> adjacentTiles = MathUtils.getAdjacentNodes(mGameActivity.getQuest().getTiles(), tile, 1, false, mGameActivity.getActiveCharacter());
            for (Tile adjacent : adjacentTiles) {
                if (adjacent.getAction() == Actions.MOVE) {
                    List<Tile> path = new AStar<Tile>().search(mGameActivity.getQuest().getTiles(), mGameActivity.getActiveCharacter().getTilePosition(), adjacent, false, mGameActivity.getActiveCharacter());
                    if (path != null && (shortestPath == null || path.size() < shortestPath.size())) {
                        shortestPath = path;
                    }
                }
            }
            if (shortestPath == null) {
                for (Tile adjacent : adjacentTiles) {
                    List<Tile> path = new AStar<Tile>().search(mGameActivity.getQuest().getTiles(), mGameActivity.getActiveCharacter().getTilePosition(), adjacent, false, mGameActivity.getActiveCharacter());
                    if (path != null && (shortestPath == null || path.size() < shortestPath.size())) {
                        shortestPath = path;
                    }
                }
                if (shortestPath != null) {
                    shortestPath = shortestPath.subList(0, Math.min(mGameActivity.getActiveCharacter().calculateMovement() + 1, shortestPath.size()));
                }
            }
        }

        if (shortestPath != null) {
            isMoving = true;
            animateMove(shortestPath, callback);
        } else {
            callback.onActionDone(false);
        }
    }

    private Tile getTileAtCoordinates(float x, float y) {
        TMXTile tmxTile = mGameActivity.mTmxTiledMap.getTMXLayers().get(0).getTMXTileAt(x, y);
        if (tmxTile != null) {
            return mGameActivity.getQuest().getTiles()[tmxTile.getTileRow()][tmxTile.getTileColumn()];
        } else {
            return null;
        }
    }

    public void showAllActions(Tile fromTile) {
        Unit activeCharacter = mGameActivity.getActiveCharacter();

        // get reachable tiles
        Set<Tile> reachableTiles = new HashSet<>();
        Tile t;
        for (Tile[] hTile : mGameActivity.getQuest().getTiles()) {
            for (Tile tile : hTile) {
                if (tile != null && !reachableTiles.contains(tile) && MathUtils.calcManhattanDistance(tile, fromTile) <= activeCharacter.calculateMovement()
                        && activeCharacter.canMoveIn(tile)) {
                    List<Tile> path = new AStar<Tile>().search(mGameActivity.getQuest().getTiles(), fromTile, tile, false, activeCharacter);
                    if (path != null) {
                        for (int n = 0; n < path.size(); n++) {
                            t = path.get(n);
                            if (n <= activeCharacter.calculateMovement()) {
                                reachableTiles.add(t);
                                if (t.getSubContent().size() > 0 && t.getSubContent().get(0) instanceof Door && !((Door) t.getSubContent().get(0)).isOpen()) {
                                    break;
                                }
                            }
                        }
                    }
                }
            }
        }

        // add movement tiles
        ActionTile c;
        for (Tile tile : reachableTiles) {
            tile.setAction(Actions.MOVE);
            c = new ActionTile(Actions.MOVE, tile, mGameActivity.getVertexBufferObjectManager(), activeCharacter.getRank() == Ranks.ENEMY);
            mGameActivity.mGroundLayer.attachChild(c);
        }

        // add special actions
        if (activeCharacter.getRank() == Ranks.ME || activeCharacter.getRank() == Ranks.ALLY) {
            showSpecialActions();
        }
    }

    public void showSpecialActions() {
        for (GameElement gameElement : mGameActivity.getQuest().getObjects()) {
            if (mGameActivity.getActiveCharacter() != gameElement
                    && ((MathUtils.calcManhattanDistance(gameElement.getTilePosition(), mGameActivity.getActiveCharacter().getTilePosition()) <= mGameActivity.getActiveCharacter().calculateMovement() + 1)
                    || mGameActivity.getActiveCharacter().isRangeAttack() && gameElement.isEnemy(mGameActivity.getActiveCharacter()))) {
                addAvailableAction(gameElement);
            }
        }
    }

    public void hideActionTiles() {
        ActionTile actionTile;
        for (int n = 0; n < mGameActivity.mGroundLayer.getChildCount(); n++) {
            actionTile = (ActionTile) mGameActivity.mGroundLayer.getChildByIndex(n);
            actionTile.getTile().setAction(null);
            actionTile.getTile().setSelected(false);
        }
        mGameActivity.mGroundLayer.detachChildren();
    }

    private void addAvailableAction(GameElement gameElement) {
        if (gameElement instanceof Trap) {
            return;
        }

        boolean isActionPossible = mGameActivity.getActiveCharacter().isNextTo(gameElement.getTilePosition())
                || mGameActivity.getActiveCharacter().isRangeAttack() && gameElement.isEnemy(mGameActivity.getActiveCharacter());
        if (!isActionPossible) {
            Set<Tile> adjacentTiles = MathUtils.getAdjacentNodes(mGameActivity.getQuest().getTiles(), gameElement.getTilePosition(), 1, false, mGameActivity.getActiveCharacter());
            for (Tile adjacent : adjacentTiles) {
                if (adjacent.getAction() == Actions.MOVE) {
                    isActionPossible = true;
                    break;
                }
            }
        }

        if (isActionPossible) {
            if (mGameActivity.getActiveCharacter().isEnemy(gameElement)) {
                addActionToTile(Actions.ATTACK, gameElement.getTilePosition());
            } else if (gameElement instanceof Searchable && mGameActivity.getActiveCharacter().getRank() == Ranks.ME) {
                addActionToTile(Actions.SEARCH, gameElement.getTilePosition());
            }
        }
    }

    private void addActionToTile(Actions action, Tile tile) {
        ActionTile actionTile = new ActionTile(action, tile, mGameActivity.getVertexBufferObjectManager(), false);
        tile.setAction(action);
        mGameActivity.mGroundLayer.attachChild(actionTile);
    }

    private void animateMove(List<Tile> path, final OnActionExecuted callback) {
        Log.d(TAG, "animate movement");
        if (path.size() > 1) {
            path.remove(0);
            Log.d(TAG, "path size = " + path.size());
            final UnitSprite sprite = (UnitSprite) mGameActivity.getActiveCharacter().getSprite();
            final Tile nextTile = path.get(0);
            final Directions direction = Directions.from(nextTile.getX() - mGameActivity.getActiveCharacter().getTilePosition().getX(), mGameActivity.getActiveCharacter().getTilePosition().getY() - nextTile.getY());
            sprite.walk(direction);
            final List<Tile> p = new ArrayList<>(path);
            animationHandler = new TimerHandler(1.0f / 60, true, new ITimerCallback() {
                @Override
                public void onTimePassed(TimerHandler pTimerHandler) {
                    if (direction == Directions.EAST && sprite.getX() >= nextTile.getTileX()
                            || direction == Directions.WEST && sprite.getX() <= nextTile.getTileX()
                            || direction == Directions.SOUTH && sprite.getY() >= nextTile.getTileY()
                            || direction == Directions.NORTH && sprite.getY() <= nextTile.getTileY()) {

                        Log.d(TAG, "unregister movement animation handler");
                        animationHandler.reset();
                        mScene.unregisterUpdateHandler(animationHandler);

                        mGameActivity.getActiveCharacter().setTilePosition(nextTile);
                        sprite.setPosition(nextTile.getTileX(), nextTile.getTileY());
                        Log.d(TAG, "character is z-index = " + sprite.getZIndex());

                        mScene.sortChildren();

                        if (nextTile.getSubContent().size() > 0 && nextTile.getSubContent().get(0) instanceof Trap) {
                            isMoving = false;
                            sprite.stand();
                            Trap trap = (Trap) nextTile.getSubContent().get(0);
                            if (trap.isRevealed()
                                    && (mGameActivity.getActiveCharacter().getIdentifier().equals(HeroFactory.buildDwarf())
                                    || mGameActivity.getActiveCharacter().hasItem(ItemFactory.buildToolbox()))) {
                                Log.d(TAG, "disarm trap !");
                                showAnimatedText(mGameActivity.getActiveCharacter(), mGameActivity.getString(R.string.disarming_trap));
                                mGameActivity.removeElement(trap);
                                mGameActivity.nextTurn();
                            } else {
                                Log.d(TAG, "trap !");
                                boolean wasRevealed = trap.isRevealed();
                                trap.reveal();

                                boolean isDamaged = Math.random() < (1 - Math.pow(0.5, trap.getAttackDice())) / (trap.isRevealed() ? 4 : 1);
                                if (isDamaged) {
                                    applyEffect(new DamageEffect(null, -1, 0), mGameActivity.getActiveCharacter().getTilePosition(), true);
                                }
                                rollFightDie(mGameActivity.getActiveCharacter(), isDamaged ? 0 : 1, 0, 1, true);

                                if (isDamaged || !wasRevealed) {
                                    // stop movement if not revealed or damaged
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            mGameActivity.nextTurn();
                                        }
                                    }, 700);
                                } else {
                                    // continue movement
                                    animateMove(p, callback);
                                }
                            }
                        } else {
                            animateMove(p, callback);
                        }
                    } else {
                        sprite.setPosition(sprite.getX() + 2 * direction.getDx(), sprite.getY() - 2 * direction.getDy());
                        mInputManager.checkAutoScrolling(sprite.getX(), sprite.getY());
                    }
                }
            });
            mScene.registerUpdateHandler(animationHandler);
        } else {
            Log.d(TAG, "movement animation is over");
            UnitSprite sprite = (UnitSprite) mGameActivity.getActiveCharacter().getSprite();
            sprite.stand();
            mScene.sortChildren();
            mScene.unregisterUpdateHandler(animationHandler);
            callback.onActionDone(true);
        }
    }

    private void animateFight(final Unit attacker, final Unit target, final FightResult fightResult, final OnActionExecuted callback) {
        final UnitSprite attackerSprite = (UnitSprite) attacker.getSprite();
        final Sprite targetSprite = target.getSprite();

        if (attacker.isNextTo(target.getTilePosition())) {
            mGameActivity.playSound("close_combat_attack", false);
        } else if (attacker.isRangeAttack()) {
            mGameActivity.playSound("range_attack", false);
        }

        // play sound
        if (fightResult.getState() == FightResult.States.BLOCK) {
            mGameActivity.playSound("block", false);
        } else if (fightResult.getState() == FightResult.States.DAMAGE && fightResult.getDamage() > 0) {
            if (target.getRank() == Ranks.ME || target.getRank() == Ranks.ALLY) {
                mGameActivity.playSound("damage_hero", false);
            } else {
                mGameActivity.playSound("damage_monster", false);
            }
        }

        // draw damage and fight result text
        if (fightResult.getState() == FightResult.States.DAMAGE || fightResult.getState() == FightResult.States.CRITICAL) {
            showAnimatedText(target, "-" + fightResult.getDamage());
        }
        if (fightResult.getState() != FightResult.States.DAMAGE) {
            mGameActivity.drawAnimatedText(targetSprite.getX() + GameConstants.PIXEL_BY_TILE / 3, targetSprite.getY() - GameConstants.PIXEL_BY_TILE, fightResult.getState().name().toLowerCase(), fightResult.getState().getColor(), 0.4f, 40, -0.15f);
        }

        // animate characters
        final Directions direction = Directions.from(targetSprite.getX() - attackerSprite.getX(), targetSprite.getY() - attackerSprite.getY());
        attackerSprite.changeOrientation(Directions.from(targetSprite.getX() - attackerSprite.getX(), attackerSprite.getY() - targetSprite.getY()));
        animationHandler = new TimerHandler(1.0f / 50, true, new ITimerCallback() {

            private static final int DURATION_IN_FRAMES = 17;
            private static final int OFFSET = 5;
            private static final float ATTACKER_SPEED = 3f;
            private static final float TARGET_SPEED = 1.2f;

            private int offset = OFFSET;

            private boolean done = false;

            @Override
            public void onTimePassed(TimerHandler pTimerHandler) {
                offset--;
                if (offset >= -OFFSET) {
                    attackerSprite.setPosition(attackerSprite.getX() + (offset >= 0 ? ATTACKER_SPEED : -ATTACKER_SPEED) * direction.getDx(), attackerSprite.getY() + (offset >= 0 ? ATTACKER_SPEED : -ATTACKER_SPEED) * direction.getDy());
                    if (fightResult.getState() == FightResult.States.DAMAGE || fightResult.getState() == FightResult.States.CRITICAL) {
                        targetSprite.setPosition(targetSprite.getX(), targetSprite.getY() - (offset >= 0 ? TARGET_SPEED : -TARGET_SPEED) * Directions.NORTH.getDy());
                        targetSprite.setColor(1.0f, 0.0f, 0.0f);
                    } else if (fightResult.getState() == FightResult.States.DODGE) {
                        targetSprite.setPosition(targetSprite.getX() + (offset >= 0 ? ATTACKER_SPEED : -ATTACKER_SPEED) * direction.getDx(), targetSprite.getY() + (offset >= 0 ? ATTACKER_SPEED : -ATTACKER_SPEED) * direction.getDy());
                    }
                } else if (offset <= -DURATION_IN_FRAMES + OFFSET) {
                    targetSprite.setColor(1.0f, 1.0f, 1.0f);
                    mScene.unregisterUpdateHandler(animationHandler);
                    if (!done) {
                        done = true;
                        attackerSprite.stand();
                        target.updateSprite();
                        callback.onActionDone(true);
                    }
                }
            }
        });
        mScene.registerUpdateHandler(animationHandler);
    }

    private void showAnimatedText(Unit target, String message) {
        mGameActivity.drawAnimatedText(target.getSprite().getX() - 2 * GameConstants.PIXEL_BY_TILE / 3, target.getSprite().getY() - GameConstants.PIXEL_BY_TILE, message, message.startsWith("-") ? FightResult.States.DAMAGE.getColor() : FightResult.States.DODGE.getColor(), 0.4f, 40, -0.15f);
    }

    public void animateDeath(final Unit target, final OnActionExecuted onActionExecuted) {
        Log.d(TAG, "animating death");
        Sprite sprite = target.getSprite();
        mGameActivity.drawAnimatedSprite(sprite.getX(), sprite.getY(), "blood.png", 65, GameConstants.ANIMATED_SPRITE_ALPHA, 1.0f, 0, true, 10, new OnActionExecuted() {
            @Override
            public void onActionDone(boolean success) {
                onActionExecuted.onActionDone(true);
            }
        });
        mGameActivity.playSound("death", false);
        sprite.setRotation(90);
        sprite.setPosition(sprite.getX() + 10, sprite.getY() + 10);
    }

    private void animateReward(Reward reward) {
        if (reward.getGold() > 0) {
            mGameActivity.playSound("coins", false);
            mGameActivity.drawAnimatedText(mGameActivity.getHero().getSprite().getX() - 4 * GameConstants.PIXEL_BY_TILE / 3, mGameActivity.getHero().getSprite().getY() - GameConstants.PIXEL_BY_TILE, "+" + reward.getGold() + " gold", Color.YELLOW, 0.4f, 50, -0.15f);
        }
    }

    public ActiveSkill getActivatedSkill() {
        return activatedSkill;
    }

    public void setActivatedSkill(ActiveSkill skill) {
        if (skill.isPersonal()) {
            activatedSkill = skill;
            mGUIManager.displayBigLabel(mGameActivity.getString(R.string.use_skill_personal, mGameActivity.getString(skill.getName(mGameActivity.getResources()))), R.color.green);
            useSkill(mGameActivity.getActiveCharacter().getTilePosition());
        } else if (activatedSkill == skill) {
            activatedSkill = null;
            mGUIManager.displayBigLabel(mGameActivity.getString(R.string.use_skill_off, mGameActivity.getString(skill.getName(mGameActivity.getResources()))), R.color.red);
        } else {
            activatedSkill = skill;
            mGUIManager.displayBigLabel(mGameActivity.getString(R.string.use_skill_on, mGameActivity.getString(skill.getName(mGameActivity.getResources()))), R.color.green);
        }
    }

    public void useSkill(final Tile tile) {
        setInputEnabled(false);
        mGUIManager.displayBigLabel(mGameActivity.getString(R.string.use_skill_personal, mGameActivity.getString(activatedSkill.getName(mGameActivity.getResources()))), R.color.green);
        mGameActivity.playSound("magic", false);

        animateSkill(new OnActionExecuted() {
            @Override
            public void onActionDone(boolean success) {
                Effect effect = activatedSkill.getEffect();
                if (!activatedSkill.isPersonal() || activatedSkill.getRadius() == 0) {
                    applyEffect(effect, tile, true);
                }
                if (activatedSkill.getRadius() > 0) {
                    Set<Tile> targetTiles = MathUtils.getAdjacentNodes(mGameActivity.getQuest().getTiles(), tile, activatedSkill.getRadius(), true, null);
                    for (Tile targetTile : targetTiles) {
                        if (targetTile != mGameActivity.getActiveCharacter().getTilePosition()) {
                            applyEffect(effect, targetTile, true);
                        }
                    }
                }

                activatedSkill.use();
                activatedSkill = null;
                mGUIManager.updateSkillButtons();

                // go to next turn when everyone is dead
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mGameActivity.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mGameActivity.nextTurn();
                            }
                        });
                    }
                }, 700);
            }
        });
    }

    public void applyEffect(Effect effect, final Tile tile, boolean addExtra) {
        Log.d(TAG, "Applying effect of " + effect.getValue() + " " + effect.getTarget() + " on tile " + tile.getX() + "," + tile.getY());

        if (effect instanceof InvocationEffect) {
            if (tile.getContent() == null) {
                Set<Tile> tiles = MathUtils.getAdjacentNodes(mGameActivity.getQuest().getTiles(), tile, 2, true, mGameActivity.getActiveCharacter());
                ArrayList<Tile> shuffle = new ArrayList<>(tiles);
                Collections.shuffle(shuffle);
                mGameActivity.getQuest().addGameElement(((InvocationEffect) effect).getUnit(), shuffle.get(0));
                mGameActivity.addElementToScene(((InvocationEffect) effect).getUnit());
            }
            return;
        }

        // apply effect
        if (tile.getContent() != null && tile.getContent() instanceof Unit) {
            final Unit target = (Unit) tile.getContent();
            if (effect instanceof RecoveryEffect) {
                // recover all skills
                for (Skill skill : target.getSkills()) {
                    if (skill instanceof ActiveSkill) {
                        ((ActiveSkill) skill).reset();
                    }
                }
                mGUIManager.updateSkillButtons();
            } else if (effect.getTarget() == Characteristics.HP) {
                // holy water
                if (activatedSkill != null && activatedSkill.getIdentifier().equals(ItemFactory.buildHolyWater().getIdentifier()) &&
                        !target.getIdentifier().equals(MonsterFactory.buildSkeleton().getIdentifier()) && !target.getIdentifier().equals(MonsterFactory.buildZombie().getIdentifier())
                        && !target.getIdentifier().equals(MonsterFactory.buildMummy().getIdentifier())) {
                    // only damage undead
                } else {
                    // damage or heal
                    if (effect instanceof PoisonEffect && effect.getValue() > 0 && target.getHp() - target.getCurrentHP() == 0) {
                        // do not show useless regeneration
                    } else {
                        showAnimatedText(target, effect.getValue() > 0 ? "+" + Math.min(target.getHp() - target.getCurrentHP(), effect.getValue()) : "" + effect.getValue());
                    }
                    target.setCurrentHP(Math.min(target.getHp(), target.getCurrentHP() + effect.getValue()));
                }
            } else {
                // special effects
                target.getBuffs().add(effect);

                if (!(effect instanceof StunEffect) && effect.getTarget() != null) {
                    showAnimatedText(target, (effect.getValue() > 0 ? "+" + effect.getValue() : "" + effect.getValue()) + " " + effect.getTarget().name().toLowerCase() + (effect.getValue() > 1 ? " dice" : "die"));
                }
            }

            if (addExtra && effect.getSpecial() != null) {
                target.getBuffs().add(effect.getSpecial());
            }

            if (target.isDead()) {
                // animate deaths and remove dead units
                animateDeath(target, new OnActionExecuted() {
                    @Override
                    public void onActionDone(boolean success) {
                    }
                });
                mGameActivity.removeElement(target);
            }

            target.updateSprite();
        }

        // animate
        if (effect.getSpriteName() != null) {
            mGameActivity.drawAnimatedSprite(tile.getTileX(), tile.getTileY(), effect.getSpriteName(), 50, GameConstants.ANIMATED_SPRITE_ALPHA, 1.0f, 0, true, 100, null);
        }

        mGUIManager.updateHeroLayout();
        mGUIManager.updateQueue(mGameActivity.getActiveCharacter(), mGameActivity.getQuest());
    }

    public void setInputEnabled(boolean enabled) {
        inputDisabled = !enabled;
        mInputManager.setEnabled(enabled);
    }

    private void animateSkill(final OnActionExecuted callback) {
        final UnitSprite sprite = (UnitSprite) mGameActivity.getActiveCharacter().getSprite();
        if (activatedSkill.getIdentifier().equals("swirl_swords")) {
            Set<Tile> targetTiles = MathUtils.getAdjacentNodes(mGameActivity.getQuest().getTiles(), mGameActivity.getActiveCharacter().getTilePosition(), activatedSkill.getRadius(), true, null);
            final Iterator<Tile> tileIterator = targetTiles.iterator();
            new Timer().schedule(new TimerTask() {
                private int n = 7;
                private Tile tile;

                @Override
                public void run() {
                    sprite.walk(Directions.values()[n % 4]);
                    tile = tileIterator.next();
                    mGameActivity.drawAnimatedSprite(tile.getTileX(), tile.getTileY(), "slash.png", 70, GameConstants.ANIMATED_SPRITE_ALPHA, 0.7f, 0, true, 100, null);
                    n--;
                    if (n < 0) {
                        sprite.stand();
                        cancel();
                        callback.onActionDone(true);
                    }
                }
            }, 0, 80);
        } else if (activatedSkill.getIdentifier().equals("courage")) {
            sprite.stand();
            sprite.setColor(((BuffEffect) activatedSkill.getEffect()).getBuffColor());
            new Timer().schedule(new TimerTask() {
                private int n = 7;

                @Override
                public void run() {
                    sprite.setScale((float) (0.4 + 0.03 * (n % 3)));
                    n--;
                    if (n < 0) {
                        sprite.setScale(0.4f);
                        cancel();
                        callback.onActionDone(true);
                    }
                }
            }, 0, 80);
        } else if (activatedSkill.getIdentifier().equals("camouflage")) {
            sprite.stand();
            new Timer().schedule(new TimerTask() {
                private int n = 7;

                @Override
                public void run() {
                    sprite.setAlpha((float) (0.3 + 0.1 * n));
                    n--;
                    if (n < 0) {
                        cancel();
                        callback.onActionDone(true);
                    }
                }
            }, 0, 80);
        } else if (activatedSkill.getIdentifier().equals("ground_slam")) {
            sprite.stand();
            new Timer().schedule(new TimerTask() {
                private int n = 12;
                private final float initialY = sprite.getY();
                private final float initialCameraX = mGameActivity.getCamera().getCenterX();

                @Override
                public void run() {
                    if (n >= 5) {
                        sprite.setPosition(sprite.getX(), (float) (initialY - 10 * Math.sin((n - 5) * Math.PI / 7)));
                    } else {
                        mGameActivity.getCamera().offsetCenter((float) (-3 * Math.cos(n * 2 * Math.PI / 5)), mGameActivity.getCamera().getCenterY());
                    }

                    n--;
                    if (n < 0) {
                        sprite.setPosition(sprite.getX(), initialY);
                        mGameActivity.getCamera().setCenter(initialCameraX, mGameActivity.getCamera().getCenterY());
                        cancel();
                        callback.onActionDone(true);
                    }
                }
            }, 0, 50);
        } else if (activatedSkill.getIdentifier().equals("drunken_master")) {
            sprite.stand();
            new Timer().schedule(new TimerTask() {
                private int n = 7;
                private final float initialX = sprite.getX();

                @Override
                public void run() {
                    sprite.setColor(Color.GREEN);
                    sprite.setPosition((float) (initialX - 3 * Math.cos(n * 2 * Math.PI / 7)), sprite.getY());

                    n--;
                    if (n < 0) {
                        sprite.setColor(Color.WHITE);
                        sprite.setPosition(initialX, sprite.getY());
                        cancel();
                        callback.onActionDone(true);
                    }
                }
            }, 0, 60);
        } else if (activatedSkill.getIdentifier().equals("war_cry")) {
            sprite.stand();
            new Timer().schedule(new TimerTask() {
                private int n = 8;
                private final float initialCameraX = mGameActivity.getCamera().getCenterX();

                @Override
                public void run() {
                    mGameActivity.getCamera().offsetCenter((float) (3 * Math.cos(n * 2 * Math.PI / 8)), mGameActivity.getCamera().getCenterY());

                    n--;
                    if (n < 0) {
                        mGameActivity.getCamera().setCenter(initialCameraX, mGameActivity.getCamera().getCenterY());
                        cancel();
                        callback.onActionDone(true);
                    }
                }
            }, 0, 50);
        } else if (activatedSkill.getIdentifier().equals("healing_plants")) {
            sprite.stand();
            new Timer().schedule(new TimerTask() {
                private int n = 7;

                @Override
                public void run() {
                    sprite.setColor(n % 4 == 0 ? Color.WHITE : new Color(0.0f, 0.6f, 0.0f));

                    n--;
                    if (n < 0) {
                        sprite.setColor(Color.WHITE);
                        cancel();
                        callback.onActionDone(true);
                    }
                }
            }, 0, 60);
        } else if (activatedSkill.getIdentifier().equals("rock_skin") || activatedSkill.getIdentifier().equals("darkness_winds")) {
            sprite.stand();
            sprite.setColor(((BuffEffect) activatedSkill.getEffect()).getBuffColor());

            new Timer().schedule(new TimerTask() {
                private int n = 7;

                @Override
                public void run() {
                    sprite.setScale((float) (0.4 + 0.07 * Math.sin(n * Math.PI / 7)));

                    n--;
                    if (n < 0) {
                        sprite.setScale(0.4f);
                        cancel();
                        callback.onActionDone(true);
                    }
                }
            }, 0, 60);
        } else {
            callback.onActionDone(true);
        }
    }

    private void rollFightDice(FightResult fightResult, Unit target) {
        // roll attack dice
        for (int n = 0; n < mGameActivity.getActiveCharacter().getAttackAgainst(target); n++) {
            rollFightDie(mGameActivity.getActiveCharacter(), n < fightResult.getAttackScore() ? 0 : 1, n, mGameActivity.getActiveCharacter().getAttackAgainst(target), true);
        }

        if (fightResult.getAttackScore() > 0) {
            // roll defense dice
            for (int n = 0; n < target.getDefense(); n++) {
                rollFightDie(target, n < fightResult.getDefenseScore() ? (target instanceof Monster ? 2 : 1) : 0, n, target.getDefense(), false);
            }
        }
    }

    private void rollFightDie(Unit character, final int result, int dieIndex, int nbDice, boolean isAttack) {
        final String diceSprite = isAttack ? "dice_attack.png" : "dice_defense.png";
        final float x = character.getSprite().getX() + (2 * dieIndex - nbDice) * 6;
        final float y = character.getSprite().getY() - 30;
        mGameActivity.drawAnimatedSprite(x, y, diceSprite, 20, 0.1f, 1.0f, 1, true, 1000, new OnActionExecuted() {
            @Override
            public void onActionDone(boolean success) {
                mGameActivity.drawSprite(x, y, diceSprite, 50, 0.1f, result);
            }
        });
    }

}
