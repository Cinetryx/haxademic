package com.haxademic.core.media.audio.playback;

import java.util.ArrayList;
import java.util.HashMap;

import com.haxademic.core.app.P;
import com.haxademic.core.file.FileUtil;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.interphase.Scales;

public class DroneSampler {
	
	protected String audioDir;
	protected String[] audioFiles;
	protected WavPlayer activePlayer;
	protected HashMap<String, DroneSamplerLoop> droneLoops = new HashMap<String, DroneSamplerLoop>();
	protected int soundIndex = -1;
	protected int loopInterval = 15000;
	protected int loopLastStartTime = -loopInterval;
	
	
	public DroneSampler(String audioDir, float loopIntervalSeconds) {
		this.audioDir = audioDir;
		this.loopInterval = P.round(loopIntervalSeconds * 1000); // ms
		P.out("DroneSampler loading sounds from:", this.audioDir);
		loadSounds();
	}
	
	protected void loadSounds() {
		// load audio directory
		ArrayList<String> sounds = FileUtil.getFilesInDirOfTypes(FileUtil.getPath(audioDir), "wav,aif");
		audioFiles = new String[sounds.size()];
		for (int i = 0; i < sounds.size(); i++) {
			audioFiles[i] = sounds.get(i);
			P.out("Loading...", audioFiles[i]);
		}
	}
	
	protected void startNextSound() {
		// kill old players
		killOldPlayers();
		// go to next index & play next sound!
		soundIndex = (soundIndex < audioFiles.length - 1) ? soundIndex + 1 : 0;	
		String nextSoundId = audioFiles[soundIndex];
		startPlayer(nextSoundId);
	}
	
	protected void killOldPlayers() {
		for (HashMap.Entry<String, DroneSamplerLoop> entry : droneLoops.entrySet()) {
			// String id = entry.getKey();
			DroneSamplerLoop synthLoop = entry.getValue();
			// do something with the key/value
			// kill old players
			if(synthLoop.active() && P.p.millis() - synthLoop.startTime() > loopInterval/2) {
				synthLoop.stop();
			}
		}
	}
	
	protected void startPlayer(String id) {
		// lazy-init SynthLoop
		if(droneLoops.containsKey(id) == false) {
			droneLoops.put(id, new DroneSamplerLoop(id));
		}
		// get pitch
		int newPitch = Scales.SCALES[0][MathUtil.randRange(0, Scales.SCALES[0].length - 1)];
		// play!
		droneLoops.get(id).start(newPitch);
	}
	
	protected void checkNextSoundInterval() {
		if(P.p.millis() > loopLastStartTime + loopInterval) {
			loopLastStartTime = P.p.millis();
			startNextSound();
		}
	}
	
	public void update() {
		checkNextSoundInterval();
		for (HashMap.Entry<String, DroneSamplerLoop> entry : droneLoops.entrySet()) {
//			String id = entry.getKey();
			DroneSamplerLoop synthLoop = entry.getValue();
			synthLoop.update();
		}
	}
}