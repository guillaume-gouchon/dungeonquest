package com.giggs.heroquest.game.base;

import android.util.Log;

import com.giggs.heroquest.game.graphics.GameElementSprite;
import com.giggs.heroquest.game.graphics.GraphicHolder;
import com.giggs.heroquest.models.StorableResource;
import com.giggs.heroquest.models.characters.Ranks;
import com.giggs.heroquest.models.dungeons.Tile;
import com.giggs.heroquest.utils.pathfinding.MathUtils;

import org.andengine.opengl.vbo.VertexBufferObjectManager;
import org.andengine.util.color.Color;

public abstract class GameElement extends StorableResource implements GraphicHolder {

    private static final String TAG = "GameElement";
    private static final long serialVersionUID = 3125325887530074781L;

    private final int spriteWidth, spriteHeight, nbSpritesX, nbSpritesY;
    protected transient GameElementSprite sprite;
    protected transient Tile tilePosition;
    private Ranks rank;

    public GameElement(String identifier, Ranks rank, int spriteWidth, int spriteHeight, int nbSpritesX, int nbSpritesY) {
        super(identifier);
        this.rank = rank;
        this.spriteWidth = spriteWidth;
        this.spriteHeight = spriteHeight;
        this.nbSpritesY = nbSpritesY;
        this.nbSpritesX = nbSpritesX;
    }

    public abstract void createSprite(VertexBufferObjectManager vertexBufferObjectManager);

    public GameElementSprite getSprite() {
        return sprite;
    }

    public Tile getTilePosition() {
        return tilePosition;
    }

    public void setTilePosition(Tile tilePosition) {
        Log.d(TAG, "new tile for " + identifier + " = " + (tilePosition != null ? tilePosition.getX() + ", " + tilePosition.getY() : tilePosition));
        if (this.tilePosition != null) {
            this.tilePosition.setContent(null);
        }
        this.tilePosition = tilePosition;
        if (tilePosition != null) {
            tilePosition.setContent(this);
            if (sprite != null) {
                sprite.setZIndex(10 + tilePosition.getY());
            }
        }
    }

    public Color getSelectionColor() {
        switch (rank) {
            case ENEMY:
                return new Color(1.0f, 0.0f, 0.0f, 0.7f);
            default:
                return new Color(1.0f, 1.0f, 1.0f, 0.7f);
        }
    }

    public boolean isEnemy(GameElement gameElement) {
        return (rank == Ranks.ME || rank == Ranks.ALLY) && gameElement.getRank() == Ranks.ENEMY || rank == Ranks.ENEMY && (gameElement.getRank() == Ranks.ME || gameElement.getRank() == Ranks.ALLY);
    }

    public boolean isNextTo(Tile tile) {
        return MathUtils.calcManhattanDistance(tilePosition, tile) == 1;
    }

    public Ranks getRank() {
        return rank;
    }

    public void setRank(Ranks rank) {
        this.rank = rank;
    }

    public void destroy() {
        setTilePosition(null);
    }

    @Override
    public String getSpriteName() {
        return identifier + ".png";
    }

    @Override
    public int getNbSpritesY() {
        return nbSpritesY;
    }

    @Override
    public int getSpriteWidth() {
        return spriteWidth;
    }

    @Override
    public int getSpriteHeight() {
        return spriteHeight;
    }

    @Override
    public int getNbSpritesX() {
        return nbSpritesX;
    }

    public boolean isVisible() {
        return tilePosition.isVisible();
    }

}

