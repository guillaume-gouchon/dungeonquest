package com.giggs.heroquest.game.gui;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.giggs.heroquest.R;
import com.giggs.heroquest.utils.MusicManager;

/**
 * Created by guillaume on 1/14/15.
 */
public class GameMenu extends Dialog implements View.OnClickListener {

    public GameMenu(Context context, View.OnClickListener leaveQuestCallback, View.OnClickListener exitCallback) {
        super(context, R.style.FullScreenDialog);
        setContentView(R.layout.dialog_game_menu);
        setCancelable(true);
        setCanceledOnTouchOutside(true);

        final Animation menuButtonAnimation = AnimationUtils.loadAnimation(context, R.anim.bottom_in);

        // resume game button
        View resumeGameBtn = findViewById(R.id.resume_game_btn);
        resumeGameBtn.setAnimation(menuButtonAnimation);
        resumeGameBtn.setOnClickListener(this);

        // exit button
        View exitButton = findViewById(R.id.exit_btn);
        exitButton.setAnimation(menuButtonAnimation);
        exitButton.setOnClickListener(exitCallback);

        setOnShowListener(new OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                menuButtonAnimation.start();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.resume_game_btn:
                dismiss();
                break;
        }
    }

}
