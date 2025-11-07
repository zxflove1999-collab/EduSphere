-- =======================================================
--  38 个表
-- =======================================================

USE campus_system;

-- 禁用外键检查，以便导入数据时跳过顺序依赖
SET FOREIGN_KEY_CHECKS = 0;


-- =======================================================
-- 模块 I: 基础配置与清空 (SETUP & CLEANUP)
-- =======================================================

-- 1.1 清理所有数据 (安全起见，可选执行)
TRUNCATE TABLE t_forum_comment; TRUNCATE TABLE t_forum_post; TRUNCATE TABLE t_forum_category;
TRUNCATE TABLE t_book_preference; TRUNCATE TABLE t_borrow_record; TRUNCATE TABLE t_book_tag;
TRUNCATE TABLE t_tag; TRUNCATE TABLE t_book_policy; TRUNCATE TABLE t_book;
TRUNCATE TABLE t_course_resource; TRUNCATE TABLE t_resource_folder;
TRUNCATE TABLE t_dorm_survey; TRUNCATE TABLE t_dorm_status_record; TRUNCATE TABLE t_dorm_bed;
TRUNCATE TABLE t_dorm_room; TRUNCATE TABLE t_dorm_building;
TRUNCATE TABLE t_leave_request; TRUNCATE TABLE t_attendance_record; TRUNCATE TABLE t_attendance_session;
TRUNCATE TABLE t_teaching_suggestion; TRUNCATE TABLE t_submission_analysis; TRUNCATE TABLE t_submission_file;
TRUNCATE TABLE t_homework_submission; TRUNCATE TABLE t_homework;
TRUNCATE TABLE t_student_grade; TRUNCATE TABLE t_grade_component;
TRUNCATE TABLE t_student_class; TRUNCATE TABLE t_class_schedule; TRUNCATE TABLE t_classroom;
TRUNCATE TABLE t_building; TRUNCATE TABLE t_class; TRUNCATE TABLE t_course; TRUNCATE TABLE t_department;
TRUNCATE TABLE t_teacher_profile; TRUNCATE TABLE t_student_profile; TRUNCATE TABLE t_user_role;
TRUNCATE TABLE t_user; TRUNCATE TABLE t_role;

-- 1.2 插入核心角色 (t_role) - 3 条
INSERT INTO `t_role` (`role_id`, `role_name`, `role_key`, `description`, `is_system`) VALUES
(1, '超级管理员', 'super_admin', '系统最高权限管理员', 1),
(2, '教师', 'teacher', '授课教师及教职工', 1),
(3, '学生', 'student', '在校学生', 1);

-- 1.3 插入院系数据 (t_department) - 5 条
INSERT INTO `t_department` (`department_id`, `department_name`) VALUES
(1, '信息工程学院'), (2, '经济管理学院'), (3, '人文社会科学学院'), (4, '外国语学院'), (5, '环境与能源学院');

-- 1.4 插入教学楼数据 (t_building) - 3 条
INSERT INTO `t_building` (`building_id`, `building_name`) VALUES
(1, '教三楼'), (2, '实验楼'), (3, '文科楼');


-- =======================================================
-- 模块 II: 核心用户与身份 (CORE USERS & PROFILES) - 150 条记录
-- =======================================================

-- 2.1 插入核心用户 (t_user) - 150 条 (5 Admin, 25 Teacher, 120 Student)
-- 管理员 (1001-1005)
INSERT INTO `t_user` (`user_id`, `username`, `password_hash`, `full_name`, `email`, `phone_number`) VALUES
(1001, 'admin01', 'hash_admin_1', '超级管理员', 'admin1@sys.com', '13000000001'),
(1002, 'dean01', 'hash_dean_1', '教务管理员', 'dean1@sys.com', '13000000002'),
(1003, 'dorm_mgr01', 'hash_dorm_1', '宿舍管理员', 'dorm1@sys.com', '13000000003'),
(1004, 'lib_mgr01', 'hash_lib_1', '图书管理员', 'lib1@sys.com', '13000000004'),
(1005, 'net_mgr01', 'hash_net_1', '网络管理员', 'net1@sys.com', '13000000005');

-- 教师 (2001-2025) - 25 条
INSERT INTO `t_user` (`user_id`, `username`, `password_hash`, `full_name`, `email`, `phone_number`)
SELECT
    id + 2000 AS user_id,
    CONCAT('T', LPAD(id, 4, '0')) AS username,
    CONCAT('hash_t_', id) AS password_hash,
    CONCAT('教师_', LPAD(id, 2, '0')) AS full_name,
    CONCAT('t', id, '@edu.cn') AS email,
    CONCAT('131', LPAD(id * 111 + 1000, 7, '0')) AS phone_number
FROM (
    SELECT 1 AS id UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
    UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20
    UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25
) AS T;

-- 学生 (3001-3120) - 120 条
INSERT INTO `t_user` (`user_id`, `username`, `password_hash`, `full_name`, `email`, `phone_number`)
SELECT
    T.id + 3000 AS user_id,
    CONCAT('S', LPAD(T.id, 4, '0')) AS username,
    CONCAT('hash_s_', T.id) AS password_hash,
    CONCAT('学生_', LPAD(T.id, 3, '0')) AS full_name,
    CONCAT('s', T.id, '@std.cn') AS email,
    CONCAT('139', LPAD(T.id * 99 + 5000, 7, '0')) AS phone_number
