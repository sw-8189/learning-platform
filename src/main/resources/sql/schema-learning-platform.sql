-- learning-platform/src/main/resources/sql/schema-learning-platform.sql

-- 创建数据库
CREATE DATABASE IF NOT EXISTS learning_platform
    DEFAULT CHARACTER SET utf8mb4
    COLLATE utf8mb4_general_ci;

USE learning_platform;

-- 用户表（包含你的学习信息字段）
DROP TABLE IF EXISTS user;
CREATE TABLE user (
                      id BIGINT PRIMARY KEY AUTO_INCREMENT,
                      username VARCHAR(50) NOT NULL UNIQUE,
                      password VARCHAR(100) NOT NULL,
                      phone VARCHAR(20) UNIQUE,
                      email VARCHAR(100) UNIQUE,
                      avatar_url VARCHAR(2000) DEFAULT '/image/avatar/default-avatar.png',
                      role VARCHAR(20) NOT NULL DEFAULT 'USER',
                      gender VARCHAR(20),
                      learning_preference VARCHAR(255),
                      course_interest VARCHAR(255),
                      learning_goal VARCHAR(50),
                      status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '用户状态：ACTIVE(正常), FROZEN(冻结)',
                      create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                      update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);


-- 课程表
DROP TABLE IF EXISTS course;
CREATE TABLE course (
                        id BIGINT PRIMARY KEY AUTO_INCREMENT,
                        title VARCHAR(100) NOT NULL,
                        description TEXT,
                        detail_description TEXT COMMENT '课程详细描述',
                        level VARCHAR(20),
                        category VARCHAR(50),
                        cover_url VARCHAR(255),
                        teacher VARCHAR(50),
                        teacher_intro TEXT COMMENT '讲师介绍',
                        price DECIMAL(10,2) DEFAULT 0,
                        tags VARCHAR(500) COMMENT '课程标签，多个标签用逗号分隔，包含学习偏好和课程兴趣',
                        course_outline TEXT COMMENT '课程大纲，JSON格式存储',
                        course_images TEXT COMMENT '课程图片，JSON格式存储多个图片URL',
                        duration VARCHAR(50) COMMENT '课程时长，如：40小时',
                        student_count INT DEFAULT 0 COMMENT '学习人数',
                        rating DECIMAL(3,2) DEFAULT 0 COMMENT '课程评分，0-5分',
                        create_time DATETIME DEFAULT CURRENT_TIMESTAMP
);

-- 用户课程关联表
DROP TABLE IF EXISTS user_course;
CREATE TABLE user_course (
                             id BIGINT PRIMARY KEY AUTO_INCREMENT,
                             user_id BIGINT NOT NULL,
                             course_id BIGINT NOT NULL,
                             join_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                             UNIQUE KEY uk_user_course (user_id, course_id),
                             KEY idx_user_id (user_id),
                             KEY idx_course_id (course_id)
);

