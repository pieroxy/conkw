export enum Endpoints {
  HOME="/dashboards",
  LOGIN="/login",
  DASHBOARD_VIEW="/dashboards/edit/:id",
  PASSWORD_CHANGE="/profile/changepassword/:id",
  DASHBOARD_PANEL_EDIT="/dashboards/editPanel/:dashboardId/:panelId",
  PROFILE="/profile/",
  SETTINGS="/settings/",
  ALERTS="/alerts/",
  EXTRACTORS_LIST="/extractors/list/",
  EXTRACTOR_DETAIL="/extractors/detail/:className/:name",
}