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
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.StrictMode;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.CategoryHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.FlushedInputStream;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.LocalEventObject;


// in EventsListingFragment
public class EventAdapterOLD extends ArrayAdapter<LocalEventObject> {

	private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final DateFormat extDateFormat = new SimpleDateFormat("EEEEEE dd/MM/yyyy");
	private Context context;
	private int layoutResourceId;
	private int elementSelected = -1;
	private boolean postProcAndHeader = true;
	public EventAdapterOLD(Context context, int layoutResourceId, boolean postProcAndHeader) {
		super(context, layoutResourceId);
		this.context = context;
		this.layoutResourceId = layoutResourceId;
		this.postProcAndHeader = postProcAndHeader; 
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		EventPlaceholder e = null;
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(layoutResourceId, parent, false);
			e = new EventPlaceholder();
			e.title = (TextView) row.findViewById(R.id.event_placeholder_title);
			e.location = (TextView) row.findViewById(R.id.event_placeholder_loc);
			//e.hour = (TextView) row.findViewById(R.id.event_placeholder_hour);
			e.icon = (ImageView) row.findViewById(R.id.event_placeholder_photo);
			e.attendees = (TextView) row.findViewById(R.id.event_placeholder_participants);
			e.rating = (RatingBar) row.findViewById(R.id.rating_bar);

			//e.dateSeparator = (TextView) row.findViewById(R.id.date_separator);
			row.setTag(e);
		} else
		{
			e = (EventPlaceholder) row.getTag();
		}

		e.event = getItem(position);

		//****  EVENT INFO   ***** //
		//Log.i("EVENT", "title: " + e.event.getTitle() + "!!");
		//Log.i("EVENT", "rating: " + e.event.getCommunityData().getAverageRating() + "!!");
		//Log.i("EVENT", "participants: " + e.event.getAttendees() + "!!");
		//Log.i("EVENT", "location: " + (String) e.event.getCustomData().get("where_name") + "!!");
		//Log.i("EVENT", "when: " + e.event.eventDatesString() + "!!");
		//Log.i("EVENT", "image: " + e.event.getCustomData().get("event_img").toString() + "!!");

		e.title.setText(e.event.getTitle());
		e.attendees.setText(e.event.getAttendees().toString());

		//String place = Utils.getEventShortAddress(e.event);
		String place =  (String) e.event.getCustomData().get("where_name");
		e.location.setText(place);

		//e.hour.setText(e.event.eventDatesString());
		//e.hour.setText(Utils.eventDatesString(dateFormat, e.event.getFromTime(), e.event.getToTime()));
		//e.hour.setText(e.event.getTimingFormatted());



		//load the event image
		//only for debug!!! to be substituted with an AsyncTask!!!
		if (android.os.Build.VERSION.SDK_INT > 9) {
			StrictMode.ThreadPolicy policy = 
					new StrictMode.ThreadPolicy.Builder().permitAll().build();
			StrictMode.setThreadPolicy(policy);
		}
		URL img_url = null;
		Bitmap bmp = null;
		try {
			if (e.event.getCustomData().containsKey("event_img")){
				img_url = (URL) e.event.getCustomData().get("event_img");
				//Log.i("EVENT", "image url: " + img_url.toString() + "!!");
				if (img_url!=null) bmp = BitmapFactory.decodeStream(img_url.openConnection().getInputStream());}
			else{
				bmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_placeholder_photo);
			} 
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		if (bmp!=null) e.icon.setImageBitmap(bmp);




		//if (CategoryHelper.FAMILY_CATEGORY_EVENT.equals(e.event.getType()))
		//drawable = eventCertified(e.event);
		// Choose if show the separator or not

		e.rating.setRating(e.event.getCommunityData().getAverageRating());
		//e.rating.setRating(3);


		LocalEventObject event = getItem(position);

		Calendar previousEvent = null;
		Calendar currentEvent = Calendar.getInstance();
		;
		currentEvent.setTimeInMillis(event.getFromTime());


		/*
		if (position - 1 >= 0) {
			previousEvent = Calendar.getInstance();
			previousEvent.setTimeInMillis(getItem(position - 1).getFromTime());
		}

		if ((previousEvent == null || previousEvent.get(Calendar.DATE) != currentEvent.get(Calendar.DATE)) && postProcAndHeader) {
			e.dateSeparator.setVisibility(View.VISIBLE);
			// create date
			e.dateSeparator.setText(setDateString(e));
		} else {
			e.dateSeparator.setVisibility(View.GONE);
		} */

		return row;
	}


	Drawable drawable_from_url(String url, String src_name) throws java.net.MalformedURLException, java.io.IOException {
		return Drawable.createFromStream(((java.io.InputStream)new java.net.URL(url).getContent()), src_name);

	}


	private Drawable eventCertified(LocalEventObject o) {

		if (o.getCustomData()!=null && ((Boolean) o.getCustomData().get("certified"))) {
			/* se ceretificato e evento */
			return context.getResources().getDrawable(R.drawable.ic_e_family_certified);
		}

		return context.getResources().getDrawable(CategoryHelper.getIconByType(o.getType()));
	}

	private String setDateString(EventPlaceholder e) {
		String newdateformatted = new String("");

		Date dateToday = new Date();
		String stringToday = (dateFormat.format(dateToday));
		String stringEvent = (dateFormat.format(new Date(e.event.getFromTime())));

		Calendar cal = Calendar.getInstance();
		cal.setTime(dateToday);
		cal.add(Calendar.DAY_OF_YEAR, 1); // <--
		Date tomorrow = cal.getTime();
		String stringTomorrow = (dateFormat.format(tomorrow));
		// check actual date
		if (stringToday.equals(stringEvent)) {
			// if equal put the Today string
			newdateformatted = stringToday;
			newdateformatted = this.context.getString(R.string.list_event_today) + " " + newdateformatted;
		} else if (stringTomorrow.equals(stringEvent)) {
			// else if it's tomorrow, cat that string
			newdateformatted = stringTomorrow;
			newdateformatted = this.context.getString(R.string.list_event_tomorrow) + " " + newdateformatted;
		}
		// else put the day's name
		else
			newdateformatted = extDateFormat.format(new Date(e.event.getFromTime()));
		return newdateformatted;
	}

	public int getElementSelected() {
		return elementSelected;
	}

	public void setElementSelected(int elementSelected) {
		this.elementSelected = elementSelected;
	}

}
