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

	long skip30s = 0;
	public boolean loop = true;
	
	Player musicPlayer;

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
        
        musicPlayer = new Player();
        musicPlayer.start();
    }
    
    
    private class Player extends Thread{
        
        public Player(){ 
        }
        
        public void start(){
            System.out.println("Starting");
            while(true){
                if(!play()){
                    if(!loop)    break;//loop until no longer playing.
                    System.out.println("Looping");
                }
            }
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
                if(System.currentTimeMillis() > startTime + 30000 && skip30s==0)
                    skip30s = skip;
            }

            sourceLine.drain();
            sourceLine.close();
            
            return false;
        }
    }
}