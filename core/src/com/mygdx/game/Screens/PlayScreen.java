package com.mygdx.game.Screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Scenes.Hud;
import com.mygdx.game.Sprites.Enemy;
import com.mygdx.game.Sprites.Goomba;
import com.mygdx.game.Sprites.Mario;
import com.mygdx.game.Tools.B2WorldCreator;
import com.mygdx.game.Tools.Controller;
import com.mygdx.game.Tools.WorldContactListener;

/**
 Represents the main screen of our game.
 Calls timer, box2d world, map, player sprite, enemy sprite, controller, camera classes.
 */
public class PlayScreen extends ApplicationAdapter implements Screen {
    private MarioBros game;
    private TextureAtlas atlas;

    Texture texture;
    private OrthographicCamera gamecam;
    private Viewport gamePort;
    private Hud hud;

    //Tiled map variables
    private TmxMapLoader maploader;
    private TiledMap map;
    private OrthogonalTiledMapRenderer renderer;

    //Box2d variables
    private World world;
    private Box2DDebugRenderer b2dr;
    private B2WorldCreator creator;

    // player sprite
    private Mario player;
    private Music music;

    public static SpriteBatch batch;
    // Controller
    Controller controller;

    public PlayScreen(MarioBros game) {
        atlas = new TextureAtlas("Mario_and_Enemies.pack");

        this.game = game;

        //create camera to follow player's movement
        gamecam = new OrthographicCamera();

        gamePort = new FitViewport(MarioBros.V_WIDTH / MarioBros.PPM, MarioBros.V_HEIGHT / MarioBros.PPM, gamecam);

        // create hud for timer
        hud = new Hud(game.batch);

        //Load the tiled map and set up map renderer
        maploader = new TmxMapLoader();
        map = maploader.load("ourgame1.tmx");
        renderer = new OrthogonalTiledMapRenderer(map, 1  / MarioBros.PPM);

        // The gamecamera is set at the center of the game screen at the start
        gamecam.position.set(gamePort.getWorldWidth() / 2, gamePort.getWorldHeight() / 2, 0);

        //create Box2D world, setting no gravity in X, -10 gravity in Y, and allow bodies to sleep
        world = new World(new Vector2(0, -10), true);
        b2dr = new Box2DDebugRenderer();

        BodyDef bdef = new BodyDef();
        PolygonShape shape = new PolygonShape();
        FixtureDef fdef = new FixtureDef();
        Body body;

        creator = new B2WorldCreator(this);

        //create player in the game world
        player=new Mario(this);

        world.setContactListener(new WorldContactListener());
        music = MarioBros.manager.get("audio/music/yokai_music.ogg", Music.class); //load the music from the assets folder
        music.setLooping(true); //loops the music
        music.play();

        //create controller
        controller = new Controller();


    }

    public TextureAtlas getAtlas(){
        return atlas;
    }


    /**
     * Recieves the input from keyboard and touch screen
     * Link to a class named 'Controller': {@link com.mygdx.game.Tools.Controller}
     */
    public void handleInput(float dt){
        // this is for touch screen
        if(player.currentState != Mario.State.DEAD) {
            if (controller.isRightPressed())
                player.b2body.setLinearVelocity(new Vector2(1, player.b2body.getLinearVelocity().y));
            else if (controller.isLeftPressed())
                player.b2body.setLinearVelocity(new Vector2(-1, player.b2body.getLinearVelocity().y));
            else
                player.b2body.setLinearVelocity(new Vector2(0, player.b2body.getLinearVelocity().y));

            if (controller.isUpPressed() && player.b2body.getLinearVelocity().y == 0)
                player.b2body.applyLinearImpulse(new Vector2(0, 5f), player.b2body.getWorldCenter(), true);

            // this is for keyboard input
            if (Gdx.input.isKeyJustPressed(Input.Keys.UP))
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0.5f), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) && player.b2body.getLinearVelocity().x <= 2)
                player.b2body.applyLinearImpulse(new Vector2(0.1f, 0), player.b2body.getWorldCenter(), true);
            if (Gdx.input.isKeyPressed(Input.Keys.LEFT) && player.b2body.getLinearVelocity().x >= -2)
                player.b2body.applyLinearImpulse(new Vector2(-0.1f, 0), player.b2body.getWorldCenter(), true);

        }
    }

    /**
     * update the screen, the update time is 1/60 second.
     * handle the user input first, then locate and update the game camera to the player's coordinates.
     */

    public void update (float dt){
        //handle user input
        handleInput(dt);
        for(Enemy enemy : creator.getGoombas())
            enemy.update(dt);

        //1 step is 1/60 second
        world.step(1/60f,6,2);
        player.update(dt);
        hud.update(dt);

        //locate gamecam to the player coordinates
        gamecam.position.x = player.b2body.getPosition().x;
        gamecam.position.y = player.b2body.getPosition().y;

        //update gamecam with player coordinates
        gamecam.update();
        renderer.setView(gamecam);

    }



    @Override
    public void show() {
    }

    /**
     * render the game map, draw the timer label
     */

    @Override
    public void render(float delta) {
        update(delta);
        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // render the game map
        renderer.render();
        b2dr.render(world,gamecam.combined);
        game.batch.setProjectionMatrix(gamecam.combined);
        game.batch.begin();
        player.draw(game.batch); // draw is in the sprite class
        for(Enemy enemy : creator.getGoombas())
            enemy.draw(game.batch);
        game.batch.end();

        // draw Hud (timer label)
        game.batch.setProjectionMatrix(hud.stage.getCamera().combined);
        hud.stage.draw();

        if(gameOver()){
            game.setScreen(new GameOverScreen(game));
            dispose();

        }

        controller.draw();

    }

    /**
     * get the Mario's state. Return true if Mario's state is dead
     * link to a class called "Mario" : {@link com.mygdx.game.Sprites.Mario}
     */

    public boolean gameOver(){
        if(player.currentState == Mario.State.DEAD && player.getStateTimer() > 10){
            return true;
        }
        return false;
    }


    /**
     * Update the game viewport and controller
     */
    @Override
    public void resize(int width, int height) {

        gamePort.update(width,height);
        controller.resize(width, height);
    }

    /**
     * Get the tiled map
     */
    public TiledMap getMap(){
        return map;
    }

    public World getWorld(){
        return world;
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
        //dispose of all our resources
        map.dispose();
        renderer.dispose();
        world.dispose();
        b2dr.dispose();
        hud.dispose();
    }
}

