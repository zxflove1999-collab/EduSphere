package com.example.campus.controller;

import com.example.campus.common.BusinessException;
import com.example.campus.common.PageResult;
import com.example.campus.common.Result;
import com.example.campus.entity.TeacherProfile;
import com.example.campus.entity.User;
import com.example.campus.service.TeacherService;
import com.example.campus.vo.TeacherVO;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 教师管理控制器
 */
@RestController
@RequestMapping("/admin/teachers")
@RequiredArgsConstructor
public class TeacherController {
    private final TeacherService teacherService;
    private static final Pattern TEACHER_ID_PATTERN = Pattern.compile("^[A-Za-z0-9]+$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^\\d{11}$");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * 教师列表查询
     */
    @GetMapping
    public Result<PageResult<TeacherVO>> listTeachers(@RequestParam(required = false) Integer page,
                                                        @RequestParam(required = false) Integer pageSize,
                                                        @RequestParam(required = false) String full_name,
                                                        @RequestParam(required = false) String teacher_id_number,
                                                        @RequestParam(required = false) String department,
                                                        @RequestParam(required = false) String title,
                                                        @RequestParam(required = false) Integer status) {
        validatePagination(page, pageSize);
        if (status != null && status != 1 && status != 2) {
            throw new BusinessException("status参数无效（1=激活，2=禁用）");
        }
        if (StringUtils.hasText(teacher_id_number) && !TEACHER_ID_PATTERN.matcher(teacher_id_number).matches()) {
            throw new BusinessException("教师编号格式错误（字母或数字）");
        }
        PageResult<TeacherVO> result = teacherService.listTeachers(page, pageSize, full_name,
                teacher_id_number, department, title, status);
        return Result.success(result);
    }

    /**
     * 添加教师
     */
    @PostMapping
    public Result<?> addTeacher(@RequestBody TeacherCreateRequest request) {
        if (!StringUtils.hasText(request.getUsername())) {
            throw new BusinessException("登录账号为必填项");
        }
        String username = request.getUsername().trim();
        if (!TEACHER_ID_PATTERN.matcher(username).matches()) {
            throw new BusinessException("登录账号格式错误（仅允许字母或数字）");
        }
        if (!StringUtils.hasText(request.getFull_name())) {
            throw new BusinessException("教师真实姓名为必填项");
        }
        if (request.getGender() != null && (request.getGender() < 0 || request.getGender() > 2)) {
            throw new BusinessException("性别值无效（0/1/2）");
        }
        if (StringUtils.hasText(request.getEmail()) && !EMAIL_PATTERN.matcher(request.getEmail()).matches()) {
            throw new BusinessException("邮箱格式错误");
        }
        if (StringUtils.hasText(request.getPhone_number()) && !PHONE_PATTERN.matcher(request.getPhone_number()).matches()) {
            throw new BusinessException("手机号码格式错误");
        }

        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(request.getPassword());
        user.setFullName(request.getFull_name());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone_number());

        TeacherProfile profile = new TeacherProfile();
        profile.setGender(request.getGender());
        profile.setDepartment(request.getDepartment());
        profile.setTitle(request.getTitle());
        profile.setOfficeLocation(request.getOffice_location());

        teacherService.addTeacher(user, profile);
        return Result.success();
    }

    /**
     * 根据ID查询教师
     */
    @GetMapping("/{id}")
    public Result<TeacherVO> getTeacherById(@PathVariable Long id) {
        TeacherVO teacher = teacherService.getTeacherById(id);
        return Result.success(teacher);
    }

    /**
     * 修改教师资料
     */
    @PutMapping
    public Result<?> updateTeacher(@RequestBody TeacherUpdateRequest request) {
        User user = new User();
        user.setUserId(request.getUser_id());
        user.setFullName(request.getFull_name());
        user.setEmail(request.getEmail());
        user.setPhoneNumber(request.getPhone_number());
        user.setStatus(request.getStatus());

        TeacherProfile profile = new TeacherProfile();
        profile.setDepartment(request.getDepartment());
        profile.setTitle(request.getTitle());
        profile.setOfficeLocation(request.getOffice_location());

        teacherService.updateTeacher(user, profile);
        return Result.success();
    }

    /**
     * 删除教师
     */
    @DeleteMapping("/{ids}")
    public Result<?> deleteTeachers(@PathVariable String ids) {
        if (!StringUtils.hasText(ids)) {
            throw new BusinessException("教师ID不能为空");
        }
        List<Long> userIds = new ArrayList<>();
        for (String part : ids.split(",")) {
            String trimmed = part.trim();
            if (!StringUtils.hasText(trimmed)) {
                continue;
            }
            if (!trimmed.matches("^\\d+$")) {
                throw new BusinessException("教师ID格式错误");
            }
            userIds.add(Long.parseLong(trimmed));
        }
        if (userIds.isEmpty()) {
            throw new BusinessException("教师ID不能为空");
        }
        teacherService.deleteTeachers(userIds);
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
    static class TeacherCreateRequest {
        private String username;
        private String password;
        private String full_name;
        private String email;
        private String phone_number;
        private Integer gender;
        private String department;
        private String title;
        private String office_location;
    }

    @lombok.Data
    static class TeacherUpdateRequest {
        private Long user_id;
        private String full_name;
        private String email;
        private String phone_number;
        private Integer status;
        private String department;
        private String title;
        private String office_location;
    }
}

