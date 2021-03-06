package com.haxademic.demo.draw.filters.shaders;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.media.DemoAssets;

import processing.core.PGraphics;
import processing.opengl.PShader;
import processing.video.Movie;

public class Demo_OpticalFlowGLSL_WIP
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Movie testMovie;
	protected PGraphics lastFrame;
	protected PGraphics curFrame;
	protected PGraphics opFlowResult;
	protected PGraphics opFlowResultLerped;
	protected PShader opFlowShader;
	protected PShader textureLerpShader;
	
	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 640 );
		Config.setProperty( AppSettings.HEIGHT, 480 );
	}

	protected void firstFrame() {
		// load movie
		testMovie = DemoAssets.movieKinectSilhouette();
		testMovie.jump(0);
		testMovie.loop();
		testMovie.speed(0.8f);
		
		// create buffers
		curFrame = p.createGraphics(p.width, p.height, PRenderers.P3D);
		lastFrame = p.createGraphics(p.width, p.height, PRenderers.P3D);
		opFlowResult = p.createGraphics(p.width, p.height, PRenderers.P3D);
		opFlowResultLerped = p.createGraphics(p.width, p.height, PRenderers.P3D);
		DebugView.setTexture("curFrame", curFrame);
		DebugView.setTexture("lastFrame", lastFrame);
		DebugView.setTexture("opFlowResult", opFlowResult);
		DebugView.setTexture("opFlowResultLerped", opFlowResultLerped);
		
		// load shader
		opFlowShader = p.loadShader(FileUtil.getPath("haxademic/shaders/filters/optical-flow.glsl"));
		textureLerpShader = p.loadShader(FileUtil.getPath("haxademic/shaders/filters/texture-blend-towards-texture.glsl"));
	}

	protected void drawApp() {
		p.background(0);
		
		if(testMovie.width > 10) {
			// copy movie frames
			ImageUtil.cropFillCopyImage(curFrame, lastFrame, true);
			ImageUtil.cropFillCopyImage(testMovie.get(), curFrame, true);
			
			// update/draw shader
			opFlowShader.set("tex0", curFrame);
			opFlowShader.set("tex1", lastFrame);
			opFlowShader.set("lambda", 1f);
			opFlowShader.set("offset", 0.05f, 0.05f);
			opFlowShader.set("scale", 1.5f);
			opFlowResult.filter(opFlowShader);
			
			// fade it
			// run target blend shader
			textureLerpShader.set("blendLerp", 0.5f);
			textureLerpShader.set("targetTexture", opFlowResult);
			opFlowResultLerped.filter(textureLerpShader);
		}
		
		// draw lerped op flow result
		p.image(opFlowResultLerped, 0, 0);
		
		// debug
		// r,g,b,a = -x,+x,-y,+y
		p.loadPixels();
		p.fill(255);
		p.noStroke();
		for (int x = 0; x < p.width; x += 5) {
			for (int y = 0; y < p.height; y += 5) {
				int pixelColor = ImageUtil.getPixelColor(p, x, y);
				float r = ColorUtil.redFromColorInt(pixelColor) / 255f;
				float g = ColorUtil.greenFromColorInt(pixelColor) / 255f;
				float b = ColorUtil.blueFromColorInt(pixelColor) / 255f;
				float a = ColorUtil.alphaFromColorInt(pixelColor) / 255f;
				float xDir = (r + g) - 0.5f;
				float yDir = (b + a) - 0.5f;
				float rotation = -1f * (r * -P.TWO_PI); 
				if(xDir + yDir > 0.01f) { 
					p.pushMatrix();
					p.translate(x, y);
					p.rotate(rotation);
					if(g > 0.03f) p.rect(0, -1, 100f * g, 1);
					p.popMatrix();
				}
			}
		}

	}

}








