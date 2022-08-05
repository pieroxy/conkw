export class UserAgentUtils {
  public static getDescription(ua:string):string {
    return UserAgentUtils.getBrowser(ua) + " / " + UserAgentUtils.getOS(ua);
  }
  static getBrowser(ua: string):string {
    if (ua.includes("Firefox/") || ua.includes("FxiOS/")) return "Firefox";
    if (ua.includes("CriOS/")) return "Chrome";
    if (ua.includes("EdgiOS/") || ua.includes("Edg/") || ua.includes("Edge/") || ua.includes("EdgA/")) return "Edge";
    if (ua.includes("iPhone;")
      ||ua.includes("iPod;")
      ||ua.includes("iPad;")
      ||ua.includes("Macintosh;")) 
      if (ua.includes("Safari/")) return "Safari"
    if (ua.includes("Chrome/")) return "Chrome";
    return "Unknown browser"
  }
  static getOS(ua: string):string {
    if (ua.includes("iPhone;")||ua.includes("iPod;")) return "iPhone";
    if (ua.includes("iPad;")) return "iPhone";
    if (ua.includes("Macintosh;")) return "Mac"
    if (ua.includes("Windows")) return "Windows"
    if (ua.includes("Android ")) return "Android";
    if (ua.includes("Linux ")) return "Linux"
    return "Unknown OS";
  }

}