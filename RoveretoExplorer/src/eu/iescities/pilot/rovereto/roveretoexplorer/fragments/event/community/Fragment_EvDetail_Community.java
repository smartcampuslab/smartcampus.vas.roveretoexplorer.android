package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.community;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;

public class Fragment_EvDetail_Community extends Fragment {
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	View header;
	List<String> listCommentsHeader;
	HashMap<String, List<String>> listCommentsChild;
	boolean rating_is_open = true;
	boolean attending_is_open = true;

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
		// header part
		header = getActivity().getLayoutInflater().inflate(R.layout.frag_ev_detail_community_header, null);
		setHeaderInteraction(header);
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
	}

	private void setHeaderInteraction(View header) {

		set_attending_interaction(header);

		set_rating_interaction(header);

	}

	private void set_attending_interaction(View header2) {
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
}
