window.onerror = function(message, file, lineNumber) {
  handleError(message + " " + file + ":" + lineNumber);
}
setInterval(updateStatus, 1000);

function init() {
  window.showMetricGap=false;
  window.values = {};
  window.geometry = 0;
  initDomCache();
  scheduleLoad();
  window.onresize = checkScreen;
  window.lastupdate = 0;
  window.debug = "";
  window.jsError = "";
  window.apiErrors = [];
  initClocks();
  setTimeout(()=>{window.geometry=0;checkScreen()}, 2000);
}

function handleError(e) {
  window.jsError = e;
}

function callApi(qs) {
  var xmlhttp = new XMLHttpRequest();
  var url = "/api?"+qs;
  xmlhttp.open("GET", url, true); // false = synchronous
  xmlhttp.onreadystatechange = function () {
    if (xmlhttp.readyState == 4) {
      var res = xmlhttp.responseText + "";
      if (res.charAt(0) == '{') {
        res = JSON.parse(res);
        console.log(res);
      }
    }
  }
  
  xmlhttp.send();
}

function scheduleLoad() {
  try {
    load();
  } catch (e) {
    handleError(e);
  }

  if (window.lastResponseJitter === undefined) {
    setTimeout(scheduleLoad, 1000);
  } else {
    let ttw = 1000 + (50 - window.lastResponseJitter)/5;
    if (ttw < 0) ttw+=1000;
    //console.log("w"+ttw+" " + (ttt2-ttt));
    setTimeout(scheduleLoad, ttw);
  }
}

function load() {
  updateDelay();
  var xmlhttp = new XMLHttpRequest();
  var url = "/api";
  var removeQS = false;
  if (location.href.indexOf("?")>0) {
    url += location.href.substring(location.href.indexOf("?"));
    removeQS=true;
  }
  xmlhttp.open("GET", url, true); // false = synchronous
  xmlhttp.onreadystatechange = function () { loaded(xmlhttp, removeQS); }
  xmlhttp.send();
  updateClock();
}

function updateDelay() {
  let e = document.getElementById("cw-delay");
  let v = (new Date().getTime() - window.lastupdate)/1000
  if (v>4) window.showMetricGap = true;
  if (e) {
    if (window.lastupdate) {
      e.innerText = getTimeLabel(v) + "/" + window.lastResponseJitter + "ms/" + window.debug+"ms";
      e.className = v>4 ? "error":"";
    } else {
      e.innerText = "Starting";
    }
  }
}

function displayErrors() {
  if (document.getElementById("errorDiv")) document.body.removeChild(document.getElementById("errorDiv"));
  e = document.createElement("div");
  e.id = "errorDiv";
  e.onclick = function() {document.body.removeChild(e);}
  let html = "";
  if (window.jsError) html += window.jsError + "<hr>";
  if (window.apiErrors) {
    window.apiErrors.forEach(e => {html += e + "<hr>";});
  }
  e.innerHTML = html;
  document.body.appendChild(e);
}

function updateStatus() {
  let e = document.getElementById("cw-status");
  if (e) {
    if (!e.onclick) e.onclick=displayErrors;
    let count = 0;
    if (window.jsError) count++;
    if (window.apiErrors && window.apiErrors.length) count+= window.apiErrors.length;

    let value = (count) ? (count + " errors") : "ok";
    updateField(e, "innerHTML", "cw-status", value, value != "ok" ? "yes" : "no")
  }
}

function zoom(zl) {
  console.log("zooming to " + zl);
  document.body.style.transform = "scale("+(zl/100)+")";
}

function getGeometry() {
  return window.innerHeight + " " + window.innerWidth;//window.document.body.scrollHeight +  + " " + window.document.body.scrollWidth;
}

function checkScreen() {
  if (geometry != getGeometry()) {
    zoom(100);
    geometry = getGeometry()
    setTimeout(function() {
      var zv = window.innerHeight*100 / window.document.body.scrollHeight;
      var zh = window.innerWidth*100 / window.document.body.scrollWidth;
      var tz = Math.min(100,zv,zh);
      if (tz<100) tz *= 0.99;
      window.zoomlevel = tz;
      zoom(tz);
  
    }, 1000);
  }
}

function loaded(req, removeQS) {
  try {
    if (req.readyState == 4) {
      var res = req.responseText + "";
      if (res.charAt(0) == '{') {
        let before = Date.now();
        res = JSON.parse(res);
        window.lastResponseJitter = res.responseJitter;
        //console.log(res.responseJitter);
        refresh(res);
        window.apiErrors = res.errors;
        if (removeQS && location.href.indexOf("?")>0) {
          location.href = '/';
        }
        let after = Date.now();
        window.debug=after-before;
      }
      window.showMetricGap=false;
    }
  } catch (e) {
    handleError(e);
  }
}

