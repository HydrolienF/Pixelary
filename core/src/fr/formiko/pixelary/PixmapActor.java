package fr.formiko.pixelary;

import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;

public class PixmapActor extends Actor {
    private final Pixmap pixmap;
    private final boolean pic;
    private boolean editableByPlayer;
    private boolean displayhelp;

    public PixmapActor(Pixmap pixmap, boolean pic) {
        super();
        this.pixmap = pixmap;
        this.pic = pic;
        addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) { click(false, (int) x, (int) y); }
            @Override
            public void touchDragged(InputEvent event, float x, float y, int pointer) { click(false, (int) x, (int) y); }
        });
        editableByPlayer = true;
    }
    public PixmapActor(Pixmap pixmap) { this(pixmap, false); }

    public float getCenterX() { return getX() + getWidth() / 2; }
    public float getCenterY() { return getY() + getHeight() / 2; }
    public void setCenterX(float x) { setX(x - getWidth() / 2); }
    public void setCenterY(float y) { setY(y - getHeight() / 2); }
    public Pixmap getPixmap() { return pixmap; }
    public boolean isEditableByPlayer() { return editableByPlayer; }
    public void setEditableByPlayer(boolean editableByPlayer) { this.editableByPlayer = editableByPlayer; }
    public void switchDisplayHelp() {
        displayhelp = !displayhelp;
        System.out.println("displayhelp = " + displayhelp);
    }

    /**
     * Set size then resize to fit the pixmap racio.
     */
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        super.setSize((int) (getPixelSize() * pixmap.getWidth()), (int) (getPixelSize() * pixmap.getHeight()));
        // System.out.println("setSize " + width + " " + height + " " + getWidth() + " " + getHeight());
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
                float x = getX() + i * pixelSize;
                float y = getY() + h - (j + 1) * pixelSize;
                // Pixelary.shapeDrawer.setColor(NumberUtils.intToFloatColor(color));
                // Pixelary.shapeDrawer.getPackedColor();
                Pixelary.shapeDrawer.filledRectangle(x, y, pixelSize, pixelSize);
                if (displayhelp && Pixelary.getModelPixmap().getPixmap().getPixel(i, j) != color) {
                    Color c = new Color(color);
                    Pixelary.shapeDrawer.setColor(1f - c.r, 1f - c.g, 1f - c.b, 1f);
                    Pixelary.shapeDrawer.line(x, y, x + pixelSize, y + pixelSize);
                    Pixelary.shapeDrawer.line(x + pixelSize, y, x, y + pixelSize);
                    // Pixelary.shapeDrawer.rectangle(getX() + i * pixelSize + 1, getY() + h - (j + 1) * pixelSize + 1, pixelSize - 2,
                    // pixelSize - 2);
                }
                Pixelary.shapeDrawer.setColor(Color.BLACK);
                Pixelary.shapeDrawer.rectangle(x, y, pixelSize, pixelSize);
            }
        }
    }

    /**
     * React to a click on screen by setting color or getting color from palette.
     */
    public void click(boolean isAI, int x, int y) {
        // System.out.println("Click on " + x + " " + y + " " + isAI);
        Player player;
        if (isAI) {
            player = Player.AI;
        } else {
            if (!isEditableByPlayer()) {
                return;
            }
            player = Player.HUMAN;
        }
        // get clicked position
        int pixelSize = (int) getPixelSize();
        int i = x / pixelSize;
        int j = (int) (getHeight() - y) / pixelSize;
        // System.out.println("Click match pixmap location " + i + " " + j);

        if (pic) {
            // System.out.println("Pic color on " + i + " " + j);
            player.setColor(pixmap.getPixel(i, j));
        } else {
            // System.out.println("Place color on " + i + " " + j);
            pixmap.setColor(player.getColor());
            pixmap.fillRectangle(i, j, 1, 1);
            // System.out.println("Color " + player.getColor() + " placed on " + i + " " + j);
        }
    }
    /**
     * React to a click on screen by setting color or getting color from palette.
     */
    public void clickFromScreenCoo(boolean isAI, int x, int y) { click(isAI, x - (int) getX(), y - (int) getY()); }


    public Vector2 getFirstPixelToCompleteWithColor(Color color, Pixmap playerPixmap) {
        for (int j = 0; j < pixmap.getHeight(); j++) {
            for (int i = 0; i < pixmap.getWidth(); i++) {
                // Different color between the 2 pixmap & pen have the wanted color.
                if (pixmap.getPixel(i, j) != playerPixmap.getPixel(i, j) && new Color(pixmap.getPixel(i, j)).equals(color)) {
                    return new Vector2(i, j);
                }
            }
        }
        return null;
    }
    public Color getFirstUncompleteColor(Pixmap playerPixmap) {
        for (int j = 0; j < pixmap.getHeight(); j++) {
            for (int i = 0; i < pixmap.getWidth(); i++) {
                if (pixmap.getPixel(i, j) != playerPixmap.getPixel(i, j)) {
                    return new Color(pixmap.getPixel(i, j));
                }
            }
        }
        return null;
    }
    public Vector2 getFirstPixelWithColor(Color color) {
        for (int j = 0; j < pixmap.getHeight(); j++) {
            for (int i = 0; i < pixmap.getWidth(); i++) {
                if (new Color(pixmap.getPixel(i, j)).equals(color)) {
                    return new Vector2(i, j);
                }
            }
        }
        return null;
    }

    public Vector2 toScreenCoord(Vector2 v) {
        return new Vector2((v.x + 0.5f) * getPixelSize() + getX(), getHeight() - (v.y + 0.5f) * getPixelSize() + getY());
    }
    // public Vector2 toPixmapCoord(Vector2 v) {
    // return new Vector2((v.x - getX()) / getPixelSize() - 0.5f, (v.y - getY()) / getPixelSize() - 0.5f);
    // }

    public boolean containsCoo(int x, int y) { return x >= getX() && x <= getX() + getWidth() && y >= getY() && y <= getY() + getHeight(); }


    public Vector2 getRandomPixelToDestroy(Color color, Pixmap playerPixmap) {
        List<Vector2> pixels = new ArrayList<Vector2>();
        for (int j = 0; j < pixmap.getHeight(); j++) {
            for (int i = 0; i < pixmap.getWidth(); i++) {
                // Different color between the 2 pixmap & pen have the wanted color.
                if (pixmap.getPixel(i, j) == playerPixmap.getPixel(i, j) && !new Color(playerPixmap.getPixel(i, j)).equals(color)) {
                    pixels.add(new Vector2(i, j));
                }
            }
        }
        return pixels.get(Pixelary.random.nextInt(pixels.size()));
    }

    public List<Vector2> getAllPixelWithSameColor(Color color) {
        List<Vector2> pixels = new ArrayList<Vector2>();
        for (int j = 0; j < pixmap.getHeight(); j++) {
            for (int i = 0; i < pixmap.getWidth(); i++) {
                if (new Color(pixmap.getPixel(i, j)).equals(color)) {
                    pixels.add(new Vector2(i, j));
                }
            }
        }
        return pixels;
    }
}
