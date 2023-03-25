package fr.formiko.pixelary;

import fr.formiko.pixelary.tools.Assets;
import fr.formiko.pixelary.tools.Musics;
import fr.formiko.pixelary.tools.Shapes;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputMultiplexer;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Pixmap.Format;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
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
import space.earlygrey.shapedrawer.ShapeDrawer;


public class Pixelary extends ApplicationAdapter {
	private SpriteBatch batch;
	private static List<PixmapActor> pixmapActors;
	private static List<PixmapActor> paletteActors;
	private static List<Label> labels;
	public static ShapeDrawer shapeDrawer;
	private Image helpButton;
	private Stage stage;
	private int w;
	private int h;
	private Camera camera;
	private Viewport viewport;
	private InputMultiplexer inputMultiplexer;
	public static Label.LabelStyle labelStyle;
	private int fontSize = 55;
	private Color clearColor;
	static int currentLevel;
	private Assets assets;
	private static Vector2 aiTarget;
	public static double scoreAI;
	public static double scorePlayer;
	public static Random random = new Random();
	public static TextScreen textScreen;
	private static Map<String, Sound> soundMap = new HashMap<String, Sound>();


	public Pixelary() {}

	public static PixmapActor getModelPixmap() { return pixmapActors.get(1); }
	public static PixmapActor getAIPixmap() { return pixmapActors.get(0); }
	public static PixmapActor getPlayerPixmap() { return pixmapActors.get(2); }
	public static PixmapActor getAIPalette() { return paletteActors.get(0); }
	public static float getPixelSize() { return getAIPixmap().getPixelSize(); }

