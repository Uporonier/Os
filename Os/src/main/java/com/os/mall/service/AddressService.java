package com.os.mall.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.os.mall.entity.Address;
import com.os.mall.mapper.AddressMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Service
public class AddressService extends ServiceImpl<AddressMapper, Address> {

    @Resource
    private AddressMapper addressMapper;

    public List<Address> findAllById(Long id) {
        QueryWrapper<Address> listQueryWrapper = new QueryWrapper<>();  //创建了一个QueryWrapper对象，用于构建查询条件
        listQueryWrapper.eq("user_id",id);//添加查询条件，查询用户ID等于传入参数id的地址信息。
        return list(listQueryWrapper);
    }
}
