package uk.ac.bbk.dcs;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.app.Activity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class BbktimetableActivity extends Activity implements OnClickListener {
	/** Called when the activity is first created. */

	private EditText uname;
	private EditText pword;
	private Button submit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		uname = (EditText) findViewById(R.id.username);
		pword = (EditText) findViewById(R.id.password);
		submit = (Button) findViewById(R.id.buttonsubmit);

		submit.setOnClickListener(this);
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub

		String url = "https://puck.mda.bbk.ac.uk/bsis_student/pp_stu";
		String username = "jjoshi02"; // @@ uname.getText().toString()
		String password = ""; // @@ pword.getText().toString();
		String result = null;
		HttpURLConnection c = null;
		InputStream in = null;

		System.out.println("User ID :" + username);
		System.out.println("Password is :" + password);

		try {
			c = (HttpURLConnection) new URL(url).openConnection();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		c.setRequestProperty(
				"Authorization",
				"basic "
						+ Base64.encode((username + ":" + password).getBytes(),
								Base64.NO_WRAP));
		try {
			c.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			c.disconnect();
		}

		try {
			in = new BufferedInputStream(c.getInputStream());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try {
			result = readStream(in);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.println("*** BEGIN ***");
		System.out.println(result);
		System.out.println("*** END ***");
	}

	public static String readStream(InputStream in) throws IOException {
		StringBuilder sb = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(in), 1000);
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			sb.append(line);
		}
		in.close();
		return sb.toString();
	}
}
