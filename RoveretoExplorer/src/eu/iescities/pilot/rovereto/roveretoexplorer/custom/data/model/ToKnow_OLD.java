package eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model;

public class ToKnow_OLD {

	private String title;
	
	
	private String content;
	
	public ToKnow_OLD() {	
	}
	
	public ToKnow_OLD(String title, String content) {
		setTitle(title);
		setContent(content);
	}
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getContent() {
		return content;
	}
	
	public void setContent(String content) {
		this.content = content;
	}
	
}
