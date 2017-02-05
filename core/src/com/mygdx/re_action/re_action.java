package com.mygdx.re_action;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.loaders.ModelLoader;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g3d.*;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.loader.G3dModelLoader;
import com.badlogic.gdx.graphics.g3d.model.data.ModelData;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.graphics.g3d.utils.TextureProvider;
import com.badlogic.gdx.utils.BaseJsonReader;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.UBJsonReader;

import java.io.InputStream;

public class re_action extends ApplicationAdapter {

	//player entities
	private Player player;

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
		player = new Player();

		//TODO remove
		player.cam.position.set(10f, 10f, 10f);
		player.cam.lookAt(0f, 0f, 0f);

		//initialize lighting
		dl = new DirectionalLight();
		dl.set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f);

		//initialize world entities
		env = new Environment();
		env.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		env.add(dl);

		mb = new ModelBatch();

		//ModelBuilder mblr = new ModelBuilder();

		G3dModelLoader ml = new G3dModelLoader(new UBJsonReader());

		//ModelData md = ml.loadModelData(Gdx.files.internal("invertcube.g3db"));

		mBgCube = ml.loadModel(Gdx.files.internal("invertcube.g3db"));

		/*mBgCube = mblr.createBox(5, 5, 5,
				new Material(ColorAttribute.createDiffuse(Color.GREEN)),
				VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);*/

		iBgCube = new ModelInstance(mBgCube);

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
		mb.render(iBgCube, env);
		mb.end();

		player.fpsController.update();
		player.cam.update();

		//TODO set player spirte to follow camera
	}
	
	@Override
	public void dispose () {
	}
}
