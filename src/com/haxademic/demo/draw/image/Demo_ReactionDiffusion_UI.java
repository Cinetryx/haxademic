package com.haxademic.demo.draw.image;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.draw.filters.pshader.BlendTowardsTexture;
import com.haxademic.core.draw.filters.pshader.BlurHFilter;
import com.haxademic.core.draw.filters.pshader.BlurVFilter;
import com.haxademic.core.draw.filters.pshader.BrightnessStepFilter;
import com.haxademic.core.draw.filters.pshader.ColorizeTwoColorsFilter;
import com.haxademic.core.draw.filters.pshader.FXAAFilter;
import com.haxademic.core.draw.filters.pshader.FakeLightingFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackMapFilter;
import com.haxademic.core.draw.filters.pshader.FeedbackRadialFilter;
import com.haxademic.core.draw.filters.pshader.RotateFilter;
import com.haxademic.core.draw.filters.pshader.SaturationFilter;
import com.haxademic.core.draw.filters.pshader.SharpenFilter;
import com.haxademic.core.draw.filters.pshader.ThresholdFilter;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.draw.textures.SimplexNoiseTexture;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.keyboard.KeyboardState;
import com.haxademic.core.hardware.midi.MidiDevice;
import com.haxademic.core.hardware.midi.devices.LaunchControl;
import com.haxademic.core.hardware.webcam.WebCam;
import com.haxademic.core.render.FrameLoop;
import com.haxademic.core.ui.UI;

import processing.core.PGraphics;
import processing.opengl.PShader;

public class Demo_ReactionDiffusion_UI 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	// TODO:
	// - Test audio looping & pitch shifting in Beads (a la Communichords, but with audio FFT data)
	//   - Draw 512x128 texture and blur it to use as displacement map
	//   - Note frequencies: https://pages.mtu.edu/~suits/notefreqs.html
	// - Add noise wavy shader in addition to the basic wavy sin() lines
	//   - More lines shaders in general - should have a number to switch between
	//     - Checkerboard
	//     - Add uniform controls for all b&w patterns
	//   - Add a slider to switch between b&w patterns
	// - Find parameters & make a nice collection of them
	// - Figure out performance issues
	// 	 - FXAA & R/D shaders are slow. Is this because of kernel processing?
	//   - Make a more efficient blur for this app, w/fewer lookups
	//   - Blur values above 1 seem to trigger the broken R/D state with fine lines
	// - Other draw styles: each panel with a different pattern rotated. 2 layers on top like DAM moire piece. 
	// - Try 32-bit textures for smoothness? Only matters on main pg and any shaders that use maps?
	// - Auto-detect blank/black screen & re-seed
	// - B&W color-cycling shader for pattern textures
	// - Darken amp could also be a texture instead of uniform/solid black 
	
	// app
	protected boolean clearScreen = true;
	protected boolean seedQueue = false;
	protected PGraphics pgPost;

	// texture to mix
	protected PGraphics linesTexture;
	protected PShader gradientShader;

	// feedback map
	protected PGraphics map;
	protected SimplexNoiseTexture simplexNoise;
	
	// UI
	protected String FEEDBACK_AMP = "FEEDBACK_AMP";
	protected String FEEDBACK_ROTATE = "FEEDBACK_ROTATE";
	protected String FEEDBACK_OFFSET_X = "FEEDBACK_OFFSET_X";
	protected String FEEDBACK_OFFSET_Y = "FEEDBACK_OFFSET_Y";
	
	protected String mapZoom = "mapZoom";
	protected String mapRot = "mapRot";
	
	protected String feedbackAmp = "feedbackAmp";
	protected String feedbackBrightStep = "feedbackBrightStep";
	protected String feedbackAlphaStep = "feedbackAlphaStep";
	protected String feedbackRadiansStart = "feedbackRadiansStart";
	protected String feedbackRadiansRange = "feedbackRadiansRange";
	protected String FEEDBACK_ITERS = "FEEDBACK_ITERS";
	
	protected String feedbackRadialAmp = "feedbackRadialAmp";
	protected String feedbackMultX = "feedbackMultX";
	protected String feedbackMultY = "feedbackMultY";
	protected String feedbackWaveAmp = "feedbackWaveAmp";
	protected String feedbackWaveFreq = "feedbackWaveFreq";
	protected String feedbackWaveStartMult = "feedbackWaveStartMult";
	
	protected String DARKEN_AMP = "DARKEN_AMP";
	protected String RD_ITERATIONS = "RD_ITERATIONS";
	protected String RD_BLUR_AMP_X = "RD_BLUR_AMP_X";
