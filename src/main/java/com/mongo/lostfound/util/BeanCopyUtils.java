package com.mongo.lostfound.util;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.BeanUtils;

public class BeanCopyUtils {

    public static <S, T> void copyNonNullProperties(S source, T target) {
        if (source == null || target == null) return;

        Map<String, Object> sourceValues = new HashMap<>();
        for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(source.getClass())) {
            try {
                Method readMethod = pd.getReadMethod();
                if (readMethod != null && !"class".equals(pd.getName())) {
                    Object value = readMethod.invoke(source);
                    if (value != null) {
                        sourceValues.put(pd.getName(), value);
                    }
                }
            } catch (Exception ignored) {}
        }

        for (PropertyDescriptor pd : BeanUtils.getPropertyDescriptors(target.getClass())) {
            if (sourceValues.containsKey(pd.getName())) {
                try {
                    Method writeMethod = pd.getWriteMethod();
                    if (writeMethod != null) {
                        writeMethod.invoke(target, sourceValues.get(pd.getName()));
                    }
                } catch (Exception ignored) {}
            }
        }
    }
}
