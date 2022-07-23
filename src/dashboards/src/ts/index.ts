import m = require('mithril');
import "../css/style.scss";
import { HomePage } from './components/pages/HomePage';
import { LoginPage } from './components/pages/auth/LoginPage';
import { AuthenticatedPageResolver } from './utils/AuthenticatedPageResolver';
import { ChangePassword } from './components/pages/auth/ChangePassword';
import { DashboardEditPage } from './components/pages/dashboards/DashboardEdit';

(function () {
  m.route(document.body, "/home", {
    "/home":new AuthenticatedPageResolver(HomePage),
    "/login":LoginPage,
    "/dashboards/edit/:id":new AuthenticatedPageResolver(DashboardEditPage),
    "/changepassword/:id":ChangePassword
  })
})();