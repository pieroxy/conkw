import m from 'mithril';

export class ShieldCheckIcon implements m.ClassComponent {
  public view() {
    return m("svg.icon", {
      viewBox:"0 0 24 24",
      fill:"none",
      xmlns:"http://www.w3.org/2000/svg"
    }, [
      m("path.secondary", {
        fillRule:"evenodd",
        clipRule:"evenodd", 
        d:"M4 7L12 3L20 7C20 12.1932 17.2157 19.5098 12 21C6.78428 19.5098 4 12.1932 4 7Z"
      }),
      m("path.primary", {
        strokeLinecap:"round",
        strokeLinejoin:"round",
        d:"M15.0001 10L11.0001 14L9 12M12 3L4 7C4 12.1932 6.78428 19.5098 12 21C17.2157 19.5098 20 12.1932 20 7L12 3Z",
      })
    ])
  }
}