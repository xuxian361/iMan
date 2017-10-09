package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by sundy on 17/10/9.
 */

@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class CountryCodeEntity extends BaseResEntity {

    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private String id;
        private String name;
        private String code;

    }

}
