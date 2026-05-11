-- ================================================================
-- StudyHelper 高中生助学平台 - 数据库表结构
-- 数据库: MySQL 8.0
-- 编码: utf8mb4
-- 创建时间: 2026-05-11
-- ================================================================

-- 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS studyhelper
    DEFAULT CHARACTER SET utf8mb4
    DEFAULT COLLATE utf8mb4_unicode_ci;

USE studyhelper;

-- ================================================================
-- 1. 用户表（学生、家长、管理员）
-- ================================================================
CREATE TABLE `users` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（BCrypt加密）',
    `role` ENUM('student', 'parent', 'admin') NOT NULL DEFAULT 'student' COMMENT '角色: student=学生, parent=家长, admin=管理员',
    `real_name` VARCHAR(50) DEFAULT NULL COMMENT '真实姓名',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(100) DEFAULT NULL COMMENT '邮箱',
    `grade` TINYINT UNSIGNED DEFAULT NULL COMMENT '年级: 1=高一, 2=高二, 3=高三',
    `avatar_url` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
    `bind_code` VARCHAR(6) DEFAULT NULL COMMENT '家长绑定码（6位数字）',
    `parent_user_id` INT UNSIGNED DEFAULT NULL COMMENT '关联家长ID（学生使用）',
    `study_target` VARCHAR(50) DEFAULT NULL COMMENT '升学目标（学生使用）',
    `weak_subjects` VARCHAR(500) DEFAULT NULL COMMENT '薄弱学科（JSON数组，学生使用）',
    `admin_level` TINYINT UNSIGNED DEFAULT NULL COMMENT '管理员级别（管理员使用）: 1=超级管理员, 2=内容管理员',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '状态: 0=禁用, 1=正常',
    `last_login_at` DATETIME DEFAULT NULL COMMENT '最后登录时间',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_role` (`role`),
    KEY `idx_grade` (`grade`),
    KEY `idx_bind_code` (`bind_code`),
    KEY `idx_parent_user_id` (`parent_user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户表（学生、家长、管理员）';

