/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.astuetz.PagerSlidingTabStrip;


import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView.OnChildClickListener;
import eu.trentorise.smartcampus.android.common.SCAsyncTask.SCAsyncTaskProcessor;
import eu.trentorise.smartcampus.android.common.listing.AbstractLstingFragment;
import eu.trentorise.smartcampus.android.common.tagging.SemanticSuggestion;
import eu.trentorise.smartcampus.android.common.tagging.TaggingDialog.TagProvider;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
import eu.trentorise.smartcampus.territoryservice.model.BaseDTObject;
import eu.trentorise.smartcampus.territoryservice.model.CommunityData;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CategoryHelper.CategoryDescriptor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.ViewHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.EventObjectForBean;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.LocalEventObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.SearchFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.WhenForSearch;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.WhereForSearch;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapManager;

// to be used for event listing both in categories and in My Events
public class EventsListingFragment extends Fragment  {
	private ListView list;
	private Context context;

	public static final String ARG_CATEGORY = "event_category";
	public static final String ARG_POI = "event_poiId";
	public static final String ARG_POI_NAME = "event_poi_title";
	public static final String ARG_QUERY = "event_query";
	public static final String ARG_QUERY_TODAY = "event_query_today";
	public static final String ARG_MY = "event_my";
	public static final String ARG_CATEGORY_SEARCH = "category_search";
	public static final String ARG_MY_EVENTS_SEARCH = "my_events_search";
	public static final String ARG_LIST = "event_list";
	public static final String ARG_ID = "id_event";
	public static final String ARG_INDEX = "index_adapter";

	private String category;
	private EventAdapter eventsAdapter;
	private boolean mFollowByIntent;
	private String idEvent = "";
	private Integer indexAdapter;
	private Boolean reload = false;
	private Integer postitionSelected = -1;
	private boolean postProcAndHeader = true;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	//For the expandable list view 
	List<String> dateGroupList;
	private List<LocalEventObject> listEvents = new ArrayList<LocalEventObject>();
	Map<String, List<LocalEventObject>> eventCollection;
	ExpandableListView expListView;



	

	@Override
	public void onResume() {
		super.onResume();
		if (!idEvent.equals("")) {
			// get info of the event
			LocalEventObject event = DTHelper.findEventById(idEvent);
			// notify
			eventsAdapter.notifyDataSetChanged();
			idEvent = "";
			indexAdapter = 0;
		}

	}

