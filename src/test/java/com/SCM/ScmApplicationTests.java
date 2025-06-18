package com.SCM;

import com.SCM.role.RoleInitializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
@TestConfiguration
@ImportAutoConfiguration(exclude = RoleInitializer.class)
class ScmApplicationTests {
	@Test
	@Disabled("Disabling temporarily for CI")
	void contextLoads() {
	}
}
