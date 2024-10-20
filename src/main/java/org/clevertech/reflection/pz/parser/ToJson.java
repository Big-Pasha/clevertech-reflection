package org.clevertech.reflection.pz.parser;

import java.lang.reflect.Field;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ToJson {

    private Integer tabCounter = 0;

    public final String defaultPackage;

    public ToJson() {
        String packageName = this.getClass().getPackageName();

        defaultPackage = Arrays.stream(packageName.split("\\."))
                .limit(2)
                .collect(Collectors.joining("."));
    }

    public String toJson(Object object) {
        if (object == null) {
            return "null";
        }

        Class<?> clazz = object.getClass();

        StringBuilder sb = new StringBuilder();
        wrapObject(sb);
        sb.append(
                Arrays.stream(clazz.getDeclaredFields())
                        .map(field -> getFieldNameAndValue(field, object))
                        .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
                        .toString()
        );
        wrapEndObj(sb);
        removeComaAndNewLine(sb);

        return sb.toString();
    }

    public String toJsonInLine(Object object) {
        String json = toJson(object);

        return null != json ? json.replaceAll("( |\\n)", "") : json;
    }

    private StringBuilder getFieldNameAndValue(Field field, Object instance) {
        try {
            field.setAccessible(true);

            String fieldType = field.getAnnotatedType().toString();
            String fieldName = field.getName();
            Object fieldValue = field.get(instance);
            StringBuilder sb = new StringBuilder();

            if (null != fieldValue && fieldType.contains("Map")) {
                Set<?> keys = (Set<?>) field.getType().getMethod("keySet").invoke(fieldValue);
                Map<?, ?> map = (Map<?, ?>) fieldValue;

                wrapKeyToObj(sb, fieldName);
                for (Object key : keys) {
                    wrapKey(sb, key.toString());
                    wrapJsonSupportedValue(sb, map.get(key));
                }
                wrapEndObj(sb);
            } else if (null != fieldValue && fieldType.contains("List")) {
                List<?> valueList = (List<?>) fieldValue;
                boolean isParseNeeded = checkIfParseNeeded(fieldType);

                wrapKeyToList(sb, fieldName);

                for (Object value : valueList) {
                    if (isParseNeeded && null != value) {
                        wrapParsedObj(sb, toJson(value));
                    } else {
                        wrapValueToList(sb, value);
                    }
                }
                wrapEndList(sb);
            } else if (checkIfParseNeeded(fieldType) && null != fieldValue) {
                wrapKeyToObjWithExistObj(sb, fieldName);
                wrapParsedObj(sb, toJson(fieldValue));
            } else if (isSupportedJsonType(fieldType) || null == fieldValue) {
                wrapKey(sb, fieldName);
                wrapJsonSupportedValue(sb, fieldValue);
            } else {
                wrapKey(sb, fieldName);
                wrapValue(sb, formatSpecificTypes(fieldType, fieldValue));
            }

            return sb;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private Object formatSpecificTypes(String type, Object object) {
        Object formattedObject = object;

        if (null != type && type.contains("OffsetDateTime")) {
            formattedObject = formatDateTime(object);
        }

        return formattedObject;
    }

    private Object formatDateTime(Object object) {
        OffsetDateTime offsetDateTime = (OffsetDateTime) object;
        return offsetDateTime.format(DateTimeFormatter.ISO_DATE_TIME);
    }

    private boolean isSupportedJsonType(String type) {
        if (type == null) return true;

        List<String> listSupportedJsonTypes = Arrays.asList(
                "byte",
                "short",
                "int",
                "integer",
                "float",
                "double",
                "boolean"
        );

        return listSupportedJsonTypes.stream()
                .anyMatch(supportedType -> type.toLowerCase().contains(supportedType));
    }

    private boolean checkIfParseNeeded(String type) {
        return type.contains(defaultPackage);
    }

    private void wrapKey(StringBuilder sb, String key) {
        addTabs(sb, tabCounter);
        sb.append(Constant.QUOTE)
                .append(key)
                .append(Constant.QUOTE)
                .append(Constant.COLON);
    }

    private void wrapKeyToObj(StringBuilder sb, String key) {
        addTabs(sb, tabCounter);
        sb.append(Constant.QUOTE)
                .append(key)
                .append(Constant.NAME_TO_OBJECT);
        tabCounter++;
    }

    private void wrapKeyToObjWithExistObj(StringBuilder sb, String key) {
        addTabs(sb, tabCounter);
        sb.append(Constant.QUOTE)
                .append(key)
                .append(Constant.NAME_TO_EXIST_OBJ);
        tabCounter++;
    }

    private void wrapObject(StringBuilder sb) {
        addTabs(sb, tabCounter);
        sb.append(Constant.START_OBJECT);
        tabCounter++;
    }

    private void wrapEndObj(StringBuilder sb) {
        removeLastComma(sb);
        tabCounter--;
        addTabs(sb, tabCounter);

        sb.append(Constant.END_OBJECT)
                .append(Constant.COMMA_NEW_LINE);
    }

    private void wrapKeyToList(StringBuilder sb, String key) {
        addTabs(sb, tabCounter);
        sb.append(Constant.QUOTE)
                .append(key)
                .append(Constant.NAME_TO_ARRAY);

        tabCounter++;
    }

    private void wrapValueToList(StringBuilder sb, Object value) {
        addTabs(sb, tabCounter);
        sb.append(Constant.QUOTE)
                .append(value)
                .append(Constant.QUOTE)
                .append(Constant.COMMA_NEW_LINE);

        tabCounter++;
    }

    private void wrapParsedObj(StringBuilder sb, Object value) {
        sb.append(value)
                .append(Constant.COMMA_NEW_LINE);
    }

    private void wrapEndList(StringBuilder sb) {
        removeLastComma(sb);
        tabCounter--;
        addTabs(sb, tabCounter);

        sb.append(Constant.END_OF_ARRAY);
    }

    private void wrapValue(StringBuilder sb, Object value) {
        sb.append(Constant.QUOTE)
                .append(value)
                .append(Constant.QUOTE)
                .append(Constant.COMMA_NEW_LINE);
    }

    private void wrapJsonSupportedValue(StringBuilder sb, Object value) {
        sb.append(value)
                .append(Constant.COMMA_NEW_LINE);
    }

    private void addTabs(StringBuilder sb, Integer tabCounter) {
        sb.append(String.valueOf(Constant.TAB).repeat(Math.max(0, tabCounter)));
    }

    private void removeLastComma(StringBuilder sb) {
        sb.delete(sb.length() - 2, sb.length() - 1);
    }

    private void removeComaAndNewLine(StringBuilder sb) {
        sb.delete(sb.length() - 2, sb.length());
    }
}
