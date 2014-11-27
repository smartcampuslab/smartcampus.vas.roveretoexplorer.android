package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event;

import java.util.Locale;

import android.app.Activity;
import android.location.Address;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.maps.GeoPoint;

import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.PagerSlidingTabStrip;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.community.Fragment_EvDetail_Community;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere.Fragment_EvDetail_DaSapere;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.Fragment_EvDetail_Info;
import eu.iescities.pilot.rovereto.roveretoexplorer.log.LogHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapManager;

public class Fragment_EventDetails extends Fragment {

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	public ExplorerObject mEvent = null;
	private String mEventId;
	private String mEventImageUrl;

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

			if (getArguments() != null) {
				mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
				mEvent = DTHelper.findEventById(mEventId);
				mEventImageUrl = getArguments().getString(Utils.ARG_EVENT_IMAGE_URL);
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
	}

	
	
	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onStart");


		// Set up the action bar.
		((ActionBarActivity) getActivity()).getSupportActionBar().setTitle(mEvent.getTitle());
		// Specify that the Home/Up button should not be enabled, since there is
		// no hierarchical
		// parent.
		tabs = (PagerSlidingTabStrip) getActivity().findViewById(R.id.tabs);
		pager = (ViewPager) getActivity().findViewById(R.id.pager);
		adapter = new MyPagerAdapter(getChildFragmentManager());
		pager.post(new Runnable() {
	        public void run() {
	        	pager.setAdapter(adapter);
	        }
	    });
		pager.setAdapter(adapter);

		final int pageMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources()
				.getDisplayMetrics());
		pager.setPageMargin(pageMargin);

		tabs.setViewPager(pager);
		tabs.setIndicatorColor(getResources().getColor(R.color.tabs_lines));
		tabs.setUnderlineColor(getResources().getColor(R.color.tabs_lines));
		tabs.setDividerColor(getResources().getColor(R.color.tabs_lines));
		LogHelper.sendEventViewed(mEventId,getActivity());

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC", "Fragment_evDetail --> onResume");
	}

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

	/* Pager Adapter */
	public class MyPagerAdapter extends FragmentStatePagerAdapter {
		private final String[] TITLES = { "Info", "Da Sapere", "Comunita" };
		
		
		private Fragment mPrimaryItem;

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
			
			if (getCount()<=3){
				tabs.setShouldExpand(true);
			}
			
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
			
			
			switch (position) {
			case 0:
				return Fragment_EvDetail_Info.newInstance(mEventId, mEventImageUrl);
			case 1:
				return Fragment_EvDetail_DaSapere.newInstance(mEventId);
			case 2:
				return Fragment_EvDetail_Community.newInstance(mEventId);
			default:
				return null;
			}
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position, Object object) {
			super.setPrimaryItem(container, position, object);
			mPrimaryItem = (Fragment) object;
		}

		public Fragment getPrimaryItem() {
			return mPrimaryItem;
		}

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
		 LogHelper.sendViaggiaRequest(getActivity());
	}

	private ExplorerObject getEvent() {
		if (mEventId == null) {
			mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
		}
		mEvent = DTHelper.findEventById(mEventId);
		return mEvent;
	}

}
