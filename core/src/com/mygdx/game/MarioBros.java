package com.mygdx.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.mygdx.game.Screens.PlayScreen;
import com.mygdx.game.Tools.Controller;

/**
 * The central class which contains the game.
 */
public class MarioBros extends Game {
    public static final int V_WIDTH = 400;
    public static final int V_HEIGHT = 308; // 13 (one square)*16 (16 square in total)
    public static final float PPM = 100; //set pixel per meter

    //box2D collision bits

    public static final short NOTHING_BIT = 0;
    public static final short GROUND_BIT = 1;
    public static final short MARIO_BIT = 2;
    public static final short BRICK_BIT = 4;
    public static final short COIN_BIT = 8;
    public static final short DESTROYED_BIT = 16;
    public static final short OBJECT_BIT = 32;
    public static final short ENEMY_BIT = 64;
    public static final short ENEMY_HEAD_BIT = 128;
    public static final short MARIO_HEAD_BIT = 512;

    public static SpriteBatch batch;

    public static AssetManager manager;

    /**
     * Creating the essential assets of the game, like the sprites, background music,
     * and playscreen.
     */
    @Override
    public void create () {
        batch = new SpriteBatch();
        manager = new AssetManager();
        manager.load("audio/music/yokai_music.ogg", Music.class);
        manager.finishLoading();
        setScreen(new PlayScreen(this));
    }

    /**
     * Renders the graphical elements of the game.
     */
    @Override
    public void render () {
        super.render();
    }

}