module com.example.chatonjavafx {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.slf4j;
    requires org.postgresql.jdbc;
    requires org.hibernate.orm.core;
    requires jakarta.persistence;
    requires jakarta.transaction;
    requires lombok;
    requires com.fasterxml.jackson.annotation;
    requires com.fasterxml.jackson.databind;
    requires com.fasterxml.jackson.datatype.jsr310;
    requires java.logging;
    requires java.naming;

    opens rocket_chat to javafx.fxml;
    opens rocket_chat.entity;
    exports rocket_chat;
    exports rocket_chat.entity;
}