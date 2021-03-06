package com.haxademic.demo.draw.color;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.color.ImageGradient;

public class Demo_ImageGradient
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	ImageGradient imageGradient;
	
	protected void config() {
		Config.setProperty( AppSettings.FPS, 90 );
		Config.setProperty( AppSettings.WIDTH, 800 );
		Config.setProperty( AppSettings.HEIGHT, 800 );
	}

	protected void firstFrame() {

		imageGradient = new ImageGradient(ImageGradient.PASTELS());
//		imageGradient.addTexturesFromPath(ImageGradient.COOLORS_PATH);
	}

	protected void drawApp() {
		float colorProgress = 0.5f + 0.5f * P.sin(p.frameCount * 0.01f);
		p.background(imageGradient.getColorAtProgress(colorProgress));
		if(p.frameCount % 100 == 1) imageGradient.randomGradientTexture();
		
		p.pushMatrix();
		p.translate(p.width/2 - imageGradient.texture().width/2, p.height/2);
		imageGradient.drawDebug(p.g);
		p.popMatrix();
	}
}