function setStatus(msg) {
  document.getElementById("str_status").innerHTML = msg;
  document.getElementById("str_status").title = msg;
  document.getElementById("str_status").className = "error";
}

const fillTemplate = function(templateString, templateVars){
  try {
    var func = new Function(...Object.keys(templateVars),  "return `"+templateString +"`;")
    return func(...Object.values(templateVars));
  } catch (e) {
    console.log("Template " + templateString + " failed: " + e + " on model " + JSON.stringify(templateVars));
    setStatus("Template " + templateString + " failed: " + e + " on model " + JSON.stringify(templateVars));
  }
}

function refresh(data) {
  window.lastupdate = new Date().getTime();
  refreshNew(data);
}

function getPercent(value, min, max, log) {
  let posprc = (value-min)*100/(max-min);
  if (posprc<0) posprc = 0;
  if (posprc>100) posprc = 100;
  if (log) {
    posprc = posprc*0.99+1;
    posprc = Math.log10(posprc)*50;
    posprc = (posprc-1)*100/99;
  }

  return posprc;
}

function  getProperLabel(key, value) {
  if (!key) return ""+getPrecision(value);
  switch (key) {
    case "size": return getSizeLabel(value);
    case "temp": return getTempLabel(value);
    case "wtemp": return getWTempLabel(value);
    case "cpu": return getPercentLabel(value);
    case "prc": return getPercentLabel(value);
    case "stockchangeprc": return getStockChange(value*100);
    case "stockchange": return getStockChange(value);
    case "time": return getTimeLabel(value);
    case "load": return getLoadLabel(value);
    case "rpm": return getRpmLabel(value);
    case "rawint": return ""+Math.round(value);
    case "hhmm": return getHHMMLabel(value);
    case "tstohhmm": return formatTime(value);
    case "tsstohhmm": return formatTime(value*1000);
    case "tsstoh": return formatHour(value*1000);
    case "tstodow": return formatDow(value);
    case "tsstodow": return formatDow(value*1000);
    case "tsstodow3": return formatDow(value*1000).substring(0, 3);
    case "currencyBig": return formatCurrency(value);
  }
  if (key.startsWith("fixeddec.")) {
    return Number(value).toFixed(key.split(".")[1]-0);
  }
  return ""+getPrecision(value);
}

function getStockChange(value) {
  let res = Number(value).toFixed(2);
  if (value>0) res = "+"+res;
  /*while (res.length < 8) res = " " + res;
  while (res.length > 8) res = res.substring(0,res.length-1);*/
  return res;
}

function formatCurrency(value) {
  let mult=" ";
  if (value > 1000) {
    value /= 1000;
    mult = "k";
  }
  if (value > 1000) {
    value /= 1000;
    mult = "M";
  }
  if (value > 1000) {
    value /= 1000;
    mult = "B";
  }
  let res = Number(value).toFixed(2) + mult;
  while (res.length < 8) res = " " + res;
  return res;
}

function getHHMMLabel(value) {
  value = Math.round(value);
  var h = ""+Math.floor(value / 60);
  if (h.length == 1) h = "0"+h;
  var m = ""+Math.floor(value % 60);
  if (m.length == 1) m = "0"+m;
  return h+":"+m;
}

function getRpmLabel(value) {
  let res = "" + Math.round(value);
  while (res.length < 4) res = "&nbsp;" + res;
  return res + " rpm";
}

function getLoadLabel(i) {
  let res = ""+i;
  if (res.includes(".")) {
    if (i<10) i = Math.round(i*1000)/1000;
    else if (i<100) i = Math.round(i*100)/100;
    else if (i<1000) i = Math.round(i*1000)/1000;
    else i = Math.round(i);
    res = ""+i;
  }
  let missing = 5 - res.length;
  while (missing-- > 0) res = res+"&nbsp;";
  return res;
}

function getTimeLabel(value) {
  let res = "";
  value = Math.round(value);
  res = (value%60) + 's';
  value = Math.floor(value/60);
  if (value==0) return res;
  res = (value%60) + 'm ' + res;
  value = Math.floor(value/60);
  if (value==0) return res;
  res = (value%24) + 'h ' + res;
  value = Math.floor(value/24);
  if (value==0) return res;
  res = (value%365) + 'd ' + res;
  value = Math.floor(value/365);
  if (value==0) return res;
  res = value + 'y ' + res;

  return res;
}

