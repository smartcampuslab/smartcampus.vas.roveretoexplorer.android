package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Constants;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.edit.EditFieldListAdapter;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class Fragment_EvDetail_DaSapere_edit extends Fragment {

	private Context mContext;

	public static final String ARG_EVENT_ID = "event_id";

	private ExplorerObject mEvent = null;
	private String mEventId;

	private Map<String, List<String>> toKnowMap = null;

	TextView formLabel;
	EditText newFieldTypeText;
	EditText valueToAdd;
	ImageView iconImage;
	TextView eventFieldLabel;

	private ArrayList<String> value_list = new ArrayList<String>();
	EditFieldListAdapter valueListAdapter;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onAttach");
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onCreate");

		this.mContext = this.getActivity();

		if (savedInstanceState == null) {
			Log.d("FRAGMENT LC", "onCreate FIRST TIME");
			setHasOptionsMenu(true);

			if (getArguments() != null) {
				mEventId = getArguments().getString(ARG_EVENT_ID);
				Log.i("FRAGMENT LC",
						"Fragment_evDetail_Info_Tags --> EVENT ID: " + mEventId);
				mEvent = DTHelper.findEventById(mEventId);

				// get event "toknow" custom data
				toKnowMap = Utils.getCustomToKnowDataFromEvent(mEvent);

			}
		} else {
			Log.d("FRAGMENT LC",
					"Fragment_EvDetail_Edit_MultiValueField --> onCreate SUBSEQUENT TIME");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d("FRAGMENT LC",
				"Fragment_EvDetail_Edit_MultiValueField --> onCreateView");
		return inflater.inflate(R.layout.frag_ev_detail_dasapere_edit,
				container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

	}

	public void missingFieldTypeValuesAlert() {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		// set msg to show
		builder.setMessage(getResources().getString(
				R.string.missing_fieldtype_values_alert,
				newFieldTypeText.getText().toString()));
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();
					}
				});

		builder.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						getActivity().getSupportFragmentManager()
								.popBackStack();

					}
				});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	public void missingFieldTypeAlert() {

		AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		// set msg to show
		builder.setMessage(getResources().getString(
				R.string.missing_fieldtype_alert,
				newFieldTypeText.getText().toString()));
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						dialog.cancel();
					}
				});

		builder.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {

						getActivity().getSupportFragmentManager()
								.popBackStack();

					}
				});

		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}

	@Override
	public void onStart() {
		super.onStart();
		Map<String, List<String>> toKnow = Utils.getToKnowEventData(mEvent);

		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onStart");
		// fill the data
		EditText placeEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_place_type);

		if (toKnow.containsKey(Constants.CUSTOM_TOKNOW_PLACE_TYPE)
				&& toKnow.get(Constants.CUSTOM_TOKNOW_PLACE_TYPE).size() > 0) {
			placeEdit.setText(toKnow.get(Constants.CUSTOM_TOKNOW_PLACE_TYPE)
					.get(0));
		} else {
			placeEdit.setText("");
		}
		// access
		EditText accessEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_access_type);

		if (toKnow.containsKey(Constants.CUSTOM_TOKNOW_ACCESS)
				&& toKnow.get(Constants.CUSTOM_TOKNOW_ACCESS).size() > 0) {
			accessEdit.setText(toKnow.get(Constants.CUSTOM_TOKNOW_ACCESS)
					.get(0));
		} else {
			accessEdit.setText("");

		}

		// main language
		EditText languageEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_language_type);

		if (toKnow.containsKey(Constants.CUSTOM_TOKNOW_LANGUAGE_MAIN)
				&& toKnow.get(Constants.CUSTOM_TOKNOW_LANGUAGE_MAIN).size() > 0) {
			languageEdit.setText(toKnow.get(
					Constants.CUSTOM_TOKNOW_LANGUAGE_MAIN).get(0));
		} else {
			languageEdit.setText("");

		}

		// dress code
		EditText dresscodeEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_dresscode_type);

		if (toKnow.containsKey(Constants.CUSTOM_TOKNOW_CLOTHING)
				&& toKnow.get(Constants.CUSTOM_TOKNOW_CLOTHING).size() > 0) {
			dresscodeEdit.setText(toKnow.get(Constants.CUSTOM_TOKNOW_CLOTHING)
					.get(0));
		} else {
			dresscodeEdit.setText("");

		}

		// dress probability
		EditText probabilityEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_probability_type);

		if (toKnow.containsKey(Constants.CUSTOM_TOKNOW_CHANCE)
				&& toKnow.get(Constants.CUSTOM_TOKNOW_CHANCE).size() > 0) {
			probabilityEdit.setText(toKnow.get(Constants.CUSTOM_TOKNOW_CHANCE)
					.get(0));
		} else {
			probabilityEdit.setText("");

		}

		// to bring

		EditText tobringEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_tobring);

		if (toKnow.containsKey(Constants.CUSTOM_TOKNOW_TO_BRING)
				&& toKnow.get(Constants.CUSTOM_TOKNOW_TO_BRING).size() > 0) {
			tobringEdit.setText(toKnow.get(Constants.CUSTOM_TOKNOW_TO_BRING)
					.get(0));
		} else {
			tobringEdit.setText("");

		}
		// add the extra field already present in the
		final LinearLayout dasapereExtraLl = (LinearLayout) getActivity()
				.findViewById(R.id.ev_detail_dasapere_extra_type_placeholder);
		// for all the values not in a set
		for (String key : toKnow.keySet()) {
			if (!Utils.isDefaultExtraField(key) && !key.isEmpty()) {
				final View newField = getActivity().getLayoutInflater()
						.inflate(R.layout.edit_dasapere_extra, null);
				ImageView deleteExtra = (ImageView) newField
						.findViewById(R.id.delete_dasapere_extra);
				deleteExtra.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dasapereExtraLl.removeView(newField);
					}
				});
				dasapereExtraLl.addView(newField);
				EditText nameExtra = (EditText) newField
						.findViewById(R.id.ev_detail_dasapere_name_extra);
				nameExtra.setText(key);
				EditText contentExtra = (EditText) newField
						.findViewById(R.id.ev_detail_dasapere_content_extra);
				contentExtra.setText(toKnow.get(key).get(0));
			}

		}

		Button addField = (Button) getActivity().findViewById(
				R.id.toKnowAddButton);
		addField.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// add a new empty line (if the previous if filled)
				final LinearLayout dasapereExtraLl = (LinearLayout) getActivity()
						.findViewById(
								R.id.ev_detail_dasapere_extra_type_placeholder);
				for (int i = 0; i < dasapereExtraLl.getChildCount(); i++) {
					View child = dasapereExtraLl.getChildAt(i);
					EditText nameExtra = (EditText) child
							.findViewById(R.id.ev_detail_dasapere_name_extra);
					EditText contentExtra = (EditText) child
							.findViewById(R.id.ev_detail_dasapere_content_extra);
					if (nameExtra.getText().toString().isEmpty()
							|| contentExtra.getText().toString().isEmpty()) {
						// toast "fill the fields" and return
						Toast.makeText(getActivity(),
								R.string.warning_fill_fields, Toast.LENGTH_LONG)
								.show();
						return;
					}

				}
				// if every fields are filled, add another one
				final View newField = getActivity().getLayoutInflater()
						.inflate(R.layout.edit_dasapere_extra, null);
				ImageView deleteExtra = (ImageView) newField
						.findViewById(R.id.delete_dasapere_extra);
				deleteExtra.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						dasapereExtraLl.removeView(newField);
					}
				});
				dasapereExtraLl.addView(newField);
			}
		});

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
		// set new fields and save event

		Map<String, List<String>> toKnowMap = Utils
				.getCustomToKnowDataFromEvent(mEvent);
		toKnowMap.clear();
		// for every key I have a list of field(but I use only the first)
		// List<String> list = new ArrayList<String>();

		// fix part
		EditText placeEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_place_type);
		if (placeEdit != null) {
			toKnowMap.put(Constants.CUSTOM_TOKNOW_PLACE_TYPE,
					Arrays.asList(placeEdit.getText().toString()));
		}
		EditText accessEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_access_type);
		if (accessEdit != null) {
			toKnowMap.put(Constants.CUSTOM_TOKNOW_ACCESS,
					Arrays.asList(accessEdit.getText().toString()));
		}
		EditText changeEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_probability_type);
		if (changeEdit != null) {
			toKnowMap.put(Constants.CUSTOM_TOKNOW_CHANCE,
					Arrays.asList(changeEdit.getText().toString()));
		}
		EditText clothingEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_dresscode_type);
		if (clothingEdit != null) {
			toKnowMap.put(Constants.CUSTOM_TOKNOW_CLOTHING,
					Arrays.asList(clothingEdit.getText().toString()));
		}
		EditText languageEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_language_type);
		if (languageEdit != null) {
			toKnowMap.put(Constants.CUSTOM_TOKNOW_LANGUAGE_MAIN,
					Arrays.asList(languageEdit.getText().toString()));
		}
		EditText tobringEdit = (EditText) getActivity().findViewById(
				R.id.ev_detail_dasapere_tobring);
		if (languageEdit != null) {
			toKnowMap.put(Constants.CUSTOM_TOKNOW_TO_BRING,
					Arrays.asList(tobringEdit.getText().toString()));
		}
		// save variable part
		final LinearLayout dasapereExtraLl = (LinearLayout) getActivity()
				.findViewById(R.id.ev_detail_dasapere_extra_type_placeholder);
		dasapereExtraLl.removeAllViews();
		for (int i = 0; i < dasapereExtraLl.getChildCount(); i++) {
			View child = dasapereExtraLl.getChildAt(i);
			EditText nameExtra = (EditText) child
					.findViewById(R.id.ev_detail_dasapere_name_extra);
			EditText contentExtra = (EditText) child
					.findViewById(R.id.ev_detail_dasapere_content_extra);
			if (nameExtra != null && contentExtra != null) {
				if (contentExtra.getText().toString().isEmpty()
						|| nameExtra.getText().toString().isEmpty()) {
					Toast.makeText(getActivity(),
							getString(R.string.warning_fill_fields),
							Toast.LENGTH_LONG).show();
					return;
				}
				toKnowMap.put(nameExtra.getText().toString(),
						Arrays.asList(contentExtra.getText().toString()));
			}
		}

		Map<String, Object> customData = mEvent.getCustomData();
		customData.put(Constants.CUSTOM_TOKNOW, toKnowMap);
		mEvent.setCustomData(customData);

		// persist the modified field
		// new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(),
		// new UpdateEventProcessor(getActivity())).execute(mEvent);
		// }
		// }

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

			// toast problem and stay here
			else {
				Toast.makeText(getActivity(), R.string.update_failed,
						Toast.LENGTH_LONG).show();
			}
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onResume");

	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onPause");
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC",
				"Fragment_evDetail_Info_Tags --> onSaveInstanceState");
	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onDetach");
	}

	// @Override
	// public void onPrepareOptionsMenu(Menu menu) {
	// Log.i("FRAGMENT LC",
	// "start on Prepare Options Menu EVENT LISTING frag: "
	// + menu.toString());
	// menu.clear();
	//
	// // getActivity().getMenuInflater().inflate(R.menu.event_detail_menu,
	// // menu);
	//
	// /*
	// * if (category == null) { category = (getArguments() != null) ?
	// * getArguments().getString(SearchFragment.ARG_CATEGORY) : null; }
	// */
	// super.onPrepareOptionsMenu(menu);
	// }

	// private class UpdateEventProcessor extends
	// AbstractAsyncTaskProcessor<ExplorerObject, Boolean> {
	//
	// public UpdateEventProcessor(Activity activity) {
	// super(activity);
	// }
	//
	// @Override
	// public Boolean performAction(ExplorerObject... params)
	// throws SecurityException, Exception {
	// // to be enabled when the connection with the server is ok
	// return DTHelper.saveEvent(params[0], mContext);
	// // store the modified event
	// // int index = Utils.appEvents.indexOf(params[0]);
	// // Utils.appEvents.set(index, params[0]);
	// // ExplorerObject mNewEvent = Utils.appEvents.get(index);
	// // return true;
	// }
	//
	// @Override
	// public void handleResult(Boolean result) {
	// if (getActivity() != null) {
	// getActivity().getSupportFragmentManager().popBackStack();
	//
	// if (result) {
	// Toast.makeText(getActivity(),
	// R.string.event_create_success, Toast.LENGTH_SHORT)
	// .show();
	// } else {
	// Toast.makeText(getActivity(), R.string.update_success,
	// Toast.LENGTH_SHORT).show();
	// }
	// }
	// }
	// }

}
