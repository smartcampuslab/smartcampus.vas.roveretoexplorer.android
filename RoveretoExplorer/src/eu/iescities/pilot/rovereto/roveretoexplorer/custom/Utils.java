package eu.iescities.pilot.rovereto.roveretoexplorer.custom;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import android.content.Context;
import android.location.Address;

import com.google.android.gms.maps.model.LatLng;

import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.DTHelper;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.EventObjectForBean;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model.LocalEventObject;
import eu.trentorise.smartcampus.android.common.tagging.SemanticSuggestion;
import eu.trentorise.smartcampus.android.common.tagging.SemanticSuggestion.TYPE;
import eu.trentorise.smartcampus.social.model.Concept;
import eu.trentorise.smartcampus.territoryservice.model.BaseDTObject;
import eu.trentorise.smartcampus.territoryservice.model.CommunityData;
import eu.trentorise.smartcampus.territoryservice.model.EventObject;
import eu.trentorise.smartcampus.territoryservice.model.POIObject;

public class Utils {
	public static final String userPoiObject = "eu.trentorise.smartcampus.dt.model.UserPOIObject";
	public static final String servicePoiObject = "eu.trentorise.smartcampus.dt.model.ServicePOIObject";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	private static final DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
	private static final DateFormat extDateFormat = new SimpleDateFormat("EEEEEE dd/MM/yyyy");


	public static List<Concept> conceptConvertSS(Collection<SemanticSuggestion> tags) {
		List<Concept> result = new ArrayList<Concept>();
		for (SemanticSuggestion ss : tags) {
			if (ss.getType() == TYPE.KEYWORD) {
				result.add(new Concept(null, ss.getName()));
			} else if (ss.getType() == TYPE.SEMANTIC) {
				Concept c = new Concept();
				// c.setId(ss.getId());
				c.setName(ss.getName());
				c.setDescription(ss.getDescription());
				c.setSummary(ss.getSummary());
				result.add(c);
			}
		}
		return result;
	}

	public static ArrayList<SemanticSuggestion> conceptConvertToSS(List<Concept> tags) {
		if (tags == null)
			return new ArrayList<SemanticSuggestion>();
		ArrayList<SemanticSuggestion> result = new ArrayList<SemanticSuggestion>();
		for (Concept c : tags) {
			SemanticSuggestion ss = new SemanticSuggestion();
			if (c.getId() == null) {
				ss.setType(TYPE.KEYWORD);
			} else {
				// ss.setId(c.getId());
				ss.setDescription(c.getDescription());
				ss.setSummary(c.getSummary());
				ss.setType(TYPE.SEMANTIC);
			}
			ss.setName(c.getName());
			result.add(ss);
		}
		return result;
	}

	public static String conceptToSimpleString(List<Concept> tags) {
		if (tags == null)
			return null;
		String content = "";
		for (Concept s : tags) {
			if (content.length() > 0)
				content += ", ";
			content += s.getName();
		}
		return content;
	}

	public static String getPOIshortAddress(POIObject poi) {
		String res = (poi.getPoi().getStreet() == null || poi.getPoi().getStreet().length() == 0 ? 
				"" : poi .getPoi().getStreet());

		String city = (poi.getPoi().getCity() == null || poi.getPoi().getCity().length() == 0 ? 
				"" : poi .getPoi().getCity());
		if (city != null && city.length() > 0) res += (res.length() > 0 ? " " : "") + city;

		if (res.length() > 0) return res;
		return poi.getTitle();
	}

	public static Address getPOIasGoogleAddress(POIObject poi) {
		Address a = new Address(Locale.getDefault());
		a.setLatitude(poi.getLocation()[0]);
		a.setLongitude(poi.getLocation()[1]);
		a.setAddressLine(0, poi.getPoi().getStreet());
		a.setCountryCode(poi.getPoi().getCountry());
		a.setCountryName(poi.getPoi().getState());
		a.setLocality(poi.getPoi().getCity());
		a.setPostalCode(poi.getPoi().getPostalCode());
		a.setAdminArea(poi.getPoi().getRegion());
		return a;
	}

	/**
	 * @param mTrack
	 * @return
	 */


	public static boolean isCreatedByUser(BaseDTObject obj) {
		if (obj.getDomainType() == null || userPoiObject.equals(obj.getDomainType())) {
			return true;
		} else
			return false;
	}

	public static Collection<LocalEventObject> convertToLocalEventFromBean(
			Collection<EventObjectForBean> searchInGeneral) {
		Collection<LocalEventObject> returnCollection = new ArrayList<LocalEventObject>();
		for (EventObjectForBean event : searchInGeneral) {
			LocalEventObject localEvent = DTHelper.findEventById(event.getObjectForBean().getId());
			if (localEvent != null) {

				returnCollection.add(localEvent);
			}
		}
		return returnCollection;
	}

