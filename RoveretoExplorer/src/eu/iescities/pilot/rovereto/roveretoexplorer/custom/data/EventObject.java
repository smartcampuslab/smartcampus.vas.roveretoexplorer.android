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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

public class EventObject extends BaseDTObject {
	

	private static final long serialVersionUID = 388550207183035548L;
	private String whenWhere=null;
	private Address address = null;

	private String image = null;
	private String url = null;
	private String origin = null;
	private String category = null;
	private Map<String, Object> contacts = null;
	private List<String> attending = null;
	private Integer attendees = 0;

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd/MM/yyyy");

	
	public String getOrigin() {
		return origin;
	}

	public void setOrigin(String origin) {
		this.origin = origin;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public Map<String, Object> getContacts() {
		return contacts;
	}

	public void setContacts(Map<String, Object> contacts) {
		this.contacts = contacts;
	}

	private Integer rating = null;
	


	public String getImage() {
		return image;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public Integer getRating() {
		return rating;
	}

	public void setRating(Integer rating) {
		this.rating = rating;
	}

	public EventObject() {
		super();
	}

	public EventObject(String title, int averageRating,  Map<String, Object> customData) {
		//TO DO
		super();
		this.setTitle(title);
		this.setCustomData(customData);
		this.getCommunityData().setAverageRating(averageRating);
		
	}

    public CharSequence dateTimeString() {
        return DATE_FORMAT.format(new Date(getFromTime()));
}

public CharSequence toDateTimeString() {
        if (getToTime()==null||getToTime()==0)
                return dateTimeString();        
        return DATE_FORMAT.format(new Date(getToTime()));
}
	public EventObject copy() {
		EventObject o = new EventObject();
		o.setCommunityData(getCommunityData());
		o.setCommunityData(getCommunityData());
		o.setCreatorId(getCreatorId());
		o.setCreatorName(getCreatorName());
		o.setCustomData(getCustomData());
		o.setDescription(getDescription());
		o.setDomainId(getDomainId());
		o.setDomainType(getDomainType());
		o.setEntityId(getEntityId());
		o.setFromTime(getFromTime());
		o.setId(getId());
		o.setLocation(getLocation());
		o.setSource(getSource());
		o.setTiming(getTiming());
		o.setTitle(getTitle());
		o.setToTime(getToTime());
		o.setType(getType());
		o.setUpdateTime(getUpdateTime());
		o.setVersion(getVersion());
		o.setImage(getImage());
		o.setAddress(getAddress());
		o.setUrl(getUrl());
		o.setOrigin(getOrigin());
		o.setCategory(getCategory());
		o.setContacts(getContacts());
		return o;
	}

	public String getWhenWhere() {
		return whenWhere;
	}

	public void setWhenWhere(String whenWhere) {
		this.whenWhere = whenWhere;
	}

	public List<String> getAttending() {
		return attending;
	}

	public void setAttending(List<String> attending) {
		this.attending = attending;
	}

	public Integer getAttendees() {
		return attendees;
	}

	public void setAttendees(Integer attendees) {
		this.attendees = attendees;
	}
	
	
}