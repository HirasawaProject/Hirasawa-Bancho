package io.hirasawa.server.helpers

import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.primaryConstructor

/**
 * Inject arguments into functions
 *
 * This will attempt to create instances of objects passed through using similar logic to this function
 * @param kFunction Instance of function that requires injecting
 * @param providedArguments HashMap of arguments with their names and values to pass through into function
 */
fun <T> injectFunction(kFunction: KFunction<T>, providedArguments: HashMap<String, Any>): T {
    /* TODO support database ORM, like if a provided argument is matched to a table we will query the value against the
        primary key
     */
    val expectedFunctionArguments = kFunction.parameters
    val injectedArguments = HashMap<KParameter, Any>()

    for (argument in expectedFunctionArguments) {
        if (argument.name in providedArguments) {
            // If we provided the arguments then inject them as a priority
            injectedArguments[argument] = providedArguments[argument.name] ?: continue
        } else {
            // If we did not provide the arguments then we should attempt to create them
            // Right now we're not gonna do much to create classes, only attempt if they have no arguments
            val argumentClass = argument.type.classifier as KClass<*>
            var foundConstructor = false
            for (constructor in argumentClass.constructors) {
                if (constructor.parameters.isEmpty()) {
                    injectedArguments[argument] = injectFunction(constructor, HashMap())
                    foundConstructor = true
                }
            }

            if (!foundConstructor) {
                if (!argument.isOptional) {
                    throw Exception("Unable to inject variable into function: ${argument.name}")
                }
            }
        }
    }

    return kFunction.callBy(injectedArguments)
}

/**
 *
 */
@Suppress("UNCHECKED_CAST")
fun <T> injectConstructor(kClass: KClass<*>, providedArguments: HashMap<String, Any>): T {
    val instance = injectFunction(kClass.primaryConstructor as KFunction<Any>, providedArguments)
    return instance as T
}