//	protected String RD_BLUR_AMP_MAP_X = "RD_BLUR_AMP_MAP_X";
	protected String RD_BLUR_AMP_Y = "RD_BLUR_AMP_Y";
//	protected String RD_BLUR_AMP_MAP_Y = "RD_BLUR_AMP_MAP_Y";
	protected String RD_SHARPEN_AMP = "RD_SHARPEN_AMP";
//	protected String RD_SHARPEN_MAP_AMP = "RD_SHARPEN_MAP_AMP";
//	protected String RD_SHARPEN_MAP_MIN_AMP = "RD_SHARPEN_MAP_MIN_AMP";
	
	protected String TEXTURE_BLEND = "TEXTURE_BLEND";
	
	protected String FAKE_LIGHT_AMBIENT = "FAKE_LIGHT_AMBIENT";
	protected String FAKE_LIGHT_GRAD_AMP = "FAKE_LIGHT_GRAD_AMP";
	protected String FAKE_LIGHT_GRAD_BLUR = "FAKE_LIGHT_GRAD_BLUR";
	protected String FAKE_LIGHT_SPEC_AMP = "FAKE_LIGHT_SPEC_AMP";
	protected String FAKE_LIGHT_DIFF_DARK = "FAKE_LIGHT_DIFF_DARK";

	protected String FXAA_ACTIVE = "FXAA_ACTIVE";
	

	protected void config() {
		Config.setAppSize(1280, 720);
		Config.setPgSize(1920, 1080);
		Config.setProperty(AppSettings.FULLSCREEN, true);
		Config.setProperty(AppSettings.LOOP_FRAMES, 2000);
		Config.setProperty(AppSettings.SHOW_UI, true);
		Config.setProperty(AppSettings.ALWAYS_ON_TOP, false);
		Config.setProperty(AppSettings.SHOW_FPS_IN_TITLE, true);
	}
	
	/////////////////////////
	// INIT
	/////////////////////////
	
	protected void firstFrame() {
		// init midi controls
		MidiDevice.init(0, 3);
		KeyboardState.instance().updatesDebugView(false);
		
		// main buffer & postFX buffer
//		pg = PG.newPG32(pg.width, pg.height, true, false);
		pgPost = PG.newPG(pg.width, pg.height);
//		PG.setTextureRepeat(pg, true);
		PG.setTextureRepeat(pgPost, true);
		
		// feedback map
		map = PG.newPG(pg.width/8, pg.height/8);
		simplexNoise = new SimplexNoiseTexture(128, 128);
		
		// lines texture
		linesTexture = PG.newPG(pg.width/2, pg.height/2, false, false);
		gradientShader = p.loadShader(FileUtil.getPath("haxademic/shaders/textures/cacheflowe-two-color-repeating-gradient.glsl"));

		buildUI();
		
		// debug
		DebugView.setTexture("map", map);
		DebugView.setTexture("lines", linesTexture);
	}
	
	protected void buildUI() {
		UI.addTitle("Feedback (Zoom/Rotate)");
		UI.addSlider(FEEDBACK_AMP, 1, 0.99f, 1.01f, 0.0001f, true, LaunchControl.KNOB_01);
		UI.addSlider(FEEDBACK_ROTATE, 0, -0.005f, 0.005f, 0.00005f, true, LaunchControl.KNOB_02);
		UI.addSlider(FEEDBACK_OFFSET_X, 0, -0.005f, 0.005f, 0.00005f, true, LaunchControl.KNOB_03);
		UI.addSlider(FEEDBACK_OFFSET_Y, 0, -0.005f, 0.005f, 0.00005f, true, LaunchControl.KNOB_04);
		
		UI.addTitle("Feedback (Map)");
		UI.addSlider(mapZoom, 2, 0.1f, 15, 0.1f, true, LaunchControl.KNOB_05);
		UI.addSlider(mapRot, 0, 0, P.TWO_PI, 0.01f, true, LaunchControl.KNOB_06);
		UI.addSlider(feedbackAmp, 0f, 0f, 0.005f, 0.00001f, true, LaunchControl.KNOB_07);
//		UI.addSlider(feedbackBrightStep, 0f, -0.01f, 0.01f, 0.0001f, true);
//		UI.addSlider(feedbackAlphaStep, 0f, -0.01f, 0.01f, 0.0001f, true);
		UI.addSlider(feedbackRadiansStart, 0f, 0, P.TWO_PI, 0.01f, true);
		UI.addSlider(feedbackRadiansRange, P.TWO_PI * 2f, -P.TWO_PI * 2f, P.TWO_PI * 2f, 0.1f, true, LaunchControl.KNOB_08);
		UI.addSlider(FEEDBACK_ITERS, 1, 0, 10, 1f, true);
		
		UI.addTitle("Feedback (Radial)");
		UI.addSlider(feedbackRadialAmp, 0.0f, -0.005f, 0.005f, 0.00001f, false);
		UI.addSlider(feedbackMultX, 1f, 0f, 1f, 0.001f, false);
		UI.addSlider(feedbackMultY, 1f, 0f, 1f, 0.001f, false);
		UI.addSlider(feedbackWaveAmp, 0.1f, 0f, 1f, 0.001f, false);
		UI.addSlider(feedbackWaveFreq, 10f, 0f, 100f, 0.1f, false);
		UI.addSlider(feedbackWaveStartMult, 0f, -0.002f, 0.002f, 0.00001f, false);

		UI.addTitle("Reaction/Diffusion");
//		UI.addSlider(map2Zoom, 3, 0.1f, 15, 0.1f, false);
//		UI.addSlider(map2Rot, 2, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(RD_ITERATIONS, 0, 0, 10, 1f);
		UI.addSlider(RD_BLUR_AMP_X, 0, 0, 6, 0.01f, true, LaunchControl.KNOB_09);
//		UI.addSlider(RD_BLUR_AMP_MAP_X, 0, 0, 6, 0.01f);
		UI.addSlider(RD_BLUR_AMP_Y, 0, 0, 6, 0.01f, true, LaunchControl.KNOB_10);
//		UI.addSlider(RD_BLUR_AMP_MAP_Y, 0, 0, 6, 0.01f);
		UI.addSlider(RD_SHARPEN_AMP, 0, 0, 20, 0.01f, true, LaunchControl.KNOB_11);
//		UI.addSlider(RD_SHARPEN_MAP_AMP, 3, 0, 20, 0.01f);
//		UI.addSlider(RD_SHARPEN_MAP_MIN_AMP, 1f, 0, 20, 0.01f);
		UI.addSlider(DARKEN_AMP, 0, -255, 255, 1f, true, LaunchControl.KNOB_12);
		
		UI.addTitle("Texture Blend");
		UI.addSlider(TEXTURE_BLEND, 0.5f, 0f, 1f, 0.01f, false, LaunchControl.KNOB_13);
		
		UI.addTitle("Fake Light Post FX");
		UI.addSlider(FAKE_LIGHT_AMBIENT, 2f, 0.3f, 6f, 0.01f);
		UI.addSlider(FAKE_LIGHT_GRAD_AMP, 0.66f, 0.1f, 6f, 0.01f);
		UI.addSlider(FAKE_LIGHT_GRAD_BLUR, 1f, 0.1f, 6f, 0.01f);
		UI.addSlider(FAKE_LIGHT_SPEC_AMP, 2.25f, 0.1f, 6f, 0.01f);
		UI.addSlider(FAKE_LIGHT_DIFF_DARK, 0.85f, 0.1f, 2f, 0.01f);
		
		UI.addTitle("More Post FX");
		UI.addSlider(FXAA_ACTIVE, 1, 0, 1, 1);

	}
	
	/////////////////////////
	// DRAW
	/////////////////////////
	
	protected void darkenCanvas() {
		if(UI.valueInt(RD_ITERATIONS) > 0 && UI.valueEased(DARKEN_AMP) != 0) {
			BrightnessStepFilter.instance(p).setBrightnessStep(UI.valueEased(DARKEN_AMP)/255f);
			BrightnessStepFilter.instance(p).applyTo(pg);
		}
	}

	protected void setFakeLighting() {
		FakeLightingFilter.instance(p).setAmbient(UI.value(FAKE_LIGHT_AMBIENT));
		FakeLightingFilter.instance(p).setGradAmp(UI.value(FAKE_LIGHT_GRAD_AMP));
		FakeLightingFilter.instance(p).setGradBlur(UI.value(FAKE_LIGHT_GRAD_BLUR));
		FakeLightingFilter.instance(p).setSpecAmp(UI.value(FAKE_LIGHT_SPEC_AMP));
		FakeLightingFilter.instance(p).setDiffDark(UI.value(FAKE_LIGHT_DIFF_DARK));
		FakeLightingFilter.instance(p).applyTo(pgPost);
	}
	
	protected void setColorize() {
		ColorizeTwoColorsFilter.instance(p).setColor1(1f,  0.7f,  1f);
		ColorizeTwoColorsFilter.instance(p).setColor2(0f,  0f,  0f);
		ColorizeTwoColorsFilter.instance(p).applyTo(pgPost);
	}
	
	
	protected void applyZoomRotate() {
		RotateFilter.instance(p).setRotation(UI.valueEased(FEEDBACK_ROTATE));
		RotateFilter.instance(p).setZoom(UI.valueEased(FEEDBACK_AMP));
		RotateFilter.instance(p).setOffset(UI.valueEased(FEEDBACK_OFFSET_X), UI.valueEased(FEEDBACK_OFFSET_Y));
		RotateFilter.instance(p).applyTo(pg);
	}
	
	protected void applyRD() {
		for (int i = 0; i < UI.valueInt(RD_ITERATIONS); i++) {
			BlurHFilter.instance(p).setBlurByPercent(UI.valueEased(RD_BLUR_AMP_X), pg.width);
			BlurHFilter.instance(p).applyTo(pg);
			BlurVFilter.instance(p).setBlurByPercent(UI.valueEased(RD_BLUR_AMP_Y), pg.height);
			BlurVFilter.instance(p).applyTo(pg);
			
			SharpenFilter.instance(p).setSharpness(UI.valueEased(RD_SHARPEN_AMP));
			SharpenFilter.instance(p).applyTo(pg);

//			BlurHMapFilter.instance(p).setMap(map2);
//			BlurHMapFilter.instance(p).setBlurByPercent(UI.valueEased(RD_BLUR_AMP_MAP_X), pg.width);
//			BlurHMapFilter.instance(p).applyTo(pg);
//			BlurVMapFilter.instance(p).setMap(map2);
//			BlurVMapFilter.instance(p).setBlurByPercent(UI.valueEased(RD_BLUR_AMP_MAP_Y), pg.width);
//			BlurVMapFilter.instance(p).applyTo(pg);
			
//			SharpenMapFilter.instance(p).setMap(map);
//			SharpenMapFilter.instance(p).setSharpnessMax(UI.valueEased(RD_SHARPEN_MAP_AMP));
//			SharpenMapFilter.instance(p).setSharpnessMin(UI.valueEased(RD_SHARPEN_MAP_MIN_AMP));
//			SharpenMapFilter.instance(p).applyTo(pg);
		}
		ThresholdFilter.instance(p).applyTo(pg);
	}
	
	protected void updateFeedbackMapNoise() {
		simplexNoise.update(UI.valueEased(mapZoom), UI.valueEased(mapRot), 0, 0);
		ImageUtil.cropFillCopyImage(simplexNoise.texture(), map, true);
//		simplexNoise2.update(UI.valueEased(map2Zoom), UI.valueEased(map2Rot), 0, 0);
//		ImageUtil.cropFillCopyImage(simplexNoise2.texture(), map2, true);
	}

	protected void applyMapFeedback() {
		FeedbackMapFilter.instance(p).setMap(map);
		FeedbackMapFilter.instance(p).setAmp(UI.valueEased(feedbackAmp));
		FeedbackMapFilter.instance(p).setBrightnessStep(0);//UI.valueEased(feedbackBrightStep));
		FeedbackMapFilter.instance(p).setAlphaStep(0);//UI.valueEased(feedbackAlphaStep));
		FeedbackMapFilter.instance(p).setRadiansStart(UI.valueEased(feedbackRadiansStart));
		FeedbackMapFilter.instance(p).setRadiansRange(UI.valueEased(feedbackRadiansRange));
		for (int i = 0; i < UI.valueInt(FEEDBACK_ITERS); i++) FeedbackMapFilter.instance(p).applyTo(pg);
		
		// blur & threshold if R/D isn't going to do that for us
		if(UI.valueInt(RD_ITERATIONS) == 0) {
			BlurHFilter.instance(p).setBlurByPercent(UI.valueEased(RD_BLUR_AMP_X), pg.width);
			BlurHFilter.instance(p).applyTo(pg);
			BlurVFilter.instance(p).setBlurByPercent(UI.valueEased(RD_BLUR_AMP_Y), pg.height);
			BlurVFilter.instance(p).applyTo(pg);
			// does a similar thing to R/D
			ThresholdFilter.instance(p).applyTo(pg);
		}
	}
	
	protected void applyRadialFeedback() {
		FeedbackRadialFilter.instance(P.p).setAmp(UI.value(feedbackRadialAmp));
		FeedbackRadialFilter.instance(P.p).setMultX(UI.value(feedbackMultX));
		FeedbackRadialFilter.instance(P.p).setMultY(UI.value(feedbackMultY));
//		FeedbackRadialFilter.instance(P.p).setSampleMult(UI.value(feedbackBrightMult));
		FeedbackRadialFilter.instance(P.p).setWaveAmp(UI.value(feedbackWaveAmp));
		FeedbackRadialFilter.instance(P.p).setWaveFreq(UI.value(feedbackWaveFreq));
		FeedbackRadialFilter.instance(P.p).setWaveStart(p.frameCount * UI.value(feedbackWaveStartMult));
//		FeedbackRadialFilter.instance(P.p).setAlphaMult(UI.value(feedbackAlphaMult));
		FeedbackRadialFilter.instance(P.p).applyTo(pg);
	}
	
	protected void addBitmapSeed() {
//		if(p.frameCount % 200 == 0) {
		if(seedQueue) {
			// blend webcam
	//		pg.blendMode(PBlendModes.SCREEN);
	//		WebCam.instance().image();
			ImageUtil.drawImageCropFill(WebCam.instance().image(), pg, true);
			pg.blendMode(PBlendModes.BLEND);
			
			// desaturate completely
			SaturationFilter.instance(p).setSaturation(0);
			SaturationFilter.instance(p).applyTo(pg);
		}
		seedQueue = false;
	}
	
	protected void mixTexture() {
		if(UI.valueEased(TEXTURE_BLEND) == 0) return;
		BlendTowardsTexture.instance(p).setSourceTexture(linesTexture);
//		BlendTowardsTexture.instance(p).setSourceTexture(map);
		BlendTowardsTexture.instance(p).setBlendLerp(UI.valueEased(TEXTURE_BLEND));
		BlendTowardsTexture.instance(p).applyTo(pg);
	}
	
	protected void updateLinesTexture() {
		if(UI.valueEased(TEXTURE_BLEND) == 0) return;
		// update other shader properties
		gradientShader.set("color1", 0f, 0f, 0f);
		gradientShader.set("color2", 1f, 1f, 1f);
		gradientShader.set("zoom", 40f + 20f * P.sin(FrameLoop.progressRads()));
		gradientShader.set("scrollY", FrameLoop.progress() * 4f);
		gradientShader.set("oscFreq", P.PI * 8f);
		gradientShader.set("oscAmp", 0.02f + 0.02f * P.sin(FrameLoop.progressRads()));
		gradientShader.set("fade", 0.1f);
		gradientShader.set("rotate", 0.15f);//AnimationLoop.progressRads());
		linesTexture.filter(gradientShader);
	}
	
	protected void drawApp() {
		p.background(0);
		
		// pre-draw
		updateFeedbackMapNoise();
		updateLinesTexture();
		
		// set context
		pg.beginDraw();
		if(FrameLoop.count() <= 10 || clearScreen) pg.background(0);
		clearScreen = false;
		PG.setDrawCorner(pg);
		PG.setDrawFlat2d(pg, true);
		pg.endDraw();
		
		// fx steps
		addBitmapSeed();
		mixTexture();
		darkenCanvas();
		applyZoomRotate();
		applyRadialFeedback();
		applyMapFeedback();
		applyRD();
		
		// close context
//		pg.endDraw();
		
		// copy to postFX buffer
		ImageUtil.copyImage(pg, pgPost);		// copy to 2nd buffer for postprocessing
//		setColorize();
		if(UI.valueInt(FXAA_ACTIVE) == 1) FXAAFilter.instance(p).applyTo(pgPost);
		setFakeLighting();
		
		// draw post to screen
		ImageUtil.cropFillCopyImage(pgPost, p.g, false);
		
		P.store.showStoreValuesInDebugView();
	}
		
	public void keyPressed() {
		super.keyPressed();
		if(p.key == 'z') P.out(UI.valuesToJSON());
		if(p.key == ' ') clearScreen = true;
		if(p.key == 'c') seedQueue = true;
//		if(p.key == '1') UI/loadJSON(JSONObject.parse(CONFIG_1));
	}
	
}