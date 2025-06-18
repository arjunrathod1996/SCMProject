package com.SCM.testLogAppender;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.AppenderBase;
import java.util.ArrayList;
import java.util.List;

public class TestLogAppender extends AppenderBase<ILoggingEvent> {
    private final List<ILoggingEvent> logsList = new ArrayList<>();

    @Override
    protected void append(ILoggingEvent eventObject) {
        logsList.add(eventObject);
    }

    public List<ILoggingEvent> getLog() {
        return new ArrayList<>(logsList);
    }

    public void clear() {
        logsList.clear();
    }
}