FROM (
    SELECT (a.id + b.id * 10 + c.id * 100) AS id FROM
    (SELECT 1 AS id UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 0) AS a
    JOIN (SELECT 0 AS id UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) AS b
    JOIN (SELECT 0 AS id UNION ALL SELECT 1) AS c
    WHERE (a.id + b.id * 10 + c.id * 100) BETWEEN 1 AND 120
) AS T;

-- 2.2 用户角色关联表 (t_user_role) - 150 条
INSERT INTO `t_user_role` (`user_id`, `role_id`)
SELECT user_id, 1 FROM t_user WHERE user_id BETWEEN 1001 AND 1005
UNION ALL
SELECT user_id, 2 FROM t_user WHERE user_id BETWEEN 2001 AND 2025
UNION ALL
SELECT user_id, 3 FROM t_user WHERE user_id BETWEEN 3001 AND 3120;

-- 2.3 教师资料表 (t_teacher_profile) - 25 条
INSERT INTO `t_teacher_profile` (`user_id`, `teacher_id_number`, `gender`, `department`, `title`)
SELECT
    u.user_id,
    u.username,
    (u.user_id % 2) + 1 AS gender,
    CASE WHEN u.user_id % 3 = 0 THEN '信息工程学院' WHEN u.user_id % 3 = 1 THEN '经济管理学院' ELSE '人文社会科学学院' END AS department,
    CASE WHEN u.user_id % 5 = 0 THEN '教授' WHEN u.user_id % 5 = 1 THEN '副教授' ELSE '讲师' END AS title
FROM t_user u WHERE u.user_id BETWEEN 2001 AND 2025;

-- 2.4 学生资料表 (t_student_profile) - 120 条
INSERT INTO `t_student_profile` (`user_id`, `student_id_number`, `gender`, `major`, `class_name`, `grade`, `enrollment_date`)
SELECT
    u.user_id,
    CONCAT('2023', LPAD(u.user_id - 3000, 4, '0')),
    (u.user_id % 2) + 1 AS gender,
    CASE
        WHEN u.user_id % 4 = 0 THEN '计算机科学与技术'
        WHEN u.user_id % 4 = 1 THEN '软件工程'
        WHEN u.user_id % 4 = 2 THEN '金融学'
        ELSE '英语专业'
    END AS major,
    CONCAT('班级', LPAD(FLOOR((u.user_id - 3001) / 30) + 1, 2, '0')) AS class_name,
    '2023级',
    '2023-09-01'
FROM t_user u WHERE u.user_id BETWEEN 3001 AND 3120;


-- =======================================================
-- 模块 III: 教学配置与排课 (COURSE CONFIG & CLASSROOM)
-- =======================================================

-- 3.1 教室信息表 (t_classroom) - 4 条 (依赖 t_building)
INSERT INTO `t_classroom` (`room_id`, `room_number`, `building_id`, `full_name`, `capacity`, `room_type`) VALUES
(101, '301', 1, '教三楼301', 100, '多媒体教室'), (102, '405', 1, '教三楼405', 60, '普通教室'),
(201, '101', 2, '实验楼101', 50, '计算机机房'), (301, '205', 3, '文科楼205', 80, '阶梯教室');

-- 3.2 课程模板表 (t_course) - 20 条 (依赖 t_department, t_user)
INSERT INTO `t_course` (`course_id`, `course_code`, `course_name`, `department_id`, `credits`, `created_by`)
SELECT
    T.id + 100 AS course_id,
    CONCAT('C', LPAD(T.id, 3, '0')) AS course_code,
    CASE WHEN T.id <= 5 THEN CONCAT('专业课', T.id, '：计算机') WHEN T.id <= 10 THEN CONCAT('专业课', T.id, '：经济学')
         WHEN T.id <= 15 THEN CONCAT('基础课', T.id, '：数学') ELSE CONCAT('选修课', T.id, '：艺术')
    END AS course_name,
    CASE WHEN T.id <= 5 THEN 1 WHEN T.id <= 10 THEN 2 WHEN T.id <= 15 THEN 1 ELSE 3 END AS department_id,
    3.0 + (T.id % 4) * 0.5 AS credits,
    2001 + (T.id % 5) AS created_by
FROM (
    SELECT 1 AS id UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
    UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20
) AS T;

-- 3.3 开课班级表 (t_class) - 40 条 (依赖 t_course, t_user)
INSERT INTO `t_class` (`class_id`, `course_id`, `teacher_id`, `semester`, `max_capacity`, `status`)
SELECT
    T.id + 10000 AS class_id,
    100 + (T.id % 20) AS course_id,
    2001 + (T.id % 25) AS teacher_id,
    '2025-Fall' AS semester,
    60 AS max_capacity,
    2 AS status
