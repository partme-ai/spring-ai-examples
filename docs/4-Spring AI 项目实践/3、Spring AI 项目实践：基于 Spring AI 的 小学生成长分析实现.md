# Spring AI 项目实践：基于 Spring AI 的 小学生成长分析实现

## 概述

本项目实践展示了如何使用 Spring AI 实现小学生成长分析系统，通过分析学生的学习数据、行为数据和社交数据，为教师和家长提供全面的学生成长评估。

## 技术栈

- Spring Boot 3.2+
- Spring AI 1.1.4+
- Spring Data JPA
- PostgreSQL
- Ollama（本地大模型）
- Chart.js（数据可视化）

## 核心功能

1. **学习成绩分析**：分析学生的考试成绩，生成学习趋势图表
2. **行为习惯分析**：分析学生的课堂表现、作业完成情况等
3. **社交能力分析**：分析学生的社交互动情况
4. **个性化建议**：基于 AI 分析生成个性化的学习和成长建议
5. **教师/家长反馈**：收集和分析教师与家长的反馈

## 系统架构

```
小学生成长分析系统/
├── 前端：Vue 3 + Chart.js
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
spring.datasource.url=jdbc:postgresql://localhost:5432/student_analysis
spring.datasource.username=postgres
spring.datasource.password=postgres

# JPA 配置
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

# Ollama 配置
spring.ai.ollama.base-url=http://localhost:11434
spring.ai.ollama.chat.enabled=true
spring.ai.ollama.chat.model=qwen3.5:7b

# 安全配置
spring.security.user.name=admin
spring.security.user.password=admin123
```

### 3. 数据模型

创建学生、成绩、行为记录等数据模型：

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
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Score> scores;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<BehaviorRecord> behaviorRecords;
    
    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL)
    private List<Feedback> feedbacks;
    
    // getters and setters
}

@Entity
public class Score {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String subject;
    private Integer score;
    private LocalDate testDate;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    // getters and setters
}

@Entity
public class BehaviorRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String behaviorType;
    private String description;
    private LocalDate recordDate;
    private Integer score; // 行为评分
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    // getters and setters
}

@Entity
public class Feedback {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String type; // teacher, parent
    private String content;
    private LocalDate feedbackDate;
    
    @ManyToOne
    @JoinColumn(name = "student_id")
    private Student student;
    
