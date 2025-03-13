package org.zero.common.test;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author Zero (cnzeropro@163.com)
 * @since 2025/3/10
 */
class ThreadSafeTest {
    int num = 1000;

    @Test
    void test() {
        // Lock lock = new ReentrantLock();
        MyLock lock = new MyLock1();
        List<Thread> threads = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Thread thread = new Thread(() -> {
                lock.lock();
                for (int j = 0; j < 10; j++) {
                    // try {
                    //     Thread.sleep(1);
                    // } catch (InterruptedException e) {
                    //     throw new RuntimeException(e);
                    // }
                    num--;
                }
                lock.unlock();
            });
            threads.add(thread);
        }
        // 启动线程
        threads.forEach(Thread::start);
        // 等待所有线程结束
        threads.forEach(thread -> {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });
        System.out.println(num);
    }
}

interface MyLock {
    void lock();

    void unlock();
}

class MyLock1 implements MyLock {
    AtomicBoolean locked = new AtomicBoolean(false);

    public void lock() {
        while (true) {
            // 此处竞争严重，会一直尝试
            if (locked.compareAndSet(false, true)) {
                return;
            }
        }
    }

    public void unlock() {
        while (true) {
            // 此处竞争严重，会一直尝试
            if (locked.compareAndSet(true, false)) {
                return;
            }
        }
    }
}

class MyLock2 implements MyLock {
    AtomicBoolean lock = new AtomicBoolean(false);
    Thread owner;
    AtomicReference<Node> head = new AtomicReference<>(new Node());
    AtomicReference<Node> tail = new AtomicReference<>(head.get());

    @Override
    public void lock() {
        Thread currentThread = Thread.currentThread();
        if (lock.compareAndSet(false, true)) {
            owner = currentThread;
            head.get().waiter = currentThread;
            return;
        }
        Node node = tail.get();
        Node nextNode = new Node();
        nextNode.waiter = currentThread;
        node.next = nextNode;
        nextNode.prev = node;
        tail = new AtomicReference<>(nextNode);
    }

    @Override
    public void unlock() {

    }

    class Node {
        Node prev;
        Node next;
        Thread waiter;
    }
}
