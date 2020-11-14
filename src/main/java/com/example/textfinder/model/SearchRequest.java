package com.example.textfinder.model;

import java.util.Optional;
import lombok.Getter;

@Getter
public class SearchRequest {

  private final String url;
  private final Integer maxThreadsNumber;
  private final String text;
  private final Integer maxUrlScanned;

  public SearchRequest(
      final String url,
      final Integer maxThreadsNumber,
      final String text,
      final Integer maxUrlScanned) {
    //todo validation
    this.url = Optional.ofNullable(url).orElseThrow(RuntimeException::new);
    this.maxThreadsNumber = Optional.ofNullable(maxThreadsNumber).orElse(1);
    this.text = Optional.ofNullable(text).orElseThrow(RuntimeException::new);
    this.maxUrlScanned = Optional.ofNullable(maxUrlScanned).orElse(1);
  }
}
