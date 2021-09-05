package com.jamitek.photosapp.api

sealed class ApiException : Throwable() {

    /**
     * HTTP status codes related to authorization.
     */
    class AuthError : ApiException()

    /**
     * HTTP status codes related to server errors.
     */
    class ServerError : ApiException()

    /**
     * There was no response due to network transport related issues.
     */
    class NetworkError : ApiException()

    /**
     * Request was responded seemingly successfully, but the content did not match our expectations.
     */
    class SerializationError : ApiException()

    /**
     * Anything not caught by the other explicit errors.
     */
    class UnknownError(statusCode: Int?, body: String?) : ApiException()

}