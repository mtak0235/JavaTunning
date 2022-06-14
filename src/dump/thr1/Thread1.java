package dump.thr1;

public class Thread1 extends Thread {
	Object lock;
	int r;

	Thread1(Object lock) {
		this.lock = lock;
	}

	public void run() {
		this.setName(this.getClass().getName());
		while (true) {
			synchronized (lock) {
				System.out.println(this.getName() + "#run " + r++);
				asleep(1000);
			}
		}
	}

	protected void asleep(int tm) {
		try {
			Thread.sleep(tm);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}