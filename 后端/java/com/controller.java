package com;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@CrossOrigin(origins = "*")
public class controller {

    @RequestMapping("/selectWeathers")
    public Map<String,Object> selectWeathers(String area, String city){
        Map<String,Object> result = new HashMap<>();
        DataAnalysis da = new DataAnalysis(area,city);
        result.put("weather",da.getListCityWeather());
        return result;
    }

    @RequestMapping("/selectCity")
    public Set<String> selectCity(String area){
        DataAnalysis da = new DataAnalysis();
        return da.getListCity(area);
    }

    @RequestMapping("/predict")
    public Map<String,Object> predict(String area, String city){
        Map<String,Object> result = new HashMap<>();
        List<String> list = new ArrayList<>();
        DataAnalysis da = new DataAnalysis(area,city);
        list.add(da.predict());
        result.put("p",list);
        return result;
    }

}
