package com.yaoli.smsread;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: ling
 * Date: 13-4-29
 * Time: 下午3:29
 * To change this template use File | Settings | File Templates.
 */
public class DetailListViewAdapter extends BaseAdapter{
    private Context context;
    private List<Map<String, Object>> listItems;
    private LayoutInflater listContainer;

    private static final String TAG="DetailListViewAdapter";

    public static final class ViewHolder
    {
        public  LinearLayout ly_item;
        public TextView tv_date;
        public TextView tv_content;
    }

    public DetailListViewAdapter(Context context, List<Map<String, Object>> listItems)
    {
        this.context = context;
        listContainer = LayoutInflater.from(context);
        this.listItems = listItems;
        Log.d(TAG, "DetailListViewAdapter OK.ItemSize=" + getCount());
    }

    @Override
    public int getCount() {
        return  listItems.size();
    }

    @Override
    public Object getItem(int i) {
        return listItems.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void setupHolder(int position, View convertView, ViewHolder holder)
    {
        holder.tv_date = (TextView)convertView.findViewById(R.id.item_date);
        holder.tv_content = (TextView)convertView.findViewById(R.id.item_content);
        holder.ly_item = (LinearLayout)convertView.findViewById(R.id.sms_item);

        holder.tv_date.setText((String)listItems.get(position).get("ItemDate"));
        holder.tv_content.setText((String)listItems.get(position).get("ItemText"));

        Object read = listItems.get(position).get("ItemRead");
        if(read == null)
        {
            holder.ly_item.setBackgroundColor(Color.GRAY);
        }
        else
        {
            holder.ly_item.setBackgroundColor(Color.WHITE);
        }
        /*LinearLayout layout = (LinearLayout)convertView.findViewById(R.id.sms_item);

        if(read != null)
        {
            //未读短信
            layout.setBackgroundColor(Color.GRAY);
        }
        else
        {
            layout.setBackgroundColor(Color.WHITE);
        }*/
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent)
    {
        ViewHolder holder = null;
        Log.d(TAG, "getView postion="+position);
        if(convertView == null)
        {
            Log.d(TAG, "convertView = null");
            holder = new ViewHolder();
            convertView = listContainer.inflate(R.layout.sms_item, null);
            setupHolder(position, convertView, holder);
            convertView.setTag(holder);
        }
        else
        {
            holder = (ViewHolder)convertView.getTag();
            //convertView = setupHolder(position, convertView, holder);
        }
        return convertView;
    }

}
