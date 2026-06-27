package org.example.Servlet;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.example.DAO.UserDAO;
import org.example.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "*")
public class UserImportExportController {

    @Autowired
    private UserDAO userDAO;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 导出所有用户为 JSON（不含密码）
     */
    @GetMapping("/export")
    public List<Map<String, Object>> exportUsers() {
        List<User> users = userDAO.Find_All();
        List<Map<String, Object>> result = new ArrayList<>();
        for (User u : users) {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", u.getId());
            m.put("username", u.getUsername());
            m.put("email", u.getEmail());
            m.put("role", u.getRole());
            result.add(m);
        }
        return result;
    }

    /**
     * 导入用户 JSON 数组，批量创建或更新
     * 请求体格式:
     * [
     *   {"username":"admin","password":"abc123","email":"a@x.com","role":"admin"},
     *   ...
     * ]
     */
    @PostMapping("/import")
    public Map<String, Object> importUsers(@RequestBody String raw,
                                            HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list;
        try {
            list = objectMapper.readValue(raw, new TypeReference<>() {});
        } catch (IOException e) {
            response.setStatus(400);
            return Map.of("ok", false, "msg", "JSON 格式错误");
        }

        int created = 0, updated = 0, skipped = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            Map<String, Object> item = list.get(i);
            String username = (String) item.get("username");
            String password = (String) item.get("password");
            String email = (String) item.get("email");
            String role = (String) item.get("role");

            if (username == null || username.isBlank()) {
                skipped++;
                errors.add("第" + (i + 1) + "行: 用户名为空");
                continue;
            }
            if (!username.matches("^[a-zA-Z0-9]+$")) {
                skipped++;
                errors.add(username + ": 用户名格式不合法");
                continue;
            }

            try {
                User exist = userDAO.Find_By_Name(username);
                if (exist != null) {
                    // 更新
                    exist.setEmail(email != null ? email : exist.getEmail());
                    exist.setRole(role != null ? role : exist.getRole());
                    if (password != null && !password.isBlank()
                            && password.matches("^[a-zA-Z0-9]+$")) {
                        exist.setPassword(password);
                    }
                    jdbcTemplate.update(
                        "UPDATE users SET email=?, role=?, password=? WHERE id=?",
                        exist.getEmail(), exist.getRole(), exist.getPassword(), exist.getId());
                    updated++;
                } else {
                    // 新增
                    if (password == null || password.isBlank()) {
                        skipped++;
                        errors.add(username + ": 新用户必须提供密码");
                        continue;
                    }
                    if (!password.matches("^[a-zA-Z0-9]+$")) {
                        skipped++;
                        errors.add(username + ": 密码格式不合法");
                        continue;
                    }
                    jdbcTemplate.update(
                        "INSERT INTO users (username, password, email, role) VALUES (?,?,?,?)",
                        username, password,
                        email != null ? email : "",
                        role != null ? role : "user");
                    created++;
                }
            } catch (Exception e) {
                skipped++;
                errors.add(username + ": " + e.getMessage());
            }
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("ok", true);
        result.put("created", created);
        result.put("updated", updated);
        result.put("skipped", skipped);
        result.put("errors", errors);
        return result;
    }

    /**
     * 导出用户为 Excel (.xlsx)
     */
    @GetMapping("/export-excel")
    public void exportExcel(HttpServletResponse response) throws IOException {
        List<User> users = userDAO.Find_All();

        Workbook wb = new XSSFWorkbook();
        Sheet sheet = wb.createSheet("用户列表");

        // 表头
        Row header = sheet.createRow(0);
        String[] headers = {"ID", "用户名", "邮箱", "角色"};
        CellStyle headerStyle = wb.createCellStyle();
        Font headerFont = wb.createFont();
        headerFont.setBold(true);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);

        for (int i = 0; i < headers.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }

        // 数据行
        int rowIdx = 1;
        for (User u : users) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(u.getId());
            row.createCell(1).setCellValue(u.getUsername());
            row.createCell(2).setCellValue(u.getEmail() != null ? u.getEmail() : "");
            row.createCell(3).setCellValue(u.getRole() != null ? u.getRole() : "");
        }

        // 自动列宽
        for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);

        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition",
                "attachment; filename*=UTF-8''" + URLEncoder.encode("用户列表.xlsx", StandardCharsets.UTF_8));
        wb.write(response.getOutputStream());
        wb.close();
    }

    /**
     * 从 Excel 导入用户
     * 表单字段名: file
     */
    @PostMapping("/import-excel")
    public Map<String, Object> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook wb = new XSSFWorkbook(is);
        Sheet sheet = wb.getSheetAt(0);

        int created = 0, updated = 0, skipped = 0;
        List<String> errors = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;

            String username = getCellString(row, 0);   // 第1列: 用户名
            String password = getCellString(row, 1);   // 第2列: 密码
            String email    = getCellString(row, 2);   // 第3列: 邮箱
            String role     = getCellString(row, 3);   // 第4列: 角色

            if (username.isEmpty()) { skipped++; continue; }
            if (!username.matches("^[a-zA-Z0-9]+$")) {
                skipped++; errors.add(username + ": 用户名格式不合法"); continue;
            }

            try {
                User exist = userDAO.Find_By_Name(username);
                if (exist != null) {
                    exist.setEmail(!email.isEmpty() ? email : exist.getEmail());
                    exist.setRole(!role.isEmpty() ? role : exist.getRole());
                    if (!password.isEmpty() && password.matches("^[a-zA-Z0-9]+$")) {
                        exist.setPassword(password);
                    }
                    jdbcTemplate.update(
                        "UPDATE users SET email=?, role=?, password=? WHERE id=?",
                        exist.getEmail(), exist.getRole(), exist.getPassword(), exist.getId());
                    updated++;
                } else {
                    if (password.isEmpty()) { skipped++; errors.add(username + ": 新用户必须提供密码"); continue; }
                    if (!password.matches("^[a-zA-Z0-9]+$")) { skipped++; errors.add(username + ": 密码格式不合法"); continue; }
                    jdbcTemplate.update(
                        "INSERT INTO users (username, password, email, role) VALUES (?,?,?,?)",
                        username, password, email, role.isEmpty() ? "user" : role);
                    created++;
                }
            } catch (Exception e) {
                skipped++; errors.add(username + ": " + e.getMessage());
            }
        }

        wb.close();
        is.close();

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("ok", true);
        result.put("created", created);
        result.put("updated", updated);
        result.put("skipped", skipped);
        result.put("errors", errors);
        return result;
    }

    /**
     * 预览：上传 Excel → 返回 JSON 数组，前端填文本框
     */
    @PostMapping("/preview")
    public List<Map<String, Object>> preview(@RequestParam("file") MultipartFile file) throws IOException {
        InputStream is = file.getInputStream();
        Workbook wb = new XSSFWorkbook(is);
        Sheet sheet = wb.getSheetAt(0);
        List<Map<String, Object>> list = new ArrayList<>();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            String username = getCellString(row, 0);
            if (username.isEmpty()) continue;
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("username", username);
            m.put("password", getCellString(row, 1));
            m.put("email", getCellString(row, 2));
            m.put("role", getCellString(row, 3));
            list.add(m);
        }

        wb.close();
        is.close();
        return list;
    }

    private String getCellString(Row row, int col) {
        Cell cell = row.getCell(col);
        if (cell == null) return "";
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue().trim();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> "";
        };
    }
}
