package com.example.textfinder.service;

import com.example.textfinder.model.ScannedUrl;
import com.example.textfinder.model.SearchParams;
import java.util.ArrayList;
import java.util.Collection;
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
  private static final String SCANNED_URL_TOPIC = "/topic/scanned";
  private static final String URL_TO_SCAN_TOPIC = "/topic/toScan";
  final List<ScannedUrl> list = new ArrayList<>();
//   todo multithreading

  public void searchText(final SearchParams searchParams) {
    final Queue<String> queue = new LinkedList<>(Collections.singleton(searchParams.getUrl()));

    while (list.size() < searchParams.getMaxUrlScanned()) {
      if (!queue.isEmpty()) {
        final ScannedUrl scannedUrl = parseUrl(queue.poll(), searchParams.getText());
        list.add(scannedUrl);
        queue.addAll(
            Optional.ofNullable(scannedUrl.getUrls()) //todo refactor
                .map(s -> s.stream()
                    .filter(url -> !queue.contains(url) && list.stream()
                        .noneMatch(a -> a.getUrl().equals(url)))
                    .limit(searchParams.getMaxUrlScanned() - list.size())
                    .peek(url -> simpMessagingTemplate.convertAndSend(URL_TO_SCAN_TOPIC, url))
                    .collect(Collectors.toList()))
                .orElseGet(ArrayList::new));
      }
    }
  }

  private ScannedUrl parseUrl(final String url, final String text) {
    try {
      final Document doc = Jsoup.connect(url).timeout(2 * 1000) .get();

      final List<String> urls = doc.select("a[href]").stream()
          .map(s -> s.attr("href")) // todo url filter\fixer ?
          .filter(s -> s.startsWith("https:"))
          .collect(Collectors.toList());

      final boolean exists = doc.body().text().contains(text);

      final ScannedUrl scannedUrl = new ScannedUrl(url, urls, exists, null);
      //todo separate massage for new url, separate for scanned
      simpMessagingTemplate.convertAndSend(SCANNED_URL_TOPIC, scannedUrl);
      return scannedUrl; //todo factory?
    } catch (Exception e) {
      final ScannedUrl scannedUrl = new ScannedUrl(url, null, null, e.getMessage());
      simpMessagingTemplate.convertAndSend(SCANNED_URL_TOPIC, scannedUrl);
      return scannedUrl;
    }
  }
}
