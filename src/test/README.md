# 单元测试文档

## 测试概述

本项目包含全面的单元测试套件，覆盖了核心业务逻辑、工具函数、组件和服务等关键模块。

## 测试覆盖率目标

- **目标覆盖率**: 80%以上
- **当前覆盖模块**:
  - 工具类 (util): PasswordUtils, FileUploadUtils
  - 服务层 (service): EmailCodeService, UserService
  - 控制器层 (controller): AuthController

## 测试框架

- **JUnit 5**: 测试框架
- **Mockito**: Mock框架，用于模拟依赖
- **Spring Boot Test**: Spring Boot测试支持
- **MockMvc**: Web层测试支持

## 运行测试

### 运行所有测试

```bash
mvn test
```

### 运行特定测试类

```bash
mvn test -Dtest=PasswordUtilsTest
```

### 运行特定测试方法

```bash
mvn test -Dtest=PasswordUtilsTest#testIsPasswordStrong_ValidPassword
```

### 生成测试覆盖率报告

```bash
mvn clean test jacoco:report
```

覆盖率报告将生成在 `target/site/jacoco/index.html`

## 测试结构

```
src/test/java/com/nchu/learningplatform/
├── util/                          # 工具类测试
│   ├── PasswordUtilsTest.java     # 密码工具类测试
│   └── FileUploadUtilsTest.java   # 文件上传工具类测试
├── service/                       # 服务层测试
│   ├── EmailCodeServiceTest.java  # 邮箱验证码服务测试
│   └── impl/                      # 服务实现类测试
│       └── UserServiceImplTest.java # 用户服务实现类测试
├── controller/                    # 控制器测试
│   └── AuthControllerTest.java    # 认证控制器测试
├── TestBase.java                  # 测试基类
└── LearningPlatformApplicationTests.java # 应用启动测试
```

## 测试编写规范

### AAA模式

所有测试遵循AAA（Arrange-Act-Assert）模式：

```java
@Test
void testExample() {
    // Arrange: 准备测试数据和环境
    String input = "test";
    
    // Act: 执行被测试的方法
    String result = methodUnderTest(input);
    
    // Assert: 验证结果
    assertEquals("expected", result);
}
```

### 测试命名规范

- 测试类名: `被测试类名 + Test`
- 测试方法名: `test方法名_场景描述`
- 使用 `@DisplayName` 注解提供中文描述

### Mock使用规范

- 使用 `@Mock` 注解标记需要Mock的依赖
- 使用 `@InjectMocks` 注解标记被测试的类
- 使用 `@ExtendWith(MockitoExtension.class)` 启用Mockito

## 测试用例分类

### 正常场景测试

测试功能在正常输入下的行为，确保功能按预期工作。

### 边界条件测试

测试边界值，如：
- 空值/null值
- 空字符串
- 最小值/最大值
- 正好等于限制的值

### 异常情况测试

测试错误处理和异常情况，如：
- 无效输入
- 资源不存在
- 业务规则违反
- 系统异常

## 注意事项

1. **测试独立性**: 每个测试应该是独立的，不依赖其他测试的执行顺序
2. **测试可重复性**: 测试应该可以重复执行，结果一致
3. **测试速度**: 单元测试应该快速执行，避免依赖外部资源
4. **Mock外部依赖**: 使用Mock避免依赖数据库、网络等外部资源
5. **清理资源**: 使用 `@BeforeEach` 和 `@AfterEach` 清理测试数据

## 持续集成

测试在CI/CD流程中自动执行，确保代码质量。

## 贡献指南

添加新功能时，请同时添加相应的单元测试。测试覆盖率不应低于80%。