function  getTempLabel(value) {
  return getPrecision(value) + "&deg;C";
}
function  getWTempLabel(value) {
  return Math.round(value) + "&deg;";
}

function getPercentLabel(value) {
  return getPrecision(value) + "%";
}

function getSizeLabel(i) {
  if (i<1024) return getPrecision(i) + "B&nbsp;";
  i /= 1024;
  if (i<1024) return getPrecision(i) + "KB";
  i /= 1024;
  if (i<1024) return getPrecision(i) + "MB";
  i /= 1024;
  if (i<1024) return getPrecision(i) + "GB";
  i /= 1024;
  return getPrecision(i) + "TB";
}

function getPrecision(i) {
  let res = ""+i;
  if (res.includes(".")) {
    if (i<10) i = Math.round(i*100)/100;
    else if (i<100) i = Math.round(i*10)/10;
    else i = Math.round(i);
    res = ""+i;
  }
  let missing = 4 - res.length;
  while (missing-- > 0) res = res+" ";
  return res;
}


function initDomCache() {
  window.holders = [];
  loadDom(document.body)
}

function loadDom(e) {
  let ns = e.getAttribute("cw-ns");

  function add(e, attrName, build) {
    let v = e.getAttribute(attrName);
    if (v) window.holders.push(build(e, v));
  }

  if (ns) {
    let names = e.getAttributeNames();
    for (let i=0 ; i<names.length ; i++) {
      if (names[i].startsWith("cw-prop-")) {
        window.holders.push(new PropertyHolder(e,e.getAttribute("cw-ns"),e.getAttribute(names[i]),names[i].split("-")[2]));
      }
      if (names[i].startsWith("cw-style-")) {
        window.holders.push(new PropertyHolder(e.style,e.getAttribute("cw-ns"),e.getAttribute(names[i]),names[i].split("-")[2]));
      }
      switch (names[i]) {
        case "cw-stale":
          window.holders.push(new StaleHolder(e,e.getAttribute(names[i])));
          break;
        case "cw-value":
          window.holders.push(new ValueHolder(e,e.getAttribute(names[i])));
          break;
        case "cw-gauge0":
          window.holders.push(new GaugeHolder(e,e.getAttribute(names[i])));
          break;
        case "cw-hgauge0":
          window.holders.push(new HistoryGaugeHolder(e,e.getAttribute(names[i])));
          break;
      }
    }

    if (!e.getAttribute("cw-stale") && e.getAttribute("cw-stale") && e.getAttribute("cw-stale").startsWith("m:")) {
      window.holders.push(new StaleHolder(e, v));
    }
  }

  expandMultiNode(e);

  for (let i = 0 ; i<e.childElementCount ; i++)
    loadDom(e.children[i]);
}

function expandMultiNode(e) {
  if (e.getAttribute("cw-multinode-values") && e.getAttribute("cw-multinode-pattern")) {
    let values = [];
    let v = e.getAttribute("cw-multinode-values").split("-");
    let pattern = e.getAttribute("cw-multinode-pattern");
    let html = "";
    for (let i=v[0] ; i<=v[1] ; i++) {
      values.push(""+i);
    }
    for (let i=0 ; i<values.length ; i++) {
      html += e.innerHTML.replaceAll(pattern, values[i]);
    }
    e.innerHTML=html;
    e.removeAttribute("cw-multinode-values");
    e.removeAttribute("cw-multinode-pattern");
  }
}

function refreshNew(data) {
  let todo = window.holders;
  for (let i=0 ; i<todo.length ; i++) {
    let e = todo[i];
    e.update(data.metrics[e.ns]);
  }
}

function parseValueExpression(id) {
  if (!id) {
    return {
      invalid:true
    }
  }
  let c1 = id.indexOf(":");
  let c2 = id.indexOf(":", c1+1);
  let c3 = id.indexOf(":", c2+1);

  return {
    valuetype:id.substring(0, c1),
    datatype:id.substring(c1+1, c2),
    format:id.substring(c2+1, c3),
    expression:id.substring(c3+1)
  }
}

function extractMetricDelay(valueexpr, data) {
  let now = Date.now()/1000;
  if (!data) return undefined;
  switch (valueexpr.valuetype) {
    case "m":
      let mlv = data.num["ts:"+valueexpr.expression];
      if (mlv === undefined) mlv = data.timestamp;
      if (mlv === undefined) return now;
      return now - mlv/1000;
  }
  return undefined;
}

