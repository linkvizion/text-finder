package com.example.textfinder.model;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SearchParams {

  @NotNull(message = "Url cannot be null")
  private String url;

  @NotNull(message = "Searched text cannot be null")
  private String text;

  @Min(value = 1, message = "Threads cannot be less than 1")
  private Integer maxThreadsNumber;

  @Min(value = 1, message = "Max url scanned cannot be less than 1")
  private Integer maxUrlScanned;
}
