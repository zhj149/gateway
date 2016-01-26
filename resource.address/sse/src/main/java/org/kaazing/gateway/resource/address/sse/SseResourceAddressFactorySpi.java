/**
 * Copyright 2007-2015, Kaazing Corporation. All rights reserved.
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
package org.kaazing.gateway.resource.address.sse;

import static java.lang.String.format;
import static org.kaazing.gateway.resource.address.ResourceFactories.changeSchemeOnly;
import static org.kaazing.gateway.resource.address.sse.SseResourceAddress.TRANSPORT_NAME;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import org.kaazing.gateway.resource.address.ResourceAddress;
import org.kaazing.gateway.resource.address.ResourceAddressFactorySpi;
import org.kaazing.gateway.resource.address.ResourceFactory;
import org.kaazing.gateway.resource.address.ResourceOptions;
import org.kaazing.gateway.resource.address.URIUtils;

public class SseResourceAddressFactorySpi extends ResourceAddressFactorySpi<SseResourceAddress> {

    private static final String SCHEME_NAME = "sse";
    private static final int SCHEME_PORT = 80;
    private static final ResourceFactory TRANSPORT_FACTORY = changeSchemeOnly("http");
    private static final ResourceFactory SSE_HTTPXE_RESOURCE_FACTORY = changeSchemeOnly("sse+httpxe");

    @Override
    public String getSchemeName() {
        return SCHEME_NAME;
    }

    @Override
    protected int getSchemePort() {
        return SCHEME_PORT;
    }

    @Override
    protected String getTransportName() {
        return TRANSPORT_NAME;
    }

    @Override
    protected ResourceFactory getTransportFactory() {
        return TRANSPORT_FACTORY;
    }

    @Override
    protected String getProtocolName() {
        // Content-Type, not Upgrade, so different path still required to avoid collision
        return null;
    }
    
    @Override
    protected SseResourceAddress newResourceAddress0(String original, String location) {

        String host = URIUtils.getHost(location);
        int port = URIUtils.getPort(location);
        String path = URIUtils.getPath(location);

        if (host == null) {
            throw new IllegalArgumentException(format("Missing host in URI: %s", location));
        }

        if (port == -1) {
            throw new IllegalArgumentException(format("Missing port in URI: %s", location));
        }
        
        if (path == null || path.length() == 0) {
            throw new IllegalArgumentException(format("Missing path in URI: %s", location));
        }
        
        URI uriOriginal = URI.create(original);
        URI uriLocation = URI.create(location);
        return new SseResourceAddress(this, uriOriginal, uriLocation);
    }

    @Override
    protected void setAlternateOption(String location,
                                      ResourceOptions options,
                                      Map<String, Object> optionsByName) {

        // Creating alternate SseResourceAddres with httpxe tranport
        location = SSE_HTTPXE_RESOURCE_FACTORY.createURI(location);
        // copying optionsByName to so that the alternate address has its own to work with
        optionsByName = new HashMap<>(optionsByName);
        ResourceAddress alternateAddress = getResourceAddressFactory().newResourceAddress(location, optionsByName);
        options.setOption(ResourceAddress.ALTERNATE, alternateAddress);
    }

}
