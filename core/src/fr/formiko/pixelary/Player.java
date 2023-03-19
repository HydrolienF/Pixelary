package fr.formiko.pixelary;

import com.badlogic.gdx.graphics.Color;

public class Player {
    private Color color;

    public static Player AI = new Player();
    public static Player HUMAN = new Player();
    // public int id; // 0 is the player, 1 is the IA.

    public Player() {}

    public Color getColor() { return color; }
    public void setColor(Color color) { this.color = color; }
    public void setColor(int color) { this.color = new Color(color); }
}
