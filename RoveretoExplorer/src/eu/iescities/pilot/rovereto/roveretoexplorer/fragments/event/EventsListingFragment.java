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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ListView;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.SearchFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.WhenForSearch;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.WhereForSearch;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapManager;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.android.common.SCAsyncTask.SCAsyncTaskProcessor;
import eu.trentorise.smartcampus.android.common.listing.AbstractLstingFragment;
import eu.trentorise.smartcampus.android.common.listing.AbstractLstingFragment.ListingRequest;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

// to be used for event listing both in categories and in My Events
public class EventsListingFragment extends Fragment implements OnScrollListener {
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
	public static final int DEFAULT_ELEMENTS_NUMBER = 10;

	private String category;
	private EventAdapter eventsAdapter;
	private boolean mFollowByIntent;
	private String idEvent = "";
	private Integer indexAdapter;
	private Boolean reload = false;
	private Integer postitionSelected = -1;
	private boolean postProcAndHeader = true;

	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");
	protected int lastSize = 0;
	protected int position = 0;
	protected int size = DEFAULT_ELEMENTS_NUMBER;

	// For the expandable list view
	List<String> dateGroupList;
	private List<ExplorerObject> listEvents = new ArrayList<ExplorerObject>();
	Map<String, List<ExplorerObject>> eventCollection;
	ExpandableListView expListView;

	@Override
	public void onResume() {
		super.onResume();
		if (!idEvent.equals("")) {
			// get info of the event
			ExplorerObject event = DTHelper.findEventById(idEvent);
			// notify
			eventsAdapter.notifyDataSetChanged();
			idEvent = "";
			indexAdapter = 0;
		}

	}

	// private void restoreElement(EventAdapter eventsAdapter2, Integer
	// indexAdapter2, LocalExplorerObject event) {
	// removeEvent(eventsAdapter, indexAdapter);
	// insertEvent(event, indexAdapter);

	// }

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
		// list = (ListView) getActivity().findViewById(R.id.events_list);

