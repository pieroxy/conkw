import m = require('mithril');

export class Routing {
  public static DEBUG:false=false;

  static refreshRoute() {
    Routing.goToScreen(m.route.get());
  }

  public static getRoute(target:string, params?:{[id:string]:string|number}):string {
    let res:string = target + "/";
    if (params) {
      let props = Object.getOwnPropertyNames(params);
      for (let keyIdx in props) {
        let key = props[keyIdx];
        let oldvalue = res;
        res = res.replace("/:" + key + "/", "/" + params[key] + "/")
        if (oldvalue === res) throw "Parameter "+key+" does not exist on route provided.";
      }
    }
    if (res.indexOf("/:") >= 0) throw "Some parameters were not provided.";
    return res.substring(0, res.length-1);
  }

  public static goToScreen(target:string, params?:{[id:string]:string|number}, avoidHistory?:boolean) {
    if (Routing.DEBUG) console.log("Going to route " + target);
    let route = this.getRoute(target, params);
    m.route.set(route, null, {replace:!!avoidHistory});
  }
}