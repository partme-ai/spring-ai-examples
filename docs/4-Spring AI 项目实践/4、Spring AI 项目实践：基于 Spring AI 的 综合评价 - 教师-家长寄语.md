# Spring AI 项目实践：基于 Spring AI 的 综合评价 - 教师-家长寄语

## 概述

本项目实践展示了如何使用 Spring AI 实现学生综合评价系统，特别是教师和家长寄语的生成。通过 AI 分析学生的各项数据，自动生成个性化的教师评语和家长寄语，帮助教师和家长更有效地与学生沟通。

## 技术栈

- Spring Boot 3.2+
- Spring AI 1.1.4+
- Spring Data JPA
- PostgreSQL
- Ollama（本地大模型）
- Thymeleaf（模板引擎）

## 核心功能

1. **学生数据管理**：管理学生的基本信息、学习成绩、行为表现等
2. **教师评语生成**：基于学生数据自动生成教师评语
3. **家长寄语生成**：生成适合家长阅读的学生成长寄语
4. **评语模板管理**：支持自定义评语模板
5. **多维度评价**：从学习、行为、社交等多个维度进行评价

## 系统架构

```
学生综合评价系统/
├── 前端：Thymeleaf + Bootstrap
├── 后端：Spring Boot + Spring AI
├── 数据存储：PostgreSQL
└── AI 模型：Ollama (Qwen3.5)
```

## 实现步骤

### 1. 添加依赖

在 `pom.xml` 文件中添加以下依赖：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-web</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-ollama-spring-boot-starter</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-data-jpa</artifactId>
    </dependency>
    <dependency>
        <groupId>org.postgresql</groupId>
        <artifactId>postgresql</artifactId>
        <scope>runtime</scope>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-thymeleaf</artifactId>
    </dependency>
    <dependency>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
</dependencies>

<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-bom</artifactId>
            <version>1.1.4</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>
```

### 2. 配置文件

在 `application.properties` 文件中配置相关设置：

```properties
# 数据库配置
spring.datasource.url=jdbc:postgresql://localhost:5432/student_evaluation
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA 配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.model=qwen3.5:7b

# Thymeleaf 配置
spring.thymeleaf.cache=false

# 安全配置
spring.security.user.name=admin
spring.security.user.password=admin123
```

### 3. 数据模型

创建学生、评价数据等模型：

```java
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
public class Student {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private Integer age;
    private String grade;
    private String classNumber;
    private String gender;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Evaluation> evaluations;
    
    // getters and setters
}

@Entity
public class Evaluation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private LocalDate evaluationDate;
    private String semester;
    private Integer academicScore; // 学业成绩
    private Integer behaviorScore; // 行为表现
    private Integer socialScore; // 社交能力
    private String teacherComment; // 教师评语
    private String parentMessage; // 家长寄语
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    // getters and setters
}

@Entity
public class Template {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type; // teacher, parent
    private String content; // 模板内容
    private String description;
    
    // getters and setters
}
```

### 4. AI 评语生成服务

创建 AI 评语生成服务：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIEvaluationService {

    @Autowired
    private ChatClient chatClient;

    public String generateTeacherComment(Student student, Evaluation evaluation) {
        // 构建提示
        StringBuilder promptContent = new StringBuilder();
        promptContent.append("请为以下学生生成教师评语：\n");
        promptContent.append("学生姓名：").append(student.getName()).append("\n");
        promptContent.append("年级：").append(student.getGrade()).append("\n");
        promptContent.append("班级：").append(student.getClassNumber()).append("\n");
        promptContent.append("性别：").append(student.getGender()).append("\n");
        promptContent.append("学业成绩：").append(evaluation.getAcademicScore()).append("\n");
        promptContent.append("行为表现：").append(evaluation.getBehaviorScore()).append("\n");
        promptContent.append("社交能力：").append(evaluation.getSocialScore()).append("\n");
        
        // 构建提示
        Prompt prompt = new Prompt(List.of(
            new SystemMessage("你是一位经验丰富的教师，擅长根据学生的表现生成客观、鼓励性的评语。评语应该具体、有针对性，既要肯定学生的优点，也要指出需要改进的地方。"),
            new UserMessage(promptContent.toString() + "\n请生成一段适合写入学生成绩单的教师评语。")
        ));
        
        // 调用 AI 模型
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }

    public String generateParentMessage(Student student, Evaluation evaluation) {
        // 构建提示
        StringBuilder promptContent = new StringBuilder();
        promptContent.append("请为以下学生生成家长寄语：\n");
        promptContent.append("学生姓名：").append(student.getName()).append("\n");
        promptContent.append("年级：").append(student.getGrade()).append("\n");
        promptContent.append("班级：").append(student.getClassNumber()).append("\n");
        promptContent.append("性别：").append(student.getGender()).append("\n");
        promptContent.append("学业成绩：").append(evaluation.getAcademicScore()).append("\n");
        promptContent.append("行为表现：").append(evaluation.getBehaviorScore()).append("\n");
        promptContent.append("社交能力：").append(evaluation.getSocialScore()).append("\n");
        
        // 构建提示
        Prompt prompt = new Prompt(List.of(
            new SystemMessage("你是一位关心孩子成长的家长，擅长根据孩子的表现生成温暖、鼓励性的寄语。寄语应该体现对孩子的爱和期望，既要肯定孩子的进步，也要表达对未来的美好祝愿。"),
            new UserMessage(promptContent.toString() + "\n请生成一段适合家长写给孩子的寄语。")
        ));
        
        // 调用 AI 模型
        return chatClient.call(prompt).getResult().getOutput().getContent();
    }

}
```

