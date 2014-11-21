package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.RoveretoExplorerApplication;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Address;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.edit.AddressSelectActivity;
import eu.trentorise.smartcampus.android.common.GeocodingAutocompletionHelper;
import eu.trentorise.smartcampus.android.common.GeocodingAutocompletionHelper.OnAddressSelectedListener;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.android.common.geo.OSMAddress;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class Fragment_EvDetail_Edit extends Fragment {
	public final static int REQUEST_CODE = 10;

	protected Context context;
	OSMAddress selectedAddress = null;

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

	public static Fragment_EvDetail_Edit newInstance(String event_id) {
		Fragment_EvDetail_Edit f = new Fragment_EvDetail_Edit();
		Bundle b = new Bundle();
		b.putString(Utils.ARG_EVENT_ID, event_id);
		f.setArguments(b);
		return f;
	}

	public static Fragment_EvDetail_Edit newInstance(String event_id,
			String event_img_url) {
		Fragment_EvDetail_Edit f = new Fragment_EvDetail_Edit();
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
		setHasOptionsMenu(true);
		Log.d("IMAGES", "Fragment_evDetail_Info --> image url:"
				+ mEventImageUrl + "!");

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onCreateView");
		return inflater.inflate(R.layout.frag_ev_detail_info_edit_list,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onActivityCreated");

	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		super.onPrepareOptionsMenu(menu);
		menu.clear();
		getActivity().getMenuInflater().inflate(
				R.menu.detail_edit_confirm_menu, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// return super.onOptionsItemSelected(item);
		if (item.getItemId() == R.id.edit_confirm) {
			// save the new event e return on the previous fragment
			saveEvent();
			return true;
		}

		return false;
	}

	private void saveEvent() {
		// new position is already saved by saveposition

		// save time
		// update Mevent
		EditText eventWhenFrom = (EditText) getActivity().findViewById(
				R.id.ev_detail_info_time_from);
		EditText eventWhenFromHour = (EditText) getActivity().findViewById(
				R.id.ev_detail_info_time_hour_from);

		String start_date = eventWhenFrom.getText().toString();
		String start_date_hour = eventWhenFromHour.getText().toString();

		mEvent.setFromTime(Utils.toDateTimeLong(
				Utils.DATE_FORMAT_2_with_dayweek_time, start_date + " "
						+ start_date_hour));
		EditText eventWhenTo = (EditText) getActivity().findViewById(
				R.id.ev_detail_info_time_to);
		EditText eventWhenToHour = (EditText) getActivity().findViewById(
				R.id.ev_detail_info_time_hour_to);

		String stop_date = eventWhenTo.getText().toString();
		String stop_date_hour = eventWhenToHour.getText().toString();
		mEvent.setToTime(Utils.toDateTimeLong(
				Utils.DATE_FORMAT_2_with_dayweek_time, stop_date + " "
						+ stop_date_hour));

		// mEvent.set
		// save phones
		List<String> phoneList = new ArrayList<String>();
		// take the first
		TextView phone = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_telephone);
		if (phone != null && !"".equals(phone.getText().toString())) {
			phoneList.add(phone.getText().toString());
		}
		LinearLayout llphone = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_telephone_extra);
		if (llphone != null && llphone.getChildCount() != 0) {
			for (int i = 0; i < llphone.getChildCount(); i++) {

				View phoneView = llphone.getChildAt(i);
				phone = (TextView) phoneView
						.findViewById(R.id.ev_detail_info_telephone_extra);
				if (phone != null && !"".equals(phone.getText().toString())) {
					phoneList.add(phone.getText().toString());
				}
			}
		}
		// take the others
		mEvent.setPhoneEmailContacts(Utils.PHONE_CONTACT_TYPE, phoneList);

		// save mails
		List<String> emailList = new ArrayList<String>();
		// take the first
		TextView email = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_mail);
		if (email != null && !"".equals(email.getText().toString())) {
			emailList.add(email.getText().toString());
		}
		LinearLayout llmail = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_mail_extra);
		if (llmail != null && llmail.getChildCount() != 0) {
			for (int i = 0; i < llmail.getChildCount(); i++) {

				View emailView = llmail.getChildAt(i);
				email = (TextView) emailView
						.findViewById(R.id.ev_detail_info_mail_extra);
				if (email != null && !"".equals(email.getText().toString())) {
					emailList.add(email.getText().toString());
				}
			}
		}
		mEvent.setPhoneEmailContacts(Utils.EMAIL_CONTACT_TYPE, emailList);

		// save website
		EditText wbUrl = (EditText) getActivity().findViewById(
				R.id.ev_detail_info_web);
		mEvent.setWebsiteUrl(wbUrl.getText().toString());
		// save facebook
		EditText fbUrl = (EditText) getActivity().findViewById(
				R.id.ev_detail_info_facebook);
		mEvent.setFacebookUrl(fbUrl.getText().toString());
		// save twitter
		EditText twitterUrl = (EditText) getActivity().findViewById(
				R.id.ev_detail_info_twitter);
		mEvent.setTwitterUrl(twitterUrl.getText().toString());
		new SCAsyncTask<Void, Void, Boolean>(getActivity(),
				new SaveEventProcessor(getActivity())).execute();

	}

	private class SaveEventProcessor extends
			AbstractAsyncTaskProcessor<Void, Boolean> {

		Activity activity;

		public SaveEventProcessor(Activity activity) {
			super(activity);
			this.activity = activity;
		}

		@Override
		public Boolean performAction(Void... params) throws SecurityException,
				Exception {
			return DTHelper.saveEvent(mEvent, activity);
		}

		@Override
		public void handleResult(Boolean result) {
			if (!result)// false is for updating true is for creating, null in
						// case of problem
			{
				// toast updated and go back
				Toast.makeText(getActivity(), R.string.update_success,
						Toast.LENGTH_LONG).show();

				getActivity().getSupportFragmentManager().popBackStack();
			}

			else {// toast problem and stay here
				Toast.makeText(getActivity(), R.string.update_failed,
						Toast.LENGTH_LONG).show();
			}
		}
	}

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

			AutoCompleteTextView placeTextView = (AutoCompleteTextView) getActivity()
					.findViewById(R.id.ev_detail_info_place);
			placeTextView.setText(addressStr);
			GeocodingAutocompletionHelper fromAutocompletionHelper = new GeocodingAutocompletionHelper(
					getActivity(), placeTextView, Utils.ROVERETO_REGION,
					Utils.ROVERETO_COUNTRY, Utils.ROVERETO_ADM_AREA,
					mEvent.getLocation());
			fromAutocompletionHelper
					.setOnAddressSelectedListener(new OnAddressSelectedListener() {
						@Override
						public void onAddressSelected(
								android.location.Address address) {

							Log.i("ADDRESS",
									"Fragment_EvDetail_Info_WhenWhere --> onAddressSelected");

							// convert from address to OsmAddress

							savePosition(Utils
									.getOsmAddressFromAddress(address));

						}

					});

		}
		ImageView imgBtn = (ImageView) getView().findViewById(
				R.id.select_where_map);

		if (imgBtn != null) {
			imgBtn.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					Intent intent = new Intent(getActivity(),
							AddressSelectActivity.class);
					// not useful but necessary because otherwise the app
					// crashes
					// in that it is dependent on line 57 of the InfoDialog
					// class of the
					// package eu.trentorise.smartcampus.android.map;
					intent.putExtra("field", Utils.ADDRESS);
					// launch the sub-activity to locate an address in the map
					startActivityForResult(intent, REQUEST_CODE);
				}
			});

		}
		// else {
		// llPlace.setVisibility(View.GONE);
		// }

		// set time
		final EditText eventWhenFrom = (EditText) getActivity().findViewById(
				R.id.ev_detail_info_time_from);

		if ((mEvent.getFromTime() != null) && (mEvent.getFromTime() != 0)) {
			String[] fromDateTime = Utils.getDateTimeString(this.context,
					mEvent.getFromTime(), Utils.DATETIME_FORMAT, false, true);

			eventWhenFrom.setText(fromDateTime[0]);

		}

		final Calendar myCalendar = Calendar.getInstance();
		final DatePickerDialog.OnDateSetListener fromDate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateLabel();
			}

			private void updateLabel() {
				eventWhenFrom.setText(Utils.DATE_FORMAT_2_with_dayweek
						.format(myCalendar.getTime()));

			}
		};

		eventWhenFrom.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new DatePickerDialog(getActivity(), fromDate, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});
		eventWhenFrom.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean isFocus) {
				if (isFocus) {
					new DatePickerDialog(getActivity(), fromDate, myCalendar
							.get(Calendar.YEAR),
							myCalendar.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				}
			}
		});

		final EditText eventWhenFromHour = (EditText) getActivity()
				.findViewById(R.id.ev_detail_info_time_hour_from);

		if ((mEvent.getFromTime() != null) && (mEvent.getFromTime() != 0)) {
			String[] fromDateTime = Utils.getDateTimeString(this.context,
					mEvent.getFromTime(), Utils.DATETIME_FORMAT, false, true);

			eventWhenFromHour.setText(fromDateTime[1]);

		}

		final TimePickerDialog.OnTimeSetListener fromHour = new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				myCalendar.set(Calendar.HOUR, hourOfDay);
				myCalendar.set(Calendar.MINUTE, minute);
				updateLabel();
			}

			private void updateLabel() {
				eventWhenFromHour.setText(Utils.DATETIME_FORMAT_HOUR
						.format(myCalendar.getTime()));
			}
		};

		eventWhenFromHour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new TimePickerDialog(getActivity(), fromHour, myCalendar
						.get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE),
						true).show();
			}
		});
		eventWhenFromHour.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean isFocus) {
				if (isFocus) {
					new TimePickerDialog(getActivity(), fromHour, myCalendar
							.get(Calendar.HOUR), myCalendar
							.get(Calendar.MINUTE), true).show();
				}
			}
		});

		final EditText eventWhenTo = (EditText) getActivity().findViewById(
				R.id.ev_detail_info_time_to);
		LinearLayout llTimeTo = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_time_to);

		if ((mEvent.getToTime() != null) && (mEvent.getToTime() != 0)) {
			String[] toDateTime = Utils.getDateTimeString(this.context,
					mEvent.getToTime(), Utils.DATETIME_FORMAT, false, true);

			// if (toDateTime[1].matches("")|| toDateTime[1]==null) {
			// llTimeTo.setVisibility(View.GONE);
			// } else
			eventWhenTo.setText(toDateTime[0]);

		}
		final DatePickerDialog.OnDateSetListener toDate = new DatePickerDialog.OnDateSetListener() {

			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear,
					int dayOfMonth) {
				// TODO Auto-generated method stub
				myCalendar.set(Calendar.YEAR, year);
				myCalendar.set(Calendar.MONTH, monthOfYear);
				myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
				updateLabel();
			}

			private void updateLabel() {

				// String myFormat = "MM/dd/yy"; //In which you need put here
				// SimpleDateFormat sdf = new SimpleDateFormat(myFormat,
				// Locale.US);

				eventWhenTo.setText(Utils.DATE_FORMAT_2_with_dayweek
						.format(myCalendar.getTime()));
			}
		};

		eventWhenTo.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new DatePickerDialog(getActivity(), toDate, myCalendar
						.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
						myCalendar.get(Calendar.DAY_OF_MONTH)).show();
			}
		});

		eventWhenTo.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean isFocus) {
				if (isFocus) {
					new DatePickerDialog(getActivity(), toDate, myCalendar
							.get(Calendar.YEAR),
							myCalendar.get(Calendar.MONTH), myCalendar
									.get(Calendar.DAY_OF_MONTH)).show();
				}
			}
		});

		final EditText eventWhenToHour = (EditText) getActivity().findViewById(
				R.id.ev_detail_info_time_hour_to);

		if ((mEvent.getToTime() != null) && (mEvent.getToTime() != 0)) {
			String[] toDateTime = Utils.getDateTimeString(this.context,
					mEvent.getToTime(), Utils.DATETIME_FORMAT, false, true);

			eventWhenToHour.setText(toDateTime[1]);

		}

		final TimePickerDialog.OnTimeSetListener toHour = new TimePickerDialog.OnTimeSetListener() {

			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				myCalendar.set(Calendar.HOUR, hourOfDay);
				myCalendar.set(Calendar.MINUTE, minute);
				updateLabel();
			}

			private void updateLabel() {
				eventWhenToHour.setText(Utils.DATETIME_FORMAT_HOUR
						.format(myCalendar.getTime()));
			}
		};

		eventWhenToHour.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				new TimePickerDialog(getActivity(), toHour, myCalendar
						.get(Calendar.HOUR), myCalendar.get(Calendar.MINUTE),
						true).show();
			}
		});
		eventWhenToHour.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean isFocus) {
				if (isFocus) {
					new TimePickerDialog(getActivity(), toHour, myCalendar
							.get(Calendar.HOUR), myCalendar
							.get(Calendar.MINUTE), true).show();
				}
			}
		});

		// set Contacts (tel,mail,fb, twitter)
		TextView eventPhone = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_telephone);
		List<String> telephones = mEvent
				.getPhoneEmailContacts(Utils.PHONE_CONTACT_TYPE);
		LinearLayout llTelephone = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_telephone_extra);
		llTelephone.removeAllViews();
		if (telephones != null) {
			eventPhone.setText(telephones.get(0));
			// fill the linearlayout with the other numbers
			if (telephones.size() > 1) {
				llTelephone.setVisibility(View.VISIBLE);
				for (int i = 1; i < telephones.size(); i++) {
					final View child = getActivity().getLayoutInflater()
							.inflate(R.layout.edit_thelephone_extra, null);
					if (llTelephone.getChildCount() <= 3) {

						llTelephone.addView(child);
						EditText phoneNumbers = (EditText) child
								.findViewById(R.id.ev_detail_info_telephone_extra);
						phoneNumbers.setText(telephones.get(i));
						// addlistener on the images for delete
						ImageView delete = (ImageView) child
								.findViewById(R.id.ev_detail_info_telephone_delete_extra);
						delete.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								LinearLayout ll = (LinearLayout) child
										.findViewById(R.id.ev_detail_info_telephone_extra_layout);
								((ViewManager) child.getParent())
										.removeView(ll);
							}
						});
					}
				}
			}

		}
		ImageView addPhones = (ImageView) getActivity().findViewById(
				R.id.ev_detail_info_telephone_add);
		addPhones.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// add a new line :images
				LinearLayout llTelephone = (LinearLayout) getActivity()
						.findViewById(
								R.id.ev_detail_placeholder_telephone_extra);
				final View child = getActivity().getLayoutInflater().inflate(
						R.layout.edit_thelephone_extra, null);
				if (llTelephone.getChildCount() <= 3) {

					llTelephone.addView(child);
					// addlistener on the images for delete
					ImageView delete = (ImageView) child
							.findViewById(R.id.ev_detail_info_telephone_delete_extra);
					delete.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							LinearLayout ll = (LinearLayout) child
									.findViewById(R.id.ev_detail_info_telephone_extra_layout);
							((ViewManager) child.getParent()).removeView(ll);
						}
					});
				}
			}
		});
		// else {
		// llTelephone.setVisibility(View.GONE);
		// }

		// mails
		TextView eventMail = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_mail);
		List<String> emails = mEvent
				.getPhoneEmailContacts(Utils.EMAIL_CONTACT_TYPE);
		LinearLayout llEmail = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_mail_extra);
		if (emails != null && emails.size() > 1) {
			llEmail.setVisibility(View.VISIBLE);
			eventMail.setText(emails.get(0));
			// fill the linearlayout with the other numbers
			if (emails.size() > 1) {
				llEmail.setVisibility(View.VISIBLE);
				for (int i = 1; i < emails.size(); i++) {
					final View child = getActivity().getLayoutInflater()
							.inflate(R.layout.edit_mail_extra, null);
					if (llEmail.getChildCount() <= 3) {

						llEmail.addView(child);
						EditText emailAddress = (EditText) child
								.findViewById(R.id.ev_detail_info_mail_extra);
						emailAddress.setText(emails.get(i));
						// addlistener on the images for delete
						ImageView delete = (ImageView) child
								.findViewById(R.id.ev_detail_info_mail_delete_extra);
						delete.setOnClickListener(new OnClickListener() {

							@Override
							public void onClick(View v) {
								LinearLayout ll = (LinearLayout) child
										.findViewById(R.id.ev_detail_info_mail_extra_layout);
								((ViewManager) child.getParent())
										.removeView(ll);
							}
						});
					}
				}
			}

		}
		ImageView addMail = (ImageView) getActivity().findViewById(
				R.id.ev_detail_info_mail_add);
		addMail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// add a new line :images
				LinearLayout llMail = (LinearLayout) getActivity()
						.findViewById(R.id.ev_detail_placeholder_mail_extra);
				final View child = getActivity().getLayoutInflater().inflate(
						R.layout.edit_mail_extra, null);
				if (llMail.getChildCount() <= 3) {
					llMail.addView(child);
					// addlistener on the images for delete
					ImageView delete = (ImageView) child
							.findViewById(R.id.ev_detail_info_mail_delete_extra);
					delete.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View v) {
							LinearLayout ll = (LinearLayout) child
									.findViewById(R.id.ev_detail_info_mail_extra_layout);
							((ViewManager) child.getParent()).removeView(ll);
						}
					});
				}

			}
		});

		// else {
		// llEmail.setVisibility(View.GONE);
		// }
		// website
		TextView eventWeb = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_web);
		String web = mEvent.getWebsiteUrl();
		LinearLayout llWeb = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_web);
		if (web != null) {
			llWeb.setVisibility(View.VISIBLE);

			eventWeb.setText(web.toString());
		}
		// else {
		// llWeb.setVisibility(View.GONE);
		// }

		// facebook

		TextView eventFacebook = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_facebook);
		String facebook = mEvent.getFacebookUrl();
		LinearLayout llFacebook = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_facebook);
		if (facebook != null && !"http://".equals(facebook)) {
			llFacebook.setVisibility(View.VISIBLE);
			eventFacebook.setText(facebook);
		}
		// else {
		// llFacebook.setVisibility(View.GONE);
		// }
		// twitter

		TextView eventTwitter = (TextView) getActivity().findViewById(
				R.id.ev_detail_info_twitter);
		String twitter = mEvent.getTwitterUrl();
		LinearLayout llTwitter = (LinearLayout) getActivity().findViewById(
				R.id.ev_detail_placeholder_twitter);
		if (twitter != null && !"http://".equals(twitter)) {
			llTwitter.setVisibility(View.VISIBLE);
			eventTwitter.setText(twitter);
		}
		// else {
		// llTwitter.setVisibility(View.GONE);
		// }
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

	@Override
	public void onActivityResult(int requestCode, int resultCode,
			Intent result_data) {

		if (resultCode == android.app.Activity.RESULT_OK
				&& requestCode == REQUEST_CODE) {
			selectedAddress = (OSMAddress) result_data
					.getSerializableExtra(Utils.ADDRESS);
			savePosition(selectedAddress);
		}
	}

	private void savePosition(OSMAddress address) {
		if (address != null) {
			AutoCompleteTextView location = (AutoCompleteTextView) getActivity()
					.findViewById(R.id.ev_detail_info_place);
			Address newAddress = new Address();
			newAddress.setCitta(address.city());
			newAddress.setVia(address.getStreet());
			mEvent.setAddress(newAddress);
			location.setText(address.getStreet() + " " + address.city());
			mEvent.setLocation(address.getLocation());
		}
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

		if (mEvent == null) {
			mEvent = DTHelper.findEventById(mEventId);
		}

		return mEvent;
	}

}
