package eu.iescities.pilot.rovereto.roveretoexplorer.custom;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;

import com.google.android.gms.maps.model.LatLng;

import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Address;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.BaseDTObject;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.CommunityData;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.EventObject;
import eu.trentorise.smartcampus.android.common.follow.model.Concept;
import eu.trentorise.smartcampus.android.common.tagging.SemanticSuggestion;
import eu.trentorise.smartcampus.android.common.tagging.SemanticSuggestion.TYPE;

public class Utils {
	public static final String userPoiObject = "eu.trentorise.smartcampus.dt.model.UserPOIObject";
	public static final String servicePoiObject = "eu.trentorise.smartcampus.dt.model.ServicePOIObject";
	private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy hh:mm aa");

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



	/**
	 * @param mTrack
	 * @return
	 */


	//	public static boolean isCreatedByUser(BaseDTObject obj) {
	//		if (obj.getDomainType() == null || userPoiObject.equals(obj.getDomainType())) {
	//			return true;
	//		} else
	//			return false;
	//	}

	//	public static Collection<LocalEventObject> convertToLocalEventFromBean(
	//			Collection<EventObjectForBean> searchInGeneral) {
	//		Collection<LocalEventObject> returnCollection = new ArrayList<LocalEventObject>();
	//		for (EventObjectForBean event : searchInGeneral) {
	//			LocalEventObject localEvent = DTHelper.findEventById(event.getObjectForBean().getId());
	//			if (localEvent != null) {
	//
	//				returnCollection.add(localEvent);
	//			}
	//		}
	//		return returnCollection;
	//	}

