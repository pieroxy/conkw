window.ConkW = window.ConkW || {};
ConkW.data = ConkW.data || {funcs:{}};
ConkW.dates = ConkW.dates || {};

ConkW.clockFaces = [
  {
    background: "/clocks/arabic.png",
    hour: {
      div: {
        forwardLength: 0.5,
        backLength: 0.03,
        width: 0.03,
        color: "black",
        radius: "0",
        clip: "polygon(0px 20%, 100% 0%, 100% 100%, 0px 80%)"
      },
      shadowOffset: 0.01,
      shadowSpread: 0.001,
      shadowRadius: 0.003,
      shadowAngle: 90,
      shadowColor: "rgba(0, 0, 0, 0.38)"
    },
    minute: {
      div: {
        forwardLength: 0.76,
        backLength: 0.03,
        width: 0.03,
        color: "black",
        radius: "0",
        clip: "polygon(0px 20%, 100% 0%, 100% 100%, 0px 80%)"
      },
      shadowOffset: 0.02,
      shadowSpread: 0.001,
      shadowRadius: 0.004,
      shadowAngle: 90,
      shadowColor: "rgba(0, 0, 0, 0.38)"
    },
    second: {
      img: {
        width: 1436,
        height: 98,
        centerPosition: 1033,
        scale: 0.5,
        src: "/clocks/wallclock-seconds.png"
      },
      shadowOffset: 0.03,
      shadowRadius: 0.005,
      shadowAngle: 90,
      shadowColor: "rgba(0, 0, 0, 0.38)"
    }
  },
  {
    background: "/clocks/simple.png",
    hour: {
      div: {
        forwardLength: 0.6,
        backLength: 0.08,
        width: 0.03,
        color: "black",
        radius: "0"
      }
    },
    minute: {
      div: {
        forwardLength: 0.75,
        backLength: 0.08,
        width: 0.015,
        color: "black",
        radius: "0"
      }
    },
    second: {
      div: {
        forwardLength: 0.7,
        backLength: 0.2,
        width: 0.001,
        color: "black",
        radius: "0"
      }
    }
  },
  {
    background: "/clocks/chiffresRomains.png",
    hour: {
      img: {
        width: 313,
        height: 66,
        centerPosition: 235,
        scale:0.37,
        src: "/clocks/chiffresRomains-hours.png",
      }

    },
    minute: {
      img: {
        width: 373,
        height: 51,
        centerPosition: 294,
        scale:0.44,
        src: "/clocks/chiffresRomains-minutes.png",
      }
    },
    second: {
    }
  },
  {
    background: "/clocks/modern.png",
    hour: {
      div: {
        forwardLength: 0.6,
        backLength: 0.15,
        width: 0.015,
        color: "#2c2c2e",
        radius: "0"
      },
      shadowOffset: 0.005,
      shadowSpread: 0.0002,
      shadowRadius: 0.01,
      shadowAngle: 225,
      shadowColor: "rgba(0, 0, 0, 0.25)"
    },
    minute: {
      div: {
        forwardLength: 0.75,
        backLength: 0.15,
        width: 0.015,
        color: "#2c2c2e",
        radius: "0"
      },
      shadowOffset: 0.01,
      shadowSpread: 0.0005,
      shadowRadius: 0.01,
      shadowAngle: 225,
      shadowColor: "rgba(0, 0, 0, 0.25)"
    },
    second: {
      div: {
        forwardLength: 0.73,
        backLength: 0.2,
        width: 0.005,
        color: "#fd773a",
        radius: "0"
      },
      shadowOffset: 0.02,
      shadowSpread: 0.0001,
      shadowRadius: 0.02,
      shadowAngle: 225,
      shadowColor: "rgba(0, 0, 0, 0.25)"
    }
  },
  {
    background: "/clocks/elegant.png",
    hour: {
      img: {
        width: 191,
        height: 12,
        centerPosition: 156,
        scale:0.29,
        src: "/clocks/elegant-seconds.png",
      },
      shadowOffset: 0.005,
      shadowSpread: 0.00001,
      shadowRadius: 0.01,
      shadowAngle: 0,
      shadowColor: "rgba(0, 0, 0, 0.31)"
    },
    minute: {
      img: {
        width: 308,
        height: 26,
        centerPosition: 246,
        scale:0.511,
        src: "/clocks/elegant-minutes.png",
      },
      shadowOffset: 0.01,
      shadowSpread: 0.00001,
      shadowRadius: 0.01,
      shadowAngle: 0,
      shadowColor: "rgba(0, 0, 0, 0.31)"
    },
    second: {
      div: {
        forwardLength: 0.9,
        backLength: 0.15,
        width: 0.005,
        color: "#292d2e",
        radius: "0"
      },
      shadowOffset: 0.02,
      shadowSpread: 0,
      shadowRadius: 0.015,
      shadowAngle: 0,
      shadowColor: "rgba(0, 0, 0, 0.31)"
    }
  },
  {
    background: "/clocks/pink.png",
    hour: {
      div: {
        forwardLength: 0.36,
        backLength: 0.1,
        width: 0.013,
        color: "#5b4e4d",
        radius: "0"
      },
      shadowColor: "rgba(0, 0, 0, 0.27)",
      shadowOffset: 0.005,
      shadowSpread: 0.0,
      shadowAngle: 45,
      shadowRadius: 0.01
    },
    minute: {
      div: {
        forwardLength: 0.49,
        backLength: 0.1,
        width: 0.01,
        color: "#5b4e4d",
        radius: "0"
      },
      shadowOffset: 0.01,
      shadowSpread: 0,
      shadowRadius: 0.005,
      shadowAngle: 45,
      shadowColor: "rgba(0, 0, 0, 0.27)"
    },
    second: {
      img: {
        width: 445,
        height: 39,
        centerPosition: 315,
        scale:0.359,
        src: "/clocks/pink-seconds.png"
      },
      shadowOffset: 0.02,
      shadowSpread: 0,
      shadowRadius: 0.005,
      shadowAngle: 45,
      shadowColor: "rgba(0, 0, 0, 0.27)"
    }
  },
  {
    background: "/clocks/green.png",
    hour: {
      div: {
        forwardLength: 0.5,
        backLength: 0.08,
        width: 0.03,
        color: "#d7b595",
        radius: "100px"
      },
      shadowOffset: 0.01,
      shadowSpread: 0,
      shadowRadius: 0.005,
      shadowAngle: 225,
      shadowColor: "rgba(0, 0, 0, 0.38)"
    },
    minute: {
      div: {
        forwardLength: 0.79,
        backLength: 0.08,
        width: 0.03,
        color: "#dcbea0",
        radius: "100px"
      },
      shadowOffset: 0.02,
      shadowSpread: 0,
      shadowRadius: 0.01,
      shadowAngle: 225,
      shadowColor: "rgba(0, 0, 0, 0.38)"
    },
    second: {}
  },
  {
    background: "/clocks/blue.png",
    hour: {
      div: {
        forwardLength: 0.55,
        backLength: 0.08,
        width: 0.03,
        color: "#ab7f6a",
        radius: "100px"
      },
      shadowOffset: 0.01,
      shadowSpread: 0,
      shadowRadius: 0.005,
      shadowAngle: 270,
      shadowColor: "rgba(0, 0, 0, 0.38)"
    },
    minute: {
      div: {
        forwardLength: 0.66,
        backLength: 0.08,
        width: 0.03,
        color: "#b18674",
        radius: "100px"
      },
      shadowOffset: 0.02,
      shadowSpread: 0,
      shadowRadius: 0.01,
      shadowAngle: 270,
      shadowColor: "rgba(0, 0, 0, 0.38)"
    },
    second: {}
  },
  {
    background: "/clocks/wallclock.png",
    hour: {
      div: {
        forwardLength: 0.5,
        backLength: 0.015,
        width: 0.015,
        color: "#444",
        radius: "100% 0 0 100%"
      },
      shadowOffset: 0.015,
      shadowSpread: 0,
      shadowRadius: 0.01,
      shadowAngle: 180,
      shadowColor: "rgba(0, 0, 0, 0.38)"
    },
    minute: {
      div: {
        forwardLength: 0.75,
        backLength: 0.015,
        width: 0.015,
        color: "#444",
        radius: "100% 0 0 100%"
      },
      shadowOffset: 0.02,
      shadowSpread: 0,
      shadowRadius: 0.01,
      shadowAngle: 180,
      shadowColor: "rgba(0, 0, 0, 0.38)"
    },
    second: {
      img: {
        width: 1436,
        height: 98,
        centerPosition: 1033,
        scale: 0.5,
        src: "/clocks/wallclock-seconds.png"
      },
      shadowOffset: 0.03,
      shadowRadius: 0.005,
      shadowAngle: 180,
      shadowColor: "rgba(0, 0, 0, 0.38)"

    }
  },
  {
    background: "/clocks/antique.png",
    hour: {
      img: {
        width: 429,
        height: 98,
        centerPosition: 399,
        scale: 0.2429,
        src: "/clocks/antique-hours.png"
      },
      shadowOffset: 0.015,
      shadowRadius: 0.005,
      shadowAngle: 270,
      shadowColor: "rgba(0, 0, 0, 0.53)"

    },
    minute: {
      img: {
        width: 618,
        height: 82,
        centerPosition: 575,
        scale: 0.35,
        src: "/clocks/antique-minutes.png"
      },
      shadowOffset: 0.02, 
      shadowRadius: 0.005,
      shadowAngle: 270,
      shadowColor: "rgba(0, 0, 0, 0.53)"
    },
    second: {}
  }
]

