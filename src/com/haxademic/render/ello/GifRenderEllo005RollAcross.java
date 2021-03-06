package com.haxademic.render.ello;

import java.awt.image.BufferedImage;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.system.SystemUtil;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;

public class GifRenderEllo005RollAcross
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	float _frames = 60;
	float _elloSize = 40;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "640" );
		Config.setProperty( AppSettings.HEIGHT, "640" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF, "false" );
	}
	
	protected void firstFrame() {

		p.smooth(OpenGLUtil.SMOOTH_HIGH);
		_logo = p.loadShape(FileUtil.haxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.haxademicDataPath()+"svg/ello-inverse.svg");
		if(Config.getBoolean("rendering_gif", false) == true) startGifRender();
	}
	
	public void startGifRender() {
		encoder = new AnimatedGifEncoder();
		encoder.start( FileUtil.haxademicOutputPath() + SystemUtil.getTimestamp() + "-export.gif" );
		encoder.setFrameRate( 45 );
		encoder.setQuality( 15 );
		encoder.setRepeat( 0 );
	}
		
	public void renderGifFrame() {
		PImage screenshot = get();
		BufferedImage newFrame = (BufferedImage) screenshot.getNative();
		encoder.addFrame(newFrame);
	}

	protected void drawApp() {
		p.background(255);
		
		float frameRadians = PConstants.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		
		if(percentComplete == 0)
			_elloSize *= 4;
		
		PG.setDrawCorner(p);
				
		float dist = percentComplete * (p.width + _elloSize*2);
		
		float x = -_elloSize + dist;
		float circumference = _elloSize * P.PI;
		float rotationRads = (x / circumference) * P.TWO_PI;
		
		p.pushMatrix();
		p.translate(x, p.height - _elloSize/2f);
		p.rotate(rotationRads);
		p.shape(_logo, 0, 0, _elloSize, _elloSize);
		p.popMatrix();

//		filter(INVERT);
//
//		if(Config.getBoolean("rendering_gif", false) == true) renderGifFrame();
//		if( p.frameCount == _frames * 4 + 5 ) {
//			if(Config.getBoolean("rendering_gif", false) ==  true) encoder.finish();
//			if(videoRenderer != null) {				
//				videoRenderer.stop();
//				P.println("render done!");
//			}
//		}

	}
}



