package uk.ac.bbk.dcs;

import java.net.URLEncoder;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class MyTimetableActivity extends Activity implements OnClickListener {
	private Button buttonquit;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.timetable);

		buttonquit = (Button) findViewById(R.id.buttonquit);
		WebView webview = (WebView) findViewById(R.id.webview);
		webview.setVerticalScrollBarEnabled(true);
		webview.setHorizontalScrollBarEnabled(true);
		webview.setBackgroundColor(Color.parseColor("#882233"));
		String summary = "<html><body>" + BbktimetableActivity.timetableString
				+ "</body></html>";
		// System.out.println(summary);

		webview.loadData(URLEncoder.encode(summary).replaceAll("\\+", " "),
				"text/html", "utf-8");
		buttonquit.setOnClickListener(MyTimetableActivity.this);
	}

	public void onDestroy() {
		super.onDestroy();
	}

	public void onClick(View v) {
		if (v == buttonquit) {
			super.onDestroy();
			System.gc();
			System.runFinalizersOnExit(true);
			System.exit(0);
		}
	}
}