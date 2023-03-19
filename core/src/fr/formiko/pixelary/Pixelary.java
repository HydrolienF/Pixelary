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
		ScreenUtils.clear(0.9f, 0.9f, 0.9f, 1);
		batch.begin();
		shapeDrawer.setColor(Color.BLACK);
		shapeDrawer.line(w / 3, 0, w / 3, h);
		shapeDrawer.line(w * 2 / 3, 0, w * 2 / 3, h);

		stage.act();
		stage.draw();

		batch.end();
	}

	@Override
	public void dispose() { batch.dispose(); }

	public void startNewLevel(int levelId) {
		Texture t = new Texture(Gdx.files.internal("images/levels/" + levelId + ".png"));
		Pixmap tmp = Shapes.textureToPixmap(t);
		Pixmap modelPixmap = Shapes.createWhitePixmap(tmp.getWidth(), tmp.getHeight());
		modelPixmap.drawPixmap(tmp, 0, 0);
		Pixmap userPixmap = Shapes.createWhitePixmap(tmp.getWidth(), tmp.getHeight());
		Pixmap aiPixmap = Shapes.createWhitePixmap(tmp.getWidth(), tmp.getHeight());

		pixmapActors = List.of(new PixmapActor(aiPixmap), new PixmapActor(modelPixmap), new PixmapActor(userPixmap));

		stage = new Stage();
		// stage.setDebugAll(true);

		int k = 0;
		for (PixmapActor pixmapActor : pixmapActors) {
			float margin = 0.1f;
			int marginPixel = (int) (w * margin / 3);
			pixmapActor.setSize((w / 3) - 2 * marginPixel, h - 2 * marginPixel);
			pixmapActor.setX((w / 3 * k) + marginPixel);
			pixmapActor.setY(h - marginPixel - pixmapActor.getHeight());
			k++;
			stage.addActor(pixmapActor);
		}
	}
}
