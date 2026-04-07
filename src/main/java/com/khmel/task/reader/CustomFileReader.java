package com.khmel.task.reader;

import com.khmel.task.exception.CustomException;

import java.util.List;

public interface CustomFileReader {
  List<String> readFile() throws CustomException;
}