FROM (
    SELECT (a.id + b.id * 10) AS id FROM
    (SELECT 1 AS id UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 0) AS a
    JOIN (SELECT 0 AS id UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3) AS b
    WHERE (a.id + b.id * 10) BETWEEN 1 AND 40
) AS T;


-- =======================================================
-- 模块 IV: 课程排课与资源 (SCHEDULE & RESOURCES)
-- =======================================================

-- 4.1 课表时间地点表 (t_class_schedule) - 80 条 (依赖 t_class, t_classroom)
INSERT INTO `t_class_schedule` (`class_id`, `location_text`, `classroom_id`, `day_of_week`, `start_time`, `end_time`, `week_range`)
SELECT
    c.class_id,
    '教3-' AS location_text,
    101 AS classroom_id,
    (ROW_NUMBER() OVER (ORDER BY c.class_id) % 5) + 1 AS day_of_week, -- 星期一到星期五
    CASE WHEN (ROW_NUMBER() OVER (ORDER BY c.class_id) % 2) = 1 THEN '08:00:00' ELSE '10:00:00' END AS start_time,
    CASE WHEN (ROW_NUMBER() OVER (ORDER BY c.class_id) % 2) = 1 THEN '09:40:00' ELSE '11:40:00' END AS end_time,
    '1-16' AS week_range
FROM t_class c
UNION ALL
SELECT
    c.class_id,
    '实验楼-' AS location_text,
    201 AS classroom_id,
    (ROW_NUMBER() OVER (ORDER BY c.class_id) % 5) + 1 AS day_of_week,
    CASE WHEN (ROW_NUMBER() OVER (ORDER BY c.class_id) % 2) = 1 THEN '14:00:00' ELSE '16:00:00' END AS start_time,
    CASE WHEN (ROW_NUMBER() OVER (ORDER BY c.class_id) % 2) = 1 THEN '15:40:00' ELSE '17:40:00' END AS end_time,
    '1-16' AS week_range
FROM t_class c;

-- 4.2 资源文件夹表 (t_resource_folder) - 80 条 (依赖 t_class, t_user)
INSERT INTO `t_resource_folder` (`folder_id`, `class_id`, `folder_name`, `created_by`)
SELECT (t.class_id * 10) + 1, t.class_id, '课件资料', t.teacher_id FROM t_class t
UNION ALL
SELECT (t.class_id * 10) + 2, t.class_id, '补充阅读', t.teacher_id FROM t_class t;

-- 4.3 课程资源表 (t_course_resource) - 120 条 (依赖 t_class, t_resource_folder, t_user)
INSERT INTO `t_course_resource` (`class_id`, `folder_id`, `uploader_id`, `file_name`, `file_url`, `file_type`, `file_size`)
SELECT
    T1.class_id,
    T1.folder_id,
    T2.teacher_id AS uploader_id,
    CONCAT('Lecture_', T1.id, '.pdf'),
    CONCAT('/resources/', T1.class_id, '/file_', T1.id, '.pdf'),
    'pdf',
    FLOOR(RAND() * 5000000) + 100000
FROM (
    SELECT folder_id, class_id, ROW_NUMBER() OVER (ORDER BY folder_id) AS id FROM t_resource_folder
) AS T1
JOIN t_class AS T2 ON T1.class_id = T2.class_id
WHERE T1.id BETWEEN 1 AND 120;


-- =======================================================
-- 模块 V: 学生选课与教学内容 (STUDENT & CONTENT)
-- =======================================================

-- 5.1 学生选课表 (t_student_class) - 约 960 条 (依赖 t_user, t_class)
INSERT INTO `t_student_class` (`student_id`, `class_id`, `selection_time`, `status`)
SELECT
    T1.user_id,
    T2.class_id,
    DATE_SUB(NOW(), INTERVAL FLOOR(RAND() * 30) DAY),
    1
FROM (SELECT user_id FROM t_user WHERE user_id BETWEEN 3001 AND 3120) AS T1 -- 120 students
JOIN (
    -- 确保每门课都被选到
    SELECT class_id FROM t_class
    ORDER BY class_id
) AS T2
ON 1=1
WHERE (T1.user_id + T2.class_id) % 15 != 0 -- 随机排除一些选课，控制在 960 条左右
GROUP BY T1.user_id, T2.class_id
HAVING COUNT(*) > 0
LIMIT 960;

-- 5.2 更新 t_class 的当前人数
UPDATE t_class c SET current_enrollment = (
    SELECT COUNT(*) FROM t_student_class sc WHERE sc.class_id = c.class_id AND sc.status = 1
);

-- 5.3 作业发布表 (t_homework) - 100 条 (依赖 t_class, t_user)
INSERT INTO `t_homework` (`homework_id`, `class_id`, `created_by`, `title`, `due_date`)
SELECT
    T2.id AS homework_id,
    T2.class_id,
    T2.teacher_id AS created_by,
    CONCAT('作业', T2.id, '：', T2.course_name, '主题练习') AS title,
    DATE_ADD('2025-10-01', INTERVAL T2.id * 3 DAY) AS due_date
