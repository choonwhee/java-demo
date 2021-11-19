package com.demo.test.common.workflow.dto;

public class EntityTask<T> {

    private T entity;
    private Task task;

    public EntityTask() {
        super();
    }

    public EntityTask(T entity, Task task) {
        this.entity = entity;
        this.task = task;
    }

    @Override
    public String toString() {
        return "EntityTask{" +
                "entity=" + entity +
                ", task=" + task +
                '}';
    }

    public T getEntity() {
        return entity;
    }

    public void setEntity(T entity) {
        this.entity = entity;
    }

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }
}
