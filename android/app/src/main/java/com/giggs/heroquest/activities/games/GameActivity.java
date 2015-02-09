package com.giggs.heroquest.activities.games;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.giggs.heroquest.R;
import com.giggs.heroquest.activities.GameOverActivity;
import com.giggs.heroquest.activities.fragments.StoryFragment;
import com.giggs.heroquest.data.QuestFactory;
import com.giggs.heroquest.data.characters.HeroFactory;
import com.giggs.heroquest.game.ActionsDispatcher;
import com.giggs.heroquest.game.GameConstants;
import com.giggs.heroquest.game.base.GameElement;
import com.giggs.heroquest.game.base.MyBaseGameActivity;
import com.giggs.heroquest.game.base.interfaces.OnActionExecuted;
import com.giggs.heroquest.game.graphics.SelectionCircle;
import com.giggs.heroquest.game.gui.items.ItemInfoInGame;
import com.giggs.heroquest.models.Game;
import com.giggs.heroquest.models.Quest;
import com.giggs.heroquest.models.characters.Ally;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.models.characters.Monster;
import com.giggs.heroquest.models.characters.Ranks;
import com.giggs.heroquest.models.characters.Unit;
import com.giggs.heroquest.models.dungeons.Directions;
import com.giggs.heroquest.models.dungeons.Tile;
import com.giggs.heroquest.models.dungeons.decorations.Door;
import com.giggs.heroquest.models.effects.CamouflageEffect;
import com.giggs.heroquest.models.effects.Effect;
import com.giggs.heroquest.models.effects.HeroicEffect;
import com.giggs.heroquest.models.effects.PoisonEffect;
import com.giggs.heroquest.models.effects.StunEffect;
import com.giggs.heroquest.models.items.Characteristics;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.models.items.Mercenary;
import com.giggs.heroquest.models.items.consumables.Potion;
import com.giggs.heroquest.models.items.consumables.ThrowableItem;
import com.giggs.heroquest.models.skills.ActiveSkill;
import com.giggs.heroquest.models.skills.Skill;
import com.giggs.heroquest.utils.ApplicationUtils;
import com.giggs.heroquest.utils.pathfinding.MathUtils;