-- 插入示例课程数据（包含标签和详情）
INSERT INTO course (title, description, detail_description, level, category, cover_url, teacher, teacher_intro, price, tags, course_outline, course_images, duration, student_count, rating) VALUES
('Java 基础入门', '从零开始学习 Java 编程语言，掌握面向对象编程基础', '本课程是Java编程的入门课程，适合零基础的学员。课程从Java语言的基础语法开始，逐步深入讲解面向对象编程的核心概念，包括类、对象、继承、多态、封装等。通过大量实际案例和编程练习，帮助学员建立扎实的Java编程基础。\n\n课程特色：\n1. 零基础友好，循序渐进\n2. 理论与实践相结合\n3. 丰富的编程练习和项目实战\n4. 专业的讲师团队指导\n\n学完本课程后，你将能够：\n- 掌握Java基础语法和数据类型\n- 理解面向对象编程思想\n- 能够编写简单的Java应用程序\n- 为后续深入学习Java框架打下坚实基础', '初级', '编程开发', '/image/course/java-basic.jpg', '张老师', '张老师拥有10年Java开发经验，曾在多家知名互联网公司担任高级工程师和技术专家。擅长Java核心技术、Spring框架、微服务架构等领域。教学风格深入浅出，注重实践，已培养数千名Java开发工程师。', 0, '编程开发,动手实践,视觉学习,职业提升', '[{"title":"第一章：Java开发环境搭建","lessons":[{"title":"Java简介与发展历史","duration":"30分钟"},{"title":"JDK安装与配置","duration":"45分钟"},{"title":"第一个Java程序","duration":"40分钟"},{"title":"IDE工具使用（IntelliJ IDEA）","duration":"50分钟"}]},{"title":"第二章：Java基础语法","lessons":[{"title":"变量与数据类型","duration":"50分钟"},{"title":"运算符与表达式","duration":"45分钟"},{"title":"流程控制语句","duration":"60分钟"},{"title":"数组的使用","duration":"50分钟"}]},{"title":"第三章：面向对象编程","lessons":[{"title":"类与对象的概念","duration":"50分钟"},{"title":"封装与访问控制","duration":"45分钟"},{"title":"继承与多态","duration":"60分钟"},{"title":"接口与抽象类","duration":"55分钟"}]},{"title":"第四章：Java核心API","lessons":[{"title":"String字符串处理","duration":"50分钟"},{"title":"集合框架（List、Set、Map）","duration":"80分钟"},{"title":"异常处理机制","duration":"50分钟"},{"title":"IO流操作","duration":"60分钟"}]},{"title":"第五章：项目实战","lessons":[{"title":"学生管理系统开发","duration":"120分钟"},{"title":"图书管理系统开发","duration":"150分钟"}]}]', '["/image/course/java-basic-1.jpg","/image/course/java-basic-2.jpg","/image/course/java-basic-3.jpg"]', '40小时', 1250, 4.8),
('前端开发实战', '学习 HTML、CSS、JavaScript，构建现代化网页应用', '本课程是前端开发的入门到进阶课程，全面覆盖HTML、CSS、JavaScript三大核心技术。课程采用项目驱动式教学，通过多个真实项目案例，帮助学员掌握现代前端开发技能。\n\n课程内容：\n1. HTML5语义化标签和表单\n2. CSS3布局、动画和响应式设计\n3. JavaScript ES6+新特性\n4. DOM操作和事件处理\n5. 前端工程化工具使用\n\n项目实战：\n- 个人作品集网站\n- 电商产品展示页\n- 待办事项应用\n- 天气查询应用', '初级', '前端开发', '/image/course/frontend.jpg', '李老师', '李老师是资深前端开发工程师，拥有8年前端开发经验。精通Vue.js、React等主流框架，曾参与多个大型前端项目的架构设计。擅长将复杂的前端技术以通俗易懂的方式传授给学员。', 0, '前端学习,视觉学习,动手实践,职业提升', '[{"title":"第一章：HTML基础","lessons":[{"title":"HTML文档结构","duration":"40分钟"},{"title":"常用标签与属性","duration":"60分钟"},{"title":"表单元素","duration":"50分钟"},{"title":"HTML5新特性","duration":"45分钟"}]},{"title":"第二章：CSS样式设计","lessons":[{"title":"CSS选择器与优先级","duration":"50分钟"},{"title":"布局技术（Flexbox、Grid）","duration":"80分钟"},{"title":"CSS3动画与过渡","duration":"60分钟"},{"title":"响应式设计","duration":"70分钟"}]},{"title":"第三章：JavaScript编程","lessons":[{"title":"变量、数据类型与运算符","duration":"50分钟"},{"title":"函数与作用域","duration":"60分钟"},{"title":"DOM操作","duration":"70分钟"},{"title":"事件处理","duration":"60分钟"},{"title":"ES6+新特性","duration":"80分钟"}]},{"title":"第四章：项目实战","lessons":[{"title":"个人作品集网站开发","duration":"180分钟"},{"title":"待办事项应用开发","duration":"150分钟"}]}]', '["/image/course/frontend-1.jpg","/image/course/frontend-2.jpg"]', '35小时', 980, 4.7),
('Python 数据分析', '使用 Python 进行数据清洗、处理和可视化分析', '本课程全面介绍使用Python进行数据分析的核心技能。从数据获取、清洗、处理到可视化，系统讲解pandas、numpy、matplotlib等核心库的使用。通过真实案例项目，帮助学员掌握数据分析的完整流程。\n\n课程特色：\n1. 实战项目驱动，学以致用\n2. 覆盖数据分析全流程\n3. 丰富的案例和练习\n4. 从基础到进阶的系统化学习\n\n学完本课程后，你将能够：\n- 熟练使用pandas进行数据处理\n- 掌握数据清洗和预处理技巧\n- 运用matplotlib和seaborn进行数据可视化\n- 完成真实的数据分析项目', '中级', '数据科学', '/image/course/python-data.jpg', '王老师', '王老师是资深数据科学家，拥有12年数据分析和机器学习经验。曾在多家知名企业担任数据分析专家，擅长Python数据分析、数据挖掘和商业智能。教学风格严谨务实，注重理论与实践结合，已帮助数千名学员成功转型数据分析领域。', 199.00, '数据科学,阅读写作,独立学习,动手实践,职业提升', '[{"title":"第一章：Python数据分析基础","lessons":[{"title":"Python环境搭建与Jupyter使用","duration":"40分钟"},{"title":"NumPy数组操作","duration":"60分钟"},{"title":"Pandas数据结构","duration":"70分钟"},{"title":"数据读取与写入","duration":"50分钟"}]},{"title":"第二章：数据清洗与预处理","lessons":[{"title":"缺失值处理","duration":"50分钟"},{"title":"异常值检测与处理","duration":"45分钟"},{"title":"数据转换与标准化","duration":"60分钟"},{"title":"数据合并与重塑","duration":"55分钟"}]},{"title":"第三章：数据分析与统计","lessons":[{"title":"描述性统计分析","duration":"50分钟"},{"title":"分组聚合操作","duration":"60分钟"},{"title":"透视表与交叉表","duration":"50分钟"},{"title":"时间序列分析","duration":"70分钟"}]},{"title":"第四章：数据可视化","lessons":[{"title":"Matplotlib基础绘图","duration":"60分钟"},{"title":"Seaborn高级可视化","duration":"70分钟"},{"title":"交互式图表制作","duration":"50分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"电商数据分析项目","duration":"120分钟"},{"title":"销售数据可视化分析","duration":"150分钟"}]}]', '["/image/course/python-data-1.jpg","/image/course/python-data-2.jpg"]', '30小时', 850, 4.6),
('Spring Boot 企业级开发', '掌握 Spring Boot 框架，构建企业级应用', '本课程深入讲解Spring Boot框架的核心特性和企业级应用开发实践。从项目搭建、配置管理、数据访问到微服务架构，全面掌握Spring Boot开发技能。通过多个企业级项目实战，帮助学员快速成长为Spring Boot开发专家。\n\n课程内容：\n1. Spring Boot核心原理与自动配置\n2. RESTful API设计与实现\n3. 数据库集成与JPA使用\n4. 安全认证与授权\n5. 微服务架构实践\n\n项目实战：\n- 企业级后台管理系统\n- 电商平台API开发\n- 微服务架构项目', '中级', '后端开发', '/image/course/spring-boot.jpg', '赵老师', '赵老师是Spring技术专家，拥有15年Java企业级开发经验。曾主导多个大型Spring Boot项目的架构设计，精通Spring生态系统、微服务架构和分布式系统。教学风格深入浅出，注重实战，已培养大量Spring Boot开发工程师。', 299.00, '编程开发,动手实践,视觉学习,独立学习,职业提升', '[{"title":"第一章：Spring Boot入门","lessons":[{"title":"Spring Boot简介与优势","duration":"30分钟"},{"title":"项目创建与结构","duration":"40分钟"},{"title":"自动配置原理","duration":"50分钟"},{"title":"配置文件管理","duration":"45分钟"}]},{"title":"第二章：Web开发","lessons":[{"title":"RESTful API设计","duration":"60分钟"},{"title":"请求处理与响应","duration":"50分钟"},{"title":"异常处理机制","duration":"45分钟"},{"title":"文件上传下载","duration":"40分钟"}]},{"title":"第三章：数据访问","lessons":[{"title":"JPA与Hibernate","duration":"70分钟"},{"title":"MyBatis集成","duration":"60分钟"},{"title":"事务管理","duration":"50分钟"},{"title":"多数据源配置","duration":"55分钟"}]},{"title":"第四章：安全与认证","lessons":[{"title":"Spring Security基础","duration":"60分钟"},{"title":"JWT令牌认证","duration":"70分钟"},{"title":"OAuth2集成","duration":"80分钟"}]},{"title":"第五章：微服务实践","lessons":[{"title":"微服务架构设计","duration":"90分钟"},{"title":"服务注册与发现","duration":"70分钟"},{"title":"配置中心使用","duration":"60分钟"},{"title":"分布式事务处理","duration":"80分钟"}]}]', '["/image/course/spring-boot-1.jpg","/image/course/spring-boot-2.jpg","/image/course/spring-boot-3.jpg"]', '50小时', 1200, 4.7),
('Vue.js 全栈开发', '从前端到后端，使用 Vue.js 构建全栈应用', '本课程系统讲解Vue.js全栈开发技术栈，从前端Vue框架到后端Node.js，完整掌握全栈应用开发。课程涵盖Vue 3新特性、Vue Router、Vuex状态管理、Node.js后端开发等核心技术，通过真实项目实战，帮助学员具备独立开发全栈应用的能力。\n\n课程特色：\n1. Vue 3最新特性全面覆盖\n2. 前后端分离架构实践\n3. 完整的项目开发流程\n4. 企业级代码规范与最佳实践\n\n项目实战：\n- 任务管理系统（Todo App）\n- 博客平台全栈开发\n- 电商购物车系统', '中级', '前端开发', '/image/course/vue-fullstack.jpg', '陈老师', '陈老师是全栈开发专家，拥有10年前后端开发经验。精通Vue.js、React、Node.js等技术栈，曾参与多个大型全栈项目的开发与架构设计。擅长将复杂的技术以清晰易懂的方式传授，已帮助众多学员成功转型全栈开发。', 399.00, '前端学习,视觉学习,动手实践,独立学习,技能拓展', '[{"title":"第一章：Vue 3基础","lessons":[{"title":"Vue 3新特性介绍","duration":"40分钟"},{"title":"组合式API使用","duration":"70分钟"},{"title":"响应式系统原理","duration":"60分钟"},{"title":"组件开发基础","duration":"50分钟"}]},{"title":"第二章：Vue生态工具","lessons":[{"title":"Vue Router路由管理","duration":"60分钟"},{"title":"Pinia状态管理","duration":"70分钟"},{"title":"Vue CLI与Vite","duration":"50分钟"},{"title":"组件库使用","duration":"45分钟"}]},{"title":"第三章：Node.js后端开发","lessons":[{"title":"Express框架基础","duration":"60分钟"},{"title":"RESTful API设计","duration":"70分钟"},{"title":"数据库集成（MongoDB/MySQL）","duration":"80分钟"},{"title":"身份认证与授权","duration":"70分钟"}]},{"title":"第四章：前后端联调","lessons":[{"title":"Axios请求封装","duration":"50分钟"},{"title":"跨域问题解决","duration":"40分钟"},{"title":"错误处理机制","duration":"45分钟"}]},{"title":"第五章：项目实战","lessons":[{"title":"任务管理系统开发","duration":"180分钟"},{"title":"博客平台全栈开发","duration":"240分钟"}]}]', '["/image/course/vue-fullstack-1.jpg","/image/course/vue-fullstack-2.jpg"]', '45小时', 1100, 4.8),
('机器学习实战', '学习机器学习算法，使用 Python 实现实际案例', '本课程深入讲解机器学习核心算法原理和Python实现。从监督学习、无监督学习到强化学习，系统掌握机器学习全流程。通过多个真实项目案例，帮助学员具备独立解决实际机器学习问题的能力。\n\n课程内容：\n1. 机器学习基础与数学原理\n2. 监督学习算法（回归、分类）\n3. 无监督学习（聚类、降维）\n4. 模型评估与优化\n5. 深度学习入门\n\n项目实战：\n- 房价预测系统\n- 图像分类项目\n- 推荐系统开发', '高级', '人工智能', '/image/course/machine-learning.jpg', '刘老师', '刘老师是机器学习领域专家，拥有博士学位和10年机器学习研究经验。曾在多家AI公司担任算法专家，精通各类机器学习算法和深度学习技术。教学风格严谨，注重理论与实践结合，已培养大量机器学习工程师。', 499.00, '人工智能,阅读写作,独立学习,动手实践,学术深造', '[{"title":"第一章：机器学习基础","lessons":[{"title":"机器学习概述","duration":"40分钟"},{"title":"Python科学计算库","duration":"60分钟"},{"title":"数据预处理技术","duration":"70分钟"},{"title":"特征工程","duration":"80分钟"}]},{"title":"第二章：监督学习","lessons":[{"title":"线性回归与逻辑回归","duration":"90分钟"},{"title":"决策树与随机森林","duration":"100分钟"},{"title":"支持向量机","duration":"80分钟"},{"title":"神经网络基础","duration":"100分钟"}]},{"title":"第三章：无监督学习","lessons":[{"title":"K-means聚类","duration":"70分钟"},{"title":"层次聚类","duration":"60分钟"},{"title":"PCA降维","duration":"80分钟"},{"title":"异常检测","duration":"70分钟"}]},{"title":"第四章：模型优化","lessons":[{"title":"交叉验证","duration":"60分钟"},{"title":"超参数调优","duration":"80分钟"},{"title":"模型集成方法","duration":"90分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"房价预测系统","duration":"180分钟"},{"title":"图像分类项目","duration":"200分钟"}]}]', '["/image/course/machine-learning-1.jpg","/image/course/machine-learning-2.jpg","/image/course/machine-learning-3.jpg"]', '60小时', 1500, 4.9),
('UI/UX 设计基础', '掌握用户界面和用户体验设计原则', '本课程系统介绍UI/UX设计的核心理论和实践方法。从用户研究、信息架构到视觉设计，全面掌握现代产品设计流程。通过多个设计项目实战，帮助学员建立专业的设计思维和技能。\n\n课程特色：\n1. 设计思维与方法论\n2. 用户研究与需求分析\n3. 交互设计与原型制作\n4. 视觉设计规范与实践\n\n项目实战：\n- 移动App界面设计\n- 网站用户体验优化\n- 产品原型设计', '初级', '设计创意', '/image/course/ui-ux.jpg', '孙老师', '孙老师是资深UI/UX设计师，拥有12年产品设计经验。曾在多家知名互联网公司担任设计总监，擅长用户研究、交互设计和视觉设计。教学风格生动有趣，注重实战，已帮助众多学员成功转型UI/UX设计。', 159.00, '设计创意,视觉学习,动手实践,社交学习,技能拓展', '[{"title":"第一章：设计基础","lessons":[{"title":"UI/UX设计概述","duration":"40分钟"},{"title":"设计原则与规范","duration":"60分钟"},{"title":"色彩理论与应用","duration":"70分钟"},{"title":"排版设计基础","duration":"60分钟"}]},{"title":"第二章：用户研究","lessons":[{"title":"用户画像构建","duration":"60分钟"},{"title":"用户旅程地图","duration":"70分钟"},{"title":"需求分析方法","duration":"60分钟"}]},{"title":"第三章：信息架构","lessons":[{"title":"信息架构设计","duration":"70分钟"},{"title":"导航系统设计","duration":"60分钟"},{"title":"内容组织策略","duration":"50分钟"}]},{"title":"第四章：交互设计","lessons":[{"title":"交互设计原则","duration":"70分钟"},{"title":"原型设计工具使用","duration":"80分钟"},{"title":"可用性测试","duration":"60分钟"}]},{"title":"第五章：视觉设计","lessons":[{"title":"界面视觉设计","duration":"90分钟"},{"title":"设计系统构建","duration":"100分钟"},{"title":"移动端设计规范","duration":"80分钟"}]}]', '["/image/course/ui-ux-1.jpg","/image/course/ui-ux-2.jpg"]', '35小时', 920, 4.6),
('产品经理实战', '学习产品规划、需求分析和项目管理', '本课程系统讲解产品经理的核心工作方法和技能。从市场分析、需求挖掘到产品规划、项目管理，全面掌握产品经理工作全流程。通过真实案例和项目实战，帮助学员快速成长为优秀的产品经理。\n\n课程内容：\n1. 产品思维与方法论\n2. 市场分析与竞品研究\n3. 需求挖掘与用户研究\n4. 产品规划与Roadmap\n5. 项目管理与团队协作\n\n实战项目：\n- 产品需求文档（PRD）撰写\n- 产品Roadmap规划\n- 项目管理实践', '中级', '商业管理', '/image/course/product-manager.jpg', '周老师', '周老师是资深产品经理，拥有15年产品管理经验。曾在多家知名互联网公司担任产品总监，主导过多个成功产品的从0到1。擅长产品战略规划、需求分析和团队管理，已培养大量优秀产品经理。', 259.00, '商业管理,社交学习,阅读写作,视觉学习,职业提升', '[{"title":"第一章：产品思维","lessons":[{"title":"产品经理角色定位","duration":"40分钟"},{"title":"产品思维培养","duration":"60分钟"},{"title":"商业模式分析","duration":"70分钟"}]},{"title":"第二章：市场分析","lessons":[{"title":"市场调研方法","duration":"60分钟"},{"title":"竞品分析技巧","duration":"70分钟"},{"title":"用户画像构建","duration":"60分钟"}]},{"title":"第三章：需求管理","lessons":[{"title":"需求挖掘方法","duration":"70分钟"},{"title":"需求优先级排序","duration":"60分钟"},{"title":"需求文档撰写","duration":"80分钟"}]},{"title":"第四章：产品规划","lessons":[{"title":"产品Roadmap规划","duration":"90分钟"},{"title":"功能设计方法","duration":"80分钟"},{"title":"MVP设计策略","duration":"70分钟"}]},{"title":"第五章：项目管理","lessons":[{"title":"敏捷开发方法","duration":"80分钟"},{"title":"团队协作技巧","duration":"70分钟"},{"title":"数据分析与迭代","duration":"90分钟"}]}]', '["/image/course/product-manager-1.jpg","/image/course/product-manager-2.jpg"]', '40小时', 1050, 4.7),
('React 前端框架', '深入学习 React 框架，构建高性能单页应用', '本课程深入讲解React框架的核心概念和高级特性。从组件开发、状态管理到性能优化，全面掌握React开发技能。通过多个真实项目实战，帮助学员构建企业级React应用。\n\n课程特色：\n1. React Hooks深入讲解\n2. 状态管理最佳实践\n3. 性能优化技巧\n4. 企业级项目架构\n\n项目实战：\n- 待办事项应用\n- 电商购物车系统\n- 社交网络应用', '中级', '前端开发', '/image/course/react.jpg', '吴老师', '吴老师是React技术专家，拥有10年前端开发经验。精通React、Redux、Next.js等技术栈，曾参与多个大型React项目的开发。教学风格清晰易懂，注重实战，已帮助大量学员掌握React开发技能。', 299.00, '前端学习,视觉学习,动手实践,独立学习,技能拓展', '[{"title":"第一章：React基础","lessons":[{"title":"React简介与核心概念","duration":"50分钟"},{"title":"JSX语法详解","duration":"60分钟"},{"title":"组件开发基础","duration":"70分钟"},{"title":"Props与State","duration":"60分钟"}]},{"title":"第二章：React Hooks","lessons":[{"title":"useState与useEffect","duration":"80分钟"},{"title":"自定义Hooks","duration":"70分钟"},{"title":"useContext与useReducer","duration":"80分钟"},{"title":"性能优化Hooks","duration":"70分钟"}]},{"title":"第三章：状态管理","lessons":[{"title":"Redux基础","duration":"90分钟"},{"title":"Redux Toolkit使用","duration":"80分钟"},{"title":"状态管理最佳实践","duration":"70分钟"}]},{"title":"第四章：路由与数据获取","lessons":[{"title":"React Router使用","duration":"70分钟"},{"title":"数据获取与缓存","duration":"80分钟"},{"title":"错误处理机制","duration":"60分钟"}]},{"title":"第五章：项目实战","lessons":[{"title":"待办事项应用开发","duration":"180分钟"},{"title":"电商购物车系统","duration":"240分钟"}]}]', '["/image/course/react-1.jpg","/image/course/react-2.jpg"]', '45小时', 1300, 4.8),
('Node.js 后端开发', '使用 Node.js 构建服务器端应用和 API', '本课程系统讲解Node.js后端开发技术。从基础语法、异步编程到框架使用、数据库集成，全面掌握Node.js服务端开发技能。通过多个真实项目实战，帮助学员构建高性能的Node.js应用。\n\n课程内容：\n1. Node.js核心模块与API\n2. Express框架开发\n3. 异步编程与事件循环\n4. 数据库集成（MongoDB/MySQL）\n5. RESTful API设计与实现\n\n项目实战：\n- 博客系统后端开发\n- 实时聊天应用\n- 电商API服务', '中级', '后端开发', '/image/course/nodejs.jpg', '郑老师', '郑老师是Node.js技术专家，拥有12年后端开发经验。精通Node.js、Express、MongoDB等技术栈，曾主导多个大型Node.js项目的架构设计。教学风格深入浅出，注重实战，已培养大量Node.js开发工程师。', 349.00, '编程开发,动手实践,视觉学习,独立学习,职业提升', '[{"title":"第一章：Node.js基础","lessons":[{"title":"Node.js环境搭建","duration":"40分钟"},{"title":"核心模块使用","duration":"70分钟"},{"title":"异步编程基础","duration":"80分钟"},{"title":"事件循环机制","duration":"70分钟"}]},{"title":"第二章：Express框架","lessons":[{"title":"Express基础使用","duration":"60分钟"},{"title":"路由与中间件","duration":"80分钟"},{"title":"请求处理与响应","duration":"70分钟"},{"title":"错误处理机制","duration":"60分钟"}]},{"title":"第三章：数据库集成","lessons":[{"title":"MongoDB操作","duration":"90分钟"},{"title":"Mongoose使用","duration":"80分钟"},{"title":"MySQL集成","duration":"70分钟"},{"title":"数据库设计最佳实践","duration":"60分钟"}]},{"title":"第四章：API开发","lessons":[{"title":"RESTful API设计","duration":"80分钟"},{"title":"身份认证与授权","duration":"90分钟"},{"title":"API文档编写","duration":"60分钟"}]},{"title":"第五章：项目实战","lessons":[{"title":"博客系统后端开发","duration":"200分钟"},{"title":"实时聊天应用","duration":"240分钟"}]}]', '["/image/course/nodejs-1.jpg","/image/course/nodejs-2.jpg"]', '50小时', 1150, 4.7),
('MySQL 数据库设计', '学习数据库设计原理和 SQL 优化技巧', '本课程系统讲解MySQL数据库设计原理和SQL优化技巧。从数据库设计规范、SQL语句编写到性能优化、索引设计，全面掌握MySQL数据库开发技能。通过多个实战项目，帮助学员成为优秀的数据库开发工程师。\n\n课程内容：\n1. 数据库设计原理与范式\n2. SQL语句编写与优化\n3. 索引设计与优化\n4. 事务与锁机制\n5. 性能调优技巧\n\n项目实战：\n- 电商数据库设计\n- 数据分析查询优化\n- 高并发场景优化', '初级', '数据科学', '/image/course/mysql.jpg', '钱老师', '钱老师是数据库技术专家，拥有13年数据库开发和管理经验。精通MySQL、PostgreSQL等数据库系统，曾主导多个大型数据库项目的设计与优化。教学风格严谨，注重实战，已培养大量数据库开发工程师。', 199.00, '数据科学,阅读写作,独立学习,视觉学习,职业提升', '[{"title":"第一章：数据库基础","lessons":[{"title":"MySQL安装与配置","duration":"40分钟"},{"title":"数据库设计原理","duration":"70分钟"},{"title":"数据类型与约束","duration":"60分钟"}]},{"title":"第二章：SQL语句","lessons":[{"title":"DDL数据定义语言","duration":"60分钟"},{"title":"DML数据操作语言","duration":"80分钟"},{"title":"DQL数据查询语言","duration":"100分钟"},{"title":"子查询与连接","duration":"90分钟"}]},{"title":"第三章：索引与优化","lessons":[{"title":"索引原理与设计","duration":"80分钟"},{"title":"SQL性能优化","duration":"90分钟"},{"title":"执行计划分析","duration":"70分钟"}]},{"title":"第四章：高级特性","lessons":[{"title":"事务与ACID","duration":"70分钟"},{"title":"锁机制","duration":"80分钟"},{"title":"存储过程与函数","duration":"90分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"电商数据库设计","duration":"180分钟"},{"title":"查询优化实践","duration":"150分钟"}]}]', '["/image/course/mysql-1.jpg","/image/course/mysql-2.jpg"]', '35小时', 980, 4.6),
('深度学习入门', '从零开始学习深度学习理论和实践', '本课程系统讲解深度学习的核心理论和实践方法。从神经网络基础、CNN、RNN到Transformer架构，全面掌握深度学习技术。通过多个实战项目，帮助学员具备独立开发深度学习应用的能力。\n\n课程特色：\n1. 理论基础扎实，循序渐进\n2. 实战项目丰富，学以致用\n3. 涵盖主流深度学习框架\n4. 从入门到进阶的系统化学习\n\n项目实战：\n- 图像分类项目\n- 文本情感分析\n- 生成对抗网络应用', '高级', '人工智能', '/image/course/deep-learning.jpg', '周老师', '周老师是深度学习领域专家，拥有博士学位和12年深度学习研究经验。曾在多家AI公司担任算法专家，精通TensorFlow、PyTorch等框架。教学风格深入浅出，注重理论与实践结合，已培养大量深度学习工程师。', 599.00, '人工智能,阅读写作,独立学习,视觉学习,学术深造', '[{"title":"第一章：神经网络基础","lessons":[{"title":"感知机与多层感知机","duration":"70分钟"},{"title":"反向传播算法","duration":"90分钟"},{"title":"激活函数","duration":"60分钟"},{"title":"损失函数与优化器","duration":"80分钟"}]},{"title":"第二章：卷积神经网络","lessons":[{"title":"CNN原理与结构","duration":"90分钟"},{"title":"经典CNN架构","duration":"100分钟"},{"title":"图像分类实战","duration":"120分钟"}]},{"title":"第三章：循环神经网络","lessons":[{"title":"RNN与LSTM","duration":"100分钟"},{"title":"GRU网络","duration":"80分钟"},{"title":"序列建模应用","duration":"90分钟"}]},{"title":"第四章：Transformer架构","lessons":[{"title":"注意力机制","duration":"90分钟"},{"title":"Transformer原理","duration":"100分钟"},{"title":"BERT与GPT模型","duration":"110分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"图像分类项目","duration":"240分钟"},{"title":"文本情感分析","duration":"200分钟"}]}]', '["/image/course/deep-learning-1.jpg","/image/course/deep-learning-2.jpg","/image/course/deep-learning-3.jpg"]', '70小时', 1800, 4.9),
('Photoshop 设计技巧', '掌握 Photoshop 图像处理和设计技巧', '本课程系统讲解Photoshop图像处理和设计技巧。从基础操作、图层管理到高级合成、特效制作，全面掌握Photoshop设计技能。通过多个设计项目实战，帮助学员成为专业的设计师。\n\n课程内容：\n1. Photoshop基础操作\n2. 图层与蒙版技术\n3. 调色与色彩管理\n4. 图像合成技巧\n5. 特效制作方法\n\n项目实战：\n- 海报设计制作\n- 产品精修\n- 创意合成作品', '初级', '设计创意', '/image/course/photoshop.jpg', '孙老师', '孙老师是资深平面设计师，拥有15年设计经验。精通Photoshop、Illustrator等设计软件，曾为多家知名品牌提供设计服务。教学风格生动有趣，注重实战，已帮助众多学员掌握专业设计技能。', 199.00, '设计创意,视觉学习,动手实践,兴趣爱好', '[{"title":"第一章：Photoshop基础","lessons":[{"title":"界面认识与工具使用","duration":"50分钟"},{"title":"图层与选区操作","duration":"70分钟"},{"title":"色彩模式与调整","duration":"60分钟"}]},{"title":"第二章：图像处理","lessons":[{"title":"图像修复技术","duration":"80分钟"},{"title":"调色技巧","duration":"90分钟"},{"title":"滤镜使用","duration":"70分钟"}]},{"title":"第三章：图层技术","lessons":[{"title":"图层样式应用","duration":"80分钟"},{"title":"蒙版技术","duration":"90分钟"},{"title":"混合模式使用","duration":"70分钟"}]},{"title":"第四章：图像合成","lessons":[{"title":"抠图技巧","duration":"100分钟"},{"title":"图像合成方法","duration":"120分钟"},{"title":"特效制作","duration":"100分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"海报设计制作","duration":"180分钟"},{"title":"产品精修","duration":"150分钟"}]}]', '["/image/course/photoshop-1.jpg","/image/course/photoshop-2.jpg"]', '30小时', 1100, 4.7),
('项目管理实战', '学习项目管理的核心方法和工具', '本课程系统讲解项目管理的核心方法和工具使用。从项目启动、规划到执行、监控，全面掌握项目管理全流程。通过真实项目案例，帮助学员提升项目管理能力。\n\n课程内容：\n1. 项目管理基础理论\n2. 项目规划与WBS分解\n3. 进度管理与资源分配\n4. 风险管理与质量控制\n5. 团队协作与沟通技巧\n\n实战项目：\n- 项目计划制定\n- 进度跟踪管理\n- 团队协作实践', '中级', '商业管理', '/image/course/project-management.jpg', '李老师', '李老师是资深项目管理专家，拥有PMP认证和18年项目管理经验。曾在多家大型企业担任项目经理和项目总监，主导过多个成功项目。教学风格务实，注重实战，已培养大量优秀项目经理。', 299.00, '商业管理,社交学习,阅读写作,动手实践,职业提升', '[{"title":"第一章：项目管理基础","lessons":[{"title":"项目管理概述","duration":"40分钟"},{"title":"项目生命周期","duration":"50分钟"},{"title":"项目管理知识体系","duration":"60分钟"}]},{"title":"第二章：项目规划","lessons":[{"title":"项目范围管理","duration":"70分钟"},{"title":"WBS工作分解","duration":"80分钟"},{"title":"项目时间管理","duration":"90分钟"},{"title":"资源规划","duration":"70分钟"}]},{"title":"第三章：项目执行","lessons":[{"title":"团队建设与管理","duration":"80分钟"},{"title":"沟通管理","duration":"70分钟"},{"title":"质量管理","duration":"60分钟"}]},{"title":"第四章：项目监控","lessons":[{"title":"进度跟踪方法","duration":"70分钟"},{"title":"成本控制","duration":"80分钟"},{"title":"风险管理","duration":"90分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"项目计划制定","duration":"180分钟"},{"title":"项目执行与监控","duration":"200分钟"}]}]', '["/image/course/project-management-1.jpg","/image/course/project-management-2.jpg"]', '40小时', 1080, 4.7),
('Excel 数据分析', '使用 Excel 进行数据分析和可视化', '本课程系统讲解使用Excel进行数据分析和可视化的技巧。从基础函数、数据透视表到高级图表、VBA编程，全面掌握Excel数据分析技能。通过多个实战案例，帮助学员提升数据分析能力。\n\n课程内容：\n1. Excel基础函数与公式\n2. 数据透视表与透视图\n3. 高级图表制作\n4. 数据分析工具使用\n5. VBA基础编程\n\n实战案例：\n- 销售数据分析\n- 财务报表制作\n- 数据可视化仪表盘', '初级', '职业技能', '/image/course/excel.jpg', '王老师', '王老师是Excel数据分析专家，拥有10年数据分析经验。精通Excel高级功能、数据分析和可视化，曾为多家企业提供数据分析培训。教学风格清晰易懂，注重实战，已帮助大量学员提升Excel技能。', 99.00, '职业技能,动手实践,视觉学习,独立学习,职业提升', '[{"title":"第一章：Excel基础","lessons":[{"title":"Excel界面与操作","duration":"40分钟"},{"title":"常用函数使用","duration":"80分钟"},{"title":"公式编写技巧","duration":"70分钟"}]},{"title":"第二章：数据处理","lessons":[{"title":"数据排序与筛选","duration":"60分钟"},{"title":"数据验证","duration":"50分钟"},{"title":"条件格式","duration":"60分钟"}]},{"title":"第三章：数据分析","lessons":[{"title":"数据透视表","duration":"90分钟"},{"title":"数据分析工具","duration":"80分钟"},{"title":"高级函数应用","duration":"100分钟"}]},{"title":"第四章：数据可视化","lessons":[{"title":"图表制作技巧","duration":"90分钟"},{"title":"动态图表制作","duration":"100分钟"},{"title":"仪表盘设计","duration":"120分钟"}]},{"title":"第五章：实战案例","lessons":[{"title":"销售数据分析","duration":"150分钟"},{"title":"财务报表制作","duration":"180分钟"}]}]', '["/image/course/excel-1.jpg","/image/course/excel-2.jpg"]', '25小时', 750, 4.5),
('Python 爬虫开发', '学习使用 Python 进行网络爬虫开发', '本课程系统讲解使用Python进行网络爬虫开发的技术和方法。从HTTP请求、HTML解析到数据存储、反爬虫应对，全面掌握爬虫开发技能。通过多个实战项目，帮助学员具备独立开发爬虫应用的能力。\n\n课程内容：\n1. HTTP协议与请求库使用\n2. HTML解析与数据提取\n3. 数据存储与管理\n4. 反爬虫策略与应对\n5. 爬虫框架使用\n\n项目实战：\n- 新闻网站爬虫\n- 电商数据采集\n- 图片批量下载', '中级', '编程开发', '/image/course/python-spider.jpg', '张老师', '张老师是Python爬虫技术专家，拥有10年爬虫开发经验。精通各种爬虫框架和反爬虫技术，曾开发多个大型爬虫项目。教学风格注重实战，已帮助大量学员掌握爬虫开发技能。', 249.00, '编程开发,动手实践,独立学习,视觉学习,技能拓展', '[{"title":"第一章：爬虫基础","lessons":[{"title":"HTTP协议基础","duration":"50分钟"},{"title":"Requests库使用","duration":"70分钟"},{"title":"BeautifulSoup解析","duration":"80分钟"}]},{"title":"第二章：数据提取","lessons":[{"title":"XPath与CSS选择器","duration":"90分钟"},{"title":"正则表达式","duration":"80分钟"},{"title":"数据清洗","duration":"70分钟"}]},{"title":"第三章：数据存储","lessons":[{"title":"文件存储","duration":"60分钟"},{"title":"数据库存储","duration":"80分钟"},{"title":"数据去重","duration":"60分钟"}]},{"title":"第四章：高级技术","lessons":[{"title":"Scrapy框架","duration":"100分钟"},{"title":"反爬虫应对","duration":"90分钟"},{"title":"分布式爬虫","duration":"100分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"新闻网站爬虫","duration":"180分钟"},{"title":"电商数据采集","duration":"200分钟"}]}]', '["/image/course/python-spider-1.jpg","/image/course/python-spider-2.jpg"]', '35小时', 950, 4.6),
('TypeScript 进阶', '深入学习 TypeScript 类型系统和高级特性', '本课程深入讲解TypeScript类型系统和高级特性。从基础类型、泛型到高级类型、装饰器，全面掌握TypeScript开发技能。通过多个实战项目，帮助学员构建类型安全的JavaScript应用。\n\n课程特色：\n1. 类型系统深入理解\n2. 泛型编程技巧\n3. 高级类型应用\n4. 工程化实践\n\n项目实战：\n- 类型安全的工具库\n- React + TypeScript项目\n- Node.js + TypeScript应用', '中级', '前端开发', '/image/course/typescript.jpg', '陈老师', '陈老师是TypeScript技术专家，拥有9年前端开发经验。精通TypeScript类型系统和大型前端项目架构，曾主导多个TypeScript项目的开发。教学风格清晰，注重类型安全，已帮助大量学员掌握TypeScript技能。', 299.00, '前端学习,阅读写作,独立学习,视觉学习,技能拓展', '[{"title":"第一章：TypeScript基础","lessons":[{"title":"TypeScript环境搭建","duration":"40分钟"},{"title":"基础类型系统","duration":"70分钟"},{"title":"接口与类型别名","duration":"80分钟"}]},{"title":"第二章：函数与类","lessons":[{"title":"函数类型","duration":"70分钟"},{"title":"类与继承","duration":"80分钟"},{"title":"装饰器","duration":"90分钟"}]},{"title":"第三章：泛型编程","lessons":[{"title":"泛型基础","duration":"80分钟"},{"title":"泛型约束","duration":"70分钟"},{"title":"条件类型","duration":"90分钟"}]},{"title":"第四章：高级类型","lessons":[{"title":"联合类型与交叉类型","duration":"80分钟"},{"title":"映射类型","duration":"90分钟"},{"title":"工具类型","duration":"100分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"类型安全的工具库","duration":"200分钟"},{"title":"React + TypeScript项目","duration":"240分钟"}]}]', '["/image/course/typescript-1.jpg","/image/course/typescript-2.jpg"]', '40小时', 1100, 4.7),
('Docker 容器技术', '学习 Docker 容器化部署和管理', '本课程系统讲解Docker容器化技术。从Docker基础、镜像构建到容器编排、CI/CD集成，全面掌握Docker使用技能。通过多个实战项目，帮助学员具备容器化部署能力。\n\n课程内容：\n1. Docker基础与核心概念\n2. 镜像构建与管理\n3. 容器运行与管理\n4. Docker Compose编排\n5. CI/CD集成实践\n\n项目实战：\n- Web应用容器化\n- 微服务容器编排\n- 持续集成部署', '中级', '职业技能', '/image/course/docker.jpg', '赵老师', '赵老师是容器技术专家，拥有11年DevOps经验。精通Docker、Kubernetes等容器技术，曾主导多个大型项目的容器化改造。教学风格务实，注重实战，已培养大量DevOps工程师。', 349.00, '职业技能,动手实践,独立学习,视觉学习,职业提升', '[{"title":"第一章：Docker基础","lessons":[{"title":"Docker简介与安装","duration":"40分钟"},{"title":"镜像与容器","duration":"70分钟"},{"title":"Dockerfile编写","duration":"90分钟"}]},{"title":"第二章：镜像管理","lessons":[{"title":"镜像构建","duration":"80分钟"},{"title":"镜像优化","duration":"70分钟"},{"title":"镜像仓库使用","duration":"60分钟"}]},{"title":"第三章：容器管理","lessons":[{"title":"容器运行","duration":"70分钟"},{"title":"数据卷管理","duration":"80分钟"},{"title":"网络配置","duration":"70分钟"}]},{"title":"第四章：Docker Compose","lessons":[{"title":"Compose基础","duration":"80分钟"},{"title":"多容器编排","duration":"100分钟"},{"title":"服务依赖管理","duration":"70分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"Web应用容器化","duration":"180分钟"},{"title":"微服务容器编排","duration":"240分钟"}]}]', '["/image/course/docker-1.jpg","/image/course/docker-2.jpg"]', '35小时', 1000, 4.7),
('商业数据分析', '学习商业数据分析和决策支持', '本课程系统讲解商业数据分析的方法和工具。从数据收集、分析到可视化、决策支持，全面掌握商业数据分析技能。通过多个真实案例，帮助学员提升商业决策能力。\n\n课程内容：\n1. 商业数据分析基础\n2. 数据收集与整理\n3. 数据分析方法\n4. 数据可视化\n5. 商业决策支持\n\n实战案例：\n- 销售数据分析\n- 用户行为分析\n- 市场趋势预测', '中级', '商业管理', '/image/course/business-analysis.jpg', '刘老师', '刘老师是商业数据分析专家，拥有14年商业分析经验。曾在多家知名企业担任数据分析总监，擅长商业智能和决策支持。教学风格务实，注重案例，已帮助大量学员提升商业分析能力。', 399.00, '商业管理,数据科学,阅读写作,独立学习,职业提升', '[{"title":"第一章：商业分析基础","lessons":[{"title":"商业分析概述","duration":"40分钟"},{"title":"数据分析流程","duration":"60分钟"},{"title":"业务指标设计","duration":"70分钟"}]},{"title":"第二章：数据收集","lessons":[{"title":"数据来源识别","duration":"60分钟"},{"title":"数据收集方法","duration":"70分钟"},{"title":"数据质量评估","duration":"60分钟"}]},{"title":"第三章：分析方法","lessons":[{"title":"描述性分析","duration":"70分钟"},{"title":"诊断性分析","duration":"80分钟"},{"title":"预测性分析","duration":"90分钟"}]},{"title":"第四章：数据可视化","lessons":[{"title":"图表选择原则","duration":"70分钟"},{"title":"仪表盘设计","duration":"90分钟"},{"title":"报告撰写","duration":"80分钟"}]},{"title":"第五章：实战案例","lessons":[{"title":"销售数据分析","duration":"200分钟"},{"title":"用户行为分析","duration":"180分钟"}]}]', '["/image/course/business-analysis-1.jpg","/image/course/business-analysis-2.jpg"]', '40小时', 1120, 4.7),
('Illustrator 矢量设计', '掌握 Illustrator 矢量图形设计技巧', '本课程系统讲解Illustrator矢量图形设计技巧。从基础工具、路径绘制到高级特效、插画制作，全面掌握Illustrator设计技能。通过多个设计项目实战，帮助学员成为专业的矢量设计师。\n\n课程内容：\n1. Illustrator基础操作\n2. 路径与形状绘制\n3. 颜色与渐变应用\n4. 文字与排版\n5. 插画与图标设计\n\n项目实战：\n- Logo设计制作\n- 矢量插画创作\n- 图标设计', '初级', '设计创意', '/image/course/illustrator.jpg', '孙老师', '孙老师是资深矢量设计师，拥有13年设计经验。精通Illustrator、CorelDRAW等矢量设计软件，曾为多家知名品牌提供设计服务。教学风格生动，注重创意，已帮助众多学员掌握专业设计技能。', 199.00, '设计创意,视觉学习,动手实践,兴趣爱好', '[{"title":"第一章：Illustrator基础","lessons":[{"title":"界面认识与工具","duration":"50分钟"},{"title":"文档创建与管理","duration":"40分钟"},{"title":"基础绘图工具","duration":"70分钟"}]},{"title":"第二章：路径绘制","lessons":[{"title":"钢笔工具使用","duration":"90分钟"},{"title":"路径编辑技巧","duration":"80分钟"},{"title":"形状工具应用","duration":"70分钟"}]},{"title":"第三章：颜色与效果","lessons":[{"title":"颜色填充与描边","duration":"70分钟"},{"title":"渐变与图案","duration":"80分钟"},{"title":"效果与滤镜","duration":"90分钟"}]},{"title":"第四章：文字与排版","lessons":[{"title":"文字工具使用","duration":"70分钟"},{"title":"文字排版技巧","duration":"80分钟"},{"title":"文字特效制作","duration":"70分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"Logo设计制作","duration":"180分钟"},{"title":"矢量插画创作","duration":"200分钟"}]}]', '["/image/course/illustrator-1.jpg","/image/course/illustrator-2.jpg"]', '30小时', 980, 4.6),
('Git 版本控制', '学习 Git 版本控制工具的使用和团队协作', '本课程系统讲解Git版本控制工具的使用和团队协作方法。从基础操作、分支管理到冲突解决、工作流实践，全面掌握Git使用技能。通过多个实战项目，帮助学员提升团队协作效率。\n\n课程内容：\n1. Git基础操作\n2. 分支与合并\n3. 远程仓库管理\n4. 冲突解决\n5. 团队协作工作流\n\n实战项目：\n- 个人项目版本管理\n- 团队协作开发\n- 代码审查流程', '初级', '职业技能', '/image/course/git.jpg', '吴老师', '吴老师是版本控制技术专家，拥有10年开发经验。精通Git、SVN等版本控制工具，曾主导多个大型项目的版本管理。教学风格清晰，注重实践，已帮助大量学员掌握Git技能。', 99.00, '职业技能,动手实践,社交学习,独立学习,职业提升', '[{"title":"第一章：Git基础","lessons":[{"title":"Git简介与安装","duration":"30分钟"},{"title":"仓库初始化","duration":"40分钟"},{"title":"基本操作命令","duration":"60分钟"}]},{"title":"第二章：版本管理","lessons":[{"title":"提交历史查看","duration":"50分钟"},{"title":"版本回退","duration":"60分钟"},{"title":"工作区与暂存区","duration":"70分钟"}]},{"title":"第三章：分支管理","lessons":[{"title":"分支创建与切换","duration":"70分钟"},{"title":"分支合并","duration":"80分钟"},{"title":"分支策略","duration":"70分钟"}]},{"title":"第四章：远程协作","lessons":[{"title":"远程仓库操作","duration":"70分钟"},{"title":"冲突解决","duration":"90分钟"},{"title":"Pull Request流程","duration":"80分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"个人项目管理","duration":"120分钟"},{"title":"团队协作开发","duration":"150分钟"}]}]', '["/image/course/git-1.jpg"]', '20小时', 680, 4.5),
('算法与数据结构', '深入学习常用算法和数据结构', '本课程系统讲解常用算法和数据结构的原理与实现。从基础数据结构、排序算法到图算法、动态规划，全面掌握算法设计技能。通过大量编程练习，帮助学员提升算法思维和编程能力。\n\n课程内容：\n1. 基础数据结构（数组、链表、栈、队列）\n2. 树与图结构\n3. 排序与搜索算法\n4. 动态规划与贪心算法\n5. 算法复杂度分析\n\n实战练习：\n- LeetCode算法题解\n- 算法竞赛训练\n- 实际项目应用', '中级', '编程开发', '/image/course/algorithm.jpg', '郑老师', '郑老师是算法专家，拥有博士学位和12年算法研究经验。曾在多家知名互联网公司担任算法工程师，精通各类算法和数据结构。教学风格严谨，注重思维训练，已帮助大量学员提升算法能力。', 399.00, '编程开发,阅读写作,独立学习,视觉学习,学术深造', '[{"title":"第一章：基础数据结构","lessons":[{"title":"数组与链表","duration":"80分钟"},{"title":"栈与队列","duration":"70分钟"},{"title":"哈希表","duration":"90分钟"}]},{"title":"第二章：树结构","lessons":[{"title":"二叉树","duration":"100分钟"},{"title":"平衡树","duration":"90分钟"},{"title":"堆结构","duration":"80分钟"}]},{"title":"第三章：图算法","lessons":[{"title":"图的表示","duration":"80分钟"},{"title":"深度优先搜索","duration":"90分钟"},{"title":"最短路径算法","duration":"100分钟"}]},{"title":"第四章：高级算法","lessons":[{"title":"动态规划","duration":"120分钟"},{"title":"贪心算法","duration":"100分钟"},{"title":"回溯算法","duration":"110分钟"}]},{"title":"第五章：实战练习","lessons":[{"title":"LeetCode题解","duration":"200分钟"},{"title":"算法竞赛训练","duration":"240分钟"}]}]', '["/image/course/algorithm-1.jpg","/image/course/algorithm-2.jpg"]', '55小时', 1400, 4.8),
('自然语言处理', '学习 NLP 技术和文本分析', '本课程深入讲解自然语言处理技术和文本分析方法。从文本预处理、词向量到BERT、GPT等预训练模型，全面掌握NLP技术。通过多个实战项目，帮助学员具备独立开发NLP应用的能力。\n\n课程内容：\n1. 文本预处理技术\n2. 词向量与词嵌入\n3. 文本分类与情感分析\n4. 序列标注与命名实体识别\n5. 预训练模型应用\n\n项目实战：\n- 文本情感分析系统\n- 智能问答系统\n- 文本生成应用', '高级', '人工智能', '/image/course/nlp.jpg', '钱老师', '钱老师是NLP领域专家，拥有博士学位和13年NLP研究经验。曾在多家AI公司担任NLP算法专家，精通各类NLP技术和预训练模型。教学风格深入浅出，注重实战，已培养大量NLP工程师。', 599.00, '人工智能,阅读写作,独立学习,视觉学习,学术深造', '[{"title":"第一章：NLP基础","lessons":[{"title":"文本预处理","duration":"80分钟"},{"title":"分词技术","duration":"70分钟"},{"title":"词性标注","duration":"80分钟"}]},{"title":"第二章：词向量","lessons":[{"title":"Word2Vec","duration":"100分钟"},{"title":"GloVe","duration":"90分钟"},{"title":"FastText","duration":"80分钟"}]},{"title":"第三章：文本分类","lessons":[{"title":"传统机器学习方法","duration":"90分钟"},{"title":"深度学习模型","duration":"110分钟"},{"title":"情感分析实战","duration":"100分钟"}]},{"title":"第四章：序列模型","lessons":[{"title":"RNN与LSTM","duration":"110分钟"},{"title":"注意力机制","duration":"100分钟"},{"title":"Transformer架构","duration":"120分钟"}]},{"title":"第五章：预训练模型","lessons":[{"title":"BERT模型","duration":"130分钟"},{"title":"GPT模型","duration":"120分钟"},{"title":"模型微调","duration":"110分钟"}]}]', '["/image/course/nlp-1.jpg","/image/course/nlp-2.jpg","/image/course/nlp-3.jpg"]', '65小时', 1600, 4.9),
('Web 安全防护', '学习 Web 应用安全防护和漏洞修复', '本课程系统讲解Web应用安全防护和漏洞修复技术。从常见攻击方式、安全漏洞到防护措施、安全测试，全面掌握Web安全技能。通过多个实战案例，帮助学员提升Web应用安全性。\n\n课程内容：\n1. Web安全基础\n2. 常见攻击方式（SQL注入、XSS、CSRF等）\n3. 安全漏洞检测\n4. 防护措施实施\n5. 安全测试方法\n\n实战案例：\n- 漏洞扫描与修复\n- 安全加固实践\n- 渗透测试演练', '中级', '职业技能', '/image/course/web-security.jpg', '周老师', '周老师是Web安全专家，拥有14年安全研究经验。曾在多家安全公司担任安全工程师，精通各类Web安全技术和漏洞修复。教学风格务实，注重实战，已帮助大量学员提升安全防护能力。', 299.00, '职业技能,阅读写作,独立学习,动手实践,职业提升', '[{"title":"第一章：Web安全基础","lessons":[{"title":"Web安全概述","duration":"50分钟"},{"title":"HTTP协议安全","duration":"70分钟"},{"title":"认证与授权","duration":"80分钟"}]},{"title":"第二章：常见攻击","lessons":[{"title":"SQL注入攻击","duration":"90分钟"},{"title":"XSS跨站脚本","duration":"100分钟"},{"title":"CSRF攻击","duration":"80分钟"},{"title":"文件上传漏洞","duration":"90分钟"}]},{"title":"第三章：安全防护","lessons":[{"title":"输入验证","duration":"80分钟"},{"title":"输出编码","duration":"70分钟"},{"title":"安全配置","duration":"80分钟"}]},{"title":"第四章：安全测试","lessons":[{"title":"漏洞扫描","duration":"90分钟"},{"title":"渗透测试","duration":"100分钟"},{"title":"安全审计","duration":"80分钟"}]},{"title":"第五章：实战案例","lessons":[{"title":"漏洞修复实践","duration":"200分钟"},{"title":"安全加固项目","duration":"180分钟"}]}]', '["/image/course/web-security-1.jpg","/image/course/web-security-2.jpg"]', '40小时', 1050, 4.7),
('产品设计思维', '学习产品设计的思维方法和实践', '本课程系统讲解产品设计的思维方法和实践技巧。从用户研究、需求分析到原型设计、测试验证，全面掌握产品设计流程。通过多个设计项目实战，帮助学员建立专业的产品设计思维。\n\n课程内容：\n1. 设计思维方法论\n2. 用户研究与需求分析\n3. 信息架构设计\n4. 原型设计与测试\n5. 设计迭代优化\n\n项目实战：\n- 移动App设计\n- Web产品设计\n- 设计系统构建', '中级', '设计创意', '/image/course/product-design.jpg', '李老师', '李老师是资深产品设计师，拥有15年产品设计经验。曾在多家知名互联网公司担任设计总监，擅长产品设计思维和用户体验设计。教学风格生动，注重思维培养，已帮助众多学员建立设计思维。', 349.00, '设计创意,视觉学习,社交学习,阅读写作,技能拓展', '[{"title":"第一章：设计思维","lessons":[{"title":"设计思维概述","duration":"50分钟"},{"title":"设计流程","duration":"70分钟"},{"title":"设计原则","duration":"60分钟"}]},{"title":"第二章：用户研究","lessons":[{"title":"用户画像","duration":"80分钟"},{"title":"用户旅程","duration":"90分钟"},{"title":"需求分析","duration":"80分钟"}]},{"title":"第三章：信息架构","lessons":[{"title":"信息架构设计","duration":"90分钟"},{"title":"导航系统","duration":"80分钟"},{"title":"内容组织","duration":"70分钟"}]},{"title":"第四章：原型设计","lessons":[{"title":"线框图设计","duration":"90分钟"},{"title":"交互原型","duration":"100分钟"},{"title":"可用性测试","duration":"80分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"移动App设计","duration":"240分钟"},{"title":"Web产品设计","duration":"200分钟"}]}]', '["/image/course/product-design-1.jpg","/image/course/product-design-2.jpg"]', '42小时', 1150, 4.7),
('Angular 框架开发', '学习 Angular 框架构建大型前端应用', '本课程系统讲解Angular框架开发技术。从组件开发、依赖注入到路由管理、状态管理，全面掌握Angular开发技能。通过多个大型项目实战，帮助学员构建企业级Angular应用。\n\n课程内容：\n1. Angular基础与架构\n2. 组件与模块开发\n3. 服务与依赖注入\n4. 路由与导航\n5. 状态管理与HTTP\n\n项目实战：\n- 企业管理系统\n- 电商平台开发\n- 数据可视化应用', '中级', '前端开发', '/image/course/angular.jpg', '吴老师', '吴老师是Angular技术专家，拥有11年前端开发经验。精通Angular、RxJS等技术栈，曾参与多个大型Angular项目的开发。教学风格清晰，注重架构设计，已帮助大量学员掌握Angular开发技能。', 399.00, '前端学习,视觉学习,动手实践,独立学习,技能拓展', '[{"title":"第一章：Angular基础","lessons":[{"title":"Angular架构概述","duration":"60分钟"},{"title":"组件开发基础","duration":"90分钟"},{"title":"模板语法","duration":"80分钟"}]},{"title":"第二章：组件系统","lessons":[{"title":"组件通信","duration":"90分钟"},{"title":"生命周期钩子","duration":"80分钟"},{"title":"指令使用","duration":"70分钟"}]},{"title":"第三章：服务与依赖注入","lessons":[{"title":"服务创建","duration":"80分钟"},{"title":"依赖注入机制","duration":"90分钟"},{"title":"HTTP服务","duration":"100分钟"}]},{"title":"第四章：路由与状态","lessons":[{"title":"路由配置","duration":"90分钟"},{"title":"路由守卫","duration":"80分钟"},{"title":"状态管理","duration":"100分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"企业管理系统","duration":"300分钟"},{"title":"电商平台开发","duration":"360分钟"}]}]', '["/image/course/angular-1.jpg","/image/course/angular-2.jpg"]', '50小时', 1250, 4.8),
('Redis 缓存技术', '学习 Redis 缓存的使用和优化', '本课程系统讲解Redis缓存技术的使用和优化方法。从基础操作、数据结构到集群部署、性能优化，全面掌握Redis使用技能。通过多个实战项目，帮助学员提升系统性能。\n\n课程内容：\n1. Redis基础与安装配置\n2. 数据结构与命令使用\n3. 持久化机制\n4. 集群与高可用\n5. 性能优化技巧\n\n项目实战：\n- 缓存系统设计\n- 分布式锁实现\n- 消息队列应用', '中级', '后端开发', '/image/course/redis.jpg', '郑老师', '郑老师是Redis技术专家，拥有12年后端开发经验。精通Redis、Memcached等缓存技术，曾主导多个大型缓存系统的设计与优化。教学风格务实，注重性能优化，已帮助大量学员掌握Redis技能。', 299.00, '编程开发,动手实践,独立学习,视觉学习,职业提升', '[{"title":"第一章：Redis基础","lessons":[{"title":"Redis简介与安装","duration":"40分钟"},{"title":"基础命令使用","duration":"70分钟"},{"title":"数据类型","duration":"80分钟"}]},{"title":"第二章：数据结构","lessons":[{"title":"String与Hash","duration":"70分钟"},{"title":"List与Set","duration":"80分钟"},{"title":"Sorted Set","duration":"90分钟"}]},{"title":"第三章：高级特性","lessons":[{"title":"事务处理","duration":"70分钟"},{"title":"发布订阅","duration":"80分钟"},{"title":"Lua脚本","duration":"90分钟"}]},{"title":"第四章：集群与优化","lessons":[{"title":"主从复制","duration":"90分钟"},{"title":"集群部署","duration":"100分钟"},{"title":"性能优化","duration":"90分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"缓存系统设计","duration":"200分钟"},{"title":"分布式锁实现","duration":"180分钟"}]}]', '["/image/course/redis-1.jpg","/image/course/redis-2.jpg"]', '35小时', 1020, 4.7),
('MongoDB 数据库', '学习 NoSQL 数据库 MongoDB 的使用', '本课程系统讲解MongoDB NoSQL数据库的使用方法。从基础操作、数据建模到索引优化、聚合查询，全面掌握MongoDB开发技能。通过多个实战项目，帮助学员构建高性能的MongoDB应用。\n\n课程内容：\n1. MongoDB基础与安装\n2. 文档操作与查询\n3. 数据建模设计\n4. 索引与性能优化\n5. 聚合管道与复制集\n\n项目实战：\n- 内容管理系统\n- 日志分析系统\n- 实时数据应用', '中级', '数据科学', '/image/course/mongodb.jpg', '钱老师', '钱老师是MongoDB技术专家，拥有11年数据库开发经验。精通MongoDB、Elasticsearch等NoSQL数据库，曾主导多个大型MongoDB项目的设计。教学风格清晰，注重实战，已帮助大量学员掌握MongoDB技能。', 349.00, '数据科学,阅读写作,独立学习,动手实践,职业提升', '[{"title":"第一章：MongoDB基础","lessons":[{"title":"MongoDB简介与安装","duration":"40分钟"},{"title":"数据库与集合","duration":"60分钟"},{"title":"文档操作","duration":"80分钟"}]},{"title":"第二章：查询操作","lessons":[{"title":"基础查询","duration":"80分钟"},{"title":"高级查询","duration":"90分钟"},{"title":"索引使用","duration":"80分钟"}]},{"title":"第三章：数据建模","lessons":[{"title":"文档设计","duration":"90分钟"},{"title":"关系建模","duration":"80分钟"},{"title":"模式设计最佳实践","duration":"90分钟"}]},{"title":"第四章：聚合与优化","lessons":[{"title":"聚合管道","duration":"100分钟"},{"title":"性能优化","duration":"90分钟"},{"title":"复制集配置","duration":"100分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"内容管理系统","duration":"240分钟"},{"title":"日志分析系统","duration":"200分钟"}]}]', '["/image/course/mongodb-1.jpg","/image/course/mongodb-2.jpg"]', '38小时', 1080, 4.7),
('计算机视觉', '学习图像识别和计算机视觉技术', '本课程深入讲解计算机视觉技术和图像识别方法。从图像处理、特征提取到深度学习模型、目标检测，全面掌握计算机视觉技术。通过多个实战项目，帮助学员具备独立开发视觉应用的能力。\n\n课程内容：\n1. 图像处理基础\n2. 特征提取与描述\n3. 目标检测与识别\n4. 深度学习模型应用\n5. 实际项目开发\n\n项目实战：\n- 人脸识别系统\n- 物体检测应用\n- 图像分类项目', '高级', '人工智能', '/image/course/computer-vision.jpg', '周老师', '周老师是计算机视觉专家，拥有博士学位和14年视觉研究经验。曾在多家AI公司担任视觉算法专家，精通OpenCV、深度学习等技术。教学风格深入浅出，注重实战，已培养大量计算机视觉工程师。', 599.00, '人工智能,视觉学习,独立学习,动手实践,学术深造', '[{"title":"第一章：图像处理基础","lessons":[{"title":"图像基础与格式","duration":"60分钟"},{"title":"图像变换","duration":"80分钟"},{"title":"滤波与增强","duration":"90分钟"}]},{"title":"第二章：特征提取","lessons":[{"title":"边缘检测","duration":"80分钟"},{"title":"角点检测","duration":"90分钟"},{"title":"特征描述符","duration":"100分钟"}]},{"title":"第三章：目标检测","lessons":[{"title":"传统检测方法","duration":"90分钟"},{"title":"深度学习检测","duration":"120分钟"},{"title":"YOLO与R-CNN","duration":"130分钟"}]},{"title":"第四章：深度学习应用","lessons":[{"title":"CNN架构","duration":"110分钟"},{"title":"迁移学习","duration":"100分钟"},{"title":"模型优化","duration":"110分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"人脸识别系统","duration":"300分钟"},{"title":"物体检测应用","duration":"280分钟"}]}]', '["/image/course/computer-vision-1.jpg","/image/course/computer-vision-2.jpg","/image/course/computer-vision-3.jpg"]', '68小时', 1700, 4.9),
('Figma 设计工具', '掌握 Figma 进行 UI/UX 设计', '本课程系统讲解使用Figma进行UI/UX设计的技巧和方法。从基础操作、组件设计到协作功能、原型制作，全面掌握Figma设计技能。通过多个设计项目实战，帮助学员成为专业的UI/UX设计师。\n\n课程内容：\n1. Figma基础操作\n2. 组件与样式系统\n3. 布局与约束\n4. 交互原型制作\n5. 团队协作功能\n\n项目实战：\n- 移动App界面设计\n- Web界面设计\n- 设计系统构建', '初级', '设计创意', '/image/course/figma.jpg', '孙老师', '孙老师是资深UI/UX设计师，拥有12年设计经验。精通Figma、Sketch等设计工具，曾为多家知名公司提供设计服务。教学风格生动，注重实战，已帮助众多学员掌握Figma设计技能。', 199.00, '设计创意,视觉学习,动手实践,社交学习,技能拓展', '[{"title":"第一章：Figma基础","lessons":[{"title":"界面认识与工具","duration":"50分钟"},{"title":"基础绘图工具","duration":"70分钟"},{"title":"图层管理","duration":"60分钟"}]},{"title":"第二章：组件设计","lessons":[{"title":"组件创建","duration":"80分钟"},{"title":"组件变体","duration":"90分钟"},{"title":"样式系统","duration":"80分钟"}]},{"title":"第三章：布局设计","lessons":[{"title":"Auto Layout","duration":"100分钟"},{"title":"约束与响应式","duration":"90分钟"},{"title":"网格系统","duration":"80分钟"}]},{"title":"第四章：交互原型","lessons":[{"title":"原型连接","duration":"90分钟"},{"title":"交互设计","duration":"100分钟"},{"title":"动画效果","duration":"80分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"移动App设计","duration":"240分钟"},{"title":"Web界面设计","duration":"200分钟"}]}]', '["/image/course/figma-1.jpg","/image/course/figma-2.jpg"]', '32小时', 960, 4.6),
('市场营销策略', '学习数字营销和品牌推广策略', '本课程系统讲解数字营销和品牌推广策略。从市场分析、用户洞察到营销渠道、效果评估，全面掌握现代营销技能。通过多个真实案例，帮助学员提升营销能力。\n\n课程内容：\n1. 营销基础理论\n2. 市场分析与用户研究\n3. 数字营销渠道\n4. 内容营销策略\n5. 营销效果评估\n\n实战案例：\n- 品牌推广方案\n- 社交媒体营销\n- 营销数据分析', '中级', '商业管理', '/image/course/marketing.jpg', '李老师', '李老师是营销策略专家，拥有16年营销经验。曾在多家知名企业担任营销总监，擅长数字营销和品牌推广。教学风格务实，注重案例，已帮助大量学员提升营销能力。', 299.00, '商业管理,社交学习,阅读写作,视觉学习,职业提升', '[{"title":"第一章：营销基础","lessons":[{"title":"营销理论概述","duration":"50分钟"},{"title":"市场分析","duration":"70分钟"},{"title":"用户洞察","duration":"80分钟"}]},{"title":"第二章：数字营销","lessons":[{"title":"SEO与SEM","duration":"90分钟"},{"title":"社交媒体营销","duration":"100分钟"},{"title":"内容营销","duration":"90分钟"}]},{"title":"第三章：品牌策略","lessons":[{"title":"品牌定位","duration":"80分钟"},{"title":"品牌传播","duration":"90分钟"},{"title":"品牌管理","duration":"80分钟"}]},{"title":"第四章：效果评估","lessons":[{"title":"营销指标设计","duration":"80分钟"},{"title":"数据分析方法","duration":"90分钟"},{"title":"ROI评估","duration":"80分钟"}]},{"title":"第五章：实战案例","lessons":[{"title":"品牌推广方案","duration":"200分钟"},{"title":"社交媒体营销","duration":"180分钟"}]}]', '["/image/course/marketing-1.jpg","/image/course/marketing-2.jpg"]', '38小时', 1100, 4.7),
('Linux 系统管理', '学习 Linux 系统管理和运维', '本课程系统讲解Linux系统管理和运维技术。从基础命令、系统配置到服务管理、性能优化，全面掌握Linux运维技能。通过多个实战项目，帮助学员成为优秀的Linux系统管理员。\n\n课程内容：\n1. Linux基础命令与文件系统\n2. 用户与权限管理\n3. 进程与服务管理\n4. 网络配置与防火墙\n5. 系统监控与性能优化\n\n项目实战：\n- 服务器配置管理\n- 自动化运维脚本\n- 系统故障排查', '中级', '职业技能', '/image/course/linux.jpg', '王老师', '王老师是Linux系统专家，拥有15年系统运维经验。精通Linux系统管理、Shell脚本和自动化运维，曾管理多个大型服务器集群。教学风格务实，注重实战，已培养大量Linux运维工程师。', 299.00, '职业技能,动手实践,独立学习,阅读写作,职业提升', '[{"title":"第一章：Linux基础","lessons":[{"title":"Linux系统概述","duration":"40分钟"},{"title":"文件系统操作","duration":"80分钟"},{"title":"基础命令使用","duration":"100分钟"}]},{"title":"第二章：系统管理","lessons":[{"title":"用户与权限","duration":"90分钟"},{"title":"进程管理","duration":"80分钟"},{"title":"服务管理","duration":"90分钟"}]},{"title":"第三章：网络配置","lessons":[{"title":"网络配置","duration":"80分钟"},{"title":"防火墙配置","duration":"90分钟"},{"title":"远程管理","duration":"70分钟"}]},{"title":"第四章：Shell脚本","lessons":[{"title":"Shell基础","duration":"80分钟"},{"title":"脚本编写","duration":"100分钟"},{"title":"自动化运维","duration":"90分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"服务器配置","duration":"200分钟"},{"title":"自动化脚本","duration":"180分钟"}]}]', '["/image/course/linux-1.jpg","/image/course/linux-2.jpg"]', '38小时', 1050, 4.7),
('C++ 编程进阶', '深入学习 C++ 高级特性和性能优化', '本课程深入讲解C++高级特性和性能优化技术。从模板编程、STL使用到内存管理、并发编程，全面掌握C++高级开发技能。通过多个高性能项目实战，帮助学员成为C++开发专家。\n\n课程内容：\n1. 模板与泛型编程\n2. STL容器与算法\n3. 内存管理与智能指针\n4. 并发编程与多线程\n5. 性能优化技巧\n\n项目实战：\n- 高性能计算程序\n- 并发服务器开发\n- 游戏引擎核心模块', '高级', '编程开发', '/image/course/cpp.jpg', '张老师', '张老师是C++技术专家，拥有16年C++开发经验。精通C++高级特性、性能优化和系统编程，曾主导多个高性能C++项目的开发。教学风格严谨，注重性能，已培养大量C++高级工程师。', 499.00, '编程开发,阅读写作,独立学习,动手实践,学术深造', '[{"title":"第一章：模板编程","lessons":[{"title":"函数模板","duration":"80分钟"},{"title":"类模板","duration":"100分钟"},{"title":"模板特化","duration":"90分钟"}]},{"title":"第二章：STL深入","lessons":[{"title":"容器详解","duration":"120分钟"},{"title":"迭代器","duration":"100分钟"},{"title":"算法库","duration":"110分钟"}]},{"title":"第三章：内存管理","lessons":[{"title":"内存分配","duration":"100分钟"},{"title":"智能指针","duration":"110分钟"},{"title":"内存优化","duration":"100分钟"}]},{"title":"第四章：并发编程","lessons":[{"title":"多线程基础","duration":"110分钟"},{"title":"线程同步","duration":"120分钟"},{"title":"异步编程","duration":"110分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"高性能计算程序","duration":"300分钟"},{"title":"并发服务器开发","duration":"360分钟"}]}]', '["/image/course/cpp-1.jpg","/image/course/cpp-2.jpg"]', '60小时', 1450, 4.8),
('微服务架构', '学习微服务架构设计和实现', '本课程深入讲解微服务架构设计和实现技术。从服务拆分、服务治理到分布式事务、监控体系，全面掌握微服务架构技能。通过多个大型项目实战，帮助学员构建企业级微服务系统。\n\n课程内容：\n1. 微服务架构基础\n2. 服务拆分与设计\n3. 服务注册与发现\n4. 配置中心与网关\n5. 分布式事务与监控\n\n项目实战：\n- 电商微服务系统\n- 分布式系统设计\n- 服务治理实践', '高级', '后端开发', '/image/course/microservices.jpg', '赵老师', '赵老师是微服务架构专家，拥有17年分布式系统开发经验。精通微服务架构、分布式系统和云原生技术，曾主导多个大型微服务项目的架构设计。教学风格深入，注重架构思维，已培养大量架构师。', 599.00, '编程开发,阅读写作,独立学习,视觉学习,职业提升', '[{"title":"第一章：微服务基础","lessons":[{"title":"微服务概述","duration":"60分钟"},{"title":"服务拆分原则","duration":"90分钟"},{"title":"API设计","duration":"80分钟"}]},{"title":"第二章：服务治理","lessons":[{"title":"服务注册与发现","duration":"100分钟"},{"title":"负载均衡","duration":"90分钟"},{"title":"服务网关","duration":"100分钟"}]},{"title":"第三章：配置与监控","lessons":[{"title":"配置中心","duration":"90分钟"},{"title":"服务监控","duration":"100分钟"},{"title":"链路追踪","duration":"90分钟"}]},{"title":"第四章：分布式事务","lessons":[{"title":"事务理论","duration":"100分钟"},{"title":"分布式事务方案","duration":"120分钟"},{"title":"最终一致性","duration":"110分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"电商微服务系统","duration":"400分钟"},{"title":"分布式系统设计","duration":"360分钟"}]}]', '["/image/course/microservices-1.jpg","/image/course/microservices-2.jpg","/image/course/microservices-3.jpg"]', '65小时', 1650, 4.9),
('数据挖掘技术', '学习数据挖掘算法和实际应用', '本课程深入讲解数据挖掘算法和实际应用技术。从数据预处理、特征工程到分类、聚类、关联规则挖掘，全面掌握数据挖掘技能。通过多个真实项目，帮助学员具备独立进行数据挖掘的能力。\n\n课程内容：\n1. 数据挖掘基础\n2. 数据预处理技术\n3. 分类算法\n4. 聚类算法\n5. 关联规则挖掘\n\n项目实战：\n- 客户分群分析\n- 推荐系统开发\n- 异常检测应用', '高级', '数据科学', '/image/course/data-mining.jpg', '王老师', '王老师是数据挖掘专家，拥有博士学位和15年数据挖掘研究经验。精通各类数据挖掘算法和机器学习技术，曾主导多个大型数据挖掘项目。教学风格严谨，注重算法理解，已培养大量数据挖掘工程师。', 599.00, '数据科学,阅读写作,独立学习,动手实践,学术深造', '[{"title":"第一章：数据挖掘基础","lessons":[{"title":"数据挖掘概述","duration":"60分钟"},{"title":"数据预处理","duration":"100分钟"},{"title":"特征工程","duration":"110分钟"}]},{"title":"第二章：分类算法","lessons":[{"title":"决策树","duration":"100分钟"},{"title":"朴素贝叶斯","duration":"90分钟"},{"title":"支持向量机","duration":"110分钟"}]},{"title":"第三章：聚类算法","lessons":[{"title":"K-means聚类","duration":"100分钟"},{"title":"层次聚类","duration":"90分钟"},{"title":"DBSCAN","duration":"100分钟"}]},{"title":"第四章：关联规则","lessons":[{"title":"Apriori算法","duration":"110分钟"},{"title":"FP-Growth","duration":"100分钟"},{"title":"关联规则应用","duration":"90分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"客户分群分析","duration":"300分钟"},{"title":"推荐系统开发","duration":"360分钟"}]}]', '["/image/course/data-mining-1.jpg","/image/course/data-mining-2.jpg"]', '62小时', 1580, 4.8),
('强化学习', '学习强化学习算法和应用', '本课程深入讲解强化学习算法和应用技术。从马尔可夫决策过程、价值函数到深度强化学习、策略梯度，全面掌握强化学习技术。通过多个实战项目，帮助学员具备独立开发强化学习应用的能力。\n\n课程内容：\n1. 强化学习基础\n2. 价值函数方法\n3. 策略梯度方法\n4. 深度强化学习\n5. 实际应用开发\n\n项目实战：\n- 游戏AI开发\n- 机器人控制\n- 智能推荐系统', '高级', '人工智能', '/image/course/reinforcement-learning.jpg', '刘老师', '刘老师是强化学习专家，拥有博士学位和16年强化学习研究经验。精通各类强化学习算法和深度强化学习技术，曾在多家AI公司担任算法专家。教学风格深入，注重理论结合实践，已培养大量强化学习工程师。', 699.00, '人工智能,阅读写作,独立学习,视觉学习,学术深造', '[{"title":"第一章：强化学习基础","lessons":[{"title":"强化学习概述","duration":"70分钟"},{"title":"马尔可夫决策过程","duration":"110分钟"},{"title":"贝尔曼方程","duration":"100分钟"}]},{"title":"第二章：价值函数方法","lessons":[{"title":"动态规划","duration":"110分钟"},{"title":"蒙特卡洛方法","duration":"120分钟"},{"title":"时序差分学习","duration":"130分钟"}]},{"title":"第三章：策略梯度方法","lessons":[{"title":"策略梯度理论","duration":"120分钟"},{"title":"Actor-Critic","duration":"130分钟"},{"title":"PPO算法","duration":"140分钟"}]},{"title":"第四章：深度强化学习","lessons":[{"title":"DQN算法","duration":"130分钟"},{"title":"A3C算法","duration":"120分钟"},{"title":"DDPG算法","duration":"140分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"游戏AI开发","duration":"400分钟"},{"title":"机器人控制","duration":"360分钟"}]}]', '["/image/course/reinforcement-learning-1.jpg","/image/course/reinforcement-learning-2.jpg","/image/course/reinforcement-learning-3.jpg"]', '75小时', 1900, 4.9),
('Sketch 设计工具', '掌握 Sketch 进行移动端 UI 设计', '本课程系统讲解使用Sketch进行移动端UI设计的技巧和方法。从基础操作、组件设计到响应式布局、原型制作，全面掌握Sketch设计技能。通过多个移动端项目实战，帮助学员成为专业的移动UI设计师。\n\n课程内容：\n1. Sketch基础操作\n2. 组件与符号系统\n3. 响应式设计\n4. 原型与交互\n5. 设计规范与导出\n\n项目实战：\n- iOS App界面设计\n- Android App界面设计\n- 移动端设计系统', '初级', '设计创意', '/image/course/sketch.jpg', '孙老师', '孙老师是资深移动UI设计师，拥有12年设计经验。精通Sketch、Figma等设计工具，曾为多家知名公司设计移动应用。教学风格生动，注重实战，已帮助众多学员掌握移动端设计技能。', 199.00, '设计创意,视觉学习,动手实践,兴趣爱好', '[{"title":"第一章：Sketch基础","lessons":[{"title":"界面认识","duration":"40分钟"},{"title":"基础工具使用","duration":"70分钟"},{"title":"图层管理","duration":"60分钟"}]},{"title":"第二章：组件设计","lessons":[{"title":"组件创建","duration":"80分钟"},{"title":"符号系统","duration":"90分钟"},{"title":"样式管理","duration":"70分钟"}]},{"title":"第三章：移动端设计","lessons":[{"title":"iOS设计规范","duration":"90分钟"},{"title":"Android设计规范","duration":"90分钟"},{"title":"响应式设计","duration":"80分钟"}]},{"title":"第四章：原型制作","lessons":[{"title":"交互原型","duration":"100分钟"},{"title":"动效设计","duration":"90分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"iOS App设计","duration":"240分钟"},{"title":"Android App设计","duration":"200分钟"}]}]', '["/image/course/sketch-1.jpg","/image/course/sketch-2.jpg"]', '30小时', 900, 4.6),
('财务管理基础', '学习企业财务管理和分析', '本课程系统讲解企业财务管理和分析的基础知识。从财务报表、成本管理到投资决策、风险控制，全面掌握财务管理技能。通过多个真实案例，帮助学员提升财务管理能力。\n\n课程内容：\n1. 财务管理基础\n2. 财务报表分析\n3. 成本管理\n4. 投资决策\n5. 风险控制\n\n实战案例：\n- 财务报表分析\n- 投资项目评估\n- 财务风险控制', '中级', '商业管理', '/image/course/finance.jpg', '周老师', '周老师是财务管理专家，拥有15年财务管理经验。曾在多家大型企业担任财务总监，精通财务分析和投资决策。教学风格务实，注重案例，已帮助大量学员提升财务管理能力。', 299.00, '商业管理,阅读写作,独立学习,社交学习,职业提升', '[{"title":"第一章：财务管理基础","lessons":[{"title":"财务管理概述","duration":"50分钟"},{"title":"财务目标","duration":"60分钟"},{"title":"财务环境","duration":"50分钟"}]},{"title":"第二章：财务报表","lessons":[{"title":"资产负债表","duration":"80分钟"},{"title":"利润表","duration":"80分钟"},{"title":"现金流量表","duration":"90分钟"},{"title":"财务比率分析","duration":"100分钟"}]},{"title":"第三章：成本管理","lessons":[{"title":"成本核算","duration":"90分钟"},{"title":"成本控制","duration":"80分钟"},{"title":"预算管理","duration":"90分钟"}]},{"title":"第四章：投资决策","lessons":[{"title":"投资分析方法","duration":"100分钟"},{"title":"资本预算","duration":"90分钟"},{"title":"风险评估","duration":"80分钟"}]},{"title":"第五章：实战案例","lessons":[{"title":"财务报表分析","duration":"200分钟"},{"title":"投资项目评估","duration":"180分钟"}]}]', '["/image/course/finance-1.jpg","/image/course/finance-2.jpg"]', '40小时', 1080, 4.7),
('Kubernetes 容器编排', '学习 Kubernetes 容器编排和管理', '本课程深入讲解Kubernetes容器编排和管理技术。从集群部署、Pod管理到服务发现、自动扩缩容，全面掌握Kubernetes使用技能。通过多个大型项目实战，帮助学员构建企业级容器化平台。\n\n课程内容：\n1. Kubernetes架构与核心概念\n2. Pod与容器管理\n3. Service与Ingress\n4. 配置与存储管理\n5. 监控与日志\n\n项目实战：\n- 微服务容器编排\n- 高可用集群部署\n- CI/CD集成', '高级', '职业技能', '/image/course/kubernetes.jpg', '赵老师', '赵老师是Kubernetes技术专家，拥有13年容器技术经验。精通Kubernetes、Docker等容器技术，曾主导多个大型K8s集群的部署与管理。教学风格深入，注重实战，已培养大量Kubernetes工程师。', 599.00, '职业技能,动手实践,独立学习,阅读写作,职业提升', '[{"title":"第一章：Kubernetes基础","lessons":[{"title":"K8s架构概述","duration":"70分钟"},{"title":"集群部署","duration":"120分钟"},{"title":"核心概念","duration":"90分钟"}]},{"title":"第二章：Pod管理","lessons":[{"title":"Pod创建与管理","duration":"100分钟"},{"title":"Deployment","duration":"110分钟"},{"title":"StatefulSet","duration":"100分钟"}]},{"title":"第三章：服务管理","lessons":[{"title":"Service","duration":"100分钟"},{"title":"Ingress","duration":"110分钟"},{"title":"服务发现","duration":"90分钟"}]},{"title":"第四章：高级特性","lessons":[{"title":"配置管理","duration":"100分钟"},{"title":"存储管理","duration":"110分钟"},{"title":"自动扩缩容","duration":"120分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"微服务容器编排","duration":"400分钟"},{"title":"高可用集群部署","duration":"360分钟"}]}]', '["/image/course/kubernetes-1.jpg","/image/course/kubernetes-2.jpg","/image/course/kubernetes-3.jpg"]', '70小时', 1750, 4.9),
('Swift iOS 开发', '学习 Swift 语言和 iOS 应用开发', '本课程系统讲解Swift语言和iOS应用开发技术。从Swift语法、UIKit到SwiftUI、Core Data，全面掌握iOS开发技能。通过多个iOS项目实战，帮助学员构建专业的iOS应用。\n\n课程内容：\n1. Swift语言基础\n2. UIKit界面开发\n3. SwiftUI现代UI框架\n4. 数据持久化\n5. 网络请求与API集成\n\n项目实战：\n- 待办事项App\n- 天气应用\n- 社交应用开发', '中级', '编程开发', '/image/course/swift.jpg', '李老师', '李老师是iOS开发专家，拥有11年iOS开发经验。精通Swift、Objective-C和iOS开发技术，曾开发多个成功上架的iOS应用。教学风格清晰，注重实战，已帮助大量学员掌握iOS开发技能。', 449.00, '编程开发,动手实践,视觉学习,独立学习,技能拓展', '[{"title":"第一章：Swift基础","lessons":[{"title":"Swift语法","duration":"80分钟"},{"title":"面向对象编程","duration":"100分钟"},{"title":"闭包与协议","duration":"90分钟"}]},{"title":"第二章：UIKit开发","lessons":[{"title":"界面构建","duration":"100分钟"},{"title":"视图控制器","duration":"110分钟"},{"title":"导航与转场","duration":"90分钟"}]},{"title":"第三章：SwiftUI","lessons":[{"title":"SwiftUI基础","duration":"100分钟"},{"title":"状态管理","duration":"110分钟"},{"title":"数据绑定","duration":"100分钟"}]},{"title":"第四章：数据与网络","lessons":[{"title":"Core Data","duration":"110分钟"},{"title":"网络请求","duration":"100分钟"},{"title":"JSON解析","duration":"90分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"待办事项App","duration":"300分钟"},{"title":"天气应用","duration":"280分钟"}]}]', '["/image/course/swift-1.jpg","/image/course/swift-2.jpg"]', '50小时', 1280, 4.8),
('Kotlin Android 开发', '使用 Kotlin 开发 Android 应用', '本课程系统讲解使用Kotlin开发Android应用的技术。从Kotlin语法、Android基础到Jetpack组件、MVVM架构，全面掌握Android开发技能。通过多个Android项目实战，帮助学员构建专业的Android应用。\n\n课程内容：\n1. Kotlin语言基础\n2. Android基础组件\n3. Jetpack组件库\n4. MVVM架构\n5. 数据持久化与网络\n\n项目实战：\n- 新闻阅读App\n- 音乐播放器\n- 电商应用开发', '中级', '编程开发', '/image/course/kotlin.jpg', '王老师', '王老师是Android开发专家，拥有12年Android开发经验。精通Kotlin、Java和Android开发技术，曾开发多个成功上架的Android应用。教学风格清晰，注重架构设计，已帮助大量学员掌握Android开发技能。', 449.00, '编程开发,动手实践,视觉学习,独立学习,技能拓展', '[{"title":"第一章：Kotlin基础","lessons":[{"title":"Kotlin语法","duration":"80分钟"},{"title":"面向对象","duration":"100分钟"},{"title":"扩展函数","duration":"90分钟"}]},{"title":"第二章：Android基础","lessons":[{"title":"Activity与Fragment","duration":"110分钟"},{"title":"布局系统","duration":"100分钟"},{"title":"生命周期","duration":"90分钟"}]},{"title":"第三章：Jetpack组件","lessons":[{"title":"ViewModel","duration":"100分钟"},{"title":"LiveData","duration":"90分钟"},{"title":"Room数据库","duration":"110分钟"}]},{"title":"第四章：架构设计","lessons":[{"title":"MVVM架构","duration":"120分钟"},{"title":"依赖注入","duration":"100分钟"},{"title":"网络请求","duration":"110分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"新闻阅读App","duration":"360分钟"},{"title":"音乐播放器","duration":"320分钟"}]}]', '["/image/course/kotlin-1.jpg","/image/course/kotlin-2.jpg"]', '52小时', 1320, 4.8),
('Flutter 跨平台开发', '使用 Flutter 开发跨平台移动应用', '本课程系统讲解使用Flutter开发跨平台移动应用的技术。从Dart语言、Widget系统到状态管理、平台交互，全面掌握Flutter开发技能。通过多个跨平台项目实战，帮助学员构建iOS和Android应用。\n\n课程内容：\n1. Dart语言基础\n2. Flutter Widget系统\n3. 状态管理（Provider/Bloc）\n4. 路由与导航\n5. 平台交互与插件\n\n项目实战：\n- 待办事项App\n- 电商应用\n- 社交应用开发', '中级', '编程开发', '/image/course/flutter.jpg', '张老师', '张老师是Flutter开发专家，拥有10年移动开发经验。精通Flutter、Dart和跨平台开发技术，曾开发多个成功的跨平台应用。教学风格清晰，注重实战，已帮助大量学员掌握Flutter开发技能。', 399.00, '编程开发,前端学习,动手实践,视觉学习,技能拓展', '[{"title":"第一章：Flutter基础","lessons":[{"title":"Dart语言","duration":"80分钟"},{"title":"Flutter环境搭建","duration":"60分钟"},{"title":"第一个Flutter应用","duration":"70分钟"}]},{"title":"第二章：Widget系统","lessons":[{"title":"基础Widget","duration":"100分钟"},{"title":"布局Widget","duration":"110分钟"},{"title":"自定义Widget","duration":"100分钟"}]},{"title":"第三章：状态管理","lessons":[{"title":"StatefulWidget","duration":"90分钟"},{"title":"Provider状态管理","duration":"110分钟"},{"title":"Bloc模式","duration":"120分钟"}]},{"title":"第四章：高级特性","lessons":[{"title":"路由导航","duration":"100分钟"},{"title":"平台交互","duration":"110分钟"},{"title":"插件使用","duration":"100分钟"}]},{"title":"第五章：实战项目","lessons":[{"title":"待办事项App","duration":"300分钟"},{"title":"电商应用","duration":"360分钟"}]}]', '["/image/course/flutter-1.jpg","/image/course/flutter-2.jpg"]', '48小时', 1200, 4.7),
('Rust 系统编程', '学习 Rust 语言进行系统级编程', '本课程系统讲解Rust 系统编程的核心技术和开发实践。从基础语法、核心概念到高级特性、项目实战，全面掌握Rust 系统编程开发技能。通过多个真实项目案例，帮助学员具备独立开发应用的能力。
课程特色：
1. 系统化学习路径，从入门到精通
2. 丰富的实战项目，学以致用
3. 深入讲解核心原理和最佳实践
4. 专业的讲师团队指导
学完本课程后，你将能够：
- 掌握Rust 系统编程核心技能
- 能够独立完成项目开发
- 理解最佳实践和设计模式
- 具备解决实际问题的能力', '高级', '编程开发', '/image/course/rust.jpg', '陈老师', '陈老师是资深技术开发工程师，拥有15年开发经验。曾在多家知名企业担任高级工程师，擅长Rust 系统编程。教学风格生动有趣，已培养大量技术开发工程师。',  499.00, '编程开发,阅读写作,独立学习,动手实践,学术深造', '[{"title": "第一章：理论基础", "lessons": [{"title": "Rust 系统编程概述", "duration": "60分钟"}, {"title": "核心原理", "duration": "88分钟"}, {"title": "架构设计", "duration": "93分钟"}]}, {"title": "第二章：核心技术", "lessons": [{"title": "核心算法", "duration": "106分钟"}, {"title": "实现方法", "duration": "110分钟"}, {"title": "优化技巧", "duration": "104分钟"}]}, {"title": "第三章：高级应用", "lessons": [{"title": "高级特性", "duration": "118分钟"}, {"title": "性能优化", "duration": "120分钟"}, {"title": "架构设计", "duration": "118分钟"}]}, {"title": "第四章：实战项目", "lessons": [{"title": "Rust 系统编程项目开发", "duration": "362分钟"}, {"title": "Rust 系统编程项目开发", "duration": "314分钟"}]}]', '["/image/course/rust-系统编程-1.jpg","/image/course/rust-系统编程-2.jpg"]', '51小时', 872, 4.8),
('Scala 函数式编程', '学习 Scala 函数式编程范式', '本课程系统讲解Scala 函数式编程的核心技术和开发实践。从基础语法、核心概念到高级特性、项目实战，全面掌握Scala 函数式编程开发技能。通过多个真实项目案例，帮助学员具备独立开发应用的能力。
课程特色：
1. 系统化学习路径，从入门到精通
2. 丰富的实战项目，学以致用
3. 深入讲解核心原理和最佳实践
4. 专业的讲师团队指导
学完本课程后，你将能够：
- 掌握Scala 函数式编程核心技能
- 能够独立完成项目开发
- 理解最佳实践和设计模式
- 具备解决实际问题的能力', '高级', '编程开发', '/image/course/scala.jpg', '刘老师', '刘老师是技术开发技术专家，拥有15年实战经验。精通Scala 函数式编程等技术栈，曾主导多个大型项目的架构设计。教学风格生动有趣，注重技能提升，已帮助大量学员提升技能。',  449.00, '编程开发,阅读写作,独立学习,视觉学习,学术深造', '[{"title": "第一章：理论基础", "lessons": [{"title": "Scala 函数式编程概述", "duration": "62分钟"}, {"title": "核心原理", "duration": "87分钟"}, {"title": "架构设计", "duration": "96分钟"}]}, {"title": "第二章：核心技术", "lessons": [{"title": "核心算法", "duration": "107分钟"}, {"title": "实现方法", "duration": "111分钟"}, {"title": "优化技巧", "duration": "108分钟"}]}, {"title": "第三章：高级应用", "lessons": [{"title": "高级特性", "duration": "110分钟"}, {"title": "性能优化", "duration": "124分钟"}, {"title": "架构设计", "duration": "120分钟"}]}, {"title": "第四章：实战项目", "lessons": [{"title": "Scala 函数式编程项目开发", "duration": "329分钟"}, {"title": "Scala 函数式编程项目开发", "duration": "302分钟"}]}]', '["/image/course/scala-函数式编程-1.jpg","/image/course/scala-函数式编程-2.jpg"]', '68小时', 918, 4.7);

-- 插入测试用户数据（10个普通用户 + 1个管理员）
INSERT INTO user (username, password, phone, email, avatar_url, role, gender, learning_preference, course_interest, learning_goal) VALUES
('testuser1', 'test123', '13800138001', 'test1@example.com', '/image/avatar/testuser1.jpg', 'USER', '男', '视觉学习,动手实践', '编程开发,前端学习,数据科学', '职业提升'),
('testuser2', 'test123', '13800138002', 'test2@example.com', '/image/avatar/testuser2.jpg', 'USER', '女', '阅读写作,独立学习', '设计创意,商业管理,人工智能', '技能拓展'),
('testuser3', 'test123', '13800138003', 'test3@example.com', '/image/avatar/testuser3.jpg', 'USER', '男', '听觉学习,社交学习', '数据科学,人工智能,职业技能', '学术深造'),
('testuser4', 'test123', '13800138004', 'test4@example.com', '/image/avatar/testuser4.jpg', 'USER', '女', '视觉学习,阅读写作', '前端学习,设计创意,商业管理', '职业提升'),
('testuser5', 'test123', '13800138005', 'test5@example.com', '/image/avatar/testuser5.jpg', 'USER', '男', '动手实践,独立学习', '编程开发,数据科学,职业技能', '技能拓展'),
('testuser6', 'test123', '13800138006', 'test6@example.com', '/image/avatar/testuser6.jpg', 'USER', '女', '听觉学习,社交学习', '人工智能,设计创意,商业管理', '兴趣爱好'),
('testuser7', 'test123', '13800138007', 'test7@example.com', '/image/avatar/testuser7.jpg', 'USER', '男', '视觉学习,动手实践,社交学习', '编程开发,前端学习,人工智能', '职业提升'),
('testuser8', 'test123', '13800138008', 'test8@example.com', '/image/avatar/testuser8.jpg', 'USER', '女', '阅读写作,独立学习', '数据科学,设计创意,职业技能', '学术深造'),
('testuser9', 'test123', '13800138009', 'test9@example.com', '/image/avatar/testuser9.jpg', 'USER', '男', '视觉学习,听觉学习', '前端学习,人工智能,商业管理', '技能拓展'),
('testuser10', 'test123', '13800138010', 'test10@example.com', '/image/avatar/testuser10.jpg', 'USER', '女', '动手实践,阅读写作', '编程开发,数据科学,设计创意', '职业提升'),
('admin', 'admin123', '13800138000', 'admin@learning-platform.com', '/image/avatar/admin.jpg', 'ADMIN', '男', '视觉学习,动手实践,独立学习', '编程开发,数据科学,人工智能,职业技能', '职业提升');

-- testuser1 (id=1): Java基础入门, 前端开发实战, Python数据分析, React前端框架
INSERT INTO user_course (user_id, course_id) VALUES
(1, 1), (1, 2), (1, 3), (1, 9);
-- testuser2 (id=2): UI/UX设计基础, 产品经理实战, 机器学习实战, Photoshop设计技巧
INSERT INTO user_course (user_id, course_id) VALUES
(2, 7), (2, 8), (2, 6), (2, 13);
-- testuser3 (id=3): Python数据分析, 机器学习实战, 深度学习入门, 自然语言处理
INSERT INTO user_course (user_id, course_id) VALUES
(3, 3), (3, 6), (3, 12), (3, 22);
-- testuser4 (id=4): 前端开发实战, Vue.js全栈开发, React前端框架, TypeScript进阶
INSERT INTO user_course (user_id, course_id) VALUES
(4, 2), (4, 5), (4, 9), (4, 17), (4, 13);
-- testuser5 (id=5): Java基础入门, Spring Boot企业级开发, Python爬虫开发, 算法与数据结构
INSERT INTO user_course (user_id, course_id) VALUES
(5, 1), (5, 4), (5, 16), (5, 23);
-- testuser6 (id=6): 机器学习实战, UI/UX设计基础, 产品设计思维, 市场营销策略
INSERT INTO user_course (user_id, course_id) VALUES
(6, 6), (6, 7), (6, 26), (6, 32);
-- testuser7 (id=7): Java基础入门, 前端开发实战, 机器学习实战, React前端框架, Node.js后端开发
INSERT INTO user_course (user_id, course_id) VALUES
(7, 1), (7, 2), (7, 6), (7, 9), (7, 10);
-- testuser8 (id=8): Python数据分析, MySQL数据库设计, MongoDB数据库, 数据挖掘技术
INSERT INTO user_course (user_id, course_id) VALUES
(8, 3), (8, 11), (8, 29), (8, 36);
-- testuser9 (id=9): 前端开发实战, Vue.js全栈开发, 机器学习实战, 产品经理实战
INSERT INTO user_course (user_id, course_id) VALUES
(9, 2), (9, 5), (9, 6), (9, 8);
-- testuser10 (id=10): Java基础入门, Python数据分析, UI/UX设计基础, Photoshop设计技巧
INSERT INTO user_course (user_id, course_id) VALUES
(10, 1), (10, 3), (10, 7), (10, 13);

-- ============================================
-- 学习社区相关表
-- ============================================

-- 帖子表
DROP TABLE IF EXISTS post;
CREATE TABLE post (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '发布者ID',
    title VARCHAR(200) NOT NULL COMMENT '帖子标题',
    content TEXT NOT NULL COMMENT '帖子内容',
    type VARCHAR(20) NOT NULL DEFAULT 'discussion' COMMENT '帖子类型：discussion(讨论), question(问答), experience(经验分享)',
    category VARCHAR(50) COMMENT '分类：编程开发、前端开发、后端开发、数据科学、设计创意、商业管理、职业技能、人工智能',
    course_id BIGINT COMMENT '关联课程ID（可选）',
    tags VARCHAR(500) COMMENT '标签，多个用逗号分隔',
    attachment_url VARCHAR(500) COMMENT '附件下载地址（可选）',
    view_count INT DEFAULT 0 COMMENT '浏览量',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    comment_count INT DEFAULT 0 COMMENT '评论数',
    favorite_count INT DEFAULT 0 COMMENT '收藏数',
    status VARCHAR(20) DEFAULT 'published' COMMENT '状态：published(已发布), deleted(已删除)',
    visibility VARCHAR(20) DEFAULT 'PUBLIC' COMMENT '可见性：PUBLIC(公开), PRIVATE(私密)',
    is_top TINYINT(1) DEFAULT 0 COMMENT '是否置顶',
    is_resolved TINYINT(1) DEFAULT 0 COMMENT '是否已解决（仅问答类型）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_category (category),
    KEY idx_course_id (course_id),
    KEY idx_type (type),
    KEY idx_create_time (create_time)
) COMMENT '帖子表';

-- 评论表
DROP TABLE IF EXISTS comment;
CREATE TABLE comment (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '评论者ID',
    parent_id BIGINT DEFAULT NULL COMMENT '父评论ID（用于回复）',
    content TEXT NOT NULL COMMENT '评论内容',
    like_count INT DEFAULT 0 COMMENT '点赞数',
    is_best_answer TINYINT(1) DEFAULT 0 COMMENT '是否最佳回答（仅问答类型）',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    KEY idx_post_id (post_id),
    KEY idx_user_id (user_id),
    KEY idx_parent_id (parent_id)
) COMMENT '评论表';

-- 点赞表
DROP TABLE IF EXISTS post_like;
CREATE TABLE post_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_post_user (post_id, user_id),
    KEY idx_post_id (post_id),
    KEY idx_user_id (user_id)
) COMMENT '帖子点赞表';

-- 评论点赞表
DROP TABLE IF EXISTS comment_like;
CREATE TABLE comment_like (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    comment_id BIGINT NOT NULL COMMENT '评论ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_comment_user (comment_id, user_id),
    KEY idx_comment_id (comment_id),
    KEY idx_user_id (user_id)
) COMMENT '评论点赞表';

-- 收藏表
DROP TABLE IF EXISTS favorite;
CREATE TABLE favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    post_id BIGINT NOT NULL COMMENT '帖子ID',
    user_id BIGINT NOT NULL COMMENT '用户ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_post_user (post_id, user_id),
    KEY idx_post_id (post_id),
    KEY idx_user_id (user_id)
) COMMENT '收藏表';

