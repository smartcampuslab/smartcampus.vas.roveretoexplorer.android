package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.google.android.maps.GeoPoint;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.RoveretoExplorerApplication;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Address;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.Fragment_EventDetails;
import eu.iescities.pilot.rovereto.roveretoexplorer.log.LogHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.map.MapManager;

public class Fragment_EvDetail_Info extends Fragment {

	protected Context context;

	// For the expandable list view
	List<String> attributeGroupList;
	// private List<LocalExplorerObject> listEvents = new
	// ArrayList<LocalExplorerObject>();
	Map<String, List<String>> eventAttributeCollection;
	// ExpandableListView expListView;
	protected ExplorerObject mEvent = null;
	private EventDetailInfoAdapter eventDetailInfoAdapter;
	View header;

	public static final String ARG_INDEX = "index_adapter";

	private Integer indexAdapter;
	protected String mEventId;
	private String mEventImageUrl;

	// Initialize variables
	protected int ParentClickStatus = -1;
	protected int childClickStatus = -1;
	protected ArrayList<EventInfoParent> parents;
	protected List<Integer> groupImages;
	// private HashMap<Integer, List<Integer>> childImages;
	protected HashMap<Integer, List<Integer>> childType2Images;
	protected HashMap<Integer, Integer> childType1Images;

	Activity infoActivity = null;

	public static Fragment_EvDetail_Info newInstance(String event_id) {
		Fragment_EvDetail_Info f = new Fragment_EvDetail_Info();
		Bundle b = new Bundle();
		b.putString(Utils.ARG_EVENT_ID, event_id);
		f.setArguments(b);
		return f;
	}

	public static Fragment_EvDetail_Info newInstance(String event_id,
			String event_img_url) {
		Fragment_EvDetail_Info f = new Fragment_EvDetail_Info();
		Bundle b = new Bundle();
		b.putString(Utils.ARG_EVENT_ID, event_id);
		b.putString(Utils.ARG_EVENT_IMAGE_URL, event_img_url);
		f.setArguments(b);
		return f;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC",
				"Fragment_evDetail_Info --> onAttach: " + activity.toString());
		infoActivity = activity;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onCreate");

		this.context = this.getActivity();

		if (savedInstanceState == null) {
			Log.d("SCROLLTABS", "onCreate FIRST TIME");
			if (getArguments() != null) {
				mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
			}
		} else {
			Log.d("SCROLLTABS", "onCreate SUBSEQUENT TIME");
			mEventId = savedInstanceState.getString(Utils.ARG_EVENT_ID);
		}
		Log.d("IMAGES", "Fragment_evDetail_Info --> image url:"
				+ mEventImageUrl + "!");
		setHasOptionsMenu(true);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onCreateView");
		return inflater.inflate(R.layout.frag_ev_detail_info_list, container,
				false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onActivityCreated");

	}

