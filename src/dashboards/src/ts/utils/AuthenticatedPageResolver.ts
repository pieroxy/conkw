import m = require('mithril');
import { OfflinePage } from '../components/pages/auth/OfflinePage';
import { WaitingPage } from '../components/pages/auth/WaitingPage';
import { Auth, AuthenticationStatus } from './Auth';
import { Endpoints } from './navigation/Endpoints';
import { Routing } from './navigation/Routing';

export class AuthenticatedPageResolver implements m.RouteResolver {

  private comp:m.ComponentTypes;
  constructor(comp:m.ComponentTypes<any,any>) {
    this.comp = comp;
  }

  log(s:string):void {
    if (Routing.DEBUG) {
      console.log("AuthenticatedPageResolver :: "+s + " :: " + m.route.get() + " " + this.comp);
    }
  }

  onmatch():m.ComponentTypes | void {
    switch (Auth.getAuthenticationStatus()) {
      case AuthenticationStatus.CHECKING_IN_PROGRESS:
        this.log("In progress");
        return WaitingPage;
      case AuthenticationStatus.OFFLINE:
        this.log("Offline");
        return OfflinePage;
      case AuthenticationStatus.LOGGED_IN:
        this.log("OK");
        return this.comp;
      case AuthenticationStatus.NO_TOKEN:
        this.log("No token");
        Routing.goToScreen(Endpoints.LOGIN);
        return;
      default:
        this.log("Unknown");
    }
  }
}