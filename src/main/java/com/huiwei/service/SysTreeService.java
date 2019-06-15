package com.huiwei.service;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.huiwei.dao.SysDeptMapper;
import com.huiwei.dto.DeptLevelDto;
import com.huiwei.model.SysDept;
import com.huiwei.util.LevelUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MultiMap;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * 思路：
 * 1、查出所有部门转化成部门部门传输对象dto
 * 2、
 */
@Service
public class SysTreeService {
    @Resource
    private SysDeptMapper sysDeptMapper;
    public List<DeptLevelDto> deptTree(){
        List<SysDept> deptList = sysDeptMapper.getAllDept();
        List<DeptLevelDto> dtoList = Lists.newArrayList();
        for(SysDept dept:deptList){
            DeptLevelDto dto = DeptLevelDto.adapt(dept);
            dtoList.add(dto);
        }
        return deptListToTree(dtoList);
    }
    public List<DeptLevelDto> deptListToTree(List<DeptLevelDto> deptLevelDtoList){
        //先判断部门列表是否为空
        if(CollectionUtils.isEmpty(deptLevelDtoList)){
            return null;
        }
        //level -> [dept1,dept2,...]
        //创建以部门level为key，部门集合为value的map，用于后面递归操作
        //创建根部门集合，作为递归的初始条件
        Multimap<String, DeptLevelDto> levelDeptMap = ArrayListMultimap.create();
        List<DeptLevelDto> rootList = Lists.newArrayList();
        //遍历部门集合，组装根部门集合和各level集合
        for (DeptLevelDto dto:deptLevelDtoList) {
            levelDeptMap.put(dto.getLevel(),dto);
            if(LevelUtil.ROOT.equals(dto.getLevel())){
                rootList.add(dto);
            }
        }
        //按照seq从小到大进行排序
        Collections.sort(rootList, new Comparator<DeptLevelDto>() {
            @Override
            public int compare(DeptLevelDto o1, DeptLevelDto o2) {
                return o1.getSeq() - o2.getSeq();
            }
        });
        //递归生成树
        transformDeptTree(rootList,LevelUtil.ROOT,levelDeptMap);
        return rootList;
    }
    //以根部门集合，部门level，部门map为递归条件，循环递归部门树集合，生成部门树
    public void transformDeptTree(List<DeptLevelDto> deptLevelList,String level,Multimap<String, DeptLevelDto> levelDeptMap){
        for (int i = 0; i < deptLevelList.size(); i++) {
            //遍历该层的每一个元素
            DeptLevelDto deptLevelDto = deptLevelList.get(i);
            //处理当前层级的数据
            String nextLevel = LevelUtil.calculateLevel(level,deptLevelDto.getId());
            //处理下一层
            List<DeptLevelDto> tempDeptList = (List<DeptLevelDto>) levelDeptMap.get(nextLevel);
            if(CollectionUtils.isNotEmpty(tempDeptList)){
                //排序
                Collections.sort(tempDeptList,deptSeqComparator);
                //设置下一层部门
                deptLevelDto.setDeptList(tempDeptList);
                //进入到下一层处理
                transformDeptTree(tempDeptList,nextLevel,levelDeptMap);
            }
        }
    }
    //因为这个比较器要用到多次，所以单独提出来，只需要实例化一次
    public Comparator<DeptLevelDto> deptSeqComparator = new Comparator<DeptLevelDto>() {
        @Override
        public int compare(DeptLevelDto o1, DeptLevelDto o2) {
            return o1.getSeq() -  o2.getSeq();
        }
    };
}
