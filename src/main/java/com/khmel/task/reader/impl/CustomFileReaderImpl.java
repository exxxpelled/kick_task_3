package com.khmel.task.reader.impl;

import com.khmel.task.exception.CustomException;
import com.khmel.task.reader.CustomFileReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class CustomFileReaderImpl implements CustomFileReader {
  private static Logger logger = LogManager.getLogger(CustomFileReaderImpl.class);
  private static final String FILE_PATH = "data/data.txt";

  @Override
  public List<String> readFile() throws CustomException {
    Path path = Paths.get(FILE_PATH);
    if (!Files.exists(path)) {
      logger.error("There is no data file");
      throw new CustomException("There is no data file");
    }
    try {
      List<String> result = Files.readAllLines(path);
      logger.info("File was read successfully. Lines: {}", result.size());
      return result;
    } catch (IOException exception) {
      logger.error("Failed to read file");
      throw new CustomException("Failed to read file");
    }
  }
}
