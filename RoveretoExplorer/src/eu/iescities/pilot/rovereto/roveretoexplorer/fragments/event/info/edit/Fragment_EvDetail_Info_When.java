package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.edit;



import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
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
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;


public class Fragment_EvDetail_Info_When extends Fragment {

	private Context context;


	public static final String ARG_EVENT_ID = "event_id";


	private ExplorerObject mEvent = null;
	private String mEventId;


	TextView formLabel;
	EditText txtStartDay;
	EditText txtStartTime;
	EditText txtEndDay;
	EditText txtEndTime;
	EditText txtDuration;

	protected Date fromDate;
	protected Date fromTime;



	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onCreate");

		this.context = this.getActivity();

		if(savedInstanceState==null)
		{
			Log.d("FRAGMENT LC","onCreate FIRST TIME");
			setHasOptionsMenu(true);

			if (getArguments() != null) {
				mEventId = getArguments().getString(ARG_EVENT_ID);
				Log.i("FRAGMENT LC", "Fragment_evDetail_Info_When --> EVENT ID: " + mEventId);

				//now it will be always null so I load the fake data
				mEvent = DTHelper.findEventById(mEventId);
				//List<ExplorerObject> eventList = Utils.getFakeExplorerObjects();
				//mEvent = Utils.getFakeLocalExplorerObject(Utils.appEvents,mEventId);

			}

		}
		else
		{
			Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onCreate SUBSEQUENT TIME");
		}

	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater,container,savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onCreateView");

