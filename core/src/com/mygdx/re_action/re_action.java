package com.mygdx.re_action;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import javafx.scene.AmbientLight;

public class re_action extends ApplicationAdapter {

	//player entities
	private PerspectiveCamera playerCam;
	private FPSController fpsPlayerCam;

	//world entities
	Environment env;

	//objects
	private ModelBatch mb;
	private Model mBgCube;
	private ModelInstance iBgCube;

	//lighting
	private DirectionalLight dl;
	
	@Override
	public void create () {
		//delete mouse cursor
		Gdx.input.setCursorCatched(true);

		//initialize player entities
		playerCam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		playerCam.far = 1000;

		fpsPlayerCam = new FPSController(playerCam);
		fpsPlayerCam.setVelocity(100f);

		//TODO remove
		playerCam.position.set(10f, 10f, 10f);
		playerCam.lookAt(0f, 0f, 0f);

		//initialize lighting
		dl = new DirectionalLight();
		dl.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);

		//initialize world entities
		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		env.add(dl);

		mb = new ModelBatch();

		ModelBuilder mblr = new ModelBuilder();

		mBgCube = mblr.createBox(5, 5, 5,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

		iBgCube = new ModelInstance(mBgCube);

		//set player input control
		Gdx.input.setInputProcessor(fpsPlayerCam);

		//first camera update
		playerCam.update();
	}

	@Override
	public void render () {
		Gdx.gl.glViewport(0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		mb.begin(playerCam);
		mb.render(iBgCube, env);
		mb.end();

		fpsPlayerCam.update();
		playerCam.update();

		//TODO set player spirte to follow camera
	}
	
	@Override
	public void dispose () {
	}
}
