package com.khmel.task.entity;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

public class Server {
  private static final ReentrantLock channelLock = new ReentrantLock();
  private final Storage storage;

  public Server(Storage storage) {
    this.storage = storage;
  }

  public boolean upload() {
    channelLock.lock();
    try {
      TimeUnit.MILLISECONDS.sleep(500);
      return storage.add();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    } finally {
      channelLock.unlock();
    }
  }

  public boolean download() {
    channelLock.lock();
    try {
      TimeUnit.MILLISECONDS.sleep(500);
      return storage.remove();
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      return false;
    } finally {
      channelLock.unlock();
    }
  }

  public Storage getStorage() {
    return storage;
  }
}