package com.giggs.heroquest.game.graphics;

import com.giggs.heroquest.models.dungeons.traps.Trap;

import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.opengl.vbo.VertexBufferObjectManager;

/**
 * Created by guillaume on 12/5/14.
 */
public class TrapSprite extends GameElementSprite {

    private final int trapIndex;

    public TrapSprite(Trap trap, VertexBufferObjectManager vertexBufferObjectManager) {
        super(trap, vertexBufferObjectManager);
        setZIndex(3);
        setVisible(false);
        setScale(0.4f);
        trapIndex = trap.getTrapIndex();
    }

    public void setPosition(float pX, float pY) {
        super.setPosition(pX, pY + GameElementSprite.Y_OFFSET);
    }


    public void reveal() {
        animate(new long[]{150, 150, 150, 150}, new int[]{trapIndex, trapIndex + 3, trapIndex + 6, trapIndex + 9}, false, new IAnimationListener() {
            @Override
            public void onAnimationStarted(AnimatedSprite pAnimatedSprite, int pInitialLoopCount) {
            }

            @Override
            public void onAnimationFrameChanged(AnimatedSprite pAnimatedSprite, int pOldFrameIndex, int pNewFrameIndex) {
            }

            @Override
            public void onAnimationLoopFinished(AnimatedSprite pAnimatedSprite, int pRemainingLoopCount, int pInitialLoopCount) {
            }

            @Override
            public void onAnimationFinished(AnimatedSprite pAnimatedSprite) {
                stopAnimation(trapIndex + 9);
            }
        });
        setVisible(true);
    }

}