	@Override
	public void create() {
		camera = new OrthographicCamera();
		viewport = new ScreenViewport(camera);
		batch = new SpriteBatch();
		assets = new Assets();
		inputMultiplexer = new InputMultiplexer();
		Gdx.input.setInputProcessor(inputMultiplexer);
		stage = new Stage(viewport, batch);
		// stage.setDebugAll(true); // @a
		inputMultiplexer.addProcessor(stage);
		inputMultiplexer.addProcessor(getInputProcessor());

		Pixmap pixmap = new Pixmap(1, 1, Format.RGBA8888);
		pixmap.setColor(Color.WHITE);
		pixmap.drawPixel(0, 0);
		Texture texture = new Texture(pixmap);
		pixmap.dispose();
		TextureRegion region = new TextureRegion(texture, 0, 0, 1, 1);
		shapeDrawer = new ShapeDrawer(batch, region);

		BitmapFont bmf = new BitmapFont(Gdx.files.internal("fonts/dominican.fnt"));
		labelStyle = new Label.LabelStyle(bmf, Color.BLACK);

		helpButton = new Image(new Texture(Gdx.files.internal("images/icons/basic/help.png")));
		helpButton.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) { getPlayerPixmap().switchDisplayHelp(); }
		});

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

		if (Player.SPEED > 0) {
			updatePercent();
		}

		stage.act();
		stage.draw();

		// batch.begin();
		// // show AI behavior
		// if (aiTarget != null) {
		// shapeDrawer.setColor(Color.BLUE);
		// shapeDrawer.filledCircle(aiTarget.x, aiTarget.y, 10);
		// }
		// batch.end();

	}

	public static void setSpot(Vector2 v) { aiTarget = v; }

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
		Player.AI.nextClickPosition = null;
		Player.AI.playOnModel = false;
		Player.AI.actions = new LinkedList<Action>();

		Player.SPEED = 0f;
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

		stage.addActor(helpButton);

		createPens();

		Player.AI.setColor(Color.WHITE);
		Player.HUMAN.setColor(Color.WHITE);
		// stage.setDebugAll(true);

		resize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

		Musics.playLevelMusic(levelId);

		displayBeforeLevelText(levelId);
	}

	public void displayBeforeLevelText(int levelId) {
		String text = "";
		switch (levelId) {
		case 1:
			text = "Welcome player, I'm Frenchzebutt, your friend to play Pixelary !\nDéjà vue fealing ? Many people think I'm my twin Beelzebot.\n\nAim is to reproduce the model as fast as possible ! The first one to do it wins !\nBut there is no way you will win it, anyway...";
			break;
		case 2:
			text = "Now were competing for gold !\nDéjà vue fealing ? It's from my favorite game.";;
			break;
		case 3:
			text = "LET'S THE CURSED PINEAPPLE DETERMINE THE WINNER !";
			break;
		}
		long soundId = playSound("b" + levelId);
		text += "\n[70%](Click anywere to start)[%]";
		textScreen = new TextScreen(text, new Color(1, 1, 1, 0.9f));
		textScreen.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				startLevel(levelId);
				stopSound("b" + levelId, soundId);
			}
		});
		stage.addActor(textScreen);
	}
	private void startLevel(int levelId) {
		switch (levelId) {
		case 1:
			Player.SPEED = 150;
			break;
		case 2:
			Player.SPEED = 300;
			break;
		case 3:
			Player.SPEED = 100;
			break;
		}
		textScreen.remove();

		switch (levelId) {
		case 2:
			Player.AI.actions.add(new Action(0.4f, Action.Type.ONE_PIXEL, 3, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.5f, Action.Type.ONE_PIXEL, 5, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.55f, Action.Type.ONE_LINE, 15, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.56f, Action.Type.ONE_PIXEL, 5, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.70f, Action.Type.ONE_LINE, 5, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.72f, Action.Type.ONE_PIXEL, 5, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.89f, Action.Type.ONE_PIXEL, 5, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.95f, Action.Type.ONE_PIXEL, 10, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.97f, Action.Type.ONE_LINE, 20, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.99f, Action.Type.ONE_PIXEL, 5, getPlayerPixmap()));
			break;
		case 3:
			Player.AI.actions.add(new Action(0.4f, Action.Type.ONE_PIXEL, 10, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.45f, Action.Type.ONE_PIXEL, 5, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.47f, Action.Type.ONE_LINE, 15, getPlayerPixmap()));
			Player.AI.actions.add(new Action(0.89f, Action.Type.PLAY_ON_MODEL, 1, null));
			break;
		}
	}

	@Override
	public void resize(int width, int height) {
		Gdx.app.log("", "resize to " + width + "x" + height);
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
		float pixelSize = getPixelSize();
		for (PixmapActor pixmapActor : paletteActors) {
			pixmapActor.setSize(pixelSize * pixmapActor.getPixmap().getWidth(), pixelSize * pixmapActor.getPixmap().getHeight());
			pixmapActor.setCenterX((w / 3 * k + w / 6));
			pixmapActor.setCenterY(marginPixel);
			k += 2;
		}

		helpButton.setSize(48, 48);
		helpButton.setPosition(w - helpButton.getWidth(), h - helpButton.getHeight());

		if (textScreen != null) {
			textScreen.setSize(w, h);
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

		if (levelId == 1) {
			pixmapActors.get(0).setEditableByPlayer(false);
			pixmapActors.get(1).setEditableByPlayer(false);
		}
		// if (levelId == 2) {
		// pixmapActors.get(1).setEditableByPlayer(false);
		// }

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
			Color color2 = new Color(color);
			System.out.println("color " + color2.r * 255 + " " + color2.g * 255 + " " + color2.b * 255);
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
		labelNames.add(adjectif + "Frenchzebutt");
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
				skeleton.setSkin("bot");
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
		scoreAI = 1 - diff(pixmapActors.get(1).getPixmap(), pixmapActors.get(0).getPixmap());
		scorePlayer = 1 - diff(pixmapActors.get(1).getPixmap(), pixmapActors.get(2).getPixmap());
		labels.get(1).setText((int) (100 * scoreAI) + "%");
		labels.get(5).setText((int) (100 * scorePlayer) + "%");
		if (scorePlayer == 1) {
			endGame(true);
		} else if (scoreAI == 1) {
			endGame(false);
		}
	}

	public void endGame(final boolean win) {
		Player.SPEED = 0;
		Musics.stop();
		String text;
		final long soundId;
		if (win) {
			switch (currentLevel) {
			case 1:
				text = "Rrrrraaa ! How can it be ?!\n No one have ever beat Frenchzebutt !\nI won't play fair anymore ! I will use my secret weapon !";;
				break;
			case 2:
				text = "{SHRINK}I'M FRENCHZEBUTT SON OF BEELZEBIT & FRENCHZEBETTE, BROTHER OF BEELZEBOT !\nNO ONE HAVE EVER SURVIVE AFTER BRAVE ME !{ENDSHRINK}";
				break;
			case 3:
				text = "The cursed pineapple have choose,\nIt's time for me to give up.\n\nBut I will be back in next libgdx jam !\nCheck hydrolien's game for more.";;
				break;
			default:
				text = "You Win!";
				break;
			}
			text += "\n[70%](Click anywere to play next level)[%]";
			// TODO play win music
			soundId = playSound("a" + currentLevel);
		} else {
			text = "You Lose!\n Click anywere to retry";
			soundId = playSound("hahaha");
			// TODO play lose music
		}
		textScreen = new TextScreen(text, new Color(1, 1, 1, 0.9f));
		textScreen.addListener(new ClickListener() {
			@Override
			public void clicked(InputEvent event, float x, float y) {
				Musics.stop();
				stopSound("a" + currentLevel, soundId);
				if (win) {
					startNewLevel(currentLevel + 1);
				} else {
					startNewLevel(currentLevel);
				}
			}
		});

		stage.addActor(textScreen);
	}

	/**
	 * @summary click on overed actor.
	 * @param isAI
	 * @param x
	 * @param y
	 */
	public static void clickFromScreenCoo(boolean isAI, int x, int y) {
		for (PixmapActor actor : pixmapActors) {
			if (actor.containsCoo(x, y)) {
				actor.clickFromScreenCoo(isAI, x, y);
				return;
			}
		}
		for (PixmapActor actor : paletteActors) {
			if (actor.containsCoo(x, y)) {
				actor.clickFromScreenCoo(isAI, x, y);
				return;
			}
		}
	}

	/**
	 * {@summary Handle user input.}<br>
	 */
	private InputProcessor getInputProcessor() {
		InputProcessor inputProcessor = (InputProcessor) new InputProcessor() {

			@Override
			public boolean keyDown(int keycode) { return false; }
			/**
			 * {@summary React to key pressed once.}
			 * 
			 * @param keycode the key pressed
			 */
			@Override
			public boolean keyUp(int keycode) {
				if (keycode == Input.Keys.W) {
					endGame(true);
				}
				return true;
			}

			@Override
			public boolean keyTyped(char character) { return false; }

			@Override
			public boolean touchDown(int screenX, int screenY, int pointer, int button) { return false; }

			@Override
			public boolean touchUp(int screenX, int screenY, int pointer, int button) { return false; }

			@Override
			public boolean touchDragged(int screenX, int screenY, int pointer) { return false; }

			@Override
			public boolean mouseMoved(int screenX, int screenY) { return false; }

			@Override
			public boolean scrolled(float amountX, float amountY) { return false; }

		};
		return inputProcessor;
	}

	/**
	 * {@summary Play the given sound.}
	 * Sound can be play many times &#38; at same time.
	 * 
	 * @param fileName name of the sound file
	 * @param volume   volume of the sound in [0, 1]
	 * @param pan      left rigth ballance of the sound file in [-1, 1]
	 */
	public static long playSound(String fileName, float volume, float pan) {
		if (soundMap.get(fileName) == null) {
			soundMap.put(fileName, Gdx.audio.newSound(Gdx.files.internal("sounds/" + fileName + ".mp3")));
		}
		return soundMap.get(fileName).play(volume, 1f, pan);
	}
	/**
	 * {@summary Play the given sound with default volume &#38; default pan.}
	 * Sound can be play many times &#38; at same time.
	 * 
	 * @param fileName name of the sound file
	 */
	public static long playSound(String fileName) { return playSound(fileName, 1f, -0.2f); }

	public static void stopSound(String fileName, long soundId) {
		if (soundMap.get(fileName) == null) {
			soundMap.put(fileName, Gdx.audio.newSound(Gdx.files.internal("sounds/" + fileName + ".mp3")));
		}
		soundMap.get(fileName).stop(soundId);
	}
}

