package com.mygdx.re_action;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 * Created by dylan on 15/02/17.
 */
public class Physics {

    public Physics(){
    }

    public void staticCollision(Actor aDynamic, Actor aStatic, Vector3 collisionPoint, Vector3 normal){
        //get translational momentum of dynamic actor
        Vector3 tMomentum = aDynamic.getVelocity().scl(aDynamic.getMass());

        //aDynamic.velocity.scl(0);

        //get angular velocity per deltatime
        Quaternion aV = aDynamic.getAngularVelocity();

        Vector3 axis;

        //find axis
        if (aV.x == 0){
            axis = new Vector3(0, 0, 0);
        } else {
            float x = aV.x / (float) Math.sqrt(1 - Math.pow(aV.w, 2));
            float y = aV.y / (float) Math.sqrt(1 - Math.pow(aV.w, 2));
            float z = aV.z / (float) Math.sqrt(1 - Math.pow(aV.w, 2));

            axis = new Vector3(x, y, z);
        }

        //get radiusw
        Vector3 vRadius = collisionPoint.add((aDynamic.getModelInstance().transform.getTranslation(new Vector3())).scl(-1));
        Vector3 radDir = vRadius.cpy().nor();
        float radius = vRadius.len();

        //get angular momemntum of dynamic actor
        Vector3 aMomentum = axis.scl(radius * aV.getAngle() * Gdx.graphics.getDeltaTime());

        Vector3 Momemtum = tMomentum.add(aMomentum);
        //System.out.println(Momemtum.toString());

        System.out.println(normal.toString());
        float dot = Momemtum.dot(normal);
        float cosine = dot/(Momemtum.len());
        System.out.println(cosine);
        Vector3 xMomentum = normal.cpy().scl(Momemtum.len()*cosine);
        System.out.println(xMomentum.toString());

        //TODO FIX GETTING STUCK IN WALL WHEN HITTING CORNER
        //TODO FIX BLACKOUT WHEN ROTATING IN CORNER
        //todo maybe fix geometry

        //daDynamic.modelInstance.transform.trn(aDynamic.getVelocity().cpy().scl(-1));
        aDynamic.velocity.add(xMomentum.cpy().scl(-2));
    }

    public void dynamicCollision(){

    }
}