    // getters and setters
}
```

### 4. AI 分析服务

创建 AI 分析服务：

```java
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemMessage;
import org.springframework.ai.chat.prompt.UserMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIAnalysisService {

    @Autowired
    private ChatClient chatClient;

    public String analyzeStudentGrowth(Student student) {
        // 构建分析提示
        StringBuilder promptContent = new StringBuilder();
        promptContent.append("请分析以下学生的成长情况：\n");
        promptContent.append("学生姓名：").append(student.getName()).append("\n");
        promptContent.append("年级：").append(student.getGrade()).append("\n");
        promptContent.append("班级：").append(student.getClassNumber()).append("\n");
        
        // 添加成绩信息
        promptContent.append("\n成绩情况：\n");
        for (Score score : student.getScores()) {
            promptContent.append(score.getSubject()).append(": ").append(score.getScore())
                    .append(" (").append(score.getTestDate()).append(")\n");
        }
        
        // 添加行为记录
        promptContent.append("\n行为记录：\n");
        for (BehaviorRecord record : student.getBehaviorRecords()) {
            promptContent.append(record.getBehaviorType()).append(": ").append(record.getDescription())
                    .append(" 评分:").append(record.getScore()).append("\n");
        }
        
        // 添加反馈信息
        promptContent.append("\n反馈信息：\n");
        for (Feedback feedback : student.getFeedbacks()) {
            promptContent.append(feedback.getType()).append(": ").append(feedback.getContent()).append("\n");
        }
        
        // 构建提示
        Prompt prompt = new Prompt(List.of(
            new SystemMessage("你是一个专业的教育分析师，擅长分析学生的成长情况并提供有针对性的建议。"),
            new UserMessage(promptContent.toString() + "\n请提供详细的成长分析和个性化建议。")
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

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentRepository studentRepository;
    
    @Autowired
    private AIAnalysisService aiAnalysisService;

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id).orElseThrow();
    }

    @PostMapping
    public Student createStudent(@RequestBody Student student) {
        return studentRepository.save(student);
    }

    @GetMapping("/{id}/analysis")
    public String getStudentAnalysis(@PathVariable Long id) {
        Student student = studentRepository.findById(id).orElseThrow();
        return aiAnalysisService.analyzeStudentGrowth(student);
    }

}

@Repository
interface StudentRepository extends JpaRepository<Student, Long> {
}
```

## 完整示例

### 项目结构

```
spring-ai-student-analysis/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/
│   │   │       └── github/
│   │   │           └── teachingai/
│   │   │               └── student/
│   │   │                   ├── model/
│   │   │                   │   ├── Student.java
│   │   │                   │   ├── Score.java
│   │   │                   │   ├── BehaviorRecord.java
│   │   │                   │   └── Feedback.java
│   │   │                   ├── repository/
│   │   │                   │   └── StudentRepository.java
│   │   │                   ├── service/
│   │   │                   │   └── AIAnalysisService.java
│   │   │                   ├── controller/
│   │   │                   │   └── StudentController.java
│   │   │                   └── SpringAiStudentAnalysisApplication.java
│   │   └── resources/
│   │       └── application.properties
│   └── test/
│       └── java/
│           └── com/
│               └── github/
│                   └── teachingai/
│                       └── student/
│                           └── SpringAiStudentAnalysisApplicationTests.java
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
public class SpringAiStudentAnalysisApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringAiStudentAnalysisApplication.class, args);
    }

}
```

## 前端实现

### 技术栈

- Vue 3
- Chart.js
- Axios

### 核心组件

1. **学生列表组件**：展示所有学生信息
2. **学生详情组件**：展示学生详细信息
3. **成绩分析组件**：使用 Chart.js 展示成绩趋势
4. **行为分析组件**：展示行为记录和评分
5. **AI 分析组件**：展示 AI 生成的分析报告

### 示例代码

```vue
<template>
  <div class="student-analysis">
    <h1>学生成长分析系统</h1>
    
    <div class="student-list">
      <h2>学生列表</h2>
      <ul>
        <li v-for="student in students" :key="student.id"
            @click="selectStudent(student)">
          {{ student.name }} - {{ student.grade }}年级{{ student.classNumber }}班
        </li>
      </ul>
    </div>
    
    <div v-if="selectedStudent" class="student-detail">
      <h2>{{ selectedStudent.name }} 成长分析</h2>
      
      <div class="score-analysis">
        <h3>成绩分析</h3>
        <canvas ref="scoreChart"></canvas>
      </div>
      
      <div class="behavior-analysis">
        <h3>行为分析</h3>
        <ul>
          <li v-for="record in selectedStudent.behaviorRecords" :key="record.id">
            {{ record.behaviorType }}: {{ record.description }} (评分: {{ record.score }})
          </li>
        </ul>
      </div>
      
      <div class="ai-analysis">
        <h3>AI 分析报告</h3>
        <div v-if="analysisResult" class="analysis-content">
          {{ analysisResult }}
        </div>
        <button @click="getAnalysis" v-else>获取分析报告</button>
      </div>
    </div>
  </div>
</template>

<script>
import axios from 'axios';
import Chart from 'chart.js/auto';

export default {
  data() {
    return {
      students: [],
      selectedStudent: null,
      analysisResult: null,
      scoreChart: null
    };
  },
  mounted() {
    this.loadStudents();
  },
  methods: {
    async loadStudents() {
      const response = await axios.get('/api/students');
      this.students = response.data;
    },
    selectStudent(student) {
      this.selectedStudent = student;
      this.analysisResult = null;
      this.renderScoreChart();
    },
    async getAnalysis() {
      const response = await axios.get(`/api/students/${this.selectedStudent.id}/analysis`);
      this.analysisResult = response.data;
    },
    renderScoreChart() {
      if (this.scoreChart) {
        this.scoreChart.destroy();
      }
      
      const ctx = this.$refs.scoreChart.getContext('2d');
      const scores = this.selectedStudent.scores;
      
      const subjects = [...new Set(scores.map(s => s.subject))];
      const datasets = subjects.map(subject => {
        const subjectScores = scores.filter(s => s.subject === subject);
        return {
          label: subject,
          data: subjectScores.map(s => s.score),
          borderColor: this.getRandomColor(),
          tension: 0.1
        };
      });
      
      this.scoreChart = new Chart(ctx, {
        type: 'line',
        data: {
          labels: [...new Set(scores.map(s => s.testDate))],
          datasets: datasets
        },
        options: {
          responsive: true,
          plugins: {
            legend: {
              position: 'top',
            },
            title: {
              display: true,
              text: '成绩趋势'
            }
          }
        }
      });
    },
    getRandomColor() {
      const letters = '0123456789ABCDEF';
      let color = '#';
      for (let i = 0; i < 6; i++) {
        color += letters[Math.floor(Math.random() * 16)];
      }
      return color;
    }
  }
};
</script>

