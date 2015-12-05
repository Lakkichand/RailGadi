package com.railgadi.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.railgadi.R;
import com.railgadi.fonts.AppFonts;

public class CustomerCareAdapter extends BaseAdapter {

    private Context context ;
    private String [] dept, numbers ;
    private LayoutInflater inflater ;

    private int lastPosition = -1 ;

    public CustomerCareAdapter(Context context, String [] dept, String [] numbers) {

        this.context        =   context ;
        this.dept           =   dept;
        this.numbers        =   numbers ;

        this.inflater       =   (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) ;
    }

    public void changeDataSet(String [] dept, String [] numbers) {
        this.dept           =   dept ;
        this.numbers        =   numbers ;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return dept.length ;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if(convertView == null) {

            convertView     =   inflater.inflate(R.layout.railway_number_list_row, null) ;
        }

        TextView name       =   (TextView) convertView.findViewById(R.id.railway_name) ;
        TextView number     =   (TextView) convertView.findViewById(R.id.railway_number) ;

        name.setText(dept[position]);
        number.setText(numbers[position]);

        name.setTypeface(AppFonts.getRobotoLight(context));
        number.setTypeface(AppFonts.getRobotoLight(context));

        Animation animation = AnimationUtils.loadAnimation(context, (position > lastPosition) ? R.anim.up_from_bottom : R.anim.down_from_top);
        convertView.startAnimation(animation);
        lastPosition = position;

        return convertView ;
    }
}
