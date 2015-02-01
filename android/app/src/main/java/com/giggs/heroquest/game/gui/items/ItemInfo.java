package com.giggs.heroquest.game.gui.items;

import android.content.Context;
import android.view.View;

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

    public ItemInfo(Context context, Item item, Hero hero) {
        super(context, item);

        if (item instanceof Equipment) {
            final Equipment equipment = (Equipment) item;

            // add item effects
            for (Effect buff : equipment.getEffects()) {
//             addStatToItemLayout(statsLayout.getChildAt(indexStats++), (buff.getValue() > 0 ? "+" : "") + buff.getValue(), buff.getTarget().getImage(), buff.getTarget().getName(), buff.getValue() > 0 ? R.color.green : R.color.red);
            }
        }
    }

    private void addStatToItemLayout(View view, String text, int image, int hint, int color) {
        HintTextView statView = (HintTextView) view;
        statView.setText(text);
        statView.setTextHint(hint);
        statView.setCompoundDrawablesWithIntrinsicBounds(image, 0, 0, 0);
        int colorResource = mResources.getColor(color);
        statView.setTextColor(colorResource);
        statView.setVisibility(View.VISIBLE);
    }

}