### 5. 控制器

创建控制器：

```java
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.ui.Model;

import java.util.List;

@Controller
@RequestMapping("/evaluations")
public class EvaluationController {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private EvaluationRepository evaluationRepository;
    
    @Autowired
    private AIEvaluationService aiEvaluationService;

    @GetMapping
    public String listEvaluations(Model model) {
        model.addAttribute("students", studentRepository.findAll());
        return "evaluations/list";
    }

    @GetMapping("/create/{studentId}")
    public String createEvaluationForm(@PathVariable Long studentId, Model model) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        model.addAttribute("student", student);
        model.addAttribute("evaluation", new Evaluation());
        return "evaluations/create";
    }

    @PostMapping("/create")
    public String createEvaluation(@ModelAttribute Evaluation evaluation, @RequestParam Long studentId) {
        Student student = studentRepository.findById(studentId).orElseThrow();
        evaluation.setStudent(student);
        
        // 生成教师评语和家长寄语
        evaluation.setTeacherComment(aiEvaluationService.generateTeacherComment(student, evaluation));
        evaluation.setParentMessage(aiEvaluationService.generateParentMessage(student, evaluation));
        
        evaluationRepository.save(evaluation);
        return "redirect:/evaluations";
    }

    @GetMapping("/view/{id}")
    public String viewEvaluation(@PathVariable Long id, Model model) {
        Evaluation evaluation = evaluationRepository.findById(id).orElseThrow();
        model.addAttribute("evaluation", evaluation);
        return "evaluations/view";
    }

}

@Repository
interface StudentRepository extends JpaRepository<Student, Long> {
}

@Repository
interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
    List<Evaluation> findByStudentId(Long studentId);
}

@Repository
interface TemplateRepository extends JpaRepository<Template, Long> {
    List<Template> findByType(String type);
}
```

## 前端实现

### 核心页面

1. **学生列表页**：展示所有学生信息
2. **评价创建页**：创建学生评价
3. **评价详情页**：查看评价详情，包括教师评语和家长寄语

### 示例模板

#### 评价创建页 (`src/main/resources/templates/evaluations/create.html`)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>创建评价</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-5">
        <h1>创建评价 - <span th:text="${student.name}"></span></h1>
        <form th:action="@{/evaluations/create}" th:object="${evaluation}" method="post">
            <input type="hidden" th:field="*{student.id}" th:value="${student.id}">
            
            <div class="mb-3">
                <label for="evaluationDate" class="form-label">评价日期</label>
                <input type="date" class="form-control" id="evaluationDate" th:field="*{evaluationDate}" required>
            </div>
            
            <div class="mb-3">
                <label for="semester" class="form-label">学期</label>
                <input type="text" class="form-control" id="semester" th:field="*{semester}" required>
            </div>
            
            <div class="mb-3">
                <label for="academicScore" class="form-label">学业成绩</label>
                <input type="number" class="form-control" id="academicScore" th:field="*{academicScore}" min="0" max="100" required>
            </div>
            
            <div class="mb-3">
                <label for="behaviorScore" class="form-label">行为表现</label>
                <input type="number" class="form-control" id="behaviorScore" th:field="*{behaviorScore}" min="0" max="100" required>
            </div>
            
            <div class="mb-3">
                <label for="socialScore" class="form-label">社交能力</label>
                <input type="number" class="form-control" id="socialScore" th:field="*{socialScore}" min="0" max="100" required>
            </div>
            
            <button type="submit" class="btn btn-primary">生成评价</button>
        </form>
    </div>
</body>
</html>
```

#### 评价详情页 (`src/main/resources/templates/evaluations/view.html`)

```html
<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <meta charset="UTF-8">
    <title>评价详情</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css">
