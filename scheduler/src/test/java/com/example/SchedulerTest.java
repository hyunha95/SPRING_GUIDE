package com.example;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.SpyBean;

import java.util.concurrent.TimeUnit;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.verify;

@SpringBootTest
public class SchedulerTest {

    @SpyBean
    ScheduledTask scheduledTask;

    @Test
    void reportCurrentTime() {
        await().atMost(10, TimeUnit.SECONDS).untilAsserted(() -> {
            verify(scheduledTask, atLeast(2)).reportCurrentTime();
        });
    }
}
