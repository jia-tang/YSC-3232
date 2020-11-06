package com.mygdx.game.Screens;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MarioBros;

//This class is the part where I create the game over screen
// which gets triggered when the player is in the DEAD state.

public class GameOverScreen implements Screen {
    private Viewport viewport;
    private Stage stage;
    private Game game;
    public GameOverScreen(Game game){
        this.game = game;
        viewport = new FitViewport(MarioBros.V_WIDTH, MarioBros.V_HEIGHT, new OrthographicCamera());
        stage = new Stage(viewport, ((MarioBros) game).batch);

        Label.LabelStyle font = new Label.LabelStyle(new BitmapFont(), Color.WHITE);
//Set where the message is going to be displayed
        Table table = new Table();
        table.center();
        table.setFillParent(true);

        //message to be displayed:
        Label gameOverLabel = new Label("GAME OVER", font);
        Label PlayAgainLabel = new Label("Click to Play Again", font);

        table.add(gameOverLabel).expandX();
        table.row();
        table.add(PlayAgainLabel).expandX().padTop(10f);

        stage.addActor(table);

    }


    @Override
    public void show() {

    }

    @Override
//
    public void render(float delta) {
        if(Gdx.input.justTouched()) {
            //This part takes care of the player can click on the screen to replay the game
            game.setScreen(new PlayScreen((MarioBros) game));
            dispose();
        }
        Gdx.gl.glClearColor(34,139,3, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.draw();
// renders out the screen with a given background color

    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {
        stage.dispose();
    }
}
