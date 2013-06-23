package com.intere.spring.nzb.builder;

import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.X509HostnameVerifier;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import com.intere.spring.nzb.builder.StreamUtils;

/**
 * Blackbox test for HTTP Components.
 * @author einternicola
 *
 */
@Ignore
public class TestHttpComponents {
	
	private static final Logger LOG = Logger.getLogger(TestHttpComponents.class);

    private static final String BASE_URL = "http://binsearch.info";    
    
    /**
     * This method demonstrates how to use the Apache Http Client (Components) API.
     * @throws Exception
     */
    @SuppressWarnings("deprecation")
    @Test
    public void testGetQuery() throws Exception
    {    	
    	
//        String query = "q=The Lord of the Rings";
    	String query="q=The Big Bang Theory";
        String age = "adv_age=1100";
        String max = "max=1000";
        
        DefaultHttpClient client = new DefaultHttpClient();
        client = wrapClient(client);
        
        HttpGet httpget = new HttpGet(BASE_URL + StreamUtils.buildQueryParams(query,age,max));
        LOG.info("Executing query: " + BASE_URL + StreamUtils.buildQueryParams(query,age,max));
        
        HttpResponse response = client.execute(httpget);
        HttpEntity entity = response.getEntity();
        
        if (entity != null) {
            entity.consumeContent();
        }
        
        LOG.info("Initial set of cookies:");
        List<Cookie> cookies = client.getCookieStore().getCookies();
        if (cookies.isEmpty()) {
            LOG.info("None");
        } else {
            for (int i = 0; i < cookies.size(); i++) {
                LOG.info("- " + cookies.get(i).toString());
            }
        }        
    }
    
    public static DefaultHttpClient wrapClient(HttpClient base) {
        try {
            SSLContext ctx = SSLContext.getInstance("TLS");
            X509TrustManager tm = new X509TrustManager() {
 
                public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }
 
                public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
                }
 
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }
            };
            X509HostnameVerifier verifier = new X509HostnameVerifier() {
 
                @Override
                public void verify(String string, SSLSocket ssls) throws IOException {
                }
 
                @Override
                public void verify(String string, X509Certificate xc) throws SSLException {
                }
 
                @Override
                public void verify(String string, String[] strings, String[] strings1) throws SSLException {
                }
 
                @Override
                public boolean verify(String string, SSLSession ssls) {
                    return true;
                }
            };
            ctx.init(null, new TrustManager[]{tm}, null);
            SSLSocketFactory ssf = new SSLSocketFactory(ctx);
            ssf.setHostnameVerifier(verifier);
            ClientConnectionManager ccm = base.getConnectionManager();
            SchemeRegistry sr = ccm.getSchemeRegistry();
            sr.register(new Scheme("https", ssf, 443));
            return new DefaultHttpClient(ccm, base.getParams());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }
}