FROM (
    SELECT T.id, c.class_id, c.teacher_id, co.course_name FROM (
        SELECT (a.id + b.id * 10) AS id FROM
        (SELECT 1 AS id UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 0) AS a
        JOIN (SELECT 0 AS id UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) AS b
        WHERE (a.id + b.id * 10) BETWEEN 1 AND 100
    ) AS T
    JOIN t_class c ON 10000 + (T.id % 40) + 1 = c.class_id
    JOIN t_course co ON c.course_id = co.course_id
) AS T2;

-- 5.4 作业提交表 (t_homework_submission) - 约 960 条 (依赖 t_homework, t_student_class)
INSERT INTO `t_homework_submission` (`homework_id`, `student_id`, `submitted_at`, `status`, `grade`)
SELECT
    h.homework_id,
    sc.student_id,
    DATE_SUB(h.due_date, INTERVAL FLOOR(RAND() * 5) HOUR),
    CASE WHEN RAND() < 0.9 THEN 1 ELSE 2 END AS status,
    CASE WHEN RAND() < 0.95 THEN ROUND(70 + RAND() * 30, 2) ELSE NULL END AS grade
FROM t_homework h
JOIN t_student_class sc ON h.class_id = sc.class_id AND sc.status = 1
WHERE (h.homework_id * sc.student_id) % 15 != 0
LIMIT 960;

-- 5.5 作业附件表 (t_submission_file) - 50 条 (依赖 t_homework_submission)
INSERT INTO `t_submission_file` (`submission_id`, `file_name`, `file_url`, `file_size`)
SELECT submission_id, CONCAT('Homework_', submission_id, '.docx'), CONCAT('/submission/', submission_id, '.docx'), 100000 + submission_id * 100
FROM t_homework_submission WHERE submission_id BETWEEN 1 AND 50;

-- 5.6 AI作业分析表 (t_submission_analysis) - 50 条 (依赖 t_homework_submission)
INSERT INTO `t_submission_analysis` (`submission_id`, `analysis_status`, `generated_at`)
SELECT submission_id, 3 AS analysis_status, NOW() FROM t_homework_submission WHERE submission_id BETWEEN 1 AND 50;

-- 5.7 AI教学建议表 (t_teaching_suggestion) - 10 条 (依赖 t_class, t_homework)
INSERT INTO `t_teaching_suggestion` (`class_id`, `homework_id`, `suggestion_content`)
SELECT
    10001 + T.id AS class_id,
    1 + T.id AS homework_id,
    'AI建议：本次作业集中在第3章知识点出错，建议在下节课进行专项复习。'
FROM (SELECT 0 AS id UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) AS T;

-- 5.8 成绩组成项表 (t_grade_component) - 120 条 (依赖 t_class)
INSERT INTO `t_grade_component` (`class_id`, `component_name`, `weight`, `max_score`)
SELECT class_id, '平时作业', 0.3000, 100 FROM t_class WHERE class_id BETWEEN 10001 AND 10040
UNION ALL
SELECT class_id, '期中考试', 0.3000, 100 FROM t_class WHERE class_id BETWEEN 10001 AND 10040
UNION ALL
SELECT class_id, '期末考试', 0.4000, 100 FROM t_class WHERE class_id BETWEEN 10001 AND 10040;

-- 5.9 学生单项成绩表 (t_student_grade) - 约 500 条 (依赖 t_user, t_grade_component)
INSERT INTO `t_student_grade` (`student_id`, `component_id`, `score`, `recorded_by`)
SELECT
    T1.student_id,
    T2.component_id,
    ROUND(55 + RAND() * 45, 2) AS score,
    T3.teacher_id AS recorded_by
FROM (SELECT student_id, class_id FROM t_student_class WHERE student_id BETWEEN 3001 AND 3050 AND class_id BETWEEN 10001 AND 10004) AS T1
JOIN t_grade_component AS T2 ON T1.class_id = T2.class_id
JOIN t_class AS T3 ON T1.class_id = T3.class_id
WHERE (T1.student_id * T2.component_id) % 5 != 0
LIMIT 500;


-- =======================================================
-- 模块 VI: 宿舍与考勤管理 (DORMITORY & ATTENDANCE)
-- =======================================================

-- 6.1 宿舍楼表 (t_dorm_building) - 2 条 (依赖 t_user)
INSERT INTO `t_dorm_building` (`building_id`, `building_name`, `gender_limit`, `manager_id`) VALUES
(10, '松园男寝', 1, 1003), (11, '竹园女寝', 2, 1003);

-- 6.2 宿舍房间表 (t_dorm_room) - 20 条 (依赖 t_dorm_building)
INSERT INTO `t_dorm_room` (`room_id`, `building_id`, `room_number`, `full_name`, `total_beds`, `occupied_beds`)
SELECT T.id AS room_id, CASE WHEN T.id <= 210 THEN 10 ELSE 11 END AS building_id, T.id % 100 AS room_number, CONCAT('Room_', T.id), 4, 0
FROM (
    SELECT 101 AS id UNION ALL SELECT 102 UNION ALL SELECT 103 UNION ALL SELECT 104 UNION ALL SELECT 105 UNION ALL SELECT 106 UNION ALL SELECT 107 UNION ALL SELECT 108 UNION ALL SELECT 109 UNION ALL SELECT 110
    UNION ALL SELECT 201 AS id UNION ALL SELECT 202 UNION ALL SELECT 203 UNION ALL SELECT 204 UNION ALL SELECT 205 UNION ALL SELECT 206 UNION ALL SELECT 207 UNION ALL SELECT 208 UNION ALL SELECT 209 UNION ALL SELECT 210
) AS T;

