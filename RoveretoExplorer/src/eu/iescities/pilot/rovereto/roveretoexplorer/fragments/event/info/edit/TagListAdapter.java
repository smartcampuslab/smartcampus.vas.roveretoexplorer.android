package eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.edit;


import java.util.List;

import eu.iescities.pilot.rovereto.roveretoexplorer.R;
import eu.iescities.pilot.rovereto.roveretoexplorer.custom.Utils;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.EventDetailInfoAdapter;
import eu.iescities.pilot.rovereto.roveretoexplorer.fragments.event.info.EventInfoParent;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
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
import android.widget.TextView;

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
	private class ViewHolder {
		ImageView delete;
		EditText txtName;

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		 holder = null;
		String tagItem = getItem(position);
		
		Log.i("TAG", "TagListAdapter --> tags: " + tags);
		Log.i("TAG", "TagListAdapter --> tag: " + tagItem);


		LayoutInflater mInflater = (LayoutInflater) context
				.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
		if (convertView == null) {
			convertView = mInflater.inflate(R.layout.frag_ev_detail_info_edit_tags_list_row, null);
			holder = new ViewHolder();
			holder.txtName = (EditText) convertView.findViewById(R.id.tag_text);
			holder.delete = (ImageView) convertView.findViewById(R.id.delete_icon);
			convertView.setTag(holder);
		} else
			holder = (ViewHolder) convertView.getTag();


		holder.txtName.setText(tagItem);
		//holder.txtName.setText(holder.txtName.getText());
		
		holder.txtName.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				if (holder.txtName.getText().length() >= 0) {
					//holder.txtName.setText(holder.txtName.getText().toString());
					Log.i("TAG", "TagListAdapter --> new tag: " + holder.txtName.getText().toString());

					// formLabel.setText(txtEventField.getText().toString());
				}
			
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
//				if (holder.txtName.getText().length() >= 0) {
//					//holder.txtName.setText(holder.txtName.getText().toString());
//					Log.i("TAG", "TagListAdapter --> new tag: " + holder.txtName.getText().toString());
//
//					// formLabel.setText(txtEventField.getText().toString());
//				}
			}
		});
		
		
		
		holder.delete.setOnClickListener(new DeleteIconClickListener(position));
		
		
		

		return convertView;
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