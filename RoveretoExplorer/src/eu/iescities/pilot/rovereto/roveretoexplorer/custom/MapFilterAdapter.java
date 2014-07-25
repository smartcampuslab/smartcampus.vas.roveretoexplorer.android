package eu.iescities.pilot.rovereto.roveretoexplorer.custom;

import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckedTextView;
import eu.iescities.pilot.rovereto.roveretoexplorer.R;

public class MapFilterAdapter extends ArrayAdapter<String> {

	private int mIconRes;
	private boolean[] mItemStatus;

	public MapFilterAdapter(Context context, List<String> labels,
			int iconResourceId, boolean[] mItemStatus) {
		super(context, R.id.select_poi_listview,labels);
		mIconRes = iconResourceId;
		this.mItemStatus = mItemStatus;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(
					R.layout.select_poi_list_element_row, null);
		}

		CheckedTextView ctv = (CheckedTextView) convertView
				.findViewById(R.id.select_poi_checkTv);
		ctv.setText(getItem(position));
//		if(ctv.getTag()!=null){
//			ctv.setChecked((Boolean) ctv.getTag());
//		}
		ctv.setChecked(this.mItemStatus[position]);
		TypedArray icons = getContext().getResources().obtainTypedArray(
				mIconRes);
		int imgid = icons.getResourceId(position, -1);
		if (imgid != -1) {
			Drawable icon = getContext().getResources().getDrawable(imgid);
			ctv.setCompoundDrawablesWithIntrinsicBounds(icon, null, null, null);
		}
		icons.recycle();
		return convertView;
	}

}