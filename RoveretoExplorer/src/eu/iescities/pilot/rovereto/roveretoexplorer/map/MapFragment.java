package eu.iescities.pilot.rovereto.roveretoexplorer.map;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.SortedMap;
import java.util.TreeMap;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.android.gms.maps.model.TileProvider;
import com.google.android.gms.maps.model.UrlTileProvider;

import eu.iescities.pilot.rovereto.roveretoexplorer.MainActivity;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CategoryHelper.CategoryDescriptor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.DTParamsHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.EventDetailsFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.EventsListingFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.Fragment_EventDetails;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.SearchFragment;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.WhenForSearch;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.search.WhereForSearch;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapFilterDialogFragment.REQUEST_TYPE;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;

public class MapFragment extends Fragment implements MapItemsHandler, OnCameraChangeListener, OnMarkerClickListener,
		MapObjectContainer {

	private static final String TAG_FRAGMENT_POI_SELECT = "poi_select";
	public static final String ARG_POI_CATEGORY = "poi category";
	public static final String ARG_EVENT_CATEGORY = "event category";
	public static final String ARG_OBJECTS = "objects";
	public static final String ARG_TRACK_CATEGORY = "track_category";
	protected GoogleMap mMap;

	private String[] poiCategories = null;
	private String[] eventsCategories = null;
	private String[] tracksCategories = null;
	private String[] eventsCleaned = null;
	private Collection<? extends BaseDTObject> objects;
	private String osmUrl = "http://otile1.mqcdn.com/tiles/1.0.0/osm/%d/%d/%d.jpg";

	private boolean loaded = false;
	private boolean listmenu = false;

	private static View view;
    float maxZoomOnMap = 19.0f;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.fragment_map, container, false);
		} catch (InflateException e) {
			/* map is already there, just return view as it is */
		}
		return view;
	}

	/*
	 * @Override public void onCreateOptionsMenu(Menu menu, MenuInflater
	 * inflater) { Log.i("MENU", "start on Create Option Menu MAP frag");
	 * inflater.inflate(R.menu.map_menu, menu); if (listmenu) { Log.i("MENU",
	 * "ITEM 0" + menu.getItem(0).toString());
	 * menu.getItem(0).setVisible(false); }
	 * 
	 * //else { //Log.i("MENU", "ITEM 1" + menu.getItem(1).toString());
	 * //menu.getItem(1).setVisible(false); //} super.onCreateOptionsMenu(menu,
	 * inflater); }
	 */

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Log.i("MENU", "start on Options Item Selected MAP frag");
		/*
		 * if (item.getItemId() == R.id.action_poi_places) { PoiSelectFragment
		 * psf = PoiSelectFragment.istantiate(this,
		 * R.array.map_items_places_labels, R.array.map_items_places_icons,
		 * REQUEST_TYPE.POI, new String[] {
		 * CategoryHelper.POI_CATEGORIES[0].category,
		 * CategoryHelper.POI_CATEGORIES[4].category });
		 * psf.show(getFragmentManager(), TAG_FRAGMENT_POI_SELECT); return true;
		 * }
		 *//*
			 * else if (item.getItemId() == R.id.action_poi_events) {
			 * PoiSelectFragment psf = PoiSelectFragment.istantiate(this,
			 * R.array.map_items_events_labels, R.array.map_items_events_icons,
			 * REQUEST_TYPE.EVENT, CategoryHelper.getEventCategories());
			 * psf.show(getFragmentManager(), TAG_FRAGMENT_POI_SELECT); return
			 * true; } else
			 */// if (item.getItemId() == R.id.action_poi) {
		if (item.getTitle() == "filtro") {
			MapFilterDialogFragment psf = MapFilterDialogFragment.istantiate(this, R.array.map_items_events_labels,
					R.array.map_items_events_icons, REQUEST_TYPE.EVENT,eventsCategories,
					CategoryHelper.getEventCategoriesForMapFilters() );
			psf.show(getFragmentManager(), TAG_FRAGMENT_POI_SELECT);
			return true;
		} /*
		 * else if (item.getItemId() == R.id.action_poi_babies) {
		 * PoiSelectFragment psf = PoiSelectFragment.istantiate(this,
		 * R.array.map_items_babies_labels, R.array.map_items_babies_icons,
		 * REQUEST_TYPE.POI, new String[] {
		 * CategoryHelper.POI_CATEGORIES[3].category });
		 * psf.show(getFragmentManager(), TAG_FRAGMENT_POI_SELECT); return true;
		 * }
		 *//*
			 * else if (item.getItemId() == R.id.action_list) { Log.i("MENU",
			 * "ACTION LIST!!!!"); if
			 * (getArguments().containsKey(ARG_EVENT_CATEGORY) ||
			 * getArguments().containsKey(ARG_TRACK_CATEGORY) ||
			 * getArguments().containsKey(ARG_POI_CATEGORY)) { switchToList(); }
			 * return true; }
			 */
		// this is needed because the activity manage the navigation drawer
		// return getActivity().onOptionsItemSelected(item);
		return true;
	}

	private void switchToList() {
		FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
		ft.setCustomAnimations(R.anim.enter, R.anim.exit);

		String cat = null;
		if (getArguments().containsKey(ARG_EVENT_CATEGORY)) {
			cat = getArguments().getString(ARG_EVENT_CATEGORY);
			Bundle args = new Bundle();
			EventsListingFragment elf = new EventsListingFragment();
			args.putString(SearchFragment.ARG_CATEGORY, cat);
			elf.setArguments(args);
			ft.replace(R.id.content_frame, elf, MainActivity.TAG_FRAGMENT_EVENT_LIST);
			ft.addToBackStack(MainActivity.TAG_FRAGMENT_EVENT_LIST);
		}

		ft.commit();
	}

	@Override
	public void onStart() {
		super.onStart();
		// hide keyboard if it is still open
		InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(getActivity().findViewById(R.id.content_frame).getWindowToken(), 0);

		if (!loaded) {
			String key = getString(R.string.view_intent_arg_object_id);
			if (getActivity().getIntent() != null && getActivity().getIntent().hasExtra(key)) {
				new SCAsyncTask<Void, Void, BaseDTObject>(getActivity(), new LoadDataProcessor(getActivity()))
						.execute();
				eventsCategories = null;
				poiCategories = null;
			} else {
				initView();
			}
			loaded = true;
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean("loaded", loaded);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		CategoryDescriptor[] eventsDefault = DTParamsHelper
				.getDefaultArrayByParams(CategoryHelper.CATEGORY_TYPE_EVENTS);
		if (eventsDefault != null) {
			List<String> eventCategory = new ArrayList<String>();
			for (CategoryDescriptor event : eventsDefault)
				eventCategory.add(event.category);
			eventsCategories = Arrays.asList(eventCategory.toArray()).toArray(
					new String[eventCategory.toArray().length]);
		}

		setHasOptionsMenu(true);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	protected void initView() {
		if (getSupportMap() != null) {
			getSupportMap().clear();
			setUpMap();
			getSupportMap().getUiSettings().setRotateGesturesEnabled(false);
			getSupportMap().getUiSettings().setTiltGesturesEnabled(false);
		}

		if (getArguments() != null && getArguments().containsKey(ARG_OBJECTS)) {
			poiCategories = null;
			eventsCategories = null;
			drawTracks((List<BaseDTObject>) getArguments().getSerializable(ARG_OBJECTS));
			Log.i("MENU", "ARG_OBJECTS");

		} else if (getArguments() != null && getArguments().containsKey(ARG_EVENT_CATEGORY)) {
			listmenu = true;
			Log.i("MENU", "LIST MENU in ARG_EVENT_CATEGORY");
			poiCategories = null;
			setEventCategoriesToLoad(getArguments().getString(ARG_EVENT_CATEGORY));
		} else {
			Log.i("MENU", "ELSE");

			if (eventsCategories != null) {
				setEventCategoriesToLoad(eventsCategories);
				Log.i("MENU", "set event categories to load");
			}
		}

		Log.i("MENU", "LIST MENU is" + listmenu);

	}

	// Mi dava fastidio il giallo u.u
	@SuppressWarnings("unchecked")
	private void drawTracks(List<? extends BaseDTObject> list) {

		new AsyncTask<List<? extends BaseDTObject>, Void, List<? extends BaseDTObject>>() {
			@Override
			protected List<? extends BaseDTObject> doInBackground(List<? extends BaseDTObject>... params) {
				return params[0];
			}

			@Override
			protected void onPostExecute(List<? extends BaseDTObject> result) {
				addObjects(result);
			}
		}.execute(list);
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getSupportMap() != null) {
			getSupportMap().setMyLocationEnabled(true);
			getSupportMap().setOnCameraChangeListener(this);
			getSupportMap().setOnMarkerClickListener(this);
			// if (objects != null) {
			// render(objects);
			// }
		}
	}

	@Override
	public void onConfigurationChanged(Configuration arg0) {
		super.onConfigurationChanged(arg0);
	}

	@Override
	public void onPause() {
		super.onPause();
		if (getSupportMap() != null) {
			getSupportMap().setMyLocationEnabled(false);
			getSupportMap().setOnCameraChangeListener(null);
			getSupportMap().setOnMarkerClickListener(null);
		}
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		menu.clear();
		Log.i("MENU", "start on Prepare Options Menu MAP frag: " + menu.toString());
		MenuItem filter = menu.add(Menu.NONE, Menu.NONE, 1, "filtro");
		// ovItem.setIcon(getResources().getDrawable(R.drawable.ic_location_actionbar));
		filter.setIcon(getResources().getDrawable(R.drawable.ic_filter));
		filter.setVisible(true);
		filter.setShowAsAction(MenuItem.SHOW_AS_ACTION_IF_ROOM);

		super.onPrepareOptionsMenu(menu);
	}

	public void setMiscellaneousListToLoad(final List<String> trackCategories, List<String> poiCategories,
			List<String> eventCategories) {

		final String[] pcat = poiCategories.toArray(new String[poiCategories.size()]);
		this.poiCategories = pcat;
		final String[] ecat = eventCategories.toArray(new String[eventCategories.size()]);
		this.eventsCategories = ecat;

		new SCAsyncTask<Void, Void, Collection<? extends BaseDTObject>>(getActivity(), new MapLoadProcessor(
				getActivity(), this, getSupportMap()) {
			@Override
			protected Collection<? extends BaseDTObject> getObjects() {
				try {
					/*
					 * check if todays is checked and cat with searchTodayEvents
					 */
					Collection<BaseDTObject> list = new ArrayList<BaseDTObject>();

					if (ecat.length > 0)
						list.addAll(DTHelper.getEventsByCategories(0, -1, ecat));
					SortedMap<String, Integer> sort = new TreeMap<String, Integer>();
					sort.put("title", 1);
					return list;

				} catch (Exception e) {
					e.printStackTrace();
					return Collections.emptyList();
				}
			}

		}).execute();
	}

	@Override
	public void setMiscellaneousCategoriesToLoad(String... categories) {
		List<String> tracks = new ArrayList<String>();
		List<String> events = new ArrayList<String>();
		List<String> pois = new ArrayList<String>();
		for (String s : categories) {
			if (Arrays.asList(CategoryHelper.getEventCategories()).contains(s)) {
				events.add(s);
			} else
				Log.e(this.getClass().getName(), "category not found: " + s);
		}

		setMiscellaneousListToLoad(tracks, pois, events);
	}

	private void onBaseDTObjectTap(BaseDTObject o) {
		Bundle args = new Bundle();
		args.putSerializable(InfoDialog.PARAM, o);
		InfoDialog dtoTap = new InfoDialog();
		dtoTap.setArguments(args);
		dtoTap.show(getActivity().getSupportFragmentManager(), "me");
	}

	private void onBaseDTObjectsTap(List<BaseDTObject> list) {
		if (list == null || list.size() == 0)
			return;
		if (list.size() == 1) {
			onBaseDTObjectTap(list.get(0));
			return;
		}
		FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
		Fragment fragment = null;
		Bundle args = new Bundle();
		if (list.get(0) instanceof ExplorerObject) {
			fragment = new EventsListingFragment();
			args.putSerializable(SearchFragment.ARG_LIST, new ArrayList(list));
		}
		if (fragment != null) {
			fragment.setArguments(args);
			fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			// fragmentTransaction.detach(this);
			fragmentTransaction.replace(R.id.content_frame, fragment, "me");
			fragmentTransaction.addToBackStack(fragment.getTag());
			fragmentTransaction.commit();
		}
	}

	@Override
	public void setEventCategoriesToLoad(final String... categories) {
		this.eventsCategories = categories;
		this.eventsCleaned = categories;

		/* actually only event or poi at the same time */
		this.poiCategories = null;

		// mItemizedoverlay.clearMarkers();

		getSupportMap().clear();
		setUpMap();
		new SCAsyncTask<Void, Void, Collection<? extends BaseDTObject>>(getActivity(), new MapLoadProcessor(
				getActivity(), this, getSupportMap()) {
			@Override
			protected Collection<? extends BaseDTObject> getObjects() {
				try {
					/*
					 * check if todays is checked and cat with searchTodayEvents
					 */
					Collection<ExplorerObject> newList;
					newList = new ArrayList<ExplorerObject>();

					if (isTodayIncluded()) {
						newList.addAll(DTHelper.searchTodayEvents(0, -1, ""));

					} 
					if (isMyIncluded()) {
						SortedMap<String, Integer> sort = new TreeMap<String, Integer>();
						sort.put("fromTime", 1);
							newList.addAll(DTHelper.searchInGeneral(0, -1, null,  null, null, true, ExplorerObject.class,
									 sort, null));

					}
					if (eventsCleaned.length!=0)
//						newList.addAll(DTHelper.getEventsByCategories(0, -1, eventsCleaned));
					{
						SortedMap<String, Integer> sort = new TreeMap<String, Integer>();
						sort.put("fromTime", 1);
						newList.addAll(DTHelper.searchInGeneral(0, -1, null,  null, null, false, ExplorerObject.class,
								 sort, eventsCleaned));
				}
					Iterator<ExplorerObject> i = newList.iterator();
					while (i.hasNext()) {
						ExplorerObject obj = i.next();
						obj.getLocation();
						if (obj.getLocation()[0] == 0 && obj.getLocation()[1] == 0)
							i.remove();
					}
					return newList;
				} catch (Exception e) {
					e.printStackTrace();
					return Collections.emptyList();
				}
			}

		}).execute();
	}

	private boolean isTodayIncluded() {
		List<String> categoriesCleaned = new ArrayList<String>();
		boolean istodayincluded = false;
		if (eventsCategories.length > 0)
			for (int i = 0; i < eventsCategories.length; i++) {
				if (eventsCategories[i].contains("Today")) {

					istodayincluded = true;
				} else
					categoriesCleaned.add(eventsCategories[i]);

			}
		eventsCleaned = categoriesCleaned.toArray(new String[categoriesCleaned.size()]);
		return istodayincluded;
	}

	private boolean isMyIncluded() {
		List<String> categoriesCleaned = new ArrayList<String>();
		boolean isMyincluded = false;
		if (eventsCategories.length > 0)
			for (int i = 0; i < eventsCategories.length; i++) {
				if (eventsCategories[i].contains("My")) {

					isMyincluded = true;
				} else
					categoriesCleaned.add(eventsCategories[i]);

			}
		eventsCleaned = categoriesCleaned.toArray(new String[categoriesCleaned.size()]);
		return isMyincluded;
	}

	private GoogleMap getSupportMap() {
		if (mMap == null) {
			if (getFragmentManager().findFragmentById(R.id.map) != null
					&& getFragmentManager().findFragmentById(R.id.map) instanceof SupportMapFragment){
				mMap = ((SupportMapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
			}
			if (mMap != null)
				mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(MapManager.DEFAULT_POINT, MapManager.ZOOM_DEFAULT));

		}
		return mMap;
	}

	 private void setUpMap() {
	        mMap.setMapType(GoogleMap.MAP_TYPE_NONE);
	        TileProvider tileProvider = new UrlTileProvider(256, 256) {
			    @Override
			    public URL getTileUrl(int x, int y, int z) {
			        try {
//			        	if (z>17) 
//			        		z=17;
			            return new URL(String.format(osmUrl, z, x, y));
			        }
			        catch (MalformedURLException e) {
			            return null;
			        }
			    }
	        };

	        mMap.addTileOverlay(new TileOverlayOptions().tileProvider(tileProvider));
	    }
	@Override
	public boolean onMarkerClick(Marker marker) {
		List<BaseDTObject> list = MapManager.ClusteringHelper.getFromGridId(marker.getTitle());
		if (list == null || list.isEmpty())
			return true;

		if (list.size() == 1) {
			onBaseDTObjectTap(list.get(0));
		} else if (getSupportMap().getCameraPosition().zoom >= maxZoomOnMap) {
			onBaseDTObjectsTap(list);
		} else {
			MapManager.fitMapWithOverlays(list, getSupportMap());
		}
		return true;
	}

	@Override
	public void onCameraChange(CameraPosition position) {
		manageMaxLevelOfZoom(position);
	    render(objects);
	}

	private void manageMaxLevelOfZoom(CameraPosition position) {
		/*check if the zoom level is too high*/
	    if (position.zoom > maxZoomOnMap)
	    	getSupportMap().animateCamera(CameraUpdateFactory.zoomTo(maxZoomOnMap));
	}

	@Override
	public <T extends BaseDTObject> void addObjects(Collection<? extends BaseDTObject> objects) {
		if (getSupportMap() != null) {
			this.objects = objects;
			render(objects);
			MapManager.fitMapWithOverlays(objects, getSupportMap());
		}
	}

	private void render(Collection<? extends BaseDTObject> objects) {
		if (getSupportMap() != null) {
//			if (MapManager.getMapView()!=null)
//			{
//				MapManager.getMapView().getOverlays().clear();
//				MapManager.getMapView().invalidate();
//			}
//			getSupportMap().clear();
//			setUpMap();

			if (objects != null && getActivity() != null) {
				List<MarkerOptions> cluster = MapManager.ClusteringHelper.cluster(
						getActivity().getApplicationContext(), getSupportMap(), objects);
				MapManager.ClusteringHelper.removeAllMarkers();
				MapManager.ClusteringHelper.render(getActivity(), getSupportMap(), cluster, objects);
			}
		}


	}

	private class LoadDataProcessor extends AbstractAsyncTaskProcessor<Void, BaseDTObject> {

		public LoadDataProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public BaseDTObject performAction(Void... params) throws SecurityException, Exception {
			String entityId = getActivity().getIntent().getStringExtra(getString(R.string.view_intent_arg_object_id));
			String type = getActivity().getIntent().getStringExtra(getString(R.string.view_intent_arg_entity_type));

			if (entityId != null && type != null) {
				if ("event".equals(type))
					return DTHelper.findEventByEntityId(entityId);

			}
			return null;
		}

		@Override
		public void handleResult(BaseDTObject result) {

			String key = getString(R.string.view_intent_arg_object_id);
			String entityId = getActivity().getIntent().getStringExtra(key);
			getActivity().getIntent().removeExtra(key);

			if (entityId != null) {
				if (result == null) {
					Toast.makeText(getActivity(), R.string.app_failure_obj_not_found, Toast.LENGTH_LONG).show();
					return;
				}

				Fragment fragment = null;
				Bundle args = new Bundle();
				if (result instanceof ExplorerObject) {
					fragment = new Fragment_EventDetails();
					args.putString(Utils.ARG_EVENT_ID, (result.getId()));
				}
				// else if (result instanceof StoryObject) {
				// fragment = new StoryDetailsFragment();
				// args.putString(StoryDetailsFragment.ARG_STORY_ID,
				// result.getId());
				// }
				if (fragment != null) {
					FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager()
							.beginTransaction();
					fragment.setArguments(args);

					fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
					fragmentTransaction.replace(R.id.content_frame, fragment, "me");
					fragmentTransaction.addToBackStack(fragment.getTag());
					fragmentTransaction.commit();
				}
			}
		}

	}

}