</head>
<body>
    <div class="container mt-5">
        <h1>评价详情</h1>
        
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title">学生信息</h5>
            </div>
            <div class="card-body">
                <p><strong>姓名：</strong><span th:text="${evaluation.student.name}"></span></p>
                <p><strong>年级：</strong><span th:text="${evaluation.student.grade}"></span></p>
                <p><strong>班级：</strong><span th:text="${evaluation.student.classNumber}"></span></p>
                <p><strong>性别：</strong><span th:text="${evaluation.student.gender}"></span></p>
            </div>
        </div>
        
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title">评价信息</h5>
            </div>
            <div class="card-body">
                <p><strong>评价日期：</strong><span th:text="${evaluation.evaluationDate}"></span></p>
                <p><strong>学期：</strong><span th:text="${evaluation.semester}"></span></p>
                <p><strong>学业成绩：</strong><span th:text="${evaluation.academicScore}"></span></p>
                <p><strong>行为表现：</strong><span th:text="${evaluation.behaviorScore}"></span></p>
                <p><strong>社交能力：</strong><span th:text="${evaluation.socialScore}"></span></p>
            </div>
        </div>
        
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title">教师评语</h5>
            </div>
            <div class="card-body">
                <p th:text="${evaluation.teacherComment}"></p>
            </div>
        </div>
        
        <div class="card mb-4">
            <div class="card-header">
                <h5 class="card-title">家长寄语</h5>
            </div>
            <div class="card-body">
                <p th:text="${evaluation.parentMessage}"></p>
            </div>
        </div>
        
        <a href="/evaluations" class="btn btn-secondary">返回列表</a>
    </div>
</body>
</html>
```

## 完整示例

### 项目结构

```
spring-ai-evaluation/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── evaluation/
│   │   │                   ├── model/
│   │   │                   │   ├── Student.java
│   │   │                   │   ├── Evaluation.java
│   │   │                   │   └── Template.java
│   │   │                   ├── repository/
│   │   │                   │   ├── StudentRepository.java
│   │   │                   │   ├── EvaluationRepository.java
│   │   │                   │   └── TemplateRepository.java
│   │   │                   ├── service/
│   │   │                   │   └── AIEvaluationService.java
│   │   │                   ├── controller/
│   │   │                   │   └── EvaluationController.java
│   │   │                   └── SpringAiEvaluationApplication.java
│   │   └── resources/
│   │       ├── templates/
│   │       │   └── evaluations/
│   │       │       ├── list.html
│   │       │       ├── create.html
│   │       │       └── view.html
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── evaluation/
│                           └── SpringAiEvaluationApplicationTests.java
├── .gitignore
├── README.md
├── mvnw
└── mvnw.cmd
```

### 主应用类

```java
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringAiEvaluationApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiEvaluationApplication.class, args);
    }

}
```

## 测试方法

1. **启动应用**：运行 `SpringAiEvaluationApplication` 类
2. **启动 PostgreSQL**：确保 PostgreSQL 服务正在运行
3. **启动 Ollama**：确保 Ollama 服务正在运行
4. **访问应用**：打开浏览器访问 `http://localhost:8080/evaluations`
5. **创建评价**：
   - 点击 "创建评价" 按钮
   - 填写评价信息
   - 点击 "生成评价" 按钮
6. **查看评价**：点击评价列表中的 "查看" 链接，查看生成的教师评语和家长寄语

## 最佳实践

1. **数据安全**：确保学生数据的安全性和隐私保护
2. **模型选择**：选择适合教育场景的 AI 模型
3. **评语质量**：定期评估生成评语的质量，不断优化提示词
4. **个性化**：确保生成的评语具有个性化，避免千篇一律
5. **人工审核**：重要评价建议进行人工审核后再使用
6. **模板管理**：维护和更新评语模板，适应不同场景

## 相关资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [Ollama 官方文档](https://ollama.com/docs)
- [Thymeleaf 官方文档](https://www.thymeleaf.org/documentation.html)
- [Bootstrap 官方文档](https://getbootstrap.com/docs/5.3/)

## 扩展功能

1. **多语言支持**：支持生成不同语言的评语
2. **评语分类**：根据不同学科或领域生成专业评语
3. **批量生成**：支持批量生成多个学生的评语
4. **评语历史**：保存评语历史，便于跟踪学生成长
5. **数据可视化**：展示学生评价数据的趋势图表
6. **导出功能**：支持导出评价报告为 PDF 或 Word 文档
7. **家长反馈**：收集家长对评语的反馈，不断优化生成质量