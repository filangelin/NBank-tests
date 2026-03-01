package api.dao.comparison;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Objects;

public class DaoComparator {

    private final DaoComparisonConfigLoader configLoader;

    public DaoComparator() {
        this.configLoader = new DaoComparisonConfigLoader("dao-comparison.properties");
    }

    public void compare(Object apiResponse, Object dao) {

        if (apiResponse == null) {
            throw new AssertionError("API response should not be null");
        }

        if (dao == null) {
            throw new AssertionError("DAO object should not be null");
        }

        DaoComparisonConfigLoader.DaoComparisonRule rule =
                configLoader.getRuleFor(apiResponse.getClass());

        if (rule == null) {
            throw new RuntimeException(
                    "No comparison rule found for " +
                            apiResponse.getClass().getSimpleName());
        }

        Map<String, String> fieldMappings = rule.getFieldMappings();

        for (Map.Entry<String, String> mapping : fieldMappings.entrySet()) {

            String apiFieldName = mapping.getKey();
            String daoFieldName = mapping.getValue();

            Object apiValue = getFieldValue(apiResponse, apiFieldName);
            Object daoValue = getFieldValue(dao, daoFieldName);

            if (!areEqual(apiValue, daoValue)) {
                throw new AssertionError(String.format(
                        "Field mismatch for %s: API=%s, DAO=%s",
                        apiFieldName, apiValue, daoValue));
            }
        }
    }

    private boolean areEqual(Object apiValue, Object daoValue) {

        if (apiValue == daoValue) {
            return true;
        }

        if (apiValue == null || daoValue == null) {
            return false;
        }

        // ✅ Специальная обработка BigDecimal (сравнение по значению, а не по scale)
        if (apiValue instanceof BigDecimal bd1 &&
                daoValue instanceof BigDecimal bd2) {
            return bd1.compareTo(bd2) == 0;
        }

        return Objects.equals(apiValue, daoValue);
    }

    private Object getFieldValue(Object obj, String fieldName) {
        try {
            Field field = obj.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(obj);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(
                    "Failed to get field value: " + fieldName, e);
        }
    }
}