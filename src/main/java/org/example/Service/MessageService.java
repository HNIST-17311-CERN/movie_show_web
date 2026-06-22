package org.example.Service;

import org.example.DAO.MessageDAO;
import org.example.Entity.Message;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MessageService {

    @Autowired
    private MessageDAO messageDAO;

    // 获取已审核留言列表
    public List<Message> getApproved() {
        return messageDAO.findApproved();
    }

    // 用户提交留言 → 进待审核表
    public int submit(Message msg) {
        return messageDAO.insertPending(msg);
    }

    // 管理端：待审核列表
    public List<Message> getPending() {
        return messageDAO.findPending();
    }

    // 管理端：审核通过
    public int approve(Long pendingId) {
        Message msg = messageDAO.findPendingById(pendingId);
        if (msg == null) return 0;
        messageDAO.insertApproved(msg);
        messageDAO.deletePending(pendingId);
        return 1;
    }

    // 管理端：拒绝
    public int reject(Long pendingId) {
        return messageDAO.deletePending(pendingId);
    }

    // 管理端：删除已发布留言
    public int delete(Long id) {
        return messageDAO.deleteApproved(id);
    }
}
