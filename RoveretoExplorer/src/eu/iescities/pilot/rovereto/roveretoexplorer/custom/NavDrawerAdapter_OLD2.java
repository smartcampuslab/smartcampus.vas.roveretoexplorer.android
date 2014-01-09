package eu.iescities.pilot.rovereto.roveretoexplorer.custom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;


import eu.iescities.pilot.rovereto.roveretoexplorer.R;

import android.app.Activity;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavDrawerAdapter_OLD2 extends ArrayAdapter<DrawerItemOld> {

	private final Activity context;
	int layoutResourceId;
	private final ArrayList<DrawerItemOld> items;


	public NavDrawerAdapter_OLD2(Activity context, int layoutResourceId, ArrayList<DrawerItemOld> items) {
		super(context, layoutResourceId, items);
		this.context = context;
		this.items = items;
		this.layoutResourceId = layoutResourceId;
	}

	 @Override
	 public View getView(int position, View convertView, ViewGroup parent) {

		 if(convertView==null){
		// inflate the layout
		LayoutInflater inflater = ((Activity) context).getLayoutInflater();
		convertView = inflater.inflate(layoutResourceId, parent, false);
	}

	// object item based on the position
	DrawerItemOld item = items.get(position);
	// get the TextView and then set the text (item name) and tag (item ID) values
	TextView tv = (TextView) convertView.findViewById(R.id.drawer_list_textview);
	
	if (item.icon != null){
		tv.setCompoundDrawablesWithIntrinsicBounds(item.icon, null, null,
				null);
	}
	tv.setText(item.text);
	
	return convertView;
}












}
