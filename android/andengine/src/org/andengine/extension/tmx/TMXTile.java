package org.andengine.extension.tmx;

import org.andengine.opengl.texture.region.ITextureRegion;

import java.io.Serializable;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga Inc.
 *
 * @author Nicolas Gramlich
 * @since 10:39:48 - 05.08.2010
 */
public class TMXTile implements Serializable {

    private static final long serialVersionUID = -6845953827871592586L;

    // ===========================================================
    // Constants
    // ===========================================================

    // ===========================================================
    // Fields
    // ===========================================================

    transient int mGlobalTileID;
    private transient final int mTileRow;
    private transient final int mTileColumn;
    private transient final int mTileWidth;
    private transient final int mTileHeight;
    transient ITextureRegion mTextureRegion;
    protected boolean isVisible;


    // ===========================================================
    // Constructors
    // ===========================================================

    public TMXTile(final int pGlobalTileID, final int pTileColumn, final int pTileRow, final int pTileWidth, final int pTileHeight, final ITextureRegion pTextureRegion) {
        this.mGlobalTileID = pGlobalTileID;
        this.mTileRow = pTileRow;
        this.mTileColumn = pTileColumn;
        this.mTileWidth = pTileWidth;
        this.mTileHeight = pTileHeight;
        this.mTextureRegion = pTextureRegion;
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    public int getGlobalTileID() {
        return this.mGlobalTileID;
    }

    public int getTileRow() {
        return this.mTileRow;
    }

    public int getTileColumn() {
        return this.mTileColumn;
    }

    public int getTileX() {
        return this.mTileColumn * this.mTileWidth;
    }

    public int getTileY() {
        return this.mTileRow * this.mTileHeight;
    }

    public int getTileWidth() {
        return this.mTileWidth;
    }

    public int getTileHeight() {
        return this.mTileHeight;
    }

    public ITextureRegion getTextureRegion() {
        return this.mTextureRegion;
    }

    /**
     * Note this will also set the {@link org.andengine.opengl.texture.region.ITextureRegion} with the associated pGlobalTileID of the {@link TMXTiledMap}.
     *
     * @param pTMXTiledMap
     * @param pGlobalTileID
     */
    public void setGlobalTileID(final TMXTiledMap pTMXTiledMap, final int pGlobalTileID) {
        this.mGlobalTileID = pGlobalTileID;
        this.mTextureRegion = pTMXTiledMap.getTextureRegionFromGlobalTileID(pGlobalTileID);
    }

    /**
     * You'd probably want to call {@link TMXTile#setGlobalTileID(TMXTiledMap, int)} instead.
     *
     * @param pTextureRegion
     */
    public void setTextureRegion(final ITextureRegion pTextureRegion) {
        this.mTextureRegion = pTextureRegion;
    }

    public TMXProperties<TMXTileProperty> getTMXTileProperties(final TMXTiledMap pTMXTiledMap) {
        return pTMXTiledMap.getTMXTileProperties(this.mGlobalTileID);
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    // ===========================================================
    // Methods
    // ===========================================================

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public boolean isVisible() {
        return isVisible;
    }

}
