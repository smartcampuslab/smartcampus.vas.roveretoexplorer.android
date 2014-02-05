/*******************************************************************************
 * Copyright 2012-2013 Trento RISE
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *        http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either   express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event;

import java.io.IOException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageLoadingListener;
import com.nostra13.universalimageloader.core.assist.SimpleImageLoadingListener;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.RatingBar.OnRatingBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.RoveretoExplorerApplication;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Address;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.Fragment_EvDetail_Info;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.edit.DatePickerDialogFragment;

import android.view.View.OnTouchListener;
import android.view.MotionEvent;


// in EventsListingFragment
public class EventAdapter extends BaseExpandableListAdapter {

	private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final DateFormat extDateFormat = new SimpleDateFormat("EEEEEE dd/MM/yyyy");
	private Context context;
	private int elementSelected = -1;
	private boolean postProcAndHeader = true;

	// for expandable list
	private Map<String, List<ExplorerObject>> eventCollections;
	private List<String> dateGroupList;
	private int layoutResourceId;

	private EventPlaceholder eventPlaceHolder = null;
	private View row = null;
	private String rightTextViewTitle = null;
	private ArrayList<String> loadedImgs = new ArrayList();
	private int count = 0;

	private EventPlaceholder eventPlaceHolderForImg = null;
	
	
	
	
	//for loading images
	private String[] eventImageUrls;
	private ImageLoadingListener animateFirstListener = new AnimateFirstDisplayListener();
	EventsListingFragment fragment;
	

	public EventAdapter(Context context, int layoutResourceId, List<String> events_dates,
			Map<String, List<ExplorerObject>> eventCollections) {
		this.context = context;
		this.eventCollections = eventCollections;
		this.dateGroupList = events_dates;
		this.layoutResourceId = layoutResourceId;

	}
	
	
	public EventAdapter(Context context, int layoutResourceId, EventsListingFragment fragment, List<String> events_dates,
			Map<String, List<ExplorerObject>> eventCollections) {
		this.context = context;
		this.eventCollections = eventCollections;
		this.dateGroupList = events_dates;
		this.layoutResourceId = layoutResourceId;
		this.fragment = fragment;
	}

	@Override
	public View getChildView(final int groupPosition, final int childPosition, boolean isLastChild, View convertView,
			ViewGroup parent) {

		final ExplorerObject event = (ExplorerObject) getChild(groupPosition, childPosition);

		row = convertView;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);
			eventPlaceHolder = new EventPlaceholder();
			eventPlaceHolder.title = (TextView) row.findViewById(R.id.event_placeholder_title);
			eventPlaceHolder.title.setTag(eventPlaceHolder.title);
			eventPlaceHolder.location = (TextView) row.findViewById(R.id.event_placeholder_loc);
			// e.hour = (TextView)
			// row.findViewById(R.id.event_placeholder_hour);
			eventPlaceHolder.icon = (ImageView) row.findViewById(R.id.event_placeholder_photo);
			eventPlaceHolder.attendees = (TextView) row.findViewById(R.id.event_placeholder_participants);
			eventPlaceHolder.rating = (RatingBar) row.findViewById(R.id.rating_bar);
			// e.dateSeparator = (TextView)
			// row.findViewById(R.id.date_separator);
			row.setTag(eventPlaceHolder);
		} else {
			eventPlaceHolder = (EventPlaceholder) row.getTag();
		}

		eventPlaceHolder.event = event;

		// **** EVENT INFO ***** //
		// Log.i("EVENT", "EVENT ID: " + eventPlaceHolder.event.getId() + "!!");
		// Log.i("EVENT", "title: " + eventPlaceHolder.event.getTitle() + "!!");
		// Log.i("EVENT", "rating: " +
		// eventPlaceHolder.event.getCommunityData().getAverageRating() + "!!");
		// Log.i("EVENT", "participants: " +
		// eventPlaceHolder.event.getCommunityData().getAttendees() + "!!");
		// Log.i("EVENT", "location: " + (String)
		// e.event.getCustomData().get("where_name") + "!!");
		// Log.i("EVENT", "when: " + e.event.eventDatesString() + "!!");
		// Log.i("EVENT", "image: " +
		// e.event.getCustomData().get("event_img").toString() + "!!");

		eventPlaceHolder.title.setText(eventPlaceHolder.event.getTitle());
		eventPlaceHolder.attendees.setText(eventPlaceHolder.event.getCommunityData().getAttendees().toString());

		Address address = eventPlaceHolder.event.getAddress();
		if (address != null) {

			String place = (address.getLuogo() != null) ? (String) address.getLuogo() : null;
			if ((place != null) && (!place.matches(""))){
				eventPlaceHolder.location.setText(place);
			}
			else 
				eventPlaceHolder.location.setText(context.getString(R.string.city_hint));
		}

		// load the event image
		Log.i("IMAGES", "START ADAPTER, EVENT TITLE: " + eventPlaceHolder.event.getTitle() + "!!");
		// Log.i("IMAGES", "loaded: " + loadedImgs.toString() + "!!");

//		if ((loadedImgs == null) || (!loadedImgs.contains(eventPlaceHolder.event.getTitle()))) {
//			if (eventPlaceHolder.event.getImage() != null) {
//				RetreiveImageTask getImgTask = new RetreiveImageTask();
//				getImgTask.execute(eventPlaceHolder);
//			}
//		}

		if (fragment.eventImagesUrls!=null){
			Log.i("IMAGES", "EventAdapter --> image array size: " + fragment.eventImagesUrls.size() );
			this.eventImageUrls = fragment.eventImagesUrls.toArray(new String[fragment.eventImagesUrls.size()]);
		}

		Log.i("IMAGES", "EventAdapter --> group position: " + groupPosition );
		Log.i("IMAGES", "EventAdapter --> child position: " + childPosition );
//		Log.i("IMAGES", "EventAdapter --> image url : " + this.eventImageUrls[childPosition] );
		
		
		//fragment.imageLoader.displayImage(this.eventImageUrls[childPosition], eventPlaceHolder.icon, fragment.imgOptions, animateFirstListener);
		
		String imgUrl = null;
		try {
			imgUrl = fragment.eventImagesUrlNew.get(dateGroupList.get(groupPosition)).get(childPosition);
		} catch (Exception e){
			e.printStackTrace();
		}
		Log.i("IMAGES", "EventAdapter --> image new url : " + imgUrl );
		
		//fragment.imageLoader.displayImage(imgUrl, eventPlaceHolder.icon, fragment.imgOptions, animateFirstListener);
		RoveretoExplorerApplication.imageLoader.displayImage(imgUrl, eventPlaceHolder.icon, fragment.imgOptions, animateFirstListener);
		
		
		
		//set the rating bar
		eventPlaceHolder.rating.setRating(eventPlaceHolder.event.getCommunityData().getAverageRating());
		
		eventPlaceHolder.rating.setOnTouchListener(new OnTouchListener() {
	        public boolean onTouch(View v, MotionEvent event) {
	            return true;
	        }
	    });	
		
		
		eventPlaceHolder.rating.setFocusable(false);
	       
		
		Calendar previousEvent = null;
		Calendar currentEvent = Calendar.getInstance();
		

		if (event.getFromTime() != null)
			currentEvent.setTimeInMillis(event.getFromTime());

		return row;
	}

	/*
	 * public int getElementSelected() { return elementSelected; }
	 * 
	 * public void setElementSelected(int elementSelected) {
	 * this.elementSelected = elementSelected; }
	 */

	// Methods needed for the Expandable adapter
	public Object getChild(int groupPosition, int childPosition) {
		return eventCollections.get(dateGroupList.get(groupPosition)).get(childPosition);
	}

	public long getChildId(int groupPosition, int childPosition) {
		return childPosition;
	}

	public int getChildrenCount(int groupPosition) {
		return eventCollections.get(dateGroupList.get(groupPosition)).size();
	}

	public Object getGroup(int groupPosition) {
		return dateGroupList.get(groupPosition);
	}

	public int getGroupCount() {
		if (dateGroupList != null)
			return dateGroupList.size();
		else return 0;
	}

	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

		String dateLabel = (String) getGroup(groupPosition);
		if (convertView == null) {
			LayoutInflater infalInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = infalInflater.inflate(R.layout.event_list_group_item, null);
		}

		convertView.setBackgroundResource(getBackgroundColor(groupPosition));

		TextView item = (TextView) convertView.findViewById(R.id.events_date);
		item.setTypeface(null, Typeface.BOLD);
		item.setText(dateLabel);

		return convertView;

	}

	public int getBackgroundColor(int groupPosition) {

		return R.color.app_green;

	}

	public boolean hasStableIds() {
		return true;
	}

	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
	
	
	
	

}
