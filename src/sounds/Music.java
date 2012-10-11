package sounds;

import java.io.File;
import java.io.IOException;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

/**
 * 
 * @author mudgejayd
 *
 */
public class Music {

    private final int BUFFER_SIZE = 128000;
    private File soundFile;
    private AudioInputStream audioStream;
    private AudioFormat audioFormat;
    private  SourceDataLine sourceLine;
    private long loopTime;

	long skip30s = 0;
	public boolean loop = true;
	
	MusicPlayer musicPlayer;
    
	/**
	 * 
	 */
    public Music(){
        this.loop = false;
    }
    
    /**
     * 
     * @param time Time in milliseconds, at which the song will loop from.
     */
    public Music(long time){
        this.loop = true;
        this.loopTime = time;
    }

    /**
     * Plays a given sound file. Accepts .wav format.
     * 
     * 
     * @param filename the name of the file that is going to be played
     *
     */
    public void playSound(String filename){

        String strFilename = filename;

        try {
            soundFile = new File(strFilename);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
        
        musicPlayer = new MusicPlayer();
        musicPlayer.start();
    }
    
    /**
     * Creates a new thread for playing music on.
     * Now you can play the game at the same time!
     * 
     * @author mudgejayd
     *
     */
    private class MusicPlayer extends Thread{
        
        public MusicPlayer(){ 
        }
        
        public void run(){
            System.out.println("Starting");
            while(true){
                if(!play()){
                    if(!loop)    break;//loop until no longer playing.
                    System.out.println("Looping");
                }
            }
            System.out.println("And we're done.");
        }
        
        private boolean play(){
            
            System.out.println("Playing");

            try {
                audioStream = AudioSystem.getAudioInputStream(soundFile);
            } catch (Exception e){
                e.printStackTrace();
               System.exit(1);
            }

            audioFormat = audioStream.getFormat();

            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            
            try {
                sourceLine = (SourceDataLine) AudioSystem.getLine(info);
                sourceLine.open(audioFormat);
            } catch (LineUnavailableException e) {
                e.printStackTrace();
                System.exit(1);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
            
            sourceLine.start();
            
            try {
                audioStream.skip(skip30s);
            } catch (IOException e1) {e1.printStackTrace();}

            int nBytesRead = 0;
            byte[] abData = new byte[BUFFER_SIZE];
            long skip = 0;
            
            long startTime = System.currentTimeMillis();
            while (nBytesRead != -1) {
                try {
                    nBytesRead = audioStream.read(abData, 0, abData.length);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (nBytesRead >= 0) {
                    @SuppressWarnings("unused")
                    int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
                }
                skip+=nBytesRead;
                if(System.currentTimeMillis() > startTime + loopTime && skip30s==0)
                    skip30s = skip;
            }

            sourceLine.drain();
            sourceLine.close();
            
            return false;
        }
    }
}