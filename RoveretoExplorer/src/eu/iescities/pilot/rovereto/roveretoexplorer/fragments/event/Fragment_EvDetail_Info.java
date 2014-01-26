package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;


public class Fragment_EvDetail_Info extends Fragment {

	protected Context context;

	//For the expandable list view 
	List<String> attributeGroupList;
	//private List<LocalExplorerObject> listEvents = new ArrayList<LocalExplorerObject>();
	Map<String, List<String>> eventAttributeCollection;
	ExpandableListView expListView;
	public ExplorerObject mEvent = null;
	private EventDetailInfoAdapter eventDetailInfoAdapter;


	private static final String ARG_POSITION = "position";
	public static final String ARG_EVENT_ID = "event_id";
	public static final String ARG_INDEX = "index_adapter";

	private Integer indexAdapter;
	private String mEventId;

	private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final DateFormat extDateFormat = new SimpleDateFormat("EEEEEE dd/MM/yyyy");

	//Initialize variables
	protected int ParentClickStatus=-1;
	protected int ChildClickStatus=-1;
	protected ArrayList<EventInfoParent> parents;
	protected List<Integer> groupImages;
	//private HashMap<Integer, List<Integer>> childImages;
	protected HashMap<Integer, List<Integer>> childType2Images;
	protected HashMap<Integer, Integer> childType1Images;



	public static Fragment_EvDetail_Info newInstance(String event_id) {
		Fragment_EvDetail_Info  f = new Fragment_EvDetail_Info();
		Bundle b = new Bundle();
		b.putString(ARG_EVENT_ID, event_id);
		f.setArguments(b);
		return f;
	}



	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("SCROLLTABS","onAttach");

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.context = this.getActivity();

