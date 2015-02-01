package com.giggs.heroquest.game.gui.items;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.game.GameConstants;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.models.items.Mercenary;

/**
 * Created by guillaume on 1/14/15.
 */
public class ItemInfoInShop extends ItemInfo {

    public ItemInfoInShop(Context context, Item item, Hero hero, boolean isSelling, final OnItemActionSelected onItemActionSelected) {
        super(context, item, hero);

        // actions
        TextView mainActionButton = (TextView) findViewById(R.id.main_action_btn);
        if (item instanceof Mercenary) {
            mainActionButton.setText(isSelling ? context.getString(R.string.cancel_hire_for, item.getSellPrice()) : context.getString(R.string.hire_for, item.getPrice()));
        } else {
            mainActionButton.setText(isSelling ? context.getString(R.string.sell_item_for, item.getSellPrice()) : context.getString(R.string.buy_item_for, item.getPrice()));
        }
        mainActionButton.setEnabled(isSelling || hero.getGold() >= item.getPrice() && hero.getItems().size() < GameConstants.NB_ITEMS_MAX_IN_BAG);
        mainActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onItemActionSelected.onActionExecuted(ItemActionsInShop.DO_TRANSACTION);
            }
        });
        mainActionButton.setVisibility(View.VISIBLE);
    }

    public enum ItemActionsInShop {
        DO_TRANSACTION
    }

    public static interface OnItemActionSelected {

        public void onActionExecuted(ItemActionsInShop action);

    }

}
