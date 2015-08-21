/**
 * Copyright (c) 2007-2014 Kaazing Corporation. All rights reserved.
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.kaazing.gateway.server.config.parse.translate;

import org.kaazing.gateway.server.config.parse.GatewayConfigNamespace;
import org.kaazing.gateway.server.config.parse.GatewayConfigParser;

/**
 * Classes which translate/transform an 2012/09/gateway config file DOM into
 * a 2014/09/gateway config file DOM.  These classes are used by the
 * {@link GatewayConfigParser}
 */
public class September2012ToSeptember2014Translator
    extends GatewayConfigTranslatorPipeline {

    public September2012ToSeptember2014Translator() {
        super();

        // Set the September 2014 namespace.  Add this to the end of the pipeline,
        // so that any nodes that are added will have the correct namespace set.
        addTranslator(new NamespaceVisitor(GatewayConfigNamespace.CURRENT_NS));
    }
}
