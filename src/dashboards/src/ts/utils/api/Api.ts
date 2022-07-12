import m from 'mithril';
import { ApiResultCodes, ClientInfo } from '../../auto/pieroxy-conkw';
import { AppVersion } from '../../auto/version';
import { Notification, Notifications, NotificationsClass, NotificationsType } from '../../components/alwayson/Notifications';
import { ApiResponse } from '../types';
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
      appVersion:AppVersion.MVN_VER,
      gitDepth:AppVersion.GIT_DEPTH,
      gitRevision:AppVersion.GIT_REV,
    };
  }

  public static call<I,O>(options:ApiOptions<I>): Promise<O> {
    return Api.apiInternal<I,ApiResponse<O>>({
      method: options.method,
      content: options.body,
      client:Api.getClientInfo(),
      url:"/api/" + options.endpoint
    }).then((r:ApiResponse<O>):O => {
      switch (r.code) {
        case ApiResultCodes.OK:
          return r.content;
        case ApiResultCodes.DISPLAY_ERROR:
          Notifications.addNotification(new Notification(NotificationsClass.LOGIN, NotificationsType.WARNING, r.message));
          throw "HandledError";
        case ApiResultCodes.TECH_ERROR:
        default:
          throw "HandledError";
      }
    }).catch(() => {
      return new Promise(()=>{});
    });
  }

  private static apiInternal<I,O>(p:ApiParams<I>): Promise<O> {
    var ct = "application/json";

    if (this.timeout) {
      clearTimeout(this.timeout);
      this.timeout = 0;
    }

    var headers = <any>{
      "Content-Type": ct,
      "client": JSON.stringify(p.client)
    }

    this.waiting = true;
    return m.request({
      method: p.method,
      url: p.url,
      headers: headers,
      body: p.method === 'POST' ? p.content : null,
      params: p.method === 'GET' ? {input:p.content}:{},
    }).then((arg:any) => {
      this.timeout = window.setTimeout(() => {Api.waiting = false;Api.timeout=0;m.redraw()} , 150);
      return arg;
    }).catch((msg:string) => {
      this.timeout = window.setTimeout(() => {Api.waiting = false;Api.timeout=0;m.redraw()} , 150);
      return msg;
    });
  }

}


