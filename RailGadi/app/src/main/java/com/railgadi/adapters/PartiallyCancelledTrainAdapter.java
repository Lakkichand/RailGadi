package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.CancelledTrainBean.PartiallyCancelled;

import java.util.List;

public class PartiallyCancelledTrainAdapter extends BaseAdapter {

    private Context context ;
    private List<PartiallyCancelled> data ;
    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public PartiallyCancelledTrainAdapter(Context context, List<PartiallyCancelled> data) {

        this.context        =   context ;
        this.data           =   data ;
        this.inflater       =   LayoutInflater.from(context) ;
    }

    public void changeDataSet(List<PartiallyCancelled> data) {
        this.data           =   data ;
        notifyDataSetChanged() ;
    }

    @Override
    public int getCount() {
        return data.size() ;
    }

    @Override
    public Object getItem(int position) {
        return data.get(position) ;
    }

    @Override
    public long getItemId(int position) {
        return position ;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null ;

        if(convertView == null) {

            holder                  =   new ViewHolder() ;

            convertView             =   inflater.inflate(R.layout.cancelled_list_layout, null) ;

            holder.partialLayout    =   (LinearLayout) convertView.findViewById(R.id.partial_layout) ;

            holder.trainName        =   (TextView) convertView.findViewById(R.id.train_name) ;
            holder.trainNo          =   (TextView) convertView.findViewById(R.id.train_number) ;
            holder.trainType        =   (TextView) convertView.findViewById(R.id.train_type) ;
            holder.dest             =   (TextView) convertView.findViewById(R.id.dest_name) ;
            holder.src              =   (TextView) convertView.findViewById(R.id.src_name) ;
            holder.from             =   (TextView) convertView.findViewById(R.id.cancel_from) ;
            holder.to               =   (TextView) convertView.findViewById(R.id.cancel_to) ;

            convertView.setTag(holder) ;

        } else {
            holder                  =   (ViewHolder) convertView.getTag() ;
        }

        PartiallyCancelled pc           =   (PartiallyCancelled) getItem(position) ;

        holder.partialLayout.setVisibility(View.VISIBLE) ;

        holder.trainName.setText(pc.trainName) ;
        holder.trainNo.setText(pc.trainNo) ;
        holder.trainType.setText(pc.trainType.toUpperCase()) ;
        holder.from.setText(pc.cancelledFrom) ;
        holder.to.setText(pc.cancelledTo) ;
        holder.src.setText(pc.srcStationName) ;
        holder.dest.setText(pc.destStationName) ;

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }

    private static class ViewHolder {
        TextView trainNo, trainName, trainType, from, to, src, dest ;
        LinearLayout partialLayout ;
    }

}
