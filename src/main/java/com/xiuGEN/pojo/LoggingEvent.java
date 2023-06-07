package com.xiuGEN.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.scheduling.annotation.Async;

import java.io.Serializable;


@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
/**
 * 
 * @TableName logging_event
 */
public class LoggingEvent implements Serializable {
    /**
     * 
     */
    private Long event_id;

    /**
     * 
     */
    private Long timestmp;

    /**
     * 
     */
    private String formatted_message;

    /**
     * 
     */
    private String logger_name;

    /**
     * 
     */
    private String level_string;

    /**
     * 
     */
    private String thread_name;

    /**
     * 
     */
    private Integer reference_flag;

    /**
     * 
     */
    private String arg0;

    /**
     * 
     */
    private String arg1;

    /**
     * 
     */
    private String arg2;

    /**
     * 
     */
    private String arg3;

    /**
     * 
     */
    private String caller_filename;

    /**
     * 
     */
    private String caller_class;

    /**
     * 
     */
    private String caller_method;

    /**
     * 
     */
    private String caller_line;

    private String loggingTime;

}