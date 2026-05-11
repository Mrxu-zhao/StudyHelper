package com.studyhelper.common;

public class Constants {
    
    // ========== 错误码定义 ==========
    
    // 系统级错误 (1000-1999)
    public static final int CODE_SYSTEM_ERROR = 1000;
    public static final int CODE_SERVICE_MAINTENANCE = 1001;
    public static final int CODE_REQUEST_TIMEOUT = 1002;
    
    // 认证授权错误 (2000-2999)
    public static final int CODE_NOT_LOGIN = 2000;
    public static final int CODE_TOKEN_EXPIRED = 2001;
    public static final int CODE_INVALID_TOKEN = 2002;
    public static final int CODE_NO_PERMISSION = 2003;
    public static final int CODE_ACCOUNT_DISABLED = 2004;
    
    // 业务参数错误 (3000-3999)
    public static final int CODE_PARAM_NULL = 3000;
    public static final int CODE_PARAM_FORMAT_ERROR = 3001;
    public static final int CODE_PARAM_OUT_OF_RANGE = 3002;
    
    // 业务逻辑错误 (4000-4999)
    public static final int CODE_OPERATION_FAILED = 4000;
    public static final int CODE_RESOURCE_NOT_EXIST = 4001;
    public static final int CODE_RESOURCE_EXIST = 4002;
    public static final int CODE_STATUS_NOT_ALLOWED = 4003;
    public static final int CODE_BUSINESS_RULE_FAILED = 4004;
    
    // 资源相关错误 (5000-5999)
    public static final int CODE_RESOURCE_ACCESS_FAILED = 5000;
    public static final int CODE_UPLOAD_FAILED = 5001;
    public static final int CODE_DOWNLOAD_FAILED = 5002;
    
    // ========== 用户角色 ==========
    public static final String ROLE_STUDENT = "student";
    public static final String ROLE_PARENT = "parent";
    public static final String ROLE_ADMIN = "admin";
    
    // ========== 年级定义 ==========
    public static final int GRADE_1 = 1;  // 高一
    public static final int GRADE_2 = 2;  // 高二
    public static final int GRADE_3 = 3;  // 高三
    
    // ========== 题目类型 ==========
    public static final int QUESTION_SINGLE_CHOICE = 1;   // 单选题
    public static final int QUESTION_MULTIPLE_CHOICE = 2; // 多选题
    public static final int QUESTION_FILL_BLANK = 3;      // 填空题
    public static final int QUESTION_SHORT_ANSWER = 4;    // 简答题
    public static final int QUESTION_CALCULATION = 5;     // 计算题
    public static final int QUESTION_COMPOSITION = 6;     // 作文
    
    // ========== 题目难度 ==========
    public static final int DIFFICULTY_EASY = 1;
    public static final int DIFFICULTY_MEDIUM = 2;
    public static final int DIFFICULTY_HARD = 3;
    
    // ========== 学习计划状态 ==========
    public static final int PLAN_STATUS_DRAFT = 0;      // 草稿
    public static final int PLAN_STATUS_ACTIVE = 1;      // 进行中
    public static final int PLAN_STATUS_COMPLETED = 2;  // 已完成
    public static final int PLAN_STATUS_ABANDONED = 3;  // 已放弃
    
    // ========== 任务状态 ==========
    public static final int TASK_STATUS_PENDING = 0;    // 待完成
    public static final int TASK_STATUS_IN_PROGRESS = 1; // 进行中
    public static final int TASK_STATUS_COMPLETED = 2;  // 已完成
    
    // ========== 学习记录状态 ==========
    public static final int STUDY_STATUS_INCOMPLETE = 0;  // 未完成
    public static final int STUDY_STATUS_LEARNING = 1;     // 学习中
    public static final int STUDY_STATUS_MASTERED = 2;     // 已掌握
    
    // ========== 收藏类型 ==========
    public static final int FAVORITE_KNOWLEDGE = 1;  // 知识点
    public static final int FAVORITE_QUESTION = 2;   // 题目
    public static final int FAVORITE_PAPER = 3;      // 试卷
    
    // ========== 考频 ==========
    public static final String FREQUENCY_HIGH = "high";
    public static final String FREQUENCY_MEDIUM = "medium";
    public static final String FREQUENCY_LOW = "low";
    
    private Constants() {
    }
}