function extractRawValue(valueexpr, data) {
  if (!data) return undefined;
  switch (valueexpr.valuetype) {
    case "m":
      let ns = data[valueexpr.datatype];
      return ns ? ns[valueexpr.expression] : undefined;
    case "e":
      return fillTemplate(valueexpr.expression, data);
    case "l":
      return valueexpr.expression;
  }
  return null;
}

function parseDelayInSeconds(string) {
  let mult = 1;
  if (string.endsWith("m")) mult = 60;
  if (string.endsWith("h")) mult = 3600;
  if (string.endsWith("d")) mult = 86400;
  return parseInt(string)*mult;
}

function extractTypedValue(valueexpr, data) {
  let value = extractRawValue(valueexpr, data);
  if (value === undefined) return;
  switch (valueexpr.datatype) {
    case "str":
      
      return ""+value;
    case "num":
      return value - 0;
  }
  return null;
}

function extractFormattedValue(valueexpr, data) {
  let value = extractTypedValue(valueexpr, data);
  if (value === undefined) return;
  switch (valueexpr.datatype) {
    case "str":
      if (valueexpr.format.startsWith("fixedlen."))
        return toFixedLength(value, valueexpr.format.split(".")[1]-0);
      return "" + value;
    case "num":
      value = value - 0;
      return getProperLabel(valueexpr.format, value);
  }
  return null;
}

function toFixedLength(value, chars) {
  if (value.length>chars) value = value.substring(0,chars);
  while (value.length<chars) value = " "+value;
  return value;
}


function updateField(field, property, cacheKey, value, warning) {
  if (window.values[cacheKey] !== value) {
    let oldvalue = window.values[cacheKey];
    window.values[cacheKey] = value;
    if (value === undefined) {
      field.classList.add("cw-stale");
      return;
    }
    if (oldvalue === undefined && field.classList) {
      field.classList.remove("cw-stale");
    }
    field[property] = value;
    if (warning && field.classList) {
      if (warning == "yes") 
        field.classList.add("error");
      else
        field.classList.remove("error");
    }
  }
}

function isWarning(vExpr, wExpr, data) {
  if (wExpr && !wExpr.invalid) {
    let vv = extractRawValue(vExpr, data);
    let vw = extractRawValue(wExpr, data);
    switch (wExpr.format) {
      case "ifnot":
        return vv != vw ? "yes" : "no";
      case "if":
        return vv == vw ? "yes" : "no";
      case "above":
        return vv > vw ? "yes" : "no";
      case "below":
        return vv < vw ? "yes" : "no";
      }
  }
  return false;
}

function isStale(vExpr, data) {
  if (vExpr && !vExpr.invalid) {
    let f = vExpr.format;
    if (f.startsWith("olderThan.")) {
      return extractMetricDelay(vExpr, data) > parseDelayInSeconds(f.substring(10));
    }
    return extractRawValue(vExpr, data) === undefined;
  }
  return false;
}

class PropertyHolder {
  constructor(e,ns,v,pn) {
    this.element = e;
    this.propertyName = pn;
    this.valueExpr = parseValueExpression(v);
    this.cacheKey = pn+"." + v;
    this.ns = ns;
    window.values[this.cacheKey] = "--not-a-valid-value--";
  }
  update(data) {
    let v = extractRawValue(this.valueExpr, data);
    updateField(this.element, this.propertyName, this.cacheKey, v, false);
  }
}

class ValueHolder {
  constructor(e,v) {
    this.element = e;
    this.valueExpr = parseValueExpression(v);
    this.cacheKey = "value." + v;
    this.ns = e.getAttribute("cw-ns");
    this.warn = parseValueExpression(e.getAttribute("cw-warn"));
    window.values[this.cacheKey] = "--not-a-valid-value--";
  }
  update(data) {
    let v = extractFormattedValue(this.valueExpr, data);
    updateField(this.element, "innerHTML", this.cacheKey, v, isWarning(this.valueExpr, this.warn, data));
  }
}

class StaleHolder {
  constructor(e,v) {
    this.element = e;
    this.valueExpr = parseValueExpression(v);
    this.cacheKey = "value." + v;
    this.ns = e.getAttribute("cw-ns");
    window.values[this.cacheKey] = "--not-a-valid-value--";
  }
  update(data) {
    let stale = isStale(this.valueExpr, data);
    if (window.values[this.cacheKey]!=stale) {
      window.values[this.cacheKey] = stale;
      if (stale) {
        this.element.classList.add("cw-stale");
      } else {
        this.element.classList.remove("cw-stale");
      }
    }
  }
}

