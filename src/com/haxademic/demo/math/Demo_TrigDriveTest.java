package com.haxademic.demo.math;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.ui.UI;

public class Demo_TrigDriveTest
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String radians = "radians";
	protected String speed = "speed";
	
	protected float _x = 0;
	protected float _y = 0;

	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}

	protected void firstFrame() {
		UI.addSlider(radians, 0, 0, P.TWO_PI, 0.01f, false);
		UI.addSlider(speed, 1, 0, 10, 0.1f, false);

		_x = p.width / 2;
		_y = p.height / 2;
	}

	protected void drawApp() {
		background(0);
		PG.setDrawCenter(p);

		_x += P.cos(UI.value(radians)) * UI.value(speed);
		_y += P.sin(UI.value(radians)) * UI.value(speed);
		
		if( _x > p.width ) _x = 0;
		if( _x < 0 ) _x = p.width;
		if( _y > p.height ) _y = 0;
		if( _y < 0 ) _y = p.height;

		p.pushMatrix();
		p.fill(255);
		
		p.translate(_x, _y);
		p.rotate(UI.value(radians));
		p.rect(0, 0, 40, 20);
		
		p.popMatrix();
	}

}
