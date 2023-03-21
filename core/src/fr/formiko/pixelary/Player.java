package fr.formiko.pixelary;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Color color;
    private Pen pen;

    public static Player AI = new Player();
    public static Player HUMAN = new Player();
    // public int id; // 0 is the player, 1 is the IA.
    private static float SPEED = 400;
    public Vector2 nextClickPosition;

    public Player() { nextClickPosition = null; }

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public void setColor(int color) { this.color = new Color(color); }
    public Pen getPen() { return pen; }
    public void setPen(Pen pen) {
        this.pen = pen;
        pen.player = this;
    }
    public float getSpeed(float delta) { return SPEED * delta * Gdx.graphics.getWidth() / 1920; }

    // AI part
    public void moveAsAI(float delta) {
        if (nextClickPosition == null || nextClickPosition.x == -1f || nextClickPosition.y == -1f) {
            // Find the next move to do.
            // Simple AI use every color to color every pixel then win.
            if (!placeNextPixel()) {
                changeColor();
            }
        } else {
            // Do the next move to do.
            if (isOverNextClickPosition()) {
                // Pixelary.getAIPixmap().getStage().touchDown((int) nextClickPosition.x, (int) nextClickPosition.y, 0, 0);
                Pixelary.getAIPixmap().clickFromScreenCoo(true, (int) nextClickPosition.x, (int) nextClickPosition.y);
                nextClickPosition = null;
                return;
            } else {
                Vector2 v2 = new Vector2(nextClickPosition.x - pen.getX(), nextClickPosition.y - pen.getY());
                pen.moveFront(getSpeed(delta), v2);
            }
        }

    }

    public boolean isOverNextClickPosition() {
        return pen.getX() > nextClickPosition.x - Pixelary.getPixelSize() / 2
                && pen.getX() < nextClickPosition.x + Pixelary.getPixelSize() / 2
                && pen.getY() > nextClickPosition.y - Pixelary.getPixelSize() / 2
                && pen.getY() < nextClickPosition.y + Pixelary.getPixelSize() / 2;
    }


    /**
     * Do what it needs to change it's color to a color that still have pixel to color.
     */
    public void changeColor() {
        // Vector2 palettePixelToClick = Pixelary.getModelPixmap().getFirstUncompleteColor(Pixelary.getAIPixmap().getPixmap());
        // TODO click on palette instead of auto swap color.
        color = Pixelary.getModelPixmap().getFirstUncompleteColor(Pixelary.getAIPixmap().getPixmap());
    }
    /**
     * Do what it needs to do to place the next pixel with this color.
     * 
     * @return true if there is a next pixel to place with this color.
     */
    public boolean placeNextPixel() {
        Vector2 pixelToDraw = Pixelary.getModelPixmap().getFirstPixelToCompleteWithColor(color, Pixelary.getAIPixmap().getPixmap());
        if (pixelToDraw == null) {
            return false;
        }
        // nextClickPosition = Pixelary.getAIPixmap().getStage().screenToStageCoordinates(new Vector2(pixelToDraw.x, pixelToDraw.y));
        // nextClickPosition = Pixelary.getAIPixmap().getStage()
        // .screenToStageCoordinates(new Vector2(Pixelary.getAIPixmap().getX(), Pixelary.getAIPixmap().getY()));
        // @formatter:off
        nextClickPosition = new Vector2(Pixelary.getAIPixmap().getX() + (pixelToDraw.x+0.5f) * Pixelary.getPixelSize(),
                Pixelary.getAIPixmap().getY() + Pixelary.getAIPixmap().getHeight() - (pixelToDraw.y+0.5f) * Pixelary.getPixelSize());
        // @formatter:on
        System.out.println("pixelToDraw = " + pixelToDraw);
        // System.out.println("nextClickPosition = " + nextClickPosition);
        Pixelary.setSpot(nextClickPosition);
        return true;
    }
}
