import m = require('mithril');
import "../css/style.scss";
import { HomePage } from './components/pages/HomePage';
import { LoginPage } from './components/pages/auth/LoginPage';
import { AuthenticatedPageResolver } from './utils/AuthenticatedPageResolver';
import { ChangePassword } from './components/pages/auth/ChangePassword';
import { DashboardEditPage } from './components/pages/dashboards/DashboardEdit';
import { Endpoints } from './utils/navigation/Endpoints';
import { NewPanelPage } from './components/pages/dashboards/NewPanel';
import { EditSimpleGaugeWithValueAndLabelPanel } from './components/pages/dashboards/EditSimpleGaugeWithValueAndLabelPanel';

(function () {

  let routes:m.RouteDefs = {};
  routes[Endpoints.HOME] = new AuthenticatedPageResolver(HomePage);
  routes[Endpoints.LOGIN] = LoginPage;
  routes[Endpoints.DASHBOARD_EDITION] = new AuthenticatedPageResolver(DashboardEditPage);
  routes[Endpoints.PASSWORD_CHANGE] = ChangePassword;
  routes[Endpoints.PANEL_NEW] = new AuthenticatedPageResolver(NewPanelPage);
  routes[Endpoints.GAUGE_SIMPLE_VALUE_LABEL_EDIT] = new AuthenticatedPageResolver(EditSimpleGaugeWithValueAndLabelPanel);

  m.route(document.body, "/home", routes)
})();