import org.andengine.entity.Entity;
import org.andengine.entity.scene.Scene;
import org.andengine.extension.tmx.TMXLayer;
import org.andengine.extension.tmx.TMXLoader;
import org.andengine.extension.tmx.TMXTiledMap;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.util.color.Color;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends MyBaseGameActivity {

    private static final String TAG = "GameActivity";

    public Entity mGroundLayer;
    public TMXTiledMap mTmxTiledMap;
    private SelectionCircle mSelectionCircle;
    protected Quest mQuest;
    protected Hero mHero;
    protected Unit mActiveCharacter;
    protected ActionsDispatcher mActionDispatcher;
    private List<Unit> mHeroes;

    @Override
    protected void initGameActivity() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getSerializable(Game.class.getName()) != null) {
            mGame = (Game) extras.getSerializable(Game.class.getName());
        } else {
            // used fot testing only
            mGame = new Game();
            mGame.setHero(HeroFactory.buildBarbarian());
            mGame.setQuest(QuestFactory.buildTrial());
        }

        mQuest = mGame.getQuest();

        if (mQuest.getTiles() == null) {
            // copy hero object for reset dungeon after game-over
            mHero = (Hero) ApplicationUtils.deepCopy(mGame.getHero());
        }
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_game;
    }

    @Override
    public void onCreateScene(OnCreateSceneCallback pOnCreateSceneCallback) throws Exception {
        super.onCreateScene(pOnCreateSceneCallback);

        // load tiled map for current room
        final TMXLoader tmxLoader = new TMXLoader(getAssets(), mEngine.getTextureManager(),
                TextureOptions.BILINEAR_PREMULTIPLYALPHA, getVertexBufferObjectManager(), null);
        mTmxTiledMap = tmxLoader.loadFromAsset("tmx/" + mQuest.getTmxName() + ".tmx");

        mTmxTiledMap.getTMXLayers().get(2).setZIndex(10);
        for (TMXLayer tmxLayer : mTmxTiledMap.getTMXLayers()) {
            mScene.attachChild(tmxLayer);
        }

        // make the camera not exceed the bounds of the TMXEntity
        mCamera.setBounds(0, 0, mTmxTiledMap.getTileWidth() * mTmxTiledMap.getTileColumns(), mTmxTiledMap.getTileHeight() * mTmxTiledMap.getTileRows());
        mCamera.setBoundsEnabled(true);
        mCamera.setZoomFactor(1.5f);

        pOnCreateSceneCallback.onCreateSceneFinished(mScene);
    }

    @Override
    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback pOnPopulateSceneCallback) throws Exception {
        mQuest.initMap(mTmxTiledMap);

        mGroundLayer = new Entity();
        mGroundLayer.setZIndex(2);
        mScene.attachChild(mGroundLayer);

        mSelectionCircle = new SelectionCircle(getVertexBufferObjectManager());
        mScene.attachChild(mSelectionCircle);

        mHeroes = new ArrayList<>();
        if (mHero != null) {
            mHeroes.add(mHero);

            // add mercenaries
            for (Item item : mHero.getItems()) {
                if (item instanceof Mercenary) {
                    mHeroes.add(((Mercenary) item).getUnit());
                }
            }

            Set<Tile> tiles = MathUtils.getAdjacentNodes(mQuest.getTiles(), mQuest.getEntranceTile(), 2, true, mHero);
            Iterator<Tile> iterator = tiles.iterator();
            for (Unit unit : mHeroes) {
                mQuest.addGameElement(unit, iterator.next());
            }
        }

        // add elements to scene
        GameElement gameElement;
        for (Tile[] hTiles : mQuest.getTiles()) {
            for (Tile tile : hTiles) {
                gameElement = tile.getContent();
                if (gameElement != null) {
                    gameElement.setTilePosition(tile);
                    addElementToScene(gameElement);
                    if (mHero == null && gameElement.getRank() == Ranks.ME) {
                        mHero = (Hero) gameElement;
                        mHeroes.add(mHero);

                        // add mercenaries
                        for (Item item : mHero.getItems()) {
                            if (item instanceof Mercenary) {
                                mHeroes.add(((Mercenary) item).getUnit());
                            }
                        }
                    }
                }

                List<GameElement> copy = new ArrayList<>(tile.getSubContent());
                for (GameElement subContent : copy) {
                    subContent.setTilePosition(tile);
                    addElementToScene(subContent);
                }
            }
        }

        mHero.updateSprite();
        mGUIManager.setData(mHero);

        showBookIntro();

        Log.d(TAG, "populate scene is finished");
        pOnPopulateSceneCallback.onPopulateSceneFinished();

        mActionDispatcher = new ActionsDispatcher(this, mScene);

        Log.d(TAG, "sort children by z-index");
        mScene.sortChildren();

        startGame();
    }

    public void addElementToScene(GameElement gameElement) {
        Log.d(TAG, "add element to scene = " + gameElement.getIdentifier());
        gameElement.createSprite(getVertexBufferObjectManager());
        super.addElementToScene(gameElement.getSprite(), false);
    }

    public void removeElement(GameElement gameElement) {
        Log.d(TAG, "removing element");
        gameElement.destroy();
        mQuest.removeElement(gameElement);
        mGUIManager.updateQueue(mActiveCharacter, mQuest);
        super.removeElement(gameElement.getSprite(), false);
    }

    @Override
    protected void onPause() {
        if (mActiveCharacter != null && mActiveCharacter.isEnemy(mHero)) {
            mQuest.getQueue().remove(mActiveCharacter);
            mQuest.getQueue().add(0, mActiveCharacter);
        }
        mGame.setQuest(mQuest);
        super.onPause();
    }

    @Override
    protected int[] getMusicResource() {
        return new int[]{R.raw.adventure1, R.raw.adventure2, R.raw.adventure3, R.raw.adventure4};
    }

    @Override
    public void onMove(float x, float y) {
        mActionDispatcher.onMove(x, y);
    }

    @Override
    public void onCancel(float x, float y) {
        mActionDispatcher.onCancel(x, y);
    }

    @Override
    public void onPinchZoom(float zoomFactor) {
        mActionDispatcher.onPinchZoom(zoomFactor);
    }

    @Override
    public void onTap(float x, float y) {
        mActionDispatcher.onTap(x, y);
    }

    @Override
    public void onClick(View view) {
        if (mInputManager.ismIsEnabled() && mActiveCharacter.getRank() == Ranks.ME) {
            switch (view.getId()) {
                case R.id.bag:
                    mGUIManager.showBag();
                    break;
            }

            if (view.getTag(R.string.item) != null) {
                Log.d(TAG, "Show item info");
                final Item item = (Item) view.getTag(R.string.item);
                mGUIManager.showItemInfo(item, new ItemInfoInGame.OnItemActionSelected() {
                    @Override
                    public void onActionExecuted(ItemInfoInGame.ItemActionsInGame action) {
                        switch (action) {
                            case DRINK:
                                mGUIManager.hideBag();
                                drinkPotion((Potion) item);
                                updateActionTiles();
                                break;
                            case THROW:
                                mGUIManager.hideBag();
                                ThrowableItem throwable = (ThrowableItem) item;
                                mHero.use(throwable);
                                mActionDispatcher.setActivatedSkill(new ActiveSkill(item.getIdentifier(), 0, false, 0, throwable.getEffect()));
                                break;
                        }
                    }
                });
            } else if (view.getTag(R.string.show_skill) != null) {
                if (mActionDispatcher.getActivatedSkill() == view.getTag(R.string.show_skill)) {
                    Log.d(TAG, "Cancel skill");
                    mActionDispatcher.setActivatedSkill((ActiveSkill) view.getTag(R.string.show_skill));
                } else {
                    Log.d(TAG, "Show skill");
                    mGUIManager.showUseSkillInfo((Skill) view.getTag(R.string.show_skill));
                }
            } else if (view.getTag(R.string.use_skill) != null) {
                Log.d(TAG, "Use skill");
                mActionDispatcher.setActivatedSkill((ActiveSkill) view.getTag(R.string.use_skill));
            }
        }
    }

    public void drinkPotion(Potion potion) {
        Log.d(TAG, "Drink potion");
        mHero.use(potion);
        playSound("magic", false);
        mActionDispatcher.applyEffect(potion.getEffect(), mHero.getTilePosition(), false);
    }

    public Unit getActiveCharacter() {
        return mActiveCharacter;
    }

    public Quest getQuest() {
        return mQuest;
    }

    public Hero getHero() {
        return mHero;
    }

    public void startGame() {
        mCamera.setCenter(mHero.getSprite().getX(), mHero.getSprite().getY());

        updateVisibility();

        mGUIManager.hideLoadingScreen();
        mGUIManager.updateSkillButtons();

        nextTurn();
    }

    public void showBookIntro() {
        // show book introduction if needed
        if (mGame.getQuest().getIntroText(getResources()) > 0) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    findViewById(R.id.rootLayout).setVisibility(View.INVISIBLE);
                }
            });

            Bundle args = new Bundle();
            args.putInt(StoryFragment.ARGUMENT_STORY, mGame.getQuest().getIntroText(getResources()));
            ApplicationUtils.openDialogFragment(this, new StoryFragment(), args);
        }
    }

    protected void gameover() {
        Log.d(TAG, "hero is dead, game over !");
        Intent intent = new Intent(GameActivity.this, GameOverActivity.class);
        intent.putExtra(Game.class.getName(), mGame);
        startActivity(intent);
        finish();
    }

    public void nextTurn() {
        Log.d(TAG, "NEXT TURN");
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mHero.isDead()) {
                    gameover();
                    return;
                }

                // add new enemies / remove characters
                for (GameElement element : mQuest.getObjects()) {
                    if (element instanceof Ally && !mHeroes.contains(element)) {
                        mHeroes.add((Unit) element);
                    } else if (element instanceof Ally && ((Ally) element).isDead()) {
                        mHeroes.remove(element);
                    }

                    if (element instanceof Unit && element.getTilePosition() == null) {
                        removeElement(element);
                        break;
                    }
                }

                boolean isHeroic = false, isHidden = false;
                if (mActiveCharacter != null) {
                    List<Effect> copy = new ArrayList<>(mActiveCharacter.getBuffs());
                    for (Effect effect : copy) {
                        if (effect instanceof HeroicEffect) {
                            Log.d(TAG, "character is heroic");
                            isHeroic = true;
                            // animate heroic
                            if (effect.getSpriteName() != null) {
                                drawAnimatedSprite(mActiveCharacter.getTilePosition().getTileX(), mActiveCharacter.getTilePosition().getTileY(), effect.getSpriteName(), 50, GameConstants.ANIMATED_SPRITE_ALPHA, 1.0f, 0, true, 100, null);
                            }
                        } else if (effect instanceof CamouflageEffect) {
                            Log.d(TAG, "character is invisible");
                            isHidden = true;
                            // animate invisible
                            if (effect.getSpriteName() != null) {
                                drawAnimatedSprite(mActiveCharacter.getTilePosition().getTileX(), mActiveCharacter.getTilePosition().getTileY(), effect.getSpriteName(), 50, GameConstants.ANIMATED_SPRITE_ALPHA, 1.0f, 0, true, 100, null);
                            }
                            // test if character is still invisible
                            for (GameElement element : mQuest.getObjects()) {
                                if (element instanceof Monster) {
                                    // test if the monster sees invisible character depending on the distance
                                    double distance = MathUtils.calcManhattanDistance(element.getTilePosition(), mActiveCharacter.getTilePosition());
                                    Log.d(TAG, "distance to invisible character = " + distance);
                                    if (distance == 1 || ((Unit) element).testCharacteristic(Characteristics.SPIRIT, (int) (effect.getValue() + distance))) {
                                        // character is not hidden anymore
                                        isHidden = false;
                                        mActiveCharacter.getBuffs().remove(effect);
                                        // animate end of invisibility
                                        if (effect.getSpriteName() != null) {
                                            drawAnimatedSprite(element.getTilePosition().getTileX(), element.getTilePosition().getTileY(), effect.getSpriteName(), 50, GameConstants.ANIMATED_SPRITE_ALPHA, 1.0f, 0, true, 100, null);
                                        }
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (!isHeroic && !isHidden) {
                    // next character
                    mActiveCharacter = mQuest.getQueue().get(0);
                    Log.d(TAG, "updating queue to next character = " + mActiveCharacter.getRank().name() + ", " + mActiveCharacter.getHp() + "hp");

                    mQuest.getQueue().add(mQuest.getQueue().get(0));
                    mQuest.getQueue().remove(0);
                    mGUIManager.updateQueue(mActiveCharacter, mQuest);
                    mSelectionCircle.attachToGameElement(mActiveCharacter);
                    mGUIManager.updateQueue(mActiveCharacter, mQuest);
                }

                // handle current buffs
                boolean skipTurn = false;
                List<Effect> copy = new ArrayList<>(mActiveCharacter.getBuffs());
                for (Effect effect : copy) {
                    if (mActiveCharacter.isDead()) break;

                    if (effect instanceof PoisonEffect) {
                        Log.d(TAG, "got poison effect");
                        mActionDispatcher.applyEffect(effect, mActiveCharacter.getTilePosition(), false);
                    } else if (effect instanceof StunEffect) {
                        Log.d(TAG, "got stun effect");
                        if (mActiveCharacter.testCharacteristic(effect.getTarget(), effect.getValue())) {
                            Log.d(TAG, "stun test was a success");
                            mActiveCharacter.getBuffs().remove(effect);
                        } else {
                            Log.d(TAG, "skip turn");
                            skipTurn = true;
                            drawAnimatedText(mActiveCharacter.getSprite().getX() + GameConstants.PIXEL_BY_TILE / 3, mActiveCharacter.getSprite().getY() - 2 * GameConstants.PIXEL_BY_TILE / 3, getString(R.string.sleep_effect), new Color(0.0f, 1.0f, 0.0f), 0.4f, 50, -0.15f);
                        }
                    }
                }

                if (mActiveCharacter.isDead()) {
                    if (mActiveCharacter == mHero) {
                        gameover();
                    } else {
                        nextTurn();
                    }
                    return;
                }

                mActiveCharacter.initNewTurn();
                updateActionTiles();
                mGUIManager.updateHeroLayout();

                // center camera on active character
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        mInputManager.checkAutoScrolling(mActiveCharacter.getSprite().getX(), mActiveCharacter.getSprite().getY());
                        if (Math.abs(mCamera.getCenterX() - mActiveCharacter.getSprite().getX()) < GameConstants.CAMERA_WIDTH / 2
                                && Math.abs(mCamera.getCenterY() - mActiveCharacter.getSprite().getY()) < GameConstants.CAMERA_HEIGHT / 2) {
                            cancel();
                        }
                    }
                }, 0, 5);

                if (!skipTurn && mActiveCharacter instanceof Hero) {
                    rollMovementDice();
                }

                if (skipTurn) {
                    nextTurn();
                } else if (mActiveCharacter.isEnemy(mHero)) {
                    Log.d(TAG, "AI turn");
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    // find target
                                    Unit target = null;

                                    // prioritize adjacent tiles
                                    Set<Tile> adjacentTiles = MathUtils.getAdjacentNodes(mQuest.getTiles(), mActiveCharacter.getTilePosition(), 1, false, null);
                                    for (Tile tile : adjacentTiles) {
                                        if (tile.getContent() instanceof Unit && tile.getContent().isEnemy(mActiveCharacter)) {
                                            target = (Unit) tile.getContent();
                                            break;
                                        }
                                    }

                                    // then, search in whole room
                                    if (target == null) {
                                        Collections.shuffle(mQuest.getObjects());
                                        for (GameElement gameElement : mQuest.getObjects()) {
                                            if (gameElement instanceof Unit && gameElement.isEnemy(mActiveCharacter)) {
                                                target = (Unit) gameElement;
                                                break;
                                            }
                                        }
                                    }

                                    if (target == null) {
                                        nextTurn();
                                    } else {
                                        ActiveSkill availableSkill = mActiveCharacter.getAvailableSkill();
                                        if (availableSkill != null && Math.random() < 0.7) {
                                            mActionDispatcher.setActivatedSkill(availableSkill);
                                            if (!availableSkill.isPersonal()) {
                                                mActionDispatcher.useSkill(target.getTilePosition());
                                            }
                                        } else {
                                            mActionDispatcher.attack(target.getTilePosition());
                                        }
                                    }
                                }
                            });
                        }
                    }, 500);
                } else if (mActiveCharacter.getRank() == Ranks.ME || mActiveCharacter.getRank() == Ranks.ALLY) {
                    mInputManager.setEnabled(true);
                }
            }
        });
    }

    private void rollMovementDice() {
        playSound("dice", false);
        int index = 0;
        for (int die : mHero.getMovementDice()) {
            rollMovementDie(die, index++);
        }
    }

    private void rollMovementDie(final int result, int dieIndex) {
        final float x = mActiveCharacter.getSprite().getX() + (2 * dieIndex - 1) * 15;
        final float y = mActiveCharacter.getSprite().getY() - 45;
        drawAnimatedSprite(x, y, "dice.png", 15, 0.2f, 1.0f, 1, true, 1000, new OnActionExecuted() {
            @Override
            public void onActionDone(boolean success) {
                drawSprite(x, y, "dice.png", 25, 0.2f, result - 1);
            }
        });
    }

    private void updateActionTiles() {
        runOnUpdateThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "updating action tiles");
                mActionDispatcher.hideActionTiles();
                mActionDispatcher.showAllActions(mActiveCharacter.getTilePosition());
                Log.d(TAG, "updating action tiles is done");
                mActionDispatcher.setInputEnabled(!mActiveCharacter.isEnemy(mHero));
            }
        });
    }

    public void showGame() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                findViewById(R.id.rootLayout).setVisibility(View.VISIBLE);
            }
        });
    }

    public void updateVisibility() {
        List<Tile> visibleTiles = new ArrayList<>();
        for (Unit unit : mHeroes) {
            boolean isInCorridor = unit.isInCorridor(mQuest.getTiles());
            int roomXMin = -1, roomXMax = -1, roomYMin = -1, roomYMax = -1;
            for (Directions directions : Directions.values()) {
                int n = 0;
                Tile tile;
                do {
                    tile = mQuest.getTiles()[unit.getTilePosition().getY() - directions.getDy() * n][unit.getTilePosition().getX() + directions.getDx() * n];
                    if (tile.getContent() != null && tile.getContent() instanceof Unit && !mQuest.getQueue().contains(tile.getContent())) {
                        mQuest.getQueue().add((Unit) tile.getContent());
                    }
                    visibleTiles.add(tile);
                    n++;

                    if (!isInCorridor && (tile.getGround() == null || tile.getSubContent().size() > 0 && tile.getSubContent().get(0) instanceof Door)) {
                        switch (directions) {
                            case WEST:
                                if (roomXMin == -1) {
                                    roomXMin = tile.getX();
                                }
                                break;
                            case EAST:
                                if (roomXMax == -1) {
                                    roomXMax = tile.getX();
                                }
                                break;
                            case NORTH:
                                if (roomYMin == -1) {
                                    roomYMin = tile.getY();
                                }
                                break;
                            case SOUTH:
                                if (roomYMax == -1) {
                                    roomYMax = tile.getY();
                                }
                                break;
                        }
                    }
                }

                while (tile.getGround() != null && (tile.getSubContent().size() == 0 || !(tile.getSubContent().get(0) instanceof Door) || ((Door) tile.getSubContent().get(0)).isOpen()));
            }

            if (!isInCorridor) {
                Log.d(TAG, "character is in room " + roomXMin + "," + roomYMin + " to " + roomXMax + "," + roomYMax);
                Tile tile;
                for (int i = roomYMin; i < roomYMax; i++) {
                    for (int j = roomXMin; j < roomXMax; j++) {
                        tile = mQuest.getTiles()[i][j];
                        if (tile.getContent() != null && tile.getContent() instanceof Unit && !mQuest.getQueue().contains(tile.getContent())) {
                            mQuest.getQueue().add((Unit) tile.getContent());
                        }
                        visibleTiles.add(tile);
                    }
                }
            }
        }

        Log.d(TAG, visibleTiles.size() + " visible tiles");

        for (Tile[] hTiles : mQuest.getTiles()) {
            for (Tile tile : hTiles) {
                tile.setVisible(tile.getGround() == null || visibleTiles.contains(tile));
            }
        }
    }

}
