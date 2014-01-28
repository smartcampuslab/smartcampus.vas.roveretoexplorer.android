package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.community;

import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.Fragment_EventDetails;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.Fragment_EvDetail_Info;
import eu.trentorise.smartcampus.android.common.SCAsyncTask;
import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;

public class Fragment_EvDetail_Community extends Fragment {
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	View header;
	List<String> listCommentsHeader;
	HashMap<String, List<String>> listCommentsChild;
	boolean rating_is_open = true;
	boolean attending_is_open = true;
	public ExplorerObject mEvent = null;
	private String mEventId;
	
	public static Fragment_EvDetail_Community newInstance(String event_id) {
		Fragment_EvDetail_Community  f = new Fragment_EvDetail_Community();
		Bundle b = new Bundle();
		b.putString(Fragment_EvDetail_Info.ARG_EVENT_ID, event_id);
		f.setArguments(b);
		return f;
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_ev_detail_community, container, false);
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onStart() {
		super.onStart();
		//get param
		Bundle bundle = this.getArguments();
		mEventId= bundle.getString(Fragment_EventDetails.ARG_EVENT_ID);
//		try {
//		if (mEventId!=null)
//				mEvent = DTHelper.findEventByEntityId(mEventId);
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
		setLayoutInteraction();
	}

	private void setLayoutInteraction() {
		// header part
		header = getActivity().getLayoutInflater().inflate(R.layout.frag_ev_detail_community_header, null);
		// get the listview
		expListView = (ExpandableListView) getActivity().findViewById(R.id.list_comments);
		listCommentsHeader = new ArrayList<String>();
		listCommentsChild = new HashMap<String, List<String>>();
		listCommentsHeader.add(getString(R.string.label_comments));
		List<String> comments = new ArrayList<String>();
		comments.add("comment 1");
		comments.add("comment 2");
		listCommentsChild.put(listCommentsHeader.get(0), comments); // Header,
																	// Child
																	// data

		listAdapter = new CommentAdapter(getActivity(), listCommentsHeader, listCommentsChild);

		// setting list adapter
		if (expListView.getHeaderViewsCount() == 0)
			expListView.addHeaderView(header);
		expListView.setAdapter(listAdapter);
		setHeaderInteraction(header);

	}

	private void setHeaderInteraction(View header) {

		set_attending_interaction(header);

		set_rating_interaction(header);

	}
	private ExplorerObject getEvent() {
		if (mEventId == null) {
			mEventId = getArguments().getString(Fragment_EventDetails.ARG_EVENT_ID);
		}

		if (mEvent == null) {
			mEvent = DTHelper.findEventById(mEventId);
		}


		return mEvent;
	}
	private void set_attending_interaction(View header2) {
		// set attendeees number
		TextView mAttendees = (TextView) getActivity().findViewById(R.id.attending_number);
		if (getEvent().getCommunityData()!=null){
			mAttendees.setText(getEvent().getCommunityData().getAttendees().toString());
		}
		//set attending button
		ToggleButton mAttending_my = (ToggleButton) getActivity().findViewById(R.id.attending_my);
		if (getEvent().getCommunityData().getAttending() == null || getEvent().getCommunityData().getAttending().isEmpty()) {
			mAttending_my.setBackgroundResource(R.drawable.ic_monitor_off);
			mAttending_my.setChecked(false);
		} else {
			mAttending_my.setBackgroundResource(R.drawable.ic_monitor_on);
			mAttending_my.setChecked(true);
		}

		mAttending_my.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				{
					new SCAsyncTask<Boolean, Void, ExplorerObject>(getActivity(), new AttendProcessor(
							getActivity(), buttonView)).execute(getEvent().getCommunityData().getAttending() == null
							|| getEvent().getCommunityData().getAttending().isEmpty());
				}
			}
		});
		//set visible or invisible
		final ImageView attending_open = (ImageView) header.findViewById(R.id.attending_is_open);
		final ImageView attending_close = (ImageView) header.findViewById(R.id.attending_is_close);
		final LinearLayout attending_layout = (LinearLayout) header.findViewById(R.id.attending_layout);
		attending_open.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				attending_is_open = false;
				attending_layout.setVisibility(View.GONE);
				attending_open.setVisibility(View.GONE);
				attending_close.setVisibility(View.VISIBLE);
			}
		});
		attending_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				attending_is_open = true;
				attending_layout.setVisibility(View.VISIBLE);
				attending_open.setVisibility(View.VISIBLE);
				attending_close.setVisibility(View.GONE);
			}
		});
	}

	private void set_rating_interaction(View header2) {
		final ImageView rating_open = (ImageView) header.findViewById(R.id.rating_is_open);
		final ImageView rating_close = (ImageView) header.findViewById(R.id.rating_is_close);
		final LinearLayout rating_layout = (LinearLayout) header.findViewById(R.id.rating_layout);
		rating_open.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				rating_is_open = false;
				rating_layout.setVisibility(View.GONE);
				rating_open.setVisibility(View.GONE);
				rating_close.setVisibility(View.VISIBLE);
			}
		});
		rating_close.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				rating_is_open = true;
				rating_layout.setVisibility(View.VISIBLE);
				rating_open.setVisibility(View.VISIBLE);
				rating_close.setVisibility(View.GONE);
				
			}
		});
	}
	
	private class AttendProcessor extends AbstractAsyncTaskProcessor<Boolean, ExplorerObject> {

		private CompoundButton buttonView;
		private Boolean attend;

		public AttendProcessor(Activity activity, CompoundButton buttonView) {
			super(activity);
			this.buttonView = buttonView;
		}

		@Override
		public ExplorerObject performAction(Boolean... params) throws SecurityException, Exception {
			attend = params[0];
			if (attend) {
				return DTHelper.attend(getEvent());
			}
			return DTHelper.notAttend(getEvent());
		}

		private void updateAttending() {
			TextView tv;
			if (getActivity() != null) {
				// attendees
				tv = (TextView) getActivity().findViewById(R.id.attending_number);
				if (getEvent().getCommunityData().getAttendees() != null) {
					tv.setText(getEvent().getCommunityData().getAttendees() + " ");
				} else {
					tv.setText("0 ");
				}
			}
		}
		@Override
		public void handleResult(ExplorerObject result) {
			mEvent = result;
			updateAttending();
			// getSherlockActivity().invalidateOptionsMenu();
			// LocalEventObject event = getEvent();
			if (getActivity() != null)
				if (mEvent.getCommunityData().getAttending() == null || mEvent.getCommunityData().getAttending().isEmpty()) {
					Toast.makeText(getActivity(), R.string.not_attend_success, Toast.LENGTH_SHORT).show();
					buttonView.setBackgroundResource(R.drawable.ic_monitor_off);
				} else {
					Toast.makeText(getActivity(), R.string.attend_success, Toast.LENGTH_SHORT).show();
					buttonView.setBackgroundResource(R.drawable.ic_monitor_on);
				}
		}

	}
}