-- 关注表
DROP TABLE IF EXISTS follow;
CREATE TABLE follow (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    follower_id BIGINT NOT NULL COMMENT '关注者ID',
    following_id BIGINT NOT NULL COMMENT '被关注者ID',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_follower_following (follower_id, following_id),
    KEY idx_follower_id (follower_id),
    KEY idx_following_id (following_id)
) COMMENT '关注表';

-- 通知表
DROP TABLE IF EXISTS notification;
CREATE TABLE notification (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '接收者ID',
    type VARCHAR(20) NOT NULL COMMENT '通知类型：like(点赞), comment(评论), reply(回复), follow(关注), best_answer(最佳回答)',
    related_id BIGINT COMMENT '关联ID（帖子ID或评论ID）',
    from_user_id BIGINT COMMENT '触发者ID',
    content VARCHAR(500) COMMENT '通知内容',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    KEY idx_user_id (user_id),
    KEY idx_is_read (is_read),
    KEY idx_create_time (create_time)
) COMMENT '通知表';

-- 用户积分表（扩展用户表，用于积分系统）
-- 注意：MySQL不支持IF NOT EXISTS，需要手动检查或使用存储过程
-- 如果列已存在，执行会报错，可以忽略
-- status字段已在CREATE TABLE中定义，不需要ALTER TABLE
ALTER TABLE user ADD COLUMN points INT DEFAULT 0 COMMENT '积分';
ALTER TABLE user ADD COLUMN contribution INT DEFAULT 0 COMMENT '贡献值（发帖、回答等）';
-- 如果数据库已存在且没有status字段，可以手动执行以下语句：
-- ALTER TABLE user ADD COLUMN status VARCHAR(20) DEFAULT 'ACTIVE' COMMENT '用户状态：ACTIVE(正常), FROZEN(冻结)';

