# 宿舍自动分配功能修改文档（中文完整版）

本文件总结了本次为 **宿舍自动分配系统（AutoDormAllocation）** 所进行的全部代码修改、SQL 变更以及新增功能，方便组员在 GitHub 上查看与合并。

---

# 📌 1. 本次修改涉及的 Java 文件（总览）

以下文件是此次实现自动宿舍分配功能中**新增或修改**的内容：

| 文件 | 路径 | 类型 | 修改内容 |
|------|------|------|----------|
| `AutoDormAllocationController.java` | `src/main/java/com/example/campus/controller/` | 新增 | 提供 `/api/dorm/allocation/run` 自动分配接口 |
| `AutoDormAllocationService.java` | `src/main/java/com/example/campus/service/` | 新增 | 自动宿舍分配算法核心逻辑 |
| `DormPreferenceService.java` | `src/main/java/com/example/campus/service/` | 修改 | 增加 HuggingFace AI 调用、修复类型转换问题 |
| `DormPreferenceMapper.java` | `src/main/java/com/example/campus/mapper/` | 修改 | 添加 `selectAll()` 方法 |
| `DormPreferenceMapper.xml` | `src/main/resources/mapper/` | 修改 | 添加 `selectAll` SQL；修复 LocalTime 字段映射 |
| `HFClient.java` | `src/main/java/com/example/campus/util/` | 新增 | HuggingFace Zero‑Shot NLP AI 客户端 |
| `CampusApplication.java` | `src/main/java/com/example/campus/` | 修改 | 设置包扫描问题（如需要） |

---

# 📌 2. 数据库表（重新创建）

为了确保自动分配功能正常运行，重新创建了以下三张表：

---

## ✅ 2.1 `dorm_building`（宿舍楼）

```sql
DROP TABLE IF EXISTS dorm_building;

CREATE TABLE dorm_building (
    building_id INT PRIMARY KEY AUTO_INCREMENT,
    building_name VARCHAR(255) NOT NULL,
    gender_limit INT NOT NULL,         -- 1=男 2=女 0=不限
    manager_id BIGINT NULL,
    is_active INT DEFAULT 1
);
```

---

## ✅ 2.2 `dorm_room`（宿舍房间）

```sql
DROP TABLE IF EXISTS dorm_room;

CREATE TABLE dorm_room (
    room_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    building_id INT NOT NULL,
    building_name VARCHAR(255),
    room_number VARCHAR(100),
    full_name VARCHAR(255),
    total_beds INT DEFAULT 4,
    occupied_beds INT DEFAULT 0,
    room_status INT DEFAULT 1,
    room_type VARCHAR(50),
    has_private_bathroom INT DEFAULT 0
);
```

---

## ✅ 2.3 `dorm_bed`（宿舍床位）

```sql
DROP TABLE IF EXISTS dorm_bed;

CREATE TABLE dorm_bed (
    bed_id BIGINT PRIMARY KEY AUTO_INCREMENT,
    room_id BIGINT NOT NULL,
    bed_number VARCHAR(50),
    bed_status INT DEFAULT 1,       -- 1空闲 2已分配
    current_resident_id BIGINT,
    allocation_time DATETIME
);
```

---

## ⚠️ 注意：`t_dorm_preference` 保持不变，但修复了类型映射问题

表结构仍使用原来的，只需保证：

```sql
wake_up_time TIME
bedtime TIME
```

---

# 📌 3. 主要代码修改说明（完整）

---

## ✅ 3.1 新增：`AutoDormAllocationController.java`

提供接口：

```
POST /api/dorm/allocation/run
```

代码示例：

```java
@RestController
@RequestMapping("/api/dorm/allocation")
@RequiredArgsConstructor
public class AutoDormAllocationController {

    private final AutoDormAllocationService service;

    @PostMapping("/run")
    public List<Map<String, Object>> run() {
        return service.runAllocation();
    }
}
```

---

## ✅ 3.2 新增：`AutoDormAllocationService.java`

核心自动分配功能：

- 读取所有学生偏好
- 按 AI 评分排序
- 每 4 人组成一组
- 依次填入可用宿舍房间
- 分配床位

---

## ✅ 3.3 修改 `DormPreferenceService.java`

主要修改：

- 使用 `HFClient` 调用 HuggingFace Zero‑Shot 分类模型
- 将 AI 结果写入 `matchingScore`
- 修复 LocalTime 读取问题（MyBatis TypeHandler）

---

## ✅ 3.4 修改 `DormPreferenceMapper.java`

新增：

```java
List<DormPreference> selectAll();
```

---

## ✅ 3.5 修改 `DormPreferenceMapper.xml`

添加：

```xml
<select id="selectAll" resultMap="BaseResultMap">
    SELECT *
    FROM t_dorm_preference
    ORDER BY submission_time DESC
</select>
```

并加入：

```xml
<result column="wake_up_time" property="wakeUpTime" jdbcType="TIME"/>
<result column="bedtime" property="bedtime" jdbcType="TIME"/>
```

---

## ✅ 3.6 新增：`HFClient.java`

使用 OkHttp 调用 HuggingFace API：

- 自动分析学生自我介绍
- 输出标签：quiet / tidy / messy / study / gaming 等
- 评分写入数据库

---

# 📌 4. Spring Boot 启动方式

在项目根目录运行：

```
mvn spring-boot:run
```

看到：

```
Started CampusApplication in ...
```

即表示成功。

---

# 📌 5. 如何测试自动分配接口

运行：

```bash
curl http://localhost:8081/api/dorm/allocation/run -Method POST
```

成功后返回类似：

```json
[
  {
    "room": "Building A - Room 101",
    "students": [
      { "studentId": 1001, ... },
      { "studentId": 1002, ... }
    ]
  }
]
```

![image-20251129014905618](C:\Users\15233\AppData\Roaming\Typora\typora-user-images\image-20251129014905618.png)（我的本地实际运行效果图）

（由于加入了外部AI的powerkey，因此在spoot测试阶段会报错，不必管他，直接run就行了）

---

# 📌 6. 最终说明

我的项目现在：

- 自动分配宿舍
- 支持 AI 分析偏好
- 正常访问 `/api/dorm/allocation/run`
- 数据库结构规范


