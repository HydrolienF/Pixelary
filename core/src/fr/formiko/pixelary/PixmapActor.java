package fr.formiko.pixelary;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PixmapActor extends Actor {
    private final Pixmap pixmap;
    private final boolean pic;

    public PixmapActor(Pixmap pixmap, boolean pic) {
        super();
        this.pixmap = pixmap;
        this.pic = pic;
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { click(false, (int) x, (int) y); }
        });
    }
    public PixmapActor(Pixmap pixmap) { this(pixmap, false); }

    public float getCenterX() { return getX() + getWidth() / 2; }
    public float getCenterY() { return getY() + getHeight() / 2; }
    public void setCenterX(float x) { setX(x - getWidth() / 2); }
    public void setCenterY(float y) { setY(y - getHeight() / 2); }
    public Pixmap getPixmap() { return pixmap; }
    /**
     * Set size then resize to fit the pixmap racio.
     */
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        super.setSize((int) (getPixelSize() * pixmap.getWidth()), (int) (getPixelSize() * pixmap.getHeight()));
        System.out.println("setSize " + width + " " + height + " " + getWidth() + " " + getHeight());
    }
    public float getPixelSize() { return Math.min(getWidth() / pixmap.getWidth(), getHeight() / pixmap.getHeight()); }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        super.draw(batch, parentAlpha);
        // float space = 0.8f;
        // int pixelSize = (int) Math.min(w * space / (3 * modelPixmap.getWidth()), h * space / modelPixmap.getHeight());
        int pixelSize = (int) getPixelSize();
        int h = (int) getHeight();
        if (pixmap == null)
            return;
        for (int i = 0; i < pixmap.getWidth(); i++) {
            for (int j = 0; j < pixmap.getHeight(); j++) {
                int color = pixmap.getPixel(i, j);
                Pixelary.shapeDrawer.setColor(new Color(color));
                // Pixelary.shapeDrawer.setColor(NumberUtils.intToFloatColor(color));
                // Pixelary.shapeDrawer.getPackedColor();
                Pixelary.shapeDrawer.filledRectangle(getX() + i * pixelSize, getY() + h - (j + 1) * pixelSize, pixelSize, pixelSize);
                Pixelary.shapeDrawer.setColor(Color.BLACK);
                Pixelary.shapeDrawer.rectangle(getX() + i * pixelSize, getY() + h - (j + 1) * pixelSize, pixelSize, pixelSize);
            }
        }
    }

    /**
     * React to a click on screen by setting color or getting color from palette.
     */
    public void click(boolean isAI, int x, int y) {
        Player player;
        if (isAI) {
            player = Player.AI;
        } else {
            player = Player.HUMAN;
        }
        // get clicked position
        int pixelSize = (int) getPixelSize();
        int i = x / pixelSize;
        int j = (int) (getHeight() - y) / pixelSize;

        if (pic) {
            System.out.println("Pic color on " + i + " " + j);
            player.setColor(pixmap.getPixel(i, j));
        } else {
            System.out.println("Place color on " + i + " " + j);
            pixmap.setColor(player.getColor());
            pixmap.fillRectangle(i, j, 1, 1);
        }
    }
}
