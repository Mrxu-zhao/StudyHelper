package com.studyhelper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyhelper.entity.User;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface UserMapper extends BaseMapper<User> {
}
