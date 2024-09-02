package com.jack.userservice.repository;

import com.jack.userservice.entity.Users;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(SpringExtension.class)
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class UsersRepositoryTest {

    @Container
    public static PostgreSQLContainer<?> postgresqlContainer = new PostgreSQLContainer<>("postgres:latest")
            .withDatabaseName("testdb")
            .withUsername("testuser")
            .withPassword("testpass");

    @Autowired
    private UsersRepository usersRepository;

    private Users sampleUser;

    @DynamicPropertySource
    static void postgresqlProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
    }

    @BeforeEach
    void setUp() {
        sampleUser = createSampleUser();
    }

    private Users createSampleUser() {
        Users user = new Users();
        user.setName("Jack Lee");
        user.setEmail("jacklee@example.com");
        user.setPassword("encodedPassword");
        return user;
    }

    private Users saveSampleUser() {
        return usersRepository.save(sampleUser);
    }

    @Test
    void shouldCreateUserSuccessfully() {
        Users savedUser = saveSampleUser();
        assertThat(savedUser.getId()).isNotNull();
        assertThat(savedUser.getEmail()).isEqualTo("jacklee@example.com");
    }

    @Test
    void shouldFindUserByIdSuccessfully() {
        Users savedUser = saveSampleUser();
        Optional<Users> foundUser = usersRepository.findById(savedUser.getId());
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getEmail()).isEqualTo("jacklee@example.com");
    }

    @Test
    void shouldFindUserByEmailSuccessfully() {
        saveSampleUser();
        Optional<Users> foundUser = usersRepository.findByEmail("jacklee@example.com");
        assertThat(foundUser).isPresent();
        assertThat(foundUser.get().getName()).isEqualTo("Jack Lee");
    }

    @Test
    void shouldUpdateUserSuccessfully() {
        Users savedUser = saveSampleUser();
        savedUser.setName("Jack Lee Updated");
        Users updatedUser = usersRepository.save(savedUser);

        assertThat(updatedUser.getName()).isEqualTo("Jack Lee Updated");
    }

    @Test
    void shouldDeleteUserSuccessfully() {
        Users savedUser = saveSampleUser();
        usersRepository.deleteById(savedUser.getId());

        Optional<Users> deletedUser = usersRepository.findById(savedUser.getId());
        assertThat(deletedUser).isEmpty();
    }
}
