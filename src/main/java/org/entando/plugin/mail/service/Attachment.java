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

public class Attachment {

    private String name;
    private String contentType;
    private byte[] content;

    public String getName() {
        return name;
    }

    public Attachment setName(String name) {
        this.name = name;
        return this;
    }

    public String getContentType() {
        return contentType;
    }

    public Attachment setContentType(String contentType) {
        this.contentType = contentType;
        return this;
    }

    public byte[] getContent() {
        return content;
    }

    public Attachment setContent(byte[] content) {
        this.content = content;
        return this;
    }
}
