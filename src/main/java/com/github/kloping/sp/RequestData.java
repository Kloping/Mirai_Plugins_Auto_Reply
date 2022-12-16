package com.github.kloping.sp;

import lombok.Data;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Nullable;

/**
 * @author github.kloping
 */
@Data
@Accessors(chain = true)
public class RequestData {
    @Nullable
    private String key, value;
    private Integer index, type;
}
