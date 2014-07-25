package eu.iescities.pilot.rovereto.roveretoexplorer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;

public class SplashScreenActivity extends ActionBarActivity {
private static final long SPLASH_TIME_OUT = 2000;

@Override
protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	setContentView(R.layout.splash_screen);

	new Handler().postDelayed(new Runnable() {

		/*
		 * Showing splash screen with a timer. This will be useful when you
		 * want to show case your app logo / company
		 */

		@Override
		public void run() {
			// This method will be executed once the timer is over
			// Start your app main activity
			
			if (DTHelper.showTermsDialog(getSharedPreferences(DTHelper.T_D_PREFS, Context.MODE_PRIVATE))){
				  DialogFragment newFragment = TermsDialogBox.newInstance();
				    newFragment.show(SplashScreenActivity.this.getSupportFragmentManager(), "dialog");
				} else {
					finish();
					Intent i = new Intent(SplashScreenActivity.this, MainActivity.class);
					startActivity(i);
				}



			// close this activity
		}
	}, SPLASH_TIME_OUT);
}

}
