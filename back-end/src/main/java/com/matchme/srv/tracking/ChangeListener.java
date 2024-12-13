package com.matchme.srv.tracking;

import com.matchme.srv.tracking.model.ChangeLog;

public interface ChangeListener {
  void onChange(ChangeLog change);
}