-- ================================================================
-- 2. 学科表
-- ================================================================
CREATE TABLE `subjects` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '学科ID',
    `name` VARCHAR(50) NOT NULL COMMENT '学科名称',
    `code` VARCHAR(20) NOT NULL COMMENT '学科代码',
    `grade_level` TINYINT UNSIGNED DEFAULT 4 COMMENT '适用年级: 1=高一, 2=高二, 3=高三, 4=通用',
    `icon_url` VARCHAR(255) DEFAULT NULL COMMENT '学科图标URL',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '学科描述',
    `is_active` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否启用: 0=禁用, 1=启用',
    `sort_order` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序顺序',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_code` (`code`),
    KEY `idx_grade_level` (`grade_level`),
    KEY `idx_is_active` (`is_active`),
    KEY `idx_sort_order` (`sort_order`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学科表';

-- ================================================================
-- 3. 年级表
-- ================================================================
CREATE TABLE `grades` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '年级ID',
    `name` VARCHAR(20) NOT NULL COMMENT '年级名称',
    `level` TINYINT UNSIGNED NOT NULL COMMENT '年级级别: 1=高一, 2=高二, 3=高三',
    `description` VARCHAR(200) DEFAULT NULL COMMENT '年级描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_level` (`level`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='年级表';

-- ================================================================
-- 4. 章节表
-- ================================================================
CREATE TABLE `chapters` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '章节ID',
    `subject_id` INT UNSIGNED NOT NULL COMMENT '学科ID',
    `parent_id` INT UNSIGNED DEFAULT NULL COMMENT '父章节ID（顶级章节为NULL）',
    `name` VARCHAR(100) NOT NULL COMMENT '章节名称',
    `code` VARCHAR(50) DEFAULT NULL COMMENT '章节代码',
    `order_num` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '排序号',
    `level` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '层级: 1=大章, 2=小节',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '章节描述',
    `knowledge_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '知识点数量',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_subject_id` (`subject_id`),
    KEY `idx_parent_id` (`parent_id`),
    KEY `idx_order_num` (`order_num`),
    CONSTRAINT `fk_chapters_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_chapters_parent` FOREIGN KEY (`parent_id`) REFERENCES `chapters` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='章节表';

-- ================================================================
-- 5. 知识点表
-- ================================================================
CREATE TABLE `knowledge_points` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '知识点ID',
    `chapter_id` INT UNSIGNED NOT NULL COMMENT '章节ID',
    `title` VARCHAR(200) NOT NULL COMMENT '知识点标题',
    `content` TEXT DEFAULT NULL COMMENT '知识点内容（支持富文本）',
    `importance` TINYINT UNSIGNED NOT NULL DEFAULT 3 COMMENT '重要程度: 1-5',
    `difficulty` TINYINT UNSIGNED NOT NULL DEFAULT 3 COMMENT '难度等级: 1-5',
    `frequency` ENUM('high', 'medium', 'low') NOT NULL DEFAULT 'medium' COMMENT '考频: high=高频, medium=中频, low=低频',
    `tags` VARCHAR(500) DEFAULT NULL COMMENT '标签（JSON数组格式）',
    `related_ids` VARCHAR(200) DEFAULT NULL COMMENT '关联知识点ID列表',
    `view_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '浏览次数',
    `favorite_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '收藏次数',
    `is_active` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否启用: 0=禁用, 1=启用',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_chapter_id` (`chapter_id`),
    KEY `idx_importance` (`importance`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_frequency` (`frequency`),
    KEY `idx_is_active` (`is_active`),
    CONSTRAINT `fk_knowledge_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapters` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='知识点表';

-- ================================================================
-- 6. 题目表
-- ================================================================
CREATE TABLE `questions` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '题目ID',
    `subject_id` INT UNSIGNED NOT NULL COMMENT '学科ID',
    `chapter_id` INT UNSIGNED DEFAULT NULL COMMENT '所属章节ID',
    `type` TINYINT UNSIGNED NOT NULL COMMENT '题型: 1=单选题, 2=多选题, 3=填空题, 4=简答题, 5=计算题, 6=作文',
    `difficulty` TINYINT UNSIGNED NOT NULL DEFAULT 2 COMMENT '难度: 1=简单, 2=中等, 3=困难',
    `score` INT UNSIGNED NOT NULL DEFAULT 5 COMMENT '分值',
    `content` TEXT NOT NULL COMMENT '题干内容',
    `options` TEXT DEFAULT NULL COMMENT '选项（JSON数组格式，选择题使用）',
    `answer` TEXT NOT NULL COMMENT '标准答案',
    `analysis` TEXT DEFAULT NULL COMMENT '答案解析',
    `knowledge_ids` VARCHAR(500) DEFAULT NULL COMMENT '考查知识点ID列表（JSON数组）',
    `usage_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '使用次数',
    `correct_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '正确次数',
    `correct_rate` DECIMAL(5,2) DEFAULT NULL COMMENT '历史正确率',
    `source` VARCHAR(100) DEFAULT NULL COMMENT '题目来源',
    `is_active` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否启用: 0=禁用, 1=启用',
    `created_by` INT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_subject_id` (`subject_id`),
    KEY `idx_chapter_id` (`chapter_id`),
    KEY `idx_type` (`type`),
    KEY `idx_difficulty` (`difficulty`),
    KEY `idx_is_active` (`is_active`),
    KEY `idx_created_by` (`created_by`),
    CONSTRAINT `fk_questions_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_questions_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapters` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_questions_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='题目表（包含大题小题）';

-- ================================================================
-- 7. 试卷表
-- ================================================================
CREATE TABLE `papers` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '试卷ID',
    `name` VARCHAR(100) NOT NULL COMMENT '试卷名称',
    `subject_id` INT UNSIGNED NOT NULL COMMENT '学科ID',
    `grade_level` TINYINT UNSIGNED DEFAULT NULL COMMENT '适用年级: 1=高一, 2=高二, 3=高三',
    `total_score` INT UNSIGNED NOT NULL DEFAULT 100 COMMENT '总分',
    `total_time` INT UNSIGNED NOT NULL DEFAULT 120 COMMENT '建议用时（分钟）',
    `question_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '题目数量',
    `difficulty_avg` DECIMAL(3,2) DEFAULT NULL COMMENT '平均难度',
    `source_type` TINYINT UNSIGNED DEFAULT 1 COMMENT '来源: 1=手动选题, 2=智能组卷',
    `created_by` INT UNSIGNED DEFAULT NULL COMMENT '创建人ID',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '试卷描述',
    `is_published` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否发布: 0=未发布, 1=已发布',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_subject_id` (`subject_id`),
    KEY `idx_grade_level` (`grade_level`),
    KEY `idx_source_type` (`source_type`),
    KEY `idx_is_published` (`is_published`),
    KEY `idx_created_by` (`created_by`),
    CONSTRAINT `fk_papers_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE RESTRICT ON UPDATE CASCADE,
    CONSTRAINT `fk_papers_creator` FOREIGN KEY (`created_by`) REFERENCES `users` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试卷表';

-- ================================================================
-- 8. 试卷题目关联表
-- ================================================================
CREATE TABLE `paper_questions` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `paper_id` INT UNSIGNED NOT NULL COMMENT '试卷ID',
    `question_id` INT UNSIGNED NOT NULL COMMENT '题目ID',
    `order_num` INT UNSIGNED NOT NULL COMMENT '题目顺序',
    `score` INT UNSIGNED DEFAULT NULL COMMENT '本题分值',
    `is_required` TINYINT UNSIGNED NOT NULL DEFAULT 1 COMMENT '是否必答: 0=选答, 1=必答',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_paper_question` (`paper_id`, `question_id`),
    KEY `idx_paper_id` (`paper_id`),
    KEY `idx_question_id` (`question_id`),
    KEY `idx_order_num` (`order_num`),
    CONSTRAINT `fk_pq_paper` FOREIGN KEY (`paper_id`) REFERENCES `papers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_pq_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='试卷题目关联表';

-- ================================================================
-- 9. 学习计划表
-- ================================================================
CREATE TABLE `study_plans` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '计划ID',
    `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
    `name` VARCHAR(100) NOT NULL COMMENT '计划名称',
    `type` TINYINT UNSIGNED DEFAULT 1 COMMENT '计划类型: 1=日常计划, 2=冲刺计划, 3=专项计划',
    `target_date` DATE DEFAULT NULL COMMENT '目标日期',
    `total_days` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '计划总天数',
    `completed_days` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '已完成天数',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态: 0=草稿, 1=进行中, 2=已完成, 3=已放弃',
    `description` VARCHAR(500) DEFAULT NULL COMMENT '计划描述',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_status` (`status`),
    KEY `idx_type` (`type`),
    KEY `idx_target_date` (`target_date`),
    CONSTRAINT `fk_study_plans_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习计划表';

-- ================================================================
-- 10. 每日任务表
-- ================================================================
CREATE TABLE `daily_tasks` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '任务ID',
    `plan_id` INT UNSIGNED NOT NULL COMMENT '计划ID',
    `task_date` DATE NOT NULL COMMENT '任务日期',
    `subject_id` INT UNSIGNED DEFAULT NULL COMMENT '学科ID',
    `chapter_id` INT UNSIGNED DEFAULT NULL COMMENT '章节ID',
    `knowledge_ids` VARCHAR(500) DEFAULT NULL COMMENT '知识点ID列表（JSON数组）',
    `target_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '目标完成数量',
    `completed_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '实际完成数量',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态: 0=待完成, 1=进行中, 2=已完成',
    `estimated_time` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '预计耗时（分钟）',
    `actual_time` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '实际耗时（分钟）',
    `notes` TEXT DEFAULT NULL COMMENT '任务备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_plan_id` (`plan_id`),
    KEY `idx_task_date` (`task_date`),
    KEY `idx_subject_id` (`subject_id`),
    KEY `idx_chapter_id` (`chapter_id`),
    KEY `idx_status` (`status`),
    KEY `idx_user_date` (`user_id`, `task_date`),
    CONSTRAINT `fk_daily_tasks_plan` FOREIGN KEY (`plan_id`) REFERENCES `study_plans` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_daily_tasks_subject` FOREIGN KEY (`subject_id`) REFERENCES `subjects` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_daily_tasks_chapter` FOREIGN KEY (`chapter_id`) REFERENCES `chapters` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='每日任务表（学习计划执行记录）';

-- ================================================================
-- 11. 学习记录表
-- ================================================================
CREATE TABLE `study_records` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '记录ID',
    `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
    `knowledge_id` INT UNSIGNED NOT NULL COMMENT '知识点ID',
    `study_duration` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '学习时长（秒）',
    `status` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '状态: 0=未完成, 1=学习中, 2=已掌握',
    `notes` TEXT DEFAULT NULL COMMENT '个人笔记',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_knowledge` (`user_id`, `knowledge_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_knowledge_id` (`knowledge_id`),
    KEY `idx_status` (`status`),
    CONSTRAINT `fk_study_records_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_study_records_knowledge` FOREIGN KEY (`knowledge_id`) REFERENCES `knowledge_points` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='学习记录表';

-- ================================================================
-- 12. 用户收藏表
-- ================================================================
CREATE TABLE `user_favorites` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
    `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
    `favorite_type` TINYINT UNSIGNED NOT NULL COMMENT '收藏类型: 1=知识点, 2=题目, 3=试卷',
    `target_id` INT UNSIGNED NOT NULL COMMENT '目标ID（知识点/题目/试卷ID）',
    `note` VARCHAR(200) DEFAULT NULL COMMENT '收藏备注',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_favorite` (`user_id`, `favorite_type`, `target_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_favorite_type` (`favorite_type`),
    KEY `idx_target_id` (`target_id`),
    CONSTRAINT `fk_user_favorites_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='用户收藏表';

-- ================================================================
-- 13. 考试记录表
-- ================================================================
CREATE TABLE `exam_records` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '考试记录ID',
    `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
    `paper_id` INT UNSIGNED NOT NULL COMMENT '试卷ID',
    `total_score` INT UNSIGNED DEFAULT NULL COMMENT '总分',
    `obtained_score` INT UNSIGNED DEFAULT NULL COMMENT '得分',
    `score_rate` DECIMAL(5,2) DEFAULT NULL COMMENT '得分率',
    `duration` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '答题时长（分钟）',
    `answers_json` TEXT DEFAULT NULL COMMENT '答题记录（JSON格式）',
    `is_completed` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否完成: 0=未完成, 1=已完成',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '考试开始时间',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_paper_id` (`paper_id`),
    KEY `idx_is_completed` (`is_completed`),
    KEY `idx_created_at` (`created_at`),
    CONSTRAINT `fk_exam_records_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_exam_records_paper` FOREIGN KEY (`paper_id`) REFERENCES `papers` (`id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='考试记录表';

-- ================================================================
-- 14. 错题本表
-- ================================================================
CREATE TABLE `wrong_questions` (
    `id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '错题ID',
    `user_id` INT UNSIGNED NOT NULL COMMENT '用户ID',
    `question_id` INT UNSIGNED NOT NULL COMMENT '题目ID',
    `paper_id` INT UNSIGNED DEFAULT NULL COMMENT '试卷ID',
    `exam_record_id` INT UNSIGNED DEFAULT NULL COMMENT '考试记录ID',
    `user_answer` TEXT DEFAULT NULL COMMENT '用户答案',
    `correct_answer` TEXT DEFAULT NULL COMMENT '正确答案',
    `is_reviewed` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '是否已复习: 0=未复习, 1=已复习',
    `review_count` INT UNSIGNED NOT NULL DEFAULT 0 COMMENT '复习次数',
    `last_review_at` DATETIME DEFAULT NULL COMMENT '最后复习时间',
    `review_note` TEXT DEFAULT NULL COMMENT '复习笔记',
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间（收录时间）',
    `updated_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_user_question` (`user_id`, `question_id`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_question_id` (`question_id`),
    KEY `idx_paper_id` (`paper_id`),
    KEY `idx_is_reviewed` (`is_reviewed`),
    CONSTRAINT `fk_wrong_questions_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_wrong_questions_question` FOREIGN KEY (`question_id`) REFERENCES `questions` (`id`) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT `fk_wrong_questions_paper` FOREIGN KEY (`paper_id`) REFERENCES `papers` (`id`) ON DELETE SET NULL ON UPDATE CASCADE,
    CONSTRAINT `fk_wrong_questions_exam` FOREIGN KEY (`exam_record_id`) REFERENCES `exam_records` (`id`) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci COMMENT='错题本表';

-- ================================================================
-- 初始化数据
-- ================================================================

-- 插入年级数据
INSERT INTO `grades` (`name`, `level`, `description`) VALUES
('高一', 1, '高中一年级，基础知识积累阶段'),
('高二', 2, '高中二年级，重点知识深化阶段'),
('高三', 3, '高中三年级，高考冲刺阶段');

-- 插入学科数据
INSERT INTO `subjects` (`name`, `code`, `grade_level`, `icon_url`, `description`, `is_active`, `sort_order`) VALUES
('语文', 'chinese', 4, NULL, '高中语文课程', 1, 1),
('数学', 'math', 4, NULL, '高中数学课程', 1, 2),
('英语', 'english', 4, NULL, '高中英语课程', 1, 3),
('物理', 'physics', 4, NULL, '高中物理课程', 1, 4),
('化学', 'chemistry', 4, NULL, '高中化学课程', 1, 5),
('生物', 'biology', 4, NULL, '高中生物课程', 1, 6),
('历史', 'history', 4, NULL, '高中历史课程', 1, 7),
('地理', 'geography', 4, NULL, '高中地理课程', 1, 8),
('政治', 'politics', 4, NULL, '高中政治课程', 1, 9);

-- ================================================================
-- 视图定义（可选）
-- ================================================================

-- 用户学习统计视图
CREATE OR REPLACE VIEW `v_user_study_stats` AS
SELECT 
    u.id AS user_id,
    u.username,
    u.real_name,
    u.grade,
    COUNT(DISTINCT sr.id) AS total_study_records,
    COALESCE(SUM(sr.study_duration), 0) AS total_study_duration,
    COUNT(DISTINCT CASE WHEN sr.status = 2 THEN sr.knowledge_id END) AS mastered_count,
    COUNT(DISTINCT er.id) AS total_exams,
    COALESCE(AVG(er.score_rate), 0) AS avg_score_rate
FROM users u
LEFT JOIN study_records sr ON u.id = sr.user_id
LEFT JOIN exam_records er ON u.id = er.user_id AND er.is_completed = 1
WHERE u.role = 'student'
GROUP BY u.id, u.username, u.real_name, u.grade;

-- 知识点统计视图
CREATE OR REPLACE VIEW `v_knowledge_stats` AS
SELECT 
    kp.id AS knowledge_id,
    kp.title,
    c.name AS chapter_name,
    s.name AS subject_name,
    kp.view_count,
    kp.favorite_count,
    COUNT(DISTINCT sr.id) AS study_count,
    COUNT(DISTINCT CASE WHEN sr.status = 2 THEN sr.user_id END) AS mastered_count
FROM knowledge_points kp
LEFT JOIN chapters c ON kp.chapter_id = c.id
LEFT JOIN subjects s ON c.subject_id = s.id
LEFT JOIN study_records sr ON kp.id = sr.knowledge_id
GROUP BY kp.id, kp.title, c.name, s.name, kp.view_count, kp.favorite_count;

-- ================================================================
-- 存储过程示例（可选）
-- ================================================================

-- 更新题目正确率
DELIMITER //
CREATE PROCEDURE `sp_update_question_correct_rate`(IN p_question_id INT)
BEGIN
    DECLARE v_usage_count INT;
    DECLARE v_correct_count INT;
    
    SELECT usage_count, correct_count INTO v_usage_count, v_correct_count
    FROM questions WHERE id = p_question_id;
    
    IF v_usage_count > 0 THEN
        UPDATE questions 
        SET correct_rate = (v_correct_count / v_usage_count) * 100
        WHERE id = p_question_id;
    END IF;
END //
DELIMITER ;

-- ================================================================
-- 注释结束
-- ================================================================