<style scoped>
.student-analysis {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.student-list {
  margin-bottom: 30px;
}

.student-list ul {
  list-style: none;
  padding: 0;
}

.student-list li {
  padding: 10px;
  border: 1px solid #ddd;
  margin-bottom: 5px;
  cursor: pointer;
}

.student-list li:hover {
  background-color: #f5f5f5;
}

.student-detail {
  border: 1px solid #ddd;
  padding: 20px;
  border-radius: 5px;
}

.score-analysis {
  margin-bottom: 30px;
}

.behavior-analysis {
  margin-bottom: 30px;
}

.ai-analysis {
  margin-bottom: 30px;
}

.analysis-content {
  white-space: pre-line;
  border: 1px solid #ddd;
  padding: 15px;
  border-radius: 5px;
  background-color: #f9f9f9;
}
</style>
```

## 测试方法

1. **启动应用**：运行 `SpringAiStudentAnalysisApplication` 类
2. **启动 PostgreSQL**：确保 PostgreSQL 服务正在运行
3. **启动 Ollama**：确保 Ollama 服务正在运行
4. **添加测试数据**：使用 POST 请求添加测试学生数据
   ```bash
   curl -X POST http://localhost:8080/api/students \
     -H "Content-Type: application/json" \
     -d '{
       "name": "张三",
       "age": 10,
       "grade": "四年级",
       "classNumber": "三班",
       "scores": [
         {"subject": "语文", "score": 85, "testDate": "2024-01-15"},
         {"subject": "数学", "score": 90, "testDate": "2024-01-15"},
         {"subject": "英语", "score": 88, "testDate": "2024-01-15"},
         {"subject": "语文", "score": 88, "testDate": "2024-03-15"},
         {"subject": "数学", "score": 92, "testDate": "2024-03-15"},
         {"subject": "英语", "score": 90, "testDate": "2024-03-15"}
       ],
       "behaviorRecords": [
         {"behaviorType": "课堂表现", "description": "积极参与课堂讨论", "score": 5, "recordDate": "2024-01-20"},
         {"behaviorType": "作业完成", "description": "按时完成作业", "score": 4, "recordDate": "2024-01-25"},
         {"behaviorType": "同学关系", "description": "与同学相处融洽", "score": 5, "recordDate": "2024-02-01"}
       ],
       "feedbacks": [
         {"type": "teacher", "content": "学习态度认真，成绩稳步提升", "feedbackDate": "2024-03-01"},
         {"type": "parent", "content": "在家表现良好，主动完成作业", "feedbackDate": "2024-03-05"}
       ]
     }'
   ```
5. **获取分析报告**：
   ```bash
   curl http://localhost:8080/api/students/1/analysis
   ```

## 最佳实践

1. **数据安全**：确保学生数据的安全性和隐私保护
2. **模型选择**：选择适合教育场景的 AI 模型
3. **数据质量**：确保输入数据的准确性和完整性
4. **分析结果解释**：为教师和家长提供易于理解的分析结果
5. **持续改进**：根据反馈不断优化分析算法和模型

## 相关资源

- [Spring AI 官方文档](https://spring.io/projects/spring-ai)
- [Ollama 官方文档](https://ollama.com/docs)
- [Chart.js 官方文档](https://www.chartjs.org/docs/latest/)
- [Vue 3 官方文档](https://vuejs.org/)

## 扩展功能

1. **多维度分析**：增加更多维度的分析，如兴趣爱好、特长等
2. **预测模型**：基于历史数据预测学生未来的成长趋势
3. **个性化学习计划**：根据分析结果生成个性化的学习计划
4. **家校互动**：提供教师和家长的互动平台
5. **数据导出**：支持分析报告的导出功能