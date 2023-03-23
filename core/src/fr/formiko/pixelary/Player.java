package fr.formiko.pixelary;

import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Player {
    private Color color;
    private Pen pen;

    public static Player AI = new Player();
    public static Player HUMAN = new Player();
    // public int id; // 0 is the player, 1 is the IA.
    public static float SPEED = 200;
    public Vector2 nextClickPosition;
    public List<Action> actions;
    public boolean playOnModel;


    // Constructors ---------------------------------------------------------------------------------------------------
    public Player() {
        nextClickPosition = null;
        actions = new LinkedList<Action>();
    }

    // get set --------------------------------------------------------------------------------------------------------
    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public void setColor(int color) { this.color = new Color(color); }
    public Pen getPen() { return pen; }
    public void setPen(Pen pen) {
        this.pen = pen;
        pen.player = this;
    }
    public float getSpeed(float delta) { return SPEED * delta * Gdx.graphics.getWidth() / 1920; }
    public boolean isPlayOnModel() { return playOnModel; }
    public void setPlayOnModel(boolean playOnModel) {
        this.playOnModel = playOnModel;
        SPEED *= 3;
    }

    // Functions ------------------------------------------------------------------------------------------------------
    // AI part
    public void moveAsAI(float delta) {
        if (nextClickPosition == null || nextClickPosition.x == -1f || nextClickPosition.y == -1f) {
            // Find the next move to do.
            if (actions.size() > 0 && actions.get(0).triggered()) { // annoy player
                Action a = actions.get(0);
                nextClickPosition = a.getClickPosition();
                if (a.isOver()) {
                    actions.remove(0);
                }
                // System.out.println("nextClickPosition to annoy player = " + nextClickPosition);
            } else { // get a better score.
                // Simple AI use every color to color every pixel then win.
                if (!placeNextPixel()) {
                    changeColor();
                }
            }
        } else {
            // Do the next move to do.
            if (isOverNextClickPosition()) {
                // Pixelary.getAIPixmap().getStage().touchDown((int) nextClickPosition.x, (int) nextClickPosition.y, 0, 0);
                Pixelary.clickFromScreenCoo(true, (int) nextClickPosition.x, (int) nextClickPosition.y);
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
        // color = Pixelary.getModelPixmap().getFirstUncompleteColor(Pixelary.getAIPixmap().getPixmap());
        PixmapActor aiPixmapActor;
        PixmapActor modelPixmapActor;
        if (playOnModel) { // reverse mode
            aiPixmapActor = Pixelary.getModelPixmap();
            modelPixmapActor = Pixelary.getAIPixmap();
        } else { // normal mode
            aiPixmapActor = Pixelary.getAIPixmap();
            modelPixmapActor = Pixelary.getModelPixmap();
        }
        Color firstUnComplete = modelPixmapActor.getFirstUncompleteColor(aiPixmapActor.getPixmap());
        // Color firstUnComplete = Pixelary.getModelPixmap().getFirstUncompleteColor(Pixelary.getAIPixmap().getPixmap());
        if (Pixelary.currentLevel == 3 && !(color.g >= color.b && color.g >= color.r)) {
            System.out.println("Swap for color : " + color.r + " " + color.g + " " + color.b);
            firstUnComplete = Color.WHITE;
            setPlayOnModel(true);
        }
        Vector2 pixelToPic = Pixelary.getAIPalette().getFirstPixelWithColor(firstUnComplete);
        if (pixelToPic == null) {
            return;
        }
        nextClickPosition = Pixelary.getAIPalette().toScreenCoord(pixelToPic);
        Pixelary.setSpot(nextClickPosition);
    }
    /**
     * Do what it needs to do to place the next pixel with this color.
     * 
     * @return true if there is a next pixel to place with this color.
     */
    public boolean placeNextPixel() {
        PixmapActor aiPixmapActor;
        PixmapActor modelPixmapActor;
        if (playOnModel) { // reverse mode
            aiPixmapActor = Pixelary.getModelPixmap();
            modelPixmapActor = Pixelary.getAIPixmap();
        } else { // normal mode
            aiPixmapActor = Pixelary.getAIPixmap();
            modelPixmapActor = Pixelary.getModelPixmap();
        }
        Vector2 pixelToDraw = modelPixmapActor.getFirstPixelToCompleteWithColor(color, aiPixmapActor.getPixmap());
        if (pixelToDraw == null) {
            return false;
        }
        // System.out.println("pixelToDraw = " + pixelToDraw);
        nextClickPosition = aiPixmapActor.toScreenCoord(pixelToDraw);
        Pixelary.setSpot(nextClickPosition);
        return true;
    }
}
