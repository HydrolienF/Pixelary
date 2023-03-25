package fr.formiko.pixelary;

import com.badlogic.gdx.math.Vector2;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.esotericsoftware.spine.utils.SkeletonActor;

public class Frenchzebutt extends SkeletonActor {
    private long stopSpeakingTime;

    public Frenchzebutt() {
        super();
        setName("Frenchzebutt");
        SkeletonRenderer skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        String textureName = "Frenchzebutt";

        Skeleton skeleton = new Skeleton(Pixelary.assets.getSkeletonData(textureName));
        AnimationStateData stateData = new AnimationStateData(Pixelary.assets.getSkeletonData(textureName));
        AnimationState animationState = new AnimationState(stateData);

        animationState.addAnimation(1, "default", true, 0);
        animationState.addAnimation(0, "speak", true, 0);

        setRenderer(skeletonRenderer);
        setSkeleton(skeleton);
        setAnimationState(animationState);
    }

    public long getStopSpeakingTime() { return stopSpeakingTime; }
    public void setStopSpeakingTime(long stopSpeakingTime) { this.stopSpeakingTime = stopSpeakingTime; }

    @Override
    public void act(float delta) {
        if (getSkeleton() != null) {
            getSkeleton().findBone("root").setRotation(getRotation());
            getSkeleton().setScale(getScaleX(), getScaleY());
            super.act(delta);
        }
        if (getAnimationState().getCurrent(0) != null && getAnimationState().getCurrent(0).getAnimation().getName().equals("speak")) {
            setSpeaking(getStopSpeakingTime() > System.currentTimeMillis());
        }
    }
    public void setSpeaking(boolean speaking) {
        if (speaking) {
            getAnimationState().getCurrent(0).setTimeScale(1);
        } else {
            getAnimationState().getCurrent(0).setTimeScale(0);
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
