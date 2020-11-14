package com.example.textfinder.service;

import com.example.textfinder.model.ParsedPage;
import com.example.textfinder.model.SearchRequest;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Service;

@Service
public class SearchService {

  // todo multithreading

  public List<ParsedPage> searchText(final SearchRequest searchRequest) {

    final Queue<String> queue = new LinkedList<>(Collections.singleton(searchRequest.getUrl()));
    final List<ParsedPage> list = new ArrayList<>();

    while (list.size() <= searchRequest.getMaxUrlScanned()) {
      if (!queue.isEmpty()) {
        final ParsedPage parsedPage = parseUrl(queue.poll(), searchRequest.getText());
        list.add(parsedPage);
        queue.addAll(Optional.ofNullable(parsedPage.getUrls()).orElseGet(ArrayList::new));
      }
    }
    return list;
  }

  private ParsedPage parseUrl(String url, String text) {
    try {
      final Document doc = Jsoup.connect(url).get();

      final List<String> urls = doc.getElementsByAttribute("href").stream()
          .map(s -> s.attr("href")) // todo url filter\fixer ?
          .filter(s -> s.startsWith("https:"))
          .collect(Collectors.toList());

      final boolean exists = doc.body().text().contains(text);

      return new ParsedPage(url, urls, exists, null); //todo factory?
    } catch (Exception e) {
      return new ParsedPage(url, null, null, e.getMessage());
    }
  }
}
