package com.giggs.heroquest.activities.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.models.items.Mercenary;
import com.giggs.heroquest.models.items.consumables.*;
import com.giggs.heroquest.models.items.consumables.ThrowableItem;
import com.giggs.heroquest.models.items.equipments.weapons.Weapon;

import java.util.List;

public class ItemsAdapter extends ArrayAdapter<Item> {

    private final Resources mResources;
    private final LayoutInflater mInflater;

    public ItemsAdapter(final Context context, int layoutResource, List<Item> dataList) {
        super(context, layoutResource, dataList);
        mResources = context.getResources();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = mInflater.inflate(R.layout.item, parent, false);

        Item item = getItem(position);

        ((TextView) layout.findViewById(R.id.name)).setText(item.getName(mResources));
        ((ImageView) layout.findViewById(R.id.image)).setImageResource(item.getImage(mResources));

        ImageView bg = (ImageView) layout.findViewById(R.id.bg);
        if (item instanceof Weapon) {
            bg.setColorFilter(Color.argb(100, 255, 0, 0));
        } else if (item instanceof Potion || item instanceof ThrowableItem) {
            bg.setColorFilter(Color.argb(100, 0, 0, 255));
        } else if (item instanceof Mercenary) {
            bg.setColorFilter(Color.argb(100, 0, 200, 0));
        }

        return layout;
    }

}
