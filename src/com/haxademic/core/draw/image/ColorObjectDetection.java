package com.haxademic.core.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.color.ColorUtil;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.InvertFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.easing.EasingFloat;

import processing.core.PGraphics;
import processing.core.PImage;
import processing.opengl.PShader;

public class ColorObjectDetection {

	protected PGraphics source;
	protected PGraphics analysisBuffer;
	protected int colorCompare;
	protected float scale = 1;
	protected float colorClosenessThreshold = 0.95f;
	protected int minPointsThreshold = 10;
	protected float totalCounted = 0;
	protected float totalChecked = 0;
	protected int bufferW;
	protected int bufferH;
	protected boolean debugging = false;
	protected PShader colorDistanceFilter;
	protected EasingFloat x = new EasingFloat(0.5f, 0.5f);
	protected EasingFloat y = new EasingFloat(0.5f, 0.5f);

	public ColorObjectDetection(PImage sourceImg, float scale) {
		this.scale = scale;
		bufferW = P.round(scale * sourceImg.width);
		bufferH = P.round(scale * sourceImg.height);
		source = PG.newPG2DFast(bufferW, bufferH);
		analysisBuffer = PG.newPG2DFast(bufferW, bufferH);
//		source.noSmooth();
//		analysisBuffer.noSmooth();
		colorDistanceFilter = P.p.loadShader(FileUtil.getPath("haxademic/shaders/filters/color-distance.glsl"));
		setColorCompare(1f, 1f, 1f);
	}
	
	public void colorClosenessThreshold(float colorClosenessThreshold) {
		this.colorClosenessThreshold = colorClosenessThreshold;
	}
	
	public float colorClosenessThreshold() {
		return colorClosenessThreshold;
	}
	
	public void minPointsThreshold(int minPointsThreshold) {
		this.minPointsThreshold = minPointsThreshold;
	}
	
	public int minPointsThreshold() {
		return minPointsThreshold;
	}
	
	public boolean isActive() {
		return totalCounted >= minPointsThreshold;
	}
	
	public int pixelsActive() {
		return (int) totalCounted;
	}
	
	public int pixelsTotal() {
		return (int) totalChecked;
	}
	
	public void debugging(boolean debugging) {
		this.debugging = debugging;
	}
	
	public PGraphics analysisBuffer() {
		return analysisBuffer;
	}
	
	public PGraphics sourceBuffer() {
		return source;
	}
	
	public float x() {
		return x.value();
	}
	
	public float y() {
		return y.value();
	}
	
	public int colorCompare() {
		return colorCompare;
	}
	
	public void loadPixels() {
		source.loadPixels();
	}
	
	public void setColorFromSource(int x, int y) {
		loadPixels();
		int pixelColor = ImageUtil.getPixelColor(source, x, y);
		float r = ColorUtil.redFromColorInt(pixelColor) / 255f;
		float g = ColorUtil.greenFromColorInt(pixelColor) / 255f;
		float b = ColorUtil.blueFromColorInt(pixelColor) / 255f;
		setColorCompare(r, g, b);
	}
	
	public void setColorCompare(float r, float g, float b) {
		colorDistanceFilter.set("colorCompare", r, g, b);
		colorCompare = P.p.color(r * 255f, g * 255f, b * 255f);
	}
	
	public void update(PImage newFrame) {
		int analyzeStart = P.p.millis();
		
		// copy webcam to current buffer
		ImageUtil.copyImage(newFrame, source);
		SaturationFilter.instance(P.p).setSaturation(2f);
		SaturationFilter.instance(P.p).applyTo(source);

		// run color distance shader and post-process to map color closeness to white
		// should this use a shader posterize effect?
		ImageUtil.copyImage(source, analysisBuffer);
		analysisBuffer.filter(colorDistanceFilter);
		InvertFilter.instance(P.p).applyTo(analysisBuffer);
		ThresholdFilter.instance(P.p).setCutoff(colorClosenessThreshold);
		ThresholdFilter.instance(P.p).applyTo(analysisBuffer);
		// ErosionFilter.instance(P.p).applyTo(bufferOutput);
		
		// loop through pixels
		totalChecked = 0;
		totalCounted = 0;
		float totalX = 0;
		float totalY = 0;
		analysisBuffer.loadPixels();
		for (int x = 0; x < analysisBuffer.width; x++) {
			for (int y = 0; y < analysisBuffer.height; y++) {
				int pixelColor = ImageUtil.getPixelColor(analysisBuffer, x, y);
				float r = ColorUtil.redFromColorInt(pixelColor);
				if(r > 127) {
					totalCounted++;
					totalX += x;
					totalY += y;
				}
				totalChecked++;
			}
		}
		
		// calc normalized center of mass / position
		if(totalCounted > minPointsThreshold) {
			float avgX = totalX / totalCounted;
			float avgY = totalY / totalCounted;
			x.setTarget(avgX / analysisBuffer.width);
			y.setTarget(avgY / analysisBuffer.height);
		}
		
		// lerp normalized output
		x.update();
		y.update();
		
		// draw debug output
		if(debugging) {
			analysisBuffer.beginDraw();
			PG.setDrawCenter(analysisBuffer);
			analysisBuffer.fill(0, 255, 0);
			analysisBuffer.noStroke();
			analysisBuffer.ellipse(x.value() * analysisBuffer.width, y.value() * analysisBuffer.height, 10, 10);
			analysisBuffer.endDraw();

			DebugView.setValue("BufferColorObjectDetection time", (P.p.millis() - analyzeStart)+"ms");
			DebugView.setValue("BufferColorObjectDetection totalChecked", totalChecked);
		}
	}
}

