package fr.formiko.pixelary;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.ScreenUtils;

public class Pixelary extends ApplicationAdapter {
	private SpriteBatch batch;

	@Override
	public void create() { batch = new SpriteBatch(); }

	@Override
	public void render() {
		ScreenUtils.clear(1, 1, 1, 1);
		// batch.begin();
		// batch.end();
	}

	@Override
	public void dispose() { batch.dispose(); }
}
