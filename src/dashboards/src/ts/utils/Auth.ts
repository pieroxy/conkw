export class Auth {
  private static token?:string;

  static isLoggedIn():boolean {
    return !!this.token;
  }

  static setAuthToken(token:string) {
    Auth.token = token;
    localStorage.setItem("authToken", token);
  }
}