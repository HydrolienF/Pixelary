package fr.formiko.pixelary;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main(String[] args) {
		if (args.length > 0 && args[0].replaceAll("-", "").equalsIgnoreCase("version")) {
			try {
				InputStream is = new DesktopLauncher().getClass().getClassLoader().getResourceAsStream("version.md");
				String version = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8)).lines()
						.collect(Collectors.joining("\n"));
				System.out.println(version);
				System.exit(0);
			} catch (Exception e) {
				System.out.println("Fail to get version in DesktopLauncher.");
			}
		}
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.setForegroundFPS(60);
		config.setTitle("Pixelary");
		// config.setFullscreenMode(Lwjgl3ApplicationConfiguration.getDisplayMode());
		config.setMaximized(true);
		config.setWindowIcon("images/icon.png");
		// config.setResizable(false);
		Pixelary pixelary = new Pixelary();
		new Lwjgl3Application(pixelary, config);
	}
}