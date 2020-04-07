package org.tkit.maven.mp.restclient;

/**
 * Rest endpoints implementation type.
 */
public enum ImplType {

    /**
     * The class implementation. Each method response HTTP 501.
     */
    CLASS,

    /**
     * The interface implementation with {@code default} method implementation.
     * Each method response HTTP 501.
     */
    INTERFACE,

    /**
     * The proxy class implementation. Each method call rest client method.
     */
    PROXY;
}
