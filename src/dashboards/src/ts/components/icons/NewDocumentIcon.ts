import m from 'mithril';

export class NewDocumentIcon implements m.ClassComponent {
  public view() {
    return m("svg.icon", {
      viewBox:"0 0 24 24",
      fill:"none",
      xmlns:"http://www.w3.org/2000/svg"
    }, [
      m("path.secondary", {
        d:"M5 21H19V9H15C13.8954 9 13 8.10457 13 7V3H5V21Z"
      }),
      m("path.primaryfill", {
        d:"M5 3V2.25C4.58579 2.25 4.25 2.58579 4.25 3H5ZM19 21V21.75C19.4142 21.75 19.75 21.4142 19.75 21H19ZM5 21H4.25C4.25 21.4142 4.58579 21.75 5 21.75V21ZM14 3L14.5303 2.46967C14.3897 2.32902 14.1989 2.25 14 2.25V3ZM19 8H19.75C19.75 7.80109 19.671 7.61032 19.5303 7.46967L19 8ZM12.75 11C12.75 10.5858 12.4142 10.25 12 10.25C11.5858 10.25 11.25 10.5858 11.25 11H12.75ZM11.25 17C11.25 17.4142 11.5858 17.75 12 17.75C12.4142 17.75 12.75 17.4142 12.75 17H11.25ZM9 13.25C8.58579 13.25 8.25 13.5858 8.25 14C8.25 14.4142 8.58579 14.75 9 14.75V13.25ZM15 14.75C15.4142 14.75 15.75 14.4142 15.75 14C15.75 13.5858 15.4142 13.25 15 13.25V14.75ZM19 20.25H5V21.75H19V20.25ZM5.75 21V3H4.25V21H5.75ZM5 3.75H14V2.25H5V3.75ZM18.25 8V21H19.75V8H18.25ZM13.4697 3.53033L18.4697 8.53033L19.5303 7.46967L14.5303 2.46967L13.4697 3.53033ZM12.25 3V7H13.75V3H12.25ZM15 9.75H19V8.25H15V9.75ZM12.25 7C12.25 8.51878 13.4812 9.75 15 9.75V8.25C14.3096 8.25 13.75 7.69036 13.75 7H12.25ZM11.25 11V17H12.75V11H11.25ZM9 14.75H15V13.25H9V14.75Z"
      })
    ])
  }
}