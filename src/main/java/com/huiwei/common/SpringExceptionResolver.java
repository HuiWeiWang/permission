package com.huiwei.common;

import com.huiwei.exception.ParamException;
import com.huiwei.exception.PermissionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 全局异常处理器
 * 这个异常处理器实现了spring的异常处理接口，因此会被spring自动管理，当应用中出现异常时会自动应用该类进行异常处理
 */
@Slf4j
public class SpringExceptionResolver implements HandlerExceptionResolver {
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object o, Exception ex) {
        String url = request.getRequestURI().toString();
        ModelAndView mv;
        String defaultMsg = "System error";
        //.json,.page
        //这里要求项目中所有请求json数据都以.json结尾
        if(url.endsWith(".json")){
            JsonData result;
            if(ex instanceof PermissionException || ex instanceof ParamException){
                result  = JsonData.fail(ex.getMessage());
            }else{
                log.error("Unknow json exception,url:"+url,ex);
                result = JsonData.fail(defaultMsg);
            }
            mv = new ModelAndView("jsonView",result.toMap());
        }else if(url.endsWith(".page")){    //这里我们要求项目中所有请求page页面，都使用.page结尾
            log.error("Unknow page exception,url:"+url,ex);
            JsonData result = JsonData.fail(defaultMsg);
            mv = new ModelAndView("exception",result.toMap());
        }else{
            log.error("Unknow exception,url:"+url,ex);
            JsonData result = JsonData.fail(defaultMsg);
            mv = new ModelAndView("jsonView",result.toMap());
        }
        return mv;
    }
}
