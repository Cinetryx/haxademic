package com.haxademic.sketch.texture;

import java.util.ArrayList;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.data.constants.PBlendModes;
import com.haxademic.core.data.constants.PRenderers;
import com.haxademic.core.debug.DebugView;
import com.haxademic.core.draw.image.ImageUtil;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.hardware.mouse.Mouse;
import com.haxademic.core.hardware.webcam.WebCam;

import processing.core.PGraphics;
import processing.core.PImage;

public class TextureBlendStack
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
		
	protected PGraphics camerBuffer;
	protected boolean cameraInit = false;
	protected ArrayList<TextureBlendLayer> layers;
	protected ArrayList<TextureBlendLayer> layersRecycled;
	protected ArrayList<PImage> images;
	
	protected int blendIndex = 0;
	protected int[] blendModes = new int[] {
		PBlendModes.BLEND,
		PBlendModes.ADD,
		PBlendModes.DARKEST,
		PBlendModes.DIFFERENCE,
		PBlendModes.EXCLUSION,
		PBlendModes.LIGHTEST,
		PBlendModes.MULTIPLY,
//		PBlendModes.REPLACE,
		PBlendModes.SCREEN,
		PBlendModes.SUBTRACT,
	};
	protected String[] blendNames = new String[] {
		"BLEND",
		"ADD",
		"DARKEST",
		"DIFFERENCE",
		"EXCLUSION",
		"LIGHTEST",
		"MULTIPLY",
//		"REPLACE",
		"SCREEN",
		"SUBTRACT",
	};

	protected void config() {
		Config.setProperty( AppSettings.WIDTH, 1000 );
		Config.setProperty( AppSettings.HEIGHT, 600 );
	}

	protected void firstFrame() {
		camerBuffer = p.createGraphics(640, 480, PRenderers.P2D);
		layers = new ArrayList<TextureBlendLayer>();
		layers.add(new TextureBlendLayer());
		layersRecycled = new ArrayList<TextureBlendLayer>();
		loadImages();
	}
	
	protected void loadImages() {
		images = new ArrayList<PImage>();
		ArrayList<String> filenames = FileUtil.getFilesInDirOfTypes(FileUtil.getPath("images/_sketch/texture-blender"), "jpg,png");
		for (int i = 0; i < filenames.size(); i++) {
			P.println("loading: ", filenames.get(i));
			images.add(p.loadImage(filenames.get(i)));
		}
	}
	
	protected void drawApp() {
		p.background(255);
		p.noStroke();
		p.blendMode(PBlendModes.BLEND);
	
		// copy webcam (should be done in Webcam callback)
		if(WebCam.instance().image().width > 40) {
			ImageUtil.copyImage(WebCam.instance().image(), camerBuffer);
			if(cameraInit == false) {
				images.add(0, camerBuffer);
				cameraInit = true;
			}
		}
		 
		// calc selected image from slider
		int activeImageIndex = P.floor(Mouse.yNorm * images.size() - 1);
		activeImageIndex = P.constrain(activeImageIndex, 0, images.size() - 1);
		PImage activeImage = images.get(activeImageIndex);
		
		// set blend mode
		blendIndex = P.round(Mouse.xNorm * blendModes.length - 1);
		blendIndex = P.constrain(blendIndex, 0, blendModes.length - 1);
		
		// set selected image & params into top layer
		layers.get(layers.size() - 1).blendMode(blendIndex);
		layers.get(layers.size() - 1).image(activeImage);
		
		// draw layers
		p.fill(255);
		for (int i = 0; i < layers.size(); i++) {
			if(layers.get(i).image() != null) {
				p.blendMode(layers.get(i).blendMode());
				ImageUtil.drawImageCropFill(layers.get(i).image(), p.g, true);
			}
		}
		p.blendMode(PBlendModes.BLEND);
		
		// draw layer stack
		int imageW = 64;
		int imageH = 48;
		for (int i = 0; i < layers.size(); i++) {
			if(layers.get(i).image() != null) {
				p.g.pushMatrix();
				// move for grid column
				p.g.translate(20, 20 + (imageH + 10) * (layers.size() - 1 - i));
				// image border
				p.fill(0);
				p.rect(-1, -1, imageW + 2, imageH + 2);
				// image
				p.fill(255);
				p.g.image(layers.get(i).image(), 0, 0, imageW, imageH);
				// text bg
				p.fill(0, 180);
				p.rect(0, imageH - 20, imageW, 20);
				// text
				p.fill(255);
				p.g.text(blendNames[layers.get(i).blendIndex()], 6, imageH - 5);
				p.g.popMatrix();
			}
		}
	}
	
	public void keyPressed() {
		super.keyPressed();
		if(p.key == ' ') {
			addLayer();
			DebugView.setValue("layers.size()", layers.size());
		}
		if(p.key == 'z') {
			if(layers.size() > 1) {
				layersRecycled.add(layers.remove(layers.size() - 1));
			}
		}
	}
	
	protected void addLayer() {
		// use recycled 
		if(layersRecycled.size() > 0) {
			TextureBlendLayer recycleLayer = layersRecycled.remove(layersRecycled.size() - 1);
			recycleLayer.clearTexture();
			layers.add(recycleLayer);
		} else {
			layers.add(new TextureBlendLayer());
		}
	}
	
	public class TextureBlendLayer {
		
		protected PGraphics buffer;
		protected int blendMode = blendModes[0];
		protected int blendIndex = 0;
		
		public TextureBlendLayer() {
			buffer = p.createGraphics(640, 480, PRenderers.P2D);
		}
		
		public PGraphics image() {
			return buffer;
		}
		
		public void image(PImage img) {
			ImageUtil.cropFillCopyImage(img, buffer, true);
		}
		
		public void clearTexture() {
			buffer.beginDraw();
			buffer.clear();
			buffer.endDraw();
		}
		
		public int blendIndex() {
			return blendIndex;
		}
		
		public int blendMode() {
			return blendModes[blendIndex];
		}
		
		public void blendMode(int blendIndex) {
			this.blendIndex = blendIndex;
		}
		
	}

}
