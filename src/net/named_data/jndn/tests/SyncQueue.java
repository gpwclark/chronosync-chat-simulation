package net.named_data.jndn.tests;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncQueue<T> {
	LinkedBlockingQueue<T> queue;

	public SyncQueue() {
		this.queue = new LinkedBlockingQueue<>();
	}

	public T deQ() {
		T data = null;
		try {
			data = (T)this.queue.poll(5, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Logger.getLogger(Chat.class.getName()).log(Level.SEVERE,
				"interrupted while waiting on Q'd stuff", e);
		}
		return data;
	}

	public void enQ(T data) {
		this.queue.add(data);
	}
}
