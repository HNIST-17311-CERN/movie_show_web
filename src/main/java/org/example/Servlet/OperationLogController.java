package org.example.Servlet;

import org.example.DAO.OperationLogDAO;
import org.example.Entity.OperationLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin(origins = "*")
public class OperationLogController {

    @Autowired
    private OperationLogDAO operationLogDAO;

    @GetMapping("/recent")
    public List<OperationLog> recent(@RequestParam(value = "limit", defaultValue = "50") int limit) {
        return operationLogDAO.findRecent(limit);
    }

    @GetMapping("/user")
    public List<OperationLog> byUser(
            @RequestParam String username,
            @RequestParam(value = "limit", defaultValue = "50") int limit) {
        return operationLogDAO.findByUser(username, limit);
    }
}
