/*******************************************************************************
 * Copyright 2011 See AUTHORS file.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/

package com.mygdx.re_action;


import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;
import com.sun.org.apache.xpath.internal.operations.Mod;

/** Takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 * @author badlogic */
public class FPSController extends InputAdapter {
    private final Camera camera;
    private final ModelInstance modelInstance;
    private final IntIntMap keys = new IntIntMap();
    private int STRAFE_LEFT = Keys.A;
    private int STRAFE_RIGHT = Keys.D;
    private int FORWARD = Keys.W;
    private int BACKWARD = Keys.S;
    private int STRAFE_UP = Keys.SPACE;
    private int STRAFE_DOWN = Keys.SHIFT_LEFT;
    private int STABILIZE = Keys.CONTROL_LEFT;
    private float speedMultiplier = 10;
    private Vector3 velocity;
    private float degreesPerPixel = 0.5f;
    private Quaternion worldRot;

    private final Vector3 tmp = new Vector3();
    public FPSController (Camera camera, ModelInstance modelInstance) {
        this.camera = camera;
        this.modelInstance = modelInstance;

        velocity = new Vector3(0, 0, 0);
        worldRot = new Quaternion();
    }

    @Override
    public boolean keyDown (int keycode) {
        keys.put(keycode, keycode);
        return true;
    }

    @Override
    public boolean keyUp (int keycode) {
        keys.remove(keycode, 0);
        return true;
    }

    /** Sets the velocity in units per second for moving forward, backward and strafing left/right.
     * @param speedMultiplier the velocity in units per second */

    public void setSpeedMultiplier (float speedMultiplier) {
        this.speedMultiplier = speedMultiplier;
    }

    /** Sets how many degrees to rotate per pixel the mouse moved.
     * @param degreesPerPixel */
    public void setDegreesPerPixel (float degreesPerPixel) {
        this.degreesPerPixel = degreesPerPixel;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        float deltaX = -Gdx.input.getDeltaX() * degreesPerPixel;
        float deltaY = -Gdx.input.getDeltaY() * degreesPerPixel;

        Quaternion playerRot = modelInstance.transform.getRotation(new Quaternion()).nor();

        Vector3 right = new Vector3(1f, 0f, 0f);

        Vector3 up = new Vector3(0f, 1f, 0f);

        Quaternion xRotate = new Quaternion((tmp.set(up)).nor(), deltaX).nor();
        Quaternion yRotate = new Quaternion((tmp.set(right).nor()), deltaY).nor();
        Quaternion yRotateWorld = new Quaternion((tmp.set(right.mul(new Matrix4(playerRot)).nor())), deltaY).nor();
        Quaternion xRotateWorld = new Quaternion((tmp.set(up.mul(new Matrix4(playerRot)).nor())), deltaX).nor();

        Quaternion rot = xRotate.cpy().mul(yRotate.cpy()).nor();
        worldRot.set(xRotateWorld.cpy().mul(yRotateWorld.cpy()).nor());


        camera.rotate(worldRot.cpy());

        modelInstance.transform.rotate(rot);

        return super.mouseMoved(screenX, screenY);
    }

    public void update () {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update (float deltaTime) {
        if (Gdx.input.isButtonPressed(Input.Buttons.LEFT)){
            velocity.add(camera.direction.cpy().nor().scl(-1*speedMultiplier * deltaTime));
        }
        /*
        if (keys.containsKey(FORWARD)) {
            tmp.set(camera.direction).nor().scl(deltaTime * speedMultiplier);
            velocity.add(tmp);
        }
        if (keys.containsKey(BACKWARD)) {
            tmp.set(camera.direction).nor().scl(-deltaTime * speedMultiplier);
            velocity.add(tmp);
        }
        if (keys.containsKey(STRAFE_LEFT)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(-deltaTime * speedMultiplier);
            velocity.add(tmp);
        }
        if (keys.containsKey(STRAFE_RIGHT)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * speedMultiplier);
            velocity.add(tmp);
        }
        if (keys.containsKey(STRAFE_UP)) {
            tmp.set(camera.up).nor().scl(deltaTime * speedMultiplier);
            velocity.add(tmp);
        }
        if (keys.containsKey(STRAFE_DOWN)) {
            tmp.set(camera.up).nor().scl(-deltaTime * speedMultiplier);
            velocity.add(tmp);
        }
        if (keys.containsKey(STRAFE_RIGHT)) {
            tmp.set(camera.direction).crs(camera.up).nor().scl(deltaTime * speedMultiplier);
            velocity.add(tmp);
        }
        */

        //stabilizer
        if (keys.containsKey(STABILIZE)) {
            //movement
            //slow down
            tmp.set(velocity).nor().scl(-deltaTime*10);
            velocity.add(tmp);
            //stop
            if (velocity.len() < 0.1) {velocity.scl(0);}
        }
    }

    public Vector3 getVelocity(){
        return velocity;
    }
}