package fr.formiko.pixelary;

import fr.formiko.pixelary.tools.Assets;
import fr.formiko.pixelary.tools.Shapes;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.esotericsoftware.spine.AnimationState;
import com.esotericsoftware.spine.AnimationStateData;
import com.esotericsoftware.spine.Skeleton;
import com.esotericsoftware.spine.SkeletonRenderer;
import com.github.tommyettinger.textra.TypingLabel;
import space.earlygrey.shapedrawer.ShapeDrawer;


public class Pixelary extends ApplicationAdapter {
	private SpriteBatch batch;
	private List<PixmapActor> pixmapActors;
	private List<PixmapActor> paletteActors;
	private List<Label> labels;
	public static ShapeDrawer shapeDrawer;
	private Stage stage;
	private int w;
	private int h;
	private Camera camera;
	private Viewport viewport;
	private InputMultiplexer inputMultiplexer;
	private Label.LabelStyle labelStyle;
	private int fontSize = 55;
	private Color clearColor;
	private int currentLevel;
	private Assets assets;

	@Override
	public void create() {
		camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		batch = new SpriteBatch();
		assets = new Assets();
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
		ScreenUtils.clear(clearColor);
		batch.begin();
		if (pixmapActors != null) {
			shapeDrawer.setColor(Color.BLACK);
			shapeDrawer.line(w / 3, 0, w / 3, h);
			shapeDrawer.line(w * 2 / 3, 0, w * 2 / 3, h);
		}
		batch.end();

		updatePercent();

		stage.act();
		stage.draw();

	}

	@Override
	public void dispose() {
		batch.dispose();
		stage.dispose();
	}
	public void disposeLevel() {
		pixmapActors = null;
		paletteActors = null;
		labels = null;
	}

	public void startNewLevel(int levelId) {
		currentLevel = levelId;
		stage.clear();

		switch (levelId) {
		case 1:
			clearColor = new Color(0.9f, 0.9f, 0.9f, 1);
			break;
		case 2:
			clearColor = new Color(0.75f, 0.7f, 0.7f, 1);
			break;
		case 3:
			clearColor = new Color(0.6f, 0f, 0f, 1);
			break;
		}

		createPixmapActors(levelId);

		createPalettes();

		createLabels(levelId);

		createPens();

		Player.AI.setColor(Color.WHITE);
		Player.HUMAN.setColor(Color.WHITE);
		// stage.setDebugAll(true);

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
	}

