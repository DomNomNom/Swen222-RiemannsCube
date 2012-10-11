package MAIN;

import sounds.Music;

public class MusicPlayer {

	
	public static void main(String[] args){
		Music music = new Music(true, 640000L);
		music.playSound("resources/audio/music/Cubism.wav");
	}
}
