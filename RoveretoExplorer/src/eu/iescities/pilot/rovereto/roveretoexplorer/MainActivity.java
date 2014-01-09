package eu.iescities.pilot.rovereto.roveretoexplorer;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.content.res.Resources.NotFoundException;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import eu.trentorise.smartcampus.android.common.GlobalConfig;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.territoryservice.model.BaseDTObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.DrawerItemOld;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.HackActionBarToggle;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.EventsListingFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.info.InfoListingFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.poi.PoisListingFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.SearchFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.track.TrackListingFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.AbstractNavDrawerActivity;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.NavDrawerActivityConfiguration;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.NavDrawerAdapter;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.NavDrawerItem;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.NavMenuItem;
import eu.iescities.pilot.rovereto.roveretoexplorer.ui.navdrawer.NavMenuSection;

public class MainActivity extends AbstractNavDrawerActivity{

	public static final String TAG_FRAGMENT_MAP = "fragmap";
	public static final String TAG_FRAGMENT_POI_LIST = "fragpopi";
	public static final String TAG_FRAGMENT_EVENT_LIST = "fragewent";
	public static final String TAG_FRAGMENT_TRACK_LIST = "fragtrack";
	public static final String TAG_FRAGMENT_INFO_LIST = "fraginfo";

	private FragmentManager mFragmentManager;