	@Override
	public void resize(int width, int height) {
		System.out.println("resize to " + width + "x" + height);
		viewport.update(width, height, true);
		w = width;
		h = height;
		float margin = 0.1f;
		int marginPixel = (int) (w * margin / 3);

		int k = 0;
		// label
		int j = 0;
		for (Label label : labels) {
			label.setSize(w / 3, fontSize);
			label.setX(w * k / 3);
			if (j == 0) {
				label.setY(h - label.getHeight());
				j++;
			} else {
				label.setY(h - label.getHeight() * 2);
				k++;
				j = 0;
			}
		}

		// pixmaps
		k = 0;
		for (PixmapActor pixmapActor : pixmapActors) {
			pixmapActor.setSize((w / 3) - 2 * marginPixel, h - (labels.get(0).getHeight() * 2 + marginPixel));
			pixmapActor.setCenterX((w / 3 * k + w / 6));
			pixmapActor.setY(h - (labels.get(0).getHeight() * 2) - pixmapActor.getHeight());
			k++;
		}

		// palettes
		k = 0;
		float pixelSize = pixmapActors.get(0).getPixelSize();
		for (PixmapActor pixmapActor : paletteActors) {
			pixmapActor.setSize(pixelSize * pixmapActor.getPixmap().getWidth(), pixelSize * pixmapActor.getPixmap().getHeight());
			pixmapActor.setCenterX((w / 3 * k + w / 6));
			pixmapActor.setCenterY(marginPixel / 2);
			k += 2;
		}
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

		for (PixmapActor pixmapActor : pixmapActors) {
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

		for (PixmapActor pixmapActor : paletteActors) {
			stage.addActor(pixmapActor);
		}
	}
	public void createLabels(int levelId) {
		labels = new ArrayList<Label>();
		List<String> labelNames = new ArrayList<String>();
		String levelDraw;
		String adjectif = "";
		switch (levelId) {
		case 1:
			levelDraw = "Heart";
			break;
		case 2:
			levelDraw = "Gold";
			adjectif = "Malicious ";
			break;
		case 3:
			levelDraw = "Pineapple";
			adjectif = "Devilish ";
			break;
		default:
			levelDraw = "";
			break;
		}
		labelNames.add(adjectif + "AI");
		labelNames.add("");
		labelNames.add("Level " + levelId);
		labelNames.add(levelDraw);
		labelNames.add("You");
		labelNames.add("");

		for (String labelName : labelNames) {
			Label label = new Label(labelName, labelStyle);
			label.setAlignment(Align.center);
			labels.add(label);
			stage.addActor(label);
		}
	}

	public void createPens() {
		SkeletonRenderer skeletonRenderer = new SkeletonRenderer();
		skeletonRenderer.setPremultipliedAlpha(true);
		String textureName = "pen";

		for (int i = 0; i < 2; i++) {
			Skeleton skeleton = new Skeleton(assets.getSkeletonData(textureName));
			AnimationStateData stateData = new AnimationStateData(assets.getSkeletonData(textureName));
			AnimationState animationState = new AnimationState(stateData);

			Pen skeletonActor = new Pen();
			skeletonActor.setRenderer(skeletonRenderer);
			skeletonActor.setSkeleton(skeleton);
			skeletonActor.setAnimationState(animationState);

			skeletonActor.setScale(0.2f, 0.2f);
			skeletonActor.setRotation(135);

			if (i == 0) {
				Player.AI.setPen(skeletonActor);
			} else {
				Player.HUMAN.setPen(skeletonActor);
			}
			stage.addActor(skeletonActor);
		}
	}

	public double diff(Pixmap pixmap1, Pixmap pixmap2) {
		int diff = 0;
		for (int i = 0; i < pixmap1.getWidth(); i++) {
			for (int j = 0; j < pixmap1.getHeight(); j++) {
				if (pixmap1.getPixel(i, j) != pixmap2.getPixel(i, j)) {
					diff++;
				}
			}
		}
		return (double) (diff) / (pixmap1.getWidth() * pixmap1.getHeight());
	}

	public void updatePercent() {
		if (pixmapActors == null) {
			return;
		}
		double scoreAI = 1 - diff(pixmapActors.get(1).getPixmap(), pixmapActors.get(0).getPixmap());
		double scorePlayer = 1 - diff(pixmapActors.get(1).getPixmap(), pixmapActors.get(2).getPixmap());
		labels.get(1).setText((int) (100 * scoreAI) + "%");
		labels.get(5).setText((int) (100 * scorePlayer) + "%");
		if (scorePlayer == 1) {
			endGame(true);
		} else if (scoreAI == 1) {
			endGame(false);
		}
	}
	public void endGame(final boolean win) {
		stage.clear();
		disposeLevel();
		String text;
		if (win) {
			text = "You Win!";
			if (currentLevel == 3) {
				text += "\n thanks for playing!";
			} else {
				text += "\n Click HERE to play next level";
			}
			// TODO play win music
		} else {
			text = "You Lose!\n Click HERE to retry";
			// TODO play lose music
		}
		TypingLabel label = new TypingLabel(text, labelStyle);
		label.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				if (win) {
					startNewLevel(currentLevel + 1);
				} else {
					startNewLevel(currentLevel);
				}
			}
		});

		label.setAlignment(Align.center);
		label.setSize(label.getPrefWidth(), fontSize * 2);
		label.setY(h - 2 * fontSize);
		label.setX(Gdx.graphics.getWidth() / 2 - label.getWidth() / 2);
		stage.addActor(label);
	}
}
