//package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.dasapere;
//
//import java.util.ArrayList;
//import java.util.Arrays;
//import java.util.HashMap;
//import java.util.LinkedHashMap;
//import java.util.List;
//import java.util.Map;
//
//import android.app.Activity;
//import android.content.Context;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.app.ListFragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.View.OnClickListener;
//import android.view.ViewGroup;
//import android.widget.Button;
//import android.widget.Toast;
//import eu.iescities.pilot.rovereto.roveretoexplorer.R;
//import eu.iescities.pilot.rovereto.roveretoexplorer.custom.AbstractAsyncTaskProcessor;
//import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
//import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Constants;
//import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
//import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
//import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ToKnow;
//import eu.trentorise.smartcampus.android.common.SCAsyncTask;
//import eu.trentorise.smartcampus.protocolcarrier.exceptions.SecurityException;
//
//public class Fragment_EvDetail_DaSapere_OLD extends ListFragment {
//
//	private static final List<String> CUSTOM_TOKNOW_FIELDS = Arrays.asList(Constants.CUSTOM_TOKNOW_PLACE_TYPE,
//			Constants.CUSTOM_TOKNOW_ACCESS, Constants.CUSTOM_TOKNOW_CHANCE, Constants.CUSTOM_TOKNOW_LANGUAGE_MAIN,
//			Constants.CUSTOM_TOKNOW_CLOTHING, Constants.CUSTOM_TOKNOW_TO_BRING);
//
//	protected Context mContext;
//	protected String mEventId;
//	protected ExplorerObject mEvent = null;
//	private EventDetailToKnowAdapter adapter;
//
//	public static Fragment_EvDetail_DaSapere_OLD newInstance(String event_id) {
//		Fragment_EvDetail_DaSapere_OLD f = new Fragment_EvDetail_DaSapere_OLD();
//		Bundle b = new Bundle();
//		b.putString(Utils.ARG_EVENT_ID, event_id);
//		f.setArguments(b);
//		return f;
//	}
//
//	@Override
//	public void onAttach(Activity activity) {
//		super.onAttach(activity);
//		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onAttach");
//	}
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		Log.d("FRAGMENT LC", "Fragment_evDetail_Info --> onCreate");
//
//		this.mContext = this.getActivity();
//
//		if (savedInstanceState == null) {
//			Log.d("SCROLLTABS", "onCreate FIRST TIME");
//			if (getArguments() != null) {
//				mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
//				mEvent = DTHelper.findEventById(mEventId);
//			} else {
//				Log.d("SCROLLTABS", "onCreate SUBSEQUENT TIME");
//			}
//		}
//	}
//
//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//		super.onCreateView(inflater, container, savedInstanceState);
//		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onCreateView");
//		return inflater.inflate(R.layout.frag_ev_detail_dasapere, container, false);
//	}
//
//	@Override
//	public void onActivityCreated(Bundle savedInstanceState) {
//		super.onActivityCreated(savedInstanceState);
//		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onActivityCreated");
//
//		mEvent = getEvent();
//
//		adapter = new EventDetailToKnowAdapter(getActivity(), R.layout.event_toknow_row_item, getTag(), mEventId);
//		setListAdapter(adapter);
//
//		if (mEvent.getCustomData() == null) {
//			mEvent.setCustomData(new HashMap<String, Object>());
//		}
//
//		Map<String, String> toKnowMap = (Map<String, String>) mEvent.getCustomData().get(Constants.CUSTOM_TOKNOW);
//
//		if (toKnowMap == null) {
//			Map<String, Object> customData = mEvent.getCustomData();
//			customData.put(Constants.CUSTOM_TOKNOW, new LinkedHashMap<String, String>());
//			mEvent.setCustomData(customData);
//			toKnowMap = (Map<String, String>) mEvent.getCustomData().get(Constants.CUSTOM_TOKNOW);
//		}
//
//		if (toKnowMap.isEmpty()) {
//			try {
//				List<ToKnow> toKnowList = new ArrayList<ToKnow>();
//				for (String field : CUSTOM_TOKNOW_FIELDS) {
//					toKnowList.add(new ToKnow(field, ""));
//				}
//
//				// MOCKUP: fill to know map
//				// for (int i = 0; i < 15; i++) {
//				// ToKnow toKnow = new ToKnow("The title " + (i + 1),
//				// "This is the content " + (i + 1));
//				// toKnowList.add(toKnow);
//				// }
//				// MOCKUP: end
//
//				Map<String, Object> customData = new HashMap<String, Object>();
//				toKnowMap = Utils.toKnowListToMap(toKnowList);
//				customData.put(Constants.CUSTOM_TOKNOW, toKnowMap);
//				mEvent.setCustomData(customData);
//
//				// persistence
//				new SCAsyncTask<ExplorerObject, Void, Boolean>(getActivity(), new UpdateEventProcessor(getActivity()))
//						.execute(mEvent);
//			} catch (Exception e) {
//				Log.e(getClass().getName(), e.getMessage() != null ? e.getMessage() : "");
//			}
//		}
//
//		// List<ToKnow> toKnowList = Utils.toKnowMapToList((Map<String, String>)
//		// mEvent.getCustomData().get(
//		// Constants.CUSTOM_TOKNOW));
//		List<ToKnow> toKnowList = Utils.toKnowMapToList(toKnowMap);
//		adapter.addAll(toKnowList);
//		adapter.notifyDataSetChanged();
//		
//		
//		
//		
//		
//		
//		//handle the creation of new type of information by the user
//		Button toKnowAddButton = (Button) getActivity().findViewById(R.id.toKnowAddButton);
//		toKnowAddButton.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
//				Bundle args = new Bundle();
//				String frag_description = null;
//
//				Fragment editFragment = new Fragment_EvDetail_DaSapere_Form();
//				Log.i("CONTACTS", "EventDetailInfoAdapter --> event selected ID: " + mEventId + "!!");
//				args.putString(Fragment_EvDetail_DaSapere_Form.ARG_EVENT_ID, mEventId);
//				frag_description = "Fragment_EvDetail_DaSapere_Form";
//
//				editFragment.setArguments(args);
//				fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
//				// fragmentTransaction.detach(this);
//				fragmentTransaction.replace(R.id.content_frame, editFragment, frag_description);
//				fragmentTransaction.addToBackStack(getTag());
//				fragmentTransaction.commit();
//				// reset event and event id
//				// mEvent = null;
//				// mEventId = null;
//			}
//		});
//	}
//
//	@Override
//	public void onStart() {
//		super.onStart();
//		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onStart");
//	}
//
//	@Override
//	public void onResume() {
//		super.onResume();
//		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onResume");
//	}
//
//	@Override
//	public void onPause() {
//		super.onPause();
//		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onPause");
//
//	}
//
//	@Override
//	public void onStop() {
//		super.onStop();
//		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onStop");
//	}
//
//	@Override
//	public void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onSaveInstanceState");
//
//	}
//
//	@Override
//	public void onDestroyView() {
//		super.onDestroyView();
//		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onDestroyView");
//	}
//
//	@Override
//	public void onDestroy() {
//		super.onDestroy();
//		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onDestroy");
//
//	}
//
//	@Override
//	public void onDetach() {
//		super.onDetach();
//		//Log.d("FRAGMENT LC", "Fragment_evDetail_DaSapere --> onDetach");
//	}
//
//	private ExplorerObject getEvent() {
//		if (mEventId == null) {
//			mEventId = getArguments().getString(Utils.ARG_EVENT_ID);
//		}
//		mEvent = DTHelper.findEventById(mEventId);
//		return mEvent;
//	}
//
//	private class UpdateEventProcessor extends AbstractAsyncTaskProcessor<ExplorerObject, Boolean> {
//
//		public UpdateEventProcessor(Activity activity) {
//			super(activity);
//		}
//
//		@Override
//		public Boolean performAction(ExplorerObject... params) throws SecurityException, Exception {
//			// to be enabled when the connection with the server is ok
//			return DTHelper.saveEvent(params[0]);
//		}
//
//		@Override
//		public void handleResult(Boolean result) {
//			if (getActivity() != null) {
//				// getActivity().getSupportFragmentManager().popBackStack();
//
//				// if (result) {
//				// Toast.makeText(getActivity(), R.string.event_create_success,
//				// Toast.LENGTH_SHORT).show();
//				// } else {
//				// Toast.makeText(getActivity(), R.string.update_success,
//				// Toast.LENGTH_SHORT).show();
//				// }
//			}
//		}
//	}
//
//}
