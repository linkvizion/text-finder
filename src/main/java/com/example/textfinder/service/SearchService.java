package com.example.textfinder.service;

import static com.example.textfinder.Constants.HREF_CSS_SELECTOR;
import static com.example.textfinder.Constants.SCANNED_URL_TOPIC;
import static com.example.textfinder.Constants.TIME_OUT_SECONDS;
import static com.example.textfinder.Constants.URL_TO_SCAN_TOPIC;

import com.example.textfinder.model.ScannedUrl;
import com.example.textfinder.model.SearchParams;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SearchService {
  //todo tests

  private final SimpMessagingTemplate simpMessagingTemplate;
  private int SCANNED_URL_COUNT;
  private Queue<String> queue;
  private final Object MONITOR = new Object();

  public void searchText(final SearchParams searchParams) {
    SCANNED_URL_COUNT = 0;
    queue = new LinkedBlockingQueue<>(searchParams.getMaxUrlScanned());
    queue.add(searchParams.getUrl());

    final ThreadPoolExecutor threadPoolExecutor = (ThreadPoolExecutor) Executors
        .newFixedThreadPool(searchParams.getMaxThreadsNumber());

    while (SCANNED_URL_COUNT < searchParams.getMaxUrlScanned()) {
      synchronized (MONITOR) {
        if (!queue.isEmpty()) {
          String url = queue.poll();
          threadPoolExecutor
              .execute(() -> {
                synchronized (MONITOR) {
                  ScannedUrl scannedUrl = parseUrl(url, searchParams.getText(),
                      searchParams);
                  queue.addAll(scannedUrl.getUrls());
                }
              });
          SCANNED_URL_COUNT++;
        }
      }
    }

    threadPoolExecutor.shutdown();
  }

  private ScannedUrl parseUrl(final String url, final String text,
      final SearchParams searchParams) {
    try {
      final Document doc = Jsoup.connect(url).timeout(TIME_OUT_SECONDS * 1000).get();

      final List<String> urls;
      synchronized (MONITOR) {
        urls = doc.select(HREF_CSS_SELECTOR).stream()
            .map(this::getHrefValue)
            .filter(this::isNewUrl)
            .limit(
                Math.max(searchParams.getMaxUrlScanned() - (queue.size() + SCANNED_URL_COUNT), 0))
            .peek(this::pushToScan).collect(Collectors.toList());
      }

      final boolean exists = doc.body().text().contains(text);

      final ScannedUrl scannedUrl = new ScannedUrl(url, urls, exists);
      pushToScanned(scannedUrl);
      return scannedUrl;
    } catch (Exception e) {
      final ScannedUrl scannedUrl = new ScannedUrl(url, e.getMessage());
      pushToScanned(scannedUrl);
      return scannedUrl;
    }
  }

  private void pushToScan(final String url) {
    simpMessagingTemplate.convertAndSend(URL_TO_SCAN_TOPIC, url);
  }

  private void pushToScanned(final ScannedUrl scannedUrl) {
    simpMessagingTemplate.convertAndSend(SCANNED_URL_TOPIC, scannedUrl);
  }

  private String getHrefValue(final Element element) {
    return element.attr("href"); // todo url filter\fixer ?
  }

  private boolean isNewUrl(final String href) {
    synchronized (MONITOR) {
      return href.startsWith("https://") && !queue.contains(href);
    }
  }
}
