package core;

import java.util.Date;

public class Timeout extends Thread {

	@Override
	public void run() {

		System.out.printf("Server will be shutdown after %d seconds passed since the last request\n", Main.shutdownAfter);

		while (!interrupted()) {
			long workTime = new Date().getTime() - Main.lastRequestTime;
			if (workTime / 1000 > Main.shutdownAfter) {
				System.out.printf("Server is shutting down due to timeout (%d seconds passed since the last request)\n", workTime / 1000);
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
