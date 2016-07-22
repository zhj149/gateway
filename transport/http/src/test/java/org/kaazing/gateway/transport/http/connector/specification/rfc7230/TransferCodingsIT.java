/**
 * Copyright 2007-2016, Kaazing Corporation. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kaazing.gateway.transport.http.connector.specification.rfc7230;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.util.concurrent.CountDownLatch;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.future.WriteFuture;
import org.apache.mina.core.service.IoHandler;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.core.session.IoSessionInitializer;
import org.jmock.Expectations;
import org.jmock.api.Invocation;
import org.jmock.integration.junit4.JUnitRuleMockery;
import org.jmock.lib.action.CustomAction;
import org.jmock.lib.concurrent.Synchroniser;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.DisableOnDebug;
import org.junit.rules.RuleChain;
import org.junit.rules.TestRule;
import org.junit.rules.Timeout;
import org.kaazing.gateway.transport.http.HttpConnectSession;
import org.kaazing.gateway.transport.http.HttpConnectorRule;
import org.kaazing.gateway.transport.http.HttpHeaders;
import org.kaazing.gateway.transport.http.HttpMethod;
import org.kaazing.k3po.junit.annotation.Specification;
import org.kaazing.k3po.junit.rules.K3poRule;
import org.kaazing.mina.core.buffer.IoBufferAllocatorEx;
import org.kaazing.mina.core.session.IoSessionEx;
import org.kaazing.test.util.ITUtil;
import org.kaazing.test.util.MethodExecutionTrace;

public class TransferCodingsIT {

    private final HttpConnectorRule connector = new HttpConnectorRule();

    private JUnitRuleMockery context = new JUnitRuleMockery() {
        {
            setThreadingPolicy(new Synchroniser());
        }
    };

    private final TestRule trace = new MethodExecutionTrace();
    private TestRule contextRule = ITUtil.toTestRule(context);
    private final K3poRule k3po = new K3poRule().setScriptRoot("org/kaazing/specification/http/rfc7230/transfer.codings");
    private final TestRule timeoutRule = new DisableOnDebug(new Timeout(5, SECONDS));

    @Rule
    public TestRule chain = RuleChain.outerRule(trace).around(connector).around(contextRule).around(k3po).around(timeoutRule);

    @Ignore("Cannot Write Bytes")
    @Test
    @Specification({"request.transfer.encoding.chunked/response"})
    public void requestTransferEncodingChunked() throws Exception {
        final IoHandler handler = context.mock(IoHandler.class);
        final CountDownLatch closed = new CountDownLatch(1);

        context.checking(new Expectations() {
            {
                oneOf(handler).sessionCreated(with(any(IoSessionEx.class)));
                oneOf(handler).sessionOpened(with(any(IoSessionEx.class)));
                oneOf(handler).sessionClosed(with(any(IoSessionEx.class)));
                will(new CustomAction("Latch countdown") {
                    @Override
                    public Object invoke(Invocation invocation) throws Throwable {
                        closed.countDown();
                        return null;
                    }
                });
            }
        });

        connector.connect("http://localhost:8080/", handler, new ConnectSessionInitializerPostRequest());
        assertTrue(closed.await(2, SECONDS));

        k3po.finish();
    }

    @Ignore("AssertionError")
    @Test
    @Specification({"response.transfer.encoding.chunked/response"})
    public void responseTransferEncodingChunked() throws Exception {
        final IoHandler handler = context.mock(IoHandler.class);
        final CountDownLatch closed = new CountDownLatch(1);

        connector.getConnectOptions().put("http.userAgentHeaderEnabled", Boolean.FALSE);
        connector.getConnectOptions().put("http.hostHeaderEnabled", Boolean.FALSE);

        context.checking(new Expectations() {
            {
                oneOf(handler).sessionCreated(with(any(IoSessionEx.class)));
                oneOf(handler).sessionOpened(with(any(IoSessionEx.class)));
                oneOf(handler).sessionClosed(with(any(IoSessionEx.class)));
                will(new CustomAction("Latch countdown") {
                    @Override
                    public Object invoke(Invocation invocation) throws Throwable {
                        closed.countDown();
                        return null;
                    }
                });
            }
        });

        connector.connect("http://localhost:8080/", handler, new ConnectSessionInitializerPostRequest());
        assertTrue(closed.await(2, SECONDS));

        k3po.finish();
    }

    @Ignore("Trailer Not Part of spec")
    @Test
    @Specification({"request.transfer.encoding.chunked.with.trailer/response"})
    public void requestTransferEncodingChunkedWithTrailer() throws Exception {
        final IoHandler handler = context.mock(IoHandler.class);
        final CountDownLatch closed = new CountDownLatch(1);

        connector.getConnectOptions().put("http.userAgentHeaderEnabled", Boolean.FALSE);
        connector.getConnectOptions().put("http.hostHeaderEnabled", Boolean.FALSE);

        context.checking(new Expectations() {
            {
                oneOf(handler).sessionCreated(with(any(IoSessionEx.class)));
                oneOf(handler).sessionOpened(with(any(IoSessionEx.class)));
                oneOf(handler).sessionClosed(with(any(IoSessionEx.class)));
                will(new CustomAction("Latch countdown") {
                    @Override
                    public Object invoke(Invocation invocation) throws Throwable {
                        closed.countDown();
                        return null;
                    }
                });
            }
        });

        connector.connect("http://localhost:8080/", handler, new ConnectSessionInitializerGetContentType());
        assertTrue(closed.await(2, SECONDS));

        k3po.finish();
    }

    @Ignore("Trailer Not part of spec")
    @Test
    @Specification({"response.transfer.encoding.chunked.with.trailer/response"})
    public void responseTransferEncodingChunkedWithTrailer() throws Exception {
        final IoHandler handler = context.mock(IoHandler.class);
        final CountDownLatch closed = new CountDownLatch(1);

        connector.getConnectOptions().put("http.userAgentHeaderEnabled", Boolean.FALSE);
        connector.getConnectOptions().put("http.hostHeaderEnabled", Boolean.FALSE);

        context.checking(new Expectations() {
            {
                oneOf(handler).sessionCreated(with(any(IoSessionEx.class)));
                oneOf(handler).sessionOpened(with(any(IoSessionEx.class)));
                oneOf(handler).sessionClosed(with(any(IoSessionEx.class)));
                will(new CustomAction("Latch countdown") {
                    @Override
                    public Object invoke(Invocation invocation) throws Throwable {
                        closed.countDown();
                        return null;
                    }
                });
            }
        });

        connector.connect("http://localhost:8080/", handler, new ConnectSessionInitializerPostRequest());
        assertTrue(closed.await(2, SECONDS));

        k3po.finish();
    }

    private static class ConnectSessionInitializerPostRequest implements IoSessionInitializer<ConnectFuture> {
        
        private static WriteFuture writeStringMessageToSession(String message, IoSession session) {
            ByteBuffer data = ByteBuffer.wrap(message.getBytes());
            IoBufferAllocatorEx<?> allocator = ((IoSessionEx) session).getBufferAllocator();
            return session.write(allocator.wrap(data));
        }
        
        @Override
        public void initializeSession(IoSession session, ConnectFuture future) {
            HttpConnectSession connectSession = (HttpConnectSession) session;
            connectSession.setMethod(HttpMethod.POST);
            connectSession.addWriteHeader(HttpHeaders.HEADER_CONTENT_TYPE, "text/plain");
            connectSession.addWriteHeader(HttpHeaders.HEADER_TRANSFER_ENCODING, "chunked");
            writeStringMessageToSession("Chunk A", session);
            writeStringMessageToSession("Chunk B", session);
            writeStringMessageToSession("Chunk C", session);
        }
    }

    private static class ConnectSessionInitializerGetContentType implements IoSessionInitializer<ConnectFuture> {
        @Override
        public void initializeSession(IoSession session, ConnectFuture future) {
            HttpConnectSession connectSession = (HttpConnectSession) session;
            connectSession.setMethod(HttpMethod.GET);
            connectSession.addWriteHeader(HttpHeaders.HEADER_CONTENT_TYPE, "text/plain");
        }
    }

}
