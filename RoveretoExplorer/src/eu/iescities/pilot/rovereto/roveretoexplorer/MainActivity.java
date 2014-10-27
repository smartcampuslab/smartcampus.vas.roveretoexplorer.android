package eu.iescities.pilot.rovereto.roveretoexplorer;

import java.lang.reflect.Field;
import java.util.ArrayList;

import org.apache.http.HttpStatus;

import android.accounts.AccountManager;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.CategoryFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.AnimateFirstDisplayListener;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.EventsListingFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.questionnaire.QuizHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.SearchFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.log.LogHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.AbstractNavDrawerActivity;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.NavDrawerActivityConfiguration;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.NavDrawerAdapter;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.NavDrawerItem;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.NavMenuItem;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.NavMenuSection;
import eu.trentorise.smartcampus.ac.AACException;
import eu.trentorise.smartcampus.ac.SCAccessProvider;
import eu.trentorise.smartcampus.android.common.GlobalConfig;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class MainActivity extends AbstractNavDrawerActivity {

	private SharedPreferences sp;
	public static final String TAG_FRAGMENT_MAP = "fragmap";
	public static final String TAG_FRAGMENT_EVENT_LIST = "fragevent";

	private FragmentManager mFragmentManager;

	private boolean isLoading;
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			ViewConfiguration config = ViewConfiguration.get(this);
			Field menuKeyField = ViewConfiguration.class
					.getDeclaredField("sHasPermanentMenuKey");
			if (menuKeyField != null) {
				menuKeyField.setAccessible(true);
				menuKeyField.setBoolean(config, false);
			}
		} catch (Exception ex) {
			// Ignore
		}
		mFragmentManager = getSupportFragmentManager();
		signedIn();

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// this is a class created to avoid an Android bug
		// see the class for further infos.
		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout,
				R.drawable.ic_drawer, R.string.drawer_open,
				R.string.drawer_close) {

			@Override
			public void onDrawerSlide(View drawerView, float slideOffset) {
				super.onDrawerSlide(drawerView, slideOffset);
				mDrawerLayout.bringChildToFront(drawerView);
				mDrawerLayout.requestLayout();
			}

		};
		mDrawerLayout.setDrawerListener(mDrawerToggle);
		// init the class for sending Log (Shared preferences are created)
		LogHelper.init(this);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	@Override
	protected void onStop() {
		super.onStop();
		LogHelper.sendStopLog(this);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// every new start a new session Id is generated
		LogHelper.deleteSessionId(this);
		LogHelper.sendStartLog(this);
		// check if still a quiz to be done
		QuizHelper.checkQuiz(this);
		

	}

	protected void signedIn() {
		try {
			SCAccessProvider provider = SCAccessProvider.getInstance(this);
			if (!provider.isLoggedIn(MainActivity.this)) {
				showLoginDialog(SCAccessProvider.getInstance(MainActivity.this));
			}
			initDataManagement();
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(MainActivity.this, getString(R.string.auth_failed),
					Toast.LENGTH_SHORT).show();
			finish();
		}

	}

	private void showLoginDialog(final SCAccessProvider accessprovider) {
		// dialogbox for registration
		DialogInterface.OnClickListener updateDialogClickListener;

		updateDialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					try {
						if (!SCAccessProvider.getInstance(MainActivity.this)
								.login(MainActivity.this, null)) {
							new TokenTask().execute();
						}
					} catch (Exception e) {
						e.printStackTrace();
						Toast.makeText(MainActivity.this,
								getString(R.string.auth_failed),
								Toast.LENGTH_SHORT).show();
						finish();
					}
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					MainActivity.this.finish();
					break;
				}
			}
		};
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setCancelable(false);
		builder.setMessage(getString(R.string.auth_required))
				.setPositiveButton(android.R.string.yes,
						updateDialogClickListener)
				.setNegativeButton(android.R.string.no,
						updateDialogClickListener).show();
	}

	private void initDataManagement() {
		try {

			initGlobalConstants();

			try {
				DTHelper.init(getApplicationContext());
				initData();

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, getString(R.string.auth_failed),
						Toast.LENGTH_SHORT).show();
				finish();
			}

		} catch (Exception e) {
			Toast.makeText(this, R.string.app_failure_init, Toast.LENGTH_LONG)
					.show();
			e.printStackTrace();
			return;
		}
	}

	private void initGlobalConstants() throws NameNotFoundException,
			NotFoundException {
		GlobalConfig.setAppUrl(this,
				getResources().getString(R.string.smartcampus_app_url));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SCAccessProvider.SC_AUTH_ACTIVITY_REQUEST_CODE) {
			if (resultCode == RESULT_OK) {
				String token = data.getExtras().getString(
						AccountManager.KEY_AUTHTOKEN);
				if (token == null) {
					Toast.makeText(this, R.string.app_failure_security,
							Toast.LENGTH_LONG).show();
					finish();
				} else {
					initDataManagement();
				}
			} else if (resultCode == RESULT_CANCELED
					&& requestCode == SCAccessProvider.SC_AUTH_ACTIVITY_REQUEST_CODE) {
				DTHelper.endAppFailure(this, R.string.app_failure_security);
			}
		}
	}

	private boolean initData() {
		try {
			// to start with the map.
			mFragmentManager
					.beginTransaction()
					.replace(R.id.content_frame, new CategoryFragment(),
							TAG_FRAGMENT_MAP).commit();
			new SCAsyncTask<Void, Void, BaseDTObject>(this,
					new LoadDataProcessor(this)).execute();
		} catch (Exception e1) {
			Toast.makeText(this, R.string.app_failure_init, Toast.LENGTH_LONG)
					.show();
			return false;
		}
		return true;
	}

	private class LoadDataProcessor extends
			AbstractAsyncTaskProcessor<Void, BaseDTObject> {

		private int syncRequired = 0;
		private FragmentActivity currentRootActivity = null;

		public LoadDataProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public BaseDTObject performAction(Void... params)
				throws SecurityException, Exception {

			Exception res = null;

			try {
				syncRequired = DTHelper.SYNC_REQUIRED;
			} catch (Exception e) {
				res = e;
			}

			if (res != null) {
				throw res;
			}
			return null;
		}

		@Override
		public void handleResult(BaseDTObject result) {
			if (syncRequired != DTHelper.SYNC_NOT_REQUIRED) {
				if (syncRequired == DTHelper.SYNC_REQUIRED_FIRST_TIME) {
					Toast.makeText(MainActivity.this,
							R.string.initial_data_load, Toast.LENGTH_LONG)
							.show();
				}
				setSupportProgressBarIndeterminateVisibility(true);
				isLoading = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							currentRootActivity = DTHelper
									.start(MainActivity.this);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (currentRootActivity != null) {
								currentRootActivity
										.runOnUiThread(new Runnable() {
											@Override
											public void run() {
												currentRootActivity
														.setProgressBarIndeterminateVisibility(false);
												if (MainActivity.this != null) {
													MainActivity.this
															.setSupportProgressBarIndeterminateVisibility(false);
												}
												isLoading = false;
											}
										});
							}
						}
					}
				}).start();
			} else {
				setSupportProgressBarIndeterminateVisibility(false);
				// DTHelper.activateAutoSync();
			}
		}
	}

	/**
	 * @param items
	 *            where to put elements
	 * @param ids
	 *            array of arrays made in xml
	 */
	private ArrayList<NavDrawerItem> getMenuItems(int... ids) {

		ArrayList<NavDrawerItem> menu_items = new ArrayList<NavDrawerItem>();
//		menu_items.add(NavMenuSection.create(0, "Eventi"));
		String[] labels = getResources().getStringArray(ids[0]);
		String[] abTitles = getResources().getStringArray(ids[2]);

		TypedArray drawIds = getResources().obtainTypedArray((ids[1]));
		for (int j = 0; j < labels.length; j++) {
			int imgd = drawIds.getResourceId(j, -1);
			menu_items.add(NavMenuItem.create(j + 1, labels[j], abTitles[j],
					((imgd != -1) ? imgd : null), true, false, this));
		}
		drawIds.recycle();
		return menu_items;
	}

	@Override
	protected NavDrawerActivityConfiguration getNavDrawerConfiguration() {

		NavDrawerActivityConfiguration navDrawerActivityConfiguration = new NavDrawerActivityConfiguration();
		navDrawerActivityConfiguration.setMainLayout(R.layout.activity_main);
		navDrawerActivityConfiguration.setDrawerLayoutId(R.id.drawer_layout);
		navDrawerActivityConfiguration.setLeftDrawerId(R.id.left_drawer);

		navDrawerActivityConfiguration
				.setDrawerShadow(R.drawable.drawer_shadow);
		navDrawerActivityConfiguration.setDrawerOpenDesc(R.string.drawer_open);
		navDrawerActivityConfiguration
				.setDrawerCloseDesc(R.string.drawer_close);

		ArrayList<NavDrawerItem> menu_items = getMenuItems(
				R.array.drawer_items_labels, R.array.drawer_items_icons,
				R.array.drawer_items_actionbar_titles);

		navDrawerActivityConfiguration.setMenuItems(menu_items);

		navDrawerActivityConfiguration.setBaseAdapter(new NavDrawerAdapter(
				this, R.layout.navdrawer_item, menu_items));

		navDrawerActivityConfiguration.setDrawerIcon(R.drawable.ic_drawer);

		return navDrawerActivityConfiguration;
	}

	@Override
	protected void onNavItemSelected(int id) {

		Object[] objects = getFragmentAndTag(id);
		// can't replace the current fragment with nothing or with one of the
		// same type
		if (objects != null) {
			FragmentTransaction ft = mFragmentManager.beginTransaction();
			ft.setCustomAnimations(R.anim.enter, R.anim.exit);
			ft.replace(R.id.content_frame, (Fragment) objects[0],
					objects[1].toString());
			// ft.addToBackStack(objects[1].toString());
			mFragmentManager.popBackStack(null,
					FragmentManager.POP_BACK_STACK_INCLUSIVE);
			ft.commit();
		}
	}

	private Object[] getFragmentAndTag(int pos_in_list) {
		Object[] out = new Object[2];
		String cat = null;
		Bundle args = new Bundle();
		Fragment elf = null;

		switch (pos_in_list) {
		case 1: // click on "Oggi" item
			args = new Bundle();
			elf = new CategoryFragment();
			args.putString(EventsListingFragment.ARG_QUERY_TODAY, "");
			elf.setArguments(args);
			out[0] = elf;
			out[1] = TAG_FRAGMENT_EVENT_LIST;
			break;
		case 2: // click on "I miei eventi" item
			args = new Bundle();
			elf = new MapFragment();
			args.putBoolean(SearchFragment.ARG_MY, true);
			elf.setArguments(args);
			out[0] = elf;
			out[1] = TAG_FRAGMENT_EVENT_LIST;
			break;
		case 3: // click on "I miei eventi" item
			args = new Bundle();
			elf = new EventsListingFragment();
			args.putBoolean(SearchFragment.ARG_MY, true);
			elf.setArguments(args);
			out[0] = elf;
			out[1] = TAG_FRAGMENT_EVENT_LIST;
			break;


		default:
			return null;
		}
		return out;
	}

	// to handle action bar menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("MENU", "start on Create Options Menu MAIN frag");
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.global_menu, menu);


		return true;
	}

	@Override
	public void onBackPressed() {

		Log.i("BACKPRESSED", "MainActivity --> OnBackPressed ");

		// See bug:
		// http://stackoverflow.com/questions/13418436/android-4-2-back-stack-behaviour-with-nested-fragments/14030872#14030872
		// If the fragment exists and has some back-stack entry
		FragmentManager fm = getSupportFragmentManager();
		Fragment currentFragment = fm.findFragmentById(R.id.content_frame);
		if (currentFragment != null
				&& currentFragment.getChildFragmentManager()
						.getBackStackEntryCount() > 0) {
			// Get the fragment fragment manager - and pop the backstack
			currentFragment.getChildFragmentManager().popBackStack();
		}
		// Else, nothing in the direct fragment back stack
		else {

			Log.i("BACKPRESSED", "MainActivity --> current fragment: "
					+ currentFragment.getTag() + "!");

			if (!this.TAG_FRAGMENT_MAP.equals(currentFragment.getTag()))
				this.setTitleWithDrawerTitle();

			if (this.TAG_FRAGMENT_EVENT_LIST.equals(currentFragment.getTag()))
				AnimateFirstDisplayListener.displayedImages.clear();

			super.onBackPressed();

		}
	}

	/*
	 * public void goHomeFragment( AbstractNavDrawerActivity activity) {
	 * activity.getSupportFragmentManager().beginTransaction()
	 * .replace(R.id.content_frame, new MainFragment(),
	 * HOME_FRAGMENT_TAG).commit(); activity.setTitleWithDrawerTitle(); }
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}

		return false;
	}

	private class TokenTask extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... params) {
			SCAccessProvider provider = SCAccessProvider
					.getInstance(MainActivity.this);
			try {
				return provider.readToken(MainActivity.this);
			} catch (AACException e) {
				Log.e(MainActivity.class.getName(), "" + e.getMessage());
				switch (e.getStatus()) {
				case HttpStatus.SC_UNAUTHORIZED:
					try {
						provider.logout(MainActivity.this);
					} catch (AACException e1) {
						e1.printStackTrace();
					}
				default:
					break;
				}
				return null;
			}
		}

		@Override
		protected void onPostExecute(String result) {
			if (result == null) {
				SCAccessProvider provider = SCAccessProvider
						.getInstance(MainActivity.this);
				try {
					provider.login(MainActivity.this, null);
					initDataManagement();

				} catch (AACException e) {
					Log.e(MainActivity.class.getName(), "" + e.getMessage());
				}
			} else
				initDataManagement();

		}

	}
}
