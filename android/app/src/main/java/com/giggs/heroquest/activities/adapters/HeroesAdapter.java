package com.giggs.heroquest.activities.adapters;

import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.models.characters.Hero;
import com.giggs.heroquest.views.CustomCarousel;

import java.util.List;

public class HeroesAdapter extends CustomCarousel.Adapter<Hero> {

    private static final String TAG = "HeroesAdapter";

    private Resources mResources;

    public HeroesAdapter(Activity activity, int layoutResource, List<Hero> dataList, View.OnClickListener itemClickedListener) {
        super(activity, layoutResource, dataList, itemClickedListener);
        mResources = activity.getResources();
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        Log.d(TAG, "instantiate item " + position);

        View layout = (View) super.instantiateItem(container, position);
        layout.setTag(R.string.id, position);

        Hero hero = mDataList.get(position);

        ((ImageView) layout.findViewById(R.id.image)).setImageResource(hero.getImage(mResources));
        ((TextView) layout.findViewById(R.id.name)).setText(hero.getName(mResources));
        ((TextView) layout.findViewById(R.id.description)).setText(hero.getDescription(mResources));
        ((TextView) layout.findViewById(R.id.hp)).setText("" + hero.getHp());
        ((TextView) layout.findViewById(R.id.spirit)).setText("" + hero.getSpirit());
        ((TextView) layout.findViewById(R.id.attack)).setText("" + hero.getAttack());
        ((TextView) layout.findViewById(R.id.defense)).setText("" + hero.getDefense());

        return layout;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

}
