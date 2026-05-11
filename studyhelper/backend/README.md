# StudyHelper 高中生助学平台后端

## 项目简介
高中生助学平台后端服务，提供用户管理、知识库、学习计划、试卷管理等功能。

## 技术栈
- Java 17
- Spring Boot 3.2
- MyBatis-Plus 3.5
- MySQL 8.0
- JWT 认证
- Lombok

## 项目结构
```
src/main/java/com/studyhelper/
├── StudyHelperApplication.java    # 应用入口
├── config/                        # 配置类
├── controller/                    # 控制器层
├── service/                       # 服务层
├── mapper/                         # 数据访问层
├── entity/                         # 实体类
├── dto/                            # 数据传输对象
├── common/                         # 通用类
└── utils/                          # 工具类
```

## 快速开始

### 1. 环境要求
- JDK 17+
- Maven 3.6+
- MySQL 8.0+

### 2. 数据库配置
1. 创建数据库：
```sql
CREATE DATABASE studyhelper DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

2. 执行SQL脚本（docs/studyhelper.sql）

3. 修改 application.yml 中的数据库连接配置

### 3. 编译运行
```bash
# 编译
mvn clean package

# 运行
java -jar target/studyhelper-backend-1.0.0.jar

# 或使用Maven运行
mvn spring-boot:run
```

### 4. 访问接口
- 基础URL: http://localhost:8080/api
- 用户注册: POST /api/user/register
- 用户登录: POST /api/user/login

## API接口

### 用户管理
- POST /api/user/register - 用户注册
- POST /api/user/login - 用户登录
- GET /api/user/profile - 获取用户信息
- PUT /api/user/profile - 更新用户信息
- PUT /api/user/password - 修改密码
- POST /api/user/bind - 家长绑定学生
- GET /api/user/bound-students - 获取绑定学生列表

### 知识库
- GET /api/knowledge/subjects - 获取学科列表
- GET /api/knowledge/chapters - 获取章节树
- GET /api/knowledge/points - 获取知识点列表

### 学习计划
- POST /api/plan - 创建学习计划
- GET /api/plan - 获取计划列表
- GET /api/plan/{id} - 获取计划详情
- PUT /api/plan/{id} - 更新计划
- DELETE /api/plan/{id} - 删除计划
- GET /api/plan/{id}/tasks - 获取每日任务
- POST /api/plan/tasks/{taskId}/execute - 执行任务

### 试卷管理
- GET /api/papers - 获取试卷列表
- GET /api/papers/{id} - 获取试卷详情
- GET /api/papers/{id}/questions - 获取试卷题目
- POST /api/papers - 创建试卷
- POST /api/papers/generate - 智能组卷
- POST /api/papers/{paperId}/exam - 开始考试
- POST /api/papers/exam/{examRecordId}/submit - 提交考试
- GET /api/papers/exam/records - 获取考试记录

## 配置说明

### JWT配置
```yaml
jwt:
  secret: 密钥（至少256位）
  expiration: 过期时间（毫秒）
  header: 请求头名称
  prefix: Token前缀
```

### 数据库配置
```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/studyhelper
    username: root
    password: root
```

## 开发说明

### 代码规范
- 使用Lombok简化代码
- 统一响应格式使用Result类
- 控制器层不包含业务逻辑
- 使用DTO进行数据传输

### 认证说明
- 除注册、登录和公开接口外，所有接口需要JWT认证
- 请求头: Authorization: Bearer {token}
