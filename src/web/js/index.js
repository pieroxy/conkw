window.ConkW = window.ConkW || {};
ConkW.formatters = ConkW.formatters || {};
ConkW.data = ConkW.data || {funcs:{}};


window.onerror = function(message, file, lineNumber) {
    ConkW.handleError(message + " " + file + ":" + lineNumber);
}
setInterval(function() { ConkW.updateStatus() }, 1000);

ConkW.initStatic = function() {
    ConkW.showMetricGap = false;
    ConkW.data.geometry = 0;
    ConkW.data.cachedValues = {};
    ConkW.data.lastupdate = 0;
    ConkW.data.debug = "";
    ConkW.data.jsError = "";
    ConkW.data.apiErrors = [];
}

ConkW.initStatic();

ConkW.init = function() {
    ConkW.data.sid = localStorage.getItem("sid");
    var zis = this;
    var options = document.body.getAttribute("cw-options") || "";
    var checkScreenFlag = options.indexOf("noresize")==-1;
    this.initDomCache();
    this.scheduleLoad(true);
    if (checkScreenFlag) window.onresize = function() {zis.checkScreen()};
    this.dates.initClocks();
    if (checkScreenFlag) this.checkScreen();
    this.initDocumentation();
}

ConkW.initDocumentation = function() {
    var fbc = document.body.children[0];
    if (fbc && fbc.tagName=="MD") {
        console.log("MD detected");
        var fmdc = fbc.children[0];
        if (fmdc && fmdc.tagName=="H1") {
            fmdc.innerHTML = '<inlinelogo></inlinelogo><a href="/">' + fmdc.innerHTML + "</a>";
        }
    }
}

ConkW.handleError = function(e) {
    console.log(e);
    ConkW.data.jsError = e;
}

