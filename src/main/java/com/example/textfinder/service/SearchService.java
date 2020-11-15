package com.example.textfinder.service;

import com.example.textfinder.model.ScannedUrl;
import com.example.textfinder.model.SearchParams;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {

  private final SimpMessagingTemplate simpMessagingTemplate;
  private static final String MESSAGE_DESTINATION = "/topic/scanned";

  // todo multithreading

  public void searchText(final SearchParams searchParams) {

    final Queue<String> queue = new LinkedList<>(Collections.singleton(searchParams.getUrl()));
    final List<ScannedUrl> list = new ArrayList<>();

    while (list.size() < searchParams.getMaxUrlScanned()) {
      if (!queue.isEmpty()) {
        final ScannedUrl scannedUrl = parseUrl(queue.poll(), searchParams.getText());
        list.add(scannedUrl);
        queue.addAll(Optional.ofNullable(scannedUrl.getUrls()).orElseGet(ArrayList::new));
      }
    }
  }

  public ScannedUrl parseUrl(final String url, final String text) {
    try {
      final Document doc = Jsoup.connect(url).get();

      final List<String> urls = doc.getElementsByAttribute("href").stream()
          .map(s -> s.attr("href")) // todo url filter\fixer ?
          .filter(s -> s.startsWith("https:"))
          .collect(Collectors.toList());

      final boolean exists = doc.body().text().contains(text);

      final ScannedUrl scannedUrl = new ScannedUrl(url, urls, exists, null);
      //todo separate massage for new url, separate for scanned
      simpMessagingTemplate.convertAndSend(MESSAGE_DESTINATION, scannedUrl);
      return scannedUrl; //todo factory?
    } catch (Exception e) {
      final ScannedUrl scannedUrl = new ScannedUrl(url, null, null, e.getMessage());
      simpMessagingTemplate.convertAndSend(MESSAGE_DESTINATION, scannedUrl);
      return scannedUrl;
    }
  }
}
