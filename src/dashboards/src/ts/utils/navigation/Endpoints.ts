export enum Endpoints {
  HOME="/home",
  LOGIN="/login",
  DASHBOARD_EDITION="/dashboards/edit/:id",
  PANEL_NEW="/dashboards/newPanel/:dashboardId",
  PASSWORD_CHANGE="/changepassword/:id",
  GAUGE_SIMPLE_VALUE_LABEL_EDIT="/dashboards/editPanel/simpleGauge/:dashboardId/:panelId"
}