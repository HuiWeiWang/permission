package com.huiwei.controller;


import com.huiwei.common.ApplicationContextHelper;
import com.huiwei.common.JsonData;
import com.huiwei.dao.SysAclModuleMapper;
import com.huiwei.exception.PermissionException;
import com.huiwei.model.SysAclModule;
import com.huiwei.param.TestVo;
import com.huiwei.util.BeanValidator;
import com.huiwei.util.JsonMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
@RequestMapping("/test")
@Slf4j
public class TestController {
    @RequestMapping("/hello.json")
    @ResponseBody
    public JsonData hello(){
        log.info("rinima");
        throw new RuntimeException("权限异常");
       // return JsonData.success("Hello,permission!");
    }

    @RequestMapping("/validate.json")
    @ResponseBody
    public JsonData validate(TestVo vo){
        log.info("validate");
       /* try{
            Map<String,String> map = BeanValidator.validateObject(vo);
            if(map != null && map.entrySet().size() > 0){
                for(Map.Entry<String,String> entry:map.entrySet()){
                    log.info("{}->{}",entry.getKey(),entry.getValue());
                }
            }
        }catch(Exception e){

        }*/
        SysAclModuleMapper moduleMapper = ApplicationContextHelper.popBean(SysAclModuleMapper.class);
        SysAclModule module = moduleMapper.selectByPrimaryKey(1);
        log.info(JsonMapper.object2String(module));
        BeanValidator.check(vo);
        return JsonData.success("test validate!");
    }
}
