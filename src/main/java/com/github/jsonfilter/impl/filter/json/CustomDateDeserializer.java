package com.github.jsonfilter.impl.filter.json;

import java.io.IOException;
import java.text.SimpleDateFormat;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonTokenId;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.UntypedObjectDeserializer;

class CustomDateDeseralizer extends UntypedObjectDeserializer {

    private static final long serialVersionUID = -2275951539867772400L;
    final private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss.0");
    
    @Override
    public Object deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {

        if (jp.getCurrentTokenId() == JsonTokenId.ID_STRING) {
            try {
                return format.parseObject(jp.getText());
            } catch (Exception e) {
                return super.deserialize(jp, ctxt);
            }
        } else {
            return super.deserialize(jp, ctxt);
        }
    }
}