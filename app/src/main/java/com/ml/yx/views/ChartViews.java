package com.ml.yx.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.ml.yx.R;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lecho.lib.hellocharts.model.Axis;
import lecho.lib.hellocharts.model.AxisValue;
import lecho.lib.hellocharts.model.Line;
import lecho.lib.hellocharts.model.LineChartData;
import lecho.lib.hellocharts.model.PointValue;
import lecho.lib.hellocharts.model.ValueShape;
import lecho.lib.hellocharts.model.Viewport;
import lecho.lib.hellocharts.view.LineChartView;

/**
 * Created by Lijj on 16/4/10.
 */
public class ChartViews extends FrameLayout {
    private LineChartView lineChartView;

    private LineChartData data;

    private boolean hasAxes = true;
    private boolean hasAxesNames = false;
    private boolean hasLines = true;
    private boolean hasPoints = true;
    private ValueShape shape = ValueShape.CIRCLE;
    private boolean isFilled = true;
    private boolean hasLabels = false;
    private boolean isCubic = false;
    private boolean hasLabelForSelected = false;

    public static class ChartData implements Serializable,Comparable<ChartData>{
        public String x;
        public int y;

        public ChartData(String x,int y){
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(ChartData another) {
            if(another == null){
                return 1;
            }
            if(x.equals(another.x)){
                return -1;
            }else{
                return 1;
            }
        }

        @Override
        public boolean equals(Object o) {
            if(o == null){
                return false;
            }
            if(o instanceof ChartData){
                ChartData obj = (ChartData) o;
                if(y == obj.y){
                    return true;
                }
            }
            return false;
        }

        @Override
        public int hashCode() {
            return 31 + x.hashCode();
        }

    }

    public ChartViews(Context context) {
        super(context, null);
        initViews(context);
    }

    public ChartViews(Context context, AttributeSet attrs) {
        super(context, attrs);
        initViews(context);
    }

    public ChartViews(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initViews(context);
    }

    private void initViews(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_chart, this);
        lineChartView = (LineChartView) findViewById(R.id.user_line_chart);
        lineChartView.setValueSelectionEnabled(false);
        lineChartView.setValueTouchEnabled(false);
        lineChartView.setZoomEnabled(false);

        List<ChartData> initDatas = new ArrayList<ChartData>();
        generateData(initDatas);
    }

    public void setDatas(int[] datas) {
//        if(datas == null){
//            return;
//        }
//
//        generateData(datas);
//
//        if(datas.length > 0) {
//            Arrays.sort(datas);
//            toggleCubic((int) (datas[datas.length - 1] * 1.5));
//        }
    }

    public void setDatas(List<ChartData> datas){
        if(datas == null || datas.isEmpty()){
            return;
        }
        generateData(datas);

        if(datas.size() > 0) {
            Collections.sort(datas);
            ChartData bean = datas.get(0);
            toggleCubic((int) (bean.y * 1.5));
        }
    }

    private void generateData(List<ChartData> mData) {
        int lineColor = getContext().getResources().getColor(R.color.yellow);

        List<Line> lines = new ArrayList<Line>();

        List<PointValue> values = new ArrayList<PointValue>();
        List<AxisValue> axisValues = new ArrayList<AxisValue>();

        int size = mData.size();
        for (int i = 0; i < size; ++i) {
            ChartData bean = mData.get(i);
            values.add(new PointValue(i, bean.y));
            axisValues.add(new AxisValue(i).setLabel(bean.x));
        }

        Line line = new Line(values);
        line.setColor(lineColor);
        line.setShape(shape);
        line.setCubic(isCubic);
        line.setFilled(isFilled);
        line.setHasLabels(hasLabels);
        line.setHasLabelsOnlyForSelected(hasLabelForSelected);
        line.setHasLines(hasLines);
        line.setHasPoints(hasPoints);
        line.setPointColor(lineColor);

        lines.add(line);

        data = new LineChartData(lines);

        if (hasAxes) {
            Axis axisX = new Axis(axisValues);
            Axis axisY = new Axis().setHasLines(false);

            axisX.setHasLines(false);
            if (hasAxesNames) {
                axisX.setName("Axis X");
                axisY.setName("Axis Y");
            }
            data.setAxisXBottom(axisX);
//            data.setAxisYLeft(axisY);
        } else {
            data.setAxisXBottom(null);
            data.setAxisYLeft(null);
        }

        data.setBaseValue(Float.NEGATIVE_INFINITY);
        lineChartView.setLineChartData(data);

    }

    private void toggleCubic(int max) {
        final Viewport v = new Viewport(lineChartView.getMaximumViewport());
        v.bottom = 0;
        v.top = max;
        // You have to set max and current viewports separately.
        lineChartView.setMaximumViewport(v);
        // I changing current viewport with animation in this case.
        lineChartView.setCurrentViewportWithAnimation(v);
    }
}
