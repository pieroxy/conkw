package net.pieroxy.conkw.webapp.grabbers.http;

import net.pieroxy.conkw.api.metadata.grabberConfig.ConfigField;
import net.pieroxy.conkw.collectors.SimpleCollector;
import net.pieroxy.conkw.grabbersBase.TimeThrottledGrabber;
import net.pieroxy.conkw.utils.duration.CDurationParser;
import net.pieroxy.conkw.utils.hashing.Hashable;
import net.pieroxy.conkw.utils.hashing.Md5Sum;

import javax.net.ssl.*;
import java.io.IOException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateParsingException;
import java.security.cert.X509Certificate;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

// Bluntly copy pasted from https://gist.github.com/sureshpai/8c762603969e78dc2c68
public class HttpsCertGrabber extends TimeThrottledGrabber<HttpsCertGrabber.HttpsCertGrabberConfig> {
  private String _domainsAsString = null;
  // usage : SSLCertExpiry host <cn>
  // does a http://host request.
  // takes an optional cn variable (which defaults to host)

  public static void main(String[] args) throws Exception {
    if (args.length <1){
      System.out.println("Usage: SSLCertExpiry <host> [<CN>]");
      return;
    }
    String cn = args[0];
    if (args.length > 1){
      cn = args[2];
    }
    Date expiresOn = null;
    expiresOn = new HttpsCertGrabber().getExpirationDate(new HttpsTarget(cn));

    if (expiresOn!=null) {
      Date now = new Date();
      System.out.println(args[0] + " certificate expires on :" + expiresOn + ".. only " +
          (expiresOn.getTime() - now.getTime()) / (1000 * 60 * 60 * 24) + " days to go");
    } else {
      System.out.println("Could not find " + cn + " in cert.");
    }
  }

  private Date getExpirationDate(HttpsTarget target) throws IOException, CertificateParsingException, NoSuchAlgorithmException, KeyManagementException {
    // without a trust manager, i was having problems of
    // sun.security.validator.ValidatorException: PKIX path building failed: sun.security.provider.certpath.SunCertPathBuilderException: unable to find valid certification path to requested target
    // the code below was taken from
    // http://stackoverflow.com/questions/7443235/getting-java-to-accept-all-certs-over-https
    TrustManager[] trustAllCerts = new TrustManager[]{
        new X509TrustManager() {

          public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
          }

          public void checkServerTrusted(X509Certificate[] arg0, String arg1)
              throws CertificateException {
            // TODO Auto-generated method stub

          }

          public void checkClientTrusted(X509Certificate[] arg0, String arg1)
              throws CertificateException {
            // TODO Auto-generated method stub

          }
        }
    };

    String bareCn = target.domainName;

    // Install the all-trusting trust manager
    try {
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
    } catch (Exception e) {
      log(Level.WARNING, "could not install trust manager.. continuing here; it may not be necessary");
    }


