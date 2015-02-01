package com.giggs.heroquest.game.gui;

import android.app.Dialog;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.models.StorableResource;
import com.giggs.heroquest.models.items.Mercenary;
import com.giggs.heroquest.models.items.consumables.Potion;
import com.giggs.heroquest.models.items.equipments.weapons.Weapon;

/**
 * Created by guillaume on 1/14/15.
 */
public class ElementDetails extends Dialog {

    protected final TextView mNameTV;
    protected final Resources mResources;

    public ElementDetails(Context context, StorableResource element) {
        super(context, R.style.DialogNoAnimation);
        setContentView(R.layout.in_game_item_info);
        setCancelable(true);

        if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            WindowManager.LayoutParams params = getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            getWindow().setAttributes(params);
        }

        mResources = context.getResources();

        // name
        mNameTV = (TextView) findViewById(R.id.name);
        mNameTV.setText(context.getString(element.getName(mResources)));

        // image
        ((ImageView) findViewById(R.id.image)).setImageResource(element.getImage(mResources));

        // description
        TextView descriptionTV = (TextView) findViewById(R.id.description);
        int description = element.getDescription(mResources);
        if (description > 0) {
            descriptionTV.setText(description);
        } else {
            descriptionTV.setVisibility(View.GONE);
        }

        ImageView bg = (ImageView) findViewById(R.id.bg);
        if (element instanceof Weapon) {
            bg.setColorFilter(Color.argb(100, 255, 0, 0));
        } else if (element instanceof Potion) {
            bg.setColorFilter(Color.argb(100, 0, 0, 255));
        } else if (element instanceof Mercenary) {
            bg.setColorFilter(Color.argb(100, 0, 200, 0));
        }
    }

}
