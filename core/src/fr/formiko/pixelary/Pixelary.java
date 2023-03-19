package fr.formiko.pixelary;

import fr.formiko.pixelary.tools.Shapes;
import java.util.List;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.ScreenUtils;
import space.earlygrey.shapedrawer.ShapeDrawer;


public class Pixelary extends ApplicationAdapter {
	private SpriteBatch batch;
	private Pixmap modelPixmap;
	private Pixmap userPixmap;
	private Pixmap aiPixmap;
	private List<PixmapActor> pixmapActors;
	public static ShapeDrawer shapeDrawer;
	private Stage stage;
	private int w;
	private int h;

	@Override
	public void create() {
		batch = new SpriteBatch();
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();


		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawPixel(0, 0);
		Texture texture = new Texture(pixmap); // remember to dispose of later
		pixmap.dispose();
		TextureRegion region = new TextureRegion(texture, 0, 0, 1, 1);
		shapeDrawer = new ShapeDrawer(batch, region);

		startNewLevel(1);

	}

	@Override
	public void render() {
		ScreenUtils.clear(1, 1, 1, 1);
		batch.begin();
		shapeDrawer.setColor(Color.BLACK);
		shapeDrawer.line(w / 3, 0, w / 3, h);
		shapeDrawer.line(w * 2 / 3, 0, w * 2 / 3, h);

		// float space = 0.8f;
		// int pixelSize = (int) Math.min(w * space / (3 * modelPixmap.getWidth()), h * space / modelPixmap.getHeight());
		// int freeSpace = (int) (w - 3 * pixelSize * modelPixmap.getWidth());
		// drawPixmap(aiPixmap, freeSpace, 0, w / 3, h, pixelSize);
		// drawPixmap(modelPixmap, w / 3, 0, w / 3, h, pixelSize);
		// drawPixmap(userPixmap, w * 2 / 3, 0, w / 3, h, pixelSize);

		stage.act();
		stage.draw();

		batch.end();
	}

	@Override
	public void dispose() { batch.dispose(); }

	// public void drawPixmap(Pixmap pixmap, int x, int y, int w, int h, int pixelSize) {
	// if (pixmap == null)
	// return;
	// for (int i = 0; i < pixmap.getWidth(); i++) {
	// for (int j = 0; j < pixmap.getHeight(); j++) {
	// int color = pixmap.getPixel(i, j);
	// shapeDrawer.setColor(new Color(color));
	// shapeDrawer.filledRectangle(x + i * pixelSize, y + h - j * pixelSize, pixelSize, pixelSize);
	// shapeDrawer.setColor(Color.BLACK);
	// shapeDrawer.rectangle(x + i * pixelSize, y + h - j * pixelSize, pixelSize, pixelSize);
	// }
	// }
	// }

	public void startNewLevel(int levelId) {
		Texture t = new Texture(Gdx.files.internal("images/levels/" + levelId + ".png"));
		modelPixmap = Shapes.textureToPixmap(t);
		userPixmap = new Pixmap(t.getWidth(), t.getHeight(), Format.RGBA8888);
		aiPixmap = new Pixmap(t.getWidth(), t.getHeight(), Format.RGBA8888);

		pixmapActors = List.of(new PixmapActor(aiPixmap), new PixmapActor(modelPixmap), new PixmapActor(userPixmap));

		stage = new Stage();
		// stage.setDebugAll(true);

		int k = 0;
		for (PixmapActor pixmapActor : pixmapActors) {
			pixmapActor.setBounds(k * w / 3, 0, w / 3, h);
			k++;
			stage.addActor(pixmapActor);
		}
	}
}
