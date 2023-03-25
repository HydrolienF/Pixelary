package fr.formiko.pixelary.tools;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.files.FileHandle;

/**
 * {@summary Tools class to manage music.}
 * It aways play only one music at a time.
 * It have a null safety for every function.
 * 
 * @author Hydrolien
 * @version 0.2
 * @since 0.2
 */
public class Musics {
    private static Music music;
    private static Music dialog;
    private static float musicsVolume = 1f;

    public static float getMusicsVolume() { return musicsVolume; }
    public static void setMusicsVolume(float musicsVolume) { Musics.musicsVolume = musicsVolume; }
    // public static Music getMusic() { return music; }
    /**
     * Set the current music.
     * 
     * @param fileName name of the music file to load from musics/. It must be a mp3 file.
     */
    public static void setMusic(String fileName) {
        System.out.println("Set music to " + fileName);
        stop();
        dispose();
        FileHandle file = Gdx.files.internal("musics/" + fileName + ".mp3");
        if (file.exists()) {
            music = Gdx.audio.newMusic(file);
            music.setVolume(musicsVolume);
            System.out.println("Current music : " + fileName);
        } else {
            music = null;
            System.out.println("Music file not found : " + fileName);
        }
    }
    /**
     * Play the current music.
     */
    public static void play() {
        stop();
        if (music != null) {
            music.play();
        }
    }
    /**
     * Resume the current music.
     */
    public static void resume() {
        if (music != null) {
            System.out.println("Play current music");
            music.play();
        }
    }
    /**
     * Set the current music and play it.
     * 
     * @param fileName name of the music file to load from musics/. It must be a mp3 file.
     */
    public static void play(String fileName) {
        setMusic(fileName);
        play();
    }
    /**
     * Pause the current music.
     */
    public static void pause() {
        if (music != null) {
            music.pause();
        }
    }
    /**
     * Stop the current music.
     */
    public static void stop() {
        if (music != null) {
            music.stop();
        }
    }
    /**
     * Dispose the current music.
     */
    public static void dispose() {
        if (music != null) {
            music.dispose();
        }
    }
    /**
     * Set the volume of the current music.
     * 
     * @param volume volume of the music. It must be between 0 and 1.
     */
    public static void setVolume(float volume) {
        if (music != null) {
            music.setVolume(volume * musicsVolume);
        }
    }
    /**
     * Set if the current music is looping.
     * 
     * @param isLooping true if the music is looping.
     */
    public static void setLooping(boolean isLooping) {
        if (music != null) {
            music.setLooping(isLooping);
        }
    }

    /**
     * Play the levels musics.
     */
    public static void playLevelMusic(int levelId) {
        play("lvl" + levelId);
        setVolume(0.4f);
        setLooping(true);
    }

    public static void setMusicDialog(String fileName) {
        System.out.println("Set dialog to " + fileName);
        stopDialog();
        FileHandle file = Gdx.files.internal("sounds/" + fileName + ".mp3");
        if (file.exists()) {
            dialog = Gdx.audio.newMusic(file);
            dialog.setVolume(musicsVolume);
            System.out.println("Current dialog : " + fileName);
        } else {
            dialog = null;
            System.out.println("dialog file not found : " + fileName);
        }
    }
    /**
     * Stop the current dialog.
     */
    public static void stopDialog() {
        if (dialog != null) {
            dialog.stop();
        }
    }
    /**
     * Play the current dialog.
     */
    public static void playDialog() {
        stopDialog();
        if (dialog != null) {
            dialog.play();
        }
    }

    public static void playDialog(String fileName) {
        setMusicDialog(fileName);
        playDialog();
    }
}
