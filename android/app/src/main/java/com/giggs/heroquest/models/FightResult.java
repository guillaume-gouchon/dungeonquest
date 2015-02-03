package com.giggs.heroquest.models;

import org.andengine.util.color.Color;

/**
 * Created by guillaume on 10/14/14.
 */
public class FightResult {

    private final States state;
    private final int attackScore;
    private final int defenseScore;

    public FightResult(States state, int attackScore, int defenseScore) {
        this.state = state;
        this.attackScore = attackScore;
        this.defenseScore = defenseScore;
    }

    public States getState() {
        return state;
    }

    public int getDamage() {
        return Math.max(0, attackScore - defenseScore);
    }

    public int getDefenseScore() {
        return defenseScore;
    }

    public int getAttackScore() {
        return attackScore;
    }

    public enum States {
        MISS(Color.YELLOW), DAMAGE(Color.RED), BLOCK(new Color(0f, 0.8f, 0f)), DODGE(new Color(0f, 0.8f, 0f)), CRITICAL(Color.RED);

        private Color color;

        private States(Color color) {
            this.color = color;
        }

        public Color getColor() {
            return color;
        }
    }

}