class GaugeHolder {
  constructor(e,fv) {
    this.element = e;
    this.ns = e.getAttribute("cw-ns");
    this.min = parseValueExpression(e.getAttribute("cw-min"));
    this.max = parseValueExpression(e.getAttribute("cw-max"));
    this.warn = parseValueExpression(e.getAttribute("cw-warn"));
    this.wmax = e.getAttribute("cw-warningmax") === "true";

    let green = document.createElement("div");
    green.className="red" + (this.wmax ? "left" : "right");
    e.appendChild(green);

    this.valueExprs=[];
    let ck = "";
    let i=0;
    while (true) {
      let fv = e.getAttribute("cw-gauge"+i);
      if (!fv) break;
      let idx = fv.indexOf(":");
      let v = fv.substring(idx+1);
      ck += v + "/"
      this.valueExprs.push(parseValueExpression(v));

      let gauge = document.createElement("div");
      gauge.className="gauge";
      let col = fv.substring(0,idx);
      if (col !== "default") gauge.style.backgroundColor = col;
      e.appendChild(gauge);
      i++;
    }
    this.cacheKey = "gauge." + ck + e.getAttribute("cw-min") + e.getAttribute("cw-max") + e.getAttribute("cw-warn") + e.getAttribute("cw-warningmax");
    window.values[this.cacheKey] = "--not-a-valid-value--";
  }
  update(data) {
    let min = extractTypedValue(this.min, data);
    let max = extractTypedValue(this.max, data);
    let warn = this.warn.invalid ? null : extractTypedValue(this.warn, data);

    let greenWidth = warn ? (this.wmax ? ((warn-min)*100/(max-min))+"%" : (100-((warn-min)*100/(max-min))+"%")) : "0";
    let cacheValue = greenWidth + "/";
    let gw = [];
    let gcn = [];
    for (let i=0 ; i<this.valueExprs.length ; i++) {
      let value = extractTypedValue(this.valueExprs[i], data);
      let wn = ((value-min)*100/(max-min));
      gw[i] = wn;
      let w = warn ? (this.wmax ? value < warn : value > warn) : false;
      let gaugeCN=w ? "gaugewarning" : "gauge";
      gcn[i] = gaugeCN;
      cacheValue = cacheValue + "/" + gw;
    }

    if (cacheValue !== window.values[this.cacheKey]) {
      window.values[this.cacheKey] = cacheValue;
      let green = this.element.children[0];
      green.style.width = greenWidth;
      let left = 0;
      for (let i=0 ; i<this.valueExprs.length ; i++) {
        let gauge = this.element.children[i+1];
        gauge.style.width = gw[i] + "%";
        gauge.style.left = left + "%";
        left += gw[i];
        gauge.className = gcn[i];
      }
    }
  }
}


class HistoryGaugeHolder {
  constructor(e,fv) {
    this.element = e;
    this.ns = e.getAttribute("cw-ns");
    this.log = e.getAttribute("log") == "true";
    this.min = parseValueExpression(e.getAttribute("cw-min"));
    this.max = parseValueExpression(e.getAttribute("cw-max"));
    this.warn = parseValueExpression(e.getAttribute("cw-warn"));
    this.wmax = e.getAttribute("cw-warningmax") === "true";

    this.colors=[];
    this.valueExprs=[];
    let i=0;
    while (true) {
      let fv = e.getAttribute("cw-hgauge"+i);
      if (!fv) break;
      let idx = fv.indexOf(":");
      let v = fv.substring(idx+1);
      this.colors.push(fv.substring(0,idx));
      this.valueExprs.push(parseValueExpression(v));
      i++;
    }
  }
  update(data) {
    let e = this.element;
    let min = extractTypedValue(this.min, data);
    let max = extractTypedValue(this.max, data);

    let bottom=0;

    if (window.showMetricGap) {
      let container = document.createElement("div");
      container.className = "ch error";
      e.appendChild(container);
    }

    let container = document.createElement("div");
    container.className = "ch";
    for (let i=0 ; i<this.colors.length ; i++) {
      let value = extractTypedValue(this.valueExprs[i], data);
      let color = this.colors[i];
      let bar = document.createElement("div");
      let posprc = getPercent(value, min, max, this.log);
      bar.style.height = posprc+"%";
      bar.style.bottom = bottom+"%";
      bottom+=posprc;
      bar.style.backgroundColor = color;
      container.appendChild(bar);
    }
    e.appendChild(container);
    while (e.childElementCount > 199) {
      e.removeChild(e.firstChild);
    }
  }
}
