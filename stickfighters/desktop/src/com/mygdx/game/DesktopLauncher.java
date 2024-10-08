package com.mygdx.game;

import com.badlogic.gdx.backends.lwjgl3.Lwjgl3Application;
import com.badlogic.gdx.backends.lwjgl3.Lwjgl3ApplicationConfiguration;

// Please note that on macOS your application needs to be started with the -XstartOnFirstThread JVM argument
public class DesktopLauncher {
	public static void main (String[] arg) {
		Lwjgl3ApplicationConfiguration config = new Lwjgl3ApplicationConfiguration();
		config.useVsync(false);
		config.setForegroundFPS(120);
		config.setTitle("stickfighters");
		config.setResizable(false);
		config.setWindowedMode((int) Main.WIDTH, (int) Main.HEIGHT);

		new Lwjgl3Application(new Main(), config);
	}
}
