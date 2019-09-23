/*
 * Copyright (c) 2019. This code is purely educational, the rights of use are
 * reserved, the owner of the code is Martin Osorio,
 * contact mob010@alumnos.ucn.cl
 * Do not use in production.
 */


import java.time.LocalDateTime;
import java.util.Date;
import java.time.format.DateTimeFormatter;


/**
 *  ChatMessage class represent a message.
 *
 *  @author Martin Osorio.
 */
public class ChatMessage {

    private String username;
    private String message;
    private final LocalDateTime timestamp;
    private Date timeStamp;

    public ChatMessage(String username, String message){

        this.timeStamp = new Date();
        this.username = username;
        this.message = message;
        timestamp = LocalDateTime.now();
    }

    /**
     * This return = the username [date]:message
     */
    @Override
    public String toString() {

        StringBuilder msn = new StringBuilder();
        DateTimeFormatter time = DateTimeFormatter.ofPattern("yyyy-MM-dd | HH:mm:ss");


        msn.append(username).append(" ").append("[").append(timestamp.format(time)).append("]").append(": ").append(message);

        return msn.toString();
    }

    public Date getTimeStamp() {
        return timeStamp;
    }

    public String getUsername() {
        return username;
    }

    public String getMessage() {
        return message;
    }

}