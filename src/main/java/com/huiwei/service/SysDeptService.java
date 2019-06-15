package com.huiwei.service;

import com.google.common.base.Preconditions;
import com.huiwei.dao.SysDeptMapper;
import com.huiwei.exception.ParamException;
import com.huiwei.model.SysDept;
import com.huiwei.param.DeptParam;
import com.huiwei.util.BeanValidator;
import com.huiwei.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * 部门业务层
 */
@Service
public class SysDeptService {

    @Resource
    private SysDeptMapper sysDeptMapper;

    /**
     * 存储部门
     * @param param
     */
    public void save(DeptParam param){
        //1、浏览器传过来的参数都应该首先进行常规校验，再进行下一步的业务操作
        BeanValidator.check(param);
        //2、此处属于业务校验
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        //3、校验完成之后就进行数据库对象构建，数据库对象构建包括浏览器传过来的属性值和系统生成的属性值
        //建造者模式生成部门类
        //这里的业务对象dept因为需要进行业务操作，这就意味着该对象不再适合用spring来进行管理，而spring管理的应该是
        // controller和service等应用组件
        SysDept dept = SysDept.builder().name(param.getName()).id(param.getId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        //设置部门的level
        dept.setLevel((LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId())));
        //以下3个字段的值由系统进行生成
        dept.setOperator("system");//TODO:
        dept.setOperateIp("127.0.0.1");//TODO
        dept.setOperateTime(new Date());
        //4、对象组装完成以后调用Dao层进行数据存储
        sysDeptMapper.insertSelective(dept);
    }

    /**
     * 更新部门
     * @param param
     */
    public void update(DeptParam param){
        //1、万事第一步，基础校验
        BeanValidator.check(param);
        //2、业务校验
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        //根据id查出更新前部门
        SysDept before = sysDeptMapper.selectByPrimaryKey(param.getId());
        //调用谷歌的API进行对象是否为空校验，如果为空就会抛出空指针异常，就会进入全局异常处理器进行处理，后续就不会执行了
        Preconditions.checkNotNull(before,"待更新的部门不存在");
        //业务校验
        if(checkExist(param.getParentId(),param.getName(),param.getId())){
            throw new ParamException("同一层级下存在相同名称的部门");
        }
        //组装待更新的对象
        SysDept after = SysDept.builder().id(param.getId()).name(param.getName()).id(param.getId())
                .seq(param.getSeq()).remark(param.getRemark()).build();
        //设置部门的level
        after.setLevel(LevelUtil.calculateLevel(getLevel(param.getParentId()),param.getParentId()));
        after.setOperator("system-update");//TODO:
        after.setOperateIp("127.0.0.1");//TODO:
        after.setOperateTime(new Date());
        //更新部门的同时要更新该部门下的子部门，主要是更新子部门的level，此处可能引起变化
        updateWithChild(before,after);
    }
    //更新部门时，更新子部门，更新子部门要么同时成功，要么同时失败，所以这个操作应该用事务进行包装一下
    @Transactional
    public void updateWithChild(SysDept before,SysDept after){
        String newLevelPrefix = after.getLevel();
        String oldLevelPrefix = before.getLevel();
        if(!after.getLevel().equals(before.getLevel())){
            String curLevel = before.getLevel()+"."+before.getId();
            List<SysDept> childDeptList = sysDeptMapper.getChildDeptListByLevel(before.getLevel());
            if(CollectionUtils.isNotEmpty(childDeptList)){
                for (SysDept dept:childDeptList) {
                    String level = dept.getLevel();
                    if(level.indexOf(oldLevelPrefix)==0){
                        level = newLevelPrefix + level.substring(oldLevelPrefix.length());
                        dept.setLevel(level);
                    }
                }
                sysDeptMapper.batchUpdateLevel(childDeptList);
            }
        }
        sysDeptMapper.updateByPrimaryKey(after);

    }
    //判断是否已经存在该部门
    private boolean checkExist(Integer parentId,String name,Integer deptId){
        return sysDeptMapper.countByNameAndParentId(parentId,name,deptId) > 0;
    }
    //生成部门level
    private String getLevel(Integer deptId){
        SysDept dept = sysDeptMapper.selectByPrimaryKey(deptId);
        if(dept == null){
            return null;
        }
        return dept.getLevel();
    }
}
