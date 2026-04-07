package com.khmel.task;

import com.khmel.task.entity.Server;
import com.khmel.task.entity.Storage;
import com.khmel.task.entity.User;
import com.khmel.task.exception.CustomException;
import com.khmel.task.state.impl.DownloadingState;
import com.khmel.task.state.impl.UploadingState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class Main {
  private static final Logger logger = LogManager.getLogger(Main.class);

  public static void main(String[] args) {
    logger.info("Main thread started");

    try {
      Storage storage = Storage.getInstance();
      Server server = new Server(storage);

      List<User> users = List.of(
              new User(server, new DownloadingState(), 1),
              new User(server, new UploadingState(), 2),
              new User(server, new DownloadingState(), 3)
      );

      ExecutorService executor = Executors.newFixedThreadPool(users.size());
      List<Future<String>> futures = new ArrayList<>();

      for (User user : users) {
        futures.add(executor.submit(user));
      }

      for (Future<String> future : futures) {
        try {
          logger.info("Result: {}", future.get(10, TimeUnit.SECONDS));
        } catch (TimeoutException e) {
          logger.warn("Task timed out");
        } catch (ExecutionException e) {
          logger.error("Task execution failed", e);
        }
      }

      executor.shutdown();
      if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
        executor.shutdownNow();
      }
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      logger.error("Main thread interrupted", e);
    }

    logger.info("Main thread finished");
  }
}