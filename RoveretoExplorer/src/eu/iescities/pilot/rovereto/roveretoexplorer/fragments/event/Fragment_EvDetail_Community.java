package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;

public class Fragment_EvDetail_Community extends Fragment {
	ExpandableListAdapter listAdapter;
	ExpandableListView expListView;
	List<String> listCommentsHeader;
	HashMap<String, List<String>> listCommentsChild;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.frag_ev_detail_community, container, false);
	}

	@Override
	public void onStart() {
		super.onStart();
		// get the listview
		expListView = (ExpandableListView) getActivity().findViewById(R.id.list_comments);
		listCommentsHeader = new ArrayList<String>();
		listCommentsChild = new HashMap<String, List<String>>();
		listCommentsHeader.add(getString(R.string.label_comments));
		List<String> comments = new ArrayList<String>();
		comments.add("comment 1");
		listCommentsChild.put(listCommentsHeader.get(0), comments); // Header, Child data

		listAdapter = new CommentAdapter(getActivity(), listCommentsHeader, listCommentsChild);

		// setting list adapter
		expListView.setAdapter(listAdapter);
	}
}
