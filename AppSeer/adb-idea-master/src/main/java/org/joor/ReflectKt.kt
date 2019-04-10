package org.joor

inline fun <reified T> on() = Reflect.on(T::class.java)
inline fun <reified T> Reflect.asType() = this.`as`(T::class.java)