//TODO
//package com.giggs.heroquest.activities.games;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.TextView;
//
//import com.giggs.heroquest.R;
//import com.giggs.heroquest.data.BookFactory;
//import com.giggs.heroquest.data.characters.HeroFactory;
//import com.giggs.heroquest.data.characters.MonsterFactory;
//import com.giggs.heroquest.data.dungeons.DecorationFactory;
//import com.giggs.heroquest.data.items.RingFactory;
//import com.giggs.heroquest.game.base.GameElement;
//import com.giggs.heroquest.game.base.interfaces.OnActionExecuted;
//import com.giggs.heroquest.models.Chapter;
//import com.giggs.heroquest.models.Game;
//import com.giggs.heroquest.models.Reward;
//import com.giggs.heroquest.models.characters.Hero;
//import com.giggs.heroquest.models.characters.Monster;
//import com.giggs.heroquest.models.dungeons.Tile;
//import com.giggs.heroquest.models.dungeons.decorations.Decoration;
//import com.giggs.heroquest.models.dungeons.decorations.Searchable;
//import com.giggs.heroquest.models.dungeons.decorations.Stairs;
//import com.giggs.heroquest.models.effects.PermanentEffect;
//import com.giggs.heroquest.models.items.Characteristics;
//import com.giggs.heroquest.utils.ApplicationUtils;
//
//import org.andengine.entity.scene.Scene;
//
//import java.util.ArrayList;
//import java.util.List;
//
//public class TutorialQuest extends GameActivity {
//
//    private static final String TAG = "TutorialActivity";
//
//    private TextView mTutorialHintTV;
//    private int mTutorialStep = -1;
//    private String[] mTutorialHints;
//    private boolean mIsVisitedRoom;
//
//    @Override
//    protected void initGameActivity() {
//        Bundle extras = getIntent().getExtras();
//        if (extras != null && extras.getSerializable(Game.class.getName()) != null) {
//            mGame = (Game) extras.getSerializable(Game.class.getName());
//        } else {
//            // used fot testing only
//            mGame = new Game();
//            mGame.setHero(HeroFactory.buildDwarfWarrior());
//            mGame.setQuest(BookFactory.buildTutorial());
//        }
//
//        mTutorialHintTV = (TextView) findViewById(R.id.tutorial_hint);
//        mTutorialHints = getResources().getStringArray(R.array.tutorial_hints);
//
//        // new dungeon
//        Chapter chapter = mGame.getBook().getActiveChapter();
//
//        // create dungeon
//        chapter.createDungeon();
//        mGame.setDungeon(chapter.getDungeon());
//
//        // copy hero object for reset dungeon after game-over
//        mHero = (Hero) ApplicationUtils.deepCopy(mGame.getHero());
//
//        mDungeon = mGame.getDungeon();
//
//        mHero.reset();
//
//        mRoom = mDungeon.getCurrentRoom();
//        Log.d(TAG, "current room " + mRoom);
//    }
//
//    @Override
//    protected int getLayoutID() {
//        return R.layout.activity_tutorial;
//    }
//
//    @Override
//    public void showChapterIntro() {
//    }
//
//    @Override
//    public void showBookIntro() {
//    }
//
//    @Override
//    public void onPopulateScene(Scene pScene, OnPopulateSceneCallback
//            pOnPopulateSceneCallback) throws Exception {
//        mIsVisitedRoom = mRoom.isVisited();
//        super.onPopulateScene(pScene, pOnPopulateSceneCallback);
//    }
//
//    @Override
//    public void startGame() {
//        super.startGame();
//
//        if (mTutorialStep < 7) {
//            List<GameElement> copy = new ArrayList<>(mRoom.getObjects());
//            for (GameElement gameElement : copy) {
//                if (gameElement instanceof Monster || gameElement instanceof Searchable || gameElement instanceof Stairs) {
//                    removeElement(gameElement);
//                }
//            }
//        }
//
//        if (!mIsVisitedRoom) {
//            nextTutorialStep();
//
//            switch (mTutorialStep) {
//                case 1:
//                    // fight mode, attack
//                    Monster goblin = MonsterFactory.buildGoblin();
//                    goblin.getBuffs().add(new PermanentEffect(Characteristics.INITIATIVE, -100, null, 0));
//                    goblin.setReward(new Reward(null, 10, 14));
//                    mRoom.addGameElement(goblin, mRoom.getRandomFreeTile());
//                    addElementToScene(goblin);
//                    break;
//                case 4:
//                    // search treasure, equip, items
//                    Decoration treasureChest = DecorationFactory.buildSmallChest(new Reward(RingFactory.getRandomRing(0)));
//                    mRoom.addGameElement(treasureChest, mRoom.getRandomFreeTile());
//                    addElementToScene(treasureChest);
//                    break;
//                case 7:
//                    // map, skills, stairs
//                    Stairs stairs = new Stairs(true);
//                    mRoom.addGameElement(stairs, mRoom.getRandomFreeTile());
//                    addElementToScene(stairs);
//                    break;
//            }
//        }
//    }
//
//    @Override
//    public void switchRoom(Tile doorTile) {
//        if (mTutorialStep == 1 || mTutorialStep == 2 || mTutorialStep == 4 || mTutorialStep == 5) {
//            tutorialFailed();
//            return;
//        }
//        super.switchRoom(doorTile);
//    }
//
//    @Override
//    public void nextTurn() {
//        super.nextTurn();
//        if (mTutorialStep == 1) {
//            if (mActiveCharacter instanceof Monster) {
//                nextTutorialStep();
//            }
//            if (mRoom.getQueue().size() == 1) {
//                tutorialFailed();
//            }
//        }
//
//        if (mTutorialStep == 2 && mRoom.getQueue().size() == 1) {
//            nextTutorialStep();
//        }
//
//        if (mTutorialStep == 4 && mHero.getItems().size() > 2) {
//            nextTutorialStep();
//        }
//    }
//
//    private void tutorialFailed() {
//        Log.d(TAG, "tutorial failed");
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                mGUIManager.showIntrospection(R.string.tutorial_failed, R.string.ok, new OnActionExecuted() {
//                    @Override
//                    public void onActionDone(boolean success) {
//                        gameover();
//                    }
//                });
//            }
//        });
//    }
//
//    @Override
//    public void onClick(View view) {
//        if (view.getTag(R.string.skill) != null) return;
//
//        super.onClick(view);
//        if (mTutorialStep == 5 && view.getTag(R.string.item) != null) {
//            nextTutorialStep();
//        }
//    }
//
//    private void nextTutorialStep() {
//        Log.d(TAG, "next tutorial step");
//        if (mTutorialStep < mTutorialHints.length - 1) {
//            mTutorialStep++;
//
//            runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    mTutorialHintTV.setText(mTutorialHints[mTutorialStep]);
//                }
//            });
//        }
//    }
//
//}