package fr.formiko.pixelary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.SelectBox;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;
import com.github.tommyettinger.textra.TypingLabel;

public class TextScreen extends Table {
    public TypingLabel label;
    public BackgroundActor backgroundActor;
    public Color clearColor;
    public SelectBox<String> selectBox;

    public TextScreen(String text, Color clearColor, boolean withDifficultySelectBox) {
        setLayoutEnabled(false);
        this.clearColor = clearColor;

        backgroundActor = new BackgroundActor(clearColor);
        label = new TypingLabel(text, Pixelary.labelStyle);
        label.setAlignment(Align.center);
        label.setSize(label.getPrefWidth(), label.getPrefHeight());
        if (withDifficultySelectBox) {
            selectBox = new SelectBox<String>(Pixelary.selectBoxStyle);
            selectBox.setItems(new String[] {"Insane", "Hard", "Normal", "Easy", "Very easy"});
            selectBox.setSelected("Normal");
            selectBox.setAlignment(Align.center);
        }
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        add(backgroundActor);
        if (selectBox != null) {
            add(selectBox);
        }
        add(label);
    }
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        backgroundActor.setSize(width, height);
        label.setPosition((getWidth() - label.getWidth()) / 2, (getHeight() - label.getHeight()) / 2);
        if (selectBox != null) {
            selectBox.setSize(selectBox.getPrefWidth(), selectBox.getPrefHeight());
            selectBox.setPosition((getWidth() - selectBox.getWidth()) / 2, 0);
        }
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
