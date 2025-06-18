package com.SCM;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class ScmApplicationTests {

	@Test
	@Disabled("Disabling temporarily for CI")
	void contextLoads() {
	}
}
