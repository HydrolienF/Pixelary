package fr.formiko.pixelary;

import fr.formiko.pixelary.tools.Shapes;
import java.util.ArrayList;
import java.util.List;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
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

		Texture t = new Texture(Gdx.files.internal("images/levels/" + levelId + ".png"));
		Pixmap tmp = Shapes.textureToPixmap(t);
		Pixmap modelPixmap = Shapes.createWhitePixmap(tmp.getWidth(), tmp.getHeight());
		modelPixmap.drawPixmap(tmp, 0, 0);
		Pixmap userPixmap = Shapes.createWhitePixmap(tmp.getWidth(), tmp.getHeight());
		Pixmap aiPixmap = Shapes.createWhitePixmap(tmp.getWidth(), tmp.getHeight());

		// pixmapActors = List.of(new PixmapActor(aiPixmap), new PixmapActor(modelPixmap), new PixmapActor(userPixmap)); // html
		// incompatible
		pixmapActors = new ArrayList<PixmapActor>();
		pixmapActors.add(PixmapActor.newPixmapActor(aiPixmap));
		pixmapActors.add(PixmapActor.newPixmapActor(modelPixmap));
		pixmapActors.add(PixmapActor.newPixmapActor(userPixmap));

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

		paletteActors = new ArrayList<PixmapActor>();
		// TODO create 2 palettes from colors of modelPixmap

		Player.AI.setColor(Color.WHITE);
		Player.HUMAN.setColor(Color.WHITE);
		// stage.setDebugAll(true);

		Image replayButton = new Image(new Texture(Gdx.files.internal("images/icons/basic/replay.png")));
		replayButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) { System.out.println("replay"); }
		});
		replayButton.setSize(50, 50);
		stage.addActor(replayButton);
	}
}