    URL url = new URL("https://"+ target.getDomainForUrl());
    HttpsURLConnection conn = (HttpsURLConnection)url.openConnection();
    if (target.noSslValidation!=null && target.noSslValidation) {
      HttpsURLConnection httpsConn = conn;
      HostnameVerifier allHostsValid = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
          return true;
        }
      };
      SSLContext sc = SSLContext.getInstance("SSL");
      sc.init(null, trustAllCerts, new java.security.SecureRandom());
      httpsConn.setHostnameVerifier(allHostsValid);
      httpsConn.setSSLSocketFactory(sc.getSocketFactory());
    }
    conn.setConnectTimeout(1000);
    conn.setReadTimeout(1000);
    conn.connect();
    Certificate[] certs = conn.getServerCertificates();
    for(Certificate c:certs){
      //System.out.println(c.getType());
      //System.out.println(c.toString());
      X509Certificate xc = (X509Certificate)c; // we should really check the type beore doing this typecast..
      Set<String> dn = new HashSet<>();
      dn.add(xc.getSubjectDN().getName());
      if (xc.getSubjectAlternativeNames()!=null)
        xc.getSubjectAlternativeNames().forEach(l -> {
          if (l == null)
            System.out.println("nullList");
          else
            l.stream().forEach(e -> {
              if (e instanceof String) dn.add((String)e);
              //System.out.println("Class " + e == null ? null : e.getClass().getName());
              //System.out.println("Value " + e);
            });
        });

      if (dn.contains(bareCn)){
        return xc.getNotAfter();
      }
    }
    return null;
  }

  @Override
  public String getDefaultName() {
    return "httpscert";
  }

  @Override
  public HttpsCertGrabberConfig getDefaultConfig() {
    HttpsCertGrabberConfig conf = new HttpsCertGrabberConfig();
    conf.setTtl(CDurationParser.parse("3h"));
    conf.setErrorTtl(CDurationParser.parse("3h"));
    return conf;
  }

  private String getDomainsAsString() {
    if (_domainsAsString==null) {
      _domainsAsString = getConfig().getUnifiedDomains().stream().map(HttpsTarget::getDomainForUrl).collect(Collectors.joining(","));
    }
    return _domainsAsString;
  }

  @Override
  protected void load(SimpleCollector res) {
    long now = System.currentTimeMillis();
    getConfig().getUnifiedDomains().forEach(s -> {
      try {
        Date exp = getExpirationDate(s);
        if (exp!=null) {
          log(Level.INFO, "Collected crt date for " + s.getKey());
          res.collect("date_" + s.getKey(), exp.getTime());
          res.collect("days_" + s.getKey(), (exp.getTime() - now) / (1000 * 60 * 60 * 24));
          res.collect("ts_" + s.getKey(), exp.getTime() - now);
        } else {
          log(Level.INFO, "Collected  *no* date for " + s);
        }
      } catch (Exception e) {
        log(Level.SEVERE, "Could not extract crt date for " + s, e);
        res.addError(e.getMessage());
      }
    });
    res.collect("alldomains", getDomainsAsString());
  }



  public static class HttpsCertGrabberConfig extends TimeThrottledGrabber.SimpleTimeThrottledGrabberConfig {
    @ConfigField(
        label="Domain names to check certs on",
        listItemLabel = "Domain"
    )
    List<String>domains;
    List<HttpsTarget>targets;

    public List<HttpsTarget> getUnifiedDomains() {
      List<HttpsTarget> result = new ArrayList<>();
      if (domains!=null) {
        domains.stream().map(HttpsTarget::new).forEach(result::add);
      }
      if (targets!=null) {
        result.addAll(targets);
      }
      return result;
    }

    public void setDomains(List<String> domains) {
      this.domains = domains;
    }

    @Override
    public void addToHash(Md5Sum sum) {
      getUnifiedDomains().forEach(sum::add);
    }

    public void setTargets(List<HttpsTarget> targets) {
      this.targets = targets;
    }
  }

  public static class HttpsTarget implements Hashable  {
    String target;
    int port;
    String domainName;
    Boolean noSslValidation;

    public void setTarget(String target) {
      this.target = target;
    }

    public void setPort(int port) {
      this.port = port;
    }

    public void setDomainName(String domainName) {
      this.domainName = domainName;
    }

    public void setNoSslValidation(Boolean noSslValidation) {
      this.noSslValidation = noSslValidation;
    }

    public HttpsTarget(String domainName) {
      if (domainName.contains(":")) {
        this.target = this.domainName = domainName.substring(0, domainName.indexOf(":"));
        port = Integer.parseInt(domainName.substring(domainName.indexOf(":")+1));
      } else {
        this.target = this.domainName = domainName;
        port = 443;
      }
    }

    public HttpsTarget() {
    }

    public String getDomainForUrl() {
      if (port == 0) return target;
      return target + ":" + port;
    }

    @Override
    public void addToHash(Md5Sum sum) {
      sum.add(target);
      sum.add(port + "");
      sum.add(domainName);
    }

    @Override
    public String toString() {
      return "HttpsTarget{" +
              "target='" + target + '\'' +
              ", port=" + port +
              ", domainName='" + domainName + '\'' +
              ", noSslValidation=" + noSslValidation +
              '}';
    }

    String getKey() {
      return target;
    }
  }
}