		return inflater.inflate(R.layout.frag_ev_detail_info_edit_when, container, false);
	}


	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onActivityCreated");


		//getActivity().getActionBar().setTitle(mEvent.getTitle()); 
		getActivity().getActionBar().setTitle("Modifica Quando"); 

		formLabel = (TextView) getActivity().findViewById(R.id.title_when_label);
		txtStartDay= (EditText) getActivity().findViewById(R.id.start_day_text);
		txtStartTime = (EditText) getActivity().findViewById(R.id.start_time_text);
		txtEndDay= (EditText) getActivity().findViewById(R.id.end_day_text);
		txtEndTime = (EditText) getActivity().findViewById(R.id.end_time_text);
		txtDuration = (EditText) getActivity().findViewById(R.id.duration_text);

		formLabel.setText("Evento: " + mEvent.getTitle());

		//get event data

		if (mEvent.getFromTime()!= null){
			String[] fromDateTime = Utils.getDateTimeString(this.context, mEvent.getFromTime(), Utils.DATETIME_FORMAT, false, false);
			Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> from Time: " + fromTime);
			txtStartDay.setText(fromDateTime[0]);
			if (!fromDateTime[1].matches(""))
				txtStartTime.setText(fromDateTime[1]);
		}
		else{
			txtStartDay.setText(getResources().getString(R.string.day_hint));
			txtStartTime.setText(getResources().getString(R.string.time_hint));
		}


		txtStartDay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {

				DialogFragment f = DatePickerDialogFragment.newInstance((EditText) v);
				//DialogFragment f = DatePickerDialogFragment.newInstance(context ,(EditText) v);
				// f.setArguments(DatePickerDialogFragment.prepareData(f.toString()));
				f.show(getActivity().getSupportFragmentManager(), "datePicker");
			}
		});

		txtStartTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment f = TimePickerDialogFragment.newInstance((EditText) v);
				f.setArguments(TimePickerDialogFragment.prepareData(txtStartTime.toString()));
				f.show(getActivity().getSupportFragmentManager(), "timePicker");

			}
		});


		if (mEvent.getToTime()!= null){
			String[] toDateTime = Utils.getDateTimeString(this.context, mEvent.getToTime(), Utils.DATETIME_FORMAT, false, false);
			Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> to Time: " + toDateTime);
			txtEndDay.setText(toDateTime[0]);			
			if (!toDateTime[1].matches(""))
				txtEndTime.setText(toDateTime[1]);
			//compute duration!!
			String duration = "3 ore";
			txtDuration.setText(duration);
		}
		else{
			txtEndDay.setText(getResources().getString(R.string.day_hint));
			txtEndTime.setText(getResources().getString(R.string.time_hint));
		}

		txtEndDay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment f = DatePickerDialogFragment.newInstance((EditText) v);
				// f.setArguments(DatePickerDialogFragment.prepareData(f.toString()));
				f.show(getActivity().getSupportFragmentManager(), "datePicker");
			}
		});

		txtEndTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment f = TimePickerDialogFragment.newInstance((EditText) v);
				f.setArguments(TimePickerDialogFragment.prepareData(txtStartTime.toString()));
				f.show(getActivity().getSupportFragmentManager(), "timePicker");

			}
		});




		Button modifyBtn = (Button) getView().findViewById(R.id.edit_contacts_modify_button);
		modifyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {


				Log.i("FRAGMENT LC", "Edited Fields: " + txtStartDay.getText() + ", " + txtStartTime.getText() + 
						", " + txtEndDay.getText() + ", " + txtEndTime.getText());

				Toast.makeText(context, "Edited Fields: " + txtStartDay.getText() + ", " + txtStartTime.getText() + 
						", " + txtEndDay.getText() + ", " + txtEndTime.getText(), Toast.LENGTH_SHORT).show();

				//set the new fields
				//ExplorerObject ev = Utils.getFakeLocalExplorerObject(Utils.appEvents, mEventId);
				//int index = Utils.appEvents.indexOf(mEvent);
				//int index2 = Utils.appEvents.indexOf(ev);

				//Log.i("FRAGMENT LC", "index: " + index);
				//Log.i("FRAGMENT LC", "index 2: " + index2);


				//validate the input
				//				if (!Utils.validFromDateTime(fromDate, fromTime)) {
				//					// Toast.makeText(getActivity(), R.string.datetime_before_now, Toast.LENGTH_SHORT).show();
				//					ValidatorHelper.highlight(getActivity(), timeEditText, getResources().getString(R.string.datetime_before_now));
				//					return;
				//				}


				if (!txtStartDay.getText().toString().matches("")){
					String start_datetime;
					if (!txtStartTime.getText().toString().matches("")){
						start_datetime= txtStartDay.getText().toString() + " " + txtStartTime.getText().toString();
						Log.i("FRAGMENT LC", "datatime inizio string: " + start_datetime);
						Log.i("FRAGMENT LC", "datatime inizio long: " + Utils.toDateTimeLong(Utils.DATETIME_FORMAT, start_datetime));
						String[] fromTime = Utils.getDateTimeString(context,  Utils.toDateTimeLong(Utils.DATETIME_FORMAT, start_datetime), Utils.DATETIME_FORMAT, false, false);
						Log.i("FRAGMENT LC", "datatime inizio string converted: " + fromTime);
						mEvent.setFromTime(Utils.toDateTimeLong(Utils.DATETIME_FORMAT, start_datetime));
					}
					else{
						start_datetime= txtStartDay.getText().toString();
						Log.i("FRAGMENT LC", "data inizio string: " + start_datetime);
						Log.i("FRAGMENT LC", "data inizio long: " + Utils.toDateTimeLong(Utils.DATE_FORMAT, start_datetime));
						String[] fromTime = Utils.getDateTimeString(context,  Utils.toDateTimeLong(Utils.DATE_FORMAT, start_datetime), Utils.DATE_FORMAT, false, false);
						Log.i("FRAGMENT LC", "data inizio string converted: " + fromTime);
						mEvent.setFromTime(Utils.toDateTimeLong(Utils.DATE_FORMAT, start_datetime));
					}
				}
				
			
				
				
				
//				SimpleDateFormat sdf = new SimpleDateFormat("MMM dd,yyyy HH:mm");		 
//				Date resultdate = null;
//				for (ExplorerObject expObj : result) {
//					resultdate =  new Date(expObj.getFromTime());
//					if (!dateGroupList.contains(expObj.getFromTime().toString()))
//					{
//
//						dateGroupList.add(sdf.format(resultdate));
//
//						eventCollection.put(sdf.format(resultdate), new ArrayList<ExplorerObject>() );
//					}
//					//aggiungi 
//					eventCollection.get(sdf.format(resultdate)).add(expObj);
//				}
				
				
				
				

				if (!txtEndDay.getText().toString().matches("")){
					String end_datetime;
					if (!txtEndTime.getText().toString().matches("")){
						end_datetime= txtEndDay.getText().toString() + " " + txtEndTime.getText().toString();
						Log.i("FRAGMENT LC", "datatime fine string: " + end_datetime);
						Log.i("FRAGMENT LC", "datatime fine long: " + Utils.toDateTimeLong(Utils.DATETIME_FORMAT, end_datetime));
						String[] toTime = Utils.getDateTimeString(context,  Utils.toDateTimeLong(Utils.DATETIME_FORMAT, end_datetime), Utils.DATETIME_FORMAT, false, false);
						Log.i("FRAGMENT LC", "datatime fine string converted: " + toTime);
						mEvent.setToTime(Utils.toDateTimeLong(Utils.DATETIME_FORMAT, end_datetime));
					}
					else{
						end_datetime= txtEndDay.getText().toString();
						Log.i("FRAGMENT LC", "data fine string: " + end_datetime);
						Log.i("FRAGMENT LC", "data fine long: " + Utils.toDateTimeLong(Utils.DATE_FORMAT, end_datetime));
						String[] toTime = Utils.getDateTimeString(context,  Utils.toDateTimeLong(Utils.DATE_FORMAT, end_datetime), Utils.DATE_FORMAT, false, false);
						Log.i("FRAGMENT LC", "data fine string converted: " + toTime);
						mEvent.setToTime(Utils.toDateTimeLong(Utils.DATE_FORMAT, end_datetime));
					}
				}



				//persist the new contacts
				new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
				.execute(mEvent);
				//Utils.appEvents.set(index, mEvent);

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



	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onStart");


	}



	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onResume");

	}



	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onPause");

	}



	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onSaveInstanceState");

	}



	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onStop");

	}



	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onDestroyView");

	}



	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onDestroy");

	}



	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_When --> onDetach");

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


	protected void setUpTimingControls() {
		Date now = new Date();
		txtStartDay = (EditText) getView().findViewById(R.id.start_day_text);

		/*check if date and time are stored*/
		if (fromDate == null) {
			txtStartDay.setTag(now);
			txtStartDay.setText(Utils.FORMAT_DATE_UI.format(now));
		} else {
			txtStartDay.setTag(fromDate);
			txtStartDay.setText(Utils.FORMAT_DATE_UI.format(fromDate));
		}

		txtStartDay.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment f = DatePickerDialogFragment.newInstance((EditText) v);
				// f.setArguments(DatePickerDialogFragment.prepareData(f.toString()));
				f.show(getActivity().getSupportFragmentManager(), "datePicker");
			}
		});

		txtStartTime = (EditText) getView().findViewById(R.id.start_time_text);

		if (fromTime == null) {
			txtStartTime.setTag(now);
			txtStartTime.setText(Utils.FORMAT_TIME_UI.format(now));
		} else {
			txtStartTime.setTag(fromTime);
			txtStartTime.setText(Utils.FORMAT_TIME_UI.format(fromTime));
		}
		txtStartTime.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				DialogFragment f = TimePickerDialogFragment.newInstance((EditText) v);
				f.setArguments(TimePickerDialogFragment.prepareData(txtStartTime.toString()));
				f.show(getActivity().getSupportFragmentManager(), "timePicker");

			}
		});
		fromDate = fromTime = now;
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
			//
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