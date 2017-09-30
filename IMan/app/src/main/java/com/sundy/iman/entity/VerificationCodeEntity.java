package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by sundy on 17/9/30.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class VerificationCodeEntity extends BaseResEntity {

    private VerificationCodeEntity.DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private String verification_code;
    }
}
