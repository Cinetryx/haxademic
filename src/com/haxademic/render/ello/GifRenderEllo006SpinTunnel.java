package com.haxademic.render.ello;

import java.awt.image.BufferedImage;

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

public class GifRenderEllo006SpinTunnel
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	float _frames = 50;
	float _elloSize = 40;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "640" );
		Config.setProperty( AppSettings.HEIGHT, "640" );
		Config.setProperty( AppSettings.RENDERING_MOVIE, "true" );
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
		encoder.setFrameRate( 30 );
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
//		float easedPercent = Penner.easeInCirc(percentComplete, 0, 1, 1);
		
		PG.setDrawCorner(p);
		p.translate(p.width/2, p.height/2f);

		float iterateFactor = 3f;
		float origSize = p.width * 2f;
		float growth = ((origSize * iterateFactor * iterateFactor) - origSize);
		_elloSize = origSize + percentComplete * growth;

		int index = 0;
		while( _elloSize > 2 ) {
			p.pushMatrix();
			if(index % 2 == 0) {
				p.rotate(frameRadians * p.frameCount * (index+1));
//				p.fill(255);
				p.ellipse(0, 0, _elloSize, _elloSize);
				p.shape(_logo, 0, 0, _elloSize, _elloSize);
			} else {				
				p.rotate(frameRadians * p.frameCount * (index+1));
				p.shape(_logoInverse, 0, 0, _elloSize, _elloSize);
			}
			p.popMatrix();
			
			
			_elloSize /= iterateFactor;
			index++;
		}
		

//		filter(INVERT);

//		if(Config.getBoolean("rendering_gif", false) == true) renderGifFrame();
//		if( p.frameCount == _frames * 8 ) {
//			if(Config.getBoolean("rendering_gif", false) ==  true) encoder.finish();
//			if(isRendering == true) {				
//				videoRenderer.stop();
//				P.println("render done!");
//			}
//		}

	}
}



