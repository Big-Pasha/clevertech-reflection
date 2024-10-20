package org.clevertech.reflection.pz.parser;

public class ParseJ {

    private ToJson toJsonParser = new ToJson();
    private FromJson fromJsonParser = new FromJson();

    public String parseToJson(Object obj) {
        return toJsonParser.toJson(obj);
    }

    public String parseToJsonInLine(Object obj) {
        return toJsonParser.toJsonInLine(obj);
    }

    public <T> T parseFromJson(Class<T> clazz, String json) throws Exception {
        return fromJsonParser.parseFromJson(clazz, json);
    }
}