-- 6.3 宿舍床位表 (t_dorm_bed) - 50 条 (依赖 t_dorm_room, t_user)
INSERT INTO `t_dorm_bed` (`room_id`, `bed_number`, `bed_status`, `current_resident_id`, `allocation_time`)
SELECT
    101 + FLOOR((u.user_id - 3001) / 4) AS room_id,
    CASE (u.user_id - 3001) % 4 WHEN 0 THEN 'A' WHEN 1 THEN 'B' WHEN 2 THEN 'C' ELSE 'D' END AS bed_number,
    2 AS bed_status,
    u.user_id AS current_resident_id,
    '2023-09-01' AS allocation_time
FROM t_user u WHERE u.user_id BETWEEN 3001 AND 3050;

-- 6.4 更新 t_dorm_room 的已占用床位数
UPDATE t_dorm_room dr SET occupied_beds = (SELECT COUNT(*) FROM t_dorm_bed db WHERE db.room_id = dr.room_id);

-- 6.5 宿舍问卷表 (t_dorm_survey) - 120 条 (所有学生, 依赖 t_user)
INSERT INTO `t_dorm_survey` (`student_id`, `wake_up_time`, `sleep_time`, `cleanliness_preference`, `noise_tolerance`, `matching_score`)
SELECT
    u.user_id,
    CASE WHEN u.user_id % 3 = 0 THEN '06:30:00' ELSE '07:30:00' END AS wake_up_time,
    CASE WHEN u.user_id % 3 = 0 THEN '22:30:00' ELSE '00:00:00' END AS sleep_time,
    (u.user_id % 3) + 1 AS cleanliness_preference,
    (u.user_id % 3) + 1 AS noise_tolerance,
    ROUND(RAND() * 100, 2) AS matching_score
FROM t_user u WHERE u.user_id BETWEEN 3001 AND 3120;

-- 6.6 宿舍状态记录表 (t_dorm_status_record) - 50 条 (依赖 t_user)
INSERT INTO `t_dorm_status_record` (`student_id`, `status_type`, `reason`, `operator_id`, `end_time`)
SELECT u.user_id AS student_id, 2 AS status_type, '假期回家', 1003 AS operator_id, DATE_ADD(NOW(), INTERVAL 7 DAY)
FROM t_user u WHERE u.user_id BETWEEN 3001 AND 3050;

-- 6.7 考勤任务表 (t_attendance_session) - 80 条 (依赖 t_class, t_user)
INSERT INTO `t_attendance_session` (`class_id`, `created_by`, `session_type`, `start_time`, `end_time`, `required_latitude`, `required_longitude`)
SELECT
    c.class_id,
    c.teacher_id,
    3 AS session_type,
    DATE_ADD('2025-09-01 08:00:00', INTERVAL T.id * 3 DAY) AS start_time,
    DATE_ADD('2025-09-01 08:15:00', INTERVAL T.id * 3 DAY) AS end_time,
    39.9042 + RAND() * 0.1,
    116.4074 + RAND() * 0.1
FROM (SELECT class_id, teacher_id, ROW_NUMBER() OVER (ORDER BY class_id) AS id FROM t_class) AS c
JOIN (
    SELECT id FROM (SELECT 1 AS id UNION ALL SELECT 2) AS X, t_class AS Y ORDER BY Y.class_id, X.id LIMIT 80
) AS T ON 10000 + ((T.id - 1) % 40) + 1 = c.class_id;

-- 6.8 学生考勤记录表 (t_attendance_record) - 约 800 条 (依赖 t_attendance_session, t_student_class)
INSERT INTO `t_attendance_record` (`session_id`, `student_id`, `status`, `check_in_time`)
SELECT
    T1.session_id,
    T2.student_id,
    CASE WHEN RAND() < 0.85 THEN 1 WHEN RAND() < 0.95 THEN 3 ELSE 2 END AS status,
    DATE_ADD(T3.start_time, INTERVAL FLOOR(RAND() * 10) MINUTE) AS check_in_time
FROM t_attendance_session AS T1
JOIN t_student_class AS T2 ON T1.class_id = T2.class_id AND T2.status = 1
JOIN t_attendance_session AS T3 ON T1.session_id = T3.session_id
WHERE (T1.session_id * T2.student_id) % 10 != 0
LIMIT 800;

