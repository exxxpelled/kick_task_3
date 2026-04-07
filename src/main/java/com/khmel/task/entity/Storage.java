package com.khmel.task.entity;

import com.khmel.task.exception.CustomException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.locks.ReentrantLock;

public class Storage {
  private static final Logger logger = LogManager.getLogger(Storage.class);
  private static final ReentrantLock lock = new ReentrantLock();
  private static Storage instance;
  private static AtomicBoolean create = new AtomicBoolean(false);
  private final int maxCapacity;
  private int usedCapacity;

  private Storage() {
    this.maxCapacity = 10;
    this.usedCapacity = 0;
    logger.info("Storage initialized with capacity: {}", maxCapacity);
  }

  public static Storage getInstance() {
    if (!create.get()) {
      try {
        lock.lock();
        if (instance == null) {
          instance = new Storage();
          create.set(true);
        }
      } finally {
        lock.unlock();
      }
    }
    return instance;
  }

  public boolean add() {
    lock.lock();
    try {
      if (usedCapacity < maxCapacity) {
        usedCapacity++;
        logger.info("File uploaded. Used: {}/{}", usedCapacity, maxCapacity);
        return true;
      } else {
        logger.warn("Storage is full: upload rejected");
        return false;
      }
    } finally {
      lock.unlock();
    }
  }

  public boolean remove() {
    lock.lock();
    try {
      if (usedCapacity > 0) {
        usedCapacity--;
        logger.info("File downloaded. Used: {}/{}", usedCapacity, maxCapacity);
        return true;
      } else {
        logger.warn("Storage is empty: download rejected");
        return false;
      }
    } finally {
      lock.unlock();
    }
  }

  public int getUsedCapacity() {
    lock.lock();
    try {
      return usedCapacity;
    } finally {
      lock.unlock();
    }
  }

  public int getMaxCapacity() {
    return maxCapacity;
  }
}