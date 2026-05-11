package com.studyhelper.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.studyhelper.entity.Question;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface QuestionMapper extends BaseMapper<Question> {
}
