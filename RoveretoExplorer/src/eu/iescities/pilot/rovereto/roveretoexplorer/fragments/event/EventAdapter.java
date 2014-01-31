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
import java.util.List;
import java.util.Map;

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
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Address;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
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

	public EventAdapter(Context context, int layoutResourceId, List<String> events_dates,
			Map<String, List<ExplorerObject>> eventCollections) {
		this.context = context;
		this.eventCollections = eventCollections;
		this.dateGroupList = events_dates;
		this.layoutResourceId = layoutResourceId;

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
		Log.i("IMAGE", "START ADAPTER, EVENT TITLE: " + eventPlaceHolder.event.getTitle() + "!!");
		// Log.i("IMAGE", "loaded: " + loadedImgs.toString() + "!!");

		if ((loadedImgs == null) || (!loadedImgs.contains(eventPlaceHolder.event.getTitle()))) {
			if (eventPlaceHolder.event.getImage() != null) {
				RetreiveImageTask getImgTask = new RetreiveImageTask();
				getImgTask.execute(eventPlaceHolder);
			}
		}

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

	// class to handle the network connection to rietrieve the image
	class RetreiveImageTask extends AsyncTask<EventPlaceholder, String, Bitmap> {

		protected Bitmap doInBackground(EventPlaceholder... e) {

			count++;
			EventPlaceholder ev = e[0];
			Log.i("IMAGE", "********************************");
			Log.i("IMAGE", count + "START ASYNC TASK, EVENT TITLE: " + ev.event.getTitle());

			URL img_url = null;
			Bitmap bmp = null;

			rightTextViewTitle = ev.event.getTitle();
			eventPlaceHolderForImg = ev;

			try {
				img_url = new URL(ev.event.getImage());
				// Log.i("IMAGE", "image for event " + ev.event.getTitle());
				Log.i("IMAGE", "image url: " + img_url.toString() + "!!");
				if (img_url != null) {
					bmp = BitmapFactory.decodeStream(img_url.openConnection().getInputStream());
				}
			} catch (IOException e1) {
				e1.printStackTrace();
				return null;
			}

			return bmp;
		}

		protected void onPostExecute(Bitmap bmp) {
			Log.i("IMAGE", "on post execute!" + rightTextViewTitle);
			Log.i("IMAGE", "Right Title: " + rightTextViewTitle);
			Log.i("IMAGE", "Current Title: " + eventPlaceHolder.event.getTitle());

			if (bmp != null) {
				Log.i("IMAGE", "set image icon " + rightTextViewTitle);
				Log.i("IMAGE", "event place holder title " + eventPlaceHolderForImg.title.getText());

				eventPlaceHolderForImg.icon.setImageBitmap(bmp);
				loadedImgs.add((String) eventPlaceHolderForImg.title.getText());
				Log.i("IMAGE", "loaded 2: " + loadedImgs.toString() + "!!");

			}

		}

		@Override
		protected void onProgressUpdate(String... text) {
			// Things to be done while execution of long running operation is in
			// progress. For example updating ProgessDialog
			Toast.makeText(context, "loading image...", Toast.LENGTH_LONG).show();
		}

	}

}
