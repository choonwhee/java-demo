package com.demo.test.common.workflow.camunda.delegates;

import org.camunda.bpm.engine.delegate.DelegateTask;
import org.camunda.bpm.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Named;

@Named("emailNotificationTaskListener")
public class EmailNotificationTaskListener implements TaskListener {
    Logger logger = LoggerFactory.getLogger(EmailNotificationTaskListener.class);

    @Override
    public void notify(DelegateTask delegateTask) {
        logger.info("EmailNotificationTaskDelegate Notify | delegateTask: " + delegateTask.getName());
    }
}
