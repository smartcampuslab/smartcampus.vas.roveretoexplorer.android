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
package eu.iescities.pilot.rovereto.roveretoexplorer.custom.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.ContentValues;
import android.database.Cursor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.BaseDTObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.CommunityData;
import eu.trentorise.smartcampus.android.common.Utils;

public class BaseDTStorageHelper {

	public static void setCommonFields(Cursor cursor, BaseDTObject o) {
		if (cursor != null) {
			o.setId(cursor.getString(cursor.getColumnIndex("id")));
			o.setDescription(cursor.getString(cursor.getColumnIndex("description")));
			o.setTitle(cursor.getString(cursor.getColumnIndex("title")));
			o.setSource(cursor.getString(cursor.getColumnIndex("source")));

			// set community data
			o.setCommunityData(new CommunityData());
			o.getCommunityData().setAverageRating(cursor.getInt(cursor.getColumnIndex("averageRating")));
			o.getCommunityData().setRatings(
					Utils.convertJSONToObjects(cursor.getString(cursor.getColumnIndex("ratings")), Rating.class));
			o.getCommunityData().setRatingsCount(cursor.getInt(cursor.getColumnIndex("ratingsCount")));
			o.getCommunityData().setAttendees(cursor.getInt(cursor.getColumnIndex("attendees")));
			o.getCommunityData().setAttending(
					Utils.convertJSONToObject(cursor.getString(cursor.getColumnIndex("attending")), Set.class));
			o.getCommunityData().setTags(
					Utils.convertJSONToObjects(cursor.getString(cursor.getColumnIndex("tags")), String.class));

			o.setType(cursor.getString(cursor.getColumnIndex("type")));
			o.setLocation(new double[] { cursor.getDouble(cursor.getColumnIndex("latitude")),
					cursor.getDouble(cursor.getColumnIndex("longitude")) });
			o.setFromTime(cursor.getLong(cursor.getColumnIndex("fromTime")));
			o.setToTime(cursor.getLong(cursor.getColumnIndex("toTime")));

			@SuppressWarnings("unchecked")
			Map<String, Object> map = Utils.convertJSONToObject(cursor.getString(cursor.getColumnIndex("customData")),
					Map.class);
			if (map != null && !map.isEmpty())
				o.setCustomData(map);
		}
	}

	public static ContentValues toCommonContent(BaseDTObject bean) {
		ContentValues values = new ContentValues();
		values.put("id", bean.getId());
		values.put("description", bean.getDescription());

		values.put("title", bean.getTitle());
		values.put("source", bean.getSource());

		if (bean.getCommunityData() != null) {
			if (bean.getCommunityData().getTags() != null) {
				values.put("tags", Utils.convertToJSON(bean.getCommunityData().getTags()));
			}
			values.put("notes", bean.getCommunityData().getNotes());
			values.put("averageRating", bean.getCommunityData().getAverageRating());
			values.put("ratings", Utils.convertToJSON(bean.getCommunityData().getRatings()));
			if (bean.getCommunityData().getAttending()!=null && bean.getCommunityData().getAttending().isEmpty())
				values.put("attending",(String) null);
			else values.put("attending", Utils.convertToJSON(bean.getCommunityData().getAttending()));
			values.put("attendees", bean.getCommunityData().getAttendees());
			values.put("ratingsCount", bean.getCommunityData().getRatingsCount());

		}

		values.put("type", bean.getType());

		if (bean.getLocation() != null) {
			values.put("latitude", bean.getLocation()[0]);
			values.put("longitude", bean.getLocation()[1]);
		}
		values.put("fromTime", bean.getFromTime());
		values.put("toTime", bean.getToTime());

		if (bean.getCustomData() != null && !bean.getCustomData().isEmpty()) {
			values.put("customData", Utils.convertToJSON(bean.getCustomData()));
		}
		return values;
	}

	public static Map<String, String> getCommonColumnDefinitions() {
		Map<String, String> defs = new HashMap<String, String>();
		defs.put("description", "TEXT");
		defs.put("title", "TEXT");
		defs.put("source", "TEXT");
		defs.put("creatorId", "TEXT");
		defs.put("creatorName", "TEXT");
		defs.put("tags", "TEXT");
		defs.put("notes", "TEXT");
		defs.put("averageRating", "TEXT");
		defs.put("ratings", "TEXT");
		defs.put("attending", "TEXT");
		defs.put("attendees", "INTEGER");
		defs.put("ratingsCount", "INTEGER");
		defs.put("type", "TEXT");
		defs.put("latitude", "DOUBLE");
		defs.put("longitude", "DOUBLE");
		defs.put("fromTime", "DOUBLE");
		defs.put("toTime", "DOUBLE");
		defs.put("customData", "TEXT");

		return defs;
	}

}
