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

/**
 * Created by dylan on 05/02/17.
 */
public class Player {

    //TODO create getters and setters
    int health;
    Vector3 velocity;
    PerspectiveCamera cam;
    FPSController fpsController;
    Model model;
    ModelInstance modelInstance;
    float scale = 100f;


    public Player(){
        health = 10;
        velocity = new Vector3(0, 0, 0);
        //todo not very accurate, fix

        //create player camera
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.far = 1200;

        ModelBuilder mblr = new ModelBuilder();

        model = mblr.createBox(scale, scale, scale,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        modelInstance = new ModelInstance(model);
        //modelInstance.transform.translate(0, 0, -20);

        fpsController = new FPSController(cam, modelInstance);
    }

    public int isColliding(ModelInstance otherModelInstance){

        //get player affine transformation matrix
        Matrix4 affine = new Matrix4();
        affine.set(modelInstance.transform.getTranslation(new Vector3()),
                fpsController.getWorldRotation(),
                modelInstance.transform.getScale(new Vector3()).scl(scale));



        //invert affine transformation matrix
        Matrix4 inversion = affine.inv();

        //get other model affine transformation matrix
        Matrix4 oAffine = new Matrix4();
        oAffine.set(otherModelInstance.transform.getTranslation(new Vector3()),
                otherModelInstance.transform.getRotation(new Quaternion()).nor(),
                otherModelInstance.transform.getScale(new Vector3()));

        //get other mesh
        Mesh oMesh = otherModelInstance.model.meshes.get(0);
        float[] baseVertices = new float[64];
        oMesh.getVertices(baseVertices);

        /*for (int i = 0; i < baseVertices.length; i+=3){
            System.out.println(baseVertices[i] + ", " + baseVertices[i+1] + ", " + baseVertices[i+2]);
        }*/

        //todo REMOVE, FOR TESTING
        Mesh mesh = modelInstance.model.meshes.get(0);
        float[] BaseVertices = new float[48];
        mesh.getVertices(BaseVertices);
        Vector3 v3[] = new Vector3[8];

        for (int i = 0; i < BaseVertices.length; i+=6){
            //Vector3 PosVector = new Vector3(BaseVertices[i], BaseVertices[i+1], BaseVertices[i+2]);
            //PosVector.mul(affine);
            //v3[i/6] = PosVector;
        }

        //convert base mesh vertices to translated mesh vertices
        Vector3 invertedBoxCoordinates[] = new Vector3[8];
        Vector3 invertedBoxCoordinates2[] = new Vector3[8];
        for (int i = 0; i < baseVertices.length; i+=8){

            //convert vertex array into position vectors
            Vector3 posVector = new Vector3(baseVertices[i], baseVertices[i+1], baseVertices[i+2]);

            //multiply vector by it's own affine matrix
            Vector3 modPosVector = posVector.cpy().mul(new Matrix4(oAffine));

            //multiply vector by inverted affine matrix
            Vector3 invPosVector = modPosVector.cpy().mul(new Matrix4(inversion));

            //add vector to array
            invertedBoxCoordinates[i/8] = invPosVector;
            invertedBoxCoordinates2[i/8] = modPosVector;
        }

        for (int i = 0; i < invertedBoxCoordinates.length; i++){
            System.out.println(invertedBoxCoordinates[i].toString());
            //System.out.println(invertedBoxCoordinates2[i].toString());
            //System.out.println(v3[i].toString());
        }

        Vector3 unitBox[] = {new Vector3 (-0.5f, -0.5f, -0.5f),
                            new Vector3 (-0.5f, -0.5f, 0.5f),
                            new Vector3 (0.5f, -0.5f, -0.5f),
                            new Vector3 (-0.5f, 0.5f, -0.5f),
                            new Vector3 (-0.5f, 0.5f, 0.5f),
                            new Vector3 (0.5f, -0.5f, 0.5f),
                            new Vector3 (0.5f, 0.5f, -0.5f),
                            new Vector3 (0.5f, 0.5f, 0.5f)};


        //get max and min values for otherModel
        float maxX = invertedBoxCoordinates[0].x; float minX = invertedBoxCoordinates[0].x;
        float maxY = invertedBoxCoordinates[0].y; float minY = invertedBoxCoordinates[0].y;
        float maxZ = invertedBoxCoordinates[0].z; float minZ = invertedBoxCoordinates[0].z;

        for (int i = 1; i < 8; i++){
            if (invertedBoxCoordinates[i].x > maxX){
                maxX = invertedBoxCoordinates[i].x;
            }
            if (invertedBoxCoordinates[i].y > maxY){
                maxY = invertedBoxCoordinates[i].y;
            }
            if (invertedBoxCoordinates[i].z > maxZ){
                maxZ = invertedBoxCoordinates[i].z;
            }
            if (invertedBoxCoordinates[i].x < minX){
                minX = invertedBoxCoordinates[i].x;
            }
            if (invertedBoxCoordinates[i].y < minY){
                minY = invertedBoxCoordinates[i].y;
            }
            if (invertedBoxCoordinates[i].z < minZ){
                minZ = invertedBoxCoordinates[i].z;
            }
        }

        int numCollisions = 0;

        //check if points are inside unit box
        for (int i = 0; i < 8; i++){
            if (-0.5 < invertedBoxCoordinates[i].x && 0.5 > invertedBoxCoordinates[i].x &&
                    -0.5 < invertedBoxCoordinates[i].y && 0.5 > invertedBoxCoordinates[i].y &&
                    -0.5 < invertedBoxCoordinates[i].z && 0.5 > invertedBoxCoordinates[i].z){
                //System.out.println("point inside unit box");
                numCollisions++;
            }

            //unit box inside box
            if (minX < unitBox[i].x && maxX > unitBox[i].x &&
                    minY < unitBox[i].y && maxY > unitBox[i].y &&
                    minZ < unitBox[i].z && maxZ > unitBox[i].z){
                //System.out.println("unit box inside main box");
                numCollisions++;
            }
        }

        //TODO pass to collision processing (momentum stuff)



        if (numCollisions == 0){
            System.out.print("not colliding");
        } else if (numCollisions >= 8){
            System.out.print("inside");
        } else {
            System.out.print("colliding");
        }

        System.out.println(": " + numCollisions);

        return numCollisions;
    }
}
