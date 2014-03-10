package eu.iescities.pilot.rovereto.roveretoexplorer.custom.data.model;

import java.util.List;

import eu.iescities.pilot.rovereto.roveretoexplorer.R;

public class ToKnow {

	private String attrTypeName;
	
	private boolean singleValue=true;
	
	private String valueName;
	
	private int leftIconId = -1;
	
	private int[] rightIconIds = null;
	
	private int divider_height=1;
	private int divider_color = R.color.jungle_green;
	private boolean textInBold = false;
	
	
	
	public ToKnow() {	
	}
	
	public ToKnow(String attrTypeName, boolean singleValue, String valueName) {
		setTitle(attrTypeName);
		setContent(valueName);
		setSingleValue(singleValue);
	}
	
	
	public ToKnow(String attrTypeName, String valueName) {
		setTitle(attrTypeName);
		setContent(valueName);
	}
	
	
	public String getTitle() {
		return attrTypeName;
	}
	
	public void setTitle(String title) {
		this.attrTypeName = title;
	}
	
	public String getContent() {
		return valueName;
	}
	
	public void setContent(String content) {
		this.valueName = content;
	}

	public boolean isSingleValue() {
		return singleValue;
	}

	public void setSingleValue(boolean singleValue) {
		this.singleValue = singleValue;
	}
	
	

	public int getLeftIconId()
	{
		return leftIconId;
	}
	
	public void setLeftIconId(int leftIconId)
	{
		this.leftIconId = leftIconId;
	}
	
	public int[] getRightIconIds()
	{
		return rightIconIds;
	}
	
	public void setRightIconIds(int[] rightIconIds)
	{
		this.rightIconIds = rightIconIds;
	}
	
	
	public int getDividerColor()
	{
		return divider_color;
	}
	
	public void setDividerColor(int divider_colorId)
	{
		this.divider_color = divider_colorId;
	}
	
	
	public int getDividerHeight()
	{
		return divider_height;
	}
	
	public void setDividerHeight(int divider_height)
	{
		this.divider_height = divider_height;
	}
	
	
	public boolean getTextInBold()
	{
		return textInBold;
	}
	
	
	public void setTextInBold(boolean text_in_bold)
	{
		this.textInBold = text_in_bold;
	}

	
	
	
	
}
