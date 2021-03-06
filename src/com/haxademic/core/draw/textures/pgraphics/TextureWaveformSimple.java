package com.haxademic.core.draw.textures.pgraphics;

import com.haxademic.core.draw.textures.pgraphics.shared.BaseTexture;
import com.haxademic.core.math.MathUtil;
import com.haxademic.core.media.audio.analysis.AudioIn;

public class TextureWaveformSimple 
extends BaseTexture {

	protected int _numLines = 40;
	protected boolean _hasStroke = true;
	
	public TextureWaveformSimple( int width, int height ) {
		super(width, height);
		
	}
	
	public void newLineMode() {
		_numLines = MathUtil.randRange(20, 30);
		_hasStroke = !_hasStroke;
	}

	public void updateDraw() {
		feedback(10f, 0.12f);
		
		int waveformDataLength = AudioIn.waveform.length;
		float widthStep = (float) width / (float) waveformDataLength;
		float startY = height * 0.5f;
		float amp = height * 0.4f;
		
		_texture.stroke(_color);
		_texture.strokeWeight(3.f);

		for(int i = 1; i < waveformDataLength; i++) {
			_texture.line( i * widthStep, startY + AudioIn.waveform[i-1] * amp, (i+1) * widthStep, startY + AudioIn.waveform[i] * amp );
		}
	}
}
