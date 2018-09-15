package com.uofantarctica.jndn.tests.sync;

import com.uofantarctica.jndn.tests.chat.Chatter;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SyncQueue<T> {
	LinkedBlockingQueue<T> queue;
	private final long timeout;

	public SyncQueue(long timeout) {
		this.queue = new LinkedBlockingQueue<>();
		this.timeout = timeout;
	}

	public SyncQueue() {
		this.queue = new LinkedBlockingQueue<>();
		timeout = 5l;
	}

	public T deQ() {
		T data = null;
		try {
			data = (T)this.queue.poll(timeout, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			Logger.getLogger(Chatter.class.getName()).log(Level.SEVERE,
				"interrupted while waiting on Q'd stuff", e);
		}
		return data;
	}

	public void enQ(T data) {
		this.queue.add(data);
	}
}
