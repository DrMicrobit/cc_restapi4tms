package com.example.task_management_api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/******************************************************************************
 *
 * @implNote For this toy project, no own exception handling class was programmed which would handle
 *           logging, alerts, etc. As a side effect, exceptions thrown that handle bad user input
 *           also directly throw HTTP errors for the controller to answer directly. That is not
 *           proper separation of concerns, but enough to get this toy project going.
 * 
 * @implNote No injection of specific repository implementation via interface atm. Would be better
 *           to allow different implementations (e.g. in-memory, database, mock for testing). But
 *           simplicity first.
 *
 ******************************************************************************/


@SpringBootApplication
public class TaskManagementApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(TaskManagementApiApplication.class, args);
    }

}
