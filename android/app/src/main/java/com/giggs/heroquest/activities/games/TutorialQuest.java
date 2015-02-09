package com.giggs.heroquest.activities.games;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.data.QuestFactory;
import com.giggs.heroquest.data.characters.HeroFactory;
import com.giggs.heroquest.models.Game;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.utils.ApplicationUtils;

public class TutorialQuest extends GameActivity {

    private static final String TAG = "TutorialActivity";

    private TextView mTutorialHintTV;
    private int mTutorialStep = -1;
    private String[] mTutorialHints;

    @Override
    protected void initGameActivity() {
        Bundle extras = getIntent().getExtras();
        if (extras != null && extras.getSerializable(Game.class.getName()) != null) {
            mGame = (Game) extras.getSerializable(Game.class.getName());
        } else {
            // used fot testing only
            mGame = new Game();
            mGame.setHero(HeroFactory.buildBarbarian());
            mGame.setQuest(QuestFactory.buildTutorial());
        }

        mTutorialHintTV = (TextView) findViewById(R.id.tutorial_hint);
        mTutorialHints = getResources().getStringArray(R.array.tutorial_hints);

        mQuest = QuestFactory.buildTutorial();

        // copy hero object for reset dungeon after game-over
        mHero = (Hero) ApplicationUtils.deepCopy(mGame.getHero());
    }

    @Override
    protected int getLayoutID() {
        return R.layout.activity_tutorial;
    }

    @Override
    public void showBookIntro() {
    }

    @Override
    public void startGame() {
        super.startGame();
        nextTutorialStep();
    }

    @Override
    public void nextTurn() {
        super.nextTurn();
        if (mTutorialStep == 0 && mQuest.getQueue().size() > 1) {
            nextTutorialStep();
        } else if (mTutorialStep == 1 && mQuest.getQueue().size() == 1) {
            nextTutorialStep();
        } else if (mTutorialStep == 2 && mHero.getTilePosition().getY() < 7) {
            nextTutorialStep();
        } else if (mTutorialStep == 3 && mHero.getTilePosition().getX() > 10) {
            nextTutorialStep();
        }
    }

    private void nextTutorialStep() {
        Log.d(TAG, "next tutorial step");
        if (mTutorialStep < mTutorialHints.length - 1) {
            mTutorialStep++;

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    mTutorialHintTV.setText(mTutorialHints[mTutorialStep]);
                }
            });
        }
    }

}
