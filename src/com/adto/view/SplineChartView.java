/**
 * Copyright 2014  XCL-Charts
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *     http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 	
 * @Project XCL-Charts 
 * @Description Android图表基类�? * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 * @Copyright Copyright (c) 2014 XCL-Charts (www.xclcharts.com)
 * @license http://www.apache.org/licenses/  Apache v2 License
 * @version 1.0
 */
package com.adto.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.xclcharts.chart.PointD;
import org.xclcharts.chart.SplineChart;
import org.xclcharts.chart.SplineData;
import org.xclcharts.common.IFormatterTextCallBack;
import org.xclcharts.event.click.PointPosition;
import org.xclcharts.renderer.XChart;
import org.xclcharts.renderer.XEnum;
import org.xclcharts.renderer.info.AnchorDataPoint;
import org.xclcharts.renderer.plot.PlotGrid;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.Style;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
/**
 * @ClassName SplineChart01View
 * @Description  曲线�?的例�? * @author XiongChuanLiang<br/>(xcl_168@aliyun.com)
 */
public class SplineChartView extends DemoView {

	private String TAG = "SplineChartView";
	private SplineChart chart = new SplineChart();
	//分类轴标签集�?
	private LinkedList<String> labels = new LinkedList<String>();
	private LinkedList<String> labels_vertical = new LinkedList<String>();
	private LinkedList<SplineData> chartData = new LinkedList<SplineData>();
	Paint pToolTip = new Paint(Paint.ANTI_ALIAS_FLAG);
	
