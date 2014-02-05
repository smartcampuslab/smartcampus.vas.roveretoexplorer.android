package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.edit;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
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

public class Fragment_EvDetail_Info_What extends Fragment {

	private Context mContext;

	public static final String ARG_EVENT_ID = "event_id";

	private ExplorerObject mEvent = null;
	private String mEventId;
	private String mEventFieldType;

	TextView formLabel;
	TextView eventFieldLabel;
	EditText txtEventField;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onCreate");

		this.mContext = this.getActivity();

		if (savedInstanceState == null) {
			Log.d("FRAGMENT LC", "onCreate FIRST TIME");
			setHasOptionsMenu(true);

			if (getArguments() != null) {
				mEventId = getArguments().getString(ARG_EVENT_ID);
				Log.i("FRAGMENT LC", "Fragment_evDetail_Info_What --> EVENT ID: " + mEventId);
				mEvent = DTHelper.findEventById(mEventId);

				mEventFieldType = getArguments().getString(Utils.ARG_EVENT_FIELD_TYPE);
				Log.i("FRAGMENT LC", "Fragment_evDetail_Info_What --> EVENT FIELD TYPE: " + mEventFieldType);
			}
		} else {
			Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onCreate SUBSEQUENT TIME");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onCreateView");
		return inflater.inflate(R.layout.frag_ev_detail_info_edit_what, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onActivityCreated");

		if (mEvent == null) {
			mEvent = DTHelper.findEventById(mEventId);
		}

		formLabel = (TextView) getActivity().findViewById(R.id.form_label);
		formLabel.setText("Evento: " + mEvent.getTitle());
		eventFieldLabel = (TextView) getActivity().findViewById(R.id.event_field_label);
		txtEventField = (EditText) getActivity().findViewById(R.id.event_field_text);

		if (mEventFieldType.equals("description")) {
			getActivity().getActionBar().setTitle(
					getResources().getString(R.string.modify) + " " + getResources().getString(R.string.what_txt));
			eventFieldLabel.setText(getResources().getString(R.string.what_txt));
			// get event data
			if ((mEvent.getDescription() != null) && (!mEvent.getDescription().matches(""))) {
				txtEventField.setText(Html.fromHtml(mEvent.getDescription()));
			}
		}
		if (mEventFieldType.equals("origin")) {
			getActivity().getActionBar().setTitle(
					getResources().getString(R.string.modify) + " " + getResources().getString(R.string.origin_txt));
			eventFieldLabel.setText(getResources().getString(R.string.origin_txt));
			// get event data
			if ((mEvent.getOrigin() != null) && (!mEvent.getOrigin().matches(""))) {
				txtEventField.setText(mEvent.getOrigin());
			}
		}
		if (mEventFieldType.equals("title")) {
			getActivity().getActionBar().setTitle(
					getResources().getString(R.string.modify) + " " + getResources().getString(R.string.title_txt));
			eventFieldLabel.setText(getResources().getString(R.string.title_txt));
			// get event data
			if ((mEvent.getTitle() != null) && (!mEvent.getTitle().matches(""))) {
				txtEventField.setText(mEvent.getTitle());
			}

			txtEventField.addTextChangedListener(new TextWatcher() {
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count, int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
					if (txtEventField.getText().length() >= 0) {
						formLabel.setText("Evento: " + txtEventField.getText().toString());
						// formLabel.setText(txtEventField.getText().toString());
					}
				}
			});

		}

		Button modifyBtn = (Button) getView().findViewById(R.id.edit_field_modify_button);
		modifyBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View view) {
				// Toast.makeText(context, "Edited Fields: " +
				// txtEventField.getText(), Toast.LENGTH_SHORT).show();

				// set the new fields
				if (mEventFieldType.equals("description")) {
					mEvent.setDescription(txtEventField.getText().toString());
				}
				if (mEventFieldType.equals("origin")) {
					mEvent.setOrigin(txtEventField.getText().toString());
				}
				if (mEventFieldType.equals("title")) {
					mEvent.setTitle(txtEventField.getText().toString());
				}

				((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
						txtEventField.getWindowToken(), 0);
				
				// persist the modified field
				new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
						.execute(mEvent);
				// Utils.appEvents.set(index2, mEvent);
			}
		});

		Button cancelBtn = (Button) getView().findViewById(R.id.edit_field_cancel_button);
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
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onStart");

	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onResume");

	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onPause");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onSaveInstanceState");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onDetach");
	}

	@Override
	public void onPrepareOptionsMenu(Menu menu) {
		Log.i("FRAGMENT LC", "start on Prepare Options Menu EVENT LISTING frag: " + menu.toString());
		menu.clear();

		// getActivity().getMenuInflater().inflate(R.menu.event_detail_menu,
		// menu);

		/*
		 * if (category == null) { category = (getArguments() != null) ?
		 * getArguments().getString(SearchFragment.ARG_CATEGORY) : null; }
		 */
		super.onPrepareOptionsMenu(menu);
	}

	private class UpdateEventProcessor extends AbstractAsyncTaskProcessor<ExplorerObject, Boolean> {

		public UpdateEventProcessor(Activity activity) {
			super(activity);
		}

		@Override
		public Boolean performAction(ExplorerObject... params) throws SecurityException, Exception {
			// to be enabled when the connection with the server is ok
			return DTHelper.saveEvent(params[0]);
			// store the modified event
			// int index = Utils.appEvents.indexOf(params[0]);
			// Utils.appEvents.set(index, params[0]);
			// ExplorerObject mNewEvent = Utils.appEvents.get(index);
			// return true;
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