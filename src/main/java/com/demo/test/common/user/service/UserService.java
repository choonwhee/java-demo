package com.demo.test.common.user.service;

import com.demo.test.common.service.AutoService;
import com.demo.test.common.service.BaseAutoService;
import com.demo.test.common.user.dto.User;
import com.demo.test.common.user.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@AutoService(entityClass = User.class, entityRepoClass = UserRepository.class)
public interface UserService extends BaseAutoService<User, String, UserRepository> {
    public Optional<User> findById(String id);

    public List<User> search(Object searchDto);

    public List<User> simpleSearch(Map<String, Object> searchParamMap);
}
