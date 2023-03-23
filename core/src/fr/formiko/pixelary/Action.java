package fr.formiko.pixelary;

import java.util.LinkedList;
import java.util.List;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.math.Vector2;

public class Action {
    enum Type {
        ONE_PIXEL, ONE_LINE, REPLACE_COLOR
    }
    float trigger;
    Type type;
    int times;
    PixmapActor pma;
    List<Vector2> nextPixel;
    boolean triggered;

    public Action(float trigger, Type type, int times, PixmapActor pma) {
        this.trigger = trigger;
        this.type = type;
        this.times = times;
        this.pma = pma;
        nextPixel = new LinkedList<Vector2>();
    }

    public boolean triggered() {
        if (Pixelary.scorePlayer >= trigger) {
            triggered = true;
        }
        return triggered;
    }

    public Vector2 getClickPosition() {
        if (!nextPixel.isEmpty()) {
            return Pixelary.getPlayerPixmap().toScreenCoord(nextPixel.remove(0));
        }
        Color color = Player.AI.getColor();
        System.out.println("New action to ennoy player at " + trigger + " type " + type + " times " + times + " color " + color.r + " "
                + color.g + " " + color.b);
        switch (type) {
        case ONE_PIXEL:
            for (int i = 0; i < times; i++) {
                Vector2 v = Pixelary.getModelPixmap().getRandomPixelToDestroy(color, pma.getPixmap());
                if (!nextPixel.contains(v)) {
                    nextPixel.add(v);
                }
            }
            nextPixel.sort((Vector2 v1, Vector2 v2) -> (int) (v1.x * 1000 + v1.y - v2.x * 1000 - v2.y));
            break;
        case ONE_LINE:
            Vector2 center = Pixelary.getModelPixmap().getRandomPixelToDestroy(color, pma.getPixmap());
            nextPixel.add(center);
            int k = Pixelary.random.nextBoolean() ? -1 : 1;
            int i = 0;
            int j = 0;
            while (i + j < times) {
                if (Pixelary.random.nextBoolean()) {
                    i += k;
                } else {
                    j += k;
                }
                if (center.x + i < 0 || center.x + i >= Pixelary.getPlayerPixmap().getPixmap().getWidth() || center.y + j < 0
                        || center.y + j >= Pixelary.getPlayerPixmap().getPixmap().getHeight()) {
                    break;
                }
                nextPixel.add(new Vector2(center.x + i, center.y + j));
            }
            break;
        case REPLACE_COLOR:
            break;
        }
        if (!nextPixel.isEmpty()) {
            return Pixelary.getPlayerPixmap().toScreenCoord(nextPixel.remove(0));
        } else {
            return null;
        }
    }

    public boolean isOver() { return nextPixel.isEmpty(); }
}
