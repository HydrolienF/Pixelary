package fr.formiko.pixelary;

import com.badlogic.gdx.Gdx;
import com.esotericsoftware.spine.utils.SkeletonActor;

public class Pen extends SkeletonActor {
    public Player player;

    @Override
    public void act(float delta) {
        if (player != null) {
            getSkeleton().findSlot("pen color").getColor().set(player.getColor());
            if (player.equals(Player.HUMAN)) {
                setPosition(Gdx.input.getX() - getWidth() / 2, Gdx.graphics.getHeight() - Gdx.input.getY());
            }
        }
        if (getSkeleton() != null) {
            getSkeleton().findBone("root").setRotation(getRotation());
            getSkeleton().setScale(getScaleX(), getScaleY());
            super.act(delta);
        }
    }
}
