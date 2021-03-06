/*
 * Copyright 2012 Jeanfrancois Arcand
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.atmosphere.samples.chat;

import org.atmosphere.annotation.Broadcast;
import org.codehaus.jackson.map.ObjectMapper;

import javax.websocket.Endpoint;
import javax.websocket.WebSocketMessage;
import javax.websocket.server.DefaultServerConfiguration;
import javax.websocket.server.WebSocketEndpoint;
import java.io.IOException;
import java.util.Date;

/**
 * Simple POJO that implement the logic to build a Chat application.
 *
 * @author Jeanfrancois Arcand
 */
@WebSocketEndpoint(value = "/chat", configuration = Jsr356WebSocketChat.DummyServerConfiguration.class)
public class Jsr356WebSocketChat {

    private final ObjectMapper mapper = new ObjectMapper();

    @Broadcast("/chat")
    @WebSocketMessage
    public String onMessage(String message) throws IOException {
        return mapper.writeValueAsString(mapper.readValue(message, Data.class));
    }

    public final static class Data {

        private String message;
        private String author;
        private long time;

        public Data() {
            this("", "");
        }

        public Data(String author, String message) {
            this.author = author;
            this.message = message;
            this.time = new Date().getTime();
        }

        public String getMessage() {
            return message;
        }

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public long getTime() {
            return time;
        }

        public void setTime(long time) {
            this.time = time;
        }

    }

    public class DummyServerConfiguration extends DefaultServerConfiguration {
        /**
         * Creates a server configuration with the given path
         *
         * @param path the URI or URI template.
         */
        public DummyServerConfiguration(Class<? extends Endpoint> endpointClass, String path) {
            super(endpointClass, path);
        }
    }
}
