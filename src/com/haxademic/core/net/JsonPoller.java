package com.haxademic.core.net;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

import com.haxademic.core.app.P;

public class JsonPoller 
implements IJsonRequestCallback {

	protected JsonRequest jsonRequest;
	protected IJsonRequestCallback delegate;
	
	protected int numRequests = 0;
	protected boolean isRequesting = false;
	protected int lastRequestTime = -99999;
	protected int interval;

	protected Timer timer;


	public JsonPoller(String url, int interval, IJsonRequestCallback delegate) {
		jsonRequest = new JsonRequest(url);
		this.interval = interval;
		this.delegate = delegate;
		start();
	}
	
	public int numRequests() {
		return numRequests;
	}
	
	public boolean isRequesting() {
		return isRequesting;
	}
	
	public void start() {
		timer = new Timer();
		timer.schedule(new TimerTask() { public void run() {
			requestJson();
		}}, 0, interval);	 // delay, [repeat]
	}
	
	public void stop() {
		if(timer != null) timer.cancel();
	}
	
	/////////////////////////////////////
	// JSON polling
	/////////////////////////////////////
		
	protected void requestJson() {
		try {
			jsonRequest.requestJsonData(this);
			lastRequestTime = P.p.millis();
			isRequesting = true;
			numRequests++;
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/////////////////////////////////////
	// IJsonRequestCallback interface
	/////////////////////////////////////

	public void postSuccess(String responseText, int responseCode, String requestId, int responseTime) {
		delegate.postSuccess(responseText, responseCode, requestId, responseTime);
	}


	public void postFailure(String responseText, int responseCode, String requestId, int responseTime, String errorMessage) {
		delegate.postFailure(responseText, responseCode, requestId, responseTime, errorMessage);

	}

}
