package com.mygdx.re_action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by dylan on 05/02/17.
 */
public class Player {

    int health;
    Vector3 velocity;
    PerspectiveCamera cam;
    FPSController fpsController;
    ModelInstance ml;


    public Player(){
        health = 10;
        velocity = new Vector3(0, 0, 0);

        //create player camera
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.far = 1200;

        fpsController = new FPSController(cam);

        //TODO may not need
        fpsController.setSpeedMultiplier(10f);
    }
}
