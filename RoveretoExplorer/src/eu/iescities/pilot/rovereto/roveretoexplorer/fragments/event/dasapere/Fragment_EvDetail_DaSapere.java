package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere;

import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Fragment_EvDetail_DaSapere extends Fragment {
	
	
	
	 public static Fragment_EvDetail_DaSapere newInstance(String event_id) {
		  Fragment_EvDetail_DaSapere  f = new Fragment_EvDetail_DaSapere();
		  Bundle b = new Bundle();
		  b.putString(Utils.ARG_EVENT_ID, event_id);
		  f.setArguments(b);
		  return f;
		 }
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater,container,savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onCreateView");
		return inflater.inflate(R.layout.frag_ev_detail_dasapere, container, false);
	}




	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onAttach");

	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onCreate");
	}

	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onActivityCreated");
	}



	@Override
	public void onStart() {
		super.onStart();
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onStart");
		//mEvent = getEvent();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onResume");
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onPause");

	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onSaveInstanceState");

	}

	@Override
	public void onStop() {
		super.onStop();
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onStop");
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onDestroyView");
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onDestroy");

	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d("FRAGMENT LC","Fragment_evDetail_DaSapere --> onDetach");
	}





}



