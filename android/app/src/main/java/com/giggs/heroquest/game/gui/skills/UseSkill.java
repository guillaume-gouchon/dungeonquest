package com.giggs.heroquest.game.gui.skills;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.game.gui.ElementDetails;
import com.giggs.heroquest.models.skills.ActiveSkill;
import com.giggs.heroquest.models.skills.Skill;

/**
 * Created by guillaume on 1/14/15.
 */
public class UseSkill extends ElementDetails {

    public UseSkill(Context context, Skill skill, final View.OnClickListener onMainButtonClicked) {
        super(context, skill);

        // actions
        if (skill instanceof ActiveSkill && !((ActiveSkill) skill).isUsed()) {
            TextView actionButton = (TextView) findViewById(R.id.main_action_btn);
            actionButton.setText(R.string.use);
            actionButton.setTag(R.string.use_skill, skill);
            actionButton.setVisibility(View.VISIBLE);
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    onMainButtonClicked.onClick(v);
                }
            });
        }
    }

}
