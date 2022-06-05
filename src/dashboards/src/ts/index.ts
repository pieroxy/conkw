import m = require('mithril');
import "../css/style.scss";
import { HomePage } from './components/pages/HomePage';
import { LoginPage } from './components/pages/LoginPage';
import { AuthenticatedPageResolver } from './utils/AuthenticatedPageResolver';

(function () {
  m.route.prefix = "?";
  m.route(document.body, "/home", {
    "/home":new AuthenticatedPageResolver(HomePage),
    "/login":LoginPage
  })
})();