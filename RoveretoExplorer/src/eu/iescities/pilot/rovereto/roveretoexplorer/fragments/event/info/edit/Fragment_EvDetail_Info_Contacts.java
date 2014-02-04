package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.edit;



import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;


public class Fragment_EvDetail_Info_Contacts extends Fragment {

	private Context context;


	public static final String ARG_EVENT_ID = "event_id";
	public static final String ARG_EVENT_PHONE = "event_phone";
	public static final String ARG_EVENT_EMAIL = "event_email";
	public static final String ARG_EVENT_WEBSITE = "event_website";


	private eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject mEvent = null;
	private String mEventId;



	TextView formLabel;
	EditText txtPhone;
	EditText txtEmail;
	EditText txtWebsite;
	EditText txtFacebook;
	EditText txtTwitter;



	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onCreate");

		this.context = this.getActivity();

		if(savedInstanceState==null)
		{
			Log.d("FRAGMENT LC","onCreate FIRST TIME");
			setHasOptionsMenu(true);

			if (getArguments() != null) {
				mEventId = getArguments().getString(ARG_EVENT_ID);
				Log.i("FRAGMENT LC", "EVENT ID: " + mEventId);

				mEvent = DTHelper.findEventById(mEventId);
				//List<ExplorerObject> eventList = Utils.getFakeExplorerObjects();
				//mEvent = Utils.getFakeLocalExplorerObject(Utils.appEvents,mEventId);
			}

		}
		else
		{
			Log.d("FRAGMENT LC","onCreate SUBSEQUENT TIME");
		}

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater,container,savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onCreateView");

		return inflater.inflate(R.layout.frag_ev_detail_info_edit_contacts, container, false);
	}



	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onActivityCreated");

		//getActivity().getActionBar().setTitle(mEvent.getTitle()); 
		getActivity().getActionBar().setTitle("Modifica Contatti"); 

		formLabel = (TextView) getActivity().findViewById(R.id.title_contacts_label);
		txtPhone= (EditText) getActivity().findViewById(R.id.phone_text);
		txtEmail = (EditText) getActivity().findViewById(R.id.email_text);
		txtWebsite = (EditText) getActivity().findViewById(R.id.website_link);
		txtFacebook = (EditText) getActivity().findViewById(R.id.facebook_link);
		txtTwitter = (EditText) getActivity().findViewById(R.id.twitter_link);

		formLabel.setText("Evento: " + mEvent.getTitle());


		if (mEvent.getContacts().containsKey("telefono")){
			List<String> telephones = (List<String>) mEvent.getContacts().get("telefono");
			//			String[] telList = (String[]) mEvent.getContacts().get("telefono"); 
			//to change when there will be more than one tel number
			String tel =telephones.get(0);		
			txtPhone.setText(tel);
		}	


		if (mEvent.getContacts().containsKey("email")){

			List<String> emails = mEvent.bringEmails(); 
			//to change when there will be more than one email
			String email = (String) emails.get(0);		
			txtEmail.setText(email);
		}

		if (mEvent.getWebsiteUrl()!=null){
			txtWebsite.setText(mEvent.getWebsiteUrl());
		}

		if (mEvent.getFacebookUrl()!=null){
			txtFacebook.setText(mEvent.getFacebookUrl());
		}

		if (mEvent.getTwitterUrl()!=null){
			txtTwitter.setText(mEvent.getTwitterUrl());
		}



		Button modifyBtn = (Button) getView().findViewById(R.id.edit_contacts_modify_button);
		modifyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {

				Toast.makeText(context, "Edited Fields: " + txtPhone.getText() + ", " + txtEmail.getText() + 
						", " + txtTwitter.getText() , Toast.LENGTH_SHORT).show();

				//set the new fields
				//				Log.i("FRAGMENT LC", "EVENT ID 2: " + mEventId);
				//				String[] newtels = (String[]) mEvent.getContacts().get("telefono");
				//				//Log.i("FRAGMENT LC", "number of events: " + Utils.appEvents.size());
				//				Log.i("FRAGMENT LC", "vecchio telefono: " + newtels[0]);
				//				ExplorerObject ev = Utils.getFakeLocalExplorerObject(Utils.appEvents, mEventId);
				//				int index = Utils.appEvents.indexOf(mEvent);
				//				int index2 = Utils.appEvents.indexOf(ev);
				//
				//				Log.i("FRAGMENT LC", "index: " + index);
				//				Log.i("FRAGMENT LC", "index 2: " + index2);

				mEvent.getContacts().clear();
				mEvent.getContacts().put("telefono", new String[]{txtPhone.getText().toString()});
				//				Map<String,Object> contacts = new HashMap<String, Object>();
				//				contacts.put("telefono", new String[]{txtPhone.getText().toString()});
				//				contacts.put("email", new String[]{txtEmail.getText().toString()});
				mEvent.saveEmails(new ArrayList<String>(){{add(txtEmail.getText().toString());}});
				//				mEvent.setContacts(contacts);

				
				try {
					new URL(txtWebsite.getText().toString());
					mEvent.setWebsiteUrl(txtWebsite.getText().toString());
				} catch (MalformedURLException e) {
					mEvent.setWebsiteUrl(null);
				}

				
				try {
					new URL(txtFacebook.getText().toString());
					mEvent.setFacebookUrl(txtFacebook.getText().toString());
				} catch (MalformedURLException e) {
					mEvent.setFacebookUrl(null);
				}


				try {
					new URL(txtTwitter.getText().toString());
					mEvent.setTwitterUrl(txtTwitter.getText().toString());
				} catch (MalformedURLException e) {
					mEvent.setTwitterUrl(null);
				}






				//persist the new contacts
				new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
				.execute(mEvent);
				//Utils.appEvents.set(index2, mEvent);

				//go back to the previous screen
				getActivity().getSupportFragmentManager().popBackStack();


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
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onStart");


	}



	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onResume");

	}



	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onPause");

	}



	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onSaveInstanceState");

	}



	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onStop");

	}



	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onDestroyView");

	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onDestroy");

	}



	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Contacts --> onDetach");

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

			return DTHelper.saveEvent(params[0]);

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
