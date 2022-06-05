import m = require('mithril');
import { Auth } from './Auth';

export class AuthenticatedPageResolver implements m.RouteResolver {
  private comp:m.ComponentTypes;
  constructor(comp:m.ComponentTypes) {
    this.comp = comp;
  }
  onmatch():m.ComponentTypes | void {
    if (Auth.isLoggedIn()) {
      return this.comp;
    } else {
      m.route.set("/login");
      return;
    }
  }
}