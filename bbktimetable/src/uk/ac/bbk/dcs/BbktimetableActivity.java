package uk.ac.bbk.dcs;

/**
 * @author jjoshi02
 * 
 */

import java.io.IOException;
import java.io.UnsupportedEncodingException;
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
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

/**
 * main activity
 * 
 * @author jjoshi02
 * 
 */
public class BbktimetableActivity extends Activity implements OnClickListener {
	private static final String url = "https://puck.mda.bbk.ac.uk/bsis_student/pp_stu";
	public static String timetableString = null;
	private static EditText uname;
	private static EditText pword;
	private Button submit;
	private CheckBox rememberCbx;
	private HttpClient c;
	private String PREFS = "MyPrefs";
	private SharedPreferences myPrefs;

	@Override
	/**
	 * on resume
	 */
	public void onResume() {
		super.onResume();
	}

	@Override
	/**
	 * on pause
	 */
	public void onPause() {
		super.onPause();
	}

	@Override
	/**
	 * on destroy
	 */
	public void onDestroy() {
		super.onDestroy();
		System.gc();
		submit.setBackgroundDrawable(null);
	}

	@Override
	/**
	 * On create
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		uname = (EditText) findViewById(R.id.username);
		pword = (EditText) findViewById(R.id.password);
		submit = (Button) findViewById(R.id.buttonsubmit);
		rememberCbx = (CheckBox) findViewById(R.id.checkboxremember);

		myPrefs = getSharedPreferences(PREFS, 0);
		boolean rememberMe = myPrefs.getBoolean("rememberMe", false);

		if (rememberMe == true) {
			String login = myPrefs.getString("login", null);
			String upass = myPrefs.getString("password", null);

			if (login != null && upass != null) {
				byte[] dlogin = Base64.decode(login, 0);
				byte[] dpass = Base64.decode(upass, 0);
				String strlogin = null;
				String strpass = null;
				try {
					strlogin = new String(dlogin, "UTF-8");
					strpass = new String(dpass, "UTF-8");
				} catch (UnsupportedEncodingException e) {
					Toast.makeText(BbktimetableActivity.this,
							"Unable to retrieve text in UTF-8",
							Toast.LENGTH_LONG).show();
					System.out.println("Unable to retrieve text in UTF-8");
					e.printStackTrace();
				}

				uname.setText(strlogin);
				pword.setText(strpass);

				// System.out.println("Decrypted login :" + strlogin);
				// System.out.println("Decrypted pass :" + strpass);
				rememberCbx.setChecked(true);
			}
		}

		try {
			c = getNewHttpClient();
		} catch (Exception e) {
			Toast.makeText(BbktimetableActivity.this,
					"Unable to access Internet", Toast.LENGTH_LONG).show();
			throw new RuntimeException("Unable to initialise HttpClient", e);
		}
		submit.setOnClickListener(BbktimetableActivity.this);
	}

	/**
	 * Save credentials in phone settings
	 * 
	 * @throws UnsupportedEncodingException
	 */
	private void saveLoginDetails() throws UnsupportedEncodingException {
		String login = uname.getText().toString();
		String pass = pword.getText().toString();

		if (login != null && pass != null) {
			Editor e = myPrefs.edit();
			e.putBoolean("rememberMe", true);

			byte[] logindata = null;
			byte[] passdata = null;

			try {
				logindata = login.getBytes("UTF-8");
				passdata = pass.getBytes("UTF-8");
			} catch (UnsupportedEncodingException e1) {
				Toast.makeText(BbktimetableActivity.this,
						"Unable to convert text in UTF-8", Toast.LENGTH_LONG)
						.show();
				System.out.println("Unable to convert text in UTF-8");
				e1.printStackTrace();
			}
			String loginbase64 = Base64.encodeToString(logindata,
					Base64.DEFAULT);
			String passbase64 = Base64.encodeToString(passdata, Base64.DEFAULT);
			e.putString("login", loginbase64);
			e.putString("password", passbase64);

			// System.out.println("Encrypted login is: " + loginbase64);
			// System.out.println("Encrypted pass is: " + passbase64);
			e.commit();
		} else {
			Toast.makeText(BbktimetableActivity.this,
					"Username / Password blank, try again", Toast.LENGTH_LONG)
					.show();
			System.out.println("Username / Password blank, try again");
		}
	}

	/**
	 * Remove credentials from phone settings
	 */
	private void removeLoginDetails() {
		Editor e = myPrefs.edit();
		e.putBoolean("rememberMe", false);
		e.remove("login");
		e.remove("password");
		e.commit();
	}

	/**
	 * call on click
	 */
	public void onClick(View v) {
		boolean isChecked = false;
		isChecked = rememberCbx.isChecked();
		if (isChecked) {
			try {
				saveLoginDetails();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
		} else {
			removeLoginDetails();
		}

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

	/**
	 * Retrieve timetable and set static timetableString
	 * 
	 * @param page
	 *            to scrape
	 * @return true or false
	 */
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

	/**
	 * Retrieve page for url
	 * 
	 * @param url
	 *            URL to retrieve
	 * @return page content
	 */
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

	/**
	 * Get Student ID from page
	 * 
	 * @param page
	 *            HTML page
	 * @return Student ID
	 */
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

	/**
	 * Get new HTTP client
	 * 
	 * @return HTTP client object
	 * @throws KeyStoreException
	 * @throws KeyManagementException
	 * @throws NoSuchAlgorithmException
	 * @throws UnrecoverableKeyException
	 * @throws CertificateException
	 * @throws IOException
	 */
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

	/**
	 * Get Username Password credentials
	 * 
	 * @return UsernamePasswordCredentials object
	 */
	public static UsernamePasswordCredentials getCredential() {
		// String username = "jjoshi02";
		// String password = "";

		String username = uname.getText().toString();
		String password = pword.getText().toString();

		UsernamePasswordCredentials creds = null;

		// System.out.println("User ID :" + username);
		// System.out.println("Password is :" + password);
		return creds = new UsernamePasswordCredentials(username, password);
	}
}
