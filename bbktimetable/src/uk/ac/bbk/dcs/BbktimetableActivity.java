package uk.ac.bbk.dcs;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.scheme.SocketFactory;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class BbktimetableActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private EditText uname;
	private EditText pword;
	private Button submit;
	private HttpClient c;
	private boolean loginFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		try {
			c = getNewHttpClient();
		} catch (Exception e) {
			throw new RuntimeException("Unable to initialise HttpClient", e);
		}

		uname = (EditText) findViewById(R.id.username);
		pword = (EditText) findViewById(R.id.password);
		submit = (Button) findViewById(R.id.buttonsubmit);

		// StrictMode.ThreadPolicy policy = new
		// StrictMode.ThreadPolicy.Builder()
		// .permitAll().build();
		// StrictMode.setThreadPolicy(policy);

		submit.setOnClickListener(this);
	}

	public HttpClient getNewHttpClient() throws KeyStoreException,
			KeyManagementException, NoSuchAlgorithmException,
			UnrecoverableKeyException, CertificateException, IOException {

		KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
		trustStore.load(null, null);

		SSLSocketFactory sf = new InsecureSslSocketFactory(trustStore);
		sf.setHostnameVerifier(SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

		HttpParams params = new BasicHttpParams();
		HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
		HttpProtocolParams.setContentCharset(params, HTTP.UTF_8);

		SchemeRegistry registry = new SchemeRegistry();
		registry.register(new Scheme("http", PlainSocketFactory
				.getSocketFactory(), 80));
		registry.register(new Scheme("https", (SocketFactory) sf, 443));

		ClientConnectionManager ccm = new ThreadSafeClientConnManager(params,
				registry);

		return new DefaultHttpClient(ccm, params);
	}

	public static UsernamePasswordCredentials getCredential() {
		String username = "jjoshi02"; // @@ uname.getText().toString()
		String password = ""; // @@ pword.getText().toString();
		UsernamePasswordCredentials creds = null;

		System.out.println("User ID :" + username);
		System.out.println("Password is :" + password);

		return creds = new UsernamePasswordCredentials(username, password);
	}

	public String getPage(String url) {
		String page = null;
		HttpUriRequest request = null;

		if (getCredential() != null) {
			request = new HttpGet(url);

			try {
				request.addHeader(new BasicScheme().authenticate(
						getCredential(), request));
			} catch (AuthenticationException e) {
				// TODO: Handle AuthenticationException
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else {
			System.out.println("Credentials blank");
		}

		try {
			HttpResponse response = c.execute(request);
			page = EntityUtils.toString(response.getEntity());
		} catch (IOException e) {
			// TODO: Handle IOException
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		System.out.println(page);
		return page;
	}

	public String getStudentId(String page) {
		String studentId = null;

		Pattern pattern = Pattern.compile("pstuc=(\\d+)\\'");
		Matcher matcher = pattern.matcher(page);
		if (matcher.find()) {
			studentId = matcher.group(1);
			System.out.println("Student ID is :" + studentId);
		} else {
			System.out.println("No valid Student ID found");
		}
		return studentId;

	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		String url = "https://puck.mda.bbk.ac.uk/bsis_student/pp_stu";
		String page = getPage(url);
		if (page != null) {
			String id = getStudentId(page);
			if (id != null) {
				String url2 = "https://puck.mda.bbk.ac.uk/bsis_student/pp_stutt?pstuc="
						+ id;
				System.out.println("**********************" + url2
						+ "**********************");
				page = getPage(url2);
			}
		}
	}
}
