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
    BoundingBox bBox;


    public Player(){
        health = 10;
        velocity = new Vector3(0, 0, 0);

        //create player camera
        cam = new PerspectiveCamera(67, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.far = 1200;

        fpsController = new FPSController(cam);

        ModelBuilder mblr = new ModelBuilder();

        model = mblr.createBox(1, 1, 1,
                new Material(ColorAttribute.createDiffuse(Color.GREEN)),
                VertexAttributes.Usage.Position | VertexAttributes.Usage.Normal);

        modelInstance = new ModelInstance(model);
        modelInstance.transform.scale(10, 10, 10);
    }

    public boolean isColliding(ModelInstance otherModelInstance){

        //get player affine transformation matrix
        Matrix4 affine = new Matrix4();
        affine.set(modelInstance.transform.getTranslation(new Vector3()),
                modelInstance.transform.getRotation(new Quaternion()),
                modelInstance.transform.getScale(new Vector3()));

        //invert affine transformation matrix
        Matrix4 inversion = affine.inv();

        //TODO REMOVE
        Mesh mesh = modelInstance.model.meshes.get(0);
        float[] vert = new float[24*2];
        mesh.getVertices(vert);
        for (int i = 0; i < vert.length; i+=6){
            Vector3 vec = new Vector3(vert[i], vert[i+1], vert[i+2]);
            //System.out.println("now prnting for " + i);
            vec.mul(affine);

            //System.out.println(vec.toString());
        }

        //get other model affine transformation matrix
        Matrix4 oAffine = new Matrix4();
        oAffine.set(otherModelInstance.transform.getTranslation(new Vector3()),
                otherModelInstance.transform.getRotation(new Quaternion()),
                otherModelInstance.transform.getScale(new Vector3()));

        //get other mesh
        Mesh oMesh = otherModelInstance.model.meshes.get(0);
        float[] baseVertices = new float[24*3-8];
        oMesh.getVertices(baseVertices);

        //convert base mesh vertices to translated mesh vertices
        Vector3 invertedBoxCoordinates[] = new Vector3[8];
        for (int i = 0; i < baseVertices.length; i+=8){

            //convert vertex array into position vectors
            Vector3 posVector = new Vector3(baseVertices[i], baseVertices[i+1], baseVertices[i+2]);

            //multiply vector by other affine matrix
            Vector3 modPosVector = posVector.mul(oAffine);

            //multiply vector by inverted affine matrix
            Vector3 invPosVector = modPosVector.mul(inversion);

            //add vector to array
            invertedBoxCoordinates[i/8] = invPosVector;
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

        /*System.out.println(maxX);
        System.out.println(maxY);
        System.out.println(maxZ);
        System.out.println(minX);
        System.out.println(minY);
        System.out.println(minZ);*/

        //check if points are inside unit box
        for (int i = 0; i < 8; i++){
            if (-0.5 < invertedBoxCoordinates[i].x && 0.5 > invertedBoxCoordinates[i].x &&
                    -0.5 < invertedBoxCoordinates[i].y && 0.5 > invertedBoxCoordinates[i].y &&
                    -0.5 < invertedBoxCoordinates[i].z && 0.5 > invertedBoxCoordinates[i].z){
                System.out.println("point inside unit box");
                return true;
            }

            if (minX < unitBox[i].x && maxX > unitBox[i].x &&
                    minY < unitBox[i].y && maxY > unitBox[i].y &&
                    minZ < unitBox[i].z && maxZ > unitBox[i].z){
                System.out.println("unit box inside main box");
                return true;
            }
            //check if unit box points is inside box
            //check every unit box point

        }

        //TODO check if unit box is inside

        //TODO pass to collision processing (momentum stuff)


        /*Mesh mesh = modelInstance.model.meshes.get(0);

        float[] points = new float[mesh.getNumVertices()*2];

        short[] indices = new short[mesh.getNumIndices()];

        mesh.getVertices(points);

        mesh.getIndices(indices);

        for (int i = 0; i < points.length; i+=6){
            //System.out.println("now prnting for " + i);
            System.out.println(points[i] + ", " + points[i+1] + ", " + points[i+2]);
        }

        for (int i = 0; i < indices.length; i+=3){
            //System.out.println("now prnting for " + i);
            System.out.println(indices[i] + ", " + indices[i+1] + ", " + indices[i+2]);
        }

        //System.out.println(points[4] + ", " + points[5] + ", " + points[6]);

        System.out.println("Number of Vertices: " + mesh.getNumVertices());

        //System.out.println(mesh.getNumIndices());*/

        return true;
    }
}
