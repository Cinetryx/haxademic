package com.haxademic.render.ello;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.image.AnimatedGifEncoder;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.Penner;

import processing.core.PConstants;
import processing.core.PImage;
import processing.core.PShape;

public class GifRenderEllo002ScaleEased
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	AnimatedGifEncoder encoder;
	PShape _logo;
	PShape _logoInverse;
	PImage _bread;
	float _frames = 60;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "128" );
		Config.setProperty( AppSettings.HEIGHT, "128" );

		Config.setProperty( AppSettings.RENDERING_MOVIE, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF, "false" );
		Config.setProperty( AppSettings.RENDERING_GIF_FRAMERATE, "40" );
		Config.setProperty( AppSettings.RENDERING_GIF_QUALITY, "1" );
		Config.setProperty( AppSettings.RENDERING_GIF_START_FRAME, "2" );
		Config.setProperty( AppSettings.RENDERING_GIF_STOP_FRAME, ""+Math.round(_frames+1) );
	}
	
	protected void firstFrame() {

		_logo = p.loadShape(FileUtil.haxademicDataPath()+"svg/ello.svg");
		_logoInverse = p.loadShape(FileUtil.haxademicDataPath()+"svg/ello-inverse.svg");
		_bread = p.loadImage(FileUtil.haxademicDataPath()+"images/bread.png");
	}

	protected void drawApp() {
		p.background(255);
//		p.fill(255, 40);
//		p.rect(0, 0, p.width, p.height);
		p.noStroke();
		
		float frameRadians = PConstants.TWO_PI / _frames;
		float percentComplete = ((float)(p.frameCount%_frames)/_frames);
		float easedPercent = Penner.easeInOutQuart(percentComplete, 0, 1, 1);

		float frameOsc = P.sin( PConstants.TWO_PI * percentComplete);
		float elloSize = (float)((p.width - p.width*0.02) + p.width*0.02 * frameOsc);
//		float elloSize = (float)(p.width);
		
		
		p.translate(p.width/2, p.height/2);
//		p.rotate(frameRadians * p.frameCount);
//		p.rotate(easedPercent * PConstants.TWO_PI);
		
		// Ello logo
//		PG.setDrawCorner(p);
//		p.shape(_logo, 0, 0, elloSize, elloSize);
		
		// Bread!
		PG.setDrawCenter(p);
		p.image(_bread, 0, 0, elloSize, elloSize);
	}
}



