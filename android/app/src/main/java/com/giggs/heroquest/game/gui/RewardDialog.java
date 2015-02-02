package com.giggs.heroquest.game.gui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.models.Reward;

/**
 * Created by guillaume on 1/14/15.
 */
public class RewardDialog extends Dialog {

    public RewardDialog(Context context, Reward reward) {
        super(context, R.style.Dialog);
        setContentView(R.layout.in_game_item_info);
        setCancelable(false);
        findViewById(R.id.rootLayout).getBackground().setAlpha(70);

        Resources resources = context.getResources();

        TextView nameTV = (TextView) findViewById(R.id.name);
        TextView descriptionTV = (TextView) findViewById(R.id.description);

        if (reward.getItem() != null) {
            nameTV.setText(context.getString(reward.getItem().getName(resources)));
            ((ImageView) findViewById(R.id.image)).setImageResource(reward.getItem().getImage(resources));
            int description = reward.getItem().getDescription(resources);
            if (description > 0) {
                descriptionTV.setText(description);
            } else {
                descriptionTV.setVisibility(View.GONE);
            }
        } else {
            nameTV.setText(context.getString(reward.getName(resources)));
            ((ImageView) findViewById(R.id.image)).setImageResource(reward.getImage(resources));
            int description = reward.getDescription(resources);
            if (description > 0) {
                descriptionTV.setText(description);
            } else {
                descriptionTV.setVisibility(View.GONE);
            }
        }

        // actions
        TextView mainActionButton = (TextView) findViewById(R.id.main_action_btn);
        mainActionButton.setText(R.string.close);
        mainActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        mainActionButton.setVisibility(View.VISIBLE);
    }

}
