package com.example.textfinder.model;

import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class ScannedUrl {

  public ScannedUrl(String url, String error) {
    this(url, Collections.emptyList(), null, error);
  }

  public ScannedUrl(String url, List<String> urls, Boolean exist) {
    this(url, urls, exist, null);
  }

  private final String url;
  private final List<String> urls;
  private final Boolean exist;
  private final String error;
}
