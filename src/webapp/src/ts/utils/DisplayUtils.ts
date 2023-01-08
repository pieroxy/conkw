import m = require('mithril');

export class DisplayUtils {
  private static monospaceCharWidth?:number = undefined;

  public static getMonospaceCharWidth(fontSizeInPx:number):number {
    if (!this.monospaceCharWidth) {
      let div = document.createElement("DIV");
      div.innerHTML = "a";
      div.className = "monospace";
      div.style.fontSize = "100px";
      div.style.display = "inline-block";
      div.style.margin = "0";
      div.style.padding = "0";
      document.body.appendChild(div);
      this.monospaceCharWidth = div.clientWidth;
      document.body.removeChild(div);
    }
    let res = this.monospaceCharWidth / 100 * fontSizeInPx;

    return res;
  }

  public static getSimpleClassNameWithTooltip(cn:string):m.Children {
    let index = cn.lastIndexOf(".");
    if (index < 1) return cn;
    return m("span", {title:cn}, cn.substring(index+1));
  }
}