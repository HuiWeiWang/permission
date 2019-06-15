package com.huiwei.common;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

/**
 * 后台json数据返回前台接口
 */
@Getter
@Setter
public class JsonData {
    //返回结果,为true代表正常处理，为false表示后端因为什么原因处理失败
    private boolean ret;
    //异常信息
    private String msg;
    //返回数据
    private Object data;

    public JsonData(boolean ret){
        this.ret = ret;
    }

    public static JsonData success(Object object,String msg){
        JsonData jsonData = new JsonData(true);
        jsonData.data = object;
        jsonData.msg = msg;
        return jsonData;
    }
    public static JsonData success(Object object){
        JsonData jsonData = new JsonData(true);
        jsonData.data = object;
        return jsonData;
    }
    public static JsonData success(){
       return new JsonData(true);
    }
    public static JsonData fail(String msg){
        JsonData jsonData = new JsonData(false);
        jsonData.msg = msg;
        return jsonData;
    }
    public Map<String,Object> toMap(){
        Map<String,Object> result = new HashMap<>();
        result.put("ret",ret);
        result.put("msg",msg);
        result.put("data",data);
        return result;
    }
}