ConkW.dates.initClocks = function() {
  var cs = document.getElementsByTagName("clock");
  if (cs && cs.length) this.initClock(cs[0]);
}

ConkW.dates.initClock = function(e) {
  var clock = {
    root: e,
    face: document.createElement("DIV"),
  };

  clock.face.className = "clock-face";

  e.appendChild(clock.face);

  ConkW.data.cwClock = clock;

  var cf = localStorage["conkw.clockFaceIndex"];
  if (!cf || cf > ConkW.clockFaces.length) cf = 0;

  this.setClockFace(ConkW.clockFaces[cf]);

  this.updateClock(true);
}

ConkW.dates.sin = function(number) {
  return Math.sin(Math.PI*number/180);
}

ConkW.dates.cos = function(number) {
  return Math.cos(Math.PI*number/180);
}

ConkW.dates.updateClock = function(forceUpdate) {
  if (!ConkW.data.cwClock) return;

  const now = new Date();

  this.updateDates(now);
  if (!ConkW.data.clockInitialized) {
    ConkW.data.clockInitialized = 1;
  }



  ConkW.dates.setShadow = function(element, faceElement, handangle) {
    if (faceElement.shadowColor) {
      var size = ConkW.currentClockFace.offsetWidth;
      var spread = 0.01;
      var distance = 0.01;
      var radius = 0.01;
      var angle = 225;
      if (faceElement.shadowSpread !== undefined) spread = faceElement.shadowSpread;
      if (faceElement.shadowOffset !== undefined) distance = faceElement.shadowOffset;
      if (faceElement.shadowRadius !== undefined) radius = faceElement.shadowRadius;
      if (faceElement.shadowAngle  !== undefined) angle = faceElement.shadowAngle;

      var x = 0;
      var y = 0;
      if (distance) {
        x = this.sin(angle+handangle-180) * distance * size;
        y = this.cos(angle+handangle-180) * distance * size;
      }

      if (faceElement.div && !faceElement.div.clip) {
        element.style.boxShadow = x + 'px '+y+'px ' + radius*size + 'px ' + spread*size + 'px ' + faceElement.shadowColor;
      } else if (faceElement.div && faceElement.div.clip) {
        element.style.filter = 'drop-shadow('+x+'px '+y+'px ' + radius*size + 'px ' + faceElement.shadowColor + ')';
      } else if (faceElement.img) {
        element.style.filter = 'drop-shadow('+x+'px '+y+'px ' + radius*size + 'px ' + faceElement.shadowColor + ')';
      }
    }
  }

  const seconds = now.getSeconds();
  const secondsDegree = (((seconds / 60) * 360) + 90);
  if (ConkW.data.cwClock.s) {
    ConkW.data.cwClock.s.style.transform = `rotate(${secondsDegree}deg)`
    var sec = ConkW.currentClockFace.second;
    this.setShadow(ConkW.data.cwClock.s, ConkW.currentClockFace.second, secondsDegree);
  }

  if (seconds%5==0 || forceUpdate) {
    const minutes = now.getMinutes() + seconds / 60;
    const minutesDegree = (((minutes / 60) * 360) + 90);
    ConkW.data.cwClock.m.style.transform = `rotate(${minutesDegree}deg)`

    if (seconds==0 || forceUpdate) {
      if (ConkW.currentClockFace.minute.shadowColor) {
        this.setShadow(ConkW.data.cwClock.m, ConkW.currentClockFace.minute, minutesDegree);
      }

      const hours = now.getHours() + minutes / 60;
      const hoursDegree = (((hours / 12) * 360) + 90);
      ConkW.data.cwClock.h.style.transform = `rotate(${hoursDegree}deg)`
      if (ConkW.currentClockFace.hour.shadowColor) {
        this.setShadow(ConkW.data.cwClock.h, ConkW.currentClockFace.hour, hoursDegree);
      }
    }
  }
}


