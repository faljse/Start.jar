package core;

import java.util.Date;

public class Timeout extends Thread {

	@Override
	public void run() {

		System.out.printf("Server will be shut down after %d seconds after the last request\n", Main.shutdownAfter);

		while (!interrupted()) {
			long workTime = new Date().getTime() - Main.lastRequestTime;
			if (workTime / 1000 > Main.shutdownAfter) {
				System.out.printf("Server is shutting down due to timeout\n");
				Main.quit();
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}
}
