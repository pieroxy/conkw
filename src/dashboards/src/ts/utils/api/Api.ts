import m from 'mithril';
import { ClientInfo } from '../../auto/pieroxy-conkw';
import { ApiOptions } from './ApiOptions';

import { ApiParams } from "./ApiParams";

export class Api {
  private static waiting:boolean = false;
  private static timeout:number = 0;

  public static isWaiting():boolean {
    return this.waiting;
  }

  public static getClientInfo():ClientInfo {
    return {
      appVersion:(<any>window).MVN_VER,
      gitDepth:(<any>window).GIT_DEPTH,
      gitRevision:(<any>window).GIT_REV,
    };
  }

  public static call<I,O>(options:ApiOptions<I>): Promise<O> {
    return Api.apiInternal<I,O>({
      method: options.method,
      content: options.body,
      client:Api.getClientInfo(),
      url:"/api/" + options.endpoint
    });
  }

  private static apiInternal<I,O>(p:ApiParams<I>): Promise<O> {
    var ct = "application/x-www-form-urlencoded";

    if (this.timeout) {
      clearTimeout(this.timeout);
      this.timeout = 0;
    }

    var headers = <any>{
      "Content-Type": ct,
      "client": JSON.stringify(p.client)
    }

    p.url = p.url + (p.method === "GET" ? ("?input=" + encodeURIComponent(JSON.stringify(p.content))) : "");

    this.waiting = true;
    return m.request({
      method: p.method,
      url: p.url,
      headers: headers,
      serialize: (p.method === "POST" ? ()=>JSON.stringify(p.content) : function () { }),
    }).then((arg:any) => {
      this.timeout = window.setTimeout(() => {Api.waiting = false;Api.timeout=0;m.redraw()} , 150);
      return arg;
    }).catch((msg:string) => {
      this.timeout = window.setTimeout(() => {Api.waiting = false;Api.timeout=0;m.redraw()} , 150);
      return msg;
    });
  }

}


