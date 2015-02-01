package com.giggs.heroquest.game.gui.items;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.models.items.consumables.Potion;
import com.giggs.heroquest.models.items.equipments.Equipment;

/**
 * Created by guillaume on 1/14/15.
 */
public class ItemInfoInGame extends ItemInfo {

    public ItemInfoInGame(Context context, Item item, Hero hero, final OnItemActionSelected onItemActionSelected) {
        super(context, item, hero);

        // actions
        TextView mainActionButton = (TextView) findViewById(R.id.main_action_btn);
        TextView secondaryActionButton = (TextView) findViewById(R.id.secondary_action_btn);

        if (item instanceof Equipment) {
            final Equipment equipment = (Equipment) item;

        } else if (item instanceof Potion) {
            // drink potion
            mainActionButton.setText(R.string.drink);
            mainActionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                    onItemActionSelected.onActionExecuted(ItemActionsInGame.DRINK);
                }
            });
            mainActionButton.setVisibility(View.VISIBLE);
        }


    }

    public enum ItemActionsInGame {
        USE, DRINK
    }

    public static interface OnItemActionSelected {

        public void onActionExecuted(ItemActionsInGame action);

    }

}
