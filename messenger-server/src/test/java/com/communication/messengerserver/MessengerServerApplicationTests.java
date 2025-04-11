package com.communication.messengerserver;

import com.communication.messengerserver.config.TestingBeans;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@Import(TestingBeans.class)
@SpringBootTest
class MessengerServerApplicationTests {

	@Test
	void contextLoads() {
	}

}
