package com.github.johnnyjayjay.spiglin.event

import net.bytebuddy.ByteBuddy
import net.bytebuddy.implementation.MethodDelegation
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import sun.reflect.annotation.AnnotationParser
import kotlin.reflect.KClass

object ListenerGenerator {

    private val byteBuddy = ByteBuddy()

    @Suppress("CAST_NEVER_SUCCEEDS")
    fun <T : Event> generateListener(
        eventType: KClass<T>,
        action: Listener.(T) -> Unit,
        priority: EventPriority,
        ignoreCancelled: Boolean
    ): Listener {
        return byteBuddy
            .subclass(Object::class.java)
            .implement(Listener::class.java)
            .defineMethod("onEvent", Unit::class.java)
                .withParameters(eventType.java)
                .intercept(MethodDelegation.to(GeneralListener(action)))
                .annotateMethod(
                    AnnotationParser.annotationForMap(
                        EventHandler::class.java,
                        mapOf("priority" to priority, "ignoreCancelled" to ignoreCancelled)
                    )
                )
            .make()
            .load(javaClass.classLoader)
            .loaded.newInstance() as Listener
    }
}


