
-- =======================================================
-- 数据库：campus_system
-- 模块：用户与角色模块
-- =======================================================
CREATE DATABASE IF NOT EXISTS campus_system CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE campus_system;

-- =======================================================
-- 1. 核心用户表 t_user
-- 存储所有系统用户的统一登录信息，用于 JWT 认证。
-- =======================================================
CREATE TABLE `t_user` (
  `user_id` BIGINT AUTO_INCREMENT COMMENT '唯一的用户ID',
  `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '登录账号（学号/教工号/管理员账号），设为唯一',
  `password_hash` VARCHAR(255) NOT NULL COMMENT '加密后的密码哈希值',
  `full_name` VARCHAR(100) NOT NULL COMMENT '用户的真实姓名',
  `email` VARCHAR(100) UNIQUE DEFAULT NULL COMMENT '电子邮箱，设为唯一',
  `phone_number` VARCHAR(20) UNIQUE DEFAULT NULL COMMENT '手机号码，设为唯一',
  `avatar_url` VARCHAR(255) DEFAULT '/default_avatar.png' COMMENT '用户头像的URL',
  `status` TINYINT NOT NULL DEFAULT 1 COMMENT '账号状态 (1:激活, 2:已禁用/锁定)',
  `token_version` INT NOT NULL DEFAULT 1 COMMENT '(JWT认证用) 令牌版本号，修改密码或下线时递增',
  `last_login_at` TIMESTAMP NULL DEFAULT NULL COMMENT '最后登录时间',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '账号创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '账号信息最后更新时间',
  PRIMARY KEY (`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='核心用户表（系统统一登录信息）';


-- =======================================================
-- 2. 角色表 t_role
-- 定义系统中的所有角色。role_key 用于系统跳转判断。
-- =======================================================
CREATE TABLE `t_role` (
  `role_id` INT AUTO_INCREMENT COMMENT '唯一角色ID',
  `role_name` VARCHAR(50) NOT NULL COMMENT '角色名称 (如: 教师, 超级管理员)',
  `role_key` VARCHAR(100) NOT NULL UNIQUE COMMENT '关键字段 (如: student, super_admin, teaching_admin)，设为唯一',
  `description` VARCHAR(255) DEFAULT NULL COMMENT '角色描述',
  `is_system` TINYINT NOT NULL DEFAULT 0 COMMENT '是否为系统内置角色 (1:是, 0:否)',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`role_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='角色表（定义系统中所有角色）';


-- =======================================================
-- 3. 用户角色关联表 t_user_role
-- 关联用户和角色，实现多对多关系。
-- =======================================================
CREATE TABLE `t_user_role` (
  `id` BIGINT AUTO_INCREMENT COMMENT '关联ID',
  `user_id` BIGINT NOT NULL COMMENT '关联 t_user.user_id',
  `role_id` INT NOT NULL COMMENT '关联 t_role.role_id',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_role` (`user_id`, `role_id`),
  CONSTRAINT `fk_userrole_user` FOREIGN KEY (`user_id`) REFERENCES `t_user`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_userrole_role` FOREIGN KEY (`role_id`) REFERENCES `t_role`(`role_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户角色关联表（多对多关系）';


-- =======================================================
-- 4. 学生资料表 t_student_profile
-- 存储学生用户的扩展资料，与 t_user 一一对应。
-- =======================================================
CREATE TABLE `t_student_profile` (
  `user_id` BIGINT NOT NULL COMMENT '唯一用户ID，关联 t_user.user_id',
  `student_id_number` VARCHAR(50) NOT NULL UNIQUE COMMENT '学生的官方学号，设为唯一',
  `gender` TINYINT DEFAULT 0 COMMENT '性别 (0:未知, 1:男, 2:女)',
  `date_of_birth` DATE DEFAULT NULL COMMENT '出生日期',
  `major` VARCHAR(100) DEFAULT NULL COMMENT '专业',
  `class_name` VARCHAR(100) DEFAULT NULL COMMENT '班级 (如: "计算机2101班")',
  `grade` VARCHAR(10) DEFAULT NULL COMMENT '年级 (如: "2021级")',
  `enrollment_date` DATE DEFAULT NULL COMMENT '入学日期',
  `current_dorm_id` BIGINT DEFAULT NULL COMMENT '(可扩展) 关联 t_dorm_room.room_id',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_student_user` FOREIGN KEY (`user_id`) REFERENCES `t_user`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生资料表（扩展学生信息）';


-- =======================================================
-- 5. 教师资料表 t_teacher_profile
-- 存储教师用户的扩展资料，与 t_user 一一对应。
-- =======================================================
CREATE TABLE `t_teacher_profile` (
  `user_id` BIGINT NOT NULL COMMENT '唯一用户ID，关联 t_user.user_id',
  `teacher_id_number` VARCHAR(50) NOT NULL UNIQUE COMMENT '教师的官方教工号，设为唯一',
  `gender` TINYINT DEFAULT 0 COMMENT '性别 (0:未知, 1:男, 2:女)',
  `department` VARCHAR(100) DEFAULT NULL COMMENT '所属院系',
  `title` VARCHAR(50) DEFAULT NULL COMMENT '职称',
  `office_location` VARCHAR(100) DEFAULT NULL COMMENT '办公室地点',
  `research_area` TEXT DEFAULT NULL COMMENT '(可扩展) 研究方向',
  `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`user_id`),
  CONSTRAINT `fk_teacher_user` FOREIGN KEY (`user_id`) REFERENCES `t_user`(`user_id`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教师资料表（扩展教师信息）';

-- =========================================
-- 1.1 院系表
-- =========================================
CREATE TABLE t_department (
    department_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '院系唯一ID',
    department_name VARCHAR(100) NOT NULL COMMENT '院系名称 (如: 信息工程学院)',
    description TEXT NULL COMMENT '院系描述',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='院系表';


-- =========================================
-- 1.2 课程模板表
-- =========================================
CREATE TABLE t_course (
    course_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课程模板唯一ID',
    course_code VARCHAR(50) NOT NULL UNIQUE COMMENT '课程代码 (如: CS101)',
    course_name VARCHAR(100) NOT NULL COMMENT '课程名称',
    department_id INT NULL COMMENT '关联 t_department.department_id',
    credits DECIMAL(3,1) NOT NULL DEFAULT 0 COMMENT '课程学分',
    description TEXT NULL COMMENT '课程描述',
    created_by BIGINT NOT NULL COMMENT '创建人 (t_user.user_id)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (department_id) REFERENCES t_department(department_id),
    FOREIGN KEY (created_by) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程模板表';


-- =========================================
-- 1.3 开课班级表
-- =========================================
CREATE TABLE t_class (
    class_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '班级唯一ID (教学班ID)',
    course_id BIGINT NOT NULL COMMENT '关联 t_course.course_id',
    teacher_id BIGINT NOT NULL COMMENT '授课教师 (t_user.user_id)',
    semester VARCHAR(50) NOT NULL COMMENT '学期 (如: 2025-Fall)',
    max_capacity INT NOT NULL DEFAULT 100 COMMENT '最大选课人数',
    current_enrollment INT NOT NULL DEFAULT 0 COMMENT '当前已选人数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '班级状态 (1:待开放, 2:选课中, 3:授课中, 4:已结课)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (course_id) REFERENCES t_course(course_id),
    FOREIGN KEY (teacher_id) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='开课班级表';

-- =========================================
-- 1. 教室模块
-- =========================================

-- 1.1 教学楼表
CREATE TABLE t_building (
    building_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '教学楼唯一ID',
    building_name VARCHAR(100) NOT NULL COMMENT '教学楼名称 (如: 教三楼)',
    location VARCHAR(255) DEFAULT NULL COMMENT '教学楼地址',
    is_active TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用 (1:启用, 0:停用)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教学楼表 (用于组织教室)';


-- 1.2 教室信息表
CREATE TABLE t_classroom (
    room_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '教室唯一ID',
    room_number VARCHAR(50) NOT NULL COMMENT '教室编号 (如: 301)',
    building_id INT NOT NULL COMMENT '关联 t_building.building_id',
    full_name VARCHAR(150) NOT NULL UNIQUE COMMENT '教室全称 (如: 教三楼301)',
    capacity INT NOT NULL DEFAULT 0 COMMENT '教室容纳人数',
    room_type VARCHAR(50) DEFAULT NULL COMMENT '教室类型 (如: 阶梯教室, 多媒体教室)',
    equipment TEXT DEFAULT NULL COMMENT '教室设备列表 (如: 投影仪, 电脑)',
    is_available TINYINT NOT NULL DEFAULT 1 COMMENT '是否对外开放预约 (1:开放, 0:停用/维修)',
    last_maintenance_date DATE DEFAULT NULL COMMENT '上次维护日期',
    FOREIGN KEY (building_id) REFERENCES t_building(building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室信息表 (设置、显示与停用教室)';


-- =========================================
-- 1.3 教室预约申请表
-- =========================================
CREATE TABLE t_reservation_request (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '预约申请ID',
    room_id BIGINT NOT NULL COMMENT '关联 t_classroom.room_id',
    requester_id BIGINT NOT NULL COMMENT '申请人ID (t_user.user_id)',
    start_time TIMESTAMP NOT NULL COMMENT '预约开始时间',
    end_time TIMESTAMP NOT NULL COMMENT '预约结束时间',
    purpose VARCHAR(255) NOT NULL COMMENT '预约用途',
    participants_count INT DEFAULT NULL COMMENT '参与人数',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 (1:待审批, 2:已批准, 3:已驳回, 4:已撤回)',
    approver_id BIGINT DEFAULT NULL COMMENT '审批人ID (t_user.user_id)',
    approval_remarks TEXT DEFAULT NULL COMMENT '审批意见',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    FOREIGN KEY (room_id) REFERENCES t_classroom(room_id),
    FOREIGN KEY (requester_id) REFERENCES t_user(user_id),
    FOREIGN KEY (approver_id) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='教室预约申请表';


-- =========================================
-- 1.4 课表时间地点表
-- =========================================
CREATE TABLE t_class_schedule (
    schedule_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '课表条目ID',
    class_id BIGINT NOT NULL COMMENT '关联 t_class.class_id',
    location_text VARCHAR(100) NOT NULL COMMENT '上课地点 (如: 教3-101)',
    classroom_id BIGINT NULL COMMENT '关联 t_classroom.room_id (可扩展)',
    day_of_week TINYINT NOT NULL COMMENT '星期几 (1=周一, 7=周日)',
    start_time TIME NOT NULL COMMENT '上课时间 (如: 08:00:00)',
    end_time TIME NOT NULL COMMENT '下课时间 (如: 09:40:00)',
    week_range VARCHAR(100) NOT NULL DEFAULT '1-16' COMMENT '上课周次 (如: 1-16 或 1,3,5-10)',
    FOREIGN KEY (class_id) REFERENCES t_class(class_id),
    FOREIGN KEY (classroom_id) REFERENCES t_classroom(room_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课表时间地点表';


-- =========================================
-- 1.5 学生选课表
-- =========================================
CREATE TABLE t_student_class (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '选课记录ID',
    student_id BIGINT NOT NULL COMMENT '关联 t_user.user_id (学生)',
    class_id BIGINT NOT NULL COMMENT '关联 t_class.class_id',
    selection_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '选课时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '选课状态 (1:已选, 2:已退课)',
    final_score DECIMAL(5,2) NULL COMMENT '最终成绩',
    UNIQUE KEY uq_student_class (student_id, class_id),
    FOREIGN KEY (student_id) REFERENCES t_user(user_id),
    FOREIGN KEY (class_id) REFERENCES t_class(class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生选课表';

-- =========================================
-- 2.1 成绩组成项表
-- =========================================
CREATE TABLE t_grade_component (
    component_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '成绩项ID',
    class_id BIGINT NOT NULL COMMENT '关联 t_class.class_id',
    component_name VARCHAR(100) NOT NULL COMMENT '成绩项名称 (如: 期中考试, 平时作业)',
    weight DECIMAL(5,4) NOT NULL DEFAULT 0 COMMENT '权重 (如: 0.3000 代表30%)',
    max_score DECIMAL(5,2) NOT NULL DEFAULT 100 COMMENT '该项满分',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (class_id) REFERENCES t_class(class_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='成绩组成项表';


-- =========================================
-- 2.2 学生成绩表 (单项成绩)
-- =========================================
CREATE TABLE t_student_grade (
    grade_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '单项成绩ID',
    student_id BIGINT NOT NULL COMMENT '关联 t_user.user_id (学生)',
    component_id BIGINT NOT NULL COMMENT '关联 t_grade_component.component_id',
    score DECIMAL(5,2) NULL COMMENT '学生得分',
    recorded_by BIGINT NOT NULL COMMENT '录入教师 (t_user.user_id)',
    recorded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
    UNIQUE KEY uq_student_component (student_id, component_id),
    FOREIGN KEY (student_id) REFERENCES t_user(user_id),
    FOREIGN KEY (component_id) REFERENCES t_grade_component(component_id),
    FOREIGN KEY (recorded_by) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生单项成绩表';

-- =========================================
-- 3. 作业模块
-- =========================================

-- 3.1 作业发布表
CREATE TABLE t_homework (
    homework_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '作业ID',
    class_id BIGINT NOT NULL COMMENT '关联 t_class.class_id',
    created_by BIGINT NOT NULL COMMENT '发布教师 (t_user.user_id)',
    title VARCHAR(255) NOT NULL COMMENT '作业标题',
    description TEXT NULL COMMENT '作业详细描述',
    due_date TIMESTAMP NOT NULL COMMENT '截止日期',
    allow_late_submission TINYINT NOT NULL DEFAULT 0 COMMENT '是否允许迟交 (1:是, 0:否)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
    FOREIGN KEY (class_id) REFERENCES t_class(class_id),
    FOREIGN KEY (created_by) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业发布表';


-- 3.2 作业提交表
CREATE TABLE t_homework_submission (
    submission_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '提交ID',
    homework_id BIGINT NOT NULL COMMENT '关联 t_homework.homework_id',
    student_id BIGINT NOT NULL COMMENT '关联 t_user.user_id (学生)',
    content TEXT NULL COMMENT '学生提交内容 (可扩展)',
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '提交状态 (1:准时提交, 2:迟交, 3:未提交)',
    grade DECIMAL(5,2) NULL COMMENT '作业得分',
    teacher_feedback TEXT NULL COMMENT '教师评语',
    UNIQUE KEY uq_homework_student (homework_id, student_id),
    FOREIGN KEY (homework_id) REFERENCES t_homework(homework_id),
    FOREIGN KEY (student_id) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业提交表';


-- 3.3 作业附件表
CREATE TABLE t_submission_file (
    file_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文件ID',
    submission_id BIGINT NOT NULL COMMENT '关联 t_homework_submission.submission_id',
    file_name VARCHAR(255) NOT NULL COMMENT '文件原始名称',
    file_url VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    file_size BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小 (Bytes)',
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    FOREIGN KEY (submission_id) REFERENCES t_homework_submission(submission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='作业附件表';


-- 3.4 AI作业分析表
CREATE TABLE t_submission_analysis (
    analysis_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '分析ID',
    submission_id BIGINT NOT NULL COMMENT '关联 t_homework_submission.submission_id',
    student_ac_report TEXT NULL COMMENT '学生学情分析报告 (JSON格式)',
    analysis_status TINYINT NOT NULL DEFAULT 1 COMMENT '分析状态 (1:待分析, 2:分析中, 3:已完成, 4:失败)',
    generated_at TIMESTAMP NULL COMMENT '分析完成时间',
    FOREIGN KEY (submission_id) REFERENCES t_homework_submission(submission_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI作业分析表';


-- 3.5 AI教学建议表
CREATE TABLE t_teaching_suggestion (
    suggestion_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '建议ID',
    class_id BIGINT NOT NULL COMMENT '关联 t_class.class_id',
    homework_id BIGINT NULL COMMENT '可选，关联 t_homework.homework_id',
    suggestion_content TEXT NOT NULL COMMENT 'AI生成的教学建议内容',
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '生成时间',
    FOREIGN KEY (class_id) REFERENCES t_class(class_id),
    FOREIGN KEY (homework_id) REFERENCES t_homework(homework_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='AI教学建议表';


-- =========================================
-- 4. 考勤模块
-- =========================================

-- 4.1 考勤任务表
CREATE TABLE t_attendance_session (
    session_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考勤任务ID',
    class_id BIGINT NOT NULL COMMENT '关联 t_class.class_id',
    created_by BIGINT NOT NULL COMMENT '发起教师 (t_user.user_id)',
    session_type TINYINT NOT NULL DEFAULT 3 COMMENT '签到类型 (1:人脸, 2:定位, 3:人脸+定位)',
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '签到开始时间',
    end_time TIMESTAMP NOT NULL COMMENT '签到截止时间',
    required_latitude DECIMAL(10,8) NULL COMMENT '要求的纬度',
    required_longitude DECIMAL(11,8) NULL COMMENT '要求的经度',
    required_radius INT NULL DEFAULT 100 COMMENT '允许的半径 (米)',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '状态 (1:进行中, 2:已结束)',
    FOREIGN KEY (class_id) REFERENCES t_class(class_id),
    FOREIGN KEY (created_by) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='考勤任务表';


-- 4.2 学生考勤记录表
CREATE TABLE t_attendance_record (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '考勤记录ID',
    session_id BIGINT NOT NULL COMMENT '关联 t_attendance_session.session_id',
    student_id BIGINT NOT NULL COMMENT '关联 t_user.user_id (学生)',
    status TINYINT NOT NULL DEFAULT 2 COMMENT '考勤状态 (1:出勤, 2:缺勤, 3:迟到, 4:请假)',
    check_in_time TIMESTAMP NULL COMMENT '实际签到时间',
    check_in_latitude DECIMAL(10,8) NULL COMMENT '签到纬度',
    check_in_longitude DECIMAL(11,8) NULL COMMENT '签到经度',
    face_api_score DECIMAL(5,4) NULL COMMENT '人脸识别得分',
    remarks VARCHAR(255) NULL COMMENT '备注',
    updated_by BIGINT NULL COMMENT '修改人 (t_user.user_id)',
    UNIQUE KEY uq_session_student (session_id, student_id),
    FOREIGN KEY (session_id) REFERENCES t_attendance_session(session_id),
    FOREIGN KEY (student_id) REFERENCES t_user(user_id),
    FOREIGN KEY (updated_by) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='学生考勤记录表';


-- 4.3 请假申请表
CREATE TABLE t_leave_request (
    request_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '请假申请ID',
    student_id BIGINT NOT NULL COMMENT '申请学生 (t_user.user_id)',
    class_id BIGINT NULL COMMENT '请假对应课程 (t_class.class_id)',
    reason TEXT NOT NULL COMMENT '请假理由',
    start_time TIMESTAMP NOT NULL COMMENT '请假开始时间',
    end_time TIMESTAMP NOT NULL COMMENT '请假结束时间',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '申请状态 (1:待审批, 2:已批准, 3:已驳回)',
    approver_id BIGINT NULL COMMENT '审批人 (t_user.user_id)',
    approval_remarks TEXT NULL COMMENT '审批意见',
    submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    FOREIGN KEY (student_id) REFERENCES t_user(user_id),
    FOREIGN KEY (class_id) REFERENCES t_class(class_id),
    FOREIGN KEY (approver_id) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='请假申请表';


-- =========================================
-- 1. 宿舍模块
-- =========================================

-- 1.1 宿舍楼表
CREATE TABLE t_dorm_building (
    building_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '宿舍楼唯一ID',
    building_name VARCHAR(100) NOT NULL COMMENT '宿舍楼名称 (如: 西一舍, 竹园)',
    gender_limit TINYINT NOT NULL DEFAULT 0 COMMENT '性别限制 (1:男, 2:女, 0:不限)',
    manager_id BIGINT DEFAULT NULL COMMENT '楼栋管理员 (t_user.user_id)',
    is_active TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用 (1:启用, 0:停用/维修)',
    FOREIGN KEY (manager_id) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍楼表 (用于组织宿舍房间)';


-- 1.2 宿舍房间表
CREATE TABLE t_dorm_room (
    room_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '房间唯一ID',
    building_id INT NOT NULL COMMENT '关联 t_dorm_building.building_id',
    room_number VARCHAR(50) NOT NULL COMMENT '房间编号 (如: 405)',
    full_name VARCHAR(150) NOT NULL UNIQUE COMMENT '房间全称 (如: 西一舍405)',
    total_beds TINYINT NOT NULL DEFAULT 4 COMMENT '房间总床位数',
    occupied_beds TINYINT NOT NULL DEFAULT 0 COMMENT '已占用床位数 (冗余字段)',
    room_status TINYINT NOT NULL DEFAULT 1 COMMENT '房间状态 (1:正常, 2:维修, 3:已满)',
    room_type VARCHAR(50) DEFAULT NULL COMMENT '房间类型 (如: 四人间, 六人间)',
    has_private_bathroom TINYINT NOT NULL DEFAULT 0 COMMENT '是否有独立卫生间 (1:是, 0:否)',
    FOREIGN KEY (building_id) REFERENCES t_dorm_building(building_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍房间表 (宿舍管理)';


-- 1.3 宿舍床位表
CREATE TABLE t_dorm_bed (
    bed_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '床位唯一ID',
    room_id BIGINT NOT NULL COMMENT '关联 t_dorm_room.room_id',
    bed_number VARCHAR(10) NOT NULL COMMENT '床位编号 (如: A, B 或 1, 2)',
    bed_status TINYINT NOT NULL DEFAULT 1 COMMENT '床位状态 (1:空闲, 2:已分配, 3:禁用)',
    current_resident_id BIGINT DEFAULT NULL COMMENT '当前住户学生ID (t_user.user_id)',
    allocation_time TIMESTAMP DEFAULT NULL COMMENT '分配时间',
    FOREIGN KEY (room_id) REFERENCES t_dorm_room(room_id),
    FOREIGN KEY (current_resident_id) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍床位表 (宿舍分配与空余位置查看)';


-- 1.4 宿舍状态记录表
CREATE TABLE t_dorm_status_record (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '状态记录ID',
    student_id BIGINT NOT NULL COMMENT '学生ID (t_user.user_id)',
    status_type TINYINT NOT NULL DEFAULT 1 COMMENT '状态类型 (1:在校, 2:离校, 3:晚归/夜不归宿)',
    start_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '状态开始时间',
    end_time TIMESTAMP DEFAULT NULL COMMENT '状态结束时间',
    reason VARCHAR(255) DEFAULT NULL COMMENT '离校或状态原因',
    operator_id BIGINT NOT NULL COMMENT '操作人 (t_user.user_id)',
    FOREIGN KEY (student_id) REFERENCES t_user(user_id),
    FOREIGN KEY (operator_id) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍状态设置与在校记录表';


-- 1.5 宿舍问卷表
CREATE TABLE t_dorm_survey (
    survey_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '问卷ID',
    student_id BIGINT NOT NULL COMMENT '学生ID (t_user.user_id)',
    wake_up_time TIME DEFAULT NULL COMMENT '起床时间',
    sleep_time TIME DEFAULT NULL COMMENT '睡觉时间',
    cleanliness_preference TINYINT DEFAULT NULL COMMENT '卫生偏好 (1:严格, 2:一般, 3:随意)',
    noise_tolerance TINYINT DEFAULT NULL COMMENT '噪音容忍度',
    submission_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '问卷提交时间',
    matching_score DECIMAL(5,2) DEFAULT NULL COMMENT '问卷匹配得分',
    FOREIGN KEY (student_id) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍问卷表 (收集住宿偏好信息)';


-- 1.6 宿舍偏好表
CREATE TABLE t_dorm_preference (
    preference_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '宿舍偏好ID',
    student_id BIGINT NOT NULL COMMENT '学生ID (t_user.user_id)',
    wake_up_time TIME NOT NULL COMMENT '起床时间',
    bedtime TIME NOT NULL COMMENT '睡觉时间',
    study_habit_preference TINYINT DEFAULT NULL COMMENT '学习习惯偏好',
    hygiene_preference TINYINT DEFAULT NULL COMMENT '卫生偏好',
    noise_tolerance TINYINT DEFAULT NULL COMMENT '噪音容忍度',
    self_introduction TEXT DEFAULT NULL COMMENT '自我介绍',
    submission_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '提交时间',
    matching_score DECIMAL(5,2) DEFAULT NULL COMMENT '匹配得分',
    FOREIGN KEY (student_id) REFERENCES t_user(user_id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='宿舍偏好问卷表';


-- =========================================
-- 5. 课程资源模块
-- =========================================

-- 5.1 资源文件夹表
CREATE TABLE t_resource_folder (
    folder_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '文件夹ID',
    class_id BIGINT NOT NULL COMMENT '关联 t_class.class_id',
    folder_name VARCHAR(100) NOT NULL COMMENT '文件夹名称',
    parent_folder_id BIGINT NULL COMMENT '自关联父文件夹ID',
    created_by BIGINT NOT NULL COMMENT '创建人 (t_user.user_id)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    FOREIGN KEY (class_id) REFERENCES t_class(class_id),
    FOREIGN KEY (parent_folder_id) REFERENCES t_resource_folder(folder_id),
    FOREIGN KEY (created_by) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='资源文件夹表';


-- 5.2 课程资源表
CREATE TABLE t_course_resource (
    resource_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '资源ID',
    class_id BIGINT NOT NULL COMMENT '关联 t_class.class_id',
    folder_id BIGINT NULL COMMENT '所属文件夹 (t_resource_folder.folder_id)',
    uploader_id BIGINT NOT NULL COMMENT '上传者 (t_user.user_id)',
    file_name VARCHAR(255) NOT NULL COMMENT '文件名称',
    file_url VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    file_type VARCHAR(50) NULL COMMENT '文件类型 (如: pdf, mp4)',
    file_size BIGINT NOT NULL DEFAULT 0 COMMENT '文件大小 (Bytes)',
    description VARCHAR(500) NULL COMMENT '资源描述',
    upload_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    download_count INT NOT NULL DEFAULT 0 COMMENT '下载次数统计',
    FOREIGN KEY (class_id) REFERENCES t_class(class_id),
    FOREIGN KEY (folder_id) REFERENCES t_resource_folder(folder_id),
    FOREIGN KEY (uploader_id) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='课程资源表';



-- =========================================
-- 2. 图书馆模块
-- =========================================

-- 2.1 书籍信息表
CREATE TABLE t_book (
    book_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '书籍唯一ID',
    isbn VARCHAR(20) NOT NULL UNIQUE COMMENT '国际标准书号',
    title VARCHAR(255) NOT NULL COMMENT '书名',
    author VARCHAR(255) NOT NULL COMMENT '作者',
    publisher VARCHAR(100) DEFAULT NULL COMMENT '出版社',
    publication_year YEAR DEFAULT NULL COMMENT '出版年份',
    total_copies INT NOT NULL DEFAULT 0 COMMENT '总副本数',
    available_copies INT NOT NULL DEFAULT 0 COMMENT '当前可用副本数',
    cover_url VARCHAR(500) DEFAULT NULL COMMENT '封面图片URL',
    summary TEXT DEFAULT NULL COMMENT '内容摘要',
    uploader_id BIGINT NOT NULL COMMENT '录入人 (t_user.user_id)',
    uploaded_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
    FOREIGN KEY (uploader_id) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍信息表 (上传书籍)';


-- 2.2 书籍标签表
CREATE TABLE t_tag (
    tag_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '标签ID',
    tag_name VARCHAR(50) NOT NULL UNIQUE COMMENT '标签名称 (如: 计算机科学, 小说)',
    description VARCHAR(255) DEFAULT NULL COMMENT '标签描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍标签表 (添加书籍标签)';


-- 2.3 书籍与标签关联表
CREATE TABLE t_book_tag (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '关联ID',
    book_id BIGINT NOT NULL COMMENT '关联 t_book.book_id',
    tag_id INT NOT NULL COMMENT '关联 t_tag.tag_id',
    FOREIGN KEY (book_id) REFERENCES t_book(book_id),
    FOREIGN KEY (tag_id) REFERENCES t_tag(tag_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍与标签关联表';


-- 2.4 借阅策略表
CREATE TABLE t_book_policy (
    policy_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '策略ID',
    policy_name VARCHAR(100) NOT NULL COMMENT '策略名称 (如: 学生借阅规则)',
    max_duration_days INT NOT NULL DEFAULT 30 COMMENT '最大借阅时长 (天)',
    max_borrow_count INT NOT NULL DEFAULT 10 COMMENT '最大可借阅数量',
    allow_renewal_count INT NOT NULL DEFAULT 1 COMMENT '允许续借次数',
    user_type_scope TINYINT DEFAULT NULL COMMENT '适用用户类型 (1:学生, 2:教师, NULL:所有)'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅策略表 (设置借书时长)';


-- 2.5 图书借阅记录表
CREATE TABLE t_borrow_record (
    record_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '借阅记录ID',
    book_id BIGINT NOT NULL COMMENT '关联 t_book.book_id',
    borrower_id BIGINT NOT NULL COMMENT '借阅人 (t_user.user_id)',
    policy_id INT DEFAULT NULL COMMENT '借阅策略ID (t_book_policy.policy_id)',
    borrow_date DATE NOT NULL COMMENT '借出日期',
    due_date DATE NOT NULL COMMENT '应还日期',
    return_date DATE DEFAULT NULL COMMENT '实际归还日期',
    status TINYINT NOT NULL DEFAULT 1 COMMENT '借阅状态 (1:借阅中, 2:已归还, 3:逾期未还)',
    renewal_count INT NOT NULL DEFAULT 0 COMMENT '已续借次数',
    fine_amount DECIMAL(8,2) NOT NULL DEFAULT 0 COMMENT '罚款金额',
    FOREIGN KEY (book_id) REFERENCES t_book(book_id),
    FOREIGN KEY (borrower_id) REFERENCES t_user(user_id),
    FOREIGN KEY (policy_id) REFERENCES t_book_policy(policy_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书借阅记录表 (借阅书籍)';


-- 2.6 书籍偏好/推荐表
CREATE TABLE t_book_preference (
    id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '记录ID',
    user_id BIGINT NOT NULL COMMENT '关联 t_user.user_id (用户)',
    recommended_book_id BIGINT NOT NULL COMMENT '推荐书籍ID (t_book.book_id)',
    reason VARCHAR(100) DEFAULT NULL COMMENT '推荐原因 (如: 基于您借阅《A书》)',
    score DECIMAL(5,2) DEFAULT NULL COMMENT '推荐得分/相似度',
    generated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '推荐生成时间',
    FOREIGN KEY (user_id) REFERENCES t_user(user_id),
    FOREIGN KEY (recommended_book_id) REFERENCES t_book(book_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='书籍偏好/推荐表 (推送偏好书籍)';


-- =========================================
-- 2. 交流平台模块
-- =========================================

-- 2.1 论坛板块表
CREATE TABLE t_forum_category (
    category_id INT AUTO_INCREMENT PRIMARY KEY COMMENT '板块ID',
    category_name VARCHAR(100) NOT NULL COMMENT '板块名称 (如: 失物招领, 学术交流)',
    description VARCHAR(255) DEFAULT NULL COMMENT '板块描述',
    is_active TINYINT NOT NULL DEFAULT 1 COMMENT '是否启用'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛板块表 (用于划分论坛主题)';


-- 2.2 论坛帖子表
CREATE TABLE t_forum_post (
    post_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '帖子ID',
    category_id INT NOT NULL COMMENT '关联 t_forum_category.category_id',
    author_id BIGINT NOT NULL COMMENT '发帖人 (t_user.user_id)',
    title VARCHAR(255) NOT NULL COMMENT '帖子标题',
    content TEXT NOT NULL COMMENT '帖子内容',
    post_status TINYINT NOT NULL DEFAULT 1 COMMENT '帖子状态 (1:正常, 2:已删除/隐藏)',
    is_top TINYINT NOT NULL DEFAULT 0 COMMENT '是否置顶 (1:是, 0:否)',
    view_count INT NOT NULL DEFAULT 0 COMMENT '浏览次数',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '发帖时间',
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后修改时间',
    deleted_by BIGINT DEFAULT NULL COMMENT '删除操作人 (t_user.user_id)',
    FOREIGN KEY (category_id) REFERENCES t_forum_category(category_id),
    FOREIGN KEY (author_id) REFERENCES t_user(user_id),
    FOREIGN KEY (deleted_by) REFERENCES t_user(user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛帖子表 (发帖与删帖)';


-- 2.3 论坛评论表
CREATE TABLE t_forum_comment (
    comment_id BIGINT AUTO_INCREMENT PRIMARY KEY COMMENT '评论ID',
    post_id BIGINT NOT NULL COMMENT '关联 t_forum_post.post_id',
    author_id BIGINT NOT NULL COMMENT '评论人 (t_user.user_id)',
    parent_comment_id BIGINT DEFAULT NULL COMMENT '父评论ID (楼中楼)',
    content TEXT NOT NULL COMMENT '评论内容',
    is_deleted TINYINT NOT NULL DEFAULT 0 COMMENT '是否被删除 (1:是, 0:否)',
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '评论时间',
    FOREIGN KEY (post_id) REFERENCES t_forum_post(post_id),
    FOREIGN KEY (author_id) REFERENCES t_user(user_id),
    FOREIGN KEY (parent_comment_id) REFERENCES t_forum_comment(comment_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='论坛评论表 (评论与回复)';