/*
 * JiraConnection.java
 * Created on 15.07.2013 11:42:43 
 */
package plugin.bg.sparebits.pdi.jira;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.security.KeyStore;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.AuthCache;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Represents connection to a JIRA instance. Also provides helper methods to obtain different information from the
 * remote JIRA instance
 * @author nneikov 2013
 */
public class JiraConnection {

    private Logger log = LoggerFactory.getLogger(getClass());
    private String protocol;
    private String host;
    private int port;
    private String root = "/";
    private String username;
    private String password;

    private DefaultHttpClient httpClient;
    private BasicHttpContext localcontext;

    public JiraConnection() {
    }

    public JiraConnection(URL url, String username, String password) {
        this.protocol = url.getProtocol();
        this.host = url.getHost();
        this.port = url.getPort();
        this.root = url.getPath();
        this.username = username;
        this.password = password;
    }

    public JiraConnection(String protocol, String host, int port, String root, String username, String password) {
        this.protocol = protocol;
        this.host = host;
        this.port = port;
        this.root = root;
        this.username = username;
        this.password = password;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getRoot() {
        return root;
    }

    public void setRoot(String root) {
        this.root = root;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Connects the JIRA instance using the specified username and password.
     */
    public void connect() {
        log.debug(String.format("Connecting: %s://%s:%d%s .....", protocol, host, port, root));
        httpClient = buildHttpClient();
        HttpHost targetHost = new HttpHost(host, port, protocol);
        // Create AuthCache instance
        AuthCache authCache = new BasicAuthCache();
        // Generate BASIC scheme object and add it to the local auth cache
        BasicScheme basicAuth = new BasicScheme();
        authCache.put(targetHost, basicAuth);
        // Add AuthCache to the execution context
        localcontext = new BasicHttpContext();
        localcontext.setAttribute(ClientContext.AUTH_CACHE, authCache);
        httpClient.getCredentialsProvider().setCredentials(new AuthScope(host, AuthScope.ANY_PORT),
                new UsernamePasswordCredentials(username, password));
        log.info(String.format("Connected: %s://%s:%d%s!", protocol, host, port, root));
    }

    @SuppressWarnings("deprecation")
    private DefaultHttpClient buildHttpClient() {
        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
            SSLSocketFactory sf = new CustomSSLSocketFactory(trustStore);
            sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
            HttpParams params = new BasicHttpParams();
            HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
            HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);
            SchemeRegistry registry = new SchemeRegistry();
            registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));
            registry.register(new Scheme("https", sf, 443));
            ClientConnectionManager ccm = new ThreadSafeClientConnManager(params, registry);
            return new DefaultHttpClient(ccm, params);
        } catch (Exception e) {
            return new DefaultHttpClient();
        }
    }

    public String get(String request) throws ClientProtocolException, IOException {
        HttpGet httpget = new HttpGet(String.format("%s://%s:%d%s/rest/api/2%s", protocol, host, port, root, request));
        httpget.addHeader("Content-Type", "application/json");
        httpget.addHeader("Accept", "application/json");
        HttpResponse response = httpClient.execute(httpget, localcontext);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        response.getEntity().writeTo(baos);
        return new String(baos.toByteArray(), "utf-8");
    }

}