	//	public static Collection<LocalEventObject> convertToLocalEvent(Collection<EventObject> events) {
	//		Collection<EventObjectForBean> beanEvents = new ArrayList<EventObjectForBean>();
	//		Collection<LocalEventObject> returnEvents = new ArrayList<LocalEventObject>();
	//
	//		for (EventObject event : events) {
	//			EventObjectForBean newObject = new EventObjectForBean();
	//			LocalEventObject localObject = new LocalEventObject();
	//			newObject.setObjectForBean(event);
	//			localObject.setEventFromEventObjectForBean(newObject);
	//			returnEvents.add(localObject);
	//		}
	//
	//		return returnEvents;
	//	}

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
	public static String getEventShortAddress(EventObject event) {
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


	public static Map<String, List<EventObject>> createFakeEventCollection(List<String> dateGroupList ) {

		List<EventObject> eventList = getFakeEventObjects();
		Map<String, List<EventObject>> eventCollection = new LinkedHashMap<String, List<EventObject>>();
		List<EventObject> childList = new ArrayList<EventObject>();

		// preparing laptops collection(child)
		for (String event_date : dateGroupList) {
			childList = new ArrayList<EventObject>();
			if (event_date.equals("Oggi 29/10/2013")) {
				childList.add(eventList.get(0));
				childList.add(eventList.get(1));
			} else if (event_date.equals("Domani 30/10/2013"))
				childList.add(eventList.get(2));
			else if (event_date.equals("Giovedi 31/10/2013")){
				childList.add(eventList.get(3));
				childList.add(eventList.get(4));
			}

			eventCollection.put(event_date, childList);
		}

		return eventCollection;
	}

	private static List<EventObject> loadChild(EventObject[] eventsByDate) {
		List<EventObject> childList = new ArrayList<EventObject>();
		for (EventObject event : eventsByDate)
			childList.add(event);
		return childList;
	}



	public static List<EventObject> getFakeEventObjects(){

		List<EventObject> fake_events = new ArrayList<EventObject>();
		EventObject fake_event;  new EventObject(); 
		Map<String, Object> customData =  new HashMap<String, Object>(); 
		Map<String,Object> contacts = new HashMap<String, Object>();
		CommunityData communityData = new CommunityData();

		//create fake event object 1 
		fake_event = new EventObject();

	
		//set basic info
		fake_event.setTitle("Roverunning training");
		fake_event.setWhenWhere("tutti i martedì con inizio il 14 - 21 - 28 gennaio, 4 - 11 - 18 - 25 febbraio, 4 - 11 - 18 - 25 marzo, ritrovo nella piazza del Mart ore 18.00");
		fake_event.setOrigin("Assessorato allo Sport, US Quercia, NW Arcobaleno");  //the source
		fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "17/1/2014 08:30 PM"));
		fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "17/1/2014 10:30 PM"));
		fake_event.setId("1");
		fake_event.setDescription("percorrerovereto. Vuoi imparare a correre? A camminare? Vuoi migliorare la tua attività di runner? Cerchi un'opportunità per correre/camminare in compagnia? " +
				"La partecipazione è gratuita e aperta a tutti i principianti, amatori e agonisti");
		String site_url = new String("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/Roverunning-training6");
		String img_url = new String("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/roverunning-training6/124779-4-ita-IT/Roverunning-training_medium.jpg");
		fake_event.setImage(img_url);
		fake_event.setWebsiteUrl(site_url);
		
		//set location and address
		double[] loc = {45.890960000000000000,11.040139899999986};
		fake_event.setLocation(loc);
		Address address = new Address();
		address.setCitta("Rovereto");
		address.setLuogo("Palasport Marchetti");
		address.setVia("Corso Bettini, 52");
		fake_event.setAddress(address);

		//set contacts
		String[] telefono = new String[]{"0461235678", "3345678910"};
		String[] email = new String[]{"xxx@comune.rovereto.it"};
		contacts.put("telefono", telefono);
		contacts.put("email", email);
		fake_event.setContacts(contacts);
	
		//set community data
		List<String> tags  = Arrays.asList(new String[]{"sport", "calcio"});
		communityData.setTags(tags);
		communityData.setAttendees(5);
		communityData.setAverageRating(3);
		fake_event.setCommunityData(communityData);
		

		//set custom data
		customData.put("Tipo di luogo", "aperto");
		customData.put("Accesso", "libero");
		customData.put("Propabilita dell'evento", "confermato");
		customData.put("Lingua principale ", "Italiano");
		customData.put("Abbigliamento consigliato", "sportivo");
		customData.put("Abbigliamento consigliato", "sportivo");
		fake_event.setCustomData(customData);
		
		fake_events.add(fake_event);

		//create fake event object 2 
		telefono = new String[]{"0464565880"};
		email = new String[]{"xxx@comune.rovereto.it"};
		tags =  Arrays.asList(new String[]{"sport", "pallavolo"});
		site_url = new String("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/24-TORNEO-DI-NATALE-Pallavolo-Femminile");
		img_url = new String("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/24-torneo-di-natale-pallavolo-femminile/123469-1-ita-IT/24-TORNEO-DI-NATALE-Pallavolo-Femminile_medium.jpg");

		
		address = new Address();
		address.setCitta("Rovereto");
		address.setLuogo("Palasport e palestre");
		address.setVia("Corso Bettini, 52");

		customData.put("Tipo di luogo", "chiuso");
		customData.put("Accesso", "a pagamento");
		customData.put("Propabilita dell'evento", "confermato");
		customData.put("Lingua principale ", "Italiano");
		customData.put("Abbigliamento consigliato", "sportivo");



		fake_event = new EventObject();
		fake_event.setAddress(address);
		fake_event.setAddress(address);

		fake_event.setWhenWhere("whenwhere a");
		fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
		fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "28/12/2013"));
		fake_event.setId("2");
		fake_event.setDescription("description 1");
		fake_event.setTitle("24esimo TORNEO DI NATALE Pallavolo Femminile");
		fake_event.setCustomData(customData);
		fake_event.setImage(img_url);
		fake_event.setWebsiteUrl(site_url);
		contacts.put("telefono", telefono);
		contacts.put("email", email);
		contacts.put("tags", tags);
		fake_event.setContacts(contacts);
		fake_events.add(fake_event);

		//create fake event object 3 
		telefono = new String[]{"0464565880"};
		email = new String[]{"xxx@comune.rovereto.it"};
		tags =  Arrays.asList(new String[]{"sport", "pallavolo"});
		site_url = new String("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/24-TORNEO-DI-NATALE-Pallavolo-Femminile");
		img_url = new String("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/24-torneo-di-natale-pallavolo-femminile/123469-1-ita-IT/24-TORNEO-DI-NATALE-Pallavolo-Femminile_medium.jpg");
		address = new Address();
		address.setCitta("Rovereto");
		address.setLuogo("Palasport e palestre");
		address.setVia("Corso Bettini, 52");

		customData.put("Tipo di luogo", "chiuso");
		customData.put("Accesso", "a pagamento");
		customData.put("Propabilita dell'evento", "confermato");
		customData.put("Lingua principale ", "Italiano");
		customData.put("Abbigliamento consigliato", "sportivo");



		fake_event = new EventObject();
		fake_event.setAddress(address);
		fake_event.setAddress(address);

		fake_event.setWhenWhere("whenwhere a");
		fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
		fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "28/12/2013"));
		fake_event.setId("3");
		fake_event.setDescription("description 1");
		fake_event.setTitle("titolo 3");
		fake_event.setCustomData(customData);
		fake_event.setImage(img_url);
		fake_event.setWebsiteUrl(site_url);
		contacts.put("telefono", telefono);
		contacts.put("email", email);
		contacts.put("tags", tags);
		fake_event.setContacts(contacts);
		fake_events.add(fake_event);

		//create fake event object 4 
		telefono = new String[]{"0464565880"};
		email = new String[]{"xxx@comune.rovereto.it"};
		tags =  Arrays.asList(new String[]{"sport", "pallavolo"});
		site_url = new String("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/24-TORNEO-DI-NATALE-Pallavolo-Femminile");
		img_url = new String("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/24-torneo-di-natale-pallavolo-femminile/123469-1-ita-IT/24-TORNEO-DI-NATALE-Pallavolo-Femminile_medium.jpg");
		address = new Address();
		address.setCitta("Rovereto");
		address.setLuogo("Palasport e palestre");
		address.setVia("Corso Bettini, 52");

		customData.put("Tipo di luogo", "chiuso");
		customData.put("Accesso", "a pagamento");
		customData.put("Propabilita dell'evento", "confermato");
		customData.put("Lingua principale ", "Italiano");
		customData.put("Abbigliamento consigliato", "sportivo");



		fake_event = new EventObject();
		fake_event.setAddress(address);
		fake_event.setAddress(address);

		fake_event.setWhenWhere("whenwhere a");
		fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
		fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "28/12/2013"));
		fake_event.setId("4");
		fake_event.setDescription("description 1");
		fake_event.setTitle("saggio di danza");
		fake_event.setCustomData(customData);
		fake_event.setImage(img_url);
		fake_event.setWebsiteUrl(site_url);
		contacts.put("telefono", telefono);
		contacts.put("email", email);
		contacts.put("tags", tags);
		fake_event.setContacts(contacts);
		fake_events.add(fake_event);

		//			//create fake event object 5
		telefono = new String[]{"0464565880"};
		email = new String[]{"xxx@comune.rovereto.it"};
		tags =  Arrays.asList(new String[]{"sport", "pallavolo"});
		site_url = new String("http://www.comune.rovereto.tn.it/Vivi-la-citta/Sport/Calendario-eventi-sportivi/24-TORNEO-DI-NATALE-Pallavolo-Femminile");
		img_url = new String("http://www.comune.rovereto.tn.it/var/rovereto/storage/images/vivi-la-citta/sport/calendario-eventi-sportivi/24-torneo-di-natale-pallavolo-femminile/123469-1-ita-IT/24-TORNEO-DI-NATALE-Pallavolo-Femminile_medium.jpg");
		address = new Address();
		address.setCitta("Rovereto");
		address.setLuogo("Palasport e palestre");
		address.setVia("Corso Bettini, 52");

		customData.put("Tipo di luogo", "chiuso");
		customData.put("Accesso", "a pagamento");
		customData.put("Propabilita dell'evento", "confermato");
		customData.put("Lingua principale ", "Italiano");
		customData.put("Abbigliamento consigliato", "sportivo");



		fake_event = new EventObject();
		fake_event.setAddress(address);
		fake_event.setAddress(address);

		fake_event.setWhenWhere("whenwhere a");
		fake_event.setFromTime(Utils.toDateTimeLong(DATE_FORMAT, "27/12/2013"));
		fake_event.setToTime(Utils.toDateTimeLong(DATE_FORMAT, "29/12/2013"));
		fake_event.setId("5");
		fake_event.setDescription("description 1");
		fake_event.setTitle("Hockey su ghiaccio");
		fake_event.setCustomData(customData);
		fake_event.setImage(img_url);
		fake_event.setWebsiteUrl(site_url);
		contacts.put("telefono", telefono);
		contacts.put("email", email);
		contacts.put("tags", tags);
		fake_event.setContacts(contacts);
		fake_events.add(fake_event);

		return fake_events;  
	}	


	public static EventObject getFakeLocalEventObject(List<EventObject> events, String id){

		EventObject fake_event = null;
		for (EventObject event: events){
			if (event.getId()==id) {
				return event;
			} 
		}
		return fake_event;
	}



}



