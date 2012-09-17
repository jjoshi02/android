package uk.ac.bbk.dcs;

import java.net.URLEncoder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

/**
 * My timetable activity
 * 
 * @author jjoshi02
 * 
 */
public class MyTimetableActivity extends Activity implements OnClickListener {
	private Button buttonquit;

	@Override
	/**
	 * on create
	 */
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable);

		buttonquit = (Button) findViewById(R.id.buttonquit);
		WebView webview = (WebView) findViewById(R.id.webview);
		webview.setVerticalScrollBarEnabled(true);
		webview.setHorizontalScrollBarEnabled(true);
		// webview.setBackgroundColor(Color.parseColor("#882233"));
		// webview.setBackgroundColor(0x00000000);
		String summary = "<html><body>" + BbktimetableActivity.timetableString
				+ "</body></html>";
		// System.out.println(summary);

		webview.loadData(URLEncoder.encode(summary).replaceAll("\\+", " "),
				"text/html", "utf-8");
		buttonquit.setOnClickListener(MyTimetableActivity.this);
	}

	/**
	 * on destroy
	 */
	public void onDestroy() {
		super.onDestroy();
	}

	/**
	 * on click
	 */
	public void onClick(View v) {
		if (v == buttonquit) {
			super.onDestroy();
			System.gc();
			System.runFinalizersOnExit(true);
			System.exit(0);
		}
	}
}