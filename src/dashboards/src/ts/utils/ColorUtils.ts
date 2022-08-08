export class ColorUtils {
  private static gaugeDefaultColor:string="";
  private static gaugeDefaultBackground:string="";


  public static getGaugeDefaultColor():string {
    this.init();
    return this.gaugeDefaultColor;
  }

  public static getGaugeDefaultBackground():string {
    this.init();
    return this.gaugeDefaultBackground;
  }

  private static init() {
    if (this.gaugeDefaultBackground.length==0) {
      this.gaugeDefaultBackground = this.getColorForId("gaugeDefaultBackground");
      this.gaugeDefaultColor = this.getColorForId("gaugeDefaultColor");
    }
  }

  private static getColorForId(id:string):string {
    let element = document.getElementById(id);
    if (!element) throw "Element #" + id + " not found."
    let style = window.getComputedStyle(element, null);
    let color = style.getPropertyValue('color');
    return color;
  }
}