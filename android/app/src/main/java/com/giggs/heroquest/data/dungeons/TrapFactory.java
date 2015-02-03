package com.giggs.heroquest.data.dungeons;

import com.giggs.heroquest.models.dungeons.traps.Trap;

import java.util.Random;

/**
 * Created by guillaume ON 10/6/14.
 */
public class TrapFactory {

    public static Trap getTrap() {
        Random r = new Random();
        int random = r.nextInt(2);
        switch (random) {
            case 0:
                return new Trap(0, 2);
            default:
                return new Trap(1, 1);
        }
    }

}