	double yMax=50;
	double yMin=0;
	double yStep=10;
	double xMax=10;
	double xMin=0;
	
	
	public SplineChartView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		//initView();
	}
	
	
	public SplineChartView(Context context, AttributeSet attrs){   
        super(context, attrs);   
        initView();
	 }
	 
	 public SplineChartView(Context context, AttributeSet attrs, int defStyle) {
			super(context, attrs, defStyle);
			initView();
	 }
	 
	 private void initView()
	 {
			
			chartDataSet();	
			chartRender();
	 }
	 
	 
	@Override  
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {  
        super.onSizeChanged(w, h, oldw, oldh);  
             
        chart.setChartRange(w,h);
    }  				
	
	
	private void chartRender()
	{
		try {
						
			//设置绘图区默认缩进px�?留置空间显示Axis,Axistitle....		
			int [] ltrb = getBarLnDefaultSpadding();
			chart.setPadding(ltrb[0], ltrb[1], ltrb[2], ltrb[3]);		
			
			//平移时收缩下
			//float margin = DensityUtil.dip2px(getContext(), 20);
			//chart.setXTickMarksOffsetMargin(margin);
			
			//显示边框
			//chart.showRoundBorder();
			
			//数据�?
			chart.setCategories(labels);
			chart.setDataSource(chartData);
						
			//坐标�?			//数据轴最大�?
			chart.getDataAxis().setAxisMax(yMax);
			chart.getDataAxis().setAxisMin(yMin);
			//数据轴刻度间�?			
			chart.getDataAxis().setAxisSteps(yStep);
			
			//标签轴最大�?
			chart.setCategoryAxisMax(xMax);	
			
			//标签轴最小�?
			chart.setCategoryAxisMin(xMin);	
			
			//设置图的背景�?			//chart.setBackgroupColor(true,Color.BLACK);
			//设置绘图区的背景�?			
			//chart.getPlotArea().setBackgroundColor(true, Color.BLACK);;
			
			//背景网格
			PlotGrid plot = chart.getPlotGrid();			
			plot.showHorizontalLines();
			plot.showVerticalLines();			
			plot.getHorizontalLinePaint().setStrokeWidth(1);
			//plot.getHorizontalLinePaint().setColor(Color.rgb(100, 100, 100));
			//plot.getVerticalLinePaint().setColor(Color.rgb(100, 100, 100));
			plot.setHorizontalLineStyle(XEnum.LineStyle.SOLID);
		
			
			//把轴线设成和横向网络线一样和大小和颜�?演示下定制�?，这块问得人较多
			//chart.getDataAxis().getAxisPaint().setStrokeWidth(
			//		plot.getHorizontalLinePaint().getStrokeWidth());
			//chart.getCategoryAxis().getAxisPaint().setStrokeWidth(
			//		plot.getHorizontalLinePaint().getStrokeWidth());
			
			chart.getDataAxis().getAxisPaint().setColor(Color.rgb(127, 204, 204));
			chart.getCategoryAxis().getAxisPaint().setColor(Color.rgb(127, 204, 204));
			
			chart.getDataAxis().getTickMarksPaint().setColor(Color.rgb(127, 204, 204));
			chart.getCategoryAxis().getTickMarksPaint().setColor(Color.rgb(127, 204, 204));
			
			
			//居中
			chart.getDataAxis().setHorizontalTickAlign(Align.CENTER);
			chart.getDataAxis().getTickLabelPaint().setTextAlign(Align.CENTER);
			
			//居中显示�?
			//plot.hideHorizontalLines();
			//plot.hideVerticalLines();	
			//chart.setDataAxisLocation(XEnum.AxisLocation.VERTICAL_CENTER);
			//chart.setCategoryAxisLocation(XEnum.AxisLocation.HORIZONTAL_CENTER);
			
			
			//定义交叉点标签显示格�?特别备注,因曲线图的特殊�?，所以返回格式为:  x�?y�?			//请自行分析定�?			
			chart.setDotLabelFormatter(new IFormatterTextCallBack(){
	
				@Override
				public String textFormatter(String value) {
					// TODO Auto-generated method stub						
					String label = "("+value+")";				
					return (label);
				}
				
			});
			//标题
			//chart.setTitle("Spline Chart");
			//chart.addSubtitle("(XCL-Charts Demo)");
			
			//�?��点击监听
			chart.ActiveListenItemClick();
			//为了让触发更灵敏，可以扩�?px的点击监听范�?			
			chart.extPointClickRange(5);
			chart.showClikedFocus();
			
			//显示十字交叉�?			
			chart.showDyLine();
			chart.getDyLine().setDyLineStyle(XEnum.DyLineStyle.Vertical);
			//扩大实际绘制宽度
			//chart.getPlotArea().extWidth(500.f);
			
			//封闭�?			
			chart.setAxesClosed(true);
			
			//将线显示为直线，而不是平滑的
			chart.setCrurveLineStyle(XEnum.CrurveLineStyle.BEELINE);
			
			//不使用精确计算，忽略Java计算误差,提高性能
			chart.disableHighPrecision();
			
			chart.setBackgroundColor(Color.parseColor("#333"));
			
			//批注
			List<AnchorDataPoint> mAnchorSet = new ArrayList<AnchorDataPoint>();
			
			AnchorDataPoint an1 = new AnchorDataPoint(2,0,XEnum.AnchorStyle.CAPROUNDRECT);
			an1.setAlpha(200);
			an1.setBgColor(Color.RED);
			an1.setAreaStyle(XEnum.DataAreaStyle.FILL);
			
			AnchorDataPoint an2 = new AnchorDataPoint(1,1,XEnum.AnchorStyle.CIRCLE);
			an2.setBgColor(Color.GRAY);
			
			AnchorDataPoint an3 = new AnchorDataPoint(0,2,XEnum.AnchorStyle.RECT);
			an3.setBgColor(Color.BLUE);
					
			mAnchorSet.add(an1);
			mAnchorSet.add(an2);
			mAnchorSet.add(an3);
			chart.setAnchorDataPoint(mAnchorSet);	
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			Log.e(TAG, e.toString());
		}
	}
	private void chartDataSet()
	{
		
				List<PointD> linePoint1 = new ArrayList<PointD>();
				
				linePoint1.add(new PointD(2.5d, 8d));
				
				linePoint1.add(new PointD(5d, 8d));
				
				linePoint1.add(new PointD(10d, 12d));
				
				SplineData dataSeries1 = new SplineData("收入曲线",linePoint1,
						Color.rgb(54, 141, 238) );							
				dataSeries1.getLinePaint().setStrokeWidth(2);

				chartData.add(dataSeries1);	
				
	}
	
	
	public void setChartData(List<PointD> linePoint)
	{
				chartData.clear();
				SplineData dataSeries = new SplineData("收入曲线",linePoint,
						Color.rgb(54, 141, 238) );	
				dataSeries.getLinePaint().setStrokeWidth(2);
				chartData.add(dataSeries);
				chartRender();
	}
	
	
	public void setChartLabels(List<String> l)
	{
		for(int i=0;i<l.size();i++){
			labels.add(l.get(i));
		}
		//initView();
	}
	public void setTitle(String title){
		chart.setTitle(title);
	}

	
	@Override
    public void render(Canvas canvas) {
        try{
            chart.render(canvas);
        } catch (Exception e){
        	Log.e(TAG, e.toString());
        }
    }

	@Override
	public List<XChart> bindChart() {
		// TODO Auto-generated method stub		
		List<XChart> lst = new ArrayList<XChart>();
		lst.add(chart);		
		return lst;
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		// TODO Auto-generated method stub		
		
		super.onTouchEvent(event);
				
		if(event.getAction() == MotionEvent.ACTION_UP) 
		{			
			triggerClick(event.getX(),event.getY());	
		}
		return true;
	}
	
	
	//触发监听
	private void triggerClick(float x,float y)
	{		
		//交叉�?		if(chart.getDyLineVisible())chart.getDyLine().setCurrentXY(x,y);		
		if(!chart.getListenItemClickStatus())
		{
			if(chart.getDyLineVisible()&&chart.getDyLine().isInvalidate())this.invalidate();
		}else{	
			PointPosition record = chart.getPositionRecord(x,y);			
			if( null == record) return;
	
			if(record.getDataID() >= chartData.size()) return;
			SplineData lData = chartData.get(record.getDataID());
			List<PointD> linePoint =  lData.getLineDataSet();	
			int pos = record.getDataChildID();
			int i = 0;
			Iterator it = linePoint.iterator();
			while(it.hasNext())
			{
				PointD  entry=(PointD)it.next();	
				
				if(pos == i)
				{							 						
				     Double xValue = entry.x;
				     Double yValue = entry.y;	
				  
				        float r = record.getRadius();
						chart.showFocusPointF(record.getPosition(),r * 2);		
						chart.getFocusPaint().setStyle(Style.STROKE);
						chart.getFocusPaint().setStrokeWidth(3);		
						if(record.getDataID() >= 2)
						{
							chart.getFocusPaint().setColor(Color.BLUE);
						}else{
							chart.getFocusPaint().setColor(Color.RED);
						}	
						
						//在点击处显示tooltip
						pToolTip.setColor(Color.RED);				
						chart.getToolTip().setCurrentXY(x,y);
						chart.getToolTip().addToolTip(" Key:"+lData.getLineKey(),pToolTip);
						chart.getToolTip().addToolTip(" Label:"+lData.getLabel(),pToolTip);		
						chart.getToolTip().addToolTip(" Current Value:" +Double.toString(xValue)+","+Double.toString(yValue),pToolTip);
						chart.getToolTip().getBackgroundPaint().setAlpha(100);
						this.invalidate();
						
				     break;
				}
		        i++;
			}//end while
		}
	}


	public double getyMax() {
		return yMax;
	}


	public void setyMax(double yMax) {
		this.yMax = yMax;
	}


	public double getyStep() {
		return yStep;
	}


	public void setyStep(double yStep) {
		this.yStep = yStep;
	}


	public double getxMax() {
		return xMax;
	}


	public void setxMax(double xMax) {
		this.xMax = xMax;
	}
	
	
}
