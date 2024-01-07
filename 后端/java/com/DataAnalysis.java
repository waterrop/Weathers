package com;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DataAnalysis{
    private String area;
    private String city;
    private List<CityWeathers> listCityWeather;
    private Set<String> listCity;
    private MySql ms;
    public DataAnalysis() {ms = new MySql("mydata","root","123456");}
    public DataAnalysis(String area, String city) {
        this.area = area;
        this.city = city;
        ms = new MySql("mydata","root","123456");
        listCityWeather = ms.selectData(area,city);

    }
    //计算预测准确率
    public String predict(){
        // 获取当前日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMdd");
        String currentDate = dateFormat.format(new Date());
        String time = currentDate.substring(0,2) + "/" + currentDate.substring(2);
        List<String> listName = ms.selectTables();
        int length = listName.size();
        int sumHigh = 0;
        int sumLow = 0;
        List<Integer> high = new ArrayList<>();
        List<Integer> low = new ArrayList<>();
        for(int i=length-1;i>=length-7;i--){
            String tableName = listName.get(i);
            MySql ms2 = new MySql("mydata","root","123456",tableName);
            List<CityWeathers> lcw = ms2.selectData(area,city);
            for(CityWeathers x : lcw){
                if(x.getDate().equals(time)){
                    int dayHigh = fetchInt(x.getDayHigh());
                    int dayLow = fetchInt(x.getDayLow());
                    high.add(dayHigh);
                    low.add(dayLow);
                }
            }
        }
        for(int j=1;j<7;j++){
            sumHigh += high.get(j);
            sumLow += low.get(j);
        }
        // 创建百分数格式化对象
        NumberFormat percentFormat = NumberFormat.getPercentInstance();
        // 设置百分数的小数位数
        percentFormat.setMinimumFractionDigits(2);
        String predictHigh = percentFormat.format(1.0 - Math.abs(high.get(0)-(sumHigh * 1.0 / 6)) / 7);
        String predictLow = percentFormat.format(1.0 - Math.abs(low.get(0)-(sumLow * 1.0 / 6)) / 7);
        return time + "日的最高温度预测准确率为：" + predictHigh + "，最低温度的预测准确率为：" + predictLow;
    }
    //分析一个城市未来七天的气温与温差
    public void temperatureAnalysis(){
        //绘制温度曲线(最高温)
        XYSeries temperatureSeriesHigh = new XYSeries("High Temperature");
        //绘制温度曲线(最低温)
        XYSeries temperatureSeriesLow = new XYSeries("Low Temperature");
        int count = 1;
        for(CityWeathers x : listCityWeather) {
            int dayHigh = fetchInt(x.getDayHigh());
            int dayLow = fetchInt(x.getDayLow());
            System.out.println(x.getDate() + "日的最高温为" + x.getDayHigh() + "最低温为" + x.getDayLow() + "温差为" + (dayHigh-dayLow) + "°C");
            temperatureSeriesHigh.add(count, dayHigh);
            temperatureSeriesLow.add(count,dayLow);
            count++;
        }
        XYSeriesCollection dataset = new XYSeriesCollection();
        dataset.addSeries(temperatureSeriesHigh);
        dataset.addSeries(temperatureSeriesLow);
        temperatureChart("Temperature",dataset);
    }
    //分析一个城市未来七天的阴晴情况
    public void weatherAnalysis(){
        Map<String,Integer> map = new HashMap<>();
        // 创建数据
        DefaultPieDataset dataset = new DefaultPieDataset();
        for(CityWeathers x : listCityWeather) {
            if(map.containsKey(x.getDayWeather())) map.put(x.getDayWeather(),map.get(x.getDayWeather()) + 1);
            else map.put(x.getDayWeather(),1);
        }
        for(Map.Entry<String,Integer> entry : map.entrySet()){
            dataset.setValue(entry.getKey(),entry.getValue());
        }
        PieChart3D("dayWeatehr",dataset);
    }
    //将字符串中的数字提取出来
    private int fetchInt(String message){
        String extractedNumberStr = "";
        //定义正则表达式
        String regex = "-?\\d+";
        // 编译正则表达式
        Pattern pattern = Pattern.compile(regex);
        // 创建Matcher对象
        Matcher matcher = pattern.matcher(message);
        // 查找匹配
        while (matcher.find()) {
            // 提取匹配的数字
            extractedNumberStr = matcher.group();
        }
//        Integer extractedNumber = Integer.valueOf(extractedNumberStr);
        return Integer.parseInt(extractedNumberStr);
    }
    //画折线图
    private void temperatureChart(String title,XYSeriesCollection dataset){
        JFreeChart chart = ChartFactory.createXYLineChart(
                title,
                "Time",
                "Temperature (°C)",
                dataset,
                PlotOrientation.VERTICAL,
                true, true, false
        );
        //利用awt进行显示
        ChartFrame chartFrame = new ChartFrame("Test", chart);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }
    //画饼状图
    private void PieChart3D(String title, DefaultPieDataset dataset){
        // 创建JFreeChart对象
        JFreeChart chart = ChartFactory.createPieChart3D(
                title, // 图标题
                dataset, // 数据集
                false, true, true
        );
        // 利用awt进行显示
        ChartFrame chartFrame = new ChartFrame("Test2", chart);
        chartFrame.pack();
        chartFrame.setVisible(true);
    }

    public List<CityWeathers> getListCityWeather() {
        return listCityWeather;
    }

    public Set<String> getListCity(String area) {
        listCity = ms.selectCity(area);
        return listCity;
    }

    //主函数
    public static void main(String[] args){
        DataAnalysis da = new DataAnalysis("东北","七台河");
        da.temperatureAnalysis();
        da.weatherAnalysis();
    }
}
