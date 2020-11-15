let stompClient = null;

function connect() {
  stompClient = Stomp.over(new SockJS('/gs-guide-websocket'));
  stompClient.connect({}, () =>
    stompClient.subscribe('/topic/scanned', scannedUrl =>
      showScannedUrls(JSON.parse(scannedUrl.body)))
  );
}

function sendSearchParams() {
  stompClient.send("/app/find", {},
    JSON.stringify({
      'url': $("#url").val(),
      'text': $("#text").val(),
      'maxThreadsNumber': $("#max_threads_number").val(),
      'maxUrlScanned': $("#max_url_scanned").val()
    }));
}

function showScannedUrls(message) {
  //todo numerating
  $("#urls").append(
    "<tr><td>" + message.url + "</td>"
    + "<td>" + message.exist + "</td></tr>"
  );
}

function clearShowedUrls() {
  $("#urls").empty();
}

$(() => {
  $("form").on('submit', e => e.preventDefault());
  $("#send").click(() =>  {
    clearShowedUrls();
    sendSearchParams();
  });
});