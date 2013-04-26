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
                // context.getResources().getDimensionPixelSize(R.dimen.popmenu_width),  //这里宽度需要自己指定，使用 WRAP_CONTENT 会很大
                context.getResources().getDisplayMetrics().widthPixels/2-50,
                LayoutParams.WRAP_CONTENT);
        Log.d(TAG, "PopupWindow OK");
        // 这个是为了点击“返回Back”也能使其消失，并且并不会影响你的背景（很神奇的）
        popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        /*if (listener != null) {
                listener.onItemClick(position);
        }*/
        dismiss();
        //跳转到视图的Activity
        Intent intent = new Intent(context, SmsViewActivity.class);
        context.startActivity(intent);
    }

    // 设置菜单项点击监听器
    public void setOnItemClickListener(OnItemClickListener listener) {
        //this.listener = listener;
    }


    // 批量添加菜单项
    public void addItems(String[] items) {
        for (String s : items)
            itemList.add(s);
    }

    // 单个添加菜单项
    public void addItem(String item) {
        itemList.add(item);
    }

    // 下拉式 弹出 pop菜单 parent 右下角
    public void showAsDropDown(View parent) {
        int screenWidth = context.getResources().getDisplayMetrics().widthPixels;//屏幕宽度
        popupWindow.showAsDropDown(parent,
                2*parent.getWidth(),
                10);//垂直距离

        // 使其聚集
        popupWindow.setFocusable(true);
        // 设置允许在外点击消失
        popupWindow.setOutsideTouchable(true);
        // 刷新状态
        popupWindow.update();
    }

    // 隐藏菜单
    public void dismiss() {
        popupWindow.dismiss();
    }


    //适配器
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
