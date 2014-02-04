package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere;

import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.R.id;
import eu.iescities.pilot.rovereto.roveretoexplorer.R.layout;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class EvDetails_DaSapere_Form_Activity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.frag_ev_detail_dasapere_edit);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.ev_details__da_sapere__form_, menu);
		return true;
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		Button cancelButton = (Button) findViewById(R.id.edit_contacts_cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		Button okButton = (Button) findViewById(R.id.edit_contacts_modify_button);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
	}
	
}
