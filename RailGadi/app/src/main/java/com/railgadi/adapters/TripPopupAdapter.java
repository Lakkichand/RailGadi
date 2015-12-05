package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.beans.PnrStatusNewBean;

import java.util.ArrayList;
import java.util.List;

public class TripPopupAdapter extends BaseAdapter {

    private Context context ;
    private List<PnrStatusNewBean> pnrList ;

    private int lastPosition = -1 ;

    public TripPopupAdapter(Context context, List<PnrStatusNewBean> pnrList) {

        this.context = context ;
        this.pnrList = pnrList ;
    }

    @Override
    public int getCount() {
        return pnrList.size();
    }

    @Override
    public Object getItem(int position) {
        return pnrList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.msg_popup_list_row, null) ;
        }

        PnrStatusNewBean model = (PnrStatusNewBean) getItem(position) ;

        TextView pnr        = (TextView) convertView.findViewById(R.id.popup_pnr_number);
        TextView fromTo     = (TextView) convertView.findViewById(R.id.popup_from_to);
        CheckBox checkBox   = (CheckBox) convertView.findViewById(R.id.popup_check_box) ;

        try {

            pnr.setText("PNR : " + new StringBuffer(model.getPnrNumber()).insert(3, "-").insert(7, "-"));
            fromTo.setText(model.getBoardFromName() + " to " + model.getBoardToName());

        } catch(Exception e) {

        }

        if(model != null) {
            checkBox.setOnCheckedChangeListener(myCheckChangeListener);
            checkBox.setChecked(model.getCheckMark());
            checkBox.setTag(position);
        }

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }

    public PnrStatusNewBean getModel(int position ) {
        return ((PnrStatusNewBean) getItem(position)) ;
    }

    public List<PnrStatusNewBean> getAll() {
        return pnrList ;
    }

    public List<PnrStatusNewBean> getUnselectedItem() {
        List<PnrStatusNewBean> l = new ArrayList<>();
        for(PnrStatusNewBean m : pnrList) {
            if( ! m.getCheckMark()) {
                l.add(m);
            }
        }
        return l ;
    }

    public List<PnrStatusNewBean> getSelectedItem() {
        List<PnrStatusNewBean> l = new ArrayList<>();
        for(PnrStatusNewBean m : pnrList) {
            if(m.getCheckMark()) {
                l.add(m);
            }
        }
        return l ;
    }

    CompoundButton.OnCheckedChangeListener myCheckChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            getModel((Integer)buttonView.getTag()).setCheckMark(isChecked) ;
        }
    } ;
}
