package com.hackholyoke.game;

import com.badlogic.gdx.Game;

public class HackHolyoke extends Game {
	private Model model;

	@Override
	public void create () {
		model = new Model();
		setScreen(model);
	}

	@Override
	public void render () {
		super.render();
	}

	@Override
	public void dispose () {
		model.dispose();
	}

	@Override
	public void resize(int width, int height) {
		model.resize(width, height);
	}
}