ConkW.dates.setClockFace = function(face) {
  ConkW.currentClockFace = face;
  var clock = ConkW.data.cwClock;
  if (clock.s) clock.face.removeChild(clock.s);
  if (clock.m) clock.face.removeChild(clock.m);
  if (clock.h) clock.face.removeChild(clock.h);
  if (face.hour.div || face.hour.img) clock.face.appendChild(clock.h = document.createElement(face.hour.div ? "DIV" : "IMG"));
  if (face.minute.div || face.minute.img) clock.face.appendChild(clock.m = document.createElement(face.minute.div ? "DIV" : "IMG"));
  if (face.second.div || face.second.img) clock.face.appendChild(clock.s = document.createElement(face.second.div ? "DIV" : "IMG"));
  else clock.s = null;
  var size = clock.face.offsetWidth;
  ConkW.currentClockFace.offsetWidth = size;

  function setProps(style, element, faceElement) {

    if (faceElement.div) {
      style.height = (faceElement.div.width * size) + "px";
      style.width = (faceElement.div.forwardLength + faceElement.div.backLength) * 50 + '%';
      style.left = (1 - faceElement.div.forwardLength) * 50 + '%';
      style.top = (size - faceElement.div.width * size) / 2 + 'px';
      style.transformOrigin = 100 * faceElement.div.forwardLength / (faceElement.div.forwardLength + faceElement.div.backLength) + '% 50%';
      style.backgroundColor = faceElement.div.color;
      style.borderRadius = faceElement.div.radius ? faceElement.div.radius : "0";
      if (faceElement.div.clip) {
        var clipped = document.createElement("DIV");
        element.appendChild(clipped);
        clipped.style.backgroundColor = style.backgroundColor;
        clipped.style.height = style.height;
        style.backgroundColor="";
        clipped.style.clipPath = faceElement.div.clip;
      } else {
        style.clipPath = "";
      }
    }
    if (faceElement.img) {
      element.src=faceElement.img.src;

      var ratio = faceElement.img.width / faceElement.img.height;
      var width = faceElement.img.scale*size;

      style.width = width + "px";
      style.height = width/ratio + "px";
      style.left = (size/2-faceElement.img.centerPosition*size*faceElement.img.scale/faceElement.img.width) + "px"
      style.top = (size/2 - width*0.5/ratio) + "px";
      style.transformOrigin = faceElement.img.centerPosition*100/faceElement.img.width + "%";
    }
  }

  clock.root.style.backgroundImage = 'url("' + face.background + '")';
  if (clock.s) {
    clock.s.style.display = "";
    setProps(clock.s.style, clock.s, face.second);
  }
  setProps(clock.m.style, clock.m, face.minute);
  setProps(clock.h.style, clock.h, face.hour);
  this.updateClock(true);
}


