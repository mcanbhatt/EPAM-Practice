package com.dashboard.service;

import com.dashboard.model.widget.TaskWidgetData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class TaskService {

    @Cacheable(value = "widget:tasks", key = "#userId")
    public TaskWidgetData getTasks(String userId) {
        log.debug("[TaskService] Fetching tasks for user={}", userId);
        simulateLatency(60, 120);

        List<TaskWidgetData.Task> tasks = List.of(
                TaskWidgetData.Task.builder()
                        .taskId(UUID.randomUUID().toString())
                        .title("Review Q2 performance report")
                        .priority("HIGH").taskStatus("IN_PROGRESS")
                        .dueDate(Instant.now().plusSeconds(86400))
                        .assignee(userId).build(),

                TaskWidgetData.Task.builder()
                        .taskId(UUID.randomUUID().toString())
                        .title("Update API documentation")
                        .priority("MEDIUM").taskStatus("PENDING")
                        .dueDate(Instant.now().plusSeconds(172800))
                        .assignee(userId).build(),

                TaskWidgetData.Task.builder()
                        .taskId(UUID.randomUUID().toString())
                        .title("Fix login page bug #4821")
                        .priority("CRITICAL").taskStatus("OVERDUE")
                        .dueDate(Instant.now().minusSeconds(3600))
                        .assignee(userId).build(),

                TaskWidgetData.Task.builder()
                        .taskId(UUID.randomUUID().toString())
                        .title("Deploy hotfix to production")
                        .priority("HIGH").taskStatus("DONE")
                        .dueDate(Instant.now().minusSeconds(7200))
                        .assignee(userId).build()
        );

        long pending   = tasks.stream().filter(t -> "PENDING".equals(t.getTaskStatus())).count();
        long completed = tasks.stream().filter(t -> "DONE".equals(t.getTaskStatus())).count();
        long overdue   = tasks.stream().filter(t -> "OVERDUE".equals(t.getTaskStatus())).count();

        return TaskWidgetData.builder()
                .widgetId("tasks")
                .totalTasks(tasks.size())
                .pendingCount((int) pending)
                .completedCount((int) completed)
                .overdueCount((int) overdue)
                .tasks(tasks)
                .status("OK")
                .fetchedAt(Instant.now())
                .build();
    }

    private void simulateLatency(int minMs, int maxMs) {
        try {
            Thread.sleep(minMs + (long)(Math.random() * (maxMs - minMs)));
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
