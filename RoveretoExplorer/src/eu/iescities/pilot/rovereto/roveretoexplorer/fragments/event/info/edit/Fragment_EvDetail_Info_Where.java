package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.edit;



import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.DTParamsHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Address;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.trentorise.smartcampus.android.common.GeocodingAutocompletionHelper;
import eu.trentorise.smartcampus.android.common.GeocodingAutocompletionHelper.OnAddressSelectedListener;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;


public class Fragment_EvDetail_Info_Where extends Fragment {

	private Context context;


	public static final String ARG_EVENT_ID = "event_id";
	public final static int RESULT_SELECTED = 10;

	protected static final String ADDRESS = "address";


	private ExplorerObject mEvent = null;
	private String mEventId;



	protected TextView formLabel;
	protected EditText txtPlaceName;
	protected EditText txtCity;
	protected AutoCompleteTextView txtStreet;
	protected Position where;




	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onCreate");

		this.context = this.getActivity();

		if(savedInstanceState==null)
		{
			Log.d("FRAGMENT LC","onCreate FIRST TIME");
			setHasOptionsMenu(true);

			if (getArguments() != null) {
				mEventId = getArguments().getString(ARG_EVENT_ID);
				Log.i("FRAGMENT LC", "Fragment_evDetail_Info_Where --> EVENT ID: " + mEventId);

				//now it will be always null so I load the fake data
				mEvent = DTHelper.findEventById(mEventId);
				//List<ExplorerObject> eventList = Utils.getFakeExplorerObjects();
				//mEvent = Utils.getFakeLocalExplorerObject(Utils.appEvents,mEventId);
			}

		}
		else
		{
			Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onCreate SUBSEQUENT TIME");
		}

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater,container,savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onCreateView");

		return inflater.inflate(R.layout.frag_ev_detail_info_edit_where, container, false);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		List<Double> mapcenter = DTParamsHelper.getCenterMap();
		double[] refLoc = mapcenter == null ? null : new double[] { mapcenter.get(0), mapcenter.get(1) };

		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onActivityCreated");

		//getActivity().getActionBar().setTitle(mEvent.getTitle()); 
		getActivity().getActionBar().setTitle("Modifica luogo"); 

		formLabel = (TextView) getActivity().findViewById(R.id.title_where_label);
		txtPlaceName= (EditText) getActivity().findViewById(R.id.place_name_text);
		txtCity = (EditText) getActivity().findViewById(R.id.city_text);
//		txtStreet = (EditText) getActivity().findViewById(R.id.street_text);
		txtStreet = (AutoCompleteTextView) getView().findViewById(R.id.street_text);
		GeocodingAutocompletionHelper fromAutocompletionHelper = new GeocodingAutocompletionHelper(
				getActivity(), txtStreet, Utils.ROVERETO_REGION, Utils.ROVERETO_COUNTRY, Utils.ROVERETO_ADM_AREA, refLoc);
		fromAutocompletionHelper.setOnAddressSelectedListener(new OnAddressSelectedListener() {
			@Override
			public void onAddressSelected(android.location.Address address) {
				savePosition(address);
			}


		});
		ImageView imgBtn = (ImageView) getView().findViewById(R.id.select_where_map);

		if (imgBtn !=null){
			imgBtn.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(), AddressSelectActivity.class);
					intent.putExtra("field", ADDRESS);
					startActivityForResult(intent, RESULT_SELECTED);
					
				}
			});

		}
		formLabel.setText("Evento: " + mEvent.getTitle());

		Address address = mEvent.getAddress();

		if (address!=null){

			String place = (address.getLuogo() !=null)? (String) address.getLuogo() : "";
			String street = (address.getVia() !=null)? (String) address.getVia() : "";
			String city = (address.getCitta() !=null)? (String) address.getCitta() : "";
			txtPlaceName.setText(place);
			txtCity.setText(city);
			txtStreet.setText(street);
		}	


		Button modifyBtn = (Button) getView().findViewById(R.id.edit_contacts_modify_button);
		modifyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

//				Toast.makeText(context, "Edited Fields: " + txtPlaceName.getText() + ", " + txtCity.getText() + 
//						", " + txtStreet.getText() , Toast.LENGTH_SHORT).show();

				//set the new fields
