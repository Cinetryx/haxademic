package com.haxademic.sketch.three_d;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.draw.shapes.BoxBetween;
import com.haxademic.core.render.JoonsWrapper;

import processing.core.PImage;
import processing.core.PVector;
import toxi.geom.Triangle3D;
import toxi.geom.mesh.TriangleMesh;
import toxi.geom.mesh.WETriangleMesh;

public class BoxConnectJoons 
extends PAppletHax {
	public static void main(String args[]) { PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }

	protected Triangle3D tri;
	protected TriangleMesh mesh;
	protected PImage image;
	protected WETriangleMesh _mesh;

	public void setup() {
		super.setup();		
//		_mesh = MeshUtilToxi.meshFromOBJ( p, FileUtil.getHaxademicDataPath() + "models/cacheflowe-3d.obj", 16f );
	}
	
	protected void overridePropsFile() {
		p.appConfig.setProperty( AppSettings.SUNFLOW, "true" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_ACTIVE, "false" );
		p.appConfig.setProperty( AppSettings.SUNFLOW_QUALITY, "high" );
		p.appConfig.setProperty( "sunflow_save_images", "true" );
		p.appConfig.setProperty( AppSettings.WIDTH, "1280" );
		p.appConfig.setProperty( AppSettings.HEIGHT, "1280" );
		p.appConfig.setProperty( AppSettings.RENDERING_MOVIE, "false" );
	}

	public void drawApp() {
		background(0);
		lights();
		p.noStroke();
				
//		p.rotateX(5f);
		p.rotateX(-0.1f);
//		p.rotateX(mouseY*0.01f);
//		P.println(mouseY*0.01f);
//		p.rotateY(mouseX*0.01f);

		// draw room
		p.fill(0);
		// _jw.drawRoomWithSizeAndColor( width, height, JoonsWrapper.MATERIAL_SHINY, p.color( 45, 55, 45 ), 1 );
		// _jw.drawRoomSphereWithColor( JoonsWrapper.MATERIAL_SHINY, p.color( 45, 55, 45 ), 1 );
		if( joons != null ) joons.drawRoomSphereWithColor( JoonsWrapper.MATERIAL_DIFFUSE, p.color( 25, 25, 25 ), -1 );
		
		// mirror ball
		pushMatrix();
		translate(0,0,20);
		fill(255);
		sphere(12);
		popMatrix();
		// always call after drawing shapes
		if( joons != null ) joons.addColorForObject( JoonsWrapper.MATERIAL_MIRROR, -1, p.color( 200, 200, 200 ), true );

		
		// glass model
//		fill(255);
//		pushMatrix();
//		translate(0,0,20); 
//		DrawMesh.drawToxiMeshFacesNative( p, _mesh );
//		popMatrix();
//		_jw.addColorForObject( JoonsWrapper.MATERIAL_SHINY, p.color( 255, 255, 255 ), 1, false );

	
		// draw boxes

		float segments = 6;
		float radius = 60;
		float inc = P.TWO_PI / segments;
		for( float i=0; i < P.TWO_PI; i+= inc ) {
			float x = P.sin(i) * radius;
			float y = P.cos(i) * radius;
			float xNext = P.sin(i+inc) * radius;
			float yNext = P.cos(i+inc) * radius;
			
			// bars!
			p.pushMatrix();
			translate(0,0,20);
			BoxBetween.draw( p, new PVector(x, y, 0 ), new PVector( xNext, yNext, 0 ), 3 );
			p.popMatrix();
//			p.pushMatrix();
//			translate(0,0,20);
//			drawBoxBetween( new PVector(x, y, 0 ), new PVector( P.sin(i) * radius*2, P.cos(i) * radius*2, 0 ) );
//			p.popMatrix();

			// always call after drawing shapes
			if( joons != null ) joons.addColorForObject( JoonsWrapper.MATERIAL_SHINY, p.color( 200, 200, 200 ), 1, false );
			
			
			
			// joint ball
			pushMatrix();
			translate(x,y,20);
			fill(255);
			sphere(3.5f);
			popMatrix();
			// always call after drawing shapes
			if( joons != null ) joons.addColorForObject( JoonsWrapper.MATERIAL_SHINY, p.color( 0, 0, 0 ), 1, true );

			
			// glass ball
			pushMatrix();
			translate( P.sin(i) * radius * 0.66666f, P.cos(i) * radius * 0.66666f, 20);
			fill(255);
			sphere(5f);
			popMatrix();
			// always call after drawing shapes
			if( joons != null ) joons.addColorForObject( JoonsWrapper.MATERIAL_GLASS, -1, p.color( 127, 127, 127 ), true );
			
		}
	}

}
