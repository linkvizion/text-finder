package com.example.textfinder.service;

import static org.mockito.Mockito.*;

import com.example.textfinder.model.ScannedUrl;
import com.example.textfinder.model.SearchParams;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.platform.commons.util.ClassLoaderUtils;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.messaging.simp.SimpMessagingTemplate;

class SearchServiceTest {

  @Test
  void parseUrl() throws IOException {
    //given
    final SearchService searchService = new SearchService(mock(SimpMessagingTemplate.class));
    final SearchService searchServiceSpy = spy(searchService);
    final Document document = getMockedDocument();
    final ScannedUrl expected = new ScannedUrl("url",
        Arrays.asList("https://url1/", "https://url2/", "https://url3/"), true);

    //when
    doCallRealMethod().when(searchServiceSpy).parseUrl(anyString(), any(SearchParams.class));
    doReturn(document).when(searchServiceSpy).getDocument(anyString());

    final ScannedUrl scannedUrl = searchServiceSpy
        .parseUrl("url", new SearchParams("url", "test", 1, 3));

    //then
    Assertions.assertEquals(scannedUrl, expected);

  }

  private Document getMockedDocument() throws IOException {
    Path absolutePath = Paths.get("src", "test", "resources").toAbsolutePath();
    return Jsoup
        .parse(new File(absolutePath + "/test.html"), StandardCharsets.UTF_8.name());
  }
}