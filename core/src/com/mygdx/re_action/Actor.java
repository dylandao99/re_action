package com.mygdx.re_action;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Mesh;
import com.badlogic.gdx.graphics.VertexAttributes;
import com.badlogic.gdx.graphics.g3d.Material;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.utils.ModelBuilder;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Quaternion;
import com.badlogic.gdx.math.Vector3;

/**
 * Created by dylan on 15/02/17.
 *
 * For use with Physics
 */
public class Actor {
    ModelInstance modelInstance;
    Vector3 velocity;
    Vector3 angularVelocity;
    float mass;
    float scale;
    boolean isStatic;

    public final static int CUBE = 1;

    public Actor (float mass, float scale, boolean isStatic, int shape){
        //initialize variables
        this.mass = mass;
        this.scale = scale;
        this.isStatic = isStatic;
        velocity = new Vector3();
        angularVelocity = new Vector3();

        //create model
        createModel(shape);
    }

    public void createModel(int shape){

        Model model;
        ModelBuilder mblr = new ModelBuilder();

        switch (shape){
            default:
            case 1:
                model = mblr.createBox(scale, scale, scale,
                        new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                        VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);
                break;
        }

        modelInstance = new ModelInstance(model);
    }

    public Vector3 isColliding(ModelInstance otherModelInstance){

        //get player affine transformation matrix
        Matrix4 affine = new Matrix4();
        affine.set(modelInstance.transform.getTranslation(new Vector3()),
                modelInstance.transform.getRotation(new Quaternion()),
                modelInstance.transform.getScale(new Vector3()).scl(scale));

        //invert affine transformation matrix
        Matrix4 inversion = affine.cpy().inv();

        //get other mesh
        Mesh oMesh = otherModelInstance.model.meshes.get(0);
        float[] baseVertices = new float[64];
        oMesh.getVertices(baseVertices);

        //convert base mesh vertices to translated mesh vertices
        Vector3 invertedBoxCoordinates[] = new Vector3[8];
        for (int i = 0; i < baseVertices.length; i+=8){

            //convert vertex array into position vectors
            Vector3 posVector = new Vector3(baseVertices[i], baseVertices[i+1], baseVertices[i+2]);

            posVector.mul(otherModelInstance.transform);

            //multiply vector by inverted affine matrix
            Vector3 invPosVector = posVector.cpy().mul(new Matrix4(inversion));

            //add vector to array
            invertedBoxCoordinates[i/8] = invPosVector;
        }

        //TODO CLEAN THIS
        //get other model affine transformation matrix
        Matrix4 oAffine = new Matrix4();
        oAffine.set(otherModelInstance.transform.getTranslation(new Vector3()),
                otherModelInstance.transform.getRotation(new Quaternion()).nor(),
                otherModelInstance.transform.getScale(new Vector3()).scl(800)); //TODO MAKE MORE UNIVERSAL

        Matrix4 oInversion = oAffine.cpy().inv();

        Mesh mesh = modelInstance.model.meshes.get(0);
        float[] oBaseVertices = new float[48];
        mesh.getVertices(oBaseVertices);
        Vector3 v3[] = new Vector3[8];

        for (int i = 0; i < oBaseVertices.length; i+=6){
            v3[i/6] = new Vector3(oBaseVertices[i], oBaseVertices[i+1], oBaseVertices[i+2]);
        }

        for (int i = 0; i < v3.length; i++){
            Vector3 PosVector = v3[i].cpy();

            PosVector.mul(modelInstance.transform);

            PosVector.mul(oInversion.cpy());

            v3[i] = PosVector;
        }

        int numCollisions = 0;

        Matrix4 trans = new Matrix4();

        //check if points are inside unit box
        for (int i = 0; i < 8; i++){
            if (-0.5 < invertedBoxCoordinates[i].x && 0.5 > invertedBoxCoordinates[i].x &&
                    -0.5 < invertedBoxCoordinates[i].y && 0.5 > invertedBoxCoordinates[i].y &&
                    -0.5 < invertedBoxCoordinates[i].z && 0.5 > invertedBoxCoordinates[i].z){
                //System.out.println("point inside unit box");
                numCollisions++;
                trans.set(affine);
            }
        }

        //check the other box
        for (int i = 0; i < 8; i++){
            if (-0.5 < v3[i].x && 0.5 > v3[i].x &&
                    -0.5 < v3[i].y && 0.5 > v3[i].y &&
                    -0.5 < v3[i].z && 0.5 > v3[i].z){
                numCollisions++;
                trans.set(oAffine);
            }
        }

        //TODO FIND POINT OF INTERSECTION
        //find the point closest to the bound of the box

        //TODO pass to collision processing (momentum stuff)



        if (numCollisions == 0){
            System.out.println("not colliding");
            return null;
        } else if (numCollisions >= 8){
            System.out.println("inside");
            return null;
        } else {
            System.out.println("colliding");
            //locate collision point
            //search for point closest to 0.5 value
            int index = 0;
            double diff;
            Vector3 unitContactPoint;

            if (trans.equals(affine)) {
                diff = Math.abs(0.5 - invertedBoxCoordinates[0].x);
                for (int i = 0; i < invertedBoxCoordinates.length; i++) {
                    if (Math.abs(0.5 - invertedBoxCoordinates[i].x) < diff) {
                        diff = Math.abs(0.5 - invertedBoxCoordinates[i].x);
                        index = i;
                    } else if (Math.abs(0.5 - invertedBoxCoordinates[i].y) < diff) {
                        diff = Math.abs(0.5 - invertedBoxCoordinates[i].y);
                        index = i;
                    } else if (Math.abs(0.5 - invertedBoxCoordinates[i].z) < diff) {
                        diff = Math.abs(0.5 - invertedBoxCoordinates[i].z);
                        index = i;
                    }
                }
                unitContactPoint = invertedBoxCoordinates[index];
            } else {
                diff = Math.abs(0.5 - v3[0].x);
                for (int i = 0; i < v3.length; i++) {
                    if (Math.abs(0.5 - v3[i].x) < diff) {
                        diff = Math.abs(0.5 - v3[i].x);
                        index = i;
                    } else if (Math.abs(0.5 - v3[i].y) < diff) {
                        diff = Math.abs(0.5 - v3[i].y);
                        index = i;
                    } else if (Math.abs(0.5 - v3[i].z) < diff) {
                        diff = Math.abs(0.5 - v3[i].z);
                        index = i;
                    }
                }
                unitContactPoint = v3[index];
            }

            Vector3 contactPoint = unitContactPoint.mul(trans);
            System.out.println("BEST MATCH: " + contactPoint.toString());

            //transform it with affine
            return contactPoint;
        }
    }
}
