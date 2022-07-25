export class NumberUtils {
  public static getNumberOfDigits(i:number):string {
    var res = "" + i;
    if (res.includes(".")) {
        if (i<0) {
            if (i < 10) i = Math.round(i * 10) / 10;
            else i = Math.round(i);
        } else {
            if (i < 10) i = Math.round(i * 100) / 100;
            else if (i < 100) i = Math.round(i * 10) / 10;
            else i = Math.round(i);
        }
        res = "" + i;
    }
    var missing = 4 - res.length;
    if (missing > 0 && res.indexOf(".")==-1) {
        res = res+".";
        missing--;
    }
    while (missing-- > 0) res = res+"0";
    return res;

  }

  public static getSI(i:number):string {
    if (i < 1000) return NumberUtils.getNumberOfDigits(i) + " ";
    i /= 1000;
    if (i < 1000) return NumberUtils.getNumberOfDigits(i) + "K";
    i /= 1000;
    if (i < 1000) return NumberUtils.getNumberOfDigits(i) + "M";
    i /= 1000;
    if (i < 1000) return NumberUtils.getNumberOfDigits(i) + "G";
    i /= 1000;
    return NumberUtils.getNumberOfDigits(i) + "T";
  }

}