package com.backend.apirest.repository;
import com.backend.apirest.squema.task;
import com.backend.apirest.squema.TaskStatus;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface TaskRepository extends MongoRepository<task, String> {
    List<task> findByStatus(TaskStatus status, Sort sort);
    List<task> findByTitleContaining(String keyword, Sort sort);
    List<task> findByStatusAndTitleContaining(TaskStatus status, String keyword, Sort sort);
   

}

