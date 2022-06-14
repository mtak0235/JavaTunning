package dump.thr2;

public class Thread3 extends Thread {
	Object lock;
	int r;

	Thread3(Object lock) {
		this.lock = lock;
	}

	public void run() {
		this.setName(this.getClass().getName());
		while (true) {
			synchronized (lock) {
				System.out.println(this.getName() + "#run " + r++);
				asleep(lock,3000);
			}
		}
	}

	protected void asleep(Object lock, int tm) {
		try {
			lock.wait(tm);
			//this.wait(tm) 하면 런타임 에러 남.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}