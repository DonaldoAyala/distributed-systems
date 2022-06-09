function WSClient(url) {
  this.url = url;
  this.post = function (operacion, args, callback) {
    var body = "";
    try {
      var body = JSON.stringify(args);
      var req = new XMLHttpRequest();
      req.open("POST", url + "/" + operacion);
      req.setRequestHeader("Accept", "application/json");
      req.setRequestHeader("Content-Type", "application/json");
      req.onload = function () {
        if (callback != null) callback(req.status, req.response);
      };
      req.send(body);
    } catch (e) {
      alert("Error: " + e.message);
    }
  };
}