-- 6.9 请假申请表 (t_leave_request) - 50 条 (依赖 t_user, t_class)
INSERT INTO `t_leave_request` (`student_id`, `class_id`, `reason`, `start_time`, `end_time`, `status`, `approver_id`)
SELECT
    3001 + T.id AS student_id,
    10001 + (T.id % 4) AS class_id,
    CONCAT('因病请假，需休息', T.id, '天'),
    DATE_ADD(NOW(), INTERVAL T.id DAY),
    DATE_ADD(NOW(), INTERVAL T.id + 3 DAY),
    CASE WHEN RAND() < 0.8 THEN 2 ELSE 3 END AS status,
    2001 + (T.id % 5) AS approver_id
FROM (SELECT (a.id + b.id * 10) AS id FROM (SELECT 0 AS id UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) a JOIN (SELECT 0 AS id UNION ALL SELECT 1) b WHERE a.id + b.id * 10 BETWEEN 1 AND 50) AS T;


-- =======================================================
-- 模块 VII: 图书馆与论坛 (LIBRARY & FORUM)
-- =======================================================

-- 7.1 书籍信息表 (t_book) - 2 条 (依赖 t_user)
INSERT INTO `t_book` (`book_id`, `isbn`, `title`, `author`, `publisher`, `total_copies`, `available_copies`, `uploader_id`) VALUES
(1, '9787111213742', '数据结构', '严蔚敏', '清华大学出版社', 50, 48, 1004),
(2, '9787115543210', 'Python编程', 'Eric Matthes', '人民邮电出版社', 30, 30, 1004);

-- 7.2 借阅策略表 (t_book_policy) - 1 条
INSERT INTO `t_book_policy` (`policy_id`, `policy_name`, `max_duration_days`, `max_borrow_count`, `user_type_scope`) VALUES
(1, '学生借阅规则', 30, 5, 1);

-- 7.3 图书借阅记录表 (t_borrow_record) - 50 条 (依赖 t_book, t_user, t_book_policy)
INSERT INTO `t_borrow_record` (`book_id`, `borrower_id`, `policy_id`, `borrow_date`, `due_date`, `status`)
SELECT 1 + (u.user_id % 2) AS book_id, u.user_id AS borrower_id, 1 AS policy_id, DATE_SUB(NOW(), INTERVAL (u.user_id % 60) DAY) AS borrow_date, DATE_ADD(NOW(), INTERVAL 30 DAY) AS due_date, 1 AS status
FROM t_user u WHERE u.user_id BETWEEN 3001 AND 3050;

-- 7.4 书籍标签表 (t_tag) - 4 条
INSERT INTO `t_tag` (`tag_id`, `tag_name`, `description`) VALUES
(1, '计算机科学', '编程、算法、数据结构'), (2, '经济学', '宏观、微观、金融'), (3, '文学', '小说、散文'), (4, '体育', '运动、健康');

-- 7.5 书籍与标签关联表 (t_book_tag) - 4 条 (依赖 t_book, t_tag)
INSERT INTO `t_book_tag` (`book_id`, `tag_id`) VALUES
(1, 1), (1, 3), (2, 1), (2, 4);

-- 7.6 书籍偏好/推荐表 (t_book_preference) - 50 条 (依赖 t_user, t_book)
INSERT INTO `t_book_preference` (`user_id`, `recommended_book_id`, `reason`, `score`)
SELECT
    3001 + T.id AS user_id,
    1 + (T.id % 2) AS recommended_book_id,
    CONCAT('基于您的借阅记录，推荐：', CASE WHEN T.id % 2 = 0 THEN '数据结构' ELSE 'Python编程' END),
    ROUND(80 + RAND() * 20, 2) AS score
FROM (SELECT (a.id + b.id * 10) AS id FROM (SELECT 0 AS id UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) a JOIN (SELECT 0 AS id UNION ALL SELECT 1) b WHERE a.id + b.id * 10 BETWEEN 1 AND 50) AS T;

-- 7.7 论坛板块表 (t_forum_category) - 2 条
INSERT INTO `t_forum_category` (`category_id`, `category_name`, `description`) VALUES
(1, '失物招领', '寻找丢失或捡到的物品'), (2, '学术交流', '课程问题与科研讨论');

-- 7.8 论坛帖子表 (t_forum_post) - 20 条 (依赖 t_forum_category, t_user)
INSERT INTO `t_forum_post` (`post_id`, `category_id`, `author_id`, `title`, `content`)
SELECT T.id AS post_id, 1 + (T.id % 2) AS category_id, 3001 + (T.id % 100) AS author_id, CONCAT('帖子主题_', T.id), CONCAT('帖子内容：第', T.id, '条')
FROM (SELECT (a.id + b.id * 10) AS id FROM (SELECT 1 AS id UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 0) AS a
    JOIN (SELECT 0 AS id UNION ALL SELECT 1) AS b WHERE a.id + b.id * 10 BETWEEN 1 AND 20
) AS T;

-- 7.9 论坛评论表 (t_forum_comment) - 50 条 (依赖 t_forum_post, t_user)
INSERT INTO `t_forum_comment` (`post_id`, `author_id`, `content`)
SELECT 1 + (T.id % 20) AS post_id, 3001 + (T.id % 100) AS author_id, CONCAT('评论内容：第', T.id, '条')
FROM (
    SELECT (a.id + b.id * 10) AS id FROM (SELECT 1 AS id UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 0) AS a
    JOIN (SELECT 0 AS id UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4) AS b WHERE a.id + b.id * 10 BETWEEN 1 AND 50
) AS T;

