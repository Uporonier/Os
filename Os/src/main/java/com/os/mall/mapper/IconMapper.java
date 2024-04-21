package com.os.mall.mapper;

import com.os.mall.entity.Icon;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface IconMapper extends BaseMapper<Icon> {

    List<Icon> getIconCategoryMapList();
}
