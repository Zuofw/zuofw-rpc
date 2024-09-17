package com.zuofw.rpc.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 〈User实体类〉
 *
 * @author zuofw
 * @create 2024/9/7
 * @since 1.0.0
 */
@Data
public class User implements Serializable {
    private String name;
}