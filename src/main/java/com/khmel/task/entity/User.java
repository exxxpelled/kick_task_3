package com.khmel.task.entity;

import com.khmel.task.state.UserState;
import com.khmel.task.state.impl.DownloadingState;
import com.khmel.task.state.impl.UploadingState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.Callable;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class User implements Callable<String> {
  private static final Logger logger = LogManager.getLogger(User.class);
  private static final int MAX_ATTEMPTS = 5;
  private static final long RETRY_DELAY_MS = 100;

  private final Server server;
  private final AtomicReference<UserState> state;
  private final int userId;

  public User(Server server, UserState initialState, int userId) {
    this.server = server;
    this.state = new AtomicReference<>(initialState);
    this.userId = userId;
  }

  public void changeState() {
    state.updateAndGet(current ->
            current instanceof DownloadingState ? new UploadingState() : new DownloadingState()
    );
  }

  public Server getServer() {
    return server;
  }

  public UserState getState() {
    return state.get();
  }

  public int getUserId() {
    return userId;
  }

  @Override
  public String call() {
    logger.info("User-{} [{}] started", userId, Thread.currentThread().getName());

    int attempts = 0;
    boolean success = false;

    while (attempts < MAX_ATTEMPTS && !success) {
      UserState currentState = state.get();
      success = currentState.doAction(this);

      if (!success) {
        attempts++;
        logger.warn("User-{} attempt {}/{} failed, retrying...", userId, attempts, MAX_ATTEMPTS);
        changeState(); // пробуем другую операцию

        try {
          TimeUnit.MILLISECONDS.sleep(RETRY_DELAY_MS * attempts);
        } catch (InterruptedException e) {
          Thread.currentThread().interrupt();
          logger.warn("User-{} interrupted", userId);
          break;
        }
      }
    }

    String result = success
            ? String.format("User-%d completed successfully", userId)
            : String.format("User-%d failed after %d attempts", userId, MAX_ATTEMPTS);

    logger.info("User-{} [{}] finished: {}", userId, Thread.currentThread().getName(), result);
    return result;
  }
}