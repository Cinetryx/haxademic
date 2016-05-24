package com.haxademic.sketch.shader;

import com.haxademic.app.haxmapper.textures.TextureShaderTimeStepper;
import com.haxademic.core.app.AppSettings;
import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.image.filters.shaders.ChromaColorFilter;
import com.haxademic.core.system.FileUtil;

import controlP5.ControlP5;
import processing.core.PGraphics;
import processing.opengl.PShader;
import processing.video.Movie;


public class ChromaKeyShaderControlsMovie 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected PGraphics _pg;

	PShader _chromaKeyFilter;
	protected ControlP5 _cp5;
	public float thresholdSensitivity;
	public float smoothing;
	public float colorToReplace_R;
	public float colorToReplace_G;
	public float colorToReplace_B;

	TextureShaderTimeStepper underlay;
	Movie movie;

	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		p.appConfig.setProperty( AppSettings.WIDTH, "720" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "1280" );
	}
	
	public void setup() {
		super.setup();

		underlay = new TextureShaderTimeStepper( p.width, p.height, "sdf-03.glsl" );
		
		movie = new Movie(this, FileUtil.getFile("video/dancelab/013.mov")); 
//		movie = new Movie(this, FileUtil.getFile("video/dancelab/AlphaTest.mov")); 
		movie.play();
		movie.loop();
		movie.speed(1.0f);

		_pg = p.createGraphics( p.width, p.height, P.P3D );
		setupChromakey();
	}
		
	protected void setupChromakey() {
		_cp5 = new ControlP5(this);
		int cp5W = 160;
		int cp5X = 20;
		int cp5Y = 20;
		int cp5YSpace = 40;
		_cp5.addSlider("thresholdSensitivity").setPosition(cp5X,cp5Y).setWidth(cp5W).setRange(0,1f).setValue(0.73f);
		_cp5.addSlider("smoothing").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.08f);
		_cp5.addSlider("colorToReplace_R").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.71f);
		_cp5.addSlider("colorToReplace_G").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.99f);
		_cp5.addSlider("colorToReplace_B").setPosition(cp5X,cp5Y+=cp5YSpace).setWidth(cp5W).setRange(0,1f).setValue(0.02f);

	}

	public void drawApp() {
		// draw a background
		underlay.updateDrawWithTime(p.frameCount * 0.01f);
		p.image(underlay.texture(), 0, 0);

//		p.pushMatrix();
//		p.translate(p.width/2, p.height/2);
//		Gradients.radial(p, p.width * 2, p.height * 2, p.color(127 + 127f * sin(p.frameCount/10f)), p.color(127 + 127f * sin(p.frameCount/11f)), 100);
//		p.popMatrix();

		// reset chroma key uniforms
		ChromaColorFilter.instance(p).setColorToReplace(colorToReplace_R, colorToReplace_G, colorToReplace_B);
		ChromaColorFilter.instance(p).setSmoothing(smoothing);
		ChromaColorFilter.instance(p).setThresholdSensitivity(thresholdSensitivity);
		
		// draw frame to offscreen buffer
		_pg.beginDraw();
		_pg.clear();
		_pg.image(movie, 0, 0, _pg.width, _pg.height);
		_pg.endDraw();
		
		// apply filter & draw to scren
		ChromaColorFilter.instance(p).applyTo(_pg);
		p.image(_pg, 0, 0);
	}
}
