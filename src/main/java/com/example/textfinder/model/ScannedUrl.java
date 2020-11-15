package com.example.textfinder.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ScannedUrl {

  private final String url;
  private final List<String> urls;
  private final Boolean exist;
  private final String error;
}
