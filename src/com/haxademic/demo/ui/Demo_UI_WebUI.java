package com.haxademic.demo.ui;

import com.haxademic.core.app.P;
import com.haxademic.core.app.PAppletHax;
import com.haxademic.core.app.config.AppSettings;
import com.haxademic.core.app.config.Config;
import com.haxademic.core.draw.context.PG;
import com.haxademic.core.net.WebServer;
import com.haxademic.core.system.SystemUtil;
import com.haxademic.core.ui.UI;
import com.haxademic.core.ui.UIButton;

public class Demo_UI_WebUI 
extends PAppletHax {
	public static void main(String args[]) { arguments = args; PAppletHax.main(Thread.currentThread().getStackTrace()[1].getClassName()); }
	
	protected String R = "R";
	protected String G = "G";
	protected String B = "B";
	
	protected String VECTOR_3 = "VECTOR_3";
	
	protected void config() {
		Config.setProperty(AppSettings.SHOW_UI, true);
	}
	
	protected void firstFrame () {
		UI.addSlider(R, 255, 0, 255, 0.5f);
		UI.addSlider(G, 255, 0, 255, 0.5f);
		UI.addSlider(B, 255, 0, 255, 0.5f);
		UI.addSliderVector(VECTOR_3, 0, -1f, 1f, 0.001f, false);
		UI.addButton("Button", false);
		UI.addButton("Button 2", true);
		UI.addButtons(new String[] {"1", "2", "3", "4"}, true);
		for (int i = 0; i < 30; i++) UI.addSlider("Test slider " + i, 255, 0, 255, 0.5f, false);
		UI.addWebInterface(false);
		P.out(UI.configToJSON());
		P.out(UI.valuesToJSON());
	}
	
	protected void drawApp() {
		if(p.frameCount == 200) SystemUtil.openWebPage(WebServer.getServerAddress() + "ui/");

		// bg components
		p.background(
			UI.value(R),
			UI.value(G),
			UI.value(B)
		);
		
		// 3d rotation
		p.lights();
		PG.setCenterScreen(p.g);
		PG.setDrawCenter(p.g);
		p.rotateX(UI.valueX(VECTOR_3));
		p.rotateY(UI.valueY(VECTOR_3));
		p.rotateZ(UI.valueZ(VECTOR_3));
		p.fill(255);
		p.stroke(0);
		p.box(200);
	}
	
	public void uiButtonClicked(UIButton button) {
		P.out(button.id(), button.value());
	}
}
