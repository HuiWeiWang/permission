package com.huiwei.util;

import com.google.common.base.Preconditions;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.huiwei.exception.ParamException;
import org.apache.commons.collections.MapUtils;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * 此类用于校验bean
 */
public class BeanValidator {
    //全局校验工厂
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    //此处使用泛型方法  单个对象校验
    public static <T> Map<String,String> validate(T t,Class... groups){
        Validator validator = validatorFactory.getValidator();
        Set<ConstraintViolation<T>> validateResult = validator.validate(t, groups);
        if(validateResult.isEmpty()){
            return Collections.emptyMap();
        }else {
            LinkedHashMap errors = Maps.newLinkedHashMap();
            Iterator<ConstraintViolation<T>> iterator = validateResult.iterator();
            while (iterator.hasNext()){
                ConstraintViolation<T> voilation = iterator.next();
                errors.put(voilation.getPropertyPath().toString(),voilation.getMessage());
            }
            return errors;
        }
    }
    //集合校验
    public static Map<String,String> validateList(Collection<?> collection){
        Preconditions.checkNotNull(collection);
        Iterator<?> iterator = collection.iterator();
        Map errors;

        do{
            if(!iterator.hasNext()){
                return Collections.emptyMap();
            }
            Object object = iterator.next();
            errors = validate(object, new Class[0]);
        }while(errors.isEmpty());
        return errors;
    }
    //此方法包装上面的validate和validateList方法，用于处理校验所有类型
    public static Map<String,String> validateObject(Object first,Object... objects){
        if(objects != null && objects.length > 0){
            return validateList(Lists.asList(first,objects));
        }
        return validate(first,new Class[0]);
    }

    public static void check(Object param) throws ParamException{
        Map<String,String> map = BeanValidator.validateObject(param);
        if(MapUtils.isNotEmpty(map)){   //引入google的map工具
            throw new ParamException(map.toString());
        }
    }
}
