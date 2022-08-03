import { ApiEndpoints } from "../auto/ApiEndpoints";
import { User } from "../auto/pieroxy-conkw";
import { Endpoints } from './navigation/Endpoints';
import { Routing } from './navigation/Routing';

export class Auth {
  private static token?:string;
  private static user?:User;
  private static askedForToken:boolean = false;

  static getAuthenticationStatus():AuthenticationStatus {
    if (this.token) return AuthenticationStatus.LOGGED_IN;
    let localStorateToken = localStorage.getItem("authToken");
    if (localStorateToken) {
      let lst = localStorateToken;
      if (this.askedForToken) return AuthenticationStatus.CHECKING_IN_PROGRESS;
      this.askedForToken = true;
      ApiEndpoints.GetUserFromSession.call({
          token:localStorateToken
        }
      ).then((data) => {
        this.askedForToken = false;
        if (data.user) {
          this.user = data.user;
          this.token = lst;
          Routing.goToScreen(Endpoints.HOME);
        } else {
          if (data.invalidSession) {
            localStorage.removeItem("authToken");
          }
        }
      }).catch(() => {
        this.askedForToken = false;
      })
      return AuthenticationStatus.CHECKING_IN_PROGRESS;
    } else {
      return AuthenticationStatus.NO_TOKEN;
    }
  }

  static setAuthToken(token:string, user:User) {
    Auth.token = token;
    Auth.user = user;
    localStorage.setItem("authToken", token);
  }

  static clearAuthToken() {
    Auth.token = undefined;
    Auth.user = undefined;
    localStorage.removeItem("authToken");
  }

  static getAuthToken():string|undefined {
    return Auth.token;
  }

  public static getUser():User|undefined {
    return this.user;
  }
}

export enum AuthenticationStatus {
  LOGGED_IN,
  NO_TOKEN,
  CHECKING_IN_PROGRESS,
  OFFLINE
}