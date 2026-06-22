package org.example.Servlet;

import org.example.Entity.LoginUser;
import org.example.Entity.Message;
import org.example.Service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageService messageService;

    /*==========================================================================
     *                              用户端
     *==========================================================================*/

    // 获取已审核留言列表
    @GetMapping
    public List<Message> list() {
        return messageService.getApproved();
    }

    // 提交留言
    @PostMapping("/submit")
    public String submit(@RequestBody Message msg) {
        LoginUser loginUser = (LoginUser) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        msg.setUserId(loginUser.getUser().getId());
        int rows = messageService.submit(msg);
        return rows > 0 ? "提交成功，等待审核" : "提交失败";
    }

    /*==========================================================================
     *                              管理端
     *==========================================================================*/

    // 待审核列表
    @GetMapping("/pending")
    @PreAuthorize("hasAuthority('movie:manage')")
    public List<Message> pending() {
        return messageService.getPending();
    }

    // 审核通过
    @PostMapping("/approve")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String approve(@RequestParam Long id) {
        int rows = messageService.approve(id);
        return rows > 0 ? "审核通过" : "操作失败";
    }

    // 拒绝
    @PostMapping("/reject")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String reject(@RequestParam Long id) {
        int rows = messageService.reject(id);
        return rows > 0 ? "已拒绝" : "操作失败";
    }

    // 删除已发布留言
    @PostMapping("/delete")
    @PreAuthorize("hasAuthority('movie:manage')")
    public String delete(@RequestParam Long id) {
        int rows = messageService.delete(id);
        return rows > 0 ? "已删除" : "操作失败";
    }
}