	public static Collection<LocalEventObject> convertToLocalEvent(Collection<EventObject> events) {
		Collection<EventObjectForBean> beanEvents = new ArrayList<EventObjectForBean>();
		Collection<LocalEventObject> returnEvents = new ArrayList<LocalEventObject>();

		for (EventObject event : events) {
			EventObjectForBean newObject = new EventObjectForBean();
			LocalEventObject localObject = new LocalEventObject();
			newObject.setObjectForBean(event);
			localObject.setEventFromEventObjectForBean(newObject);
			returnEvents.add(localObject);
		}

		return returnEvents;
	}

	public static List<LatLng> decodePolyline(String encoded) {
		List<LatLng> polyline = new ArrayList<LatLng>();

		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			if (index >= len) {
				break;
			}
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((double) lat / 1E5, (double) lng / 1E5);
			polyline.add(p);
		}
		return polyline;
	}

	/**
	 * @param event
	 * @return
	 */
	public static String getEventShortAddress(LocalEventObject event) {
		if (event.getCustomData() != null && event.getCustomData().get("place")!=null) {
			return event.getCustomData().get("place").toString();
		} else {
			return null;
		}
	}


	public static Long toDateTimeLong(SimpleDateFormat sdf, String date_string) {
		Date date;
		Long mills = null;
		try {
			date = (Date) sdf.parse(date_string);
			mills = date.getTime();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return mills;
	}


	public static CharSequence eventDatesString(DateFormat sdf, Long fromTime, Long toTime) {
		String res = sdf.format(new Date(fromTime));
		if (toTime != null && toTime != fromTime) {
			Calendar f = Calendar.getInstance();
			f.setTimeInMillis(fromTime);
			Calendar t = Calendar.getInstance();
			t.setTimeInMillis(toTime);
			if (t.get(Calendar.DATE) != f.get(Calendar.DATE)) {
				res += " - "+ sdf.format(new Date(toTime));
			}
		}
		return res;
	}

	public String setDateString(Context context, Long fromTime) {
		String newdateformatted = new String("");

		Date dateToday = new Date();
		String stringToday = (dateFormat.format(dateToday));
		String stringEvent = (dateFormat.format(new Date(fromTime)));

		Calendar cal = Calendar.getInstance();
		cal.setTime(dateToday);
		cal.add(Calendar.DAY_OF_YEAR, 1); // <--
		Date tomorrow = cal.getTime();
		String stringTomorrow = (dateFormat.format(tomorrow));
		// check actual date
		if (stringToday.equals(stringEvent)) {
			// if equal put the Today string
			newdateformatted = stringToday;
			newdateformatted = context.getString(R.string.list_event_today) + " " + newdateformatted;
		} else if (stringTomorrow.equals(stringEvent)) {
			// else if it's tomorrow, cat that string
			newdateformatted = stringTomorrow;
			newdateformatted = context.getString(R.string.list_event_tomorrow) + " " + newdateformatted;
		}
		// else put the day's name
		else
			newdateformatted = extDateFormat.format(new Date(fromTime));
		return newdateformatted;
	}




	public static ArrayList<String> createFakeDateGroupList(){
		ArrayList<String> groupList = new ArrayList<String>();
		groupList.add("Oggi 29/10/2013");
		groupList.add("Domani 30/10/2013");
		groupList.add("Giovedi 31/10/2013");
		return groupList;
	}


	public static Map<String, List<LocalEventObject>> createFakeEventCollection(List<String> dateGroupList ) {

		List<LocalEventObject> eventList = getFakeLocalEventObjects();
		Map<String, List<LocalEventObject>> eventCollection = new LinkedHashMap<String, List<LocalEventObject>>();
		List<LocalEventObject> childList = new ArrayList<LocalEventObject>();

		// preparing laptops collection(child)
		for (String event_date : dateGroupList) {
			childList = new ArrayList<LocalEventObject>();
			if (event_date.equals("Oggi 29/10/2013")) {
				childList.add(eventList.get(0));
				childList.add(eventList.get(1));
				childList.add(eventList.get(2));
			} else if (event_date.equals("Domani 30/10/2013"))
				childList.add(eventList.get(3));
			else if (event_date.equals("Giovedi 31/10/2013")){
				childList.add(eventList.get(4));
				childList.add(eventList.get(5));
			}

			eventCollection.put(event_date, childList);
		}
		
		return eventCollection;
	}

	private static List<LocalEventObject> loadChild(LocalEventObject[] eventsByDate) {
		List<LocalEventObject> childList = new ArrayList<LocalEventObject>();
		for (LocalEventObject event : eventsByDate)
			childList.add(event);
		return childList;
	}



	public static List<LocalEventObject> getFakeLocalEventObjects(){

		List<LocalEventObject> fake_events = new ArrayList<LocalEventObject>();
		LocalEventObject fake_event;  new LocalEventObject(); 
		Map<String, Object> customData =  new HashMap<String, Object>(); 
		CommunityData communityData = new CommunityData(); 
		try {
			//create fake event object 1 
			String[] telefono = new String[]{"0461235678", "3345678910"};
			String[] email = new String[]{"xxx@comune.rovereto.it"};
			String[] tags = new String[]{"sport", "calcio"};
			URL site_url = new URL("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/Roverunning-training6");
			URL img_url = new URL("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/roverunning-training6/124779-4-ita-IT/Roverunning-training_medium.jpg");
			customData.put("where_name", "Palasport Marchetti");
			customData.put("where_city", "Rovereto");
			customData.put("where_street", "Corso Bettini, 52");
			customData.put("event_img", img_url);
			customData.put("Durata", "3 ore");
			customData.put("Telefono", telefono);
			customData.put("Email", email);
			customData.put("Tags", tags);
			customData.put("Sito web", site_url);
			customData.put("Tipo di luogo", "aperto");
			customData.put("Accesso", "libero");
			customData.put("Propabilita dell'evento", "confermato");
			customData.put("Lingua principale ", "Italiano");
			customData.put("Abbigliamento consigliato", "sportivo");

			fake_event = new LocalEventObject("Roverunning training", 3, 5, customData);
			fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "17/12/2013"));
			fake_event.setId("1");
			
			fake_events.add(fake_event);

			//create fake event object 2 
			customData = new HashMap<String, Object>(); 
			communityData = new CommunityData(); 
			telefono = new String[]{"0464565880"};
			email = new String[]{"xxx@comune.rovereto.it"};
			site_url = new URL("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/24-TORNEO-DI-NATALE-Pallavolo-Femminile");
			img_url = new URL("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/24-torneo-di-natale-pallavolo-femminile/123469-1-ita-IT/24-TORNEO-DI-NATALE-Pallavolo-Femminile_medium.jpg");
			customData.put("where_name", "Palasport e palestre");
			customData.put("where_city", "Rovereto");
			customData.put("where_street", "Corso Bettini, 52");
			customData.put("event_img", img_url);
			customData.put("Durata", "4 ore");
			customData.put("Telefono", telefono);
			customData.put("Email", email);
			customData.put("Sito web", site_url);
			customData.put("Tipo di luogo", "chiuso");
			customData.put("Accesso", "a pagamento");
			customData.put("Propabilita dell'evento", "confermato");
			customData.put("Lingua principale ", "Italiano");
			customData.put("Abbigliamento consigliato", "sportivo");

			fake_event = new LocalEventObject("24esimo TORNEO DI NATALE Pallavolo Femminile", 5, 50, customData);
			fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
			fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "29/12/2013"));
			fake_event.setId("2");
			fake_events.add(fake_event);


			//create fake event object 3 
			customData = new HashMap<String, Object>(); 
			communityData = new CommunityData(); 
			telefono = new String[]{"0464565880"};
			email = new String[]{"xxx@comune.rovereto.it"};
			site_url = new URL("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/24-TORNEO-DI-NATALE-Pallavolo-Femminile");
			customData.put("where_name", "Palasport Marchetti");
			customData.put("where_city", "Rovereto");
			customData.put("where_street", "Corso Bettini, 52");
			customData.put("Durata", "4 ore");
			customData.put("Telefono", telefono);
			customData.put("Email", email);
			customData.put("Sito web", site_url);
			customData.put("Tipo di luogo", "chiuso");
			customData.put("Accesso", "a pagamento");
			customData.put("Propabilita dell'evento", "confermato");
			customData.put("Lingua principale ", "Italiano");
			customData.put("Abbigliamento consigliato", "sportivo");

			fake_event = new LocalEventObject("9 Torneo Passamani di lotta grecoromana e ", 4, 35, customData);
			fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "30/12/2013"));
			fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "31/12/2013"));
			fake_event.setId("3");
			fake_events.add(fake_event);

			//create fake event object 4 
			customData = new HashMap<String, Object>(); 
			customData.put("where_name", "Palasport e palestre");
			fake_event = new LocalEventObject("Torneo della Pace", 2, 15, customData);
			fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
			fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "29/12/2013"));
			fake_event.setId("4");
			fake_events.add(fake_event);
			//create fake event object 5 
			customData = new HashMap<String, Object>(); 
			customData.put("where_name", "Teatro Rosmini");
			fake_event = new LocalEventObject("Saggio di danza", 1, 30, customData);
			fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
			fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "29/12/2013"));
			fake_event.setId("5");
			fake_events.add(fake_event);
			//create fake event object 6 
			customData = new HashMap<String, Object>(); 
			customData.put("where_name", "Palasport");
			fake_event = new LocalEventObject("Hockey su ghiaccio", 0, 15, customData);
			fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
			fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "29/12/2013"));
			fake_event.setId("6");
			fake_events.add(fake_event);

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return fake_events;  
	}	


	public static LocalEventObject getFakeLocalEventObject(List<LocalEventObject> events, String id){

		LocalEventObject fake_event = null;
		for (LocalEventObject event: events){
			if (event.getId()==id) {
				return event;
			} 
		}
		return fake_event;
	}



}



