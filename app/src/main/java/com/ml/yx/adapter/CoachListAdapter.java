package com.ml.yx.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ml.yx.R;
import com.ml.yx.comm.CommUtil;
import com.ml.yx.model.CoachListBean.CoachBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by BTyang on 16/4/11.
 */
public class CoachListAdapter extends BaseAdapter {

    private Context context;
    private List<CoachBean> coachBeanList = new ArrayList<>();

    public CoachListAdapter(Context context, List<CoachBean> coachBeanList) {
        this.context = context;
        this.coachBeanList = coachBeanList;
    }

    @Override
    public int getCount() {
        return coachBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return coachBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.item_coach, null);
            viewHolder.name = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.avatar = (ImageView) convertView.findViewById(R.id.iv_avatar);
            viewHolder.lock = (ImageView) convertView.findViewById(R.id.iv_lock);
            viewHolder.mask = convertView.findViewById(R.id.mask);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        CoachBean coach = coachBeanList.get(position);
        viewHolder.name.setText(coach.getName());
        viewHolder.avatar.setImageDrawable(CommUtil.getCoachDrawable(context, coach.getInstructorId()));
//        WebDataLoader.getInstance(context).loadImageView(viewHolder.avatar, coach.getAvatar());
        if (coach.isUnlock()) {
            viewHolder.mask.setVisibility(View.GONE);
            viewHolder.lock.setVisibility(View.GONE);
        } else {
            viewHolder.mask.setVisibility(View.VISIBLE);
            viewHolder.lock.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    class ViewHolder {
        TextView name;
        ImageView avatar;
        ImageView lock;
        View mask;
    }
}
