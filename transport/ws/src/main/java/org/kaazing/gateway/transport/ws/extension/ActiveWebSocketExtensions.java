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

package org.kaazing.gateway.transport.ws.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.kaazing.gateway.transport.TypedAttributeKey;

/**
 * {@link ActiveWebSocketExtensions} maintains a list of WebSocketExtensions that have successfully been negotiated. 
 * The list that it is constructed with has already properly ordered the extensions as required by the 
 * {@link WebSocketExtensionSpi}
 */
public class ActiveWebSocketExtensions{
    
    public static final ActiveWebSocketExtensions EMPTY = new ActiveWebSocketExtensions(new ArrayList<WebSocketExtensionSpi>(0));
    private static final TypedAttributeKey<ActiveWebSocketExtensions> WS_EXTENSIONS_KEY = new TypedAttributeKey<>(
            ActiveWebSocketExtensions.class, "activeWsExtensions");

    private final List<WebSocketExtensionSpi> extensions;

    public ActiveWebSocketExtensions(List<WebSocketExtensionSpi> negotiatedExtensions) {
        extensions = Collections.unmodifiableList(negotiatedExtensions);
    }

    public List<WebSocketExtensionSpi> asList() {
        return extensions;
    }

    public static ActiveWebSocketExtensions get(IoSession session) {
        ActiveWebSocketExtensions extensions = WS_EXTENSIONS_KEY.get(session);
        return extensions == null ? EMPTY : extensions;
    }

    @SuppressWarnings("unchecked")
    public <T extends WebSocketExtensionSpi> T getExtension(Class<T> extensionClass) {
        for (WebSocketExtensionSpi extension : extensions) {
            if (extensionClass == extension.getClass()) {
                return (T) extension;
            }
        }
        return null;
    }

