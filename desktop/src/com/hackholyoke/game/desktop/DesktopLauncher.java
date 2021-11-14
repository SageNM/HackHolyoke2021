package com.hackholyoke.game.desktop;

import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.hackholyoke.game.HackHolyoke;

public class DesktopLauncher {
	public static void main (String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.height = 793 ;
		config.width = 928;
		new LwjglApplication(new HackHolyoke(), config);
	}
}
