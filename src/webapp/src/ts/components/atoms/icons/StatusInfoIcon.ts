import m from 'mithril';

export class StatusInfoIcon implements m.ClassComponent {
  public view() {
    return m("svg.icon.info", {
      viewBox:"0 0 24 24",
      fill:"none",
      xmlns:"http://www.w3.org/2000/svg"
    }, [
      m("path.primaryfill", {
        d:"M11.25 17C11.25 17.4142 11.5858 17.75 12 17.75C12.4142 17.75 12.75 17.4142 12.75 17L11.25 17ZM12.75 10C12.75 9.58579 12.4142 9.25 12 9.25C11.5858 9.25 11.25 9.58579 11.25 10L12.75 10ZM11.25 7.00996C11.25 7.42418 11.5858 7.75996 12 7.75996C12.4142 7.75996 12.75 7.42418 12.75 7.00996H11.25ZM12.75 6.99997C12.75 6.58576 12.4142 6.24997 12 6.24997C11.5858 6.24997 11.25 6.58576 11.25 6.99997H12.75ZM12.75 17L12.75 10L11.25 10L11.25 17L12.75 17ZM12.75 7.00996V6.99997H11.25V7.00996H12.75ZM20.25 12C20.25 16.5563 16.5563 20.25 12 20.25V21.75C17.3848 21.75 21.75 17.3848 21.75 12H20.25ZM12 20.25C7.44365 20.25 3.75 16.5563 3.75 12H2.25C2.25 17.3848 6.61522 21.75 12 21.75V20.25ZM3.75 12C3.75 7.44365 7.44365 3.75 12 3.75V2.25C6.61522 2.25 2.25 6.61522 2.25 12H3.75ZM12 3.75C16.5563 3.75 20.25 7.44365 20.25 12H21.75C21.75 6.61522 17.3848 2.25 12 2.25V3.75Z",
      }),
      m("path.secondary", {
        d:"M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z",
      }),
    ])
  }
}