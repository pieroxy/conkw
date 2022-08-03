import m from 'mithril';

export class LogoCIcon implements m.ClassComponent {
  public view() {
    return m("svg.icon", {
      viewBox:"20 95 540 580",
      fill:"none",
      xmlns:"http://www.w3.org/2000/svg"
    }, [
      m("path.primaryfill", {
        transform:"scale(1,-1) translate(0,-650)",
        d:"M280 265c0 -45 30 -75 80 -75s90 40 90 40l100 -160s-80 -85 -225 -85c-170 0 -295 125 -295 280s125 280 295 280c145 0 225 -85 225 -85l-100 -160s-40 40 -90 40s-80 -30 -80 -75z",
      })
    ])
  }
}