package MAIN;

import sounds.Music;

/**
 * Try out this fancy music shizzbiznis.
 * 
 * @author mudgejayd
 *
 */
public class MusicPlayer {

	
	public static void main(String[] args){
		Music music = new Music(640000L);
		music.playSound("resources/audio/music/Cubism.wav");
	}
}
