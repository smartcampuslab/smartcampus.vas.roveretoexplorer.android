package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.multimedia;

import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Fragment_EvDetail_Multimedia extends Fragment {

	
	
	 public static Fragment_EvDetail_Multimedia newInstance(String event_id) {
		 Fragment_EvDetail_Multimedia  f = new Fragment_EvDetail_Multimedia();
		  Bundle b = new Bundle();
		  b.putString(Utils.ARG_EVENT_ID, event_id);
		  f.setArguments(b);
		  return f;
		 }
	
	
	
	
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag_ev_detail_multimedia, container, false);
    }
}

