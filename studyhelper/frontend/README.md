# StudyHelper Frontend

高中生助学网站 Vue3 前端项目

## 技术栈

- Vue 3 + Composition API + TypeScript
- Vite 5
- Element Plus
- Vue Router 4
- Pinia 状态管理
- Axios HTTP 客户端

## 项目结构

```
src/
├── api/              # API 接口定义
├── components/       # 公共组件
├── views/            # 页面
├── stores/           # Pinia 状态
├── router/          # 路由配置
├── types/            # TypeScript 类型
└── styles/           # 全局样式
```

## 快速开始

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 构建生产版本
npm run build

# 预览生产版本
npm run preview
```

## 页面说明

- 首页：学习概览、快速入口、统计数据
- 知识库：学科选择、章节树、知识点详情、收藏功能
- 学习计划：计划列表、创建计划、任务日历、执行记录
- 试卷：试卷列表、答题界面、成绩分析
