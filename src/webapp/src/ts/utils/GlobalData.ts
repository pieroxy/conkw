export class GlobalData {
  static dashboardTitles:Map<string,string> = new Map();

  static setDashboardTitle(id:string, title:string):void {
    GlobalData.dashboardTitles.set(id, title);
  }

  static getDashboardTitle(id:string):string {
    return GlobalData.dashboardTitles.get(id) || id;
  }
}