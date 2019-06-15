package com.huiwei.util;

import org.apache.commons.lang3.StringUtils;

public class LevelUtil {
    //部门层级分隔符
    public final static String SEPARATOR = ".";
    //
    public final static String ROOT = "0";

    //定义部门level的计算规则,Level是由部门id拼接成的
    //0
    //0.1
    //0.1.2
    //0.1.3
    //0.4
    public static String calculateLevel(String parentLevel,Integer parentId){
        if(StringUtils.isBlank(parentLevel)){
            return ROOT;
        }else{
            return StringUtils.join(parentLevel,SEPARATOR,parentId);
        }
    }
}
