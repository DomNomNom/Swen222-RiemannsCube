package server;

public class ClockThread extends Thread{

	private final int delay; // delay between pulses in us
//	private final Board game;
//	private final BoardFrame display;
	
	public ClockThread(int delay) {
		this.delay = delay;
//		this.game = game;
//		this.display = display;
	}
	
	public void run() {
		while(1 == 1) {
			// Loop forever			
			try {
				Thread.sleep(delay);
//				game.clockTick();
//				if(display != null) {
//					display.repaint();
//				}
			} catch(InterruptedException e) {
				// should never happen
			}			
		}
	}
}
