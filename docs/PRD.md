# 高中生助学网站 StudyHelper - 产品需求文档

## 文档信息

| 项目名称 | 高中生助学网站 StudyHelper |
|---------|------------------------|
| 版本号   | V1.0                   |
| 创建日期 | 2026-05-11             |
| 文档状态 | 草稿                   |

---

## 1. 产品概述

### 1.1 产品定位

StudyHelper 是一款面向高中生的智能化助学平台，致力于整合高一至高三全学科知识体系，提供科学的学习规划和精准的试卷生成服务，帮助学生高效备考、稳步提升。

### 1.2 目标用户

| 用户群体 | 年龄段 | 核心诉求 |
|---------|--------|---------|
| 高中生   | 15-18岁 | 系统化学习知识、制定学习计划、检验学习成果 |
| 家长     | 35-50岁 | 了解孩子学习进度、提供监督支持 |
| 教师/管理员 | 25-55岁 | 管理知识库、维护系统、查看学习数据 |

### 1.3 核心价值

- **知识聚合**：整合高中三年全部学科知识，支持快速检索和专项复习
- **计划驱动**：帮助学生制定、执行、追踪个性化学习计划
- **精准测评**：自动生成符合学生水平的模拟试卷，即时反馈学习效果

---

## 2. 用户角色

### 2.1 学生

| 字段 | 说明 |
|-----|------|
| role_id | 角色ID（固定值：student） |
| grade | 年级（1=高一、2=高二、3=高三） |
| study_target | 升学目标（如：985/211/一本） |
| weak_subjects | 薄弱学科列表 |

**权限范围**：
- 查看和收藏知识点
- 创建和管理个人学习计划
- 参与测评和查看成绩
- 查看学习数据统计

### 2.2 家长

| 字段 | 说明 |
|-----|------|
| role_id | 角色ID（固定值：parent） |
| student_id | 关联学生ID |
| bind_code | 绑定码（6位数字） |

**权限范围**：
- 绑定和解绑学生账号
- 查看学生学习进度和计划完成情况
- 查看考试成绩和错题分析
- 接收学习提醒通知

### 2.3 管理员

| 字段 | 说明 |
|-----|------|
| role_id | 角色ID（固定值：admin） |
| admin_level | 管理员级别（1=超级管理员、2=内容管理员） |

**权限范围**：
- 知识库增删改查
- 用户管理
- 试卷题目管理
- 数据统计分析

---

## 3. 功能需求

### 3.1 知识库管理

#### 3.1.1 学科体系

| 字段 | 类型 | 说明 |
|-----|------|------|
| subject_id | INT | 学科ID |
| subject_name | VARCHAR(50) | 学科名称（语文/数学/英语/物理/化学/生物/历史/地理/政治） |
| grade_level | TINYINT | 适用年级（1=高一、2=高二、3=高三、4=通用） |
| is_active | BOOLEAN | 是否启用 |

#### 3.1.2 章节结构

| 字段 | 类型 | 说明 |
|-----|------|------|
| chapter_id | INT | 章节ID |
| subject_id | INT | 所属学科ID |
| chapter_name | VARCHAR(100) | 章节名称 |
| chapter_order | INT | 章节顺序 |
| parent_chapter_id | INT | 父章节ID（用于大章节-小节结构） |
| knowledge_count | INT | 知识点数量 |

#### 3.1.3 知识点详情

| 字段 | 类型 | 说明 |
|-----|------|------|
| knowledge_id | INT | 知识点ID |
| chapter_id | INT | 所属章节ID |
| knowledge_title | VARCHAR(200) | 知识点标题 |
| knowledge_content | TEXT | 知识点内容（支持富文本） |
| importance_level | TINYINT | 重要程度（1-5） |
| difficulty_level | TINYINT | 难度等级（1-5） |
| exam_frequency | VARCHAR(20) | 考频统计（高频/中频/低频） |
| tags | VARCHAR(500) | 标签（ JSON数组格式） |
| related_knowledge_ids | VARCHAR(200) | 关联知识点ID列表 |

**功能要求**：
- 支持按学科、年级、章节、关键词检索
- 支持收藏和笔记功能
- 支持知识点的学习状态标记（未学习/学习中/已掌握）
- 支持知识点浏览量统计

### 3.2 学习计划管理

#### 3.2.1 计划结构

| 字段 | 类型 | 说明 |
|-----|------|------|
| plan_id | INT | 计划ID |
| user_id | INT | 用户ID |
| plan_name | VARCHAR(100) | 计划名称 |
| plan_type | TINYINT | 计划类型（1=日常计划、2=冲刺计划、3=专项计划） |
| target_date | DATE | 目标日期 |
| total_days | INT | 计划总天数 |
| status | TINYINT | 状态（0=草稿、1=进行中、2=已完成、3=已放弃） |

