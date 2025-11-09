package com.example.campus.controller;

import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.StudentProfile;
import com.example.campus.entity.User;
import com.example.campus.service.StudentService;
import com.example.campus.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 学生管理控制器
 */
@RestController
@RequestMapping("/admin/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    /**
     * 学生列表查询
     */
    @GetMapping
    public Result<PageResult<StudentVO>> listStudents(@RequestParam(required = false) Integer page,
                                                       @RequestParam(required = false) Integer pageSize,
                                                       @RequestParam(required = false) String full_name,
                                                       @RequestParam(required = false) String student_id_number,
                                                       @RequestParam(required = false) String major,
                                                       @RequestParam(required = false) String class_name,
                                                       @RequestParam(required = false) Integer status) {
        PageResult<StudentVO> result = studentService.listStudents(page, pageSize, full_name,
                student_id_number, major, class_name, status);
        return Result.success(result);
    }

    /**
     * 添加学生
     */
    @PostMapping
    public Result<?> addStudent(@RequestBody StudentCreateRequest request) {
        User user = new User();
        user.setUsername(request.getUsername());
        user.setPasswordHash(request.getPassword());
        user.setFullName(request.getFull_name());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone_number());

        StudentProfile profile = new StudentProfile();
        profile.setGender(request.getGender());
        profile.setMajor(request.getMajor());
        profile.setClassName(request.getClass_name());
        profile.setGrade(request.getGrade());
        profile.setEnrollmentDate(request.getEnrollment_date());

        studentService.addStudent(user, profile);
        return Result.success();
    }

    /**
     * 根据ID查询学生
     */
    @GetMapping("/{id}")
    public Result<StudentVO> getStudentById(@PathVariable Long id) {
        StudentVO student = studentService.getStudentById(id);
        return Result.success(student);
    }

    /**
     * 修改学生资料
     */
    @PutMapping
    public Result<?> updateStudent(@RequestBody StudentUpdateRequest request) {
        User user = new User();
        user.setUserId(request.getUser_id());
        user.setFullName(request.getFull_name());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone_number());
        user.setStatus(request.getStatus());

        StudentProfile profile = new StudentProfile();
        profile.setMajor(request.getMajor());
        profile.setClassName(request.getClass_name());
        profile.setGrade(request.getGrade());

        studentService.updateStudent(user, profile);
        return Result.success();
    }

    /**
     * 删除学生
     */
    @DeleteMapping("/{ids}")
    public Result<?> deleteStudents(@PathVariable String ids) {
        List<Long> userIds = Arrays.stream(ids.split(","))
                .map(Long::parseLong)
                .collect(Collectors.toList());
        studentService.deleteStudents(userIds);
        return Result.success();
    }

    // 内部类用于接收请求
    @lombok.Data
    static class StudentCreateRequest {
        private String username;
        private String password;
        private String full_name;
        private String email;
        private String phone_number;
        private Integer gender;
        private String major;
        private String class_name;
        private String grade;
        private java.time.LocalDate enrollment_date;
    }

    @lombok.Data
    static class StudentUpdateRequest {
        private Long user_id;
        private String full_name;
        private String email;
        private String phone_number;
        private Integer status;
        private String major;
        private String class_name;
        private String grade;
    }
}

