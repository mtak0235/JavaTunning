package dump.thr2;

public class Thread2 extends Thread {
	Object lock;
	int r;

	Thread2(Object lock) {
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
			lock.wait(tm);//락을 해제하고 슬립에 빠짐. 그리고 3초 뒤  순서대로 깸.
			//this.wait(tm) 하면 런타임 에러 남.
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}