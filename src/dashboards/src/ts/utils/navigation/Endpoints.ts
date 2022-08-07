export enum Endpoints {
  HOME="/dashboards",
  LOGIN="/login",
  DASHBOARD_EDITION="/dashboards/edit/:id",
  ITEM_NEW="/dashboards/newItem/:dashboardId/:panelId",
  PASSWORD_CHANGE="/profile/changepassword/:id",
  DASHBOARD_PANEL_EDIT="/dashboards/editPanel/:dashboardId/:panelId",
  PROFILE="/profile/",
  SETTINGS="/settings/",
  ALERTS="/alerts/"
}