#### 3.2.2 每日任务

| 字段 | 类型 | 说明 |
|-----|------|------|
| task_id | INT | 任务ID |
| plan_id | INT | 所属计划ID |
| task_date | DATE | 任务日期 |
| subject_id | INT | 学科ID |
| chapter_id | INT | 章节ID（可为空） |
| knowledge_ids | VARCHAR(500) | 知识点ID列表（JSON数组） |
| target_count | INT | 目标完成数量 |
| completed_count | INT | 实际完成数量 |
| estimated_time | INT | 预计耗时（分钟） |
| actual_time | INT | 实际耗时（分钟） |
| status | TINYINT | 状态（0=待完成、1=进行中、2=已完成） |

**功能要求**：
- 支持一键生成学习计划（基于薄弱学科智能分配）
- 支持手动调整每日任务
- 支持番茄钟计时功能
- 支持任务完成打卡
- 支持计划进度可视化（环形图/柱状图）
- 支持提醒功能（早/中/晚）

### 3.3 试卷生成

#### 3.3.1 题目结构

| 字段 | 类型 | 说明 |
|-----|------|------|
| question_id | INT | 题目ID |
| subject_id | INT | 学科ID |
| chapter_id | INT | 所属章节ID |
| question_type | TINYINT | 题型（1=单选题、2=多选题、3=填空题、4=简答题、5=计算题、6=作文） |
| difficulty | TINYINT | 难度（1=简单、2=中等、3=困难） |
| score | INT | 分值 |
| question_content | TEXT | 题干 |
| options | TEXT | 选项（JSON数组，选择题专用） |
| answer | TEXT | 标准答案 |
| analysis | TEXT | 答案解析 |
| knowledge_points | VARCHAR(500) | 考查知识点（JSON数组） |
| usage_count | INT | 使用次数 |
| correct_rate | DECIMAL(5,2) | 历史正确率 |

#### 3.3.2 试卷结构

| 字段 | 类型 | 说明 |
|-----|------|------|
| paper_id | INT | 试卷ID |
| paper_name | VARCHAR(100) | 试卷名称 |
| subject_id | INT | 学科ID |
| grade_level | TINYINT | 适用年级 |
| total_score | INT | 总分 |
| total_time | INT | 建议用时（分钟） |
| question_count | INT | 题目数量 |
| difficulty_avg | DECIMAL(3,2) | 平均难度 |
| source_type | TINYINT | 来源（1=手动选题、2=智能组卷） |
| created_by | INT | 创建人/创建方式 |

#### 3.3.3 组卷策略

| 策略名称 | 说明 |
|---------|------|
| 智能组卷 | 根据目标学科、难度分布、知识点覆盖自动生成 |
| 薄弱专项 | 针对用户薄弱知识点定向组卷 |
| 考前模拟 | 按正式考试标准（题型、分值、时间）生成 |
| 章节测试 | 按单章节或跨章节范围组卷 |

**功能要求**：
- 支持按学科、年级、章节、知识点、难度筛选题目
- 支持手动选题和智能组卷两种模式
- 支持试卷预览和导出（PDF格式）
- 支持在线答题和自动评分
- 支持错题自动收录到错题本
- 支持成绩趋势分析

---

## 4. 非功能需求

### 4.1 性能需求

| 指标 | 要求 |
|-----|------|
| 页面加载时间 | 首屏 < 2秒，完整页面 < 3秒 |
| 接口响应时间 | P95 < 500ms |
| 并发用户数 | 支持 1000+ 同时在线 |
| 试卷生成速度 | < 5秒生成一套试卷 |
| 搜索响应时间 | < 300ms |

### 4.2 安全需求

| 需求项 | 说明 |
|-------|------|
| 身份认证 | JWT Token 认证，有效期24小时 |
| 密码加密 | BCrypt 加密存储 |
| 数据传输 | HTTPS 全站加密 |
| 敏感操作 | 验证码/短信二次验证 |
| XSS防护 | 输入过滤和输出编码 |
| CSRF防护 | Token 验证机制 |
| SQL注入防护 | 参数化查询 |

### 4.3 兼容性需求

| 平台 | 要求 |
|-----|------|
| PC浏览器 | Chrome 80+、Firefox 75+、Safari 13+、Edge 80+ |
| 移动端 | iOS 12+、Android 8+ |
| 响应式布局 | 支持 375px-1920px 屏幕宽度 |

### 4.4 可用性需求

| 指标 | 要求 |
|-----|------|
| 系统可用性 | 99.5% |
| 数据备份 | 每日增量备份，每周全量备份 |
| 故障恢复 | < 4小时 |

