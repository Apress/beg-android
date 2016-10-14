/***
	Copyright (c) 2008-2009 CommonsWare, LLC
	
	Licensed under the Apache License, Version 2.0 (the "License"); you may
	not use this file except in compliance with the License. You may obtain
	a copy of the License at
		http://www.apache.org/licenses/LICENSE-2.0
	Unless required by applicable law or agreed to in writing, software
	distributed under the License is distributed on an "AS IS" BASIS,
	WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
	See the License for the specific language governing permissions and
	limitations under the License.
*/

package com.commonsware.android.fancylists.five;

import android.app.Activity;
import android.os.Bundle;
import android.app.ListActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class ViewWrapperDemo extends ListActivity {
	TextView selection;
	String[] items={"lorem", "ipsum", "dolor", "sit", "amet",
					"consectetuer", "adipiscing", "elit", "morbi", "vel",
					"ligula", "vitae", "arcu", "aliquet", "mollis",
					"etiam", "vel", "erat", "placerat", "ante",
					"porttitor", "sodales", "pellentesque", "augue",
					"purus"};
	
	@Override
	public void onCreate(Bundle icicle) {
		super.onCreate(icicle);
		setContentView(R.layout.main);
		setListAdapter(new IconicAdapter(this));
		selection=(TextView)findViewById(R.id.selection);
	}
	
	private String getModel(int position) {
		return(((IconicAdapter)getListAdapter()).getItem(position));
	}
	
	public void onListItemClick(ListView parent, View v,
															int position, long id) {
	 	selection.setText(getModel(position));
	}
	
	class IconicAdapter extends ArrayAdapter<String> {
		Activity context;
		
		IconicAdapter(Activity context) {
			super(context, R.layout.row, items);
			
			this.context=context;
		}
		
		public View getView(int position, View convertView,
												ViewGroup parent) {
			View row=convertView;
			ViewWrapper wrapper=null;
			
			if (row==null) {													
				LayoutInflater inflater=context.getLayoutInflater();
				
				row=inflater.inflate(R.layout.row, null);
				wrapper=new ViewWrapper(row);
				row.setTag(wrapper);
			}
			else {
				wrapper=(ViewWrapper)row.getTag();
			}
			
			wrapper.getLabel().setText(getModel(position));
			
			if (getModel(position).length()>4) {
				wrapper.getIcon().setImageResource(R.drawable.delete);
			}	
			else {
				wrapper.getIcon().setImageResource(R.drawable.ok);
			}
			
			return(row);
		}
	}
}
