package org.example.Servlet;

import org.example.Entity.LoginUser;
import org.example.Entity.Resource_Submission;
import org.example.Service.ResourceSubmissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class ResourceSubmissionController {

    @Autowired
    private ResourceSubmissionService submissionService;

    /*-----------------------------------------------------------------*/
    /*                       普通用户                                   */
    /*-----------------------------------------------------------------*/

    @PostMapping("/SUBMIT/RESOURCE")
    public Map<String, Object> submit(@RequestBody Resource_Submission sub) {
        try {
            var auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.getPrincipal() instanceof LoginUser) {
                sub.setSubmitter_id(((LoginUser) auth.getPrincipal()).getUser().getId());
            }
        } catch (Exception ignored) {}
        int result = submissionService.submit(sub);
        if (result > 0) {
            return Map.of("code", 200, "msg", "提交成功，等待审核");
        }
        return Map.of("code", 500, "msg", "提交失败");
    }

    /*-----------------------------------------------------------------*/
    /*                       管理员审核                                  */
    /*-----------------------------------------------------------------*/

    @GetMapping("/AUDIT/PENDING")
    public List<Resource_Submission> pending() {
        return submissionService.listPending();
    }

    @GetMapping("/AUDIT/ALL")
    public List<Resource_Submission> all() {
        return submissionService.listAll();
    }

    @PostMapping("/AUDIT/APPROVE")
    public Map<String, Object> approve(@RequestParam("id") int id) {
        submissionService.approve(id);
        return Map.of("code", 200, "msg", "已通过并同步到资源表");
    }

    @PostMapping("/AUDIT/REJECT")
    public Map<String, Object> reject(@RequestParam("id") int id,
                                       @RequestParam(value = "reason", defaultValue = "") String reason) {
        submissionService.reject(id, reason);
        return Map.of("code", 200, "msg", "已拒绝");
    }
}
