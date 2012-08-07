package uk.ac.bbk.dcs;

import android.app.Activity;
import android.os.Bundle;
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
    
    uname = (EditText)findViewById(R.id.username);
    pword = (EditText)findViewById(R.id.password);
    submit = (Button)findViewById(R.id.buttonsubmit);
    
    submit.setOnClickListener(this);
    
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		System.out.println(uname.getText().toString());
	}
}