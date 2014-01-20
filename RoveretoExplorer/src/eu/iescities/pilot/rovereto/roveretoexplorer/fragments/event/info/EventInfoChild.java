package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info;

public class EventInfoChild{
	
	private String name;
	
	private String text;
	
	private int type; 
	
	private int leftIconId = -1;
	
	private int[] rightIconIds = null;
	
	
	public String getName()
	{
		return name;
	}
	
	public void setName(String name)
	{
		this.name = name;
	}
	
	public String getText()
	{
		return text;
	}
	
	public void setText(String text)
	{
		this.text = text;
	}
	
	
	public int getType()
	{
		return type;
	}
	
	public void setType(int type)
	{
		this.type= type;
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
	
}
