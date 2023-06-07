package com.xiuGEN.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

/**
 * 
 * @TableName logging_event_exception
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class LoggingEventException implements Serializable {
    /**
     * 
     */
    private Long event_id;

    /**
     * 
     */
    private Integer i;
    /**
     * 
     */
    private String trace_line;

    private static final long serialVersionUID = 1L;

}