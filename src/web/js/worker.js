var timer = function() {
  try {
      postMessage("go");
  } catch (e) {
      console.log(e);
  }

  var now = Date.now();
  var ttw = Math.round(1000 + 50 - now%1000);
  while (ttw < 0) ttw += 1000;
  setTimeout(timer, ttw);
}

timer();