-- ============================================
-- 学习社区示例数据（方便初始展示）
-- ============================================

-- 示例帖子（假设 testuser1-3 的id分别为1、2、3，课程id存在）
INSERT INTO post (user_id, title, content, type, category, course_id, tags, attachment_url, view_count, like_count, comment_count, favorite_count, status, is_top, is_resolved)
VALUES
(1, 'Java学习路线', 'java的学习路线是什么？欢迎大家分享自己的经验和踩过的坑。', 'question', '编程开发', 1, 'Java,学习路线,入门', NULL, 35, 2, 8, 1, 'published', 1, 0),
(2, '前端开发怎么系统入门？', '最近准备系统学前端，HTML/CSS/JavaScript、框架怎么安排顺序比较好？', 'discussion', '前端开发', 2, '前端,HTML,CSS,JavaScript', NULL, 28, 1, 5, 0, 'published', 0, 0),
(3, '我的Python数据分析自学心得', '分享一下自己从零基础到可以做数据分析报表的一些经验和踩坑记录，希望能帮到你。', 'experience', '数据科学', 3, 'Python,数据分析,自学', NULL, 42, 3, 8, 2, 'published', 0, 0),
(1, 'Java期末大作业分享', '我整理了一份Java期末大作业的参考资料，包含项目结构、核心代码示例和实现思路，希望对正在做期末作业的同学有帮助。附件已上传，可以下载查看。', 'experience', '编程开发', 1, 'Java,期末作业,项目分享', '/uploads/community/期末大作业.pdf', 15, 1, 1, 0, 'published', 0, 0);

