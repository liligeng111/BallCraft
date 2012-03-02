package hkust.comp3111h.ballcraft;


import java.lang.reflect.Field;

import javax.microedition.khronos.egl.EGL10;
import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.egl.EGLDisplay;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.threed.jpct.Logger;

public class Client extends Activity implements SensorEventListener
{

	// Used to handle pause and resume...
	public static Client master = null;
	private MyRenderer renderer = null;
	
	private GameInput input;
	
	private SensorManager sensorManager;
	private GLSurfaceView mGLView;
	
	private RelativeLayout rLayout;
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
    	getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		if (master != null) {
			copy(master);
		}
		
		super.onCreate(savedInstanceState);

		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
        		WindowManager.LayoutParams.FLAG_FULLSCREEN);
        
        startService(new Intent(this, Server.class));
		
		mGLView = new GLSurfaceView(getApplication());

		mGLView.setEGLConfigChooser(new GLSurfaceView.EGLConfigChooser() {
			public EGLConfig chooseConfig(EGL10 egl, EGLDisplay display) {
				// Ensure that we get a 16bit framebuffer. Otherwise, we'll fall
				// back to Pixelflinger on some device (read: Samsung I7500)
				int[] attributes = new int[] { EGL10.EGL_DEPTH_SIZE, 16, EGL10.EGL_NONE };
				EGLConfig[] configs = new EGLConfig[1];
				int[] result = new int[1];
				egl.eglChooseConfig(display, attributes, configs, 1, result);
				return configs[0];
			}
		});
		
		initLayout();
		
		renderer = new MyRenderer(this);
		mGLView.setRenderer(renderer);
		
		setContentView(rLayout);
		
		sensorManager = (SensorManager) this.getSystemService(SENSOR_SERVICE);
		sensorManager.registerListener(this, 
				sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
				SensorManager.SENSOR_DELAY_NORMAL);
		
		input = new GameInput(new Vector2f(0f, 0f));
    }
    
    /**
     * Initialize the main layout of the game view,
     * including buttons, MUD, etc.
     */
    protected void initLayout() {
		rLayout = new RelativeLayout(this);
		rLayout.setLayoutParams(new ViewGroup.LayoutParams(
				ViewGroup.LayoutParams.FILL_PARENT,
				ViewGroup.LayoutParams.FILL_PARENT));
		
		Button castBtn = new Button(this);
		castBtn.setLayoutParams(new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT));
		castBtn.setPadding(60, 50, 60, 50);
		castBtn.getBackground().setAlpha(80);
		castBtn.setText("Function 1");
		castBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (input != null) {
					// input.addSkill(new Skill(BallCraft.Skill.TEST_SKILL_1));
				}
			}
		});
		
		Button accBtn = new Button(this);
		RelativeLayout.LayoutParams accParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.WRAP_CONTENT,
				RelativeLayout.LayoutParams.WRAP_CONTENT);
		accParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		accBtn.setLayoutParams(accParams);
		accBtn.setPadding(60, 50, 60, 50);
		accBtn.getBackground().setAlpha(80);
		accBtn.setText("Function 2");
		accBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			}
		});
		
		rLayout.addView(mGLView);
		rLayout.addView(castBtn);
		rLayout.addView(accBtn);
    }

	@Override
	protected void onPause()
	{
		super.onPause();
		mGLView.onPause();
	}

	@Override
	protected void onResume()
	{
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onStop()
	{
		super.onStop();
	}
	
	private void copy(Object src) {
		try {
			// Logger.log("Copying data from master Activity!");
			Field[] fs = src.getClass().getDeclaredFields();
			for (Field f : fs) {
				f.setAccessible(true);
				f.set(this, f.get(src));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy){}

	@Override
	public void onSensorChanged(SensorEvent event) 
	{
		input.acceleration.x = event.values[SensorManager.DATA_Y];
		input.acceleration.y = event.values[SensorManager.DATA_X];		
	}
	
	public GameInput getInput()
	{
		return input;
	}
}