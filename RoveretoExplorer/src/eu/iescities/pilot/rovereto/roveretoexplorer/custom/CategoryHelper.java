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
package eu.iescities.pilot.rovereto.roveretoexplorer.custom;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.util.Log;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;

public class CategoryHelper {

	public static final String CAT_CULTURA = "Cultura";
	public static final String CAT_SOCIALE = "Sociale";
	public static final String CAT_SPORT = "Sport";

	private final static String TAG = "CategoryHelper";
	public static final String EVENT_NONCATEGORIZED = "Other event";

	public static final String CATEGORY_TYPE_EVENTS = "events";

	public static final String CATEGORY_TODAY = "Today";
	public static final String CATEGORY_MY = "My";
	public static final String CATEGORY_ALL = "ALL";

	public static CategoryDescriptor EVENTS_TODAY = new CategoryDescriptor(R.drawable.ic_drower_flag,
			R.drawable.ic_drower_flag, CATEGORY_TODAY, R.string.categories_event_today,"#edcc18");

	public static CategoryDescriptor EVENTS_MY = new CategoryDescriptor(R.drawable.ic_drower_flag,
			R.drawable.ic_drower_flag, CATEGORY_MY, R.string.categories_event_my, "#edcc18");
	
	public static CategoryDescriptor EVENTS_ALL = new CategoryDescriptor(R.drawable.ic_flag_events_map,
			R.drawable.ic_flag_events_map, CATEGORY_ALL, R.string.categories_event_all, "#edcc18");
	
	public static CategoryDescriptor[] EVENT_CATEGORIES = new CategoryDescriptor[] {
			/* 1 */new CategoryDescriptor(R.drawable.ic_flag_culture_map, R.drawable.ic_flag_culture_map, CAT_CULTURA,
					R.string.categories_event_cultura, "#e89a1a"),
			/* 2 */new CategoryDescriptor(R.drawable.ic_flag_sport_map, R.drawable.ic_flag_sport_map, CAT_SPORT,
					R.string.categories_event_sport, "#00b85c"),
			/* 3 */new CategoryDescriptor(R.drawable.ic_flag_person_map, R.drawable.ic_flag_person_map, CAT_SOCIALE,
					R.string.categories_event_social, "#30c3a6"),
			/* 4 */new CategoryDescriptor(R.drawable.ic_flag_other_map, R.drawable.ic_flag_other_map,
					EVENT_NONCATEGORIZED, R.string.categories_event_altri_eventi, "#1aa099"),
					

	};

	private static Map<String, String> categoryMapping = new HashMap<String, String>();

	private static Map<String, CategoryDescriptor> descriptorMap = new LinkedHashMap<String, CategoryHelper.CategoryDescriptor>();
	static {
		for (CategoryDescriptor event : EVENT_CATEGORIES) {
			descriptorMap.put(event.category, event);
		}
		
		descriptorMap.put("Ambiente", EVENT_CATEGORIES[3]);
		descriptorMap.put("NaturaMenteVino", EVENT_CATEGORIES[3]);
		descriptorMap.put("Biblioteca", EVENT_CATEGORIES[3]);
		descriptorMap.put("Urban Center", EVENT_CATEGORIES[3]);


		for (String s : descriptorMap.keySet()) {
			categoryMapping.put(s, s);
		}
		categoryMapping.put("Ambiente", EVENT_NONCATEGORIZED);
		categoryMapping.put("NaturaMenteVino", EVENT_NONCATEGORIZED);
		categoryMapping.put("Biblioteca", EVENT_NONCATEGORIZED);
		categoryMapping.put("Urban Center", EVENT_NONCATEGORIZED);

	}

	public static String[] getAllCategories(Set<String> set) {
		List<String> result = new ArrayList<String>();
		for (String key : categoryMapping.keySet()) {
			if (set.contains(categoryMapping.get(key))) {
				if (key.equals(EVENT_NONCATEGORIZED)) {

					result.add(null);
				}
				result.add(key);
				// set.remove(categoryMapping.get(key));
			}
		}
		return result.toArray(new String[result.size()]);
	}

	public static String getMainCategory(String category) {
		return categoryMapping.get(category);
	}

	public static int getMapIconByType(String type) {
		if (categoryMapping.containsKey(type))
			return descriptorMap.get(categoryMapping.get(type)).map_icon;
		return R.drawable.ic_flag_events_map;
	}

	public static int getIconByType(String type) {
		if (categoryMapping.containsKey(type))
			return descriptorMap.get(categoryMapping.get(type)).thumbnail;
		return R.drawable.ic_flag_events_map;
	}

	public static class CategoryDescriptor {
		public int map_icon;
		public int thumbnail;
		public String category;
		public int description;
		public String color;
		public CategoryDescriptor(int map_icon, int thumbnail, String category, int description, String color) {
			super();
			this.map_icon = map_icon;
			this.thumbnail = thumbnail;
			this.category = category;
			this.description = description;
			this.color = color;

		}
	}

	public static CategoryDescriptor[] getEventCategoryDescriptors() {
		return EVENT_CATEGORIES;
	}

	public static String[] getEventCategories() {
		String[] res = new String[EVENT_CATEGORIES.length];
		for (int i = 0; i < EVENT_CATEGORIES.length; i++) {
			res[i] = EVENT_CATEGORIES[i].category;
		}

		Log.i("MENU", "EVENT CATEGORIES: " + res.toString() + "\n--- lenght: " + res.length);
		return res;
	}

	public static String[] getEventCategoriesForMapFilters() {
		String[] res = new String[EVENT_CATEGORIES.length + 3];
		res[0] = EVENTS_MY.category;
		res[1] = EVENTS_TODAY.category;
		res[2] = EVENTS_ALL.category;

		for (int i = 3; i < EVENT_CATEGORIES.length + 3; i++) {
			res[i] = EVENT_CATEGORIES[i - 3].category;
		}

		Log.i("MENU", "EVENT CATEGORIES: " + res.toString() + "\n--- lenght: " + res.length);
		return res;
	}

	public static CategoryDescriptor[] getEventCategoryDescriptorsFiltered() {
		return DTParamsHelper.getInstance().getFilteredArrayByParams(EVENT_CATEGORIES, CATEGORY_TYPE_EVENTS);
	}

	public static CategoryDescriptor getCategoryDescriptorByCategoryFiltered(String type, String cat) {
		if (descriptorMap.containsKey(cat)){
			return descriptorMap.get(cat);
		}
		return descriptorMap.get(EVENT_NONCATEGORIZED);
	}

}
