import m = require('mithril');
import "../css/style.scss";
import { HomePage } from './components/pages/HomePage';
import { LoginPage } from './components/pages/auth/LoginPage';
import { AuthenticatedPageResolver } from './utils/AuthenticatedPageResolver';
import { ChangePassword } from './components/pages/auth/ChangePassword';
import { DashboardEditPage } from './components/dashboards/pages/DashboardEdit';
import { Endpoints } from './utils/navigation/Endpoints';
import { ProfilePage } from './components/pages/auth/ProfilePage';
import { AlertsPage } from './components/pages/alerts/AlertsPage';
import { SettingsPage } from './components/pages/settings/Settings';
import { EditDashboardPanelPage } from './components/dashboards/pages/EditDashboardPanelPage';

(function () {

  let routes:m.RouteDefs = {};
  routes[Endpoints.HOME] = new AuthenticatedPageResolver(HomePage);
  routes[Endpoints.LOGIN] = LoginPage;
  routes[Endpoints.DASHBOARD_EDITION] = new AuthenticatedPageResolver(DashboardEditPage);
  routes[Endpoints.PASSWORD_CHANGE] = ChangePassword;
  routes[Endpoints.DASHBOARD_PANEL_EDIT] = new AuthenticatedPageResolver(EditDashboardPanelPage);
  routes[Endpoints.PROFILE] = new AuthenticatedPageResolver(ProfilePage);
  routes[Endpoints.ALERTS] = new AuthenticatedPageResolver(AlertsPage);
  routes[Endpoints.SETTINGS] = new AuthenticatedPageResolver(SettingsPage);

  m.route(document.body, Endpoints.HOME, routes);

  addEventListener('resize', m.redraw);

})();