import m from 'mithril';

export class StatusErrorIcon implements m.ClassComponent {
  public view() {
    return m("svg.icon.error", {
      viewBox:"0 0 24 24",
      fill:"none",
      xmlns:"http://www.w3.org/2000/svg"
    }, [
      m("path.primaryfill", {
        d:"M15.5303 9.53033C15.8232 9.23744 15.8232 8.76256 15.5303 8.46967C15.2374 8.17678 14.7625 8.17678 14.4696 8.46967L15.5303 9.53033ZM8.46961 14.4697C8.17672 14.7626 8.17672 15.2374 8.46961 15.5303C8.76251 15.8232 9.23738 15.8232 9.53027 15.5303L8.46961 14.4697ZM9.53039 8.46967C9.2375 8.17678 8.76263 8.17678 8.46973 8.46967C8.17684 8.76256 8.17684 9.23744 8.46973 9.53033L9.53039 8.46967ZM14.4697 15.5303C14.7626 15.8232 15.2375 15.8232 15.5304 15.5303C15.8233 15.2374 15.8233 14.7626 15.5304 14.4697L14.4697 15.5303ZM14.4696 8.46967L8.46961 14.4697L9.53027 15.5303L15.5303 9.53033L14.4696 8.46967ZM8.46973 9.53033L14.4697 15.5303L15.5304 14.4697L9.53039 8.46967L8.46973 9.53033ZM20.25 12C20.25 16.5563 16.5563 20.25 12 20.25V21.75C17.3848 21.75 21.75 17.3848 21.75 12H20.25ZM12 20.25C7.44365 20.25 3.75 16.5563 3.75 12H2.25C2.25 17.3848 6.61522 21.75 12 21.75V20.25ZM3.75 12C3.75 7.44365 7.44365 3.75 12 3.75V2.25C6.61522 2.25 2.25 6.61522 2.25 12H3.75ZM12 3.75C16.5563 3.75 20.25 7.44365 20.25 12H21.75C21.75 6.61522 17.3848 2.25 12 2.25V3.75Z",
      }),
      m("path.secondary", {
        d:"M21 12C21 16.9706 16.9706 21 12 21C7.02944 21 3 16.9706 3 12C3 7.02944 7.02944 3 12 3C16.9706 3 21 7.02944 21 12Z",
      }),
    ])
  }
}