		if(savedInstanceState==null)
		{
			Log.d("SCROLLTABS","onCreate FIRST TIME");
			if (getArguments() != null) {
				mEventId = getArguments().getString(ARG_EVENT_ID);
				//now it will be always null so I load the fake data
				//mEvent = DTHelper.findEventById(mEventId);
				List<ExplorerObject> eventList = Utils.getFakeExplorerObjects();
				mEvent = Utils.getFakeLocalExplorerObject(eventList,mEventId);
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
		return inflater.inflate(R.layout.frag_ev_detail_info, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("SCROLLTABS","onActivityCreated");

		//display the event title
		TextView titleTextView = (TextView) getActivity().findViewById(R.id.event_placeholder_title);
		String text = mEvent.getTitle() + " ";
		SpannableString ss = new SpannableString(text); 
		Drawable d = getResources().getDrawable(R.drawable.ic_action_edit); 
		d.setBounds(0, 0, 35, 35); 
		ImageSpan span = new ImageSpan(d, ImageSpan.ALIGN_BASELINE); 
		int start = text.length() - 1;
		ss.setSpan(span,start, start + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		ss.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), start, start+1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
		ss.setSpan(new ClickableSpan() {

			@Override
			public void onClick(View v) {  
				Log.d("main", "link clicked");
				Toast.makeText(context, "modify event title", Toast.LENGTH_SHORT).show(); 
			} }, start, start + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
		titleTextView.setText(ss); 
		titleTextView.setMovementMethod(LinkMovementMethod.getInstance());


		//display the event image 
		//		ImageView imgView = (ImageView) getActivity().findViewById(R.id.event_placeholder_photo);
		//		Bitmap bmp = null;
		//		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		//		StrictMode.setThreadPolicy(policy); 
		//		try {
		//			if (mEvent.getCustomData().containsKey("event_img")){
		//				URL img_url = (URL) mEvent.getCustomData().get("event_img");
		//				Log.i("SCROLLTABS", "image url: " + img_url.toString() + "!!");
		//				if (img_url!=null){ 
		//					bmp = BitmapFactory.decodeStream(img_url.openConnection().getInputStream());
		//				}
		//			}
		//		} catch (IOException e1) {
		//			e1.printStackTrace();
		//		}
		//		if (bmp!=null)
		//			imgView.setImageBitmap(bmp);


		//display the event category plus the "promoted by" attribute 
		TextView categoryTextView = (TextView) getActivity().findViewById(R.id.event_placeholder_category);
		String category = "Evento sportivo";  // to be modified!

		if (mEvent.getCustomData().containsKey("PromossoDa")){
			text = new String(category + ", promosso da " + (String) mEvent.getCustomData().get("PromossoDa") + " ") ;
			ss = new SpannableString(text); 
			start = text.length() - 1;
			ss.setSpan(span,start, start + 1, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
			ss.setSpan(new ClickableSpan() {

				@Override
				public void onClick(View v) {  
					Log.d("main", "link clicked");
					Toast.makeText(context, "modify promoted by", Toast.LENGTH_SHORT).show(); 
				} }, start, start + 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			categoryTextView.setText(ss); 
			categoryTextView.setMovementMethod(LinkMovementMethod.getInstance());
		}
		else 
			categoryTextView.setText(category + "."); 


		//display the event attributes 
		setExpandableListView(savedInstanceState);

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


	private void setExpandableListView(Bundle savedInstanceState){

		Log.d("SCROLLTABS","setExpandable list view");

		//Creating static data in arraylist
		ArrayList<EventInfoParent> dummyList = buildDummyData(mEvent);
		setGroupChildImages();


		//		attributeGroupList =  createAttributeGroupList();
		//		//eventAttributeCollection = createFakeEventDetailCollection(attributeGroupList);
		//		eventAttributeCollection = getEventDetailCollection(attributeGroupList, mEvent);

		expListView = (ExpandableListView) getActivity().findViewById(R.id.event_details_info);

		if (savedInstanceState != null) {
			// Restore last state for checked position.
			mEventId = savedInstanceState.getString(ARG_EVENT_ID);
			indexAdapter = savedInstanceState.getInt(ARG_INDEX);
		}

		/* create the adapter is it is the first time you load */
		// Adding ArrayList data to ExpandableListView values
		setAdapter(dummyList);

		expListView.setAdapter(eventDetailInfoAdapter);
		

	}


	private void setGroupChildImages()
	{
		
		groupImages= new ArrayList<Integer>();
		groupImages.add(R.drawable.ic_action_edit_white);
        groupImages.add(R.drawable.ic_action_edit_white);
        groupImages.add(R.drawable.ic_action_edit_white);
        groupImages.add(R.drawable.ic_action_edit_white);
        groupImages.add(R.drawable.ic_action_labels);

        childType1Images = new HashMap<Integer, Integer>();
        childType2Images = new HashMap<Integer, List<Integer>>();

        
//        List<Integer> where = new ArrayList<Integer>();
//        where.add(R.drawable.ic_action_place);
        
//        List<Integer> when = new ArrayList<Integer>();
//        when.add(R.drawable.ic_action_time);
        
        List<Integer> contacts = new ArrayList<Integer>();
        contacts.add(R.drawable.ic_action_phone);
        contacts.add(R.drawable.ic_action_email);
        contacts.add(R.drawable.ic_action_web_site);
        
        List<Integer> tags = new ArrayList<Integer>();
        tags.add(R.drawable.ic_action_labels_dark);
        
        childType1Images.put(groupImages.get(0), R.drawable.ic_action_place);
        childType1Images.put(groupImages.get(1), R.drawable.ic_action_time);
        childType1Images.put(groupImages.get(4), R.drawable.ic_action_labels_dark);

        childType2Images.put(groupImages.get(3), contacts);
       		
        int imageId = groupImages.get(0);
		Log.i("GROUPVIEW", "FRAGMENT IMAGE ID: " + imageId);
	}
	
	
	private void setAdapter(final ArrayList<EventInfoParent> newParents)
	{
		if (newParents == null)
			return;

		parents = newParents;

		// Check for ExpandableListAdapter object
		if (this.getExpandableListAdapter() == null)
		{
			//Create ExpandableListAdapter Object
			//final EventDetailInfoAdapter mAdapter = new EventDetailInfoAdapter();
			//EventDetailInfoAdapter mAdapter = new EventDetailInfoAdapter(context, parents, Fragment_EvDetail_Info.this);
			EventDetailInfoAdapter mAdapter = new EventDetailInfoAdapter(Fragment_EvDetail_Info.this);

			// Set Adapter to ExpandableList Adapter
			this.setListAdapter(mAdapter);
		}
		else
		{
			// Refresh ExpandableListView data 
			((EventDetailInfoAdapter)getExpandableListAdapter()).notifyDataSetChanged();
		}	
	}


	private  static ArrayList<String> createAttributeGroupList(){
		ArrayList<String> groupList = new ArrayList<String>();
		groupList.add("Dove");
		groupList.add("Quando");
		groupList.add("Cosa");
		groupList.add("Contatti");
		groupList.add("Tags");
		return groupList;
	}


	/**
	 * here should come your data service implementation
	 * @return
	 */
	private ArrayList<EventInfoParent> buildDummyData(ExplorerObject event)
	{

		Log.i("DummyList", "dummylist start");

		// Creating ArrayList of type parent class to store parent class objects
		ArrayList<EventInfoParent> list = new ArrayList<EventInfoParent>();
		for (int i = 1; i < 6; i++)
		{
			//Create parent class object
			EventInfoParent parent = new EventInfoParent();

			// Set values in parent class object
			if(i==1){   //field DOVE
				parent.setName("" + i);
				parent.setText1("Dove");
				parent.setChildren(new ArrayList<EventInfoChild>());

				String place = event.getCustomData().containsKey("where_name") ? (String) event.getCustomData().get("where_name") : null;
				String city = event.getCustomData().containsKey("where_city") ? (String) event.getCustomData().get("where_city") : null;
				String street = event.getCustomData().containsKey("where_street") ? (String) event.getCustomData().get("where_street") : null;
				if (place != null){
					// Create Child class object 
					EventInfoChild child = new EventInfoChild();
					child.setName("0");
					child.setText1(place);
					parent.getChildren().add(child);
				}
				if (city != null){
					// Create Child class object 
					EventInfoChild child = new EventInfoChild();
					child.setName("1");
					child.setText1(city);
					parent.getChildren().add(child);
				}
				if (street != null){
					// Create Child class object 
					EventInfoChild child = new EventInfoChild();
					child.setName("2");
					child.setText1(street);
					parent.getChildren().add(child);
				}

				Log.i("DummyList", "parent " + i + "size: ," + String.valueOf(parent.getChildren().size()) );
				Log.i("DummyList", "parent " + i + "children: ," + parent.getChildren() );

			}
			else if(i==2){  //field QUANDO
				parent.setName("" + i);
				parent.setText1("Quando");
				parent.setChildren(new ArrayList<EventInfoChild>());

				if (event.getFromTime()!= null){
					String when = getDateString(event.getFromTime());
					EventInfoChild child = new EventInfoChild();
					child.setName("0");
					child.setText1(when);
					parent.getChildren().add(child);
				}
				String duration = event.getCustomData().containsKey("Durata") ? (String) event.getCustomData().get("Durata") : null;
				if (duration!=null){
					EventInfoChild child = new EventInfoChild();
					child.setName("1");
					child.setText1(duration);
					parent.getChildren().add(child);
				}
			}
			else if(i==3){ //field COSA
				parent.setName("" + i);
				parent.setText1("Cosa");
				parent.setChildren(new ArrayList<EventInfoChild>());
				if (event.getDescription()!=null){
					String desc = event.getDescription();
					EventInfoChild child = new EventInfoChild();
					child.setName("0");
					child.setText1(desc);
					parent.getChildren().add(child);
				}
			}
			else if(i==4){ //field CONTATTI
				parent.setName("" + i);
				parent.setText1("Contatti");
				parent.setChildren(new ArrayList<EventInfoChild>());
				String[] telList = null;
				if (event.getCustomData().containsKey("Telefono")){
					telList = (String[]) event.getCustomData().get("Telefono"); 
					for (String tel : telList){
						EventInfoChild child = new EventInfoChild();
						child.setName("tel");
						child.setText1(tel);
						parent.getChildren().add(child);
					}

				}
				String[] emails = null;
				if (event.getCustomData().containsKey("Email")){
					emails = (String[]) event.getCustomData().get("Email"); 
					for (String email : emails){
						EventInfoChild child = new EventInfoChild();
						child.setName("email");
						child.setText1(email);
						parent.getChildren().add(child);						
					}
				}
			}
			else if(i==5){ //field TAGS
				parent.setName("" + i);
				parent.setText1("Tags");
				parent.setChildren(new ArrayList<EventInfoChild>());
				String[] tags = null;
				if (event.getCustomData().containsKey("Tags")){
					tags = (String[]) event.getCustomData().get("Tags"); 
					for (String tag : tags){
						EventInfoChild child = new EventInfoChild();
						child.setName("tag");
						child.setText1(tag);
						parent.getChildren().add(child);
					}
				}
			}

			//Adding Parent class object to ArrayList 	      
			list.add(parent);
		}
		return list;
	}




	private  Map<String, List<String>> getEventDetailCollection(List<String> attrGroupList, ExplorerObject event) {

		Map<String, List<String>> eventCollection = new LinkedHashMap<String, List<String>>();
		List<String> childList = new ArrayList<String>();

		// get attribute values collection(child)
		for (String event_attrName : attrGroupList) {
			childList = new ArrayList<String>();
			if (event_attrName.equals("Dove")) {
				String place = event.getCustomData().containsKey("where_name") ? (String) event.getCustomData().get("where_name") : null;
				String city = event.getCustomData().containsKey("where_city") ? (String) event.getCustomData().get("where_city") : null;
				String street = event.getCustomData().containsKey("where_street") ? (String) event.getCustomData().get("where_street") : null;
				if (place != null)
					childList.add(place);
				if (city != null)
					childList.add(city);
				if (street != null)
					childList.add(street);
			} else if (event_attrName.equals("Quando")){

				if (event.getFromTime()!= null){
					String when = getDateString(event.getFromTime());
					childList.add(when);
				}
				String duration = event.getCustomData().containsKey("Durata") ? (String) event.getCustomData().get("Durata") : null;
				if (duration!=null)
					childList.add("Durata: " + duration);
			}
			else if (event_attrName.equals("Cosa")){
				if (event.getDescription()!=null){
					String desc = event.getDescription();
					childList.add(desc);
				}
			}
			else if (event_attrName.equals("Contatti")){
				String[] telList = null;
				if (event.getCustomData().containsKey("Telefono")){
					telList = (String[]) event.getCustomData().get("Telefono"); 
					for (String tel : telList)
						childList.add(tel);
				}
				String[] emails = null;
				if (event.getCustomData().containsKey("Email")){
					emails = (String[]) event.getCustomData().get("Email"); 
					for (String email : emails)
						childList.add(email);
				}
			}
			else if (event_attrName.equals("Tags")){
				String[] tags = null;
				if (event.getCustomData().containsKey("Tags")){
					tags = (String[]) event.getCustomData().get("Tags"); 
					for (String tag : tags)
						childList.add(tag);
				}
			}
			eventCollection.put(event_attrName, childList);
		}
		return eventCollection;
	}




	private String getDateString(Long fromTime) {
		String newdateformatted = new String("");

		Date dateToday = new Date();
		String stringToday = (dateFormat.format(dateToday));
		String stringEvent = (dateFormat.format(new Date(fromTime)));

		Calendar cal = Calendar.getInstance();
		cal.setTime(dateToday);
		cal.add(Calendar.DAY_OF_YEAR, 1); // <--
		Date tomorrow = cal.getTime();
		String stringTomorrow = (dateFormat.format(tomorrow));
		// check actual date
		if (stringToday.equals(stringEvent)) {
			// if equal put the Today string
			newdateformatted = stringToday;
			newdateformatted = this.context.getString(R.string.list_event_today) + " " + newdateformatted;
		} else if (stringTomorrow.equals(stringEvent)) {
			// else if it's tomorrow, cat that string
			newdateformatted = stringTomorrow;
			newdateformatted = this.context.getString(R.string.list_event_tomorrow) + " " + newdateformatted;
		}
		// else put the day's name
		else
			newdateformatted = extDateFormat.format(new Date(fromTime));
		return newdateformatted;
	}




	/**
	 * Get the ExpandableListAdapter associated with this activity's
	 * ExpandableListView.
	 */
	public EventDetailInfoAdapter getExpandableListAdapter() {
		return eventDetailInfoAdapter;
	}



	public void setListAdapter(EventDetailInfoAdapter adapter) {
		eventDetailInfoAdapter  = adapter;
	}




















}
