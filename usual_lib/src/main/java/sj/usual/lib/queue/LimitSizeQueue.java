package sj.usual.lib.queue;

import android.support.annotation.NonNull;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

/**
 * 限制容器大小的先进先出队列
 * Created by WuShengjun on 2017/8/30.
 */

public class LimitSizeQueue<E> implements Queue<E> {
    private int limitSize;

    private Queue<E> queue = new LinkedList<>();
    private E pollElement;

    public LimitSizeQueue(int limitSize) {
        this.limitSize = limitSize;
    }

    /**
     * 入列
     * @param e
     * @return
     */
    @Override
    public boolean offer(E e) {
        if(queue.size() >= limitSize) { // 入列时，如果超出长度先出列
            pollElement = queue.poll();
        }
        return queue.offer(e);
    }

    /**
     * 出列
     * @return
     */
    @Override
    public E poll() {
        pollElement = queue.poll();
        return pollElement;
    }

    /**
     * 获取限制的容器大小
     * @return
     */
    public int getLimitSize() {
        return limitSize;
    }

    /**
     * 获取头元素
     * @return
     */
    public E getFirst() {
        E e = null;
        if(!queue.isEmpty()) {
            e = ((LinkedList<E>) queue).getFirst();
        }
        return e;
    }

    /**
     * 获取尾元素
     * @return
     */
    public E getLast() {
        E e = null;
        if(!queue.isEmpty()) {
            e = ((LinkedList<E>) queue).getLast();
        }
        return e;
    }

    public E getLastPoll() {
        return pollElement;
    }

    @Override
    public boolean add(E e) {
        return queue.add(e);
    }

    @Override
    public boolean addAll(Collection<? extends E> collection) {
        return queue.addAll(collection);
    }

    @Override
    public void clear() {
        queue.clear();
    }

    @Override
    public boolean contains(Object object) {
        return queue.contains(object);
    }

    @Override
    public boolean containsAll(Collection<?> collection) {
        return queue.containsAll(collection);
    }

    @Override
    public boolean isEmpty() {
        return queue.isEmpty();
    }

    @NonNull
    @Override
    public Iterator<E> iterator() {
        return queue.iterator();
    }

    @Override
    public boolean remove(Object object) {
        return queue.remove(object);
    }

    @Override
    public boolean removeAll(Collection<?> collection) {
        return queue.removeAll(collection);
    }

    @Override
    public boolean retainAll(Collection<?> collection) {
        return queue.retainAll(collection);
    }

    @Override
    public int size() {
        return queue.size();
    }

    @NonNull
    @Override
    public Object[] toArray() {
        return queue.toArray();
    }

    @NonNull
    @Override
    public <T> T[] toArray(T[] array) {
        return queue.toArray(array);
    }

    @Override
    public E remove() {
        return queue.remove();
    }

    @Override
    public E element() {
        return queue.element();
    }

    @Override
    public E peek() {
        return queue.peek();
    }
}
