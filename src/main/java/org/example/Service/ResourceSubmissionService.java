package org.example.Service;

import org.example.DAO.Movie_ResourceDAO;
import org.example.DAO.ResourceSubmissionDAO;
import org.example.Entity.Movie_Resource;
import org.example.Entity.Resource_Submission;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResourceSubmissionService {

    @Autowired
    private ResourceSubmissionDAO submissionDAO;

    @Autowired
    private Movie_ResourceDAO resourceDAO;

    public int submit(Resource_Submission sub) {
        return submissionDAO.insert(sub);
    }

    public List<Resource_Submission> listPending() {
        return submissionDAO.findByStatus("pending");
    }

    public List<Resource_Submission> listAll() {
        return submissionDAO.findAll();
    }

    @Transactional
    public void approve(int id) {
        submissionDAO.updateStatus(id, "approved", null);

        // 查询提交记录
        List<Resource_Submission> all = submissionDAO.findAll();
        Resource_Submission sub = all.stream()
                .filter(s -> s.getId() == id)
                .findFirst()
                .orElse(null);
        if (sub == null) return;

        // 复制到 movie_resource
        Movie_Resource resource = new Movie_Resource();
        resource.setMovieId((long) sub.getMovie_id());
        resource.setName(sub.getResouce_name());
        resource.setUrl(sub.getUrl());
        resource.setType(sub.getType() != null ? sub.getType() : "磁力");
        resource.setQuality(sub.getQuality());
        resource.setSize(sub.getSize());
        resource.setCreateTime(sub.getCreateTime());
        resource.setSubtitle("中字");

        resourceDAO.insert(resource);
    }

    public void reject(int id, String msg) {
        submissionDAO.updateStatus(id, "rejected", msg);
    }
}
