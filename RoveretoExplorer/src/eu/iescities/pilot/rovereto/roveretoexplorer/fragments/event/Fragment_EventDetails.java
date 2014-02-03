package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event;

import java.util.ArrayList;
import java.util.Locale;

import android.app.ActionBar;
import android.app.Activity;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.maps.GeoPoint;

import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CommentsHandler;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.PagerSlidingTabStrip;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.community.Fragment_EvDetail_Community;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere.Fragment_EvDetail_DaSapere;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.Fragment_EvDetail_Info;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.multimedia.Fragment_EvDetail_Multimedia;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapManager;

public class Fragment_EventDetails extends Fragment {

	public static final String ARG_EVENT_ID = "event_id";

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	// private int currentColor = R.color.jungle_green;
	private int currentColor = 0xFF96AA39;

	public ExplorerObject mEvent = null;
	private String mEventId;

	private Fragment mFragment = this;

	private CommentsHandler commentsHandler = null;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			Log.d("SCROLLTABS", "onCreate FIRST TIME");
			setHasOptionsMenu(true);

			if (getArguments() != null) {
				mEventId = getArguments().getString(ARG_EVENT_ID);
				mEvent = DTHelper.findEventById(mEventId);
			}
		} else {
			Log.d("SCROLLTABS", "onCreate SUBSEQUENT TIME");
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("SCROLLTABS", "onCreateView");

		return inflater.inflate(R.layout.frag_ev_detail_scrolltabs, container, false);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onActivityCreated");

		// getActivity().getActionBar().setTitle(mEvent.getTitle());

	}

	@Override
	public void onStart() {
		super.onStart();

		Log.d("FRAGMENT LC", "Fragment_evDetail --> onStart");

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onResume");
		// Set up the action bar.
		final ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle(mEvent.getTitle());
		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		actionBar.setHomeButtonEnabled(false);

		// getFragmentManager().addOnBackStackChangedListener(getListener());

		tabs = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
		pager = (ViewPager) getActivity().findViewById(R.id.pager);

		adapter = new MyPagerAdapter(getActivity().getSupportFragmentManager());

		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);

		tabs.setIndicatorColor(currentColor);

	}

	private ExplorerObject getEvent() {
		if (mEventId == null) {
			mEventId = getArguments().getString(Fragment_EventDetails.ARG_EVENT_ID);
		}

		if (mEvent == null) {
			mEvent = DTHelper.findEventById(mEventId);
		}

		return mEvent;
	}

	// private void refreshPage(int i) {
	// Fragment fragment = mFragments.get(i);
	//
	// switch (i) {
	// case 0:
	// ((TestFragment1) fragment).refreshView();
	// break;
	// case 1:
	// ((TestFragment2) fragment).refreshView();
	// break;
	// case 2:
	// ((TestFragment3) fragment).refreshView();
	// break;
	// }
	// }

	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onPause");

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onSaveInstanceState");

	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onStop");

	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onDestroyView");

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onDestroy");

	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onDetach");

	}

	public class MyPagerAdapter extends FragmentStatePagerAdapter {

		private final String[] TITLES = { "Info", "Da Sapere", "Multimedia", "Comunita" };

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			Fragment fragment = null;
			// Log.d("SCROLLTABS", "get Item is called "+i);
			if (position == 0) {
				fragment = Fragment_EvDetail_Info.newInstance(mEventId);
			}
			if (position == 1) {
				fragment = Fragment_EvDetail_DaSapere.newInstance(mEventId);
			}
			if (position == 2) {
				fragment = Fragment_EvDetail_Multimedia.newInstance(mEventId);
			}
			if (position == 3) {
				fragment = Fragment_EvDetail_Community.newInstance(mEventId);
			}

			return fragment;
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.map_view) {
			ArrayList<BaseDTObject> list = new ArrayList<BaseDTObject>();
			getEvent().setLocation(mEvent.getLocation());
			list.add(getEvent());
			MapManager.switchToMapView(list, mFragment);
			return true;
		} else if (item.getItemId() == R.id.direction_action) {
			callBringMeThere();

			return true;
		}
		return true;
	}

	protected void callBringMeThere() {
		Address to = new Address(Locale.getDefault());
		to.setLatitude(mEvent.getLocation()[0]);
		to.setLongitude(mEvent.getLocation()[1]);
		Address from = null;
		GeoPoint mylocation = MapManager.requestMyLocation(getActivity());
		if (mylocation != null) {
			from = new Address(Locale.getDefault());
			from.setLatitude(mylocation.getLatitudeE6() / 1E6);
			from.setLongitude(mylocation.getLongitudeE6() / 1E6);
		}
		DTHelper.bringmethere(getActivity(), from, to);

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		Log.i("MENU", "start on Prepare Options Menu EVENT LISTING frag: " + menu.toString());

		// menu.clear();

		getActivity().getMenuInflater().inflate(R.menu.event_detail_menu, menu);
		if (getEvent() == null || getEvent().getLocation() == null
				|| (getEvent().getLocation()[0] == 0 && getEvent().getLocation()[1] == 0)) {
			menu.findItem(R.id.map_view).setVisible(false);
			menu.findItem(R.id.direction_action).setVisible(false);
		}
		/*
		 * if (category == null) { category = (getArguments() != null) ?
		 * getArguments().getString(SearchFragment.ARG_CATEGORY) : null; }
		 */
		super.onPrepareOptionsMenu(menu);
	}

}