		list = (ListView) getActivity().findViewById(R.id.events_list);
		if (arg0 != null) {
			// Restore last state for checked position.
			idEvent = arg0.getString(ARG_ID);
			indexAdapter = arg0.getInt(ARG_INDEX);

		}
		/* create the adapter is it is the first time you load */
		dateGroupList = new ArrayList<String>();
		eventCollection = new LinkedHashMap<String, List<ExplorerObject>>();
		if (eventsAdapter == null) {
			eventsAdapter = new EventAdapter(context, R.layout.event_list_child_item, dateGroupList, eventCollection);

		}
		expListView = (ExpandableListView)
				getActivity().findViewById(R.id.events_list);
		// TO DO
		// dateGroupList = Utils.createFakeDateGroupList();
		// eventCollection = Utils.createFakeEventCollection((List<String>)
		// dateGroupList);
		//
		//		 expListView = (ExpandableListView)
		//		 getActivity().findViewById(R.id.events_list);
		//
		// if (arg0 != null) {
		// // Restore last state for checked position.
		// idEvent = arg0.getString(ARG_ID);
		// indexAdapter = arg0.getInt(ARG_INDEX);
		//
		// }
		//
		// postProcAndHeader = false;
		// /* create the adapter if it is the first time you load */
		// if (eventsAdapter == null) {
		// // eventsAdapter = new EventAdapter(context, R.layout.events_row,
		// // postProcAndHeader);
		// eventsAdapter = new EventAdapter(context, R.layout.events_row,
		// dateGroupList, eventCollection);
		//
		// }
		//
		// expListView.setAdapter(eventsAdapter);
		//
		setListenerOnEvent();

	}

	private void setListenerOnEvent() {
		expListView.setOnChildClickListener(new OnChildClickListener() {

			@Override
			public boolean onChildClick(ExpandableListView parent, View v, int
					groupPosition, int childPosition, long id) {

				Log.i("LISTENER", "I should toast 1 ");

				final ExplorerObject selected = (ExplorerObject)
						eventsAdapter.getChild(groupPosition, childPosition);

				Log.i("SCROLLTABS", "Load the scroll tabs!!");
				// Toast.makeText(context, selected.getTitle(),
				// Toast.LENGTH_LONG).show();

				FragmentTransaction fragmentTransaction =
						getActivity().getSupportFragmentManager().beginTransaction();
				Fragment_EventDetails fragment = new Fragment_EventDetails();

				Bundle args = new Bundle();

				Log.i("SCROLLTABS", "event selected ID: " + ((EventPlaceholder)
						v.getTag()).event.getId() + "!!");

				args.putString(Fragment_EventDetails.ARG_EVENT_ID,
						((EventPlaceholder) v.getTag()).event.getId());

				fragment.setArguments(args);

				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
				// fragmentTransaction.detach(this);
				fragmentTransaction.replace(R.id.content_frame, fragment,
						"event_details");
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
			// eventsAdapter = new EventAdapter(context, R.layout.events_row,
			// postProcAndHeader);
			eventsAdapter = new EventAdapter(context, R.layout.event_list_child_item, dateGroupList, eventCollection);
			expListView.setAdapter(eventsAdapter);
			setListenerOnEvent();
			reload = false;
		}
		initData();
		super.onStart();

	}

	protected void initData() {

		if (eventsAdapter != null && eventsAdapter.getGroupCount() == 0) {
			position = 0;
			lastSize = 0;

			if (loadOnStart())
				load();
			// TO DO

			//			eventsAdapter = new EventAdapter(context, R.layout.events_row, dateGroupList, eventCollection);
			//			expListView.setAdapter(eventsAdapter);

		}
		// TO DO
		// getListView().setOnScrollListener(this);

	}

	protected boolean loadOnStart() {
		return true;
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
		final ExplorerObject event = ((EventPlaceholder) v.getTag()).event;
		idEvent = event.getId();
		indexAdapter = position;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if ((postitionSelected != -1) && (scrollState == SCROLL_STATE_TOUCH_SCROLL)) {
			// hideListItemsMenu(view, true);

		}
	}

	private static class EventComparator implements Comparator<ExplorerObject> {
		public int compare(ExplorerObject c1, ExplorerObject c2) {
			if (c1.getFromTime() == c2.getFromTime())
				return 0;
			if (c1.getFromTime() < c2.getFromTime())
				return -1;
			if (c1.getFromTime() > c2.getFromTime())
				return 1;
			return 0;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		boolean loadMore = (firstVisibleItem + visibleItemCount >= totalItemCount) && // end
				// of
				// visible
				// list
				// reached
				(lastSize < eventsAdapter.getGroupCount()) && // last load has
				// been
				// successful
				eventsAdapter.getGroupCount() >= size;
				if (loadMore) {
					lastSize = eventsAdapter.getGroupCount();
					position += size;
					load();
				}
	}

	protected void load() {
		if (position == 0) {
			eventCollection.clear();
		}
		new SCListingFragmentTask<ListingRequest, Void>(getActivity(), getLoader()).execute(new ListingRequest(
				position, size));
	}

	protected SCAsyncTaskProcessor<AbstractLstingFragment.ListingRequest, List<ExplorerObject>> getLoader() {
		return new EventLoader(getActivity());
	}

	private class EventLoader extends
	AbstractAsyncTaskProcessor<AbstractLstingFragment.ListingRequest, List<ExplorerObject>> {

		private FragmentActivity currentRootActivity = null;

		public EventLoader(Activity activity) {
			super(activity);
		}

		@Override
		public List<ExplorerObject> performAction(AbstractLstingFragment.ListingRequest... params)
				throws SecurityException, Exception {
			return getEvents(params);
		}

		@Override
		public void handleResult(List<ExplorerObject> result) {
			// una volta ricevuti i dati li sistemo per data
			updateCollection(result);
			eventsAdapter = new EventAdapter(context, R.layout.event_list_child_item, dateGroupList, eventCollection);
			expListView.setAdapter(eventsAdapter);
			setListenerOnEvent();
		}

		private void updateCollection(List<ExplorerObject> result) {
			String date_with_day = null;
			for (ExplorerObject expObj : result) {
				if (!dateGroupList.contains(expObj.getFromTime().toString()))
				{
					Log.i("FORMAT", "EventsListingFragment --> date formatted: " + Utils.getDateTimeString(context, expObj.getFromTime(), Utils.DATE_FORMAT_2, true, true)[0] + "!!");

					date_with_day = Utils.getDateTimeString(context, expObj.getFromTime(), Utils.DATE_FORMAT_2, true, true)[0];
					dateGroupList.add(date_with_day);
					eventCollection.put(date_with_day, new ArrayList<ExplorerObject>() );
				}
				//aggiungi 
				eventCollection.get(date_with_day).add(expObj);
			}
		}
	}

	private List<ExplorerObject> getEvents(AbstractLstingFragment.ListingRequest... params) {
		try {
			Collection<ExplorerObject> result = null;

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

				result = DTHelper.searchInGeneral(params[0].position, params[0].size,
						bundle.getString(SearchFragment.ARG_QUERY),
						(WhereForSearch) bundle.getParcelable(SearchFragment.ARG_WHERE_SEARCH),
						(WhenForSearch) bundle.getParcelable(SearchFragment.ARG_WHEN_SEARCH), my, ExplorerObject.class,
						sort, categories);

			} else if (bundle.containsKey(ARG_POI) && (bundle.getString(ARG_POI) != null)) {
				result = DTHelper.getEventsByPOI(params[0].position, params[0].size, bundle.getString(ARG_POI));
			} else if (bundle.containsKey(SearchFragment.ARG_MY) && (bundle.getBoolean(SearchFragment.ARG_MY))) {

				result = DTHelper.searchInGeneral(params[0].position, params[0].size,
						bundle.getString(SearchFragment.ARG_QUERY),
						(WhereForSearch) bundle.getParcelable(SearchFragment.ARG_WHERE_SEARCH),
						(WhenForSearch) bundle.getParcelable(SearchFragment.ARG_WHEN_SEARCH), my, ExplorerObject.class,
						sort, categories);
//				result = tmpPostProc(result);

			} else if (bundle.containsKey(SearchFragment.ARG_QUERY)) {

				result = DTHelper.searchInGeneral(params[0].position, params[0].size,
						bundle.getString(SearchFragment.ARG_QUERY),
						(WhereForSearch) bundle.getParcelable(SearchFragment.ARG_WHERE_SEARCH),
						(WhenForSearch) bundle.getParcelable(SearchFragment.ARG_WHEN_SEARCH), my, ExplorerObject.class,
						sort, categories);

			} else if (bundle.containsKey(ARG_QUERY_TODAY)) {
				result = DTHelper.searchTodayEvents(params[0].position, params[0].size,
						bundle.getString(SearchFragment.ARG_QUERY));
			} else if (bundle.containsKey(SearchFragment.ARG_LIST)) {
				result = (Collection<ExplorerObject>) bundle.get(SearchFragment.ARG_LIST);
			} else {
				return Collections.emptyList();
			}

			listEvents.addAll(result);

			List<ExplorerObject> sorted = new ArrayList<ExplorerObject>(listEvents);

			// TO DO

			// if (sorted.size() > 0) {
			// return postProcForRecurrentEvents(sorted,
			// result.size() == 0 || result.size() < DEFAULT_ELEMENTS_NUMBER,
			// getArguments().containsKey(ARG_QUERY_TODAY));
			// } else
			return sorted;
		} catch (Exception e) {
			Log.e(EventsListingFragment.class.getName(), e.getMessage());
			e.printStackTrace();
			listEvents = Collections.emptyList();
			return listEvents;
		}
	}

	private Collection<ExplorerObject> tmpPostProc(Collection<ExplorerObject> result) {
		Collection<ExplorerObject> returnEvents = new ArrayList<ExplorerObject>();
		for (ExplorerObject event: result){
			if (!event.getCommunityData().getAttending().isEmpty())
				returnEvents.add(event);
		}
		return returnEvents;
	}

	private List<ExplorerObject> postProcForRecurrentEvents(List<ExplorerObject> result, boolean endReached,
			boolean todayOnly) {
		if (result.isEmpty())
			return result;

		// 0:00 of today
		Calendar today = Calendar.getInstance();
		today.setTimeInMillis(result.get(result.size() - 1).getFromTime());
		calToDate(today);

		// start time of the list visualization interval
		long startInterval = result.get(0).getFromTime();
		// if event start before today, the start is today
		if (startInterval < today.getTimeInMillis() || todayOnly) {
			startInterval = today.getTimeInMillis();
		}

		// end time of the list visualization interval, corresponds to the start
		// of the
		// latest event (assuming events ordered by fromTime) or to today if
		// todayOnly specified
		long endInterval = todayOnly ? today.getTimeInMillis() : result.get(result.size() - 1).getFromTime();

		List<ExplorerObject> returnList = new ArrayList<ExplorerObject>();
		EventComparator r = new EventComparator();
		// long biggerFromTime = today.getTimeInMillis();
		// if (biggerFromTime < lessFromTime )
		// biggerFromTime = lessFromTime;
		for (ExplorerObject event : result) {
			// normalize toTime field
			if (event.getToTime() == null || event.getToTime() == 0) {
				event.setToTime(event.getFromTime());
			}

			// if an event is a single day event, add it directly, otherwise
			// need to replicate
			if ((event.getToTime() != null) && (event.getFromTime() != null)
					&& (event.getToTime() != event.getFromTime())) {
				long eventFromTime = event.getFromTime();
				long eventToTime = event.getToTime();
				Calendar calFromTime = Calendar.getInstance();
				Calendar calToTime = Calendar.getInstance();
				calFromTime.setTime(new Date(eventFromTime));
				calToDate(calFromTime);
				calToTime.setTime(new Date(eventToTime));
				calToDate(calToTime);
				eventFromTime = calFromTime.getTimeInMillis();
				eventToTime = calToTime.getTimeInMillis();

				// if the times were different, but the date is the same, add it
				// directly, as above
				if (eventFromTime == eventToTime) {
					returnList.add(event);
				} else {
					// event takes more than one day, replicate it (until X)
					// starting from either fromTime of an event or from
					// interval start (when the event start in the past)
					eventFromTime = Math.max(eventFromTime, startInterval);
					// and ending on the interval end (if there are more events
					// to query) or on the event end
					eventToTime = endReached && !todayOnly ? eventToTime : endInterval;

					// replicate event within the interval
					Calendar caltmp = Calendar.getInstance();
					caltmp.setTimeInMillis(eventFromTime);
					while (caltmp.getTimeInMillis() <= eventToTime) {
						ExplorerObject newEvent = event.copy();
						newEvent.setFromTime(caltmp.getTimeInMillis());
						newEvent.setToTime(caltmp.getTimeInMillis());
						caltmp.add(Calendar.DATE, 1);
						returnList.add(newEvent);
					}
					/* calculate how much days use the events */
					/* create and entry for every day */
				}

			} else {
				/* put it in the returnList */
				returnList.add(event);
			}
		}
		Collections.sort(returnList, r);
		return returnList;

	}

	private void calToDate(Calendar cal) {
		cal.set(Calendar.HOUR_OF_DAY, 0);
		cal.set(Calendar.MINUTE, 0);
		cal.set(Calendar.SECOND, 0);
		cal.set(Calendar.MILLISECOND, 0);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		Log.i("MENU", "start on Prepare Options Menu EVENT LISTING frag: " + menu.toString());

		// menu.clear();

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

	protected class SCListingFragmentTask<Params, Progress> extends SCAsyncTask<Params, Progress, List<ExplorerObject>> {

		public SCListingFragmentTask(Activity activity,
				SCAsyncTask.SCAsyncTaskProcessor<Params, List<ExplorerObject>> processor) {
			super(activity, processor);
		}

		@Override
		protected void handleSuccess(List<ExplorerObject> result) {
			super.handleSuccess(result);
			// resetAdapter

			// if (result != null) {
			// for (ExplorerObject o: result) {
			// eventsAdapter..add(o);
			// }
			// }
			eventsAdapter.notifyDataSetChanged();
		}
	}
}
