package com.example.campus.controller;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.StudentProfile;
import com.example.campus.entity.User;
import com.example.campus.service.StudentService;
import com.example.campus.vo.StudentVO;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 学生管理控制器
 */
@RestController
@RequestMapping("/admin/students")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;
    private static final Pattern STUDENT_ID_PATTERN = Pattern.compile("^\\d+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

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
        validatePagination(page, pageSize);
        if (status != null && status != 1 && status != 2) {
            throw new BusinessException("status参数无效（1=激活，2=禁用）");
        }
        if (StringUtils.hasText(student_id_number) && !STUDENT_ID_PATTERN.matcher(student_id_number).matches()) {
            throw new BusinessException("学号格式错误（纯数字）");
        }
        PageResult<StudentVO> result = studentService.listStudents(page, pageSize, full_name,
                student_id_number, major, class_name, status);
        return Result.success(result);
    }

    /**
     * 添加学生
     */
    @PostMapping
    public Result<?> addStudent(@RequestBody StudentCreateRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BusinessException("username为必填项");
        }
        String username = request.getUsername().trim();
        if (!STUDENT_ID_PATTERN.matcher(username).matches()) {
            throw new BusinessException("username必须为纯数字学号");
        }
        if (!StringUtils.hasText(request.getFull_name())) {
            throw new BusinessException("full_name为必填项");
        }
        if (request.getGender() != null && (request.getGender() < 0 || request.getGender() > 2)) {
            throw new BusinessException("gender值无效（0/1/2）");
        }
        if (StringUtils.hasText(request.getEmail()) && !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new BusinessException("email格式不正确");
        }
        if (StringUtils.hasText(request.getPhone_number()) && !PHONE_PATTERN.matcher(request.getPhone_number()).matches()) {
            throw new BusinessException("手机号码格式不正确（11位数字）");
        }
        LocalDate enrollmentDate = null;
        if (StringUtils.hasText(request.getEnrollment_date())) {
            try {
                enrollmentDate = LocalDate.parse(request.getEnrollment_date());
            } catch (DateTimeParseException ex) {
                throw new BusinessException("enrollment_date格式应为yyyy-MM-dd");
            }
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(request.getPassword());
        user.setFullName(request.getFull_name());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone_number());

        StudentProfile profile = new StudentProfile();
        profile.setGender(request.getGender());
        profile.setMajor(request.getMajor());
        profile.setClassName(request.getClass_name());
        profile.setGrade(request.getGrade());
        profile.setEnrollmentDate(enrollmentDate);

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
        if (!StringUtils.hasText(ids)) {
            throw new BusinessException("学生ID不能为空");
        }
        List<Long> userIds = new ArrayList<>();
        for (String part : ids.split(",")) {
            String trimmed = part.trim();
            if (!StringUtils.hasText(trimmed)) {
                continue;
            }
            if (!STUDENT_ID_PATTERN.matcher(trimmed).matches()) {
                throw new BusinessException("学生ID格式错误");
            }
            userIds.add(Long.parseLong(trimmed));
        }
        if (userIds.isEmpty()) {
            throw new BusinessException("学生ID不能为空");
        }
        studentService.deleteStudents(userIds);
        return Result.success();
    }

    private void validatePagination(Integer page, Integer pageSize) {
        if (page != null && page < 1) {
            throw new BusinessException("page参数必须为正整数");
        }
        if (pageSize != null && (pageSize < 1 || pageSize > 100)) {
            throw new BusinessException("pageSize参数必须为正整数（1-100）");
        }
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
        private String enrollment_date;
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

