package com.giggs.heroquest.game.gui;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.models.Reward;

/**
 * Created by guillaume on 1/14/15.
 */
public class RewardDialog extends Dialog {

    public RewardDialog(Context context, Reward reward) {
        super(context, R.style.Dialog);
        setContentView(R.layout.in_game_reward);
        setCancelable(false);

        findViewById(R.id.rootLayout).getBackground().setAlpha(70);

        TextView itemTV = (TextView) findViewById(R.id.item);

        if (reward == null) {
//            // found nothing
//            itemTV.setText(R.string.found_nothing);
//            itemTV.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, R.drawable.found_nothing);
        } else {
            if (reward.getItem() != null) {
                // add item
                String itemName = context.getString(reward.getItem().getName(context.getResources()));
                boolean isAn = itemName.startsWith("a") || itemName.startsWith("e") || itemName.startsWith("i") || itemName.startsWith("o") || itemName.startsWith("u");
                itemTV.setText(context.getString(isAn ? R.string.found_item_an : R.string.found_item_a, itemName));
                itemTV.setCompoundDrawablesWithIntrinsicBounds(0, reward.getItem().getImage(context.getResources()), 0, 0);
                itemTV.setVisibility(View.VISIBLE);
            } else {
                itemTV.setVisibility(View.GONE);
            }
        }

        findViewById(R.id.dismiss_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }

}
