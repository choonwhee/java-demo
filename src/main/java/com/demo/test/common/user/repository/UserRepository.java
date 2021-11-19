package com.demo.test.common.user.repository;

import com.demo.test.common.jpa.CustomRepository;
import com.demo.test.common.user.dto.User;

public interface UserRepository  extends CustomRepository<User, String> {

}
