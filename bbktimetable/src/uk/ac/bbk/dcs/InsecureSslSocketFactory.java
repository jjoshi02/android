/**
 * 
 */
package uk.ac.bbk.dcs;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.SSLSocketFactory;

/**
 * insecure ssl factory
 * 
 * @author jjoshi02
 * 
 */
public class InsecureSslSocketFactory extends SSLSocketFactory {

	private static final String[] ENABLED_PROTOCOLS = new String[] { "SSLv3" };
	private SSLContext sslContext = SSLContext.getInstance("TLS");

	public InsecureSslSocketFactory(KeyStore truststore)
			throws NoSuchAlgorithmException, KeyManagementException,
			KeyStoreException, UnrecoverableKeyException {

		super(truststore);

		TrustManager tm = new X509TrustManager() {

			public void checkClientTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public void checkServerTrusted(X509Certificate[] chain,
					String authType) throws CertificateException {
			}

			public X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};

		sslContext.init(null, new TrustManager[] { tm }, null);
	}

	@Override
	/**
	 * create socket
	 */
	public Socket createSocket(Socket s, String host, int port,
			boolean autoClose) throws UnknownHostException, IOException {
		// TODO Auto-generated method stub
		SSLSocket sslSocket = (SSLSocket) sslContext.getSocketFactory()
				.createSocket(s, host, port, autoClose);
		setSslV3Only(sslSocket);
		return sslSocket;
	}

	@Override
	/**
	 * create socket
	 */
	public Socket createSocket() throws IOException {
		SSLSocket sslSocket = (SSLSocket) sslContext.getSocketFactory()
				.createSocket();
		setSslV3Only(sslSocket);
		return sslSocket;
	}

	/**
	 * set ssl version 3
	 * 
	 * @param sslSocket
	 */
	private void setSslV3Only(SSLSocket sslSocket) {
		// TODO Auto-generated method stub
		sslSocket.setEnabledProtocols(ENABLED_PROTOCOLS);
	}

}