-- 示例评论（假设上面生成的帖子id为1、2、3）
INSERT INTO comment (post_id, user_id, parent_id, content, like_count, is_best_answer)
VALUES
(1, 2, NULL, '我建议先把Java基础语法学扎实，再看一点简单项目实践。', 1, 0),
(1, 3, NULL, 'B站上有很多不错的Java路线视频，可以先跟着做一个小项目。', 1, 1),
(1, 1, NULL, '建议结合官方文档和《Head First Java》，做到每章配套练习。', 0, 0),
(1, 4, NULL, '补充一点：学完基础后可以尝试做一个学生管理系统，既能巩固知识又能提升实践能力。', 1, 0),
(1, 5, NULL, '推荐Oracle官方的Java Tutorials，虽然英文但非常系统全面。', 0, 0),
(1, 2, 2, '认同！做完小项目再总结一下笔记收获最大。', 0, 0),
(1, 6, 3, '《Head First Java》确实不错，图文并茂，适合入门。', 0, 0),
(2, 1, NULL, '可以先把HTML/CSS过一遍，再学原生JS，后面再选一个框架深入。', 0, 0),
(2, 3, NULL, '可以参考 MDN 的学习路径，再用一个周末做个登录页练手。', 0, 0),
(2, 4, NULL, '我建议先做一个静态网站（个人作品集），然后再学习框架。这样能更好地理解框架的作用。', 1, 0),
(2, 7, NULL, '框架选择建议：Vue相对容易上手，React生态更丰富，可以根据个人喜好选择。', 2, 0),
(2, 8, 1, '同意，基础很重要，框架只是工具。', 0, 0),
(3, 1, NULL, '坚持做小项目很重要，不要只看不练。', 2, 0),
(3, 2, NULL, '同意，多做练习，哪怕是模仿项目也很有帮助。', 0, 0),
(3, 3, NULL, '推荐尝试 Kaggle 的入门竞赛，数据和教程都很齐全。', 0, 0),
(3, 4, NULL, 'pandas和numpy是数据分析的基础，建议先熟练掌握这两个库。', 1, 0),
(3, 5, NULL, '可以尝试爬取一些公开数据来做分析，比如天气数据、股票数据等。', 0, 0),
(3, 8, NULL, '推荐《利用Python进行数据分析》这本书，作者就是pandas的创建者。', 1, 0),
(3, 9, 4, '说得对，pandas熟练后处理数据效率会高很多。', 0, 0),
(3, 10, 1, '是的，理论结合实际项目才能真正掌握。', 0, 0),
(4, 2, NULL, '感谢分享！这个资料对我很有帮助，特别是项目结构部分，让我对整体架构有了更清晰的认识。', 0, 0);

