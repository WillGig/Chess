package utils;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;

public class Sound {
	
	public static float VOLUME = 1.0f;
	private Clip clip;
	
	public Sound(String file){
		try{
			AudioInputStream inputStream = AudioSystem.getAudioInputStream(getClass().getResource(file));
			clip = AudioSystem.getClip();
			clip.open(inputStream);
			inputStream.close();
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void play(float volume){
		if(getVolume() != VOLUME*volume)
			setVolume(VOLUME*volume);
		if(clip.isRunning())
			clip.stop();
		clip.setFramePosition(0);
		clip.start();
	}
	
	public void stop(){
		if(clip.isRunning())
			clip.stop();
	}
	
	public float getVolume() {
	    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    return (float) Math.pow(10f, gainControl.getValue() / 20f);
	}

	public void setVolume(float volume) {
	    if (volume < 0f || volume > 1f){
	        System.err.println("INVALID VOLUME: " + volume);
	        return;
	    }
	    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);        
	    gainControl.setValue(20f * (float) Math.log10(volume));
	}
	
	public enum SoundEffect{
		
		BEEP("/bip.wav");
		
		public static float EFFECTVOLUME = 1.0f;
		
		private Sound sound;
		
		private SoundEffect(String file){
			sound = new Sound(file);
		}

		public void play(){
			sound.play(EFFECTVOLUME);
		}
		
	}
	
}

