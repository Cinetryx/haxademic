package com.haxademic.core.media;

public class MediaTimecodeTrigger {

	public interface IMediaTimecodeTriggerDelegate {
		public void mediaTimecodeTriggered(String mediaId, float time, String action);
	}
	
	protected boolean active = false;
	protected String mediaId;
	protected float triggerTime;
	protected String action;
	protected IMediaTimecodeTriggerDelegate delegate;
	protected float pastThresh = 0.3f;
	
	public MediaTimecodeTrigger(String mediaId, float time, String action, IMediaTimecodeTriggerDelegate delegate) {
		this.mediaId = mediaId;
		this.triggerTime = time;
		this.action = action;
		this.delegate = delegate;
	}
	
	public void update(String curMediaId, float curTimeSeconds) {
		if(curMediaId.equals(mediaId)) {
			if(active == false && curTimeSeconds >= triggerTime && curTimeSeconds < triggerTime + pastThresh) {
				active = true;
				delegate.mediaTimecodeTriggered(mediaId, triggerTime, action);
			} else if(active == true && (curTimeSeconds < triggerTime || curTimeSeconds > triggerTime + pastThresh)) {
				active = false;
			}
		}
	}
}