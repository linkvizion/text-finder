let stompScannedClient = null;
let stompToScanClient = null;

function connect() {
  stompScannedClient = Stomp.over(new SockJS('/gs-guide-websocket'));
  stompToScanClient = Stomp.over(new SockJS('/gs-guide-websocket'));
  stompScannedClient.connect({}, () =>
    stompScannedClient.subscribe('/topic/scanned', scannedUrl => {
      showScannedUrls(JSON.parse(scannedUrl.body));
      deleteScannedUrlFromToScan();
    })
  );
  stompToScanClient.connect({}, () =>
    stompToScanClient.subscribe('/topic/toScan', url => showUrlsToScan(url.body))
  );
}

function sendSearchParams() {
  stompScannedClient.send("/app/find", {},
    JSON.stringify({
      'url': $("#url").val(),
      'text': $("#text").val(),
      'maxThreadsNumber': $("#max_threads_number").val(),
      'maxUrlScanned': $("#max_url_scanned").val()
    }));
}

function showScannedUrls(scannedUrl) {
  //todo numerating
  $("#scanned_urls").append(
    "<tr><td>" + scannedUrl.url + "</td>"
    + "<td>" + showMassage(scannedUrl) + "</td></tr>"
  );
}

function deleteScannedUrlFromToScan() {
  $("#urls_to_scan").children()[0].remove()
}

function showUrlsToScan(urlToScan) {
  $("#urls_to_scan").append("<tr><td>" + urlToScan + "<td></tr>td>");
}

function showMassage(scannedUrl) {
  return scannedUrl.exist != null ? scannedUrl.exist : scannedUrl.error;
}

function clearTables() {
  $("#scanned_urls").empty();
  $("#urls_to_scan").empty();
}

$(() => {
  $("form").on('submit', e => e.preventDefault());
  $("#send").click(() => {
    if ($("#url").val() && $("#text").val()) {
      clearTables();
      sendSearchParams();
    }
  });
});