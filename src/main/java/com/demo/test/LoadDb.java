package com.demo.test;

import com.demo.test.common.user.dto.User;
import com.demo.test.common.user.repository.UserRepository;
import com.demo.test.common.workflow.dto.Status;
import com.demo.test.common.workflow.repository.StatusRepository;
import com.demo.test.customer.dto.Customer;
import com.demo.test.customer.repository.CustomerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

@Configuration
public class LoadDb {
    private static final Logger log = LoggerFactory.getLogger(LoadDb.class);

    @Bean
    CommandLineRunner
    initDb(CustomerRepository customerRepository, UserRepository userRepository, StatusRepository statusRepository) {
        return args -> {
            User user = new User();
            user.setId("1111111");
            user.setPassword("password");
            user.setFirstName("John");
            user.setLastName("Smith");
            user.setEmail("js@gmail.com");
            List<String> roles = new ArrayList<>();
            user.setLineManager("2222222");
            roles.add("OPS");
            user.setRoles(roles);
            log.info("Preloading User " + userRepository.save(user));

            user = new User();
            user.setId("2222222");
            user.setPassword("password");
            user.setFirstName("Jane");
            user.setLastName("Doe");
            user.setEmail("jane@yahoo.com");
            user.setLineManager("6666666");
            roles = new ArrayList<>();
            roles.add("VIEWER");
            user.setRoles(roles);
            log.info("Preloading User " + userRepository.save(user));

            user = new User();
            user.setId("3333333");
            user.setPassword("password");
            user.setFirstName("Clark");
            user.setLastName("Kent");
            user.setEmail("sm@aol.com");
            user.setLineManager("5555555");
            roles = new ArrayList<>();
            roles.add("CUS_FR_GROUP");
            user.setRoles(roles);
            log.info("Preloading User " + userRepository.save(user));

            user = new User();
            user.setId("4444444");
            user.setPassword("password");
            user.setFirstName("Bruce");
            user.setLastName("Wayne");
            user.setEmail("batman@hotmail.com");
            user.setLineManager("5555555");
            roles = new ArrayList<>();
            roles.add("CUS_FR_GROUP");
            user.setRoles(roles);
            log.info("Preloading User " + userRepository.save(user));

            user = new User();
            user.setId("5555555");
            user.setPassword("password");
            user.setFirstName("Billy");
            user.setLastName("Batson");
            user.setEmail("shazam@gmail.com");
            user.setLineManager("6666666");
            roles = new ArrayList<>();
            roles.add("VIEWER");
            user.setRoles(roles);
            log.info("Preloading User " + userRepository.save(user));

            user = new User();
            user.setId("6666666");
            user.setPassword("password");
            user.setFirstName("Diana");
            user.setLastName("Prince");
            user.setEmail("ww@hotmail.com");
            roles = new ArrayList<>();
            roles.add("VIEWER");
            user.setRoles(roles);
            log.info("Preloading User " + userRepository.save(user));

            log.info("Preloading Status " + statusRepository.save(new Status("open", "Open")));
            log.info("Preloading Status " + statusRepository.save(new Status("saved", "Saved")));
            log.info("Preloading Status " + statusRepository.save(new Status("submitted", "Submitted")));
            log.info("Preloading Status " + statusRepository.save(new Status("approved", "Approved")));
            log.info("Preloading Status " + statusRepository.save(new Status("rejected", "Rejected")));

            log.info("Preloading Customer " + customerRepository.save(new Customer("Choon Whee", "Peh", "34 Jurong East St92", "SG", "98543210" ,"cwp@gmail.com", "Test First Line Review Comments.", "Test Final Review Comments.")));
            log.info("Preloading Customer " + customerRepository.save(new Customer("John", "Smith", "46 Madison Ave, Manhattan, NY", "US", "134709876543" ,"js@yahoo.com")));
            log.info("Preloading Customer " + customerRepository.save(new Customer("Tony", "Stark", "Avengers HQ", "US","43243242" ,"iron_man@avengers.com")));
            log.info("Preloading Customer " + customerRepository.save(new Customer("Thor", "Odinson", "Asgard ", "AG", "324314" ,"thor@palace.asgard")));
            log.info("Preloading Customer " + customerRepository.save(new Customer("Bruce", "Banner", "Greenlane 2345", null , "4343442" ,"hulk@avengers.com")));
            log.info("Preloading Customer " + customerRepository.save(new Customer("Steve", "Rogers", "America, NY", "US", "41342342" ,"cap@america.com")));
            log.info("Preloading Customer " + customerRepository.save(new Customer("Clint", "Barton", "Seoul", "SK", "4444534564" ,"hawk@eye.com")));
            log.info("Preloading Customer " + customerRepository.save(new Customer("Wanda", "Maximoff", "Sokovia", "SO", "63433535" ,"wanda@vision.com")));
            log.info("Preloading Customer " + customerRepository.save(new Customer("T", "Challa", "Wakanda", "WK", "43545245" ,"black.panther@wakanda.gov")));
            log.info("Preloading Customer " + customerRepository.save(new Customer("Pietro", "Maximoff", "Sokovia", "SO", "2455234545" ,"quick@silver.org")));
            log.info("Preloading Customer " + customerRepository.save(new Customer("Logan", "Howlett", "Forested Area", "UK","75745747" ,"wolverine@xmen.com")));
            log.info("Preloading Customer " + customerRepository.save(new Customer("Wade", "Wilson", "Hello Kitty Land", "JP", "23749533" ,"deadpool@greenlantern.com")));
        };
    }

}