//				ExplorerObject ev = Utils.getFakeLocalExplorerObject(Utils.appEvents, mEventId);
//				int index = Utils.appEvents.indexOf(mEvent);
//				int index2 = Utils.appEvents.indexOf(ev);
//
//				Log.i("FRAGMENT LC", "index: " + index);
//				Log.i("FRAGMENT LC", "index 2: " + index2);

				Address modifiedAddress= new Address();
				modifiedAddress.setLuogo(txtPlaceName.getText().toString());
				modifiedAddress.setVia(txtStreet.getText().toString());
				modifiedAddress.setCitta(txtCity.getText().toString());
				mEvent.setAddress(modifiedAddress);
				
				//persist the new contacts
								new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
								.execute(mEvent);
				//Utils.appEvents.set(index, mEvent);
				


			}
		});


		Button cancelBtn = (Button) getView().findViewById(R.id.edit_contacts_cancel_button);
		cancelBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});


	}

	private void savePosition(android.location.Address address ) {
		EditText street = null;
		EditText city = null;


		String s = "";
		for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
			s += address.getAddressLine(i) + " ";
		}
		s = s.trim();

			where = new Position(address.getAddressLine(0), address.getCountryName(), address.getLocality(), address.getLongitude(),
					address.getLatitude());
			if (getView() != null) {
				street = (EditText) getView().findViewById(R.id.street_text);
				city = (EditText) getView().findViewById(R.id.city_text);
			}

		if (street != null) {
			street.setFocusable(false);
			street.setFocusableInTouchMode(false);
			street.setText(s);
			street.setFocusable(true);
			street.setFocusableInTouchMode(true);
		}
		
		if (city != null) {
			city.setFocusable(false);
			city.setFocusableInTouchMode(false);
			city.setText(address.getLocality());
			city.setFocusable(true);
			city.setFocusableInTouchMode(true);
		}
		

	}

	//to be deleted when there will be the call to the server
	public void setNewEventContacts(String eventID, String[] tel, String[] email, String website){

		//		//set the new fields	
		//		Map<String,Object> contacts = new HashMap<String, Object>();
		//		contacts.put("telefono", tel);
		//		contacts.put("email", email);
		//		mEvent.getContacts().clear();
		//		mEvent.setContacts(contacts);
		//		mEvent.setWebsiteUrl(website);

	}



	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent result) {
		
		if (resultCode == RESULT_SELECTED) {
			android.location.Address address = result.getParcelableExtra("address");
			String field = result.getExtras().getString("field");
			savePosition(address);
		}
	}











	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onStart");


	}



	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onResume");

	}



	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onPause");

	}



	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onSaveInstanceState");

	}



	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onStop");

	}



	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onDestroyView");

	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onDestroy");

	}



	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Where --> onDetach");

	}


	@Override
	public void onPrepareOptionsMenu(Menu menu) {

		Log.i("FRAGMENT LC", "start on Prepare Options Menu EVENT LISTING frag: " + menu.toString());

		menu.clear();

		//getActivity().getMenuInflater().inflate(R.menu.event_detail_menu, menu);

		/*if (category == null) {
			category = (getArguments() != null) ? getArguments().getString(SearchFragment.ARG_CATEGORY) : null;
		}
		 */
		super.onPrepareOptionsMenu(menu);
	}    


	private class UpdateEventProcessor extends AbstractAsyncTaskProcessor<ExplorerObject, Boolean> {

		public UpdateEventProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public Boolean performAction(ExplorerObject... params) throws SecurityException, Exception {

			//to be enabled when the connection with the server is ok
			return DTHelper.saveEvent(params[0]);
			//store the modified event
//			int index = Utils.appEvents.indexOf(params[0]);
//			Utils.appEvents.set(index, params[0]);
//			ExplorerObject mNewEvent = Utils.appEvents.get(index);
//			return true;
		}

		@Override
		public void handleResult(Boolean result) {
			if (getActivity() != null) {
				getActivity().getSupportFragmentManager().popBackStack();

				if (result) {
					Toast.makeText(getActivity(), R.string.event_create_success, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.update_success, Toast.LENGTH_SHORT).show();
				}
			}
		}
	}


}