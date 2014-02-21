package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.edit;


import java.util.List;

import eu.iescities.pilot.rovereto.roveretoexplorer.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;

public class TagListAdapter extends ArrayAdapter<String> {

	Context context;
	List<String> tags;

	ViewHolder holder = null;

	public TagListAdapter(Context context, int resourceId,
			List<String> items) {
		super(context, resourceId, items);
		this.context = context;
		this.tags = items;

	}

	/*private view holder class*/
	private static class ViewHolder {
		protected ImageView delete;
		protected EditText txtName;

	}


	public View getView(int position, View convertView, ViewGroup parent) {
		
		String tagItem = getItem(position);

		View view = null;
		if (convertView == null) {
			LayoutInflater mInflater = (LayoutInflater) context
					.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);  

			view = mInflater.inflate(R.layout.frag_ev_detail_info_edit_tags_list_row, null);
			final ViewHolder viewHolder = new ViewHolder();
			
			viewHolder.txtName = (EditText) view.findViewById(R.id.tag_text);
			viewHolder.delete = (ImageView) view.findViewById(R.id.delete_icon);

			viewHolder.txtName
			.addTextChangedListener(new TextWatcher() {
			    
				@Override
				public void onTextChanged(CharSequence s, int start, int before, int count) {
					   			        // TODO Auto-generated method stub
			    }

			    @Override
			    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			        // TODO Auto-generated method stub
			    }
			    
			    
				@Override
			    public void afterTextChanged(Editable s) {
			        // TODO Auto-generated method stub
			    	String  element = (String) viewHolder.txtName.getTag();
//					Log.i("TAG", "TagListAdapter --> TextWatcher --> old edited string: " + element);
			    	int id = tags.indexOf(element);
			    	element = s.toString();
			    	if (id!=-1){
				    	tags.set(id, element);
				    	viewHolder.txtName.setTag(tags.get(id));
			    	}
			    }
				        
			});
			
			view.setTag(viewHolder);
			viewHolder.txtName.setTag(tags.get(position));
		} else {
			view = convertView;
			((ViewHolder) view.getTag()).txtName.setTag(tags.get(position));
		}
		ViewHolder holder = (ViewHolder) view.getTag();
		holder.txtName.setText(tags.get(position));
		holder.delete.setOnClickListener(new DeleteIconClickListener(position));

		return view;
	
}


private final class DeleteIconClickListener implements OnClickListener {
	private final int position;

	private DeleteIconClickListener(int position) {
		this.position = position;
	}

	@Override
	public void onClick(View v) {
		Log.i("FRAGMENT LC", "TagListAdapter --> Delete button pressed!");

		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setMessage(R.string.tag_remove_request);
		builder.setCancelable(false);
		builder.setPositiveButton(R.string.yes,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				tags.remove(position);
				notifyDataSetChanged();
			}
		});
		builder.setNegativeButton(R.string.no,
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alertDialog = builder.create();
		alertDialog.show();
	}
}


}