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
import eu.trentorise.smartcampus.territoryservice.model.BaseDTObject;

public class CategoryHelper {
	
	public static final String CAT_TRACK_PISTA_CICLABILE = "Pista ciclabile";
	public static final String CAT_TRACK_PASSEGGIATE = "Passeggiate";
	public static final String CAT_TRACK_PISTE_CICLOPEDONALI = "Piste ciclopedonali";
	
	public static final String CAT_INFO_DISTRETTI_E_ORGANIZZAZIONI = "Distretti e organizzazioni";
	public static final String CAT_INFO_POLITICHE_DEI_DISTRETTI = "Politiche dei distretti";
	public static final String CAT_INFO_VALUTATORI_AUDIT = "Valutatori \"Audit\"";
	public static final String CAT_INFO_CERTIFICATORI_AUDIT = "Certificatori \"Audit\"";
	public static final String CAT_INFO_POLITICHE_PROVINCIALI = "Politiche provinciali";
	public static final String CAT_INFO_NOTIZIE = "Notizie";

	public static final String CAT_POI_FAMILY_IN_TRENTINO = "\"Family in Trentino\"";
	public static final String CAT_POI_TAVOLO_NUOVI_MEDIA = "Tavolo \"Nuovi Media\"";
	public static final String CAT_POI_PUNTI_ALLATTAMENTO = "Punti allattamento";
	public static final String CAT_POI_VACANZE_AL_MARE = "Vacanze al mare";
	public static final String CAT_POI_FAMILY_AUDIT = "\"Family Audit\"";

	public static final String CAT_EVENT_ALTO_GARDA = "Alto Garda";
	public static final String CAT_EVENT_ESTATE_GIOVANI_E_FAMIGLIA = "Estate giovani e famiglia";
	public static final String CAT_EVENT_CULTURA = "Cultura";
	public static final String CAT_EVENT_SPORT = "Sport";
	public static final String CAT_EVENT_SVAGO = "Svago";
	public static final String CAT_EVENT_ALTRI_EVENTI = "Altri eventi";
	
	private final static String TAG = "CategoryHelper";
	// private static final String POI_NONCATEGORIZED = "Other place";
	// private static final String EVENT_NONCATEGORIZED = "Other event";
	// private static final String STORY_NONCATEGORIZED = "Other story";

	public static final String CATEGORY_TYPE_POIS = "pois";
	public static final String CATEGORY_TYPE_EVENTS = "events";
	public static final String CATEGORY_TYPE_INFOS = "infos";
	public static final String CATEGORY_TYPE_TRACKS = "tracks";

	public static final String FAMILY_CATEGORY_POI = "Family - Organizations";
	public static final String FAMILY_CATEGORY_EVENT = "Family";

	public static final String CATEGORY_TODAY = "Today";
	public static final String CATEGORY_MY = "My";

	public static CategoryDescriptor[] EVENT_CATEGORIES = new CategoryDescriptor[] {
		   /* 1 */ new CategoryDescriptor(R.drawable.ic_estate_map, R.drawable.ic_summer,
					CAT_EVENT_ESTATE_GIOVANI_E_FAMIGLIA, R.string.categories_event_summer_family),
		   /* 2 */ new CategoryDescriptor(R.drawable.ic_altogarda_map, R.drawable.ic_altogarda, 
					CAT_EVENT_ALTO_GARDA, R.string.categories_event_alto_garda),
		  /* 3 */ new CategoryDescriptor(R.drawable.ic_altogarda_map, R.drawable.ic_altogarda, 
				  CAT_EVENT_ALTO_GARDA, R.string.categories_event_cultura),
          /* 4 */ new CategoryDescriptor(R.drawable.ic_altogarda_map, R.drawable.ic_altogarda, 
        		  CAT_EVENT_ALTO_GARDA, R.string.categories_event_sport),
          /* 5 */ new CategoryDescriptor(R.drawable.ic_altogarda_map, R.drawable.ic_altogarda, 
        		  CAT_EVENT_ALTO_GARDA, R.string.categories_event_svago),
		 /* 6 */new CategoryDescriptor(R.drawable.ic_altogarda_map, R.drawable.ic_altogarda, 
				 CAT_EVENT_ALTO_GARDA, R.string.categories_event_altri_eventi),
	};



	private static Map<String, String> categoryMapping = new HashMap<String, String>();

	private static Map<String, CategoryDescriptor> descriptorMap = new LinkedHashMap<String, CategoryHelper.CategoryDescriptor>();
	static {
		for (CategoryDescriptor event : EVENT_CATEGORIES) {
			descriptorMap.put(event.category, event);
		}

		

		for (String s : descriptorMap.keySet()) {
			categoryMapping.put(s, s);
		}

	}

	public static String[] getAllCategories(Set<String> set) {
		List<String> result = new ArrayList<String>();
		for (String key : categoryMapping.keySet()) {
			if (set.contains(categoryMapping.get(key))) {
				// if (key.equals(EVENT_NONCATEGORIZED) ||
				// key.equals(POI_NONCATEGORIZED) ||
				// key.equals(STORY_NONCATEGORIZED)) {
				//
				// result.add(null);
				// }
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
		return R.drawable.ic_marker_e_generic;
	}

	public static int getIconByType(String type) {
		if (categoryMapping.containsKey(type))
			return descriptorMap.get(categoryMapping.get(type)).thumbnail;
		return R.drawable.ic_e_other;
	}

	public static class CategoryDescriptor {
		public int map_icon;
		public int thumbnail;
		public String category;
		public int description;

		public CategoryDescriptor(int map_icon, int thumbnail, String category, int description) {
			super();
			this.map_icon = map_icon;
			this.thumbnail = thumbnail;
			this.category = category;
			this.description = description;
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


	


	
	public static CategoryDescriptor[] getEventCategoryDescriptorsFiltered() {
		return DTParamsHelper.getInstance().getFilteredArrayByParams(EVENT_CATEGORIES, CATEGORY_TYPE_EVENTS);
	}


	public static CategoryDescriptor getCategoryDescriptorByCategoryFiltered(String type, String cat) {
		return descriptorMap.get(cat);
		
	}

}
