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
    private int ROLL_LEFT = Keys.Q;
    private int ROLL_RIGHT = Keys.E;
    private int STRAFE_UP = Keys.SPACE;
    private int STRAFE_DOWN = Keys.SHIFT_LEFT;
    private int STABILIZE = Keys.CONTROL_LEFT;
    private float speedMultiplier = 10;
    private Vector3 velocity;
    private float degreesPerPixel = 0.5f;
    private float rollMultiplier = 10.0f;
    private Quaternion rollVelocity;
    private Vector3 cubeForward;
    private Vector3 cubeUp;

    private final Vector3 tmp = new Vector3();
    public FPSController (Camera camera, ModelInstance modelInstance) {
        this.camera = camera;
        this.modelInstance = modelInstance;

        cubeForward = new Vector3(0f,0f,-1f);
        cubeUp = new Vector3(0f, 1f, 0f);
        velocity = new Vector3(0, 0, 0);
        rollVelocity = new Quaternion();
        modelInstance.transform.setToTranslation(0, 0, -20);

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
        //right.mul(playerRot);

        Vector3 up = new Vector3(0f, 1f, 0f);
        //up.mul(playerRot);

        Quaternion xRotate = new Quaternion((tmp.set(up)).nor(), deltaX).nor();
        Quaternion yRotate = new Quaternion((tmp.set(right).nor()), deltaY).nor();
        Quaternion yRotateWorld = new Quaternion((tmp.set(right.mul(new Matrix4(playerRot)).nor())), deltaY).nor();

        //TODO NEED TO LEFT MULTIPLY

        //TODO xRotate only affects cubeForward
        //TODO yRotate affects cubeUp and cubeForward

        Quaternion rot = xRotate.cpy().mul(yRotate.cpy()).nor();


        cubeForward.mul(new Matrix4(yRotateWorld.cpy()));
        cubeForward.mul(new Matrix4(xRotate.cpy()));

        //cubeUp.mul(new Matrix4(playerRot.conjugate().mul(rot).nor()));
        cubeUp.mul(new Matrix4(yRotateWorld));

        modelInstance.transform.rotate(rot);



        /*
        //get player affine transformation matrix
        Matrix4 affine = new Matrix4();
        affine.set(modelInstance.transform.getTranslation(new Vector3()).add(0, 0, 20),
                modelInstance.transform.getRotation(new Quaternion()),
                modelInstance.transform.getScale(new Vector3()));

        Vector3 dir = new Vector3(0f, 0f, -1f);
        dir.mul(affine);
        camera.direction.set(dir);*/

        //Vector3 up = new Vector3(0f, 1f, 0f);
        //up.mul(affine);
        //camera.up.set(up);

        //camera.up.set(cubeUp);
        //camera.direction.set(cubeForward);

        //System.out.println(rot.getAngle());


        /*Quaternion worldRot = new Quaternion(modelInstance.transform.getRotation(new Quaternion())).nor();

        //set x and y axis quaternion
        Quaternion xRotate = new Quaternion().set(tmp.set(camera.up).nor(), deltaX).nor();
        Quaternion yRotate = new Quaternion().set(tmp.set(camera.direction).crs(camera.up).nor(), deltaY).nor();

        Quaternion rot = xRotate.mul(yRotate).nor();

        System.out.println("Worldrot: " + worldRot.toString());
        System.out.println("Rot: " + rot.toString());

        camera.rotate(qTmp.set(rot));

        Quaternion rotation = qTmp.set(rot).mul(qTmp2.set(worldRot).conjugate());

        System.out.println("New Rot: " + rotation.toString());

        modelInstance.transform.rotate(rot);

        System.out.println("New Rotation: " + modelInstance.transform.getRotation(new Quaternion()));

        System.out.println();
        */

        return super.mouseMoved(screenX, screenY);
    }

    public void update () {
        update(Gdx.graphics.getDeltaTime());
    }

    public void update (float deltaTime) {
        //velocity-based
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
            Quaternion roll = new Quaternion().set(tmp.set(camera.direction).nor(), -rollMultiplier*deltaTime);
            rollVelocity.mul(roll);
        }
        if (keys.containsKey(ROLL_RIGHT)) {
            Quaternion roll = new Quaternion().set(tmp.set(camera.direction).nor(), rollMultiplier*deltaTime);
            rollVelocity.mul(roll);
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

        //stabilizer
        if (keys.containsKey(STABILIZE)) {
            //movement
            //slow down
            tmp.set(velocity).nor().scl(-deltaTime*10);
            velocity.add(tmp);
            //stop
            if (velocity.len() < 0.1) {velocity.scl(0);}

            //rotation
            rollVelocity.slerp(new Quaternion(), deltaTime*10);
        }

        //update position with velocity
        //TODO CHANGE TO FOLLOW
        camera.position.add(tmp.set(velocity));
        modelInstance.transform.trn(velocity);

        //update rotation with rollVelocity
        //camera.rotate(rollVelocity);
        //modelInstance.transform.rotate(rollVelocity);

        camera.direction.set(cubeForward);
        camera.up.set(cubeUp);

        camera.update(true);
    }
}