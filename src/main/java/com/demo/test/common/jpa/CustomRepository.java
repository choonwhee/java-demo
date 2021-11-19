package com.demo.test.common.jpa;

import com.demo.test.common.workflow.dto.EntityTask;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@NoRepositoryBean
public interface CustomRepository<T, ID extends Serializable> extends JpaRepository<T, ID> {
    List<T> simpleSearch(Map<String, Object> searchParamMap);

    Page<T> simpleSearch(Pageable pageable, Map<String, Object> searchParamMap);

    List<T> search(Object searchDto);

    Page<T> search(Pageable pageable, Object searchDto);

    List<EntityTask<T>> simpleSearchTask(Map<String, Object> searchBy);

    Page<EntityTask<T>> simpleSearchTask(Pageable pageable, Map<String, Object> searchBy);

    List<EntityTask<T>> searchTask(Object searchDto);

    Page<EntityTask<T>> searchTask(Pageable pageable, Object searchDto);

    List<EntityTask<T>> simpleSearchTaskByUserRights(String userId, Collection<String> userRoles, Map<String, Object> searchBy);

    Page<EntityTask<T>> simpleSearchTaskByUserRights(Pageable pageable, String userId, Collection<String> userRoles, Map<String, Object> searchBy);

    List<EntityTask<T>> searchTaskByUserRights(String userId, Collection<String> userRoles, Object searchDto);

    Page<EntityTask<T>> searchTaskByUserRights(Pageable pageable, String userId, Collection<String> userRoles, Object searchDto);

}