import m from 'mithril';
import { ApiResultCodes, ClientInfo } from '../../auto/pieroxy-conkw';
import { AppVersion } from '../../auto/version';
import { Auth } from '../Auth';
import { Endpoints } from '../navigation/Endpoints';
import { Notification, Notifications, NotificationsClass, NotificationsType } from '../Notifications';
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
    return Api.apiInternal<I,O>({
      method: options.method,
      content: options.body,
      client:Api.getClientInfo(),
      url:"/api/" + options.endpoint,
      hideFromSpinner: !!options.hideFromSpinner
    }).then((r:ApiResponse<O>):O => {
      if (r.code!=ApiResultCodes.SERVER_UNREACHABLE) {
        Notifications.dismissClass(NotificationsClass.SERVER_UNREACHABLE);
      }
      switch (r.code) {
        case ApiResultCodes.OK:
          return r.content;
        case ApiResultCodes.GO_TO_LOGIN:
          Auth.clearAuthToken(); // The token might be invalid
          m.route.set(Endpoints.LOGIN);
          return r.content;
        case ApiResultCodes.DISPLAY_ERROR:
          Notifications.addNotification(new Notification(NotificationsClass.SERVER_RESPONSE, NotificationsType.WARNING, r.message, 10));
          throw "HandledError";
        case ApiResultCodes.TECH_ERROR:
          Notifications.addNotification(new Notification(NotificationsClass.SERVER_RESPONSE, NotificationsType.ERROR, "The server encountered an error. More informations can probably be found in the logs.", 10));
          throw "HandledError";
        case ApiResultCodes.SERVER_UNREACHABLE:
          Notifications.addNotification(new Notification(NotificationsClass.SERVER_UNREACHABLE, NotificationsType.ERROR, ["The server could not be reached. Is it running? Is your network connection ok?"], 5));
          throw "HandledError";
        default:
          Notifications.addNotification(new Notification(NotificationsClass.SERVER_UNREACHABLE, NotificationsType.ERROR, "An unknown error has occured.", 5));
          throw "HandledError";
      }
    }).catch((_e) => {
      return new Promise(()=>{});
    });
  }

  private static apiInternal<I,O>(p:ApiParams<I>): Promise<ApiResponse<O>> {
    var ct = "application/json";

    if (this.timeout && !p.hideFromSpinner) {
      clearTimeout(this.timeout);
      this.timeout = 0;
    }

    var headers = <any>{
      "Content-Type": ct,
      "client": JSON.stringify(p.client)
    }
    if (Auth.getAuthToken()) headers.authToken = Auth.getAuthToken();

    if (!p.hideFromSpinner) this.waiting = true;
    return m.request({
      method: p.method,
      url: p.url,
      headers: headers,
      body: p.method === 'POST' ? p.content : null,
      params: p.method === 'GET' ? {input:JSON.stringify(p.content)}:{},
    }).then((arg:ApiResponse<O>) => {
      if (!p.hideFromSpinner) this.timeout = window.setTimeout(() => {Api.waiting = false;Api.timeout=0;m.redraw()} , 150);
      arg.httpCode=200;
      return arg;
    }).catch((err:MError<ApiResponse<O>>) => {
      if (!p.hideFromSpinner) this.timeout = window.setTimeout(() => {Api.waiting = false;Api.timeout=0;m.redraw()} , 150);
      if (!err.response) {
        err.response = {
          code:ApiResultCodes.SERVER_UNREACHABLE,
          content: <any>null,
          httpCode: err.code,
          message:err.message
        };
      } else {
        err.response.httpCode=err.code;
      }
      return err.response;
    });
  }
}


interface MError<O> {
  message:string;
  code:number;
  response:O;
}