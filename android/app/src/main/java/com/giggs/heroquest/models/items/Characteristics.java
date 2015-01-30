package com.giggs.heroquest.models.items;

import com.giggs.heroquest.R;

/**
 * s
 * Created by guillaume on 19/10/14.
 */
public enum Characteristics {
    HP(R.string.hp, R.drawable.ic_health), SPIRIT(R.string.spirit, R.drawable.ic_spirit), ATTACK(R.string.attack, R.drawable.ic_axe),
    DEFENSE(R.string.defense, R.drawable.ic_shield),
    DAMAGE(R.string.damage, 0), BLOCK(R.string.block, 0),
    DODGE(R.string.dodge, 0), MOVEMENT(R.string.movement, 0),
    INITIATIVE(R.string.initiative, 0), CRITICAL(R.string.critical, 0),
    PROTECTION(R.string.protection, 0);

    private final int name;
    private final int image;

    private Characteristics(int name, int image) {
        this.name = name;
        this.image = image;
    }

    public int getName() {
        return name;
    }

    public int getImage() {
        return image;
    }

}
