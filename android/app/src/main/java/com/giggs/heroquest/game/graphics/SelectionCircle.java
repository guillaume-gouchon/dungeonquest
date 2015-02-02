package com.giggs.heroquest.game.graphics;

import com.giggs.heroquest.game.andengine.custom.CenteredSprite;
import com.giggs.heroquest.game.base.GameElement;
import com.giggs.heroquest.game.base.GraphicsManager;

import org.andengine.opengl.vbo.VertexBufferObjectManager;

public class SelectionCircle extends CenteredSprite {

    private GameElement gameElement;

    public SelectionCircle(VertexBufferObjectManager pVertexBufferObjectManager) {
        super(0, 0, GraphicsManager.sGfxMap.get("selection.png"), pVertexBufferObjectManager);
        setScale(0.5f);
        setZIndex(5);
    }

    @Override
    protected void onManagedUpdate(final float pSecondsElapsed) {
        this.setRotation(getRotation() + 0.8f);
        super.onManagedUpdate(pSecondsElapsed);

        if (gameElement != null) {
            GameElementSprite sprite = gameElement.getSprite();
            setPosition(sprite.getX(), sprite.getY() - GameElementSprite.Y_OFFSET + getHeightScaled() / 2);
        }
    }

    public void attachToGameElement(GameElement gameElement) {
        this.gameElement = gameElement;
        setColor(gameElement.getSelectionColor());
    }

}
