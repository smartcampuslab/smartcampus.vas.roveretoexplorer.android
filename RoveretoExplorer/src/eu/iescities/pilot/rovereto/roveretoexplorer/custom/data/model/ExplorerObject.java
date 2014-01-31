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
package eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.Address;

public class ExplorerObject extends BaseDTObject {

	private static final long serialVersionUID = 388550207183035548L;
	private String whenWhere = null;
	private Address address = null;

	private String image = null;

	private String websiteUrl = null;
	private String facebookUrl = null;
	private String twitterUrl = null;

	private String origin = null;
	private String category = null;
	private Map<String, Object> contacts = null;

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

	public String getWebsiteUrl() {
		return websiteUrl;
	}

	public void setWebsiteUrl(String url) {
		this.websiteUrl = url;
	}

	public String getFacebookUrl() {
		return facebookUrl;
	}

	public void setFacebookUrl(String url) {
		this.facebookUrl = url;
	}

	public String getTwitterUrl() {
		return twitterUrl;
	}

	public void setTwitterUrl(String url) {
		this.twitterUrl = url;
	}


	public ExplorerObject() {
		super();
	}

	public CharSequence dateTimeString() {
		return DATE_FORMAT.format(new Date(getFromTime()));
	}

	public CharSequence toDateTimeString() {
		if (getToTime() == null || getToTime() == 0)
			return dateTimeString();
		return DATE_FORMAT.format(new Date(getToTime()));
	}

	
	public ExplorerObject copy() {
		ExplorerObject o = new ExplorerObject();
		o.setCommunityData(getCommunityData());
		o.setCommunityData(getCommunityData());
		o.setCustomData(getCustomData());
		o.setDescription(getDescription());
		o.setFromTime(getFromTime());
		o.setId(getId());
		o.setLocation(getLocation());
		o.setSource(getSource());
		o.setTitle(getTitle());
		o.setToTime(getToTime());
		o.setType(getType());
		o.setUpdateTime(getUpdateTime());
		o.setVersion(getVersion());
		o.setImage(getImage());
		o.setAddress(getAddress());
		o.setWebsiteUrl(getWebsiteUrl());
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


	public List<String> bringEmails() {
		if (getContacts().containsKey("email")) {
			String[] parts = ((String) getContacts().get("email")).split(",");
			List<String> returnList = new ArrayList<String>(Arrays.asList(parts));  
			return returnList;
		} else
			return null;
	}

	public void saveEmails(List<String> emails) {
		StringBuffer buf = new StringBuffer(256); // Java default is 16,
		for (String email: emails){
			buf.append(email+",");

		}
		buf.deleteCharAt(buf.length() - 1);
		this.getContacts().put("email", buf);

	}


}
