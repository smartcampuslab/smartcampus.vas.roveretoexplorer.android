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
import android.view.LayoutInflater;
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

public class MainActivityOld extends ActionBarActivity{

	public static final String TAG_FRAGMENT_MAP = "fragmap";
	public static final String TAG_FRAGMENT_POI_LIST = "fragpopi";
	public static final String TAG_FRAGMENT_EVENT_LIST = "fragewent";
	public static final String TAG_FRAGMENT_TRACK_LIST = "fragtrack";
	public static final String TAG_FRAGMENT_INFO_LIST = "fraginfo";

	private FragmentManager mFragmentManager;
	private DrawerLayout mDrawerLayout;
	private ListView mListView;
	private ActionBarDrawerToggle mDrawerToggle;
	private String[] navMenuTitles;
	private boolean isLoading;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);


		setContentView(R.layout.activity_main);

		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		getSupportActionBar().setHomeButtonEnabled(true);

		setupProperties();
		initDataManagement(savedInstanceState);

	}
	
	
	//it is not referenced anywhere
	/*public void navDrawerOutItemClick(View v) {
		if (v.getId() == R.id.tv_header) {
			if (! (mFragmentManager.findFragmentById(R.id.frame_content) instanceof MapFragment))
				onChildClick(null, null, -1, -1, -1);
			else
				mDrawerLayout.closeDrawers();
		}
	} */
	
	

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

	private void setupProperties() {

		mFragmentManager = getSupportFragmentManager();
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		// this is a class created to avoid an Android bug
		// see the class for further infos.
		mDrawerToggle = new HackActionBarToggle(this, mDrawerLayout, R.drawable.ic_drawer, R.string.app_name,
				R.string.app_name);

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mListView = (ListView) findViewById(R.id.left_drawer);

		navMenuTitles = getResources().getStringArray(R.array.fragments_label_array);

		//Add header title
		//LayoutInflater inflater = getLayoutInflater();
		//ViewGroup header_news = (ViewGroup)inflater.inflate(R.layout.drawer_header_title, mListView, false);
		//mListView.addHeaderView(header_news, null, false);
		
		NavDrawerAdapter nda = buildAdapter();
		mListView.setAdapter(nda);

		//mListView.setOnChildClickListener(this);
	}

	
	private NavDrawerAdapter buildAdapter() {
		//ArrayList<DrawerItem> items = new ArrayList<DrawerItem>();

		// getting items from the xml
		// see the method for further infos
//		ArrayList<DrawerItemOld> items =  populateFromXml(R.array.drawer_items_labels,
//				R.array.drawer_items_icons);
		ArrayList<NavDrawerItem> items =  populateFromXml(R.array.drawer_items_labels,
				R.array.drawer_items_icons);

		return new NavDrawerAdapter(this, R.layout.navdrawer_item, items);
	}



	/**
	 * @param items
	 *            where to put elements
	 * @param ids
	 *            array of arrays made in xml
	 */
	private ArrayList<NavDrawerItem> populateFromXml(int... ids) {

		ArrayList<NavDrawerItem> items = new ArrayList<NavDrawerItem>();
		String[] labels = getResources().getStringArray(ids[0]);
		TypedArray drawIds = getResources().obtainTypedArray((ids[1]));
		for (int j = 0; j < labels.length; j++) {
			int imgd = drawIds.getResourceId(j, -1);
			items.add(NavMenuItem.create(j,
					labels[j],
					((imgd != -1) ? imgd : null), false, false, this));
		}
		drawIds.recycle();
		return items;
	}


	
	/**
	 * @param items
	 *            where to put elements
	 * @param ids
	 *            array of arrays made in xml
	 */
	private ArrayList<DrawerItemOld> populateFromXml2(int... ids) {

		ArrayList<DrawerItemOld> items = new ArrayList<DrawerItemOld>();

		String[] labels = getResources().getStringArray(ids[0]);
		TypedArray drawIds = getResources().obtainTypedArray((ids[1]));
		for (int j = 0; j < labels.length; j++) {
			int imgd = drawIds.getResourceId(j, -1);
			items.add(new DrawerItemOld(
					labels[j],
					((imgd != -1) ? getResources().getDrawable(imgd) : null)));
		}
		drawIds.recycle();
		return items;
	}



	@Override
	protected void onDestroy() {
		DTHelper.destroy();
		super.onDestroy();
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
	
		return false;
	}


	@Override
	protected void onPause() {
		if (DTHelper.getLocationHelper() != null)
			DTHelper.getLocationHelper().stop();
		super.onPause();
	}


	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}


	@Override
	protected void onResume() {
		if (DTHelper.getLocationHelper() != null)
			DTHelper.getLocationHelper().start();
		super.onResume();
	}



	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		mDrawerToggle.onConfigurationChanged(newConfig);
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
					Toast.makeText(MainActivityOld.this, R.string.initial_data_load, Toast.LENGTH_LONG).show();
				}
				setSupportProgressBarIndeterminateVisibility(true);
				isLoading = true;
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							currentRootActivity = DTHelper.start(MainActivityOld.this);
						} catch (Exception e) {
							e.printStackTrace();
						} finally {
							if (currentRootActivity != null) {
								currentRootActivity.runOnUiThread(new Runnable() {
									@Override
									public void run() {
										currentRootActivity.setProgressBarIndeterminateVisibility(false);
										if (MainActivityOld.this != null) {
											MainActivityOld.this.setSupportProgressBarIndeterminateVisibility(false);
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

	

}
