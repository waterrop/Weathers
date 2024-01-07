package com;

import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;


public class MyJsoup {
    private Document doc;
    private Map<String, String> areaMap;
    private Map<String, Map<String, String>> cityLinkMap;
    private Map<String,List<CityWeathers>> cityWeathers;
    public MyJsoup(){
        areaMap=new HashMap<>();
    }
    public MyJsoup(String url){
        try{
            Document document = Jsoup.connect(url)
                    .data("query","java")
                    .userAgent("Firefox")
                    .cookie("auth","token")
                    .timeout(8000)
                    .post();
            this.doc=Jsoup.parse(document.html());
            areaMap=new HashMap<>();
            cityLinkMap=new HashMap<>();
            cityWeathers=new HashMap<>();
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    public Document getDoc(){return doc;}
    public Map<String, String> getAreaMap(){return areaMap;}
    public Map<String, Map<String, String>> getCityLinkMap(){return cityLinkMap;}
    public Map<String, List<CityWeathers>> getCityWeathers() {return cityWeathers;}
    //获取中国各个地区的名字及编号,并获取每个地区里城市的天气预报链接
    public void getAreaMessage(){
        //选择所有地区的a标签
        Elements area=doc.selectXpath("//ul[@class='nav nav-tabs tab2']/li[@class='']/a[position()<9]");
        int count = 0;
        for(Element x : area){
            if(count==8)break;
            String areaNum = x.attr("href").replace("#","");
            String areaName = x.text();
            areaMap.put(areaName,areaNum);
            count++;
        }
        //建立一个城市与其链接的map
        Map<String, String> cityLink;
        //遍历每个areaMap的键值对，获取每个地区的编号，找到编号对应的div标签
        for(Map.Entry<String, String> entry:areaMap.entrySet()){
            //获取地区编号
            String value = entry.getValue();
            Elements cities = doc.selectXpath("//div[@id='" + value + "']/div/div/a");
            //初始化一个城市与其链接的map
            cityLink = new HashMap<>();
            for(Element x:cities){
                String url = "https://weather.cma.cn/";
                //获取城市名字
                String cityName = x.text();
                //获取城市天气预报链接
                String cityUrl = url + x.attr("href");
                cityLink.put(cityName,cityUrl);
            }
            cityLinkMap.put(entry.getKey(),cityLink);
        }
    }
    //获取每个城市的信息
    public void getCityMessage(){
        //遍历每个地区
        for(Map.Entry<String, Map<String, String>> areaEntry:cityLinkMap.entrySet()){
            //获取每个地区对应的每个城市的链接
            for(Map.Entry<String, String> cityEntry:areaEntry.getValue().entrySet()){
                //每个城市的url
                String cityUrl = cityEntry.getValue();
                //爬取每个城市的html
                Document cityDoc;
                try {
                    cityDoc = Jsoup.connect(cityUrl)
                            .data("query", "java")
                            .userAgent("Firefox")
                            .cookie("auth", "token")
                            .timeout(8500)
                            .get();
                    cityDoc = Jsoup.parse(cityDoc.html());
                    //找到7天天气预报的标签
                    Elements sevenWeather = cityDoc.selectXpath("//div[@class='hp']/div[@class='row hb days ']/div");
                    List<CityWeathers> lcw = new ArrayList<>();
                    for (Element x : sevenWeather) {
                        String[] str = x.selectXpath("div[@class='day-item']").text().split(" ");
                        String high = x.selectXpath("div[@class='day-item bardiv']/div/div[@class='high']").text();
                        String low = x.selectXpath("div[@class='day-item bardiv']/div/div[@class='low']").text();
                        CityWeathers cw = new CityWeathers(str[0], str[1], str[2], str[3], str[4], high, low, str[5], str[6], str[7]);
                        lcw.add(cw);
                    }
                    cityWeathers.put(cityEntry.getKey(), lcw);
                }catch(IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
    //打印爬取到的天气信息
    public void PrintMessage(){
        int count = 0;
        for(Map.Entry<String,List<CityWeathers>> entry : cityWeathers.entrySet()){
            System.out.println("城市：" + entry.getKey());
            int i = 0;
            for(i=0;i<7;i++){
                System.out.println("第"+(i+1)+"天的天气信息"+entry.getValue().get(i).toString());
            }
            count++;
        }
        System.out.println(count);
    }
    //将数据写入csv文件备份
    public void WriteData(){
        // 获取当前日期
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String currentDate = dateFormat.format(new Date());
        try{
            BufferedWriter writer = new BufferedWriter(new FileWriter(currentDate + ".csv", StandardCharsets.UTF_8));
            writer.write("area,city,week,date,dayWeather,dayWindDirection,dayWindPower,dayHigh,dayLow,nightWeather,nightWindDirection,nightWindPower\n");
            //遍历每个地区
            for(Map.Entry<String, Map<String, String>> areaEntry:cityLinkMap.entrySet()){
                //获取每个地区对应的每个城市的链接
                for(Map.Entry<String, String> cityEntry:areaEntry.getValue().entrySet()){
                    String city = cityEntry.getKey();
                    int i = 0;
                    for(i=0;i<7;i++){
                        writer.write("'"+areaEntry.getKey()+"','"+city+"',"+cityWeathers.get(city).get(i).toMessage()+"\n");
                    }
                }
            }
        }catch(IOException e){
            e.printStackTrace();
        }
    }
    //将数据存入数据库
    public void insertDataToDatabase(){
        MySql ms = new MySql("mydata","root","123456");
        // 遍历每个地区
        for (Map.Entry<String, Map<String, String>> areaEntry : cityLinkMap.entrySet()) {
            String area = areaEntry.getKey();
            // 获取每个地区对应的每个城市的链接
            for (Map.Entry<String, String> cityEntry : areaEntry.getValue().entrySet()) {
                String city = cityEntry.getKey();
                int i = 0;
                for (i = 0; i < 7; i++) {
                    String week = cityWeathers.get(city).get(i).getWeek();
                    String date = cityWeathers.get(city).get(i).getDate();
                    String dayWeather = cityWeathers.get(city).get(i).getDayWeather();
                    String dayWindDirection = cityWeathers.get(city).get(i).getDayWindDirection();
                    String dayWindPower = cityWeathers.get(city).get(i).getDayWindPower();
                    String dayHigh = cityWeathers.get(city).get(i).getDayHigh();
                    String dayLow = cityWeathers.get(city).get(i).getDayLow();
                    String nightWeather = cityWeathers.get(city).get(i).getNightWeather();
                    String nightWindDirection = cityWeathers.get(city).get(i).getNightWindDirection();
                    String nightWindPower = cityWeathers.get(city).get(i).getNightWindPower();
                    ms.insertData(area,city,week,date,dayWeather,dayWindDirection,dayWindPower,dayHigh,dayLow,nightWeather,nightWindDirection,nightWindPower);
                }
            }
        }
        ms.closeMysql();
    }
    public static void main(String[] args){
        MyJsoup mj=new MyJsoup("https://weather.cma.cn/web/weather/map.html#");
        mj.getAreaMessage();
        mj.getCityMessage();
        mj.PrintMessage();
        mj.WriteData();
        mj.insertDataToDatabase();
    }
}
