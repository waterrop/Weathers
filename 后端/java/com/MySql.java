package com;

import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.Date;

public class MySql{
    //数据库连接信息
    private String JDBC_URL;
    private String JDBC_USER;
    private String JDBC_PASSWORD;
    private Connection connection;
    private String tableName;

    public MySql() {}
    public MySql(String database, String JDBC_USER, String JDBC_PASSWORD) {
        this.JDBC_URL = "jdbc:mysql://localhost:3306/"+database;
        this.JDBC_USER = JDBC_USER;
        this.JDBC_PASSWORD = JDBC_PASSWORD;
        //连接数据库
        try{
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            // 获取当前日期
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
            String currentDate = dateFormat.format(new Date());
            // 创建新表名
            tableName = currentDate + "weathers";
            ifExists(connection);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public MySql(String database, String JDBC_USER, String JDBC_PASSWORD,String name){
        this.JDBC_URL = "jdbc:mysql://localhost:3306/"+database;
        this.JDBC_USER = JDBC_USER;
        this.JDBC_PASSWORD = JDBC_PASSWORD;
        //连接数据库
        try{
            connection = DriverManager.getConnection(JDBC_URL, JDBC_USER, JDBC_PASSWORD);
            tableName = name;
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //查看表是否存在
    public void ifExists(Connection connection){
        try {
            // 检查表是否存在
            String checkTableExistence = "SELECT 1 FROM " + tableName + " LIMIT 1;";
            try (PreparedStatement preparedStatement = connection.prepareStatement(checkTableExistence)) {
                preparedStatement.executeQuery();
            }
        } catch (SQLException e) {
            // 表不存在，创建新表
            createTable(connection);
        }
    }
    //创建新表
    public void createTable(Connection connection){
        try{
            String createTableSQL = "CREATE TABLE " + tableName + "(" +
                    "area VARCHAR(30)," +
                    "city VARCHAR(30)," +
                    "week VARCHAR(30)," +
                    "date VARCHAR(30)," +
                    "dayWeather VARCHAR(30)," +
                    "dayWindDirection VARCHAR(30)," +
                    "dayWindPower VARCHAR(30)," +
                    "dayHigh VARCHAR(30)," +
                    "dayLow VARCHAR(30)," +
                    "nightWeather VARCHAR(30)," +
                    "nightWindDirection VARCHAR(30)," +
                    "nightWindPower VARCHAR(30)," +
                    "PRIMARY KEY (area, city, week, date)" +
                    ")";
            PreparedStatement preparedStatement = connection.prepareStatement(createTableSQL);
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //将数据存入数据库
    public void insertData(String area,String city,String week,String date,String dayWeather,String dayWindDirection,String dayWindPower,String dayHigh,String dayLow,String nightWeather,String nightWindDirection,String nightWindPower){
        String sql = "INSERT INTO " + tableName +
                "(area, city, week, date, dayWeather, dayWindDirection, dayWindPower, dayHigh, dayLow, nightWeather, nightWindDirection, nightWindPower) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            // 设置参数
            preparedStatement.setString(1, area);
            preparedStatement.setString(2, city);
            preparedStatement.setString(3, week);
            preparedStatement.setString(4, date);
            preparedStatement.setString(5, dayWeather);
            preparedStatement.setString(6, dayWindDirection);
            preparedStatement.setString(7, dayWindPower);
            preparedStatement.setString(8, dayHigh);
            preparedStatement.setString(9, dayLow);
            preparedStatement.setString(10, nightWeather);
            preparedStatement.setString(11, nightWindDirection);
            preparedStatement.setString(12, nightWindPower);

            // 执行插入操作
            preparedStatement.executeUpdate();
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    //获取所有的表名
    public List<String> selectTables(){
        List<String> list = new ArrayList<>();
        String sql = "show tables;";
        try{
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                String name = resultSet.getString("Tables_in_mydata");
                list.add(name);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
    //从数据库提取城市的天气数据
    public List<CityWeathers> selectData(String area, String city){
        List<CityWeathers> lcw = new ArrayList<>();
        try{
            String sql = "SELECT * FROM " + tableName + " where area = '" + area + "' and city = '" + city + "'" + " order by date";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                String week = resultSet.getString("week");
                String date = resultSet.getString("date");
                String dayWeather = resultSet.getString("dayWeather");
                String dayWindDirection = resultSet.getString("dayWindDirection");
                String dayWindPower = resultSet.getString("dayWindPower");
                String dayHigh = resultSet.getString("dayHigh");
                String dayLow = resultSet.getString("dayLow");
                String nightWeather = resultSet.getString("nightWeather");
                String nightWindDirection = resultSet.getString("nightWindDirection");
                String nightWindPower = resultSet.getString("nightWindPower");
                CityWeathers cw = new CityWeathers(week,date,dayWeather,dayWindDirection,dayWindPower,dayHigh,dayLow,nightWeather,nightWindDirection,nightWindPower);
                lcw.add(cw);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lcw;
    }
    //从数据库提取每个地区的城市名称
    public Set<String> selectCity(String area){
        Set<String> lc = new HashSet<>();
        try{
            String sql = "select * from " + tableName + " where area = '" + area + "' ";
            PreparedStatement preparedStatement = connection.prepareStatement(sql);
            ResultSet resultSet = preparedStatement.executeQuery();
            while(resultSet.next()){
                String city = resultSet.getString("city");
                lc.add(city);
            }
            resultSet.close();
            preparedStatement.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lc;
    }
    //关闭数据库
    public void closeMysql(){
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    //主函数
    public static void main(String[] args){
        MySql ms = new MySql("mydata","me","123456");
        List<CityWeathers> lcw = ms.selectData("东北","七台河");
    }
}
