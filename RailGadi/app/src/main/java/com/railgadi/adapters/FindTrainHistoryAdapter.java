package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.FindTrainHistoryContainer;
import com.railgadi.fonts.AppFonts;

import java.util.List;

public class FindTrainHistoryAdapter extends BaseAdapter {

    private List<FindTrainHistoryContainer> historyList ;
    private Context context ;
    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public FindTrainHistoryAdapter(Context context, List<FindTrainHistoryContainer> historyList) {

        this.context = context ;
        this.historyList = historyList ;

        this.inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    @Override
    public int getCount() {
        return historyList.size();
    }

    @Override
    public Object getItem(int position) {
        return historyList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            convertView = inflater.inflate(R.layout.find_train_history_custom_row, null);
        }

        TextView srcCode      =   (TextView) convertView.findViewById(R.id.history_src_code) ;
        TextView srcName      =   (TextView) convertView.findViewById(R.id.history_src_name) ;

        ImageView arrow       =   (ImageView) convertView.findViewById(R.id.history_arrow) ;

        TextView destCode     =   (TextView) convertView.findViewById(R.id.history_dest_code) ;
        TextView destName     =   (TextView) convertView.findViewById(R.id.history_dest_name) ;

        FindTrainHistoryContainer container = historyList.get(position) ;

        String sCode = container.getSrcCode() ;
        String sName = container.getSrcName() ;
        String dCode = container.getDestCode() ;
        String dName = container.getDestName() ;

        srcCode.setText(sCode);
        srcCode.setTypeface(AppFonts.getRobotoLight(context));
        srcName.setText(sName);
        destCode.setText(dCode);
        destCode.setTypeface(AppFonts.getRobotoLight(context));
        destName.setText(dName);
        arrow.setImageAlpha(100);

        //Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        //convertView.startAnimation(animation);
        //lastPosition = position;

        return convertView;
    }
}
