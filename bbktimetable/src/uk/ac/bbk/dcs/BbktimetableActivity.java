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
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

public class BbktimetableActivity extends Activity implements OnClickListener {
	private static final String url = "https://puck.mda.bbk.ac.uk/bsis_student/pp_stu";
	public static String timetableString = null;
	private static EditText uname;
	private static EditText pword;
	private Button submit;
	private HttpClient c;
	private String PREFS = "MyPrefs";
	private SharedPreferences myPrefs;

	@Override
	public void onResume() {
		super.onResume();
		/*
		 * Toast.makeText(BbktimetableActivity.this, "  onResume called",
		 * Toast.LENGTH_LONG).show();
		 */}

	@Override
	public void onPause() {
		super.onPause();
		/*
		 * Toast.makeText(BbktimetableActivity.this, "  onPause called",
		 * Toast.LENGTH_LONG).show();
		 */}

	@Override
	public void onDestroy() {
		super.onDestroy();
		/*
		 * Toast.makeText(BbktimetableActivity.this, " on destroy called",
		 * Toast.LENGTH_LONG).show();
		 */
		System.gc();
		// System.runFinalizersOnExit(true);
		// System.exit(0);
		submit.setBackgroundDrawable(null);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
		myPrefs = getSharedPreferences(PREFS, 0);

		boolean rememberMe = myPrefs.getBoolean("rememberMe", false);

		if (rememberMe == true) {
			// get previously stored login details
			String login = myPrefs.getString("login", null);
			String upass = myPrefs.getString("password", null);

			if (login != null && upass != null) {
				// fill input boxes with stored login and pass
				EditText loginEbx = (EditText) findViewById(R.id.username);
				EditText passEbx = (EditText) findViewById(R.id.password);
				loginEbx.setText(login);
				passEbx.setText(upass);

				// set the check box to 'checked'
				CheckBox rememberMeCbx = (CheckBox) findViewById(R.id.checkboxremember);
				rememberMeCbx.setChecked(true);
			}
		}

		uname = (EditText) findViewById(R.id.username);
		pword = (EditText) findViewById(R.id.password);
		submit = (Button) findViewById(R.id.buttonsubmit);

		try {
			c = getNewHttpClient();
		} catch (Exception e) {
			Toast.makeText(BbktimetableActivity.this,
					"Unable to access Internet", Toast.LENGTH_LONG).show();
			throw new RuntimeException("Unable to initialise HttpClient", e);
		}
		submit.setOnClickListener(BbktimetableActivity.this);
	}

	public void onClick(View v) {
		String page = null;
		String tableText = null;
		page = getPage(url);
		if (page != null) {
			String id = getStudentId(page);
			if (id != null) {
				String url2 = url + "tt?pstuc=" + id;
				System.out.println("*** Fetching timetable URL :" + url2);
				page = null;
				page = getPage(url2);
				if (page != null) {
					if (getMyTimetable(page)) {
						if (timetableString != null) {
							if (v == submit) {
								Intent timetableIntent = new Intent(
										v.getContext(),
										MyTimetableActivity.class);
								startActivity(timetableIntent);
								callNull();
							}
						} else {
							Toast.makeText(this, "Unable to get timetable",
									Toast.LENGTH_LONG).show();
							System.out.println("Unable to get timetable");
						}
					}
				}
			} else {
				Toast.makeText(this, "Failed to login as Student",
						Toast.LENGTH_LONG).show();
				System.out.println("Failed to login as Student");
			}
		} else {
			Toast.makeText(this, "Unable to load page", Toast.LENGTH_LONG)
					.show();
			System.out.println("Unable to load page");
		}
	}

	private boolean getMyTimetable(String page) {
		String tableText, first, second, third;
		tableText = first = second = third = null;
		boolean flag = false;

		Document doc = Jsoup.parse(page);
		if (doc != null) {
			Element e = doc.getElementsContainingOwnText(
					"Timetable By Date Range").first();
			if (e != null) {
				Element myElement = e.parent();
				first = e.toString();
				Element next = myElement.nextElementSibling();
				second = next.toString();
				Element secondNext = myElement.nextElementSibling()
						.nextElementSibling();
				third = secondNext.toString();
				tableText = first + second + third;
				// System.out.println(tableText);
				if (tableText != null && first != null && second != null
						&& third != null) {
					timetableString = tableText;
					flag = true;
				} else {
					Toast.makeText(this, "Unable to scrap Timetable page",
							Toast.LENGTH_LONG).show();
					System.out.println("Unable to scrap timetable page");
				}
			} else {
				Toast.makeText(this, "Unable to find Timetable",
						Toast.LENGTH_LONG).show();
				System.out.println("Unable to find Timetable");
			}
		} else {
			Toast.makeText(this, "Unable to parse document", Toast.LENGTH_LONG)
					.show();
			System.out.println("Unable to parse document");
		}
		return flag;
	}

	public void callNull() {
		this.finish();
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
				e.printStackTrace();
				throw new RuntimeException(e);
			}

			try {
				HttpResponse response = c.execute(request);
				page = EntityUtils.toString(response.getEntity());
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
		} else {
			Toast.makeText(this, "Unable to retrieve credentials",
					Toast.LENGTH_LONG).show();
			System.out.println("Unable to retrieve credentials");
		}
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
			Toast.makeText(this, "No valid Student ID found", Toast.LENGTH_LONG)
					.show();
			System.out.println("No valid Student ID found");
		}
		return studentId;
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
		String username = "jjoshi02";
		String password = "Uk5afe25";

		//String username = uname.getText().toString();
		//String password = pword.getText().toString();

		UsernamePasswordCredentials creds = null;

		System.out.println("User ID :" + username);
		System.out.println("Password is :" + password);

		return creds = new UsernamePasswordCredentials(username, password);
	}
}
