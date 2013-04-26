package com.yaoli.smsread;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;


public class PopMenu implements OnItemClickListener {
	/*public interface OnItemClickListener {
        public void onItemClick(int index);
	}*/
	//
	private ArrayList<String> itemList;
	private Context context;
    private PopupWindow popupWindow;
    private ListView listView;
    //private OnItemClickListener listener;
    private LayoutInflater inflater;
    private static final String TAG = "PopMenu";
    public PopMenu(Context context) {
        this.context = context;
        Log.d(TAG, "PopMenu new");
        itemList = new ArrayList<String>();

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pop_menu, null);

        listView = (ListView) view.findViewById(R.id.pop_list_view);
        listView.setAdapter(new PopAdapter());
        listView.setOnItemClickListener(this);

        popupWindow = new PopupWindow(view, 
                       // context.getResources().getDimensionPixelSize(R.dimen.popmenu_width),  //???????????????????? WRAP_CONTENT ?????
        		LayoutParams.WRAP_CONTENT,
        		LayoutParams.WRAP_CONTENT);
        Log.d(TAG, "PopupWindow OK");
        // ????????????????Back?????????????????????????????????????????
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*if (listener != null) {
                listener.onItemClick(position);
        }*/
        dismiss();
		//??????????Activity
	 	Intent intent = new Intent(context, SmsViewActivity.class);
	 	context.startActivity(intent);
}

// ???ò????????????
public void setOnItemClickListener(OnItemClickListener listener) {
         //this.listener = listener;
}


// ????????????
public void addItems(String[] items) {
        for (String s : items)
                itemList.add(s);
}

// ????????????
public void addItem(String item) {
        itemList.add(item);
}

// ????? ???? pop??? parent ?????
public void showAsDropDown(View parent) {
		int screenWidth = context.getResources().getDisplayMetrics().widthPixels;//???????
        popupWindow.showAsDropDown(parent,
        		 2*parent.getWidth(),
        		 20);//???????

        // ??????
        popupWindow.setFocusable(true);
        // ?????????????????
        popupWindow.setOutsideTouchable(true);
        // ?????
        popupWindow.update();
	}

// ??????
public void dismiss() {
        popupWindow.dismiss();
	}


//??????
private final class PopAdapter extends BaseAdapter {
        @Override
        public int getCount() {
                return itemList.size();
        }

        @Override
        public Object getItem(int position) {
                return itemList.get(position);
        }

        @Override
        public long getItemId(int position) {
                return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
                ViewHolder holder;
                if (convertView == null) {
                        convertView = inflater.inflate(R.layout.pop_menu_item, null);
                        holder = new ViewHolder();
                        convertView.setTag(holder);
                        holder.groupItem = (TextView) convertView.findViewById(R.id.pop_item_text_view);
                } else {
                        holder = (ViewHolder) convertView.getTag();
                }
                holder.groupItem.setText(itemList.get(position));

                return convertView;
        }

        private final class ViewHolder {
                TextView groupItem;
        }
}

}
