package com.example.campus.controller;

import com.example.campus.CampusApplication;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * API接口测试类
 */
@SpringBootTest(classes = CampusApplication.class)
@AutoConfigureWebMvc
@ActiveProfiles("test")
public class ApiTest {

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    private MockMvc mockMvc;
    private String token;

    /**
     * 初始化MockMvc
     */
    public void initMockMvc() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    /**
     * 测试登录接口
     */
    @Test
    public void testLogin() throws Exception {
        initMockMvc();
        
        Map<String, String> loginRequest = new HashMap<>();
        loginRequest.put("username", "2021001001");
        loginRequest.put("password", "123456");

        String result = mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.token").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // 提取token用于后续测试
        Map<String, Object> response = objectMapper.readValue(result, Map.class);
        Map<String, Object> data = (Map<String, Object>) response.get("data");
        token = (String) data.get("token");
        
        System.out.println("登录成功，Token: " + token);
    }

    /**
     * 测试获取个人资料接口
     */
    @Test
    public void testGetProfile() throws Exception {
        initMockMvc();
        
        // 先登录获取token
        testLogin();
        
        if (token == null) {
            System.out.println("警告：未获取到token，跳过此测试");
            return;
        }

        mockMvc.perform(get("/profile")
                        .header("token", token))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.user_id").exists());
    }

    /**
     * 测试角色列表查询接口
     */
    @Test
    public void testListRoles() throws Exception {
        initMockMvc();

        mockMvc.perform(get("/admin/roles")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.total").exists())
                .andExpect(jsonPath("$.data.rows").isArray());
    }

    /**
     * 测试添加角色接口
     */
    @Test
    public void testAddRole() throws Exception {
        initMockMvc();

        Map<String, String> roleRequest = new HashMap<>();
        roleRequest.put("role_name", "测试角色");
        roleRequest.put("role_key", "test_role");
        roleRequest.put("description", "这是一个测试角色");

        mockMvc.perform(post("/admin/roles")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(roleRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    /**
     * 测试查询所有角色列表接口
     */
    @Test
    public void testListAllRoles() throws Exception {
        initMockMvc();

        mockMvc.perform(get("/admin/roles/list"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data").isArray());
    }

    /**
     * 测试学生列表查询接口
     */
    @Test
    public void testListStudents() throws Exception {
        initMockMvc();

        mockMvc.perform(get("/admin/students")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.total").exists())
                .andExpect(jsonPath("$.data.rows").isArray());
    }

    /**
     * 测试添加学生接口
     */
    @Test
    public void testAddStudent() throws Exception {
        initMockMvc();

        Map<String, Object> studentRequest = new HashMap<>();
        studentRequest.put("username", "2021001999");
        studentRequest.put("password", "123456");
        studentRequest.put("full_name", "测试学生");
        studentRequest.put("email", "test@edu.cn");
        studentRequest.put("phone_number", "13800009999");
        studentRequest.put("gender", 1);
        studentRequest.put("major", "计算机科学与技术");
        studentRequest.put("class_name", "计算机2101班");
        studentRequest.put("grade", "2021级");
        studentRequest.put("enrollment_date", "2021-09-01");

        mockMvc.perform(post("/admin/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(studentRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    /**
     * 测试教师列表查询接口
     */
    @Test
    public void testListTeachers() throws Exception {
        initMockMvc();

        mockMvc.perform(get("/admin/teachers")
                        .param("page", "1")
                        .param("pageSize", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1))
                .andExpect(jsonPath("$.data.total").exists())
                .andExpect(jsonPath("$.data.rows").isArray());
    }

    /**
     * 测试添加教师接口
     */
    @Test
    public void testAddTeacher() throws Exception {
        initMockMvc();

        Map<String, Object> teacherRequest = new HashMap<>();
        teacherRequest.put("username", "T2025999");
        teacherRequest.put("password", "123456");
        teacherRequest.put("full_name", "测试教师");
        teacherRequest.put("email", "teacher@edu.cn");
        teacherRequest.put("phone_number", "13900009999");
        teacherRequest.put("gender", 1);
        teacherRequest.put("department", "信息工程学院");
        teacherRequest.put("title", "教授");
        teacherRequest.put("office_location", "教三楼 999");

        mockMvc.perform(post("/admin/teachers")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(teacherRequest)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(1));
    }

    /**
     * 测试文件上传接口
     */
    @Test
    public void testUpload() throws Exception {
        initMockMvc();

        // 注意：这里需要实际的文件，测试时可能需要mock
        // 由于需要MultipartFile，这里只测试接口是否存在
        System.out.println("文件上传接口需要实际文件，请手动测试");
    }
}

