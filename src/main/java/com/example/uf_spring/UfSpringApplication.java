package com.example.uf_spring;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import com.example.uf_spring.model.User;
import com.example.uf_spring.model.Role;
import com.example.uf_spring.repository.UserRepository;

@SpringBootApplication
public class UfSpringApplication {

	public static void main(String[] args) {
		SpringApplication.run(UfSpringApplication.class, args);
	}

	@Bean
	public CommandLineRunner initData(UserRepository userRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			// 기존 데이터가 없을 때만 초기화
			if (userRepository.count() == 0) {
				System.out.println("=== 테스트 데이터 초기화 시작 ===");
				
				// admin 유저 생성 (비밀번호: admin)
				String adminPassword = passwordEncoder.encode("admin");
				User admin = new User("관리자", "admin", adminPassword, 30, Role.ADMIN);
				userRepository.save(admin);
				System.out.println("✅ admin 유저 생성 완료 (비밀번호: admin)");
				System.out.println("   해시값: " + adminPassword);
				
				// user 유저 생성 (비밀번호: user)
				String userPassword = passwordEncoder.encode("user");
				User user = new User("테스트유저", "user", userPassword, 25, Role.USER);
				userRepository.save(user);
				System.out.println("✅ user 유저 생성 완료 (비밀번호: user)");
				System.out.println("   해시값: " + userPassword);
				
				// 비밀번호 검증 테스트
				System.out.println("🔍 비밀번호 검증 테스트:");
				System.out.println("   admin/admin 매칭: " + passwordEncoder.matches("admin", adminPassword));
				System.out.println("   user/user 매칭: " + passwordEncoder.matches("user", userPassword));
				System.out.println("   admin/user 매칭: " + passwordEncoder.matches("admin", userPassword));
				System.out.println("   user/admin 매칭: " + passwordEncoder.matches("user", adminPassword));
				
				System.out.println("=== 테스트 데이터 초기화 완료 ===");
				System.out.println("📋 생성된 테스트 계정:");
				System.out.println("   - admin/admin (관리자)");
				System.out.println("   - user/user (일반 사용자)");
			} else {
				System.out.println("ℹ️ 기존 데이터가 존재하여 초기화를 건너뜁니다.");
			}
		};
	}
}
