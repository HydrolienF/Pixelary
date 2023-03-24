package fr.formiko.pixelary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TypingLabel;

public class TextScreen extends Table {
    public TypingLabel label;
    public BackgroundActor backgroundActor;
    public Color clearColor;
    public TextScreen(String text, Color clearColor) {
        setLayoutEnabled(false);
        this.clearColor = clearColor;

        backgroundActor = new BackgroundActor(clearColor);
        label = new TypingLabel(text, Pixelary.labelStyle);
        label.setAlignment(Align.center);
        label.setSize(label.getPrefWidth(), label.getPrefHeight());
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        add(backgroundActor);
        add(label);
    }
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        backgroundActor.setSize(width, height);
        label.setPosition((getWidth() - label.getWidth()) / 2, (getHeight() - label.getHeight()) / 2);
    }
}
class BackgroundActor extends Actor {
    public Color clearColor;
    public BackgroundActor(Color clearColor) {
        this.clearColor = clearColor;
        setPosition(0, 0);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // batch.begin();
        Pixelary.shapeDrawer.setColor(clearColor);
        Pixelary.shapeDrawer.filledRectangle(getX(), getY(), getWidth(), getHeight());
        // batch.end();
    }
}
