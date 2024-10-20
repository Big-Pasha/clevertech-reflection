package org.clevertech.reflection.pz.parser;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class FromJson {

    public <T> T parseFromJson(Class<T> clazz, String json) throws Exception {
        if (null == json || json.isEmpty())
            throw new ParseJException("Json is invalid, null, empty");

        return parseObject(clazz, json, null);
    }

    private  <T> T parseObject(Class<T> clazz, String json, Type genericTypes) {
        try {
            T resultedObj = null;

            if (clazz.getTypeName().startsWith("java.util.Map")) {
                HashMap<Object, Object> map = new HashMap<>();
                json = removeStartAndEndObjectAndList(json);

                resultedObj = (T) parseMap(map, json, genericTypes);
            } else if (clazz.getTypeName().startsWith("java.util.List")) {
                List<Object> list = new ArrayList<>();

                Type[] typeArguments = ((ParameterizedType) genericTypes).getActualTypeArguments();
                Type type = typeArguments[0];

                List<String> arrayList = new ArrayList<>();
                while (json.contains("[{")) {
                    String[] result = splitToArray(json, true);
                    arrayList.add("\"" + result[0] + "\"" + ":" + "[" + result[1] + "]");
                    json = result[2];
                }

                int count = 0;
                for (String obj : removeStartAndEndObjectAndList(json).split("},\\{")) {
                    if (!arrayList.isEmpty()) {
                        obj = obj + "," + arrayList.get(count);
                        count++;
                    }
                    list.add(parseObject((Class) type, obj, null));
                }

                resultedObj = (T) list;
            } else {
                resultedObj = clazz.getDeclaredConstructor().newInstance();
                json = removeStartAndEndObjectAndList(json);

                if (json.contains("[{")) {
                    String[] result = splitToArray(json, false);

                    Field field = clazz.getDeclaredField(result[0]);
                    String valueJson = result[1];
                    json = result[2];

                    field.setAccessible(true);
                    field.set(resultedObj, parseObject(field.getType(), valueJson, field.getGenericType()));
                }

                String[] pairs = json.split(",(?![^{]*}|\\[[^\\]]*\\])");
                for (String pair : pairs) {
                    parseValues(pair, clazz, resultedObj);
                }
            }

            return resultedObj;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private String[] splitToArray(String json, boolean getFirstArr) {
        String[] arr = new String[3];
        int startValue = json.indexOf("[{") + 1;
        int endValue = getFirstArr ? json.indexOf("}]") + 1 : json.lastIndexOf("}]") + 1;
        int startKey = json.substring(0, startValue - 3).lastIndexOf("\"");

        //key
        arr[0] = json.substring(startKey + 1, startValue - 3);
        //value
        arr[1] = json.substring(startValue, endValue);
        //other json
        arr[2] = json.substring(0, startKey - 1) + json.substring(endValue + 1, json.length());

        return arr;
    }

    private Object parseMap(Map<Object, Object> map, String json, Type genericTypes) throws Exception {
        Type[] typeArguments = ((ParameterizedType) genericTypes).getActualTypeArguments();
        String keyType = typeArguments[0].getTypeName();
        String valueType = typeArguments[1].getTypeName();

        String[] pairs = json.split(",");

        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            String key = keyValue[0].trim().replace("\"", "");
            String value = keyValue[1].trim().replace("\"", "");

            map.put(parseValueByType(keyType, key), parseValueByType(valueType, value));
        }

        return map;
    }

    private String removeStartAndEndObjectAndList(String json) {
        if (json.startsWith("{")) {
            int start = json.indexOf("{") + 1;
            int end = json.lastIndexOf("}");
            return json.substring(start, end);
        } else if (json.startsWith("[")) {
            int start = json.indexOf("[") + 1;
            int end = json.lastIndexOf("]");
            return json.substring(start, end);
        } else {
            return json;
        }
    }

    private void parseValues(String pair, Class clazz, Object resultedObj) throws Exception {
        int index = pair.indexOf(":");
        String key = pair.substring(0, index).trim().replace("\"", "");
        String value = pair.substring(index + 1, pair.length()).trim().replace("\"", "");
        Field field = clazz.getDeclaredField(key);
        field.setAccessible(true);

        if (pair.contains("{") || pair.contains("[")) {
            value = pair.substring(pair.indexOf(":") + 1, pair.length());
            field.set(resultedObj, parseObject(field.getType(), value, field.getGenericType()));
        } else {
            field.set(resultedObj, parseValue(field, value));
        }
    }

    private Object parseValue(Field field, String value) throws Exception {
        String type = field.getAnnotatedType().toString();
        type = type.indexOf("<") != -1 ? type.substring(0, type.indexOf("<")) : type;
        Object paesedValue = null;

        if ("null".equals(value)) {
            return null;
        }

        paesedValue = parseValueByType(type, value);

        return paesedValue;
    }

    private Object parseValueByType(String type, String value) throws Exception {
        Object paesedValue = null;

        switch (type) {
            case "java.lang.Byte", "byte" -> paesedValue = Byte.parseByte(value);
            case "java.lang.Short", "short" -> paesedValue = Short.parseShort(value);
            case "java.lang.Integer", "int" -> paesedValue = Integer.parseInt(value);
            case "java.lang.Long", "long" -> paesedValue = Long.parseLong(value);
            case "java.lang.Float", "float" -> paesedValue = Float.parseFloat(value);
            case "java.lang.Double", "double" -> paesedValue = Double.parseDouble(value);
            case "java.lang.Boolean", "boolean" -> paesedValue = Boolean.parseBoolean(value);
            case "java.lang.Character", "char" -> paesedValue = value.charAt(0);
            case "java.lang.String" -> paesedValue = value;
            case "java.util.UUID" -> paesedValue = UUID.fromString(value);
            case "java.math.BigDecimal" -> paesedValue = new BigDecimal(value);
            case "java.time.OffsetDateTime" ->
                    paesedValue = OffsetDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
            case "java.time.LocalDate" -> paesedValue = LocalDate.parse(value);
        }

        return paesedValue;
    }
}
