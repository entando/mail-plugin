/*
 * Copyright 2019-Present Entando Inc. (http://www.entando.com) All rights reserved.
 * 
 * This library is free software; you can redistribute it and/or modify it under
 * the terms of the GNU Lesser General Public License as published by the Free
 * Software Foundation; either version 2.1 of the License, or (at your option)
 * any later version.
 * 
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public License for more
 * details.
 */
package org.entando.plugin.mail.service;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64ByteArraySerializer extends StdSerializer<byte[]> {

    private static final long serialVersionUID = 1L;

    public Base64ByteArraySerializer() {
        super(byte[].class);
    }

    @Override
    public void serialize(byte[] bytes, JsonGenerator gen, SerializerProvider provider) throws IOException {
        if (bytes == null) {
            gen.writeNull();
        } else {
            gen.writeString(new String(Base64.getDecoder().decode(bytes), StandardCharsets.UTF_8));
        }
    }
}