	//private void restoreElement(EventAdapter eventsAdapter2, Integer indexAdapter2, LocalEventObject event) {
	//	removeEvent(eventsAdapter, indexAdapter);
	//	insertEvent(event, indexAdapter);

//	}

	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putString(ARG_ID, idEvent);
		if (indexAdapter != null)
			outState.putInt(ARG_INDEX, indexAdapter);

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this.getActivity();
		setHasOptionsMenu(true);
		setFollowByIntent();
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.eventslist, container, false);
	}

	@Override
	public void onActivityCreated(Bundle arg0) {
		super.onActivityCreated(arg0);
		//list = (ListView) getActivity().findViewById(R.id.events_list);
		
		
		dateGroupList =  Utils.createFakeDateGroupList();
		eventCollection = Utils.createFakeEventCollection((List<String>)dateGroupList);
		
		expListView = (ExpandableListView) getActivity().findViewById(R.id.events_list);
				
		if (arg0 != null) {
			// Restore last state for checked position.
			idEvent = arg0.getString(ARG_ID);
			indexAdapter = arg0.getInt(ARG_INDEX);

		}

		postProcAndHeader = false;
		/* create the adapter if it is the first time you load */
		if (eventsAdapter == null) {
			//eventsAdapter = new EventAdapter(context, R.layout.events_row, postProcAndHeader);
			eventsAdapter = new EventAdapter(context, R.layout.events_row, dateGroupList, eventCollection);
		
		}
		
		expListView.setAdapter(eventsAdapter);
		
		
		expListView.setOnChildClickListener(new OnChildClickListener() {
			 
            @Override
			public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {

                Log.i("LISTENER", "I should toast 1 ");

            	final LocalEventObject selected = (LocalEventObject) eventsAdapter.getChild(
                        groupPosition, childPosition);
                
                
                Log.i("SCROLLTABS", "Load the scroll tabs!!");
                //Toast.makeText(context, selected.getTitle(), Toast.LENGTH_LONG).show();
                
               
                FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
    			Fragment_EventDetails fragment = new Fragment_EventDetails();

    			Bundle args = new Bundle();
    			
                Log.i("SCROLLTABS", "event selected ID: " + ((EventPlaceholder) v.getTag()).event.getId() + "!!");

    			args.putString(Fragment_EventDetails.ARG_EVENT_ID, ((EventPlaceholder) v.getTag()).event.getId());
    			
    			
    			fragment.setArguments(args);

    			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
    			// fragmentTransaction.detach(this);
    			fragmentTransaction.replace(R.id.content_frame, fragment, "event_details");
    			fragmentTransaction.addToBackStack(fragment.getTag());
    			fragmentTransaction.commit();
                
             
                return true;
            }
        });
		
	}
	
	
	@Override
	public void onStart() {
		Bundle bundle = this.getArguments();

		if (reload) {
			//eventsAdapter = new EventAdapter(context, R.layout.events_row, postProcAndHeader);
			eventsAdapter = new EventAdapter(context, R.layout.events_row, dateGroupList, eventCollection);
			expListView.setAdapter(eventsAdapter);
			reload = false;
		}
		
		super.onStart();

	}
	
	private void setFollowByIntent() {
		try {
			ApplicationInfo ai = getActivity().getPackageManager().getApplicationInfo(getActivity().getPackageName(),
					PackageManager.GET_META_DATA);
			Bundle aBundle = ai.metaData;
			mFollowByIntent = aBundle.getBoolean("follow-by-intent");
		} catch (NameNotFoundException e) {
			mFollowByIntent = false;
			Log.e(EventsListingFragment.class.getName(), "you should set the follow-by-intent metadata in app manifest");
		}

	}

	private void setStoreEventId(View v, int position) {
		final LocalEventObject event = ((EventPlaceholder) v.getTag()).event;
		idEvent = event.getId();
		indexAdapter = position;
	}

	//@Override
	//public void onScrollStateChanged(AbsListView view, int scrollState) {
		//super.onScrollStateChanged(view, scrollState);
	//	if ((postitionSelected != -1) && (scrollState == SCROLL_STATE_TOUCH_SCROLL)) {
		//	hideListItemsMenu(view, true);

		//}
//	}

	
	
	private static class EventComparator implements Comparator<LocalEventObject> {
		public int compare(LocalEventObject c1, LocalEventObject c2) {
			if (c1.getFromTime() == c2.getFromTime())
				return 0;
			if (c1.getFromTime() < c2.getFromTime())
				return -1;
			if (c1.getFromTime() > c2.getFromTime())
				return 1;
			return 0;
		}
	}

	
	
