package com.haxademic.app.mirrors;

import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pgraphics.MotionFire;
import com.haxademic.core.draw.filters.pgraphics.shared.BaseVideoFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.hardware.webcam.WebCam.IWebCamCallback;

import processing.core.PGraphics;
import processing.core.PImage;

public class MagicMirrors 
extends PAppletHax
implements IWebCamCallback {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected float w = 1280;
	protected float h = 720;
	protected BaseVideoFilter vfxPre;
	protected BaseVideoFilter vfx;
	
	protected int webcamW = (int)w;//1920;
	protected int webcamH = (int)h;//1080;
	protected PGraphics webcamBuffer;

	protected void config() {
		Config.setProperty(AppSettings.WIDTH, (int) w);
		Config.setProperty(AppSettings.HEIGHT, (int) h);
		Config.setProperty(AppSettings.FULLSCREEN, false);
		Config.setProperty(AppSettings.WIDTH, 1280);
		Config.setProperty(AppSettings.HEIGHT, 720);
		Config.setProperty(AppSettings.ALWAYS_ON_TOP, false);
		Config.setProperty(AppSettings.APP_NAME, "MagicMirrors");
	}

	protected void firstFrame() {
		WebCam.instance().setDelegate(this);
		webcamBuffer = PG.newPG2DFast(webcamW, webcamH);
		
//		vfx = new GPUParticlesSheetDisplacer(p.width, p.height, 0.5f);
//		vfx = new PixelTriFilter(p.width, p.height, 20);
//		vfx = new ColorDiff8BitRows(p.width, p.height, 20);
//		vfx = new TileRepeat(p.width, p.height);
//		vfx = new BlobLinesFeedback(p.width, p.height);
//		vfx = new HalftoneCamo(p.width, p.height);
//		vfx = new RadialHistory(p.width, p.height);
//		vfx = new UVGridOpticalFlow(p.width, p.height);
//		vfx = new ColorGradientTrail(p.width, p.height);
		vfx = new MotionFire(p.width, p.height);
//		vfx = new GPUParticlesLauncher(p.width, p.height);
//		vfx = new SmokeFeedback(p.width, p.height);
	}

	protected void drawApp() {
		background(0);
		if(vfxPre != null) {
			vfxPre.update();
			vfx.newFrame(vfxPre.image());
			vfx.update();
		} else {
			vfx.update();
		}
		
		// bloom
		pg.beginDraw();
		pg.image(vfx.image(), 0, 0);
		pg.endDraw();
//		BloomFilter.instance(p).setStrength(1f);
//		BloomFilter.instance(p).applyTo(pg);
		
		// draw to screen
		p.image(pg, 0, 0);
	}

	@Override
	public void newFrame(PImage frame) {
		// copy webcam and create motion detection at size of cropped webcam (and downscaling)
		ImageUtil.cropFillCopyImage(frame, webcamBuffer, true);
		ImageUtil.flipH(webcamBuffer);
		
		if(vfxPre != null) {
			// chain it up
			vfxPre.newFrame(webcamBuffer);
		} else {
			// send new webcam mirror frame to vfx
			vfx.newFrame(webcamBuffer);
		}
	}

		
}

