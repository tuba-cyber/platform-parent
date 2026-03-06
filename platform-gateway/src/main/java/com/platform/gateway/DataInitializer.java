package com.platform.gateway;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.platform.gateway.company.entity.Company;
import com.platform.gateway.company.repository.CompanyRepository;
import com.platform.gateway.user.entity.Permission;
import com.platform.gateway.user.entity.Role;
import com.platform.gateway.user.entity.User;
import com.platform.gateway.user.repository.RoleRepository;
import com.platform.gateway.user.repository.UserRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final CompanyRepository companyRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    @Transactional
    public void run(String... args) {

        if (companyRepository.count() > 0) {
            log.info("Başlangıç verileri zaten mevcut, atlanıyor.");
            return;
        }

        log.info("Başlangıç verileri oluşturuluyor...");

        // Şirket oluştur
        Company company = new Company();
        company.setName("Platform A.Ş.");
        company.setCode("PLATFORM");
        company.setEmail("info@platform.com");
        company.setPhone("0212 000 00 00");
        company.setAddress("İstanbul, Türkiye");
        companyRepository.save(company);
        log.info("Şirket oluşturuldu: {}", company.getName());

        // Yetkiler oluştur
        Permission permUserView = createPermission(
            "USER_VIEW", "Kullanıcı görüntüleme", "USER"
        );
        Permission permUserEdit = createPermission(
            "USER_EDIT", "Kullanıcı düzenleme", "USER"
        );
        Permission permModuleView = createPermission(
            "MODULE_VIEW", "Modül görüntüleme", "MODULE"
        );
        Permission permModuleEdit = createPermission(
            "MODULE_EDIT", "Modül düzenleme", "MODULE"
        );
        Permission permCbsView = createPermission(
            "CBS_VIEW", "Harita görüntüleme", "CBS"
        );
        Permission permCbsEdit = createPermission(
            "CBS_EDIT", "Harita düzenleme", "CBS"
        );
        Permission permDashboardView = createPermission(
            "DASHBOARD_VIEW", "Dashboard görüntüleme", "DASHBOARD"
        );

        entityManager.flush();

        // Admin rolü
        Role adminRole = new Role();
        adminRole.setName("ADMIN");
        adminRole.setDescription("Sistem yöneticisi");
        adminRole.getPermissions().add(permUserView);
        adminRole.getPermissions().add(permUserEdit);
        adminRole.getPermissions().add(permModuleView);
        adminRole.getPermissions().add(permModuleEdit);
        adminRole.getPermissions().add(permCbsView);
        adminRole.getPermissions().add(permCbsEdit);
        adminRole.getPermissions().add(permDashboardView);
        roleRepository.save(adminRole);

        // User rolü
        Role userRole = new Role();
        userRole.setName("USER");
        userRole.setDescription("Standart kullanıcı");
        userRole.getPermissions().add(permModuleView);
        userRole.getPermissions().add(permCbsView);
        userRole.getPermissions().add(permDashboardView);
        roleRepository.save(userRole);

        log.info("Roller oluşturuldu: ADMIN, USER");

        entityManager.flush();

        // Admin kullanıcı
        User admin = new User();
        admin.setUsername("admin");
        admin.setEmail("admin@platform.com");
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setFirstName("Sistem");
        admin.setLastName("Yöneticisi");
        admin.setCompany(company);
        admin.getRoles().add(adminRole);
        userRepository.save(admin);

        // Test kullanıcısı
        User testUser = new User();
        testUser.setUsername("user");
        testUser.setEmail("user@platform.com");
        testUser.setPassword(passwordEncoder.encode("user123"));
        testUser.setFirstName("Test");
        testUser.setLastName("Kullanıcı");
        testUser.setCompany(company);
        testUser.getRoles().add(userRole);
        userRepository.save(testUser);

        log.info("Kullanıcılar oluşturuldu: admin, user");
        log.info("Başlangıç verileri başarıyla oluşturuldu!");
    }

    private Permission createPermission(
            String name, String description, String module) {
        Permission permission = new Permission();
        permission.setName(name);
        permission.setDescription(description);
        permission.setModule(module);
        entityManager.persist(permission);
        return permission;
    }
}