/*	private List<LocalEventObject> getEventsOld(AbstractLstingFragment.ListingRequest... params) {
		try {
			Collection<LocalEventObject> result = null;

			Bundle bundle = getArguments();
			boolean my = false;

			if (bundle == null) {
				return Collections.emptyList();
			}
			if (bundle.getBoolean(SearchFragment.ARG_MY))
				my = true;
			String categories = bundle.getString(SearchFragment.ARG_CATEGORY);
			SortedMap<String, Integer> sort = new TreeMap<String, Integer>();
			sort.put("fromTime", 1);
			if (bundle.containsKey(SearchFragment.ARG_CATEGORY)
					&& (bundle.getString(SearchFragment.ARG_CATEGORY) != null)) {

				result = Utils.convertToLocalEventFromBean(DTHelper.searchInGeneral(params[0].position, params[0].size,
						bundle.getString(SearchFragment.ARG_QUERY),
						(WhereForSearch) bundle.getParcelable(SearchFragment.ARG_WHERE_SEARCH),
						(WhenForSearch) bundle.getParcelable(SearchFragment.ARG_WHEN_SEARCH), my,
						EventObjectForBean.class, sort, categories));

			} else if (bundle.containsKey(ARG_POI) && (bundle.getString(ARG_POI) != null)) {
				result = Utils.convertToLocalEvent(DTHelper.getEventsByPOI(params[0].position, params[0].size,
						bundle.getString(ARG_POI)));
			} else if (bundle.containsKey(SearchFragment.ARG_MY) && (bundle.getBoolean(SearchFragment.ARG_MY))) {

				result = Utils.convertToLocalEventFromBean(DTHelper.searchInGeneral(params[0].position, params[0].size,
						bundle.getString(SearchFragment.ARG_QUERY),
						(WhereForSearch) bundle.getParcelable(SearchFragment.ARG_WHERE_SEARCH),
						(WhenForSearch) bundle.getParcelable(SearchFragment.ARG_WHEN_SEARCH), my,
						EventObjectForBean.class, sort, categories));

			} else if (bundle.containsKey(SearchFragment.ARG_QUERY)) {

				result = Utils.convertToLocalEventFromBean(DTHelper.searchInGeneral(params[0].position, params[0].size,
						bundle.getString(SearchFragment.ARG_QUERY),
						(WhereForSearch) bundle.getParcelable(SearchFragment.ARG_WHERE_SEARCH),
						(WhenForSearch) bundle.getParcelable(SearchFragment.ARG_WHEN_SEARCH), my,
						EventObjectForBean.class, sort, categories));

			} else if (bundle.containsKey(ARG_QUERY_TODAY)) {
				result = DTHelper.searchTodayEvents(params[0].position, params[0].size,
						bundle.getString(SearchFragment.ARG_QUERY));
			} else if (bundle.containsKey(SearchFragment.ARG_LIST)) {
				result = (Collection<LocalEventObject>) bundle.get(SearchFragment.ARG_LIST);
			} else {
				return Collections.emptyList();
			}

			// conversion to LocalObject 
			listEvents.addAll(result);

			List<LocalEventObject> sorted = new ArrayList<LocalEventObject>(listEvents);
			if (!postProcAndHeader) {
				return sorted;
			} else {
				Calendar cal = Calendar.getInstance();
				cal.setTimeInMillis(System.currentTimeMillis());
				calToDate(cal);
				long biggerFromTime = cal.getTimeInMillis();
				if (sorted.size() > 0) {
					// listEvents.addAll(postProcForRecurrentEvents(sorted,
					// biggerFromTime));
					return postProcForRecurrentEvents(sorted, biggerFromTime, result.size() == 0
							|| result.size() < getSize());
				} else {
					return sorted;
				}
			}
		} catch (Exception e) {
			Log.e(EventsListingFragment.class.getName(), "" + e.getMessage());
			e.printStackTrace();
			listEvents = Collections.emptyList();
			return listEvents;
		}
	} */
	
	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		
		Log.i("MENU", "start on Prepare Options Menu EVENT LISTING frag: " + menu.toString());
		
		//menu.clear();
		
		getActivity().getMenuInflater().inflate(R.menu.list_menu, menu);

		if (category == null) {
			category = (getArguments() != null) ? getArguments().getString(SearchFragment.ARG_CATEGORY) : null;
		}

		super.onPrepareOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		if (item.getItemId() == R.id.map_view) {
			category = (getArguments() != null) ? getArguments().getString(SearchFragment.ARG_CATEGORY) : null;
			boolean query = getArguments().containsKey(SearchFragment.ARG_QUERY);

			if (category != null && !query) {
				Log.i("AB TITLE", "switchToMapView category:" + category);
				MapManager.switchToMapView(category, MapFragment.ARG_EVENT_CATEGORY, this);
			} else {
				ArrayList<BaseDTObject> target = new ArrayList<BaseDTObject>();
				if (list != null) {
					for (int i = 0; i < list.getAdapter().getCount(); i++) {
						BaseDTObject o = (BaseDTObject) list.getAdapter().getItem(i);
						if (o.getLocation() != null && o.getLocation()[0] != 0 && o.getLocation()[1] != 0) {
							target.add(o);
						}
					}
				}
				
				Log.i("AB TITLE", "switchToMapView BaseDTOObjects:" + target.toString());
				MapManager.switchToMapView(target, this);
			}
			return true;
		}

		else if (item.getItemId() == R.id.search_action) {
			FragmentTransaction fragmentTransaction;
			Fragment fragment;
			fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
			fragment = new SearchFragment();
			Bundle args = new Bundle();
			args.putString(SearchFragment.ARG_CATEGORY, category);
			args.putString(CategoryHelper.CATEGORY_TYPE_EVENTS, CategoryHelper.CATEGORY_TYPE_EVENTS);
			if (getArguments() != null && getArguments().containsKey(SearchFragment.ARG_MY)
					&& getArguments().getBoolean(SearchFragment.ARG_MY))
				args.putBoolean(SearchFragment.ARG_MY, getArguments().getBoolean(SearchFragment.ARG_MY));
			fragment.setArguments(args);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.replace(R.id.content_frame, fragment, "events");
			fragmentTransaction.addToBackStack(fragment.getTag());
			fragmentTransaction.commit();
			/* add category to bundle */
			return true;

		} else {
			return super.onOptionsItemSelected(item);
		}
	}

	
	
	

}
