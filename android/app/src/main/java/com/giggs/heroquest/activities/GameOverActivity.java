package com.giggs.heroquest.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AnimationUtils;

import com.giggs.heroquest.MyActivity;
import com.giggs.heroquest.R;
import com.giggs.heroquest.data.QuestFactory;
import com.giggs.heroquest.models.Game;

public class GameOverActivity extends MyActivity implements View.OnClickListener {

    private Game mGame;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_over);

        setupUI();

        mGame = (Game) getIntent().getSerializableExtra(Game.class.getName());
        mGame.setQuest(QuestFactory.getAll().get(mGame.getQuest().getId()));
    }

    private void setupUI() {
        View retryButton = findViewById(R.id.retry_btn);
        retryButton.setOnClickListener(this);
        retryButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in));

        View exitButton = findViewById(R.id.exit_btn);
        exitButton.setOnClickListener(this);
        exitButton.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.zoom_in));
    }

    @Override
    protected int[] getMusicResource() {
        return new int[]{R.raw.game_over};
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.retry_btn:
                intent = new Intent(this, mGame.getQuest().getActivityClass());
                intent.putExtra(Game.class.getName(), mGame);
                startActivity(intent);
                finish();
                break;
            case R.id.exit_btn:
                intent = new Intent(this, AdventureActivity.class);
                intent.putExtra(Game.class.getName(), mGame);
                startActivity(intent);
                finish();
                break;
        }
    }

}