	private boolean isLoading;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//Log.i("AB TITLE", "MainActivity start on create!!!");
		mFragmentManager = getSupportFragmentManager();
		initDataManagement(savedInstanceState);

	}


	private void initDataManagement(Bundle savedInstanceState) {
		try {
			initGlobalConstants();

			try {
				//				if (!SCAccessProvider.getInstance(this).login(this, null)) {
				DTHelper.init(getApplicationContext());
				initData(); 
				//				}

			} catch (Exception e) {
				e.printStackTrace();
				Toast.makeText(this, getString(R.string.auth_failed), Toast.LENGTH_SHORT).show();
				finish();
			}

		} catch (Exception e) {
			Toast.makeText(this, R.string.app_failure_init, Toast.LENGTH_LONG).show();
			e.printStackTrace();
			return;
		}
	}


	private void initGlobalConstants() throws NameNotFoundException, NotFoundException {
		GlobalConfig.setAppUrl(this, getResources().getString(R.string.smartcampus_app_url));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	private boolean initData() {
		try {
			// to start with the map.
			mFragmentManager.beginTransaction().replace(R.id.content_frame, new MapFragment(), TAG_FRAGMENT_MAP).commit();
			new SCAsyncTask<Void, Void, BaseDTObject>(this, new LoadDataProcessor(this)).execute();
		} catch (Exception e1) {
			Toast.makeText(this, R.string.app_failure_init, Toast.LENGTH_LONG).show();
			return false;
		}
		return true;
	}




	private class LoadDataProcessor extends AbstractAsyncTaskProcessor<Void, BaseDTObject> {

		private int syncRequired = 0;
		private FragmentActivity currentRootActivity = null;

		public LoadDataProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public BaseDTObject performAction(Void... params) throws SecurityException, Exception {

			Exception res = null;

			try {
				syncRequired = DTHelper.SYNC_REQUIRED;//DTHelper.syncRequired();
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
					Toast.makeText(MainActivity.this, R.string.initial_data_load, Toast.LENGTH_LONG).show();
				}
				setSupportProgressBarIndeterminateVisibility(true);
				isLoading = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							currentRootActivity = DTHelper.start(MainActivity.this);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (currentRootActivity != null) {
								currentRootActivity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										currentRootActivity.setProgressBarIndeterminateVisibility(false);
										if (MainActivity.this != null) {
											MainActivity.this.setSupportProgressBarIndeterminateVisibility(false);
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
				//				DTHelper.activateAutoSync();
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
		menu_items.add(NavMenuSection.create(0, "Eventi"));
		String[] labels = getResources().getStringArray(ids[0]);
		String[] abTitles = getResources().getStringArray(ids[2]);

		TypedArray drawIds = getResources().obtainTypedArray((ids[1]));
		for (int j = 0; j < labels.length; j++) {
			int imgd = drawIds.getResourceId(j, -1);
			menu_items.add(NavMenuItem.create(j+1,
					labels[j], abTitles[j],
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

		ArrayList<NavDrawerItem> menu_items =  getMenuItems(R.array.drawer_items_labels,
				R.array.drawer_items_icons, R.array.drawer_items_actionbar_titles);

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
			ft.addToBackStack(objects[1].toString());

			ft.commit();
		}
	}


	private Object[] getFragmentAndTag(int pos_in_list) {
		Object[] out = new Object[2];
		String cat = null;
		Bundle args = new Bundle();
		EventsListingFragment elf = null;

		switch (pos_in_list) {
		case 1: // click on "I miei eventi" item 
			cat = CategoryHelper.CAT_EVENT_ESTATE_GIOVANI_E_FAMIGLIA;
			args = new Bundle();
			elf = new EventsListingFragment();
			args.putString(SearchFragment.ARG_CATEGORY, cat);
			elf.setArguments(args);
			out[0] = elf;
			out[1] = TAG_FRAGMENT_EVENT_LIST;
			break;
		case 2: // click on "Oggi" item 
			cat = CategoryHelper.CAT_EVENT_ESTATE_GIOVANI_E_FAMIGLIA;
			args = new Bundle();
			elf = new EventsListingFragment();
			args.putString(SearchFragment.ARG_CATEGORY, cat);
			elf.setArguments(args);
			out[0] = elf;
			out[1] = TAG_FRAGMENT_EVENT_LIST;
			break;
		case 3: // click on "Cultura" item 
			cat = CategoryHelper.CAT_EVENT_ESTATE_GIOVANI_E_FAMIGLIA;
			args = new Bundle();
			elf = new EventsListingFragment();
			args.putString(SearchFragment.ARG_CATEGORY, cat);
			elf.setArguments(args);
			out[0] = elf;
			out[1] = TAG_FRAGMENT_EVENT_LIST;
			break;
		case 4: // click on "Sport" item 
			cat = CategoryHelper.CAT_EVENT_ESTATE_GIOVANI_E_FAMIGLIA;
			args = new Bundle();
			elf = new EventsListingFragment();
			args.putString(SearchFragment.ARG_CATEGORY, cat);
			elf.setArguments(args);
			out[0] = elf;
			out[1] = TAG_FRAGMENT_EVENT_LIST;
			break;
		case 5: // click on "Svago" item 
			cat = CategoryHelper.CAT_EVENT_ESTATE_GIOVANI_E_FAMIGLIA;
			args = new Bundle();
			elf = new EventsListingFragment();
			args.putString(SearchFragment.ARG_CATEGORY, cat);
			elf.setArguments(args);
			out[0] = elf;
			out[1] = TAG_FRAGMENT_EVENT_LIST;
			break;
		case 6: // click on "Altri eventi" item 
			cat = CategoryHelper.CAT_EVENT_ESTATE_GIOVANI_E_FAMIGLIA;
			args = new Bundle();
			elf = new EventsListingFragment();
			args.putString(SearchFragment.ARG_CATEGORY, cat);
			elf.setArguments(args);
			out[0] = elf;
			out[1] = TAG_FRAGMENT_EVENT_LIST;
			break;
		}
		return out;
	}  


	//to handle action bar menu
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.i("MENU", "start on Create Options Menu MAIN frag");
		MenuInflater inflater = getMenuInflater();

		inflater.inflate(R.menu.global_menu, menu);

		//if (listmenu) {
		//Log.i("MENU", "ITEM 0" + menu.getItem(0).toString());
		//menu.getItem(0).setVisible(false);
		//} 

		//else {
		//Log.i("MENU", "ITEM 1" + menu.getItem(1).toString());
		//menu.getItem(1).setVisible(false);
		//}
		//super.onCreateOptionsMenu(menu, inflater);

		return true;
	} 



	@Override
	public void onBackPressed() {


		// See bug: http://stackoverflow.com/questions/13418436/android-4-2-back-stack-behaviour-with-nested-fragments/14030872#14030872
		// If the fragment exists and has some back-stack entry
		FragmentManager fm = getSupportFragmentManager();
		Fragment currentFragment = fm.findFragmentById(R.id.content_frame);
		if (currentFragment != null && currentFragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
			// Get the fragment fragment manager - and pop the backstack
			currentFragment.getChildFragmentManager().popBackStack();
		}
		// Else, nothing in the direct fragment back stack
		else {

			Log.i("AB TITLE", "current fragment: " + currentFragment.getTag() +"!");

			if ( !this.TAG_FRAGMENT_MAP.equals(currentFragment.getTag())) 
				this.setTitleWithDrawerTitle();

			super.onBackPressed();


		}
	}

		/*public void goHomeFragment( AbstractNavDrawerActivity activity) {
        activity.getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new MainFragment(), HOME_FRAGMENT_TAG).commit();
        activity.setTitleWithDrawerTitle();
    }*/





	}
