package eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model;

public class ToKnow {

	private String title;
	private String content;
	
	public ToKnow() {	
	}
	
	public ToKnow(String title, String content) {
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
