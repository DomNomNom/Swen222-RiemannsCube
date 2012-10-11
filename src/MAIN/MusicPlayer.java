package MAIN;

import sounds.Music;

public class MusicPlayer {

	
	public static void main(String[] args){
		Music music = new Music();
		music.playSound("resources/audio/music/Cubism.wav");
	}
}
