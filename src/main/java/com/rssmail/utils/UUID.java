package com.rssmail.utils;

public class UUID {
    public UUID() {
  }

    /**
     * Returns a new {@link UuidSource} that generates UUIDs using {@link UUID#randomUUID}.
     */
    public java.util.UUID random() {
      return java.util.UUID.randomUUID();
  }
}