ConkW.dates.rotateClockFace = function(event, date) {
  var e = event.target;
  while (e && e.tagName != "CLOCK") e = e.parentElement;
  if (e.tagName != "CLOCK") return;

  var cf = localStorage["conkw.clockFaceIndex"];
  if (!cf) cf = "0";
  cf = (cf - 0 + 1) % ConkW.clockFaces.length;
  localStorage["conkw.clockFaceIndex"] = cf;
  this.setClockFace(ConkW.clockFaces[cf]);
}

ConkW.dates.updateDates = function(now) {
  if (!ConkW.data.wmDates) {
    ConkW.data.wmDates = document.getElementsByTagName("cw-date");
  }
  var todo = ConkW.data.wmDates;
  for (var i = 0; i < todo.length; i++) {
    var cacheKey;
    var e = todo[i];
    var value = null;
    var id = e.getAttribute("cw-id");
    if (id) {
      switch (id) {
        case "dayoftheweek":
          value = this.getDow(now);
          break;
        case "dayofmonth":
          value = this.getDom(now);
          break;
        case "month":
          value = this.getMon(now);
          break;
        case "year":
          value = now.getFullYear();
          break;
      }
      cacheKey = "cw-dates-" + id;
      ConkW.updateField(e, "innerHTML", cacheKey, value, false);
    }
  }
}

