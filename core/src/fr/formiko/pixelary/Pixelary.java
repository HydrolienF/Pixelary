package fr.formiko.pixelary;

import fr.formiko.pixelary.tools.Shapes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import space.earlygrey.shapedrawer.ShapeDrawer;


public class Pixelary extends ApplicationAdapter {
	private SpriteBatch batch;
	private List<PixmapActor> pixmapActors;
	private List<PixmapActor> paletteActors;
	public static ShapeDrawer shapeDrawer;
	private Stage stage;
	private int w;
	private int h;
	private Viewport viewport;
	private InputMultiplexer inputMultiplexer;
	private Label.LabelStyle labelStyle;

	@Override
	public void create() {
		viewport = new FitViewport(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), new OrthographicCamera());
		batch = new SpriteBatch();
		w = Gdx.graphics.getWidth();
		h = Gdx.graphics.getHeight();
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		stage = new Stage(viewport, batch);
		inputMultiplexer.addProcessor(stage);

		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawPixel(0, 0);
		Texture texture = new Texture(pixmap); // remember to dispose of later
		pixmap.dispose();
		TextureRegion region = new TextureRegion(texture, 0, 0, 1, 1);
		shapeDrawer = new ShapeDrawer(batch, region);

		BitmapFont bmf = new BitmapFont(Gdx.files.internal("fonts/dominican.fnt"));
		labelStyle = new Label.LabelStyle(bmf, Color.BLACK);

		startNewLevel(1);


	}

	@Override
	public void render() {
		ScreenUtils.clear(0.9f, 0.9f, 0.9f, 1);
		batch.begin();
		shapeDrawer.setColor(Color.BLACK);
		shapeDrawer.line(w / 3, 0, w / 3, h);
		shapeDrawer.line(w * 2 / 3, 0, w * 2 / 3, h);
		batch.end();
		stage.act();
		stage.draw();
	}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
	}

	public void startNewLevel(int levelId) {
		stage.clear();

		createPixmapActors(levelId);

		createPalettes();

		createLabels(levelId);

		Player.AI.setColor(Color.WHITE);
		Player.HUMAN.setColor(Color.WHITE);
		// stage.setDebugAll(true);
	}

	/** Create the 3 drawing pixmap. */
	private void createPixmapActors(int levelId) {
		Texture t = new Texture(Gdx.files.internal("images/levels/" + levelId + ".png"));
		Pixmap tmp = Shapes.textureToPixmap(t);
		Pixmap modelPixmap = Shapes.createWhitePixmap(tmp.getWidth(), tmp.getHeight());
		modelPixmap.drawPixmap(tmp, 0, 0);
		Pixmap userPixmap = Shapes.createWhitePixmap(tmp.getWidth(), tmp.getHeight());
		Pixmap aiPixmap = Shapes.createWhitePixmap(tmp.getWidth(), tmp.getHeight());

		pixmapActors = new ArrayList<PixmapActor>();
		pixmapActors.add(new PixmapActor(aiPixmap));
		pixmapActors.add(new PixmapActor(modelPixmap));
		pixmapActors.add(new PixmapActor(userPixmap));

		int k = 0;
		for (PixmapActor pixmapActor : pixmapActors) {
			float margin = 0.1f;
			int marginPixel = (int) (w * margin / 3);
			pixmapActor.setSize((w / 3) - 2 * marginPixel, h - 2 * marginPixel);
			pixmapActor.setX((w / 3 * k) + marginPixel);
			pixmapActor.setY(h - (2 * marginPixel) - pixmapActor.getHeight());
			k++;
			stage.addActor(pixmapActor);
		}
	}

	/** Create 2 palette with the color of the model pixmap. */
	private void createPalettes() {
		Pixmap modelPixmap = pixmapActors.get(1).getPixmap();
		paletteActors = new ArrayList<PixmapActor>();
		Set<Integer> colors = new HashSet<Integer>();
		for (int i = 0; i < modelPixmap.getWidth(); i++) {
			for (int j = 0; j < modelPixmap.getHeight(); j++) {
				colors.add(modelPixmap.getPixel(i, j));
			}
		}
		Pixmap paletteHuman = Shapes.createWhitePixmap(colors.size(), 1);
		Pixmap paletteAI = Shapes.createWhitePixmap(colors.size(), 1);
		int k = 0;
		for (Integer color : colors) {
			paletteHuman.drawPixel(k, 0, color);
			paletteAI.drawPixel(k, 0, color);
			k++;
		}
		paletteActors.add(new PixmapActor(paletteAI, true));
		paletteActors.add(new PixmapActor(paletteHuman, true));

		k = 0;
		float pixelSize = pixmapActors.get(0).getPixelSize();
		for (PixmapActor pixmapActor : paletteActors) {
			float margin = 0.1f;
			int marginPixel = (int) (w * margin / 3);
			pixmapActor.setSize(pixelSize * pixmapActor.getPixmap().getWidth(), pixelSize * pixmapActor.getPixmap().getHeight());
			pixmapActor.setCenterX((w / 3 * k + w / 6));
			pixmapActor.setY(marginPixel);
			k += 2;
			stage.addActor(pixmapActor);
		}
	}
	public void createLabels(int levelId) {
		List<String> labelNames = new ArrayList<String>();
		labelNames.add("AI");
		labelNames.add("Level " + levelId);
		labelNames.add("You");

		int k = 0;
		for (String labelName : labelNames) {
			float margin = 0.1f;
			int marginPixel = (int) (w * margin / 3);
			Label label = new Label(labelName, labelStyle);
			label.setAlignment(Align.center);
			label.setSize(w / 3, marginPixel);
			// label.setCenterX((w / 3 * k + w / 6));
			label.setX(w * k / 3);
			label.setY(h - marginPixel);
			k++;
			stage.addActor(label);
		}
	}
}