    public boolean hasExtension(Class<? extends WebSocketExtensionSpi> extensionClass) {
        return getExtension(extensionClass) != null;
    }

//    public static final ActiveWebSocketExtensions EMPTY = new ActiveWebSocketExtensions(new ArrayList<ExtensionHeader>(0), ExtensionHeader.EndpointKind.SERVER);
//    private static final TypedAttributeKey<ActiveWebSocketExtensions> WS_EXTENSIONS_KEY 
//           = new TypedAttributeKey<>(ActiveWebSocketExtensions.class, "activeWsExtensions");
//
//    
//    private final List<ExtensionHeader> extensions;
//    private final List<ExtensionHeader> binaryDecodingExtensions;
//    private final List<ExtensionHeader> textDecodingExtensions;
//    private final List<ExtensionHeader> binaryEncodingExtensions;
//    private final List<ExtensionHeader> textEncodingExtensions;
//    private final EscapeSequencer binaryEscapeSequencer;
//    private final EscapeSequencer textEscapeSequencer;
//    
//    private final byte[] commonBytes;
//    
//    private ExtensionHeader escapedDecodingExtension = null;
//
//    public ActiveWebSocketExtensions(List<ExtensionHeader> negotiatedExtensions, ExtensionHeader.EndpointKind endpointKind) {
//        extensions = new ArrayList<>(negotiatedExtensions.size());
//        SortedSet<ExtensionHeader> sorted = new TreeSet<>(new Comparator<ExtensionHeader>(){
//
//            @Override
//            public int compare(ExtensionHeader o1, ExtensionHeader o2) {
//                assert o1 != null && o2 != null : "cannot negotiate a null extension"; 
//                return o1.getOrdering().compareTo(o2.getOrdering());
//            }
//            
//        });
//        sorted.addAll(negotiatedExtensions);
//        extensions.addAll(sorted);
//        binaryDecodingExtensions = new ArrayList<>(extensions.size());
//        textDecodingExtensions = new ArrayList<>(extensions.size());
//        binaryEncodingExtensions = new ArrayList<>(extensions.size());
//        textEncodingExtensions = new ArrayList<>(extensions.size());
//        byte[] commonBytes = new byte[0];
//        int commonCount = 0;
//        for (ExtensionHeader extension : negotiatedExtensions) {
//            if (extension.canDecode(endpointKind, BINARY)) {
//                binaryDecodingExtensions.add(extension);
//            }
//            if (extension.canDecode(endpointKind, TEXT)) {
//                textDecodingExtensions.add(extension);
//            }
//            if (extension.canEncode(endpointKind, BINARY)) {
//                binaryEncodingExtensions.add(extension);
//            }
//            if (extension.canEncode(endpointKind, TEXT)) {
//                textEncodingExtensions.add(extension);
//            }
//        }
//        binaryEscapeSequencer = binaryEncodingExtensions.isEmpty() ? null : new EscapeSequencer(WsExtensionUtils.getEscapeSequences(binaryEncodingExtensions));
//        textEscapeSequencer = textEncodingExtensions.isEmpty() ? null : new EscapeSequencer(WsExtensionUtils.getEscapeSequences(textEncodingExtensions));
//        this.commonBytes = new byte[commonCount];
//        System.arraycopy(commonBytes, 0, this.commonBytes, 0, commonCount);
//    }
//
//    // for unit tests and gateway.management
//    // TODO: change signature to 
//    //       public List<WebSocketExtensionSpi> asList()
//
//    public List<ExtensionHeader> asList() {
//        return Collections.unmodifiableList(extensions);
//    }
//
//    @Deprecated
//    public boolean canDecode(Kind messageKind) {
//        switch(messageKind) {
//        case BINARY:
//            return binaryDecodingExtensions.size() > 0;
//        case TEXT:
//            return textDecodingExtensions.size() > 0;
//        default:
//            return false;
//        }
//    }
//
//    /**
//     * Decodes the extension message into a WsMessage and writes it to the decoder output 
//     * if the payload matches any of the active, decoding extensions.
//     * @param payload   Content of a WebSocket binary or text frame
//     * @return          true if the payload matched a decoding extension so was decoded (even if it was an escape frame
//     *                  and so was not written to decoder output), else false.
//     */
//    @Deprecated
//    public boolean decode(IoBufferEx payload, Kind messageKind, ProtocolDecoderOutput out) {
//        List<ExtensionHeader> decodingExtensions = null;
//        switch(messageKind) {
//        case BINARY:
//            decodingExtensions = binaryDecodingExtensions;
//            break;
//        case TEXT:
//            decodingExtensions = textDecodingExtensions;
//            break;
//        default:
//            return false;
//        }
//        if (decodingExtensions.isEmpty()) {
//            return false;
//        }
//        
//        final int position = payload.position();
//        
//        // Exit fast if not an extension frame by examining bytes common to all extensions
//        for (int i = 0; i<commonBytes.length; i++) {
//            if (payload.get(position + i) != commonBytes[i]) {
//                return false;
//            }
//        }
//    
//        for (ExtensionHeader extension : decodingExtensions) {
//            byte[] controlBytes = extension.getControlBytes();
//            if ( !startsWith(payload, controlBytes) ) {
//                continue;
//            }
//            if (extension == escapedDecodingExtension) {
//                escapedDecodingExtension = null;
//                return false;
//            }
//            // Advance past control bytes
//            payload.position(position + controlBytes.length);
//            if (payload.remaining() == 0) {
//                // escape frame
//                assert escapedDecodingExtension == null;
//                escapedDecodingExtension = extension;
//                return true; // payload has been consumed
//            }
//            out.write(extension.decode(payload));
//            return true;
//        }
//        return false;
//    }
//
//    public static ActiveWebSocketExtensions get(IoSession session) {
//        ActiveWebSocketExtensions extensions = WS_EXTENSIONS_KEY.get(session);
//        return extensions == null ? EMPTY : extensions; 
//    }
//    
//    @Deprecated
//    public EscapeSequencer getEscapeSequencer(Kind messageKind) {
//        return messageKind == TEXT ? textEscapeSequencer : binaryEscapeSequencer;
//    }
//
//    @SuppressWarnings("unchecked")
//    // TODO: change signature to 
//    //       public <T extends webSocketExtensionSpi> T getExtension(Class<T> extensionClass)
//    public <T extends ExtensionHeader> T getExtension(Class<T> extensionClass) {
//        for (ExtensionHeader extension : extensions) {
//            if ( extensionClass == extension.getClass() ) {
//                return (T)extension;
//            }
//        }
//        return null;
//    }
//    
//    public boolean hasExtension(Class<? extends ExtensionHeader> extensionClass) {
//        return getExtension(extensionClass) != null;
//    }
//    
//    public static ActiveWebSocketExtensions merge(ActiveWebSocketExtensions extensions1, ActiveWebSocketExtensions extensions2,
//            ExtensionHeader.EndpointKind endpointKind) {
//        if (extensions1 == EMPTY) {
//            return extensions2;
//        }
//        if (extensions2 == EMPTY) {
//            return extensions1;
//        }
//        List<ExtensionHeader> merged = new ArrayList<>(extensions1.extensions.size() + extensions2.extensions.size());
//        merged.addAll(extensions1.extensions);
//        merged.addAll(extensions2.extensions);
//        return new ActiveWebSocketExtensions(merged, endpointKind);
//    }
//    
//    public void removeBridgeFilters(IoFilterChain filterChain) {
//        for (ExtensionHeader extension : extensions) {
//            extension.removeBridgeFilters(filterChain);
//        }
//    }
//    
//    public void set(IoSession session) {
//        WS_EXTENSIONS_KEY.set(session, this);
//    }
//    
//    public String toString() {
//        return extensions.toString();
//    }
//    
//    public void updateBridgeFilters(IoFilterChain filterChain) {
//        for (ExtensionHeader extension : extensions) {
//            extension.updateBridgeFilters(filterChain);
//        }
//    }
//    
//    public static byte[] getCommonBytes(List<ExtensionHeader> extensions, ExtensionHeader.EndpointKind endpointKind) {
//        byte[] commonBytes = new byte[0];
//        int commonCount = 0;
//        boolean first = true;
//        for (ExtensionHeader extension : extensions) {
//            byte[] controlBytes = extension.getControlBytes();
//            if (first) {
//                commonBytes = controlBytes.clone();
//                first = false;
//            }
//            else {
//                for (int j=0; j<4 && controlBytes[j] == commonBytes[j]; j++) {
//                    commonCount = j+1;
//                }
//            }
//        }
//        byte[] result = new byte[commonCount];
//        System.arraycopy(commonBytes, 0, result, 0, commonCount);
//        return result;
//    }
//    
//    private static boolean startsWith(IoBufferEx buf, byte[] bytes) {
//        if (buf.remaining() < bytes.length) {
//            return false;
//        }
//        int position = buf.position();
//        for (int i=0; i<4; i++) {
//            if (buf.get(position + i) != bytes[i]) {
//                return false;
//            }
//        }
//        return true;
//    }
    
}
