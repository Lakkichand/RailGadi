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
import com.railgadi.beans.CancelledTrainBean.FullyCancelled;

import java.util.List;

public class FullyCancelledTrainAdapter extends BaseAdapter {

    private Context context ;
    private List<FullyCancelled> data ;
    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public FullyCancelledTrainAdapter(Context context, List<FullyCancelled> data) {

        this.context        =   context ;
        this.data           =   data ;
        this.inflater       =   LayoutInflater.from(context) ;
    }

    public void changeDataSet(List<FullyCancelled> data) {
        this.data = data ;
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

        FullyCancelled fc           =   (FullyCancelled) getItem(position) ;

        holder.partialLayout.setVisibility(View.GONE) ;

        holder.trainName.setText(fc.trainName) ;
        holder.trainNo.setText(fc.trainNo) ;
        holder.trainType.setText(fc.trainType.toUpperCase()) ;
        holder.src.setText(fc.srcStationName) ;
        holder.dest.setText(fc.destStationName) ;

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
