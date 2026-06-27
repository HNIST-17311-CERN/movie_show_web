package org.example.DAO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import java.nio.file.*;
import java.util.*;
import java.util.regex.*;

@Repository
public class DocDAO {

    @Autowired
    JdbcTemplate jdbcTemplate;

    private final RowMapper<Map<String, Object>> DocRowMapper = (rs, rowNum) -> {
        Map<String, Object> m = new LinkedHashMap<>();
        m.put("id", rs.getLong("id"));
        m.put("title", rs.getString("title"));
        m.put("htmlContent", rs.getString("html_content"));
        m.put("textContent", rs.getString("text_content"));
        m.put("deltaJson", rs.getString("delta_json"));
        m.put("createTime", rs.getTimestamp("create_time"));
        m.put("updateTime", rs.getTimestamp("update_time"));
        return m;
    };

    /*-----------------------------------------------------------------*/
    /*                         文档 CRUD                                */
    /*-----------------------------------------------------------------*/

    public long Insert(String title, String html, String text, String delta) {
        String sql = "INSERT INTO richtext_docs (title, html_content, text_content, delta_json, create_time, update_time) VALUES (?,?,?,?,NOW(),NOW())";
        jdbcTemplate.update(sql, title, html, text, delta);
        Long id = jdbcTemplate.queryForObject("SELECT LAST_INSERT_ID()", Long.class);
        return id != null ? id : 0;
    }

    public int Update(long id, String title, String html, String text, String delta) {
        String sql = "UPDATE richtext_docs SET title=?, html_content=?, text_content=?, delta_json=?, update_time=NOW() WHERE id=?";
        return jdbcTemplate.update(sql, title, html, text, delta, id);
    }

    public Map<String, Object> Find_By_Id(long id) {
        String sql = "SELECT * FROM richtext_docs WHERE id = ?";
        List<Map<String, Object>> list = jdbcTemplate.query(sql, DocRowMapper, id);
        return list.isEmpty() ? null : list.get(0);
    }

    public List<Map<String, Object>> Find_All() {
        String sql = "SELECT id, title, text_content AS preview, create_time, update_time FROM richtext_docs ORDER BY update_time DESC";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", rs.getLong("id"));
            m.put("title", rs.getString("title"));
            m.put("preview", rs.getString("preview"));
            m.put("createTime", rs.getTimestamp("create_time"));
            m.put("updateTime", rs.getTimestamp("update_time"));
            return m;
        });
    }

    public int Delete_By_Id(long id) {
        String sql = "DELETE FROM richtext_docs WHERE id = ?";
        return jdbcTemplate.update(sql, id);
    }

    /*-----------------------------------------------------------------*/
    /*                       文件关联 CRUD                               */
    /*-----------------------------------------------------------------*/

    public int Delete_Files_By_DocId(long docId) {
        String sql = "DELETE FROM doc_files WHERE doc_id = ?";
        return jdbcTemplate.update(sql, docId);
    }

    public int Insert_File(long docId, String fileUrl, String fileName, long fileSize) {
        String sql = "INSERT INTO doc_files (doc_id, file_url, file_name, file_size) VALUES (?,?,?,?)";
        return jdbcTemplate.update(sql, docId, fileUrl, fileName, fileSize);
    }

    public List<Map<String, Object>> Find_Files_By_DocId(long docId) {
        String sql = "SELECT * FROM doc_files WHERE doc_id = ? ORDER BY id";
        return jdbcTemplate.query(sql, (rs, rowNum) -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", rs.getLong("id"));
            m.put("docId", rs.getLong("doc_id"));
            m.put("fileUrl", rs.getString("file_url"));
            m.put("fileName", rs.getString("file_name"));
            m.put("fileSize", rs.getLong("file_size"));
            m.put("createTime", rs.getTimestamp("create_time"));
            return m;
        }, docId);
    }

    /*-----------------------------------------------------------------*/
    /*                       文件解析 + 同步                             */
    /*-----------------------------------------------------------------*/

    /**
     * 从 HTML 中提取所有文件引用（img src 和 a href）
     */
    public List<String[]> Extract_Files(String html) {
        List<String[]> files = new ArrayList<>();
        Set<String> seen = new HashSet<>();
        Pattern p = Pattern.compile("(?:src|href)=\"(/api/files/download/[^\"]+)\"");
        Matcher matcher = p.matcher(html);
        while (matcher.find()) {
            String url = matcher.group(1);
            String clean = url.replaceAll("\\?.*", "");
            if (seen.add(clean)) {
                String name = url.contains("?name=")
                        ? url.replaceAll(".*\\?name=", "")
                        : Paths.get(clean).getFileName().toString();
                files.add(new String[]{clean, name});
            }
        }
        return files;
    }

    /**
     * 根据文件 URL 从磁盘查文件大小
     */
    public long Get_File_Size(String url) {
        try {
            String rel = url.replace("/api/files/download/", "");
            Path p = Paths.get("uploads/files", rel);
            return Files.exists(p) ? Files.size(p) : 0;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 同步关联文件：清旧 → 解析 HTML → 写新
     */
    public void Sync_Files(long docId, String html) {
        Delete_Files_By_DocId(docId);
        List<String[]> files = Extract_Files(html);
        for (String[] f : files) {
            long size = Get_File_Size(f[0]);
            Insert_File(docId, f[0], f[1], size);
        }
    }
}
