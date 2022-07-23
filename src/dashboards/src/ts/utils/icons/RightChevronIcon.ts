import m from 'mithril';

export class RightChevronIcon implements m.ClassComponent {
  public view() {
    return m("svg.icon.chevron", {
      viewBox:"0 0 24 24",
      fill:"none",
      xmlns:"http://www.w3.org/2000/svg"
    }, [
      m("path.primary", {
        d:"M9 20L17 12L9 4",
      })
    ])
  }
}