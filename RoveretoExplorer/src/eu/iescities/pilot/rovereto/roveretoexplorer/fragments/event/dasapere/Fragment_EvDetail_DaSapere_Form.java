package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere;

import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Constants;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ToKnow;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class Fragment_EvDetail_DaSapere_Form extends Fragment {

	private Context mContext;

	public static final String ARG_EVENT_ID = "event_id";
	public static final String ARG_TOKNOW_INDEX = "index";

	private String mEventId;
	private ExplorerObject mEvent = null;
	private Integer toKnowIndex = null;
	private ToKnow toKnow;

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
				if (getArguments().containsKey(ARG_TOKNOW_INDEX)) {
					toKnowIndex = getArguments().getInt(ARG_TOKNOW_INDEX);
				}
			}
		} else {
			Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onCreate SUBSEQUENT TIME");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onCreateView");
		return inflater.inflate(R.layout.frag_ev_detail_dasapere_edit, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_What --> onActivityCreated");

		final List<ToKnow> list = Utils.toKnowMapToList((Map<String, String>) mEvent.getCustomData().get(
				Constants.CUSTOM_TOKNOW));

		TextView toKnowLabelTextView = (TextView) getActivity().findViewById(R.id.toknow_label_textview);
		final EditText toKnowTitleEditText = (EditText) getActivity().findViewById(R.id.toknow_edittext_title);
		final EditText toKnowContentEditText = (EditText) getActivity().findViewById(R.id.toknow_edittext_content);

		if (toKnowIndex != null) {
			toKnow = list.get(toKnowIndex);
			toKnowLabelTextView.setText(R.string.toknow_label_edit);

			if (toKnow.getTitle().startsWith("_toknow_")) {
				Integer resId = getActivity().getResources().getIdentifier(toKnow.getTitle(), "string",
						"eu.iescities.pilot.rovereto.roveretoexplorer");
				if (resId != null && resId != 0) {
					String mandatoryTitle = getActivity().getResources().getString(resId);
					toKnowTitleEditText.setText(mandatoryTitle);
					toKnowTitleEditText.setInputType(InputType.TYPE_NULL);
				}
			} else {
				toKnowTitleEditText.setText(toKnow.getTitle());
			}

			toKnowContentEditText.setText(toKnow.getContent());
		}

		Button cancelButton = (Button) getActivity().findViewById(R.id.edit_contacts_cancel_button);
		cancelButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});

		Button okButton = (Button) getActivity().findViewById(R.id.edit_contacts_modify_button);
		okButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
						toKnowTitleEditText.getWindowToken(), 0);

				if (toKnowTitleEditText.getText().toString().trim().length() == 0) {
					Toast.makeText(getActivity(), R.string.toknow_toast_title_empty, Toast.LENGTH_SHORT).show();
				} else {
					if (toKnow == null) {
						toKnow = new ToKnow("", "");
						list.add(toKnow);
					}

					if (!toKnow.getTitle().startsWith("_toknow_")) {
						toKnow.setTitle(toKnowTitleEditText.getText().toString().trim());
					}
					toKnow.setContent(toKnowContentEditText.getText().toString().trim());
					mEvent.getCustomData().put(Constants.CUSTOM_TOKNOW, Utils.toKnowListToMap(list));
					new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
							.execute(mEvent);
				}
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
		}

		@Override
		public void handleResult(Boolean result) {
			if (getActivity() != null) {
				if (result) {
					Toast.makeText(getActivity(), R.string.event_create_success, Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(getActivity(), R.string.update_success, Toast.LENGTH_SHORT).show();
				}

				getActivity().getSupportFragmentManager().popBackStack();
			}
		}
	}

}