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

import java.util.Collections;
import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.EventObjectForBean;
import eu.trentorise.smartcampus.android.common.Utils;
import eu.trentorise.smartcampus.storage.db.BeanStorageHelper;

public class EventStorageHelper implements BeanStorageHelper<EventObjectForBean> {

	@Override
	public EventObjectForBean toBean(Cursor cursor) {

		EventObjectForBean returnEventObjectForBean = new EventObjectForBean();
		EventObject event = new EventObject();
		BaseDTStorageHelper.setCommonFields(cursor, event);

		event.setWhenWhere(cursor.getString(cursor.getColumnIndex("whenWhere")));
		event.setAddress(new Address());
		event.getAddress().setCitta(cursor.getString(cursor.getColumnIndex("luogo")));
		event.getAddress().setCitta(cursor.getString(cursor.getColumnIndex("citta")));
		event.getAddress().setCitta(cursor.getString(cursor.getColumnIndex("via")));
		event.setImage(cursor.getString(cursor.getColumnIndex("image")));
		event.setImage(cursor.getString(cursor.getColumnIndex("url")));
		event.setImage(cursor.getString(cursor.getColumnIndex("origin")));
		event.setImage(cursor.getString(cursor.getColumnIndex("category")));
		@SuppressWarnings("unchecked")
		Map<String, Object> map = Utils.convertJSONToObject(cursor.getString(cursor.getColumnIndex("contacts")),
				Map.class);
		if (map != null && !map.isEmpty())
			event.setContacts(map);

		returnEventObjectForBean.setObjectForBean(event);
		return returnEventObjectForBean;
	}

	@Override
	public ContentValues toContent(EventObjectForBean bean) {
		EventObject event = bean.getObjectForBean();
		ContentValues values = BaseDTStorageHelper.toCommonContent(event);

		values.put("whenWhere", event.getWhenWhere());
		if (event.getAddress() != null) {
			values.put("luogo", event.getAddress().getLuogo());
			values.put("via", event.getAddress().getVia());
			values.put("citta", event.getAddress().getCitta());
		}
		values.put("image", event.getImage());
		values.put("url", event.getWebsiteUrl());
		values.put("origin", event.getOrigin());
		values.put("category", event.getCategory());
		if (event.getContacts()!= null) {
			values.put("contacts", Utils.convertToJSON(event.getContacts()));
		}

		return values;
	}

	@Override
	public Map<String, String> getColumnDefinitions() {
		Map<String, String> defs = BaseDTStorageHelper.getCommonColumnDefinitions();

		defs.put("whenWhere", "TEXT");
		defs.put("luogo", "TEXT");
		defs.put("via", "TEXT");
		defs.put("citta", "TEXT");
		defs.put("image", "TEXT");
		defs.put("url", "TEXT");
		defs.put("origin", "TEXT");
		defs.put("category", "TEXT");
		defs.put("contacts", "TEXT");

		// defs.put("fromTime", "INTEGER");
		// defs.put("toTime", "INTEGER");
		// defs.put("timing", "TEXT");
		// defs.put("attendees", "INTEGER");
		// defs.put("attending", "TEXT");

		// defs.put("poiIdUserDefined", "INTEGER");
		// defs.put("fromTimeUserDefined", "INTEGER");
		// defs.put("toTimeUserDefined", "INTEGER");

		return defs;
	}

	@Override
	public boolean isSearchable() {
		return true;
	}

}
