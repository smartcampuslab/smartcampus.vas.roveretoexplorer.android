package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info;


import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Address;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.EventObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.EventPlaceholder;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.Fragment_EventDetails;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.TextView;
import android.widget.Toast;


public class Fragment_EvDetail_Info extends Fragment {



	protected Context context;

	//For the expandable list view 
	List<String> attributeGroupList;
	//private List<LocalEventObject> listEvents = new ArrayList<LocalEventObject>();
	Map<String, List<String>> eventAttributeCollection;
	ExpandableListView expListView;
	public EventObject mEvent = null;
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


	private View _lastColored;


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
		ArrayList<EventInfoParent> dummyList = getEventDetailData(mEvent);
		setGroupImages();


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
		
		
//		expListView
//         .setOnChildClickListener(new OnChildClickListener() {
//
//             @Override
//             public boolean onChildClick(ExpandableListView parent,
//                     View v, int groupPosition, int childPosition,
//                     long id) {
//
//            	 if(_lastColored != null)
//            	    {
//            	    _lastColored.setBackgroundColor(Color.WHITE);
//            	    _lastColored.invalidate();
//            	    }
//            	    _lastColored = v;
//            	    
//                 v.setBackgroundColor(Color.BLUE);
//
//
//            	    return false;
//
//                 }
//         });

	}


	private void setGroupImages()
	{

		groupImages= new ArrayList<Integer>();
		groupImages.add(R.drawable.ic_action_edit_white);
		groupImages.add(R.drawable.ic_action_edit_white);
		groupImages.add(R.drawable.ic_action_edit_white);
		groupImages.add(R.drawable.ic_action_edit_white);
		groupImages.add(R.drawable.ic_action_new_label);


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
	private ArrayList<EventInfoParent> getEventDetailData(EventObject event)
	{


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

				Address address = event.getAddress();
				if (address != null){

					String place = (address.getLuogo() !=null)? (String) address.getLuogo() : null;

					String street = (address.getVia() !=null)? (String) address.getVia() : null;

					String city = (address.getCitta() !=null)? (String) address.getCitta() : null;

					if (place != null){
						// Create Child class object 
						EventInfoChild child = new EventInfoChild();
						child.setName("0");
						child.setText(place);
						child.setType(0);
						parent.getChildren().add(child);
					}
					if (city != null){
						// Create Child class object 
						EventInfoChild child = new EventInfoChild();
						child.setName("1");
						child.setText(city);
						child.setType(0);
						parent.getChildren().add(child);
					}
					if (street != null){
						// Create Child class object 
						EventInfoChild child = new EventInfoChild();
						child.setName("2");
						child.setText(street);
						child.setType(0);
						parent.getChildren().add(child);
					}
				}

			}
			else if(i==2){  //field QUANDO
				parent.setName("" + i);
				parent.setText1("Quando");
				parent.setChildren(new ArrayList<EventInfoChild>());

				if (event.getFromTime()!= null){
					String when = getDateString(event.getFromTime());
					EventInfoChild child = new EventInfoChild();
					child.setName("0");
					child.setText(when);
					child.setType(0);
					parent.getChildren().add(child);
				}

				if (event.getToTime()!= null){
					//compute duration
					String duration = "3 ore";
					if (duration!=null){
						EventInfoChild child = new EventInfoChild();
						child.setName("1");
						child.setText("Durata: " + duration);
						child.setType(0);
						parent.getChildren().add(child);
					}
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
					child.setText(desc);
					child.setType(0);
					parent.getChildren().add(child);
				}
			}
			else if(i==4){ //field CONTATTI
				parent.setName("" + i);
				parent.setText1("Contatti");
				parent.setChildren(new ArrayList<EventInfoChild>());
				String[] telList = null;

				//set the Phone item of type 1
				EventInfoChild telChildLabel = new EventInfoChild();
				telChildLabel.setName("Phones");
				telChildLabel.setText("Telefono");
				telChildLabel.setType(1);
				telChildLabel.setLeftIconId( R.drawable.ic_action_phone);
				
				//to be added again when it will be possible to add more numbers
//				int[] rightIconIds = new int[]{R.drawable.ic_action_new};
//				telChildLabel.setRightIconIds(rightIconIds);
				
				parent.getChildren().add(telChildLabel);


				//set the list of phone numbers
				if (event.getContacts().containsKey("telefono")){
					telList = (String[]) event.getContacts().get("telefono"); 
					for (String tel : telList){
						EventInfoChild child1 = new EventInfoChild();
						child1.setName("tel");
						child1.setText(tel);
						child1.setType(0);
						
						//to be added when it will be possible to cancel/edit the single item
						//int[] rightIconIds1 = new int[]{R.drawable.ic_action_edit, R.drawable.ic_action_cancel, R.drawable.ic_action_call};
						int[] rightIconIds1 = new int[]{R.drawable.ic_action_call};
						
						child1.setRightIconIds(rightIconIds1);
						parent.getChildren().add(child1);
					}
				}

				//set the Email item of type 1
				EventInfoChild emailChildLabel = new EventInfoChild();
				emailChildLabel.setName("Emails");
				emailChildLabel.setText("Email");
				emailChildLabel.setType(1);
				emailChildLabel.setLeftIconId( R.drawable.ic_action_email);
		
				
				//to be added again when it will be possible to add more emails
//				int[] rightIconIdsEmail = new int[]{R.drawable.ic_action_new_email};
//				emailChildLabel.setRightIconIds(rightIconIdsEmail);
				
				parent.getChildren().add(emailChildLabel);


				String[] emails = null;
				if (event.getContacts().containsKey("email")){
					emails = (String[]) event.getContacts().get("email"); 
					for (String email : emails){
						EventInfoChild child = new EventInfoChild();
						child.setName("email");
						child.setText(email);
						child.setType(0);

						//to be added when it will be possible to cancel/edit the single item
						//int[] rightIconIds2 = new int[]{R.drawable.ic_action_edit, R.drawable.ic_action_cancel, R.drawable.ic_action_email};
						int[] rightIconIds2 = new int[]{R.drawable.ic_compose_email};
						child.setRightIconIds(rightIconIds2);
						parent.getChildren().add(child);						
					}
				}

				//set the Web Site item of type 0
				EventInfoChild siteChildLabel = new EventInfoChild();
				siteChildLabel.setName("Site");
				if (event.getWebsiteUrl()!=null){
					siteChildLabel.setText("<a href=\"" + event.getWebsiteUrl() + "\">Website</a>");
				}
				else
					siteChildLabel.setText("Web Site");
				siteChildLabel.setType(0);
				siteChildLabel.setLeftIconId( R.drawable.ic_action_web_site);
				parent.getChildren().add(siteChildLabel);

				//set Facebook item of type 0
				EventInfoChild fbChildLabel = new EventInfoChild();
				fbChildLabel.setName("FB");
				if (event.getFacebookUrl()!=null){
					siteChildLabel.setText("<a href=\"" + event.getFacebookUrl() + "\">Facebook</a>");
				}
				else
					siteChildLabel.setText("Facebook");
				fbChildLabel.setType(0);
				fbChildLabel.setLeftIconId( R.drawable.ic_facebook); //to substitute with the right icon
				parent.getChildren().add(fbChildLabel);

				//set Twitter item of type 0
				EventInfoChild twitterChildLabel = new EventInfoChild();
				twitterChildLabel.setName("Twitter");
				if (event.getTwitterUrl()!=null){
					siteChildLabel.setText("<a href=\"" + event.getTwitterUrl() + "\">Twitter</a>");
				}
				else
					siteChildLabel.setText("Twitter");

				twitterChildLabel.setType(0);
				twitterChildLabel.setLeftIconId( R.drawable.ic_twitter); //to substitute with the right icon
				parent.getChildren().add(twitterChildLabel);


			}
			else if(i==5){ //field TAGS
				parent.setName("" + i);
				parent.setText1("Tags");
				parent.setChildren(new ArrayList<EventInfoChild>());
				List<String> tags = null;
				if (event.getCommunityData().getTags()!=null){
					tags = event.getCommunityData().getTags(); 
					for (String tag : tags){
						EventInfoChild child = new EventInfoChild();
						child.setName("tag");
						child.setText(tag);
						child.setType(0);
						child.setLeftIconId(R.drawable.ic_action_labels_dark);
						parent.getChildren().add(child);
					}
				}
			}

			//Adding Parent class object to ArrayList 	      
			list.add(parent);
		}
		return list;
	}




	private  Map<String, List<String>> getEventDetailCollection(List<String> attrGroupList, EventObject event) {

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
