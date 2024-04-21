package com.os.mall.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.os.mall.entity.MyFile;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface FileMapper extends BaseMapper<MyFile> {
}
