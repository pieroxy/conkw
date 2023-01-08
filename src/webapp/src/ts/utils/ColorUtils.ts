export class ColorUtils {
  private static gaugeDefaultColor:string="";
  private static gaugeErrorColor:string="";
  private static gaugeDefaultBackground:string="";
  private static gaugeErrorThumb:string="";


  public static getGaugeDefaultColor():string {
    this.init();
    return this.gaugeDefaultColor;
  }

  public static getGaugeDefaultBackground():string {
    this.init();
    return this.gaugeDefaultBackground;
  }

  public static getGaugeErrorBackground():string {
    this.init();
    return this.gaugeErrorColor;
  }

  public static getGaugeErrorThumb():string {
    this.init();
    return this.gaugeErrorThumb;
  }

  private static init() {
    if (this.gaugeDefaultBackground.length==0) {
      this.gaugeDefaultBackground = this.getColorForId("gaugeDefaultBackground");
      this.gaugeDefaultColor = this.getColorForId("gaugeDefaultColor");
      this.gaugeErrorColor = this.getColorForId("gaugeErrorColor");
      this.gaugeErrorThumb = this.getColorForId("gaugeErrorThumb");
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