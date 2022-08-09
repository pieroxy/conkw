import m = require('mithril');
import "../css/style.scss";
import { HomePage } from './components/pages/HomePage';
import { LoginPage } from './components/pages/auth/LoginPage';
import { AuthenticatedPageResolver } from './utils/AuthenticatedPageResolver';
import { ChangePassword } from './components/pages/auth/ChangePassword';
import { Endpoints } from './utils/navigation/Endpoints';
import { ProfilePage } from './components/pages/auth/ProfilePage';
import { AlertsPage } from './components/pages/alerts/AlertsPage';
import { SettingsPage } from './components/pages/settings/Settings';
import { DashboardPanelEditPage } from './components/dashboards/pages/DashboardPanelEditPage';
import { DashboardViewPage } from './components/dashboards/pages/DashboardViewPage';

(function () {

  let routes:m.RouteDefs = {};
  routes[Endpoints.HOME] = new AuthenticatedPageResolver(HomePage);
  routes[Endpoints.LOGIN] = LoginPage;
  routes[Endpoints.DASHBOARD_VIEW] = new AuthenticatedPageResolver(DashboardViewPage);
  routes[Endpoints.PASSWORD_CHANGE] = ChangePassword;
  routes[Endpoints.DASHBOARD_PANEL_EDIT] = new AuthenticatedPageResolver(DashboardPanelEditPage);
  routes[Endpoints.PROFILE] = new AuthenticatedPageResolver(ProfilePage);
  routes[Endpoints.ALERTS] = new AuthenticatedPageResolver(AlertsPage);
  routes[Endpoints.SETTINGS] = new AuthenticatedPageResolver(SettingsPage);

  m.route(document.body, Endpoints.HOME, routes);

  addEventListener('resize', m.redraw);

})();