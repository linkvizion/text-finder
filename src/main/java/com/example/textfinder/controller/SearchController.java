package com.example.textfinder.controller;

import com.example.textfinder.model.SearchRequest;
import com.example.textfinder.service.SearchService;
import com.example.textfinder.service.SearchService1;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class SearchController {

  private final SearchService searchService;

  @PostMapping(value = "/")
  public void search(@RequestBody SearchRequest searchRequest) {
    //todo validation, websocket
    searchService.searchText(searchRequest);
  }
}
