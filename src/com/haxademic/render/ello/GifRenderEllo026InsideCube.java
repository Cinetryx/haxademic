package com.haxademic.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.filters.pshader.VignetteFilter;
import com.haxademic.core.draw.shapes.Shapes;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PImage;

public class GifRenderEllo026InsideCube 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected int _frames = 95;
	PImage img;

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 1000 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );
		Config.setProperty( AppSettings.RENDERING_MOVIE, true );
		Config.setProperty( AppSettings.RENDERING_MOVIE_START_FRAME, 1 );
		Config.setProperty( AppSettings.RENDERING_MOVIE_STOP_FRAME, (int)_frames + 1 );
	}

	protected void firstFrame() {

		img = loadImage(FileUtil.haxademicDataPath() + "images/ello.png");
		noStroke();
	}

	protected void drawApp() {
		background(0);
		translate(width/2, height/2, 0);
		
		float percentComplete = 2f * ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete % 1, 0, 1, 1);
		float radsComplete = (easedPercent) * P.TWO_PI;

		if(percentComplete < 1f) {
			rotateX(P.PI); 
			rotateY(radsComplete * 0.25f);
		} else {
			rotateX(P.PI); 
			rotateZ(-radsComplete); 
		}

		Shapes.drawTexturedCube(p.g, 1200 + 0f * P.sin(P.PI + radsComplete), img);
		
		VignetteFilter.instance(p).applyTo(p);
	}
}
