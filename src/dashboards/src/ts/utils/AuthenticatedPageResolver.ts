import m = require('mithril');
import { OfflinePage } from '../components/pages/auth/OfflinePage';
import { WaitingPage } from '../components/pages/auth/WaitingPage';
import { Auth, AuthenticationStatus } from './Auth';

export class AuthenticatedPageResolver implements m.RouteResolver {
  private comp:m.ComponentTypes;
  constructor(comp:m.ComponentTypes<any,any>) {
    this.comp = comp;
  }
  onmatch():m.ComponentTypes | void {
    switch (Auth.getAuthenticationStatus()) {
      case AuthenticationStatus.CHECKING_IN_PROGRESS:
        return WaitingPage;
      case AuthenticationStatus.OFFLINE:
        return OfflinePage;
      case AuthenticationStatus.LOGGED_IN:
        return this.comp;
      case AuthenticationStatus.NO_TOKEN:
        m.route.set("/login");
        return;
    }
  }
}