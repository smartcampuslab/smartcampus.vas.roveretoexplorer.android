package eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model;

import java.io.Serializable;
import java.util.List;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import eu.trentorise.smartcampus.territoryservice.model.BaseDTObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;

public class TrackObject extends BaseDTObject implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3399886172947825575L;

	private List<LatLng> decodedLine = null;
	
	private String track;
	
	public List<LatLng> decodedLine() {
		if (decodedLine == null) {
			decodedLine = Utils.decodePolyline(track);
		}
		return decodedLine;
	}
	
	public LatLng startingPoint() {
		if (decodedLine() != null && !decodedLine.isEmpty()) {
			return decodedLine.get(0);
		}
		return null;
	}
	
	public double[] getLocation() {
		if (decodedLine() != null && !decodedLine.isEmpty()) {
			LatLng ll= decodedLine.get(0);
			return new double[]{ll.latitude,ll.longitude};
		}
		return null;
	}

	/**
	 * @return
	 */
	public String getTrack() {
		return track;
	}

	public void setTrack(String track) {
		this.track = track;
	}
	
	public String customDescription(Context ctx) {
		String d = getDescription();
		if (CategoryHelper.CAT_TRACK_PASSEGGIATE.equals(getType())) {
			// TODO
		}
		if (CategoryHelper.CAT_TRACK_PISTE_CICLOPEDONALI.equals(getType())) {
			// TODO
		}
		return d;
	}
	
}