-- 重新开启外键检查
SET FOREIGN_KEY_CHECKS = 1;

-- =======================================================
-- zhuijia data
-- =======================================================

USE campus_system;
-- 禁用外键检查，以确保插入顺序不影响关联性
SET FOREIGN_KEY_CHECKS = 0;

-- 教师用户ID范围: 2001-2025
-- 学生用户ID范围: 3001-3120

-- =======================================================
-- 1. 教室与排课模块扩展 (t_classroom, t_class_schedule)
-- 扩展 6 个新教室 (固定 ID 从 401 开始，需手动插入)
-- =======================================================

-- 1.1 插入新教室 (t_classroom) - 6 条
-- 假设教三楼(1)和实验楼(2)有空余房间
INSERT INTO `t_classroom` (`room_id`, `room_number`, `building_id`, `full_name`, `capacity`, `room_type`) VALUES
(401, '401', 1, '教三楼401', 50, '多媒体教室'),
(402, '402', 1, '教三楼402', 50, '多媒体教室'),
(501, '501', 2, '实验楼501', 30, '研讨室'),
(502, '502', 2, '实验楼502', 30, '研讨室'),
(601, '601', 3, '文科楼601', 120, '大型报告厅'),
(602, '602', 3, '文科楼602', 60, '普通教室');

-- 1.2 插入新教室的课表 (t_class_schedule) - 12 条
-- 选取 12 个班级来分配给新教室作为晚课地点
INSERT INTO `t_class_schedule` (`class_id`, `location_text`, `classroom_id`, `day_of_week`, `start_time`, `end_time`, `week_range`)
SELECT
    C.class_id,
    CONCAT('新教室-', T.room_id, ' '),
    T.room_id AS classroom_id,
    (ROW_NUMBER() OVER (ORDER BY T.room_id) % 5) + 1 AS day_of_week,
    '18:00:00' AS start_time,
    '19:40:00' AS end_time,
    '1-16' AS week_range
FROM (
    -- 选取 12 个班级来分配
    SELECT class_id FROM t_class ORDER BY RAND() LIMIT 12
) AS C
JOIN (
    -- 循环使用 6 个新教室，每间教室分配 2 个班级
    SELECT 401 AS room_id UNION ALL SELECT 402 UNION ALL SELECT 501 UNION ALL SELECT 502 UNION ALL SELECT 601 UNION ALL SELECT 602
    UNION ALL SELECT 401 AS room_id UNION ALL SELECT 402 UNION ALL SELECT 501 UNION ALL SELECT 502 UNION ALL SELECT 601 UNION ALL SELECT 602
) AS T ON 1=1
LIMIT 12;

-- =======================================================
-- 2. 图书馆模块扩展 (t_book, t_tag, t_book_tag, t_borrow_record, t_book_preference)
-- 扩展 8 本新书，10 个新标签
-- =======================================================

-- 2.1 插入新书籍 (t_book) - 8 条 (book_id 是 AUTO_INCREMENT)
INSERT INTO `t_book` (`book_id`, `isbn`, `title`, `author`, `publisher`, `total_copies`, `available_copies`, `uploader_id`) VALUES
(NULL, '9787111352342', '操作系统：精髓与设计原理', 'William Stallings', '机械工业出版社', 40, 35, 1004),
(NULL, '9787030510341', '高等代数', '邱维声', '科学出版社', 60, 55, 1004),
(NULL, '9787508658094', '原则', '瑞·达利欧', '中信出版社', 50, 40, 1004),
(NULL, '9787540787309', '三体：地球往事', '刘慈欣', '花城出版社', 100, 80, 1004),
(NULL, '9787532167198', '月亮与六便士', '毛姆', '上海译文出版社', 45, 40, 1004),
(NULL, '9787505740440', '人类简史', '尤瓦尔·赫拉利', '中信出版社', 70, 60, 1004),
(NULL, '9787544270030', '围城', '钱钟书', '南海出版公司', 35, 30, 1004),
(NULL, '9787506385418', '软件工程导论', '张海藩', '清华大学出版社', 55, 50, 1004);

-- 2.2 插入新标签 (t_tag) - 10 条 (tag_id 是 AUTO_INCREMENT)
INSERT INTO `t_tag` (`tag_id`, `tag_name`, `description`) VALUES
(NULL, '小说', '文学作品、故事'),
(NULL, '历史', '世界史、文明史'),
(NULL, '管理学', '商业、原则、领导力'),
(NULL, '科幻', '未来、想象力'),
(NULL, '编程语言', 'Java, C++, Go'),
(NULL, '网络安全', '加密、防火墙'),
(NULL, '文学经典', '名著、传世之作'),
(NULL, '社会科学', '社会学、心理学'),
(NULL, '生物医学', '生命科学、医疗'),
(NULL, '艺术设计', '美学、设计理论');

-- 2.3 插入新的书籍与标签关联 (t_book_tag) - 30 条
-- 关联新书和新标签
INSERT INTO `t_book_tag` (`book_id`, `tag_id`)
SELECT
    T.book_id,
    T.tag_id
