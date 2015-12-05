package com.railgadi.fragments;

import android.content.Context;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.larvalabs.svgandroid.SVG;
import com.larvalabs.svgandroid.SVGParser;
import com.railgadi.R;
import com.railgadi.activities.MainActivity;
import com.railgadi.adapters.SeatMapSpinnerAdapter;
import com.railgadi.beans.SeatMapDataBean;
import com.railgadi.beans.SeatMapStaticBean;
import com.railgadi.utilities.Constants;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;


public class SeatMapFragment extends Fragment {


    private View rootView;
    private Spinner classSpinner;
    private ViewPager seatMapPager;
    private SeatMapSpinnerAdapter adapter;
    private LinearLayout imageContainer;
    private LayoutInflater inflater;
    private int totalPos = 1;
    private Random rnd;
    private List<SeatMapDataBean> seatMapList;
    private TextView header, totalSeat, coachNumbering ;
    private List<TextView> dotList;

    private int defaultSelection ;

    public SeatMapFragment(int defaultSelection) {

        this.defaultSelection   =   defaultSelection ;
    }



    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.seat_map_fragment, container, false);

        MainActivity.toolbar.setTitle(getActivity().getResources().getString(R.string.seat_map).toUpperCase());

        initializeAllViews();
        setDataOnViews();
        setIndicators(0);

        classSpinner.setSelection(defaultSelection);

        return rootView;
    }


    private void initializeAllViews() {

        seatMapPager = (ViewPager) rootView.findViewById(R.id.seat_map_pager);

        classSpinner = (Spinner) rootView.findViewById(R.id.seat_list_spinner);

        imageContainer = (LinearLayout) rootView.findViewById(R.id.seat_dot_layout);

        header  = (TextView) rootView.findViewById(R.id.header_seat_map) ;

        totalSeat   =   (TextView) rootView.findViewById(R.id.total_seat) ;

        coachNumbering  =   (TextView) rootView.findViewById(R.id.coach_numbering) ;

        inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        rnd = new Random();

        dotList = new ArrayList<>();

    }


    private SeatMapDataBean selectedBean ;

    private void setDataOnViews() {

        seatMapList = getData();

        adapter = new SeatMapSpinnerAdapter(getActivity(), seatMapList);

        classSpinner.setAdapter(adapter);

        totalPos = 1;
        classSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selectedBean = seatMapList.get(position) ;
                header.setText(selectedBean.getHeaderList().get(0).getTitle());
                coachNumbering.setText(selectedBean.getHeaderList().get(0).getCoachNu());
                totalSeat.setText(selectedBean.getHeaderList().get(0).getTotal());

                totalPos = Integer.parseInt(selectedBean.getNoOfPage());

                setIndicators(position);

                seatMapPager.setAdapter(new MyPagesAdapter(getActivity(), seatMapList.get(position)));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        seatMapPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                header.setText(selectedBean.getHeaderList().get(position).getTitle());
                coachNumbering.setText(selectedBean.getHeaderList().get(position).getCoachNu());
                totalSeat.setText(selectedBean.getHeaderList().get(position).getTotal());

                for (int i = 0; i < dotList.size(); i++) {
                    if (i == position) {
                        dotList.get(i).setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
                    } else {
                        dotList.get(i).setTextColor(getActivity().getResources().getColor(R.color.light_gray));
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    private void setIndicators(int position) {

        imageContainer.removeAllViews();
        dotList.clear();

        int first = Integer.parseInt(seatMapList.get(position).getNoOfPage());

        for (int i = 0; i < first; i++) {

            TextView textview = new TextView(getActivity());
            textview.setGravity(Gravity.TOP);
            textview.setText(Html.fromHtml("<big><b>.</b></big>"));
            textview.setTypeface(null, Typeface.BOLD);
            if (i == 0) {
                textview.setTextColor(getActivity().getResources().getColor(R.color.colorPrimary));
            } else {
                textview.setTextColor(getActivity().getResources().getColor(R.color.light_gray));
            }

            dotList.add(textview);

            imageContainer.addView(textview);
        }
    }


    private List<SeatMapDataBean> getData() {

        List<SeatMapDataBean> seatMapList = new ArrayList<>();

        String[] name = {"First AC", "Second AC", "Third AC", "Sleeper Class",
                "Janshatabdi AC Chair Car", "Janshatabdi Non-AC Sitting", "Shatabdi Chair Car", "Shatabdi Executive",
                "Double Decker", "First Class Non-AC", "GaribRath 3A"} ;
        String[] number = {"4", "4", "4", "2", "3", "2","2","1","1","1","1"} ;

        SeatMapStaticBean [] hfa       =   {Constants.FIRST_AC_ONE_O, Constants.FIRST_AC_TWO_O, Constants.FIRST_AC_THREE_O, Constants.FIRST_AC_FOUR_O} ;
        SeatMapStaticBean [] hsa       =   {Constants.SECOND_AC_ONE_O, Constants.SECOND_AC_TWO_O, Constants.SECOND_AC_THREE_O, Constants.SECOND_AC_FOUR_O} ;
        SeatMapStaticBean [] hta       =   {Constants.THIRD_AC_ONE_O, Constants.THIRD_AC_TWO_O, Constants.THIRD_AC_THREE_O, Constants.THIRD_AC_FOUR_O} ;
        SeatMapStaticBean [] hss       =   {Constants.SLEEP_CLASS_ONE_O, Constants.SLEEP_CLASS_TWO_O} ;
        SeatMapStaticBean [] hjsAcCh   =   {Constants.JNSH_AC_CC_ONE_O, Constants.JNSH_AC_CC_TWO_O, Constants.JNSH_AC_CC_THREE_O} ;
        SeatMapStaticBean [] hjsNac    =   {Constants.JNSH_NON_AC_ONE_O, Constants.JNSH_NON_AC_TWO_O} ;
        SeatMapStaticBean [] hshtcc    =   {Constants.SHTB_CC_ONE_O, Constants.SHTB_CC_TWO_O} ;
        SeatMapStaticBean [] hshExc    =   {Constants.SHTB_EX_O} ;
        SeatMapStaticBean [] hdd       =   {Constants.DOUBLE_DACKER_O} ;
        SeatMapStaticBean [] hfcNa     =   {Constants.FC_NON_AC_O} ;
        SeatMapStaticBean [] hgrb      =   {Constants.GARIB_RATH_3A_O} ;

        SeatMapStaticBean [][] headers = {hfa, hsa, hta, hss, hjsAcCh, hjsNac, hshtcc, hshExc, hdd, hfcNa, hgrb} ;

        int [] fa = {R.drawable._1a_icf_one, R.drawable._1a_lhb_two, R.drawable._1a_2a_icf_three, R.drawable._1a_3a_icf_four} ;
        int [] sa = {R.drawable._2a_icf_one, R.drawable._2a_lbh_two, R.drawable._1a_2a_icf_three, R.drawable._2a_3a_icf_four} ;
        int [] ta = {R.drawable._3a_icf_one, R.drawable._3a_lbh_two, R.drawable._1a_3a_icf_three, R.drawable._2a_3a_icf_four} ;
        int [] ss = {R.drawable._sl_icf_one, R.drawable._sl_lbh_two} ;
        int [] jsAcCh = {R.drawable.jnshtbdi_layout_1, R.drawable.jnshtbdi_layout_2, R.drawable.jnshtbdi_layout_3} ;
        int [] jsNac = {R.drawable.layout_one, R.drawable._icf_1} ;
        int [] shtcc = {R.drawable._shatabdi_lhb, R.drawable._shatabdi_cc} ;
        int [] shExc = {R.drawable.ec_icf} ;
        int [] dd = {R.drawable.double_dacker} ;
        int [] fcNa = {R.drawable.fc} ;
        int [] grbrt3A = {R.drawable.garib_one} ;

        int [][] imgArr = {fa, sa, ta, ss, jsAcCh, jsNac, shtcc, shExc, dd, fcNa, grbrt3A} ;

        for (int i = 0; i < name.length; i++) {
            SeatMapDataBean bean = new SeatMapDataBean();

            bean.setName(name[i]);
            bean.setNoOfPage(number[i]);

            int [] arr = imgArr[i] ;
            SeatMapStaticBean [] srr = headers[i] ;

            List<Integer> pagesList = new ArrayList<>();

            List<SeatMapStaticBean> hList = new ArrayList<>() ;

            for (int j = 0; j < arr.length; j++) {
                pagesList.add(arr[j]);
            }

            for(int k = 0 ; k < srr.length ; k++) {
                hList.add(srr[k]);
            }

            bean.setPages(pagesList);
            bean.setHeaderList(hList);

            if(i == 0) {
                selectedBean = bean ;
            }

            seatMapList.add(bean);
        }

        return seatMapList;
    }


    private class MyPagesAdapter extends PagerAdapter {

        int total = 0;

        private SeatMapDataBean bean ;
        private Context context ;

        public MyPagesAdapter(Context context, SeatMapDataBean bean) {
            this.bean = bean ;
            this.context = context ;
        }

        public void set_value(int total) {
            this.total = total;
        }

        @Override
        public int getCount() {
            //Return total pages, here one for each data item

            return bean.getPages().size();
            // return Integer.MAX_VALUE;
        }

        //Create the given page (indicated by position)
        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            View page = inflater.inflate(R.layout.seat_map_pager_items, null);

            ImageView imageView = (ImageView) page.findViewById(R.id.seat_map_image);

            ((ViewPager) container).addView(page, 0);

            imageView.setImageDrawable(getSvgDrawable(bean.getPages().get(position))) ;

            return page;
        }

        // method for creating svg drawable
        public Drawable getSvgDrawable(int imageId) {
            SVG svg = SVGParser.getSVGFromResource(context.getResources(), imageId);
            return svg.createPictureDrawable();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == (View) arg1;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
            object = null;
        }
    }
}
