package com.giggs.heroquest.game.gui.items;

import android.content.Context;
import android.view.View;

import com.giggs.heroquest.R;
import com.giggs.heroquest.game.gui.ElementDetails;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.models.effects.Effect;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.models.items.equipments.Equipment;
import com.giggs.heroquest.views.HintTextView;

/**
 * Created by guillaume on 1/14/15.
 */
public class ItemInfo extends ElementDetails {

    public ItemInfo(Context context, Item item) {
        super(context, item);

        if (item instanceof Equipment) {
            final Equipment equipment = (Equipment) item;

            // add item effects
            HintTextView statView = (HintTextView) findViewById(R.id.stats);
            for (Effect effect : equipment.getEffects()) {
                statView.setText((effect.getValue() == 1 ? "+" : "") + effect.getValue() + (effect.getValue() > 1 ? " dice" : " die"));
                statView.setCompoundDrawablesWithIntrinsicBounds(effect.getTarget().getImage(), 0, 0, 0);
                statView.setTextHint(effect.getTarget().getName());
                statView.setVisibility(View.VISIBLE);
            }

        }
    }

}
