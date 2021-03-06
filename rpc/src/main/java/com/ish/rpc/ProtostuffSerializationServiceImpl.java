/*****************************************************************
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 ****************************************************************/

package com.ish.rpc;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This implementation of {@link SerializationService} uses Protostuff Runtime Serializer.
 */
public class ProtostuffSerializationServiceImpl implements SerializationService {

    @Override
    public <T> void serialize(T source, OutputStream outputStream) throws IOException {
        Class<T> clazz = (Class<T>) source.getClass();
        Schema<T> schema = RuntimeSchema.getSchema(clazz);
        ProtostuffIOUtil.writeTo(outputStream, source, schema, LinkedBuffer.allocate());
    }

    @Override
    public <T> T deserialize(InputStream inputStream, Class<T> objectClass) throws IOException {
        Schema<T> schema = RuntimeSchema.getSchema(objectClass);
        T result = schema.newMessage();
        ProtostuffIOUtil.mergeFrom(inputStream, result, schema, LinkedBuffer.allocate());
        return objectClass.cast(result);
    }

}
