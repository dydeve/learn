package com.dydeve.data.example.test;

import java.util.AbstractQueue;
import java.util.Collection;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @Description:
 * @Date 下午2:42 2020/5/18
 * @Author: joker
 */
public class EmptyBlockingQueue<E> extends AbstractQueue<E>
		implements BlockingQueue<E>, java.io.Serializable  {

	@Override
	public Iterator<E> iterator() {
		return new EmptyItr();
	}

	@Override
	public int size() {
		return 0;
	}

	@Override
	public void put(E e) throws InterruptedException {
		throw new InterruptedException("Unable to insert into queue");
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit) throws InterruptedException {
		unit.sleep(timeout);
		return false;
	}

	@Override
	public E take() throws InterruptedException {
		throw new InterruptedException("Unable to take from queue");
	}

	@Override
	public E poll(long timeout, TimeUnit unit) throws InterruptedException {
		unit.sleep(timeout);
		return null;
	}

	@Override
	public int remainingCapacity() {
		return 0;
	}

	@Override
	public int drainTo(Collection<? super E> c) {
		return 0;
	}

	@Override
	public int drainTo(Collection<? super E> c, int maxElements) {
		return 0;
	}

	@Override
	public boolean offer(E e) {
		return false;
	}

	@Override
	public E poll() {
		return null;
	}

	@Override
	public E peek() {
		return null;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		if (c.size() > 0) {
			throw new IllegalArgumentException("Too many items in collection");
		}
		return false;
	}



	private class EmptyItr implements Iterator<E> {

		@Override
		public boolean hasNext() {
			return false;
		}

		@Override
		public E next() {
			throw new NoSuchElementException("empty iterator");
		}
	}
}
