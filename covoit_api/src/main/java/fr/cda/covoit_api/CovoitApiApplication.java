package fr.cda.covoit_api;

import fr.cda.covoit_api.domain.entity.Role;
import fr.cda.covoit_api.domain.entity.Status;
import fr.cda.covoit_api.repository.RoleRepository;
import fr.cda.covoit_api.repository.StatusRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CovoitApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(CovoitApiApplication.class, args);
	}

    @Bean
    CommandLineRunner init(RoleRepository roleRepo, StatusRepository statusRepo) {
        return args -> {
            if (roleRepo.findByLabel("USER").isEmpty()) {
                roleRepo.save(new Role(null, "USER"));
                roleRepo.save(new Role(null, "ADMIN"));
            }
            if (statusRepo.findByLabel("ACTIVE").isEmpty()) {
                statusRepo.save(new Status(null, "ACTIVE"));
                statusRepo.save(new Status(null, "BANNED"));
            }
        };
    }

}
