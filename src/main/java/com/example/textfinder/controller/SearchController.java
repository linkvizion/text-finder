package com.example.textfinder.controller;

import com.example.textfinder.model.SearchRequest;
import com.example.textfinder.service.SearchService;
import javax.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
@AllArgsConstructor
public class SearchController {

  private final SearchService searchService;

  @PostMapping(value = "/")
  public void search(@RequestBody @Valid SearchRequest searchRequest) {
    //todo websocket
    searchService.searchText(searchRequest);
  }
}
