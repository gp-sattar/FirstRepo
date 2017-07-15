package com.grampower.attendance.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.grampower.attendance.R;
import com.grampower.attendance.pojos.NotificationWrapper;
import com.ms.square.android.expandabletextview.ExpandableTextView;

import java.util.List;

/**
 * Created by sam on 4/7/17.
 */

public class NotificationRecyclerAdapter extends  RecyclerView.Adapter<NotificationRecyclerAdapter.viewHolder> {

  List<NotificationWrapper>  notificationList;
   Context context;

    public NotificationRecyclerAdapter(List<NotificationWrapper> notificationList, Context context) {
        this.notificationList = notificationList;
        this.context = context;
    }

    @Override
    public viewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notifications_child_view,parent,false);
        return  new viewHolder(view);
    }

    @Override
    public void onBindViewHolder(viewHolder holder, int position) {
        NotificationWrapper notification=notificationList.get(position);
       // holder.mNotification.setText(notification.getNotification());
          holder.mExpandableTextView.setText(notification.getNotification());
        holder.mTimeDiff.setText(notification.getKey());
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }


    class viewHolder extends RecyclerView.ViewHolder{
         TextView mNotification,mTimeDiff;
         ExpandableTextView mExpandableTextView;
        public viewHolder(View itemView) {
            super(itemView);
           // mNotification=(TextView)itemView.findViewById(R.id.notification_line);
            mExpandableTextView=(ExpandableTextView)itemView.findViewById(R.id.expand_text_view);
            mTimeDiff=(TextView)itemView.findViewById(R.id.notify_time);
        }
    }
}
