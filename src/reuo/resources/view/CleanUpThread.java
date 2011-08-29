package reuo.resources.view;

public class CleanUpThread extends Thread {
	ResourceViewer viewer;
	
	public CleanUpThread(ResourceViewer viewer) {
		this.viewer = viewer;
		setPriority(
			Math.max(
				Thread.MIN_PRIORITY, 
				Thread.currentThread().getPriority()-5
			)
		);
	}
	
	boolean running = true;
	public void run() {
		while(running){
			synchronized(this){
				try {
					viewer.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			System.gc();
		}
	}
}
