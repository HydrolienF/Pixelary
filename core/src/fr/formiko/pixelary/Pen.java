package fr.formiko.pixelary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.utils.SkeletonActor;

public class Pen extends SkeletonActor {
    public Player player;

    @Override
    public void act(float delta) {
        if (player != null) {
            getSkeleton().findSlot("pen color").getColor().set(player.getColor());
            if (player.equals(Player.HUMAN)) {
                setPosition(Gdx.input.getX() - getWidth() / 2, Gdx.graphics.getHeight() - Gdx.input.getY());
            } else {
                player.moveAsAI(delta);
            }
        }
        if (getSkeleton() != null) {
            getSkeleton().findBone("root").setRotation(getRotation());
            getSkeleton().setScale(getScaleX(), getScaleY());
            super.act(delta);
        }
    }

    /**
     * {@summary Move in x &#38; y}
     * 
     * @param x x
     * @param y y
     */
    public void translate(float x, float y) {
        setX(getX() + x);
        setY(getY() + y);
    }
    /**
     * {@summary Move in the facing direction.}
     * 
     * @param distance distance to move
     */
    public void moveFront(float distance, Vector2 direction) {
        float facingAngle = direction.angleDeg();
        translate((float) (distance * Math.cos(Math.toRadians(facingAngle))), (float) (distance * Math.sin(Math.toRadians(facingAngle))));
    }
}
