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
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.utils.IntIntMap;

/** Takes a {@link Camera} instance and controls it via w,a,s,d and mouse panning.
 * @author badlogic */
public class FPSController extends InputAdapter {
    private final Camera camera;
    private final IntIntMap keys = new IntIntMap();
    private int STRAFE_LEFT = Keys.A;
    private int STRAFE_RIGHT = Keys.D;
    private int FORWARD = Keys.W;
    private int BACKWARD = Keys.S;
    private int ROLL_LEFT = Keys.Q;
    private int ROLL_RIGHT = Keys.E;
    private int STRAFE_UP = Keys.SPACE;
    private int STRAFE_DOWN = Keys.SHIFT_LEFT;
    private float speedMultiplier = 5;
    private Vector3 velocity;
    private float degreesPerPixel = 0.5f;
    private float rollSpeed = 0.8f;
    private final Vector3 tmp = new Vector3();

    public FPSController (Camera camera) {
        this.camera = camera;
        velocity = new Vector3(0, 0, 0);
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
        //set quaternion
        Quaternion xRotate = new Quaternion().set(camera.up, deltaX);
        camera.rotate(xRotate);
        tmp.set(camera.direction).crs(camera.up).nor();
        Quaternion yRotate = new Quaternion().set(tmp, deltaY);
        camera.rotate(yRotate);
        return super.mouseMoved(screenX, screenY);
    }

    public void update () {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update (float deltaTime) {
        //TODO change to do based upon velocity
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
        if (keys.containsKey(ROLL_LEFT)) {
            Quaternion roll = new Quaternion().set(camera.direction, -rollSpeed);
            camera.rotate(roll);
        }
        if (keys.containsKey(ROLL_RIGHT)) {
            Quaternion roll = new Quaternion().set(camera.direction, rollSpeed);
            camera.rotate(roll);
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
        //TODO CONSTANT MOVEMENT
        camera.position.add(velocity);
        //TODO CONSTANT ROTATION

        camera.update(true);
    }
}