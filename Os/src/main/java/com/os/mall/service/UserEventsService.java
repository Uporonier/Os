package com.os.mall.service;

import com.os.mall.entity.UserEvents;
import com.os.mall.mapper.UserEventsMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserEventsService {

    @Autowired
    UserEventsMapper userEventsMapper;
    public void add(UserEvents userEvents)
    {
        if (userEventsMapper.countUserEvent(userEvents.getUserid(),userEvents.getGoodsid()) == 0) {
            userEventsMapper.insertUserEvent(userEvents);
        }
    }

    public List<Long> getAllProductIdsByUserId(Long userId) {
        return userEventsMapper.findAllProductIdsByUserId(userId);
    }

}
