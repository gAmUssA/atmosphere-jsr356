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

import eu.infomas.annotation.AnnotationDetector;
import org.atmosphere.cpr.AnnotationProcessor;
import org.atmosphere.cpr.DefaultAnnotationProcessor;
import org.atmosphere.cpr.WebSocketProcessorFactory;
import org.atmosphere.websocket.WebSocketProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.websocket.server.WebSocketEndpoint;
import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;

public class Jsr356AnnotationProcessor extends DefaultAnnotationProcessor {

    private Logger logger = LoggerFactory.getLogger(Jsr356AnnotationProcessor.class);

    @Override
    public AnnotationProcessor scan(File rootDir) throws IOException {
        super.scan(rootDir);
        final ClassLoader cl = Thread.currentThread().getContextClassLoader();
        final AnnotationDetector.TypeReporter reporter = new AnnotationDetector.TypeReporter() {

            @SuppressWarnings("unchecked")
            @Override
            public Class<? extends Annotation>[] annotations() {
                return new Class[]{
                        WebSocketEndpoint.class
                };
            }

            @Override
            public void reportTypeAnnotation(Class<? extends Annotation> annotation, String className) {
                logger.info("Found Annotation in {} being scanned: {}", className, annotation);
                if (WebSocketEndpoint.class.equals(annotation)) {
                    try {
                        Object c = cl.loadClass(className).newInstance();
                        Jsr356WebSocketHandler w = new Jsr356WebSocketHandler(c);

                        WebSocketEndpoint a = c.getClass().getAnnotation(WebSocketEndpoint.class);
                        String path = a.value();
                        framework.initWebSocket();
                        WebSocketProcessor p = WebSocketProcessorFactory.getDefault().getWebSocketProcessor(framework);
                        p.registerWebSocketHandler(path, w);
                    } catch (Throwable t) {
                        logger.error("", t);
                    }
                }
            }
        };
        logger.trace("Scanning JSR 356 annotations in {}", rootDir.getAbsolutePath());
        final AnnotationDetector cf = new AnnotationDetector(reporter);
        cf.detect(rootDir);
        return this;
    }
}
