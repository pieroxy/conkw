import m from 'mithril';

export class DeleteIcon implements m.ClassComponent {
  public view() {
    return m("svg.icon", {
      viewBox:"0 0 24 24",
      fill:"none",
      xmlns:"http://www.w3.org/2000/svg",
    }, [
      m("path.secondary", {
        d:"M18 18V6H6V18C6 19.1046 6.89543 20 8 20H16C17.1046 20 18 19.1046 18 18Z",
      }),
      m("path.primary", {
        d:"M18 6V18C18 19.1046 17.1046 20 16 20H8C6.89543 20 6 19.1046 6 18V6M15 6V5C15 3.89543 14.1046 3 13 3H11C9.89543 3 9 3.89543 9 5V6M4 6H20M10 10V16M14 10V16",
      }),
    ])
  }
}