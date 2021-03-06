
package com.haxademic.demo.draw.context;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.OpenGLUtil;
import com.haxademic.core.draw.context.PG;

public class Demo_OpenGLUtil_setBlendMode
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, "800" );
		Config.setProperty( AppSettings.HEIGHT, "800" );
	}

	protected void drawApp() {
		p.background(0);
		p.noStroke();
		
		OpenGLUtil.setBlending( p.g, true );
		int frameLoop = p.frameCount % 300;
		if(frameLoop < 50)       { OpenGLUtil.setBlendMode( p.g, OpenGLUtil.Blend.DARK_INVERSE ); DebugView.setValue("blendMode", "DARK_INVERSE"); }
		else if(frameLoop < 100) { OpenGLUtil.setBlendMode( p.g, OpenGLUtil.Blend.ALPHA_REVEAL ); DebugView.setValue("blendMode", "ALPHA_REVEAL"); }
		else if(frameLoop < 150) { OpenGLUtil.setBlendMode( p.g, OpenGLUtil.Blend.ADD_SATURATE ); DebugView.setValue("blendMode", "ADD_SATURATE"); }
		else if(frameLoop < 200) { OpenGLUtil.setBlendMode( p.g, OpenGLUtil.Blend.ADDITIVE ); DebugView.setValue("blendMode", "ADDITIVE"); }
		else if(frameLoop < 250) { OpenGLUtil.setBlendMode( p.g, OpenGLUtil.Blend.LIGHT_ADD ); DebugView.setValue("blendMode", "LIGHT_ADD"); }
		else if(frameLoop < 300) { OpenGLUtil.setBlendMode( p.g, OpenGLUtil.Blend.SATURATE ); DebugView.setValue("blendMode", "SATURATE"); }

		PG.setDrawCenter(p);
		PG.setBasicLights(p);

		float numShapes = 200;
		
		for( float i=0; i < numShapes; i++ ) {
			float red = i/numShapes * 255f;
			float green = 255f - i/numShapes * 255f;
			float blue = 255f - i/numShapes * 255f;
			p.fill(red, green, blue, 20);
			
			float radius = 180 + 26f * P.sin(i+p.frameCount*0.02f);
			float radians = ((i+p.frameCount*0.25f)/P.TWO_PI) * 0.5f;// * P.sin((i/10f+p.frameCount/10f));
			float xRot = P.sin(radians);
			float yRot = P.cos(radians);
			p.pushMatrix();
			p.translate(p.width/2f + xRot * radius, p.height/2f + yRot * radius);
			p.rotate(-radians);
			p.ellipse(
					0,
					0,
					radius/3f,
					radius/3f
			);
			p.popMatrix();
		}

		// reset blend
		OpenGLUtil.setBlending( p.g, false );
	}

}
