package org.example.Servlet;

import org.example.DAO.DocDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/docs")
@CrossOrigin(origins = "*")
public class DocController {

    @Autowired
    DocDAO docDAO;

    /**
     * 保存或更新文档
     */
    @PostMapping("/save")
    public Map<String, Object> save(@RequestBody Map<String, Object> body) {
        String title = (String) body.getOrDefault("title", "未命名文档");
        String html = (String) body.getOrDefault("htmlContent", "");
        String text = (String) body.getOrDefault("textContent", "");
        String delta = (String) body.getOrDefault("deltaJson", "");
        Object idObj = body.get("id");

        long docId;
        if (idObj != null && !"".equals(String.valueOf(idObj))) {
            docId = Long.parseLong(String.valueOf(idObj));
            docDAO.Update(docId, title, html, text, delta);
        } else {
            docId = docDAO.Insert(title, html, text, delta);
        }

        docDAO.Sync_Files(docId, html);
        List<Map<String, Object>> files = docDAO.Find_Files_By_DocId(docId);

        String action = (idObj != null && !"".equals(String.valueOf(idObj))) ? "updated" : "created";
        return Map.of("ok", true, "id", docId, "action", action, "files", files.size());
    }

    @GetMapping("/{id}")
    public Map<String, Object> load(@PathVariable Long id) {
        Map<String, Object> doc = docDAO.Find_By_Id(id);
        if (doc == null) return Map.of("ok", false, "msg", "文档不存在");
        doc.put("ok", true);
        doc.put("files", docDAO.Find_Files_By_DocId(id));
        return doc;
    }

    @GetMapping("/list")
    public List<Map<String, Object>> list() {
        return docDAO.Find_All();
    }

    @DeleteMapping("/{id}")
    public Map<String, Object> delete(@PathVariable Long id) {
        docDAO.Delete_Files_By_DocId(id);
        docDAO.Delete_By_Id(id);
        return Map.of("ok", true);
    }
}
