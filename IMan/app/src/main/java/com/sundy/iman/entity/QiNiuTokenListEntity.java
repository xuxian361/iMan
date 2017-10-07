package com.sundy.iman.entity;

import java.io.Serializable;
import java.util.List;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by sundy on 17/10/2.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QiNiuTokenListEntity extends BaseResEntity {

    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
        private List<QiNiuTokenItemEntity> list;
    }

}