	// @Override
	// public void onPrepareOptionsMenu(Menu menu) {
	// super.onPrepareOptionsMenu(menu);
	// menu.clear();
	// getActivity().getMenuInflater().inflate(R.menu.detail_edit_menu, menu);
	// }

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.event_detail_info_menu, menu);
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		getActivity().getMenuInflater().inflate(R.menu.event_detail_info_menu,
				menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.map_view) {
			ArrayList<BaseDTObject> list = new ArrayList<BaseDTObject>();
			getEvent().setLocation(mEvent.getLocation());
			list.add(getEvent());
			MapManager.switchToMapView(list, this);
			return true;
		} else if (item.getItemId() == R.id.direction_action) {
			callBringMeThere();

			return true;
		} else if (item.getItemId() == R.id.edit) {
			// call fragment edit with the event for parameter
			FragmentTransaction fragmentTransaction = getActivity()
					.getSupportFragmentManager().beginTransaction();
			Fragment_EvDetail_Edit fragment = new Fragment_EvDetail_Edit();

			Bundle args = new Bundle();

			args.putString(Utils.ARG_EVENT_ID, mEvent.getId());
			fragment.setArguments(args);

			fragmentTransaction
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
			fragmentTransaction.replace(R.id.content_frame, fragment,
					"event_edit");
			fragmentTransaction.addToBackStack(fragment.getTag());
			fragmentTransaction.commit();
		}
		return true;

	}

	protected void callBringMeThere() {
		android.location.Address to = new android.location.Address(
				Locale.getDefault());
		to.setLatitude(mEvent.getLocation()[0]);
		to.setLongitude(mEvent.getLocation()[1]);
		android.location.Address from = null;
		GeoPoint mylocation = MapManager.requestMyLocation(getActivity());
		if (mylocation != null) {
			from = new android.location.Address(Locale.getDefault());
			from.setLatitude(mylocation.getLatitudeE6() / 1E6);
			from.setLongitude(mylocation.getLongitudeE6() / 1E6);
		}
		DTHelper.bringmethere(getActivity(), from, to);
		LogHelper.sendViaggiaRequest(getActivity());
	}

	// private void editField(String field_type) {
	//
	// Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> INFO ACTIVITY: " +
	// infoActivity.toString());
	// Log.i("CONTACTS", "Fragment_EvDetail_Info --> event selected ID: " +
	// mEventId + "!!");
	//
	// String frag_description = "event_details_info_edit_" + field_type;
	// Bundle args = new Bundle();
	// args.putString(Utils.ARG_EVENT_ID, mEventId);
	// args.putString(Utils.ARG_EVENT_FIELD_TYPE, field_type);
	//
	// Fragment edit_fragment = new Fragment_EvDetail_Info_What();
	//
	// // FragmentTransaction transaction =
	// // getChildFragmentManager().beginTransaction();
	// // if (edit_fragment != null) {
	// // edit_fragment.setArguments(args);
	// // transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	// // // fragmentTransaction.detach(this);
	// // //transaction.replace(R.id.content_frame, edit_fragment,
	// // frag_description);
	// // transaction.add(edit_fragment, frag_description);
	// // //transaction.addToBackStack(edit_fragment.getTag());
	// // transaction.commit();
	// // // reset event and event id
	// // mEvent = null;
	// // mEventId = null;
	// // }
	//
	// FragmentTransaction fragmentTransaction =
	// getActivity().getSupportFragmentManager().beginTransaction();
	// if (edit_fragment != null) {
	// // reset event and event id
	// mEvent = null;
	// mEventId = null;
	// edit_fragment.setArguments(args);
	// fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
	// // fragmentTransaction.detach(this);
	// fragmentTransaction.replace(R.id.content_frame, edit_fragment,
	// frag_description);
	// fragmentTransaction.addToBackStack(getTag());
	// fragmentTransaction.commit();
	//
	// }
	//
	// }

	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onStart");
		mEvent = getEvent();
		if (mEvent != null)
			mEventImageUrl = mEvent.getImage();

		TextView titleTextView = (TextView) getActivity().findViewById(
				R.id.event_info_placeholder_title);
		titleTextView.setText(mEvent.getTitle());

		// display the event image
		ImageView imgView = (ImageView) getActivity().findViewById(
				R.id.event_placeholder_photo);

		if ((mEventImageUrl != null) && (!mEventImageUrl.matches(""))) {
			RoveretoExplorerApplication.imageLoader.displayImage(
					mEventImageUrl, imgView);
		}

		// set category

		String category = mEvent.categoryString(getActivity());
		LinearLayout llCat = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_category);

		if (category != null) {
			llCat.setVisibility(View.VISIBLE);
			TextView categoryTextView = (TextView) getActivity().findViewById(
					R.id.ev_detail_info_category);
			categoryTextView.setText(category);
		} else {
			llCat.setVisibility(View.GONE);
		}
		// set whenwhere
		String whenwhere = mEvent.getWhenWhere();
		LinearLayout llWhere = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_whenwhere);

		if (whenwhere != null) {
			llWhere.setVisibility(View.VISIBLE);
			TextView whenWhereTextView = (TextView) getActivity().findViewById(
					R.id.ev_detail_info_whenwhere);
			whenWhereTextView.setText(whenwhere);
		} else {
			llWhere.setVisibility(View.GONE);
		}
		// set place
		Address address = mEvent.getAddress();
		LinearLayout llPlace = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_place);
		if (address != null) {

			String place = (address.getLuogo() != null) ? (String) address
					.getLuogo() : null;

			String street = (address.getVia() != null) ? (String) address
					.getVia() : null;

			String city = (address.getCitta() != null) ? (String) address
					.getCitta() : null;

			String addressStr = "";

			if ((place != null) && (!place.matches("")))
				addressStr = addressStr + place;

			if ((street != null) && (!street.matches("")))
				addressStr = addressStr + ", " + street;

			if ((city != null) && (!city.matches("")))
				addressStr = addressStr + ", " + city;

			if (addressStr.startsWith(","))
				addressStr = addressStr.substring(1);

			if (addressStr.length() == 0)
				addressStr = context.getString(R.string.city_hint);

			llPlace.setVisibility(View.VISIBLE);
			TextView placeTextView = (TextView) getActivity().findViewById(
					R.id.ev_detail_info_place);
			placeTextView.setText(addressStr);

		} else {
			llPlace.setVisibility(View.GONE);
		}

		// set time
		TextView eventWhen = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_time);
		LinearLayout llTime = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_time);

		if ((mEvent.getFromTime() != null) && (mEvent.getFromTime() != 0)) {
			String[] fromDateTime = Utils.getDateTimeString(this.context,
					mEvent.getFromTime(), Utils.DATETIME_FORMAT, false, true);

			if (!fromDateTime[1].matches("")) {
				eventWhen.setText(getResources().getString(
						R.string.date_with_time, fromDateTime[0],
						fromDateTime[1]));
			} else
				eventWhen.setText(fromDateTime[0]);

		}

		if ((mEvent.getToTime() != null) && (mEvent.getToTime() != 0)) {
			String[] toDateTime = Utils.getDateTimeString(this.context,
					mEvent.getToTime(), Utils.DATETIME_FORMAT, false, true);

			if (!toDateTime[1].matches("")) {
				eventWhen.setText(eventWhen.getText()
						+ " \n"
						+ getResources().getString(R.string.date_with_time,
								toDateTime[0], toDateTime[1]));

			} else
				eventWhen.setText(eventWhen.getText() + " \n" + toDateTime[0]);
		}
		if ((mEvent.getToTime() == null) || (mEvent.getToTime() == 0)
				|| (mEvent.getFromTime() == null)
				|| (mEvent.getFromTime() == 0)) {
			llTime.setVisibility(View.GONE);

		} else {
			llTime.setVisibility(View.VISIBLE);

		}
		// set Contacts (tel,mail,fb, twitter)
		TextView eventPhone = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_telephone);
		List<String> telephones = mEvent
				.getPhoneEmailContacts(Utils.PHONE_CONTACT_TYPE);
		LinearLayout llTelephone = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_telephone);
		LinearLayout llTelephoneExtra = (LinearLayout) getActivity()
				.findViewById(R.id.ev_detail_placeholder_telephone_extra);
		if (telephones != null) {
			llTelephone.setVisibility(View.VISIBLE);
			eventPhone.setText(telephones.get(0));
			if (telephones.size() > 1) {
				llTelephoneExtra.setVisibility(View.VISIBLE);
				for (int i = 1; i < telephones.size(); i++) {
					final View child = getActivity().getLayoutInflater()
							.inflate(R.layout.edit_thelephone_extra_info, null);
					TextView phoneExtra = (TextView) child
							.findViewById(R.id.ev_detail_info_telephone_extra);
					phoneExtra.setText(telephones.get(i));
					llTelephoneExtra.addView(child);
				}
			} else {
				llTelephoneExtra.setVisibility(View.GONE);
			}
		} else {
			llTelephone.setVisibility(View.GONE);

		}
		// mails
		TextView eventMail = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_mail);
		List<String> emails = mEvent
				.getPhoneEmailContacts(Utils.EMAIL_CONTACT_TYPE);
		LinearLayout llEmail = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_mail);
		LinearLayout llEmailExtra = (LinearLayout) getActivity()
				.findViewById(R.id.ev_detail_placeholder_mail_extra);
		if (emails != null) {
			llEmail.setVisibility(View.VISIBLE);
			eventMail.setText(emails.get(0));
			if (emails.size() > 1) {
				llEmailExtra.setVisibility(View.VISIBLE);
				for (int i = 1; i < emails.size(); i++) {
					final View child = getActivity().getLayoutInflater()
							.inflate(R.layout.edit_mail_extra_info, null);
					TextView mailExtra = (TextView) child
							.findViewById(R.id.ev_detail_info_email_extra);
					mailExtra.setText(emails.get(i));
					llEmailExtra.addView(child);
				}
			} else {
				llEmailExtra.setVisibility(View.GONE);
			}
		} else {
			llEmail.setVisibility(View.GONE);
		}
		// website
		TextView eventWeb = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_web);
		String web = mEvent.getWebsiteUrl();
		LinearLayout llWeb = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_web);
		if (web != null) {
			llWeb.setVisibility(View.VISIBLE);

			eventWeb.setText(web.toString());
		} else {
			llWeb.setVisibility(View.GONE);
		}

		// facebook

		TextView eventFacebook = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_facebook);
		String facebook = mEvent.getFacebookUrl();
		LinearLayout llFacebook = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_facebook);
		if (facebook != null && !"http://".equals(facebook)) {
			llFacebook.setVisibility(View.VISIBLE);
			eventFacebook.setText(facebook);
		} else {
			llFacebook.setVisibility(View.GONE);
		}
		// twitter

		TextView eventTwitter = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_twitter);
		String twitter = mEvent.getTwitterUrl();
		LinearLayout llTwitter = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_twitter);
		if (twitter != null && !"http://".equals(twitter)) {
			llTwitter.setVisibility(View.VISIBLE);
			eventTwitter.setText(twitter);
		} else {
			llTwitter.setVisibility(View.GONE);
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onPause");

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onSaveInstanceState");
		outState.putString(Utils.ARG_EVENT_ID, mEventId);
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onDestroy");

	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onDetach");
	}


	private void setGroupImages() {

		groupImages = new ArrayList<Integer>();
		groupImages.add(R.drawable.ic_action_edit_white);
		groupImages.add(R.drawable.ic_action_edit_white);
		groupImages.add(R.drawable.ic_action_edit_white);
		groupImages.add(R.drawable.ic_action_edit_white);

	}

	protected void resetEvent() {
		mEvent = null;
		mEventId = null;
	}



	private void emptyFields(EventInfoParent parent) {
		EventInfoChild child = new EventInfoChild();
		child.setName("Description");
		child.setText(getResources().getString(R.string.event_no_information));
		child.setType(0);
		parent.getChildren().add(child);
	}

	/**
	 * Get the ExpandableListAdapter associated with this activity's
	 * ExpandableListView.
	 */
	public EventDetailInfoAdapter getExpandableListAdapter() {
		return eventDetailInfoAdapter;
	}

	public void setListAdapter(EventDetailInfoAdapter adapter) {
		eventDetailInfoAdapter = adapter;
	}

	private ExplorerObject getEvent() {
		if (mEventId == null) {
			mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
		}

		// if (mEvent == null) {
		mEvent = DTHelper.findEventById(mEventId);
		// }

		return mEvent;
	}

}
