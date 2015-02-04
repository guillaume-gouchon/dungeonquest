package com.giggs.heroquest.activities.adapters;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.giggs.heroquest.R;
import com.giggs.heroquest.models.Quest;

import java.util.List;

public class QuestsAdapter extends ArrayAdapter<Quest> {

    private final Resources mResources;
    private final LayoutInflater mInflater;

    public QuestsAdapter(final Context context, int layoutResource, List<Quest> dataList) {
        super(context, layoutResource, dataList);
        mResources = context.getResources();
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View layout = mInflater.inflate(R.layout.quest_item, parent, false);

        Quest quest = getItem(position);

        ((TextView) layout.findViewById(R.id.title)).setText(quest.getName(mResources));
//        if (quest.getIntroText(mResources) > 0) {
//            ((TextView) layout.findViewById(R.id.subtitle)).setText(quest.getIntroText(mResources));
//        } else {
            layout.findViewById(R.id.subtitle).setVisibility(View.GONE);
//        }
        layout.findViewById(R.id.lock).setVisibility(quest.isAvailable() ? View.GONE : View.VISIBLE);
        ((ImageView) layout.findViewById(R.id.done)).setImageResource(quest.isDone() ? R.drawable.ic_star : R.drawable.ic_star_black);

        return layout;
    }

}