FROM (
    -- 确保 book_id 和 tag_id 是当前最大的 ID，并向下推算
    SELECT b.book_id, t.tag_id
    FROM t_book b
    JOIN t_tag t
    -- 筛选出最后 8 本书 (新书)
    WHERE b.book_id > (SELECT MAX(book_id) - 8 FROM t_book)
    -- 筛选出最后 10 个标签 (新标签)
    AND t.tag_id > (SELECT MAX(tag_id) - 10 FROM t_tag)
) AS T
WHERE (T.book_id * T.tag_id) % 3 = 0 -- 随机选择 30% 进行关联
LIMIT 30;

-- 2.4 插入更多借阅记录 (t_borrow_record) - 40 条
-- 让更多学生（3051-3090）借阅新书
INSERT INTO `t_borrow_record` (`book_id`, `borrower_id`, `policy_id`, `borrow_date`, `due_date`, `status`)
SELECT
    (SELECT MAX(book_id) - (u.user_id % 8) FROM t_book) AS book_id, -- 借阅新书
    u.user_id AS borrower_id,
    1 AS policy_id,
    DATE_SUB(NOW(), INTERVAL (u.user_id % 50) DAY) AS borrow_date,
    DATE_ADD(NOW(), INTERVAL 30 DAY) AS due_date,
    1 AS status
FROM t_user u WHERE u.user_id BETWEEN 3051 AND 3090;

-- 2.5 插入更多书籍偏好 (t_book_preference) - 50 条
-- 让学生 (3091-3120) 产生对新书的偏好
INSERT INTO `t_book_preference` (`user_id`, `recommended_book_id`, `reason`, `score`)
SELECT
    u.user_id AS user_id,
    (SELECT MAX(book_id) - (u.user_id % 8) FROM t_book) AS recommended_book_id, -- 偏好新书
    CONCAT('基于您的专业，推荐书籍', (SELECT MAX(book_id) - (u.user_id % 8) FROM t_book)),
    ROUND(75 + RAND() * 25, 2) AS score
FROM t_user u WHERE u.user_id BETWEEN 3091 AND 3120;


-- =======================================================
-- 3. 论坛模块扩展 (t_forum_category, t_forum_post, t_forum_comment)
-- 扩展 3 个新分类
-- =======================================================

-- 3.1 插入新论坛板块 (t_forum_category) - 3 条 (category_id 是 AUTO_INCREMENT)
INSERT INTO `t_forum_category` (`category_id`, `category_name`, `description`) VALUES
(NULL, '二手交易', '转让/求购闲置物品'),
(NULL, '校内生活', '食堂、娱乐、吐槽'),
(NULL, '实习招聘', '分享实习信息和面经');

-- 3.2 插入更多论坛帖子 (t_forum_post) - 30 条
INSERT INTO `t_forum_post` (`category_id`, `author_id`, `title`, `content`)
SELECT
    (SELECT MAX(category_id) - (T.id % 3) FROM t_forum_category) AS category_id, -- 循环分配给新类别
    3001 + (T.id % 120) AS author_id, -- 学生作者
    CASE
        WHEN T.id % 3 = 0 THEN CONCAT('急出！九成新笔记本电脑 (帖子', T.id, ')')
        WHEN T.id % 3 = 1 THEN CONCAT('吐槽食堂今日新菜品 (帖子', T.id, ')')
        ELSE CONCAT('XX公司暑期实习面经分享 (帖子', T.id, ')')
    END AS title,
    CONCAT('这是第', T.id, '个新的论坛帖子内容。') AS content
FROM (
    SELECT 1 AS id UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4 UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9 UNION ALL SELECT 10
    UNION ALL SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13 UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18 UNION ALL SELECT 19 UNION ALL SELECT 20
    UNION ALL SELECT 21 UNION ALL SELECT 22 UNION ALL SELECT 23 UNION ALL SELECT 24 UNION ALL SELECT 25 UNION ALL SELECT 26 UNION ALL SELECT 27 UNION ALL SELECT 28 UNION ALL SELECT 29 UNION ALL SELECT 30
) AS T;

-- 3.3 插入更多论坛评论 (t_forum_comment) - 70 条
-- 评论新的帖子
INSERT INTO `t_forum_comment` (`post_id`, `author_id`, `content`)
SELECT
    T1.post_id,
    3001 + (T1.id % 120) AS author_id, -- 学生作者
    CONCAT('这条新的评论针对帖子', T1.post_id, '，内容是：很赞同。')
FROM (
    -- 选取最近的 30 个帖子 (即新帖子)
    SELECT post_id, ROW_NUMBER() OVER (ORDER BY post_id DESC) AS id FROM t_forum_post
    ORDER BY post_id DESC LIMIT 30
) AS T1
JOIN (
    SELECT 1 AS X UNION ALL SELECT 2 UNION ALL SELECT 3
) AS T2 ON 1=1 -- 每个帖子增加 3 条评论
LIMIT 70;


-- 重新开启外键检查
SET FOREIGN_KEY_CHECKS = 1;