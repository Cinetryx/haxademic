package com.haxademic.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.MotionBlurPGraphics;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PConstants;
import processing.core.PGraphics;
import processing.core.PShape;

public class GifRenderEllo025LoadingAnimationV3
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	PShape _logo;
	PShape _logoInverse;
	float _frames = 62;
	
	protected PGraphics _pg;
	protected MotionBlurPGraphics _pgMotionBlur;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 500 );
		Config.setProperty( AppSettings.HEIGHT, 500 );
		Config.setProperty( AppSettings.SMOOTHING, AppSettings.SMOOTH_HIGH );

		Config.setProperty( AppSettings.RENDERING_MOVIE, false );
		Config.setProperty( AppSettings.RENDERING_GIF, false );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, 60 );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, 15 );
		Config.setProperty( AppSettings.RENDERING_GIF_START_FRAME, Math.round(_frames) );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, Math.round(_frames*2) );
	}
	
	protected void firstFrame() {

		_logo = p.loadShape(FileUtil.haxademicDataPath()+"svg/ello-mouth-gray-01.svg");
		buildCanvas();
	}

	protected void buildCanvas() {
		_pg = p.createGraphics( p.width, p.height, P.P3D );
		_pg.smooth(OpenGLUtil.SMOOTH_HIGH);
		_pgMotionBlur = new MotionBlurPGraphics(30);
	}


	protected void drawApp() {
		p.background(255);
		drawGraphics(_pg);
		_pgMotionBlur.updateToCanvas(_pg, p.g, 0.8f);
	}

	public void drawGraphics(PGraphics pg) {
		
		float frameRadians = PConstants.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
//		float easedPercent = Penner.easeInOutExpo(percentComplete, 0, 1, 1);
//		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);
		float easedPercent = Penner.easeInOutCubic(percentComplete, 0, 1, 1);

		float frameOsc = P.sin( PConstants.TWO_PI * percentComplete);
		float elloSize = (float)(p.width);
		
		PG.setDrawCenter(pg);
		pg.beginDraw();
		pg.clear();
		pg.background(255);
		pg.noStroke();

		pg.translate(pg.width/2, pg.height/2);
		float rotations = 2;
		pg.rotate(easedPercent * PConstants.TWO_PI * rotations);
		pg.shape(_logo, 0, 0, elloSize, elloSize);

		pg.endDraw();
	}
}



