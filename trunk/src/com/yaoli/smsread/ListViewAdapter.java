package com.yaoli.smsread;

import java.util.List;
import java.util.Map;

import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.yaoli.smsread.R;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ListViewAdapter extends BaseAdapter{
	private Context context;
	private List<Map<String, Object>> listItems;
	private LayoutInflater listContainer;
	
	public static final int ITEM_TYPE_CREATE = 0;
	public static final int ITEM_TYPE_NORMAL = 1;
	public final String TAG = "ListViewAdapter";
	public static final class ViewHolder
	{
		public ImageView imageHead;
		public ImageView imageCreateMsg;
		//public TextView tv_address;
		public TextView tv_contact;
		public TextView tv_count;
		public TextView tv_date;
		public TextView tv_content;
		
		public TextView tv_create_msg;
	}
	public ListViewAdapter(Context context, List<Map<String, Object>> listItems)
	{
		this.context = context;
		listContainer = LayoutInflater.from(context);
		this.listItems = listItems;
		Log.d(TAG, "ListViewAdapter OK.ItemSize="+getCount());
	}

	public int getCount()
	{
		return listItems.size();
	}
	public int getItemViewType(int position)
	{
		if(position == 0) return ITEM_TYPE_CREATE;
		else return ITEM_TYPE_NORMAL;
	}
    //生成带有未读数字的头像
   private Bitmap genHeadImageWithCount(Drawable drawable, String strCount)
   {
       int w = drawable.getIntrinsicWidth();
       int h = drawable.getIntrinsicHeight();
       Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
       if(strCount.length()==0) return bitmap;//没有未读计数
       Canvas canvasTemp = new Canvas(bitmap);
       Rect rect = new Rect();
       Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
       paint.setTextSize(40);
       paint.getTextBounds(strCount, 0, strCount.length(), rect);
       paint.setColor(Color.RED);
       paint.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
       drawable.setBounds(0, 0, w, h);
       canvasTemp.setBitmap(bitmap);
       drawable.draw(canvasTemp);
       float radius = w/5;
       float x = w-radius, y=radius;
       paint.setColor(Color.WHITE);
       canvasTemp.drawCircle(x,  y, radius+4, paint);
       paint.setColor(Color.RED);
       canvasTemp.drawCircle(x,  y, radius, paint);
       paint.setColor(Color.WHITE);
       canvasTemp.drawText(strCount, w - radius-rect.width()/2-3,  radius+rect.height()/2, paint);
       canvasTemp.save();
       canvasTemp = null;
       return bitmap;
   }
	public void setupNormalHolder(int position, View convertView, ViewHolder holder)
	{
		 holder.imageHead = (ImageView)convertView.findViewById(R.id.Image);
		 holder.tv_contact = (TextView)convertView.findViewById(R.id.Contact);
		 //holder.tv_address = (TextView)convertView.findViewById(R.id.Address);
		 holder.tv_count = (TextView)convertView.findViewById(R.id.Count);
		 holder.tv_date = (TextView)convertView.findViewById(R.id.Date);
		 holder.tv_content = (TextView)convertView.findViewById(R.id.Content);

        holder.imageHead.setImageBitmap(genHeadImageWithCount(context.getResources().getDrawable(R.drawable.image_head),
                (String)listItems.get(position-1).get("UnRead")));

		 holder.imageHead.setBackgroundResource(R.drawable.image_head);
		 holder.tv_contact.setText((String)listItems.get(position-1).get("Contact"));
		 //holder.tv_address.setText((String)listItems.get(position-1).get("Address"));
		 holder.tv_count.setText((String)listItems.get(position-1).get("Count"));
		 holder.tv_date.setText((String)listItems.get(position-1).get("Date"));
		 holder.tv_content.setText((String)listItems.get(position-1).get("Content"));
	}
	public void setupCreateHolder(int position, View convertView, ViewHolder holder)
	{
		holder.imageCreateMsg = (ImageView)convertView.findViewById(R.id.CreateImage);
		holder.tv_create_msg = (TextView)convertView.findViewById(R.id.CreateMsg);
		holder.imageCreateMsg.setBackgroundResource(R.drawable.create_icon);
		holder.tv_create_msg.setText("新建短消息");
	}
	public View setupHolder(int position, View convertView, ViewHolder holder)
	{
		if(getItemViewType(position) == ITEM_TYPE_CREATE)
		{
			convertView = listContainer.inflate(R.layout.main_item_create, null);
			setupCreateHolder(position, convertView, holder);
		}
		else
		{
			convertView = listContainer.inflate(R.layout.main_item, null);
			setupNormalHolder(position, convertView, holder);
		}
		return convertView;
	}
	public View getView(int position, View convertView, ViewGroup parent)
	{
		 ViewHolder holder = null;
		 Log.d(TAG, "getView postion="+position);
		 if(convertView == null)
		 {
			 Log.d(TAG, "convertView = null");
			 holder = new ViewHolder();
			 convertView = setupHolder(position, convertView, holder);
			 convertView.setTag(holder);
		 }
		 else
		 {
			 holder = (ViewHolder)convertView.getTag();
			 if(getItemViewType(position) == ITEM_TYPE_CREATE)
				 setupCreateHolder(position, convertView, holder);
			 else
				 setupNormalHolder(position, convertView, holder);
		 }
		 return convertView;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		if(position < getCount() && position > 0)
			return listItems.get(position-1);
		else
			return null;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position-1;
	}
	public int getViewTypeCount() {
		return 2;
	}
	public boolean isEnabled(int position) {
		//return getItemViewType(position) == ITEM_TYPE_NORMAL;
		return true;
	}
}
