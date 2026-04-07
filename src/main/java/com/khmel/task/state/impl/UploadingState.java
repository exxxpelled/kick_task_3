package com.khmel.task.state.impl;

import com.khmel.task.entity.User;
import com.khmel.task.state.UserState;

public class UploadingState implements UserState {
  @Override
  public boolean doAction(User user) {
    return user.getServer().upload();
  }
}