package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event;

import java.util.List;

import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CommentsHandler;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.PagerSlidingTabStrip;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.community.Fragment_EvDetail_Community;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere.Fragment_EvDetail_DaSapere;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.Fragment_EvDetail_Info;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.multimedia.Fragment_EvDetail_Multimedia;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.EventObject;

public class Fragment_EventDetails extends Fragment {
	
	public static final String ARG_EVENT_ID = "event_id";

	private PagerSlidingTabStrip tabs;
	private ViewPager pager;
	private MyPagerAdapter adapter;
	private int currentColor = 0xFF96AA39;
	
	public EventObject mEvent = null;
	private String mEventId;

	private Fragment mFragment = this;

	private CommentsHandler commentsHandler = null;
	
	
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        Log.d("SCROLLTABS","onAttach");

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(savedInstanceState==null)
        {
            Log.d("SCROLLTABS","onCreate FIRST TIME");
        	setHasOptionsMenu(true);

    		if (getArguments() != null) {
    			mEventId = getArguments().getString(ARG_EVENT_ID);
    			//now it will be always null so I load the fake data
    			//mEvent = DTHelper.findEventById(mEventId);
    			List<EventObject> eventList = Utils.getFakeEventObjects();
    			mEvent = Utils.getFakeLocalEventObject(eventList,mEventId);
    		}

        }
        else
        {
            Log.d("SCROLLTABS","onCreate SUBSEQUENT TIME");
        }
     
    }
    

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater,container,savedInstanceState);
        Log.d("SCROLLTABS","onCreateView");
        
		return inflater.inflate(R.layout.frag_ev_detail_scrolltabs, container, false);
    
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.d("SCROLLTABS","onActivityCreated");
        
        getActivity().getActionBar().setTitle(mEvent.getTitle()); 

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

    @Override
    public void onStart() {
        super.onStart();
        Log.d("SCROLLTABS","onStart");

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d("SCROLLTABS","onResume");

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d("SCROLLTABS","onPause");

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("SCROLLTABS","onSaveInstanceState");

    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d("SCROLLTABS","onStop");

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d("SCROLLTABS","onDestroyView");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("SCROLLTABS","onDestroy");

    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d("SCROLLTABS","onDetach");

    }
    
    
    public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "Info", "Da Sapere", "Multimedia", "Comunita"};

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
		      Fragment fragment=null;
//	        Log.d("SCROLLTABS", "get Item is called "+i);
	        if(position==0)
	        {
	            fragment= Fragment_EvDetail_Info.newInstance(mEventId);
	        }
	        if(position==1)
	        {
	            fragment=new Fragment_EvDetail_DaSapere();
	        }
	        if(position==2)
	        {
	            fragment=new Fragment_EvDetail_Multimedia();
	        }
	        if(position==3)
	        {
	            fragment=new Fragment_EvDetail_Community();
	        }
	        
	        return fragment;
		}

	}

    
    @Override
	public void onPrepareOptionsMenu(Menu menu) {
		
		Log.i("MENU", "start on Prepare Options Menu EVENT LISTING frag: " + menu.toString());
		
		//menu.clear();
		
		getActivity().getMenuInflater().inflate(R.menu.event_detail_menu, menu);

		/*if (category == null) {
			category = (getArguments() != null) ? getArguments().getString(SearchFragment.ARG_CATEGORY) : null;
		}
*/
		super.onPrepareOptionsMenu(menu);
	}    
    
    
    
    
    
    
    
    
    
    
    
    
    
}
