package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.edit;

import java.util.ArrayList;
import java.util.List;

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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.Fragment_EvDetail_Info;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class Fragment_EvDetail_Info_Tags extends Fragment {

	private Context mContext;

	public static final String ARG_EVENT_ID = "event_id";

	private ExplorerObject mEvent = null;
	private String mEventId;

	TextView formLabel;
	EditText tagToAdd;


	private ArrayList<String> tag_list = null;

	TagListAdapter tagListAdapter;

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
				Log.i("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> EVENT ID: " + mEventId);
				mEvent = DTHelper.findEventById(mEventId);
				// get event tags data
				if (mEvent.getCommunityData().getTags()!=null)
					tag_list = new ArrayList<String>(mEvent.getCommunityData().getTags());
			}
		} else {
			Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onCreate SUBSEQUENT TIME");
		}

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onCreateView");
		return inflater.inflate(R.layout.frag_ev_detail_info_edit_tags, container, false);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_Info_Tags --> onActivityCreated");
		Log.i("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> EVENT ID activity created: " + mEventId);

		if (mEvent == null) {
			Log.i("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> MY EVENT null");

			mEvent = DTHelper.findEventById(mEventId);

		}

		//set the action bar title
		getActivity().getActionBar().setTitle(
				getResources().getString(R.string.tag_add) + " " + getResources().getString(R.string.create_tags));

	
		Log.i("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> EVENT title  activity created: " + mEvent.getTitle());
		Log.i("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> TAG LIST: " + tag_list);

		formLabel = (TextView) getActivity().findViewById(R.id.form_label);
		formLabel.setText("Evento: " + mEvent.getTitle());

		tagToAdd = (EditText) getActivity().findViewById(R.id.tags_tv);

		tagListAdapter = new TagListAdapter(mContext, R.layout.frag_ev_detail_info_edit_tags_list_row, tag_list);

		ListView list = (ListView) getActivity().findViewById(R.id.tag_list);
		list.setAdapter(tagListAdapter);
		tagListAdapter.notifyDataSetChanged();
		//list.setOnItemClickListener(this);


		Button cancel = (Button) getActivity().findViewById(R.id.btn_tags_cancel);
		cancel.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				getActivity().getSupportFragmentManager().popBackStack();
			}
		});

		Button addTag = (Button) getActivity().findViewById(R.id.addTagButton);
		addTag.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				String addedTag = tagToAdd.getText().toString();
				if (addedTag != null && addedTag.trim().length() > 0) {
					tagListAdapter.add(addedTag);
					tagListAdapter.notifyDataSetChanged();
					tagToAdd.setText("");
				}
			}
		});

		Button ok = (Button) getActivity().findViewById(R.id.btn_tags_ok);
		ok.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				List<String> list = new ArrayList<String>();
				if (!tagListAdapter.isEmpty()) {
					for (int i = 0; i < tagListAdapter.getCount(); i++) {
						list.add(tagListAdapter.getItem(i));
					}
				}
				
				Log.i("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> TAG LIST button OK: " + list);

				mEvent.getCommunityData().setTags(list);
				// persist the modified field
				new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
						.execute(mEvent);
				//listener.onTagsSelected(list);
				//dismiss();
			}
		});		




	}

	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onStart");

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
		Log.d("FRAGMENT LC", "Fragment_evDetail_Info_Tags --> onSaveInstanceState");
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