---

## 5. 用户流程图

### 5.1 学生学习主流程

```
[登录/注册]
    ↓
[完善个人信息（年级、学科偏好）]
    ↓
[首页：学习概览]
    ├→ [知识库浏览] → [选择学科] → [选择章节] → [学习知识点] → [标记学习状态]
    │
    ├→ [学习计划] → [创建计划/查看现有计划] → [执行每日任务] → [打卡记录]
    │               ↓
    │           [计划完成/调整计划]
    │
    └→ [试卷中心] → [生成试卷] → [在线答题] → [提交评分] → [查看成绩+错题分析]
                      ↓
                  [薄弱知识点复习]
```

### 5.2 家长监督流程

```
[登录/注册家长账号]
    ↓
[绑定学生账号（输入绑定码）]
    ↓
[查看学生看板]
    ├→ [学习进度] → [查看日/周/月统计]
    │
    ├→ [计划执行] → [查看今日任务完成情况]
    │
    ├→ [成绩分析] → [查看历次考试成绩和排名变化]
    │
    └→ [消息提醒] → [接收学习异常提醒]
```

### 5.3 管理员后台流程

```
[管理员登录]
    ↓
[知识库管理] → [添加/编辑/删除知识点]
    ↓
[题目管理] → [添加/编辑/删除题目]
    ↓
[用户管理] → [查看用户列表/禁用账号]
    ↓
[数据分析] → [查看访问统计/使用数据]
```

---

## 6. 竞品分析

### 6.1 主要竞品对比

| 维度 | StudyHelper | 猿辅导 | 作业帮 | 学而思网校 |
|-----|-------------|--------|--------|-----------|
| 目标用户 | 高中生 | K12全学段 | K12全学段 | K12全学段 |
| 知识库 | 高中三年全覆盖 | 全学段覆盖 | 全学段覆盖 | 全学段覆盖 |
| 学习计划 | 智能+手动双模式 | 有 | 无 | 有 |
| 试卷生成 | 智能组卷+手动选题 | 无 | 题库搜索 | 无 |
| AI功能 | 智能组卷/薄弱分析 | AI批改 | 拍照搜题 | AI老师 |
| 家长监管 | 进度看板 | 家长端 | 家长通 | 家长旁听 |
| 价格策略 | 基础免费+增值付费 | 付费课程 | 免费+付费 | 付费课程 |

### 6.2 StudyHelper 差异化优势

1. **专注高中**：聚焦高中三年，避免功能分散，深度优化
2. **知识图谱**：建立知识点关联图，支持智能推导学习路径
3. **精准测评**：基于用户历史数据，智能生成个性化试卷
4. **轻量高效**：无需下载APP，浏览器即用，降低使用门槛

### 6.3 市场机会

- 高中生时间紧张，需要高效学习工具
- 家长对监督工具有强需求
- 市面缺乏专注高中阶段的垂直平台
- AI技术可大幅提升个性化体验

---

## 7. 数据字典（核心实体）

### 7.1 用户表 (users)

```sql
CREATE TABLE users (
    id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    role ENUM('student', 'parent', 'admin') NOT NULL,
    real_name VARCHAR(50),
    phone VARCHAR(20),
    email VARCHAR(100),
    grade TINYINT COMMENT '1=高一,2=高二,3=高三',
    avatar_url VARCHAR(255),
    bind_code VARCHAR(6) COMMENT '家长绑定码',
    parent_user_id INT COMMENT '关联家长ID',
    status TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 7.2 学习记录表 (study_records)

```sql
CREATE TABLE study_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    knowledge_id INT NOT NULL,
    study_duration INT COMMENT '学习时长（秒）',
    status TINYINT COMMENT '0=未完成,1=已掌握',
    notes TEXT COMMENT '个人笔记',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

### 7.3 考试记录表 (exam_records)

```sql
CREATE TABLE exam_records (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    paper_id INT NOT NULL,
    total_score INT,
    obtained_score INT,
    duration INT COMMENT '答题时长（分钟）',
    answers_json TEXT COMMENT '答题记录JSON',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
```

---

## 8. 附录

### 8.1 术语表

| 术语 | 说明 |
|-----|------|
| 知识点 | 最小学习单元，包含一个具体的学习内容 |
| 章节 | 知识点的上一级组织单位 |
| 学科 | 章节的上一级分类，如数学、语文等 |
| 智能组卷 | 基于算法自动选择题目组成试卷 |
| 薄弱知识点 | 用户多次答错或正确率低于阈值的知识点 |

### 8.2 文档版本历史

| 版本 | 日期 | 修改内容 | 作者 |
|-----|------|---------|------|
| V1.0 | 2026-05-11 | 初始版本 | Hermes Agent |
