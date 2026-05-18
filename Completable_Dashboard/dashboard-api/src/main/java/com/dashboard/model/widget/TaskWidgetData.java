package com.dashboard.model.widget;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class TaskWidgetData {
    private String       widgetId;
    private int          totalTasks;
    private int          pendingCount;
    private int          completedCount;
    private int          overdueCount;
    private List<Task>   tasks;
    private String       status;
    private Instant      fetchedAt;

    @Data
    @Builder
    public static class Task {
        private String  taskId;
        private String  title;
        private String  priority;    // LOW | MEDIUM | HIGH | CRITICAL
        private String  taskStatus;  // PENDING | IN_PROGRESS | DONE | OVERDUE
        private Instant dueDate;
        private String  assignee;
    }

    public static TaskWidgetData fallback() {
        return TaskWidgetData.builder()
                .widgetId("tasks")
                .totalTasks(0)
                .pendingCount(0)
                .completedCount(0)
                .overdueCount(0)
                .tasks(List.of())
                .status("DEGRADED")
                .fetchedAt(Instant.now())
                .build();
    }
}
