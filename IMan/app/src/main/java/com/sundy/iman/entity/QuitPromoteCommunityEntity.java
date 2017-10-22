package com.sundy.iman.entity;

import java.io.Serializable;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

/**
 * Created by sundy on 17/10/22.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class QuitPromoteCommunityEntity extends BaseResEntity {
    private DataEntity data;

    @Data
    public static class DataEntity implements Serializable {
    }
}
