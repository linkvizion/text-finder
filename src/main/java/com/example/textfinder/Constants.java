package com.example.textfinder;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {
  public static final String SCANNED_URL_TOPIC = "/topic/scanned";
  public static final String URL_TO_SCAN_TOPIC = "/topic/toScan";
  public static final String HREF_CSS_SELECTOR = "a[href]";
  public static final Integer TIME_OUT_SECONDS = 5;
}
