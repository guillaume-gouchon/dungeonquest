package com.giggs.heroquest.game.base.interfaces;

import com.giggs.heroquest.models.Reward;
import com.giggs.heroquest.models.characters.Pnj;

/**
 * Created by guillaume on 10/9/14.
 */
public interface OnDiscussionReplySelected {

    public void onReplySelected(Pnj pnj, int next, Reward instantReward);

}
