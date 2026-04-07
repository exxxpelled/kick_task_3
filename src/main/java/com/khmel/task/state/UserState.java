package com.khmel.task.state;

import com.khmel.task.entity.User;

public interface UserState {
  boolean doAction(User user);
}
