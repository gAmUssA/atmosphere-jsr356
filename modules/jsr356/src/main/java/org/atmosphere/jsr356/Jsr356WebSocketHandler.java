/*
 * Copyright 2012 Jean-Francois Arcand
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
package org.atmosphere.jsr356;

import org.atmosphere.websocket.WebSocket;
import org.atmosphere.websocket.WebSocketHandler;
import org.atmosphere.websocket.WebSocketProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.websocket.annotations.WebSocketClose;
import javax.net.websocket.annotations.WebSocketError;
import javax.net.websocket.annotations.WebSocketMessage;
import javax.net.websocket.annotations.WebSocketOpen;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Jsr356WebSocketHandler implements WebSocketHandler {

    private Logger logger = LoggerFactory.getLogger(Jsr356AnnotationProcessor.class);

    private final Object object;
    private final Method onOpenMethod;
    private final Method onCloseMethod;
    private final Method onTextMessageMethod;
    private final Method onByteMessageMethod;
    private final Method onErrorMethod;

    protected Jsr356WebSocketHandler(Object c) {
        this.object = c;
        this.onOpenMethod = populate(c, WebSocketOpen.class);
        this.onCloseMethod = populate(c, WebSocketClose.class);
        this.onTextMessageMethod = populate(c, WebSocketMessage.class);
        this.onByteMessageMethod = null; //TODO:
        this.onErrorMethod = populate(c, WebSocketError.class);
    }

    @Override
    public void onByteMessage(WebSocket webSocket, byte[] data, int offset, int length) throws IOException {
        Object s = invoke(onByteMessageMethod, data);
        if (s != null) {
            webSocket.write(s.toString());
        }
    }

    @Override
    public void onTextMessage(WebSocket webSocket, String data) throws IOException {
        Object s = invoke(onTextMessageMethod, data);
        if (s != null) {
            webSocket.write(s.toString());
        }
    }

    @Override
    public void onOpen(WebSocket webSocket) throws IOException {
        Object s = invoke(onOpenMethod, null);
        if (s != null) {
            webSocket.write(s.toString());
        }
    }

    @Override
    public void onClose(WebSocket webSocket) {
        invoke(onCloseMethod, null);
    }

    @Override
    public void onError(WebSocket webSocket, WebSocketProcessor.WebSocketException t) {
        invoke(onErrorMethod, t);
    }

    private Method populate(Object c, Class<? extends Annotation> annotation) {
        for (Method m : c.getClass().getMethods()) {
            if (m.isAnnotationPresent(annotation)) {
                return m;
            }
        }
        return null;
    }

    private Object invoke(Method m, Object o) {
        if (m != null) {
            try {
                return m.invoke(object, o == null ? new Object[]{} : new Object[]{o});
            } catch (IllegalAccessException e) {
                logger.debug("", e);
            } catch (InvocationTargetException e) {
                logger.debug("", e);
            }
        }
        return null;
    }

}
