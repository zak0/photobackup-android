package com.jamitek.photosapp

class Event<T>(value: T) {

    private var internalValue: T? = value

    fun get(): T? {
        val ret = internalValue
        internalValue = null
        return ret
    }

    fun peek(): T? {
        return internalValue
    }

}