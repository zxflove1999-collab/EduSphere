# API接口测试说明

## 测试前准备

1. **确保数据库已创建并初始化**
   - 执行 `init-db.sql` 创建数据库和表结构
   - 确保数据库连接配置正确（`application.properties`）

2. **启动项目**
   ```bash
   mvn spring-boot:run
   ```
   或使用IDE直接运行 `CampusApplication`

3. **确保端口8081可用**

## 接口测试清单
[API测试说明.md](API%E6%B5%8B%E8%AF%95%E8%AF%B4%E6%98%8E.md)
### 1. 认证管理

#### 1.1 用户登录
- **接口**: `POST /login`
- **测试数据**:
```json
{
    "username": "2021001001",
    "password": "123456"
}
```
- **预期结果**: 返回token和用户信息

### 2. 个人中心

#### 2.1 获取当前登录用户信息
- **接口**: `GET /profile`
- **Header**: `token: {登录返回的token}`
- **预期结果**: 返回用户详细信息

#### 2.2 修改当前用户信息
- **接口**: `PUT /profile`
- **Header**: `token: {登录返回的token}`
- **测试数据**:
```json
{
    "basicInfo": {
        "email": "new_email@edu.cn",
        "phone_number": "13900000000"
    },
    "profileInfo": {
        "major": "软件工程",
        "class_name": "软件2102班"
    }
}
```

#### 2.3 修改当前用户密码
- **接口**: `PUT /profile/password`
- **Header**: `token: {登录返回的token}`
- **测试数据**:
```json
{
    "oldPassword": "123456",
    "newPassword": "newPassword123"
}
```

### 3. 文件上传

#### 3.1 文件上传
- **接口**: `POST /upload`
- **Content-Type**: `multipart/form-data`
- **参数**: `file` (文件)
- **预期结果**: 返回文件URL

### 4. 角色管理（Admin）

#### 4.1 角色列表查询
- **接口**: `GET /admin/roles?page=1&pageSize=10&role_name=学生`
- **预期结果**: 返回分页的角色列表

#### 4.2 添加角色
- **接口**: `POST /admin/roles`
- **测试数据**:
```json
{
    "role_name": "教务管理员",
    "role_key": "teaching_admin",
    "description": "负责管理智能教学系统的管理员"
}
```

#### 4.3 根据ID查询角色
- **接口**: `GET /admin/roles/1`
- **预期结果**: 返回角色详情

#### 4.4 修改角色
- **接口**: `PUT /admin/roles`
- **测试数据**:
```json
{
    "role_id": 1,
    "role_name": "学生",
    "role_key": "student",
    "description": "学生角色"
}
```

#### 4.5 删除角色
- **接口**: `DELETE /admin/roles/3`
- **注意**: 系统角色不能删除

#### 4.6 查询所有角色（下拉列表）
- **接口**: `GET /admin/roles/list`
- **预期结果**: 返回简化的角色列表

### 5. 学生管理（Admin）

#### 5.1 学生列表查询
- **接口**: `GET /admin/students?page=1&pageSize=10&full_name=张&major=计算机`
- **预期结果**: 返回分页的学生列表

#### 5.2 添加学生
- **接口**: `POST /admin/students`
- **测试数据**:
```json
{
    "username": "2021001002",
    "password": "654321",
    "full_name": "李同学",
    "email": "li@edu.cn",
    "phone_number": "13987654321",
    "gender": 2,
    "major": "软件工程",
    "class_name": "软件2102班",
    "grade": "2021级",
    "enrollment_date": "2021-09-01"
}
```

#### 5.3 根据ID查询学生
- **接口**: `GET /admin/students/1001`
- **预期结果**: 返回学生详情

#### 5.4 修改学生资料
- **接口**: `PUT /admin/students`
- **测试数据**:
```json
{
    "user_id": 1001,
    "full_name": "张同学",
    "email": "new_zhang@edu.cn",
    "phone_number": "13811112222",
    "status": 1,
    "major": "计算机科学与技术",
    "class_name": "计算机2101班",
    "grade": "2021级"
}
```

#### 5.5 删除学生
- **接口**: `DELETE /admin/students/1001,1002`
- **注意**: 支持批量删除

### 6. 教师管理（Admin）

#### 6.1 教师列表查询
- **接口**: `GET /admin/teachers?page=1&pageSize=10&full_name=周&department=信息工程学院`
- **预期结果**: 返回分页的教师列表

#### 6.2 添加教师
- **接口**: `POST /admin/teachers`
- **测试数据**:
```json
{
    "username": "T2025002",
    "password": "654321",
    "full_name": "陈老师",
    "email": "chen@edu.cn",
    "phone_number": "13612345678",
    "gender": 1,
    "department": "信息工程学院",
    "title": "副教授",
    "office_location": "教三楼 402"
}
```

#### 6.3 根据ID查询教师
- **接口**: `GET /admin/teachers/2001`
- **预期结果**: 返回教师详情

#### 6.4 修改教师资料
- **接口**: `PUT /admin/teachers`
- **测试数据**:
```json
{
    "user_id": 2001,
    "full_name": "周嘉仪",
    "email": "new_zhou@edu.cn",
    "phone_number": "13711112222",
    "status": 1,
    "department": "信息工程学院",
    "title": "教授",
    "office_location": "教三楼 401A"
}
```

#### 6.5 删除教师
- **接口**: `DELETE /admin/teachers/2001,2002`
- **注意**: 支持批量删除

## 使用Postman或curl测试

### 示例：测试登录接口

**使用curl**:
```bash
curl -X POST http://localhost:8081/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "2021001001",
    "password": "123456"
  }'
```

**使用Postman**:
1. 方法: POST
2. URL: http://localhost:8081/login
3. Headers: Content-Type: application/json
4. Body: 选择raw，JSON格式，输入测试数据

## 常见问题

1. **数据库连接失败**
   - 检查 `application.properties` 中的数据库配置
   - 确保MySQL服务已启动
   - 确保数据库 `campus_system` 已创建

2. **JWT token无效**
   - 确保先调用登录接口获取token
   - 检查token是否过期（默认24小时）
   - 检查Header中token字段名是否正确

3. **分页查询返回空数据**
   - 确保数据库中有测试数据
   - 检查查询条件是否正确

4. **文件上传失败**
   - 检查上传目录权限
   - 检查文件大小是否超过限制（默认10MB）

## 运行单元测试

项目已包含单元测试类 `ApiTest.java`，可以运行以下命令执行测试：

```bash
mvn test
```

或使用IDE直接运行测试类。

