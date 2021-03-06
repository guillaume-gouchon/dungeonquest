package com.giggs.heroquest.activities.fragments;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.activities.AdventureActivity;
import com.giggs.heroquest.activities.games.GameActivity;
import com.giggs.heroquest.models.Game;

public class StoryFragment extends DialogFragment implements View.OnClickListener {

    public static final String ARGUMENT_STORY = "story";
    public static final String ARGUMENT_IS_OUTRO = "is_outro";

    private boolean mIsOutro = false;

    /**
     * UI
     */
    private TextView mStoryTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Black_NoTitleBar_Fullscreen); // remove title from dialog fragment
    }

    @Override
    public void onStart() {
        super.onStart();

        if (getDialog() == null)
            return;

        // set the animations to use on showing and hiding the dialog
        getDialog().getWindow().setWindowAnimations(R.style.DialogAnimation);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View layout = inflater.inflate(R.layout.dialog_fragment_story, container, false);

        // retrieve story content
        Bundle args = getArguments();
        int storyResource = args.getInt(ARGUMENT_STORY);
        String story = getString(storyResource);

        mStoryTV = (TextView) layout.findViewById(R.id.story);
        mStoryTV.setText(story);

        startAnimation();

        mIsOutro = args.getBoolean(ARGUMENT_IS_OUTRO);

        layout.findViewById(R.id.start_button).setOnClickListener(this);

        return layout;
    }

    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if (mIsOutro) {
            Intent intent = new Intent(getActivity(), AdventureActivity.class);
            intent.putExtra(Game.class.getName(), ((GameActivity) getActivity()).getGame());
            getActivity().startActivity(intent);
            getActivity().finish();
        } else if (getActivity() != null) {
            ((GameActivity) getActivity()).showGame();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.start_button:
                dismiss();
                break;
        }
    }

    protected void startAnimation() {
        mStoryTV.startAnimation(AnimationUtils.loadAnimation(getActivity().getApplicationContext(), R.anim.story_text_animation));
    }

}
