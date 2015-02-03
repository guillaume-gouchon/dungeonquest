package com.giggs.heroquest.game.gui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.models.Reward;
import com.giggs.heroquest.models.items.consumables.Potion;
import com.giggs.heroquest.models.items.equipments.weapons.Weapon;

/**
 * Created by guillaume on 1/14/15.
 */
public class RewardDialog extends Dialog {

    public RewardDialog(Context context, Reward reward) {
        super(context, R.style.Dialog);
        setContentView(R.layout.in_game_item_info);
        setCancelable(false);

        Resources resources = context.getResources();

        TextView nameTV = (TextView) findViewById(R.id.name);
        TextView descriptionTV = (TextView) findViewById(R.id.description);
        ImageView bg = (ImageView) findViewById(R.id.bg);

        if (reward == null) {
            reward = new Reward("tr_nothing", null, null, 0);
        }

        if (reward.getItem() != null) {
            nameTV.setText(context.getString(reward.getItem().getName(resources)));
            ((ImageView) findViewById(R.id.image)).setImageResource(reward.getItem().getImage(resources));
            int description = reward.getItem().getDescription(resources);
            if (description > 0) {
                descriptionTV.setText(description);
            } else {
                descriptionTV.setVisibility(View.GONE);
            }

            if (reward.getItem() instanceof Weapon) {
                bg.setColorFilter(Color.argb(100, 255, 0, 0));
            } else if (reward.getItem() instanceof Potion) {
                bg.setColorFilter(Color.argb(100, 0, 0, 255));
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
            
            bg.setColorFilter(Color.argb(100, 100, 100, 0));
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
