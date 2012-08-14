package uk.ac.bbk.dcs;

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

	EditText uname;
	EditText pword;
	Button submit;

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

		System.out.println(uname.getText().toString());

		String surl = "https://puck.mda.bbk.ac.uk/bsis_student/pp_stu";
		String username = "jjoshi02";
		String password = "";
		HttpURLConnection c = null;

		try {
			c = (HttpURLConnection) new URL(surl).openConnection();
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
						+ Base64.encode("jjoshi02:".getBytes(),
								Base64.NO_WRAP));
		try {
			c.connect();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			c.disconnect();
		}
		
		InputStream is = null;
		InputStreamReader isr = new InputStreamReader(is);

		int numCharsRead;
		char[] charArray = new char[1024];
		StringBuffer sb = new StringBuffer();
		try {
			while ((numCharsRead = isr.read(charArray)) > 0) {
				sb.append(charArray, 0, numCharsRead);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String result = sb.toString();

		System.out.println("*** BEGIN ***");
		System.out.println(result);
		System.out.println("*** END ***");

	}

}
