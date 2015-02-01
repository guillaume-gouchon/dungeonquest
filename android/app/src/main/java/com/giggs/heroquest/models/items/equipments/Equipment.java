package com.giggs.heroquest.models.items.equipments;

import android.content.Context;

import com.giggs.heroquest.models.Levelable;
import com.giggs.heroquest.models.effects.Effect;
import com.giggs.heroquest.models.items.Item;
import com.giggs.heroquest.models.items.requirements.Requirement;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by guillaume ON 10/6/14.
 */
public abstract class Equipment extends Item implements Levelable {

    private static final long serialVersionUID = 662104930156421984L;

    private final List<Effect> effects = new ArrayList<Effect>();
    private final List<Requirement> requirements = new ArrayList<Requirement>();
    protected final int level;

    public Equipment(String identifier, int level, int price) {
        super(identifier, true, price);
        this.level = level;
    }

    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    public List<Effect> getEffects() {
        return effects;
    }

    public void addRequirement(Requirement requirement) {
        requirements.add(requirement);
    }

    public List<Requirement> getRequirements() {
        return requirements;
    }

    @Override
    public int getLevel() {
        return level;
    }

}
