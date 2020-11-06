package com.mygdx.game.Sprites;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.CircleShape;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Filter;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.World;
import com.mygdx.game.MarioBros;
import com.mygdx.game.Screens.PlayScreen;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.utils.Array;

/**
 * The Mario class acts to keep the many states of the main playable character.
 */
public class Mario extends Sprite {
    public enum State {FALLING, JUMPING, STANDING, RUNNING, DEAD };
    public State currentState;
    public State previousState;
    public World world;
    public Body b2body;
    private TextureRegion marioStand;
    private TextureRegion marioDead;
    private Animation<TextureRegion> marioRun;
    private Animation<TextureRegion> marioJump;
    private float stateTimer;
    private boolean runningRight;
    private boolean marioIsDead;

    /**
     * This Mario object is to animate the many states, like jumping and dying.
     */
    public Mario(PlayScreen screen){
        super(screen.getAtlas().findRegion("little_mario"));
        this.world=screen.getWorld();
        currentState = State.STANDING;
        previousState = State.STANDING;
        stateTimer = 0;
        runningRight = true;

        Array<TextureRegion> frames = new Array<TextureRegion>();
        for(int i = 1; i < 4; i++)
            frames.add(new TextureRegion(getTexture(), i * 16, 0, 16, 16));
        marioRun = new <TextureRegion>Animation(0.1f, frames);
        frames.clear();

        //only 2 frames in the jump animation
        for(int i = 4; i < 6; i++)
            frames.add(new TextureRegion(getTexture(), i*16, 0, 16, 16));
        marioJump = new <TextureRegion>Animation(0.1f, frames);

        defineMario();
        marioStand = new TextureRegion(getTexture(),0,0,16,16);
        setBounds(0,0,16/MarioBros.PPM,16/MarioBros.PPM);
        setRegion(marioStand);

        marioDead = new TextureRegion(getTexture(), 96,0,16,16);
    }

    /**
     * Updating the world state periodically saving each state of the character moving.
     * @param dt delta time
     */
    public void update(float dt){
        setPosition(b2body.getPosition().x-getWidth()/2,b2body.getPosition().y-getHeight()/2);
        setRegion(getFrame(dt));
    }

    /**
     * Updating mario animation based on the state.
     */
    public TextureRegion getFrame(float dt){
        currentState = getState();

        TextureRegion region;
        switch(currentState){
            case DEAD:
                region = marioDead;
                break;
            case JUMPING:
                region = marioJump.getKeyFrame(stateTimer);
                break;
            case RUNNING:
                region = marioRun.getKeyFrame(stateTimer, true);
                break;
            case FALLING:
            case STANDING:
            default:
                region = marioStand;
                break;
        }

        //takes care of flipping when player is not moving right
        if(((b2body.getLinearVelocity().x < 0) || !runningRight) && !region.isFlipX()){
            region.flip(true, false);
            runningRight = false;
        } else if (((b2body.getLinearVelocity().x > 0 || runningRight) && region.isFlipX())) {
            region.flip(true, false);
            runningRight = true;
        }
        stateTimer = currentState == previousState ? stateTimer + dt : 0;
        previousState = currentState;
        return region;
    }

    /**
     * Update state of playable main character.
     * @return Returns state based on linear velocity of the playable character body.
     */
    public State getState(){
        if(marioIsDead)
            return State.DEAD;
        else if(b2body.getLinearVelocity().y > 0 || (b2body.getLinearVelocity().y < 0 && previousState == State.JUMPING))
            return State.JUMPING;
        else if (b2body.getLinearVelocity().y < 0)
            return State.FALLING;
        else if (b2body.getLinearVelocity().x != 0)
            return State.RUNNING;
        else
            return State.STANDING;
    }

    public boolean isDead(){
        return marioIsDead;
    }

    public float getStateTimer(){
        return stateTimer;
    }

    //hit is not activated yet, have to go back to the part where the video explains about this bit
    public void hit() {
        //MarioBros.manager.get("audio/music/mario_music.ogg", Music.class).stop();
        //MarioBros.manager.get("audio/sounds/mariodie.wav", Sound.class).play();
        marioIsDead = true;
        Filter filter = new Filter();
        filter.maskBits = MarioBros.NOTHING_BIT;
        for (Fixture fixture : b2body.getFixtureList()) {
            fixture.setFilterData(filter);
        }

        b2body.applyLinearImpulse(new Vector2(0, 4f), b2body.getWorldCenter(), true);
    }

    /**
     * Creating the Mario b2body.
     */
    public void defineMario(){
        BodyDef bdef = new BodyDef();
        bdef.position.set(32/ MarioBros.PPM,32/MarioBros.PPM);
        bdef.type = BodyDef.BodyType.DynamicBody;
        b2body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        CircleShape shape = new CircleShape();
        shape.setRadius(5/MarioBros.PPM); // size of the body
        fdef.filter.categoryBits = MarioBros.MARIO_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT | MarioBros.COIN_BIT | MarioBros.BRICK_BIT;
        fdef.filter.categoryBits = MarioBros.ENEMY_BIT;
        fdef.filter.maskBits = MarioBros.GROUND_BIT |
                MarioBros.COIN_BIT |
                MarioBros.BRICK_BIT |
                MarioBros.ENEMY_BIT |
                MarioBros.OBJECT_BIT|
                MarioBros.ENEMY_HEAD_BIT
        ;

        fdef.shape=shape;
        b2body.createFixture(fdef);

        EdgeShape head = new EdgeShape();
        head.set(new Vector2(-2 / MarioBros.PPM, 7 / MarioBros.PPM), new Vector2(2 / MarioBros.PPM, 7 / MarioBros.PPM));
        fdef.shape = head;
        fdef.isSensor = true;

        b2body.createFixture(fdef).setUserData("head");



    }
}