ConkW.callApi = function(qs) {
    var xmlhttp = new XMLHttpRequest();
    var url = "/api?" + qs + ConkW.getAuthToAppendToUrl();
    xmlhttp.open("GET", url, true); // false = synchronous
    xmlhttp.onreadystatechange = function() {
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

ConkW.scheduleLoad = function(forcenow) {
    var nowb = Date.now();
    var jnow = nowb%1000;
    if (jnow>150 && !forcenow) {
        setTimeout(ConkW.scheduleLoad, Math.round(1050 - jnow));
        return;
    }
    try {
        ConkW.load();
    } catch (e) {
        ConkW.handleError(e);
    }

    var now = Date.now();
    var ttw = Math.round(1000 + 50 - now%1000);
    if (ttw < 0) ttw += 1000;
    //console.log("w"+ttw+" " + (ttt2-ttt));
    setTimeout(ConkW.scheduleLoad, ttw);
}

ConkW.sendActionToServer = function() {
    var url = location.href;
    var xmlhttp = new XMLHttpRequest();
    console.log(url);
    var pos = url.indexOf("%3BgrabberAction%3D")+19;
    var grabber = url.substring(pos, url.indexOf("%3B", pos));
    var targeturl = "/api?grabberAction="+grabber + ConkW.getAuthToAppendToUrl();
    targeturl += "&" + url.substring(url.indexOf("?")+1);
    xmlhttp.open("GET", targeturl, true); // false = synchronous
    xmlhttp.onreadystatechange = function() { location.href = url.substring(0, url.indexOf("?")) }
    xmlhttp.send();
}

ConkW.getAuthToAppendToUrl = function() {
    var appendToUrl = "";
    if (ConkW.data.sid) {
        appendToUrl = "&__SID__=" + ConkW.data.sid;
    }
    return appendToUrl;
}

ConkW.load = function() {
    var grabbers = document.body.getAttribute("cw-grabbers");
    if (grabbers) {
        this.updateDelay();
        if (location.href.indexOf("?") > 0) {
            ConkW.sendActionToServer();
        } else {
            var requestTime = Date.now();
            var xmlhttp = new XMLHttpRequest();
            var url = "/api?grabbers=" + grabbers + ConkW.getAuthToAppendToUrl();
            xmlhttp.open("GET", url, true); // false = synchronous
            xmlhttp.onreadystatechange = function() { ConkW.loaded(xmlhttp, requestTime); }
            xmlhttp.send();
        }
    }
    this.dates.updateClock();
}

ConkW.updateDelay = function() {
    var e = document.getElementById("cw-delay");
    var v = Math.round(Date.now() - ConkW.data.lastupdate)
    if (v > 5000) ConkW.showMetricGap = true;
    if (e) {
        if (ConkW.data.lastupdate) {
            e.innerHTML = v + "/" + ConkW.data.lastResponseJitter + "/" + ConkW.data.debug;
            e.className = v > 5000 ? "cw-error" : "";
        } else {
            e.innerText = "Starting";
        }
    }
}

ConkW.displayErrors = function() {
    if (document.getElementById("errorDiv")) document.body.removeChild(document.getElementById("errorDiv"));
    e = document.createElement("div");
    e.id = "errorDiv";
    e.onclick = function() { document.body.removeChild(e); }
    var html = "";
    if (ConkW.data.jsError) html += ConkW.data.jsError + "<hr>";
    if (ConkW.data.apiErrors) {
        ConkW.data.apiErrors.forEach(function (e) { html += e + "<hr>"; });
    }
    e.innerHTML = html;
    document.body.appendChild(e);
}

ConkW.updateStatus = function() {
    var e = document.getElementById("cw-status");
    if (e) {
        if (!e.onclick) e.onclick = function() {ConkW.displayErrors();}
        var count = 0;
        if (ConkW.data.jsError) count++;
        if (ConkW.data.apiErrors && ConkW.data.apiErrors.length) count += ConkW.data.apiErrors.length;

        var value = (count) ? (count + " errors") : "ok";
        this.updateField(e, "innerHTML", "cw-status", value, value != "ok" ? "yes" : "no")
    }
}

ConkW.zoom = function(zl) {
    console.log("zooming to " + zl);
    document.body.style.transform = "scale(" + (zl / 100) + ")";
    document.body.style.width = (10000/zl) + "%";
}

ConkW.getGeometry = function() {
    return window.innerHeight + " " + window.innerWidth; //window.document.body.scrollHeight +  + " " + window.document.body.scrollWidth;
}

ConkW.forceScreenRefresh = function() {
    ConkW.data.geometry="";
}

ConkW.checkScreen = function() {
    if (ConkW.data.geometry != this.getGeometry()) {
        var zd = document.getElementById("cw-zoom");
        this.zoom(100);
        var ctz = 100;
        while (true) {
            ConkW.data.geometry = this.getGeometry()
            var zv = window.innerHeight * (100) / window.document.body.scrollHeight;
            var zh = window.innerWidth * (10000/ctz) / window.document.body.scrollWidth;
            var tz = Math.min(100, zv, zh);
            if (tz < 100) tz *= 0.99;
            if (tz >= ctz) {
                if (zd) zd.innerText = Math.round(ctz)+'%';
                break;
            }

            ConkW.data.zoomlevel = ctz = ctz*0.95;
            this.zoom(ctz);
        }
    }
}

ConkW.displayLogin = function() {
    if (document.getElementById("loginDiv")) return;

    var s = document.createElement("script");
    s.src="/js/sha1.js";
    document.body.appendChild(s);

    var e = document.createElement("div");
    e.id = "dialogVeil";
    var html = '<div id="loginDiv">Authentication Needed<br><br><table><tr><td>Login: <td><input autocapitalize="none" id="loginfield" type="text"><tr><td>Password: <td><input id="passwordfield" type="password"><tr><td><td><input type="button" value="ok" onclick="ConkW.doLogin()"></table></div>';
    e.innerHTML = html;
    document.body.appendChild(e);
    document.getElementById("loginfield").focus();
}

ConkW.doLogin = function() {
    var loginfield = document.getElementById("loginfield");
    var passwordfield = document.getElementById("passwordfield");
    if (loginfield && passwordfield) {
        var xmlhttp = new XMLHttpRequest();
        var url = "/api?__U__=" + encodeURIComponent(loginfield.value);
        xmlhttp.open("GET", url, true);
        xmlhttp.onreadystatechange = function() { 
            if (xmlhttp.readyState == 4) {
                var res = xmlhttp.responseText + "";
                if (res.charAt(0) == '{') {
                    res = JSON.parse(res);
                    ConkW.doSendPassword(res.saltForPassword);
                }
            }
        }
        xmlhttp.send();
    }
}

ConkW.doSendPassword = function(salt) {
    var passwordfield = document.getElementById("passwordfield");
    var xmlhttp = new XMLHttpRequest();
    var url = "/api?__U__=" + encodeURIComponent(loginfield.value) + "&__P__=" + SHA1.get().update(salt + passwordfield.value).hex();
    xmlhttp.open("GET", url, true);
    xmlhttp.onreadystatechange = function() { 
        if (xmlhttp.readyState == 4) {
            var res = xmlhttp.responseText + "";
            if (res.charAt(0) == '{') {
                res = JSON.parse(res);
                if (res.sessionToken) {
                    localStorage.setItem("sid", res.sessionToken);
                    ConkW.data.sid = res.sessionToken;
                    document.body.removeChild(document.getElementById("dialogVeil"));
                }
                if (res.errorMessage) {
                    alert(res.errorMessage);
                }
            }
        }
}
    xmlhttp.send();
}

ConkW.loaded = function(req, requestTime) {
    try {
        if (req.readyState == 4) {
            var res = req.responseText + "";
            if (res.charAt(0) == '{') {
                var before = Date.now();
                var loadTime = before - requestTime;
                res = JSON.parse(res);
                if (res.needsAuthentication) {
                    ConkW.displayLogin();
                } else {
                    ConkW.data.lastResponseJitter = res.responseJitter;
                    //console.log(res.responseJitter);
                    ConkW.refresh(res);
                    ConkW.data.apiErrors = res.errors;
                    // The following forces a synchronous redraw
                    // No performance hit since a redraw needs to happen
                    // but allows monitoring of the drawing time.
                    document.body.offsetHeight; 
                    var after = Date.now();
                    ConkW.data.debug = loadTime + "|" + (after - before);
                }
            }
            ConkW.showMetricGap = false;
        }
    } catch (e) {
        this.handleError(e);
    }
}

ConkW.setStatus = function(msg) {
    var status = document.getElementById("str_status");
    if (status) {
        status.innerHTML = msg;
        status.title = msg;
        status.className = "cw-error";
    } else {
        console.log("Failed to set status to " + msg);
    }
}

try {
    eval('ConkW.fillTemplate = function (templateString, templateVars) {\n'+
    '    try {\n'+
    '        var func = ConkW.data.funcs[templateString];\n'+
    '        if (!func) {\n'+
    '            console.log("creating " + templateString);\n'+
    '            ConkW.data.funcs[templateString] = func = new Function(...Object.keys(templateVars), "return `" + templateString + "`;");\n'+
    '        }\n'+
    '    \n'+
    '        return func(...Object.values(templateVars));\n'+
    '    } catch (e) {\n'+
    '        console.log("Template " + templateString + " failed: " + e + " on model " + JSON.stringify(templateVars));\n'+
    '        ConkW.setStatus("Template " + templateString + " failed: " + e + " on model " + JSON.stringify(templateVars));\n'+
    '    }\n'+
    '}');
} catch (e) {
    console.log("Using legacy template parsing.");
}

if (!ConkW.fillTemplate) { // Old browsers that cannot parse the above code.
    ConkW.getFFT = function(path, obj) {
        var f = ConkW.data.funcs[path];
        if (!f) {
            console.log("creating " + path);
            ConkW.data.funcs[path] = f = Function("num", "str", "timestamp", "return " + path);
        }
        var res = f(obj.num, obj.str, obj.timestamp);
        return res;
    }
    
    ConkW.fillTemplate = function(template, map) {
        var res = template.replace(/\$\{.+?}/g, function(match) {
            const path = match.substr(2, match.length - 3).trim();
            return ConkW.getFFT(path, map);
        });
        return res;
    }
}  

ConkW.refresh = function(data) {
    ConkW.data.lastupdate = Date.now();
    this.refreshNew(data);
    this.checkScreen();
}

ConkW.getPercent = function(value, min, max, log) {
    var posprc = (value - min) * 100 / (max - min);
    if (posprc < 0) posprc = 0;
    if (posprc > 100) posprc = 100;
    if (log) {
        posprc = posprc * 0.99 + 1;
        posprc = Math.log10(posprc) * 50;
        posprc = (posprc - 1) * 100 / 99;
    }

    return posprc;
}

ConkW.getProperLabel = function(key, value) {
    if (!key) return "" + this.getPrecision(value);
    switch (key) {
        case "size":
            return this.getSizeLabel(value);
        case "datarate":
            return this.getDataRateLabel(value);
        case "temp":
            return this.getTempLabel(value);
        case "wtemp":
            return this.getWTempLabel(value);
        case "cpu":
            return this.getPercentLabel(value);
        case "prc":
            return this.getPercentLabel(value);
        case "prc01":
            return this.getPercentLabel(value * 100);
        case "stockchangeprc":
            return this.getStockChange(value * 100);
        case "stockchange":
            return this.getStockChange(value);
        case "time":
            return this.getTimeLabel(value);
        case "time_ms":
            return this.getTimeLabel(value/1000);
        case "load":
            return this.getLoadLabel(value);
        case "rpm":
            return this.getRpmLabel(value);
        case "rawint":
            return "" + Math.round(value);
        case "hhmm":
            return this.getHHMMLabel(value);
        case "tstohhmmss":
            return this.dates.formatTimeSecs(value);
        case "tstohhmm":
            return this.dates.formatTime(value);
        case "tsstohhmm":
            return this.dates.formatTime(value * 1000);
        case "tsstoh":
            return this.dates.formatHour(value * 1000);
        case "tstodow":
            return this.dates.formatDow(value);
        case "tsstodow":
            return this.dates.formatDow(value * 1000);
        case "tsstodate":
            return this.dates.formatDate(value * 1000);
        case "tstodate":
            return this.dates.formatDate(value);
        case "tstodatetime":
            return this.dates.formatDatetime(value);
        case "tsstodatetime":
            return this.dates.formatDatetime(value * 1000);
        case "si":
            return this.getSI(value);
        case "tsstodow3":
            return this.dates.formatDow(value * 1000).substring(0, 3);
        case "currencyBig":
            return this.formatCurrency(value);
        case "yesno":
            return value===1 ? "Yes" : "No";
    }
    if (key.startsWith("fixeddec.")) {
        return Number(value).toFixed(key.split(".")[1] - 0);
    }
    return "" + this.getPrecision(value);
}

ConkW.buttonPushed = function (e) {
    if (e.src.indexOf("pushed")==-1) e.src = e.src.replace(".png", "-pushed.png");
}
ConkW.buttonReleased = function (e) {
    setTimeout(function() { e.src = e.src.replace("-pushed.png", ".png"); }, 200);
}



ConkW.getStockChange = function(value) {
    var res = Number(value).toFixed(2);
    if (value > 0) res = "+" + res;
    //while (res.length < 8) res = " " + res;
    //while (res.length > 8) res = res.substring(0,res.length-1);
    return res;
}

ConkW.formatCurrency = function(value) {
    var mult = " ";
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
    var res = Number(value).toFixed(2) + mult;
    while (res.length < 8) res = " " + res;
    return res;
}

ConkW.getHHMMLabel = function(value) {
    value = Math.round(value);
    var h = "" + Math.floor(value / 60);
    if (h.length == 1) h = "0" + h;
    var m = "" + Math.floor(value % 60);
    if (m.length == 1) m = "0" + m;
    return h + ":" + m;
}

ConkW.getRpmLabel = function(value) {
    var res = "" + Math.round(value);
    while (res.length < 4) res = " " + res;
    return res + " rpm";
}

ConkW.getLoadLabel = function(i) {
    var res = "" + i;
    if (res.includes(".")) {
        if (i < 10) i = Math.round(i * 1000) / 1000;
        else if (i < 100) i = Math.round(i * 100) / 100;
        else if (i < 1000) i = Math.round(i * 1000) / 1000;
        else i = Math.round(i);
        res = "" + i;
    }
    var missing = 5 - res.length;
    while (missing-- > 0) res = res + " ";
    return res;
}

ConkW.getTimeLabel = function(value) {
    var res = "";
    value = Math.round(value);
    res = (value % 60) + 's';
    value = Math.floor(value / 60);
    if (value == 0) return res;
    res = (value % 60) + 'm ' + res;
    value = Math.floor(value / 60);
    if (value == 0) return res;
    res = (value % 24) + 'h ' + res;
    value = Math.floor(value / 24);
    if (value == 0) return res;
    res = (value % 365) + 'd ' + res;
    value = Math.floor(value / 365);
    if (value == 0) return res;
    res = value + 'y ' + res;

    return res;
}

ConkW.getTempLabel = function(value) {
    return this.getPrecision(value) + "&deg;C";
}

ConkW.getWTempLabel = function(value) {
    return Math.round(value) + "&deg;";
}

ConkW.getPercentLabel = function(value) {
    return this.getPrecision(value) + "%";
}

ConkW.getSizeLabel = function(i) {
    if (i < 1024) return this.getPrecision(i) + "B&nbsp;";
    i /= 1024;
    if (i < 1024) return this.getPrecision(i) + "KB";
    i /= 1024;
    if (i < 1024) return this.getPrecision(i) + "MB";
    i /= 1024;
    if (i < 1024) return this.getPrecision(i) + "GB";
    i /= 1024;
    return this.getPrecision(i) + "TB";
}

ConkW.getDataRateLabel = function(i) {
    return this.getSizeLabel(i) + "/s";
}

ConkW.getSI = function(i) {
    if (i < 1000) return this.getPrecision(i) + " ";
    i /= 1000;
    if (i < 1000) return this.getPrecision(i) + "K";
    i /= 1000;
    if (i < 1000) return this.getPrecision(i) + "M";
    i /= 1000;
    if (i < 1000) return this.getPrecision(i) + "G";
    i /= 1000;
    return this.getPrecision(i) + "T";
}

ConkW.getPrecision = function(i) {
    var res = "" + i;
    if (res.includes(".")) {
        if (i<0) {
            if (i < 10) i = Math.round(i * 10) / 10;
            else i = Math.round(i);
        } else {
            if (i < 10) i = Math.round(i * 100) / 100;
            else if (i < 100) i = Math.round(i * 10) / 10;
            else i = Math.round(i);
        }
        res = "" + i;
    }
    var missing = 4 - res.length;
    if (missing > 0 && res.indexOf(".")==-1) {
        res = res+".";
        missing--;
    }
    while (missing-- > 0) res = res+"0";
    return res;
}


ConkW.initDomCache = function() {
    ConkW.data.holders = [];
    this.loadDom(document.body, ConkW.data.holders)
}

ConkW.loadDom = function(e, holders) {
    var ns = e.getAttribute("cw-ns");
    var processChildren = true;

    if (ns) {
        var names = e.getAttributeNames();
        for (var i = 0; i < names.length; i++) {
            if (names[i].startsWith("cw-prop-")) {
                holders.push(new PropertyHolder(e, e.getAttribute("cw-ns"), e.getAttribute(names[i]), names[i].split("-")[2]));
            }
            if (names[i].startsWith("cw-style-")) {
                holders.push(new PropertyHolder(e.style, e.getAttribute("cw-ns"), e.getAttribute(names[i]), names[i].split("-")[2]));
            }
            switch (names[i]) {
                case "cw-warn":
                    holders.push(new WarnHolder(e, e.getAttribute(names[i])));
                    break;
                case "cw-stale":
                    holders.push(new StaleHolder(e, e.getAttribute(names[i])));
                    break;
                case "cw-value":
                    holders.push(new ValueHolder(e, e.getAttribute(names[i])));
                    break;
                case "cw-gauge0":
                    holders.push(new GaugeHolder(e, e.getAttribute(names[i])));
                    break;
                case "cw-hgauge0":
                    holders.push(new HistoryGaugeHolderCanvas(e, e.getAttribute(names[i])));
                    break;
                case "cw-multinode-pattern":
                    holders.push(new MultivalueHolder(e));
                    processChildren = false;
                    break;
            }
        }

        if (!e.getAttribute("cw-stale") && e.getAttribute("cw-stale") && e.getAttribute("cw-stale").startsWith("m:")) {
            holders.push(new StaleHolder(e, v));
        }
    }

    this.expandNode(e);

    if (processChildren) this.loadChildren(e, holders);
}

ConkW.loadChildren = function(e, holders) {
  for (var i = 0; i < e.childElementCount; i++)
  this.loadDom(e.children[i], holders);
}

ConkW.expandNode = function(e) {
    if (e.getAttribute("cw-fill")) {
        var fill = e.getAttribute("cw-fill");
        if (fill.indexOf("grabberDefault:") == 0) {
            this.fillNode(e, fill.substring(15));
        }
    }
}

ConkW.fillNode = function(element, name) {
    var xmlhttp = new XMLHttpRequest();
    var url = "/htmlTemplates?name=" + name;
    xmlhttp.open("GET", url, false);
    xmlhttp.onreadystatechange = function() {
        if (xmlhttp.readyState == 4) {
            var res = xmlhttp.responseText + "";
            element.innerHTML = res;
            element.removeAttribute("cw-fill");
        }
    }
    xmlhttp.send();
}

ConkW.refreshNew = function(data) {
    var todo = ConkW.data.holders;
    for (var i = 0; i < todo.length; i++) {
        var e = todo[i];
        e.update(data.metrics[e.ns]);
    }
}

ConkW.parseValueExpression = function(id) {
    if (!id) {
        return {
            invalid: true
        }
    }

    var c1 = id.indexOf(":");
    var c2 = id.indexOf(":", c1 + 1);

    var format="";
    var c3 = 0;
    var state=0;
    for (var i=c2+1 ; i<id.length ; i++) {
        var c = id.charAt(i);
        switch (state) {
            case 0: {
                if (c === '\\') {
                    state=1;
                    break;
                }
                if (c === ':') {
                    state=2;
                    c3 = i;
                    break;
                }
                format += c;
                break;
            }
            case 1: {
                format += c;
                state=0;
                break;

            }
        }
        if (state==2) break;
    }



    return {
        valuetype: id.substring(0, c1),
        datatype: id.substring(c1 + 1, c2),
        format: format,
        expression: id.substring(c3 + 1)
    }
}

ConkW.extractMetricDelay = function(valueexpr, data) {
    var now = Date.now() / 1000;
    if (!data) return undefined;
    switch (valueexpr.valuetype) {
        case "m":
            var mlv = data.num["ts:" + valueexpr.expression];
            if (mlv === undefined) mlv = data.timestamp;
            if (mlv === undefined) return now;
            return now - mlv / 1000;
    }
    return undefined;
}

ConkW.extractRawValue = function(valueexpr, data) {
    if (!data) return undefined;
    switch (valueexpr.valuetype) {
        case "m":
            var ns = data[valueexpr.datatype];
            return ns ? ns[valueexpr.expression] : undefined;
        case "e":
            return this.fillTemplate(valueexpr.expression, data);
        case "l":
            return valueexpr.expression;
    }
    return null;
}

ConkW.parseDelayInSeconds = function(string) {
    var mult = 1;
    if (string.endsWith("m")) mult = 60;
    if (string.endsWith("h")) mult = 3600;
    if (string.endsWith("d")) mult = 86400;
    return parseInt(string) * mult;
}

ConkW.extractTypedValue = function(valueexpr, data) {
    var value = this.extractRawValue(valueexpr, data);
    if (value === undefined) return;
    switch (valueexpr.datatype) {
        case "str":

            return "" + value;
        case "num":
            return value - 0;
    }
    return null;
}

ConkW.extractFormattedValue = function(valueexpr, data) {
    var value = this.extractTypedValue(valueexpr, data);
    if (value === undefined) return;
    switch (valueexpr.datatype) {
        case "str":
            if (valueexpr.format.startsWith("fixedlen."))
                return this.toFixedLength(value, valueexpr.format.split(".")[1] - 0);
            return "" + value;
        case "num":
            value = value - 0;
            return this.getProperLabel(valueexpr.format, value);
    }
    return null;
}

ConkW.toFixedLength = function(value, chars) {
    if (value.length > chars) value = value.substring(0, chars);
    while (value.length < chars) value = " " + value;
    return value;
}


ConkW.updateField = function(field, property, cacheKey, value, warning) {
    if (ConkW.data.cachedValues[cacheKey] !== value) {
        var oldvalue = ConkW.data.cachedValues[cacheKey];
        ConkW.data.cachedValues[cacheKey] = value;
        if (value === undefined && field.classList) {
            field.classList.add("cw-stale");
            return;
        }
        if (oldvalue === undefined && field.classList) {
            field.classList.remove("cw-stale");
        }
        field[property] = value;
        if (warning && field.classList) {
            if (warning == "yes")
                field.classList.add("cw-error");
            else
                field.classList.remove("cw-error");
        }
    }
}

ConkW.isWarning = function(vExpr, wExpr, data) {
    if (wExpr && !wExpr.invalid) {
        var vv = vExpr ? this.extractRawValue(vExpr, data) : null;
        var vw = this.extractRawValue(wExpr, data);
        switch (wExpr.format) {
            case "isnot":
                return vv != vw ? "yes" : "no";
            case "is":
                return vv == vw ? "yes" : "no";
            case "valuecontains":
                return (vv+"").indexOf(vw)!=-1 ? "yes" : "no";
            case "valueabove":
                return vv > vw ? "yes" : "no";
            case "valuebelow":
                return vv < vw ? "yes" : "no";
        }
    }
    return false;
}

ConkW.isStale = function(vExpr, data) {
    if (vExpr && !vExpr.invalid) {
        var f = vExpr.format;
        if (f.startsWith("olderThan.")) {
            return this.extractMetricDelay(vExpr, data) > this.parseDelayInSeconds(f.substring(10));
        }
        return this.extractRawValue(vExpr, data) === undefined;
    }
    return false;
}

class PropertyHolder {
    constructor(e, ns, v, pn) {
        this.element = e;
        this.propertyName = pn;
        this.valueExpr = ConkW.parseValueExpression(v);
        this.ns = ns;
        this.cacheKey = pn + "." + this.ns + "." + v;
        ConkW.data.cachedValues[this.cacheKey] = "--not-a-valid-value--";
    }
    update(data) {
        var v = ConkW.extractFormattedValue(this.valueExpr, data);
        ConkW.updateField(this.element, this.propertyName, this.cacheKey, v, false);
    }
}

class WarnHolder {
    constructor(e, v) {
        this.element = e;
        this.valueExpr = ConkW.parseValueExpression(v);
        this.valueReference = ConkW.parseValueExpression(e.getAttribute("cw-warn-value"));
        this.ns = e.getAttribute("cw-ns");
        this.cacheKey = "warn." + this.ns + "." + v;
        ConkW.data.cachedValues[this.cacheKey] = "--not-a-valid-value--";
    }
    update(data) {
        if (ConkW.isWarning(this.valueReference, this.valueExpr, data)==='yes')
            this.element.classList.add("cw-error");
        else
            this.element.classList.remove("cw-error");

    }
}

class ValueHolder {
    constructor(e, v) {
        this.element = e;
        this.valueExpr = ConkW.parseValueExpression(v);
        this.ns = e.getAttribute("cw-ns");
        this.cacheKey = "value." + this.ns + "." + v;
        this.warn = ConkW.parseValueExpression(e.getAttribute("cw-value-warn"));
        ConkW.data.cachedValues[this.cacheKey] = "--not-a-valid-value--";
    }
    update(data) {
        var v = ConkW.extractFormattedValue(this.valueExpr, data);
        ConkW.updateField(this.element, "innerHTML", this.cacheKey, v, ConkW.isWarning(this.valueExpr, this.warn, data));
    }
}

class StaleHolder {
    constructor(e, v) {
        this.element = e;
        this.valueExpr = ConkW.parseValueExpression(v);
        this.ns = e.getAttribute("cw-ns");
        this.cacheKey = "value." + this.ns + "." + v;
        ConkW.data.cachedValues[this.cacheKey] = "--not-a-valid-value--";
    }
    update(data) {
        var stale = ConkW.isStale(this.valueExpr, data);
        if (ConkW.data.cachedValues[this.cacheKey] != stale) {
            ConkW.data.cachedValues[this.cacheKey] = stale;
            if (stale) {
                this.element.classList.add("cw-stale");
            } else {
                this.element.classList.remove("cw-stale");
            }
        }
    }
}

class GaugeHolder {
    constructor(e, fv) {
        this.element = e;
        this.ns = e.getAttribute("cw-ns");
        this.min = ConkW.parseValueExpression(e.getAttribute("cw-min"));
        this.max = ConkW.parseValueExpression(e.getAttribute("cw-max"));
        this.warn = ConkW.parseValueExpression(e.getAttribute("cw-value-warn"));
        this.wmax = this.warn.format === "valuebelow";

        var green = document.createElement("div");
        green.className = "red" + (this.wmax ? "left" : "right");
        e.appendChild(green);

        this.valueExprs = [];
        var ck = "";
        var i = 0;
        while (true) {
            var fv = e.getAttribute("cw-gauge" + i);
            if (!fv) break;
            var idx = fv.indexOf(":");
            var v = fv.substring(idx + 1);
            ck += v + "/"
            this.valueExprs.push(ConkW.parseValueExpression(v));

            var gauge = document.createElement("div");
            gauge.className = "gauge";
            var col = fv.substring(0, idx);
            if (col !== "default") gauge.style.backgroundColor = col;
            e.appendChild(gauge);
            i++;
        }
        this.cacheKey = "gauge." + this.ns + "." + ck + e.getAttribute("cw-min") + e.getAttribute("cw-max") + e.getAttribute("cw-value-warn");
        ConkW.data.cachedValues[this.cacheKey] = "--not-a-valid-value--";
    }
    update(data) {
        var min = ConkW.extractTypedValue(this.min, data);
        var max = ConkW.extractTypedValue(this.max, data);
        var warn = this.warn.invalid ? null : ConkW.extractTypedValue(this.warn, data);

        var greenWidth = warn ? (this.wmax ? ((warn - min) * 100 / (max - min)) + "%" : (100 - ((warn - min) * 100 / (max - min)) + "%")) : "0";
        var cacheValue = greenWidth + "/";
        var gw = [];
        var gcn = [];
        for (var i = 0; i < this.valueExprs.length; i++) {
            var value = ConkW.extractTypedValue(this.valueExprs[i], data);
            var wn = ((value - min) * 100 / (max - min));
            gw[i] = wn;
            var w = warn ? (this.wmax ? value < warn : value > warn) : false;
            var gaugeCN = value === undefined ? "gaugestale" : (w ? "gaugewarning" : "gauge");
            gcn[i] = gaugeCN;
            cacheValue = cacheValue + "/" + gw;
        }

        if (cacheValue !== ConkW.data.cachedValues[this.cacheKey]) {
            ConkW.data.cachedValues[this.cacheKey] = cacheValue;
            var green = this.element.children[0];
            green.style.width = greenWidth;
            var left = 0;
            for (var i = 0; i < this.valueExprs.length; i++) {
                var gauge = this.element.children[i + 1];
                gauge.style.width = gw[i] + "%";
                gauge.style.left = left + "%";
                left += gw[i];
                gauge.className = gcn[i];
            }
        }
    }
}


class HistoryGaugeHolderFloat {
    constructor(e, fv) {
        this.element = e;
        this.elementWidth = e.scrollWidth;
        this.ns = e.getAttribute("cw-ns");
        this.bgcolor = e.getAttribute("cw-bgcolor");
        this.log = e.getAttribute("cw-log") == "true";
        this.min = ConkW.parseValueExpression(e.getAttribute("cw-min"));
        this.max = ConkW.parseValueExpression(e.getAttribute("cw-max"));
        this.maxValue = 0;
        this.warn = ConkW.parseValueExpression(e.getAttribute("cw-value-warn"));
        this.wmax = this.warn.format === "valuebelow";
        e.style.backgroundColor = this.bgcolor;

        this.colors = [];
        this.valueExprs = [];
        var i = 0;
        while (true) {
            var fv = e.getAttribute("cw-hgauge" + i);
            if (!fv) break;
            var idx = fv.indexOf(":");
            var v = fv.substring(idx + 1);
            this.colors.push(fv.substring(0, idx));
            this.valueExprs.push(ConkW.parseValueExpression(v));
            i++;
        }
    }
    update(data) {
        var zis = this;
        var e = this.element;
        var min = ConkW.extractTypedValue(this.min, data);
        var max = ConkW.extractTypedValue(this.max, data);

        var bottom = 0;

        if (ConkW.showMetricGap) {
            var container = document.createElement("div");
            container.className = "ch error";
            e.appendChild(container);
        }

        var container = document.createElement("div");
        container.className = "ch";
        for (var i = 0; i < this.colors.length; i++) {
            var value = ConkW.extractTypedValue(this.valueExprs[i], data);
            var color = this.colors[i];
            var bar = document.createElement("div");
            bar.setAttribute("cw-value", value);
            bar.className = "hgauge";
            var posprc = ConkW.getPercent(value, min, max, this.log);
            bar.style.height = posprc + "%";
            bar.style.bottom = bottom + "%";
            bottom += posprc;
            bar.style.backgroundColor = color;
            container.appendChild(bar);
        }
        if (bottom<100) {
            var bar = document.createElement("div");
            bar.className = "hgaugebg";
            bar.style.height = (100-bottom) + "%";
            bar.style.bottom = bottom + "%";
            bar.style.backgroundColor = this.bgcolor;
            container.appendChild(bar);
        }
        e.appendChild(container);
        while (e.childElementCount > this.elementWidth) {
            e.removeChild(e.firstChild);
        }

        if (max != this.maxValue) {
            this.maxValue = max;
            // Max has changed, we need to recompute height of all elements.
            e.childNodes.forEach(function (container) {
                var bottom=0;
                container.childNodes.forEach(function (bar) {
                    if (bar.className === "hgauge") {
                        var posprc = ConkW.getPercent(parseFloat(bar.getAttribute("cw-value")), min, max, zis.log);
                        bar.style.height = posprc + "%";
                        bar.style.bottom = bottom + "%";
                        bottom += posprc;
                    } else { // className is hgaugebg
                        bar.style.height = (100-bottom) + "%";
                        bar.style.bottom = bottom + "%";
                    }
                })
            })
        }
    }
}

class MultivalueHolder {
    constructor(e) {
        this.element = e;
        this.ns = e.getAttribute("cw-ns");
        this.from = ConkW.parseValueExpression(e.getAttribute("cw-multinode-from"));
        this.to = ConkW.parseValueExpression(e.getAttribute("cw-multinode-to"));
        this.in = ConkW.parseValueExpression(e.getAttribute("cw-multinode-in"));
        this.pattern = e.getAttribute("cw-multinode-pattern");
        this.childHolders = [];
        this.content = e.innerHTML;
        this.cacheKey = "multivalue." + this.ns + "." + JSON.stringify(this.from) + "." + JSON.stringify(this.to) + "." + JSON.stringify(this.in) + "." + JSON.stringify(this.pattern);
        ConkW.data.cachedValues[this.cacheKey] = "--not-a-valid-value--";
    }
    update(data) {
        if (this.in.invalid) { // In a list of values
            var from = ConkW.extractTypedValue(this.from, data);
            var to = ConkW.extractTypedValue(this.to, data);
            if (typeof from === "number" && typeof to === "number") {
                var cacheValue = from + "/" + to;
                if (ConkW.data.cachedValues[this.cacheKey] !== cacheValue) {
                    ConkW.forceScreenRefresh();
                    console.log("Redrawing " + cacheValue + " !== " + ConkW.data.cachedValues[this.cacheKey]);
                    ConkW.data.cachedValues[this.cacheKey] = cacheValue;
                    this.childHolders = [];
                    var html = "";
                    for (var i = from; i < to; i++) {
                        html += this.content.replaceAll(this.pattern, i+"");
                    }
                    this.element.innerHTML = html;
                    ConkW.loadChildren(this.element, this.childHolders);
                } // Else nothing changed.
            } else {
                if (typeof from !== "undefined" && typeof to !== "undefined") {
                    ConkW.handleError("From ("+(typeof from)+") or to ("+(typeof to)+") are not numbers in element id=" + this.element.id);
                } else {
                    console.log("Cannot expand multinode with id " + this.element.id + " from " + from + " to " + to);
                }
            }
        } else { // numeric from -> to
            var invalues = ConkW.extractFormattedValue(this.in, data);
            if (invalues && ConkW.data.cachedValues[this.cacheKey] !== invalues) {
                console.log("Redrawing " + invalues + " !== " + ConkW.data.cachedValues[this.cacheKey]);
                ConkW.forceScreenRefresh();
                ConkW.data.cachedValues[this.cacheKey] = invalues;
                this.childHolders = [];
                var values = invalues.split(",");
                var html = "";
                for (var i = 0; i < values.length; i++) {
                    html += this.content.replaceAll(this.pattern, values[i]);
                }
                this.element.innerHTML = html;
                ConkW.loadChildren(this.element, this.childHolders);
            } // Else nothing changed.
        }
        for (var i=0 ; i<this.childHolders.length ; i++) {
            this.childHolders[i].update(data);
        }
    }
}


class HistoryGaugeHolderCanvas {
    constructor(e, fv) {
        this.element = e;
        this.ns = e.getAttribute("cw-ns");
        this.bgcolor = e.getAttribute("cw-bgcolor");
        if (!this.bgcolor) {
            this.bgcolor = "#202040";
        }
        this.log = e.getAttribute("cw-log") == "true";
        this.min = ConkW.parseValueExpression(e.getAttribute("cw-min"));
        this.max = ConkW.parseValueExpression(e.getAttribute("cw-max"));
        this.maxValue = -1;
        this.warn = ConkW.parseValueExpression(e.getAttribute("cw-value-warn"));
        this.wmax = this.warn.format === "valuebelow";
        e.style.backgroundColor = this.bgcolor;

        this.colors = [];
        this.valueExprs = [];
        var i = 0;
        while (true) {
            var fv = e.getAttribute("cw-hgauge" + i);
            if (!fv) break;
            var idx = fv.indexOf(":");
            var v = fv.substring(idx + 1);
            this.colors.push(fv.substring(0, idx));
            this.valueExprs.push(ConkW.parseValueExpression(v));
            i++;
        }

        var canvas = document.createElement("canvas");
        canvas.setAttribute("width", this.w=e.scrollWidth);
        canvas.setAttribute("height", this.h=e.scrollHeight);
        e.appendChild(canvas);
        this.canvas = canvas;
    }
    scroll() {
        var ctx = this.canvas.getContext('2d');
        ctx.save();
        ctx.translate(-1, 0);
        ctx.drawImage(this.canvas, 0, 0);
        ctx.restore();
    }
    update(data) {
        var ctx = this.canvas.getContext('2d');

        this.scroll();

        var min = ConkW.extractTypedValue(this.min, data);
        var max = ConkW.extractTypedValue(this.max, data);

        var bottom = 0;
        var bgColor = this.bgcolor;

        if (ConkW.showMetricGap) {
            bgColor = "red";
        }

        ctx.fillStyle = bgColor;
        ctx.fillRect(this.w-1, 0, 1, this.h);

        for (var i = 0; i < this.colors.length; i++) {
            var value = ConkW.extractTypedValue(this.valueExprs[i], data);
            ctx.fillStyle = this.colors[i];
            var posprc = ConkW.getPercent(value, min, max, this.log);
            var hpx = posprc*this.h/100;

            ctx.fillRect(this.w-1, this.h-hpx-bottom, 1, hpx);
            bottom += hpx;
        }

        if (max != this.maxValue && this.maxValue!=-1) {
            this.scroll();
            ctx.fillStyle = "orange";
            ctx.fillRect(this.w-1, 0, 1, this.h);
            this.maxValue = max;
        }
    }
}