-- 示例帖子点赞（对应上面的帖子和用户）
INSERT INTO post_like (post_id, user_id)
VALUES
(1, 2),
(1, 3),
(2, 1),
(3, 1),
(3, 2),
(3, 3);

-- 示例评论点赞
INSERT INTO comment_like (comment_id, user_id)
VALUES
(1, 1),
(2, 1),
(4, 2),
(4, 7),
(7, 4),
(10, 5),
(11, 6),
(11, 9),
(13, 4),
(16, 6),
(18, 5);

-- 示例收藏
INSERT INTO favorite (post_id, user_id)
VALUES
(1, 1),
(3, 2),
(3, 3);


-- 私信表
DROP TABLE IF EXISTS message;
CREATE TABLE message (
                         id BIGINT PRIMARY KEY AUTO_INCREMENT,
                         sender_id BIGINT NOT NULL COMMENT '发送者ID',
                         receiver_id BIGINT NOT NULL COMMENT '接收者ID',
                         content TEXT NOT NULL COMMENT '消息内容',
                         is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读',
                         create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
                         KEY idx_sender_id (sender_id),
                         KEY idx_receiver_id (receiver_id),
                         KEY idx_is_read (is_read),
                         KEY idx_create_time (create_time),
                         KEY idx_sender_receiver (sender_id, receiver_id)
) COMMENT '私信表';



