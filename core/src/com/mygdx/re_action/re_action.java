package com.mygdx.re_action;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.UBJsonReader;

import java.io.InputStream;
import java.util.ArrayList;

public class re_action extends ApplicationAdapter {

	//player entities
	private Player player;

	//world entities
	Environment env;

	//objects
	private ModelBatch mb;

	private ArrayList<Actor> actors;

	private Actor background;

	private Model mBgCube;
	private ModelInstance iBgCube;

	//lighting
	private DirectionalLight dl;
	
	@Override
	public void create () {
		//delete mouse cursor
		Gdx.input.setCursorCatched(true);

		//initialize player entities
		player = new Player(1f, 100f, Actor.CUBE, null);

		//initialize background
		background = new Actor(0, 800f, Actor.CUSTOM, "invertcubescaled.g3db");

		//initialize lighting
		dl = new DirectionalLight();
		dl.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);

		//initialize world entities
		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		env.add(dl);

		mb = new ModelBatch();

		//set player input control
		Gdx.input.setInputProcessor(player.fpsController);

		//first camera update
		player.cam.update();

	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		mb.begin(player.cam);
		mb.render(background.getModelInstance(), env);
		mb.render(player.getModelInstance(), env);
		mb.end();

		player.fpsController.update();
		player.cam.update();

		//check background cube collision
		Vector3 collisionPoint = player.isCollidingCubeCube (background);
		if(collisionPoint != null){
			Physics physics = new Physics();
			physics.staticCollision(player.modelInstance, iBgCube, collisionPoint);
		}

		//check other cube collision

	}
	
	@Override
	public void dispose () {
	}
}
