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

import java.util.Map;

import android.content.ContentValues;
import android.database.Cursor;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.ExplorerObject;
import eu.trentorise.smartcampus.android.common.Utils;
import eu.trentorise.smartcampus.storage.db.BeanStorageHelper;

public class EventStorageHelper implements BeanStorageHelper<ExplorerObject> {

	@Override
	public ExplorerObject toBean(Cursor cursor) {

		ExplorerObject event = new ExplorerObject();
		BaseDTStorageHelper.setCommonFields(cursor, event);

		event.setWhenWhere(cursor.getString(cursor.getColumnIndex("whenWhere")));
		event.setAddress(new Address());
		event.getAddress().setLuogo(cursor.getString(cursor.getColumnIndex("luogo")));
		event.getAddress().setCitta(cursor.getString(cursor.getColumnIndex("citta")));
		event.getAddress().setVia(cursor.getString(cursor.getColumnIndex("via")));
		event.setImage(cursor.getString(cursor.getColumnIndex("image")));
		event.setWebsiteUrl(cursor.getString(cursor.getColumnIndex("websiteurl")));
		event.setFacebookUrl(cursor.getString(cursor.getColumnIndex("facebookurl")));
		event.setTwitterUrl(cursor.getString(cursor.getColumnIndex("twitterurl")));
		event.setImage(cursor.getString(cursor.getColumnIndex("origin")));
		event.setImage(cursor.getString(cursor.getColumnIndex("category")));
		@SuppressWarnings("unchecked")
		Map<String, Object> map = Utils.convertJSONToObject(cursor.getString(cursor.getColumnIndex("contacts")),
				Map.class);
		if (map != null && !map.isEmpty())
			event.setContacts(map);

		return event;
	}

	@Override
	public ContentValues toContent(ExplorerObject bean) {
		ExplorerObject event = bean;
		ContentValues values = BaseDTStorageHelper.toCommonContent(event);

		values.put("whenWhere", event.getWhenWhere());
		if (event.getAddress() != null) {
			values.put("luogo", event.getAddress().getLuogo());
			values.put("via", event.getAddress().getVia());
			values.put("citta", event.getAddress().getCitta());
		}
		values.put("image", event.getImage());
		values.put("websiteurl", event.getWebsiteUrl());
		values.put("twitterurl", event.getWebsiteUrl());
		values.put("facebookurl", event.getWebsiteUrl());
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
		defs.put("websiteurl", "TEXT");
		defs.put("twitterurl", "TEXT");
		defs.put("facebookurl", "TEXT");
		defs.put("origin", "TEXT");
		defs.put("category", "TEXT");
		defs.put("contacts", "TEXT");

		return defs;
	}

	@Override
	public boolean isSearchable() {
		return true;
	}

}
