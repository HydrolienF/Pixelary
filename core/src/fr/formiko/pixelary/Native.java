package fr.formiko.pixelary;

import com.badlogic.gdx.Gdx;

public interface Native {
    public void exit();
}

class NullNative implements Native {
    @Override
    public void exit() { Gdx.app.exit(); }
}