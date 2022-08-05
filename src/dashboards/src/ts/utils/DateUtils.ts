export class DateUtils {
  static formatRoughDate(date:string):string {
    let delay = Date.now() - this.parseDate(date).getTime();
    if (delay > 0) {
      if (delay < 5000) return "Just now"
      if (delay < 120000) return Math.round(delay/1000) + " seconds ago";
      if (delay < 60*60000) return Math.round(delay/60000) + " minutes ago";
      if (delay < 24*60*60000) return Math.round(delay/(60000*60)) + " hours ago";
      return Math.round(delay/(60000*60*24)) + " days ago"
    } else {
      return "In the future";
    }
  }

  static parseDate(date:string):Date {
    return new Date(date);
  }
}