package fr.formiko.pixelary;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;

public class PixmapActor extends Actor {
    private Pixmap pixmap;

    public PixmapActor(Pixmap pixmap) { this.pixmap = pixmap; }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        // float space = 0.8f;
        // int pixelSize = (int) Math.min(w * space / (3 * modelPixmap.getWidth()), h * space / modelPixmap.getHeight());
        int pixelSize = (int) Math.min(getWidth() / pixmap.getWidth(), getHeight() / pixmap.getHeight());
        int h = (int) getHeight();
        if (pixmap == null)
            return;
        for (int i = 0; i < pixmap.getWidth(); i++) {
            for (int j = 0; j < pixmap.getHeight(); j++) {
                int color = pixmap.getPixel(i, j);
                Pixelary.shapeDrawer.setColor(new Color(color));
                Pixelary.shapeDrawer.filledRectangle(getX() + i * pixelSize, getY() + h - (j + 1) * pixelSize, pixelSize, pixelSize);
                Pixelary.shapeDrawer.setColor(Color.BLACK);
                Pixelary.shapeDrawer.rectangle(getX() + i * pixelSize, getY() + h - (j + 1) * pixelSize, pixelSize, pixelSize);
            }
        }
    }
}
