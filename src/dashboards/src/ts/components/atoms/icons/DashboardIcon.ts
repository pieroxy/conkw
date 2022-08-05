import m from 'mithril';

export class DashboardIcon implements m.ClassComponent {
  public view() {
    return m("svg.icon", {
      viewBox:"0 0 24 24",
      fill:"none",
      xmlns:"http://www.w3.org/2000/svg",
      preserveAspectRatio:"xMidYMid"
    }, [
      m("rect.secondary", {x:"4",y:"4",width:"16",height:"16"}),
      m("line.primary", {x1:"7",y1:"7",x2:"10",y2:"7"}),
      m("line.primary", {x1:"7",y1:"10",x2:"12",y2:"10"}),
      m("line.primary", {x1:"7",y1:"13",x2:"10",y2:"13"}),
      m("line.primary", {x1:"7",y1:"16",x2:"13",y2:"16"}),
      m("rect.primary", {x:"4",y:"4",width:"16",height:"16"}),
    ])
  }
}