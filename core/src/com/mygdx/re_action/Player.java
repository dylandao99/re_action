package com.mygdx.re_action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.math.collision.BoundingBox;
import com.sun.javafx.sg.prism.NGShape;

/**
 * Created by dylan on 05/02/17.
 */
public class Player extends Actor{

    //TODO create getters and setters
    int health;
    PerspectiveCamera cam;
    FPSController fpsController;

    public Player(float mass, float scale, boolean isStatic, int shape){

        super(mass, scale, isStatic, shape);

        health = 10;

        //create player camera
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.far = 1200;

        fpsController = new FPSController(cam, modelInstance);
    }
}
