package com.example.textfinder.controller;

import com.example.textfinder.model.SearchParams;
import com.example.textfinder.service.SearchService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@AllArgsConstructor
public class SearchController {

  private final SearchService searchService;

  @MessageMapping("/find")
  public void find(@Valid final SearchParams searchParams) {
    searchService.searchText(searchParams);
  }
}
