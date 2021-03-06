package com.haxademic.core.hardware.depthcamera;

import com.haxademic.core.app.P;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.depthcamera.cameras.IDepthCamera;

import processing.core.PGraphics;

public class KinectDepthSilhouetteSmoothed {

	protected IDepthCamera kinectWrapper;
	protected int pixelSkip = 5;
	protected int pixelsActive = 0;
	public static int KINECT_NEAR = 500;
	public static int KINECT_FAR = 1800;

	protected PGraphics depthBuffer;
	protected PGraphics avgBuffer;
	protected PGraphics postBuffer;

	public KinectDepthSilhouetteSmoothed(IDepthCamera kinectWrapper, int pixelSkip) {
		this.kinectWrapper = kinectWrapper;
		this.pixelSkip = pixelSkip;
		
		depthBuffer = P.p.createGraphics(DepthCameraSize.WIDTH / pixelSkip, DepthCameraSize.HEIGHT / pixelSkip, PRenderers.P3D);
		avgBuffer = P.p.createGraphics(DepthCameraSize.WIDTH / pixelSkip, DepthCameraSize.HEIGHT / pixelSkip, PRenderers.P3D);
		postBuffer = P.p.createGraphics(DepthCameraSize.WIDTH / pixelSkip, DepthCameraSize.HEIGHT / pixelSkip, PRenderers.P3D);
	}
	
	public int pixelsActive() {
		return pixelsActive;
	}
	
	public PGraphics depthBuffer() {
		return depthBuffer;
	}
	
	public PGraphics avgBuffer() {
		return avgBuffer;
	}
	
	public PGraphics image() {
		return postBuffer;
	}
	
	public void update() {
		// draw current depth to buffer
		depthBuffer.beginDraw();
		depthBuffer.noStroke();
		depthBuffer.background(0);
		depthBuffer.fill(255);
		float pixelDepth;
		pixelsActive = 0;
		for ( int x = 0; x < depthBuffer.width; x++ ) {
			for ( int y = 0; y < depthBuffer.height; y++ ) {
				pixelDepth = kinectWrapper.getDepthAt( x * pixelSkip, y * pixelSkip );
				if( pixelDepth != 0 && pixelDepth > KINECT_NEAR && pixelDepth < KINECT_FAR ) {
					depthBuffer.pushMatrix();
					depthBuffer.rect(x, y, 1, 1);
					depthBuffer.popMatrix();
					pixelsActive++;
				}
			}
		}
		depthBuffer.endDraw();
		
		// lerp texture
		BlendTowardsTexture.instance(P.p).setBlendLerp(0.25f);
		BlendTowardsTexture.instance(P.p).setSourceTexture(depthBuffer);
		BlendTowardsTexture.instance(P.p).applyTo(avgBuffer);

		// blur averaged buffer		
		BlurHFilter.instance(P.p).setBlurByPercent(0.25f, avgBuffer.width);
		BlurHFilter.instance(P.p).applyTo(avgBuffer);
		BlurVFilter.instance(P.p).setBlurByPercent(0.25f, avgBuffer.height);
		BlurVFilter.instance(P.p).applyTo(avgBuffer);
		
		// clean up post copy
		ImageUtil.copyImage(avgBuffer, postBuffer);
		BrightnessFilter.instance(P.p).setBrightness(1.25f);
		BrightnessFilter.instance(P.p).applyTo(postBuffer);
		ThresholdFilter.instance(P.p).setCutoff(0.4f);
		ThresholdFilter.instance(P.p).applyTo(postBuffer);
	}
}
