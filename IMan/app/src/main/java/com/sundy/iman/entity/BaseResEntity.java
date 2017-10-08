package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by sundy on 17/9/20.
 */

@Data
@ToString
@EqualsAndHashCode
public class BaseResEntity implements Serializable {

    protected int code; //0: success
    protected String msg;

}
