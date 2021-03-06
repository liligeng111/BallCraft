package hkust.comp3111h.ballcraft.ui;

import hkust.comp3111h.MyApplication;
import hkust.comp3111h.ballcraft.MapModeDef;
import hkust.comp3111h.ballcraft.TerrainDef;
import hkust.comp3111h.ballcraft.client.Map;
import hkust.comp3111h.ballcraft.client.MapParser;
import hkust.comp3111h.ballcraft.server.Plane;
import hkust.comp3111h.ballcraft.server.Unit;
import hkust.comp3111h.ballcraft.server.Wall;

import java.util.Vector;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class MapRenderer implements GLSurfaceView.Renderer {
    
    private Map currMap;
        
    private Vector<Unit> units;
    
    private float rotateAngle = 0;
    
    private boolean shouldReload = true;
    
    public MapRenderer() {
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        gl.glViewport(0, 0, width, height);
        gl.glMatrixMode(GL10.GL_PROJECTION);
        gl.glLoadIdentity();
        GLU.gluPerspective(gl, 45.0f, (float) width / (float) height, 0.1f, 1000f);
        gl.glMatrixMode(GL10.GL_MODELVIEW);
        gl.glLoadIdentity();
        
        this.loadLight(gl);

        gl.glEnable(GL10.GL_LIGHTING);
        gl.glEnable(GL10.GL_LIGHT0);
   }
    
    private void loadLight(GL10 gl) {
        // set light according to whether it is day or night
        int mapMode = currMap.getMode();
        float [] lightAmbient = MapModeDef.getMapModeAmbientLightById(mapMode);
        float [] lightDiffuse = MapModeDef.getMapModeDiffuseLightById(mapMode);
        float [] lightSpecular = MapModeDef.getMapModeSpecularLightById(mapMode);
        float [] lightPosition = { 0, 0, 100f, 1f };

        float [] matAmbient = { 0.4f, 0.4f, 0.4f, 1f };
        float [] matDiffuse = { 0.6f, 0.6f, 0.6f, 1f };
        float [] matSpecular = { 0.9f, 0.9f, 0.9f, 1f };
        float [] matShininess = { 8f };

        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_AMBIENT, matAmbient, 0);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_DIFFUSE, matDiffuse, 0);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SPECULAR, matSpecular, 0);
        gl.glMaterialfv(GL10.GL_FRONT_AND_BACK, GL10.GL_SHININESS, matShininess, 0);

        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_AMBIENT, lightAmbient, 0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_DIFFUSE, lightDiffuse, 0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_SPECULAR, lightSpecular, 0);
        gl.glLightfv(GL10.GL_LIGHT0, GL10.GL_POSITION, lightPosition, 0);
    }
    
    public void setMap(String mapName) {
        currMap = MapParser.getMapFromXML(mapName);
        units = currMap.getUnit();
        this.shouldReload = true;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        gl.glShadeModel(GL10.GL_SMOOTH);
        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f);
        gl.glClearDepthf(1.0f);
        gl.glEnable(GL10.GL_DEPTH_TEST);
        gl.glDepthFunc(GL10.GL_LEQUAL);
        gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST);
        gl.glEnable(GL10.GL_NORMALIZE);
        gl.glEnable(GL10.GL_RESCALE_NORMAL);
        
        gl.glEnable(GL10.GL_BLEND);
        gl.glBlendFunc(GL10.GL_ONE, GL10.GL_ONE_MINUS_SRC_ALPHA); 
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        if (this.shouldReload) {
	        Wall.loadTexture(gl, MyApplication.getAppContext(), 
	                TerrainDef.getTerrainWallTextureBallId(currMap.getTerrain()));
	        Plane.loadTexture(gl, MyApplication.getAppContext(), 
	                TerrainDef.getTerrainFloorTextureById(currMap.getTerrain()));
	        this.loadLight(gl);
	        this.shouldReload = false;
        }
        
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);
        gl.glLoadIdentity();
        
        GLU.gluLookAt(gl, (float) (300 * Math.cos(rotateAngle)), (float) (300 * Math.signum(rotateAngle)), 
                100, 0, 0, 0, 0, 0, 1);
        rotateAngle += 0.003f;
        
        for (int i = 0; i < units.size(); i++) {
            units.get(i).draw(gl);
        }
    }
    
}
