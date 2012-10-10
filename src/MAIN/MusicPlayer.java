package MAIN;

import sounds.Music;

public class MusicPlayer {

	
	public static void main(String[] args){
		Music music = new Music();
		music.playSound("src/sounds/sound.wav");
	}
}