ConkW.dates.getDow = function(date) {
  switch (date.getDay()) {
    case 0: return "Sunday";
    case 1: return "Monday";
    case 2: return "Tuesday";
    case 3: return "Wednesday";
    case 4: return "Thursday";
    case 5: return "Friday";
    case 6: return "Saturday";
  }
  return "wtf ?"
}

ConkW.dates.getMon = function(date) {
  switch (date.getMonth()) {
    case 0: return "January";
    case 1: return "February";
    case 2: return "March";
    case 3: return "April";
    case 4: return "May";
    case 5: return "June";
    case 6: return "July";
    case 7: return "August";
    case 8: return "September";
    case 9: return "October";
    case 10: return "November";
    case 11: return "December";
  }
  return "wtf ?"
}

ConkW.dates.getDom = function(date) {
  return date.getDate() + "";
}

ConkW.dates.formatTime = function(ts) {
  var date = new Date(ts);
  var h = date.getHours();
  var m = date.getMinutes();
  if (h < 10) h = "0" + h;
  if (m < 10) m = "0" + m;
  return h + ":" + m;
}
ConkW.dates.formatTimeSecs = function(ts) {
  var date = new Date(ts);
  var h = date.getHours();
  var m = date.getMinutes();
  var s = date.getSeconds();
  if (h < 10) h = "0" + h;
  if (m < 10) m = "0" + m;
  if (s < 10) s = "0" + s;
  return h + ":" + m + ":" + s;
}

ConkW.dates.formatHour = function(ts) {
  var date = new Date(ts);
  var h = date.getHours();
  return h + "h";
}

ConkW.dates.formatDow = function(ts) {
  return this.getDow(new Date(ts));
}

ConkW.dates.formatDate = function(ts) {
  var date = new Date(ts);
  return date.getFullYear() + "-" + date.getMonth() + "-" + date.getDate();
}

ConkW.dates.formatDatetime = function(ts) {
  return this.formatDate(ts) + " " + this.formatTime(ts);
}
 