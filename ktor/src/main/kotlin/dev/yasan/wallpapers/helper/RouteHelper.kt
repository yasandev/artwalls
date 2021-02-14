package dev.yasan.wallpapers.helper

import io.ktor.application.*
import io.ktor.http.*
import io.ktor.response.*

suspend fun ApplicationCall.getStringParamOrRespond(paramName: String, parameters: Parameters): String {
    val param = parameters[paramName]
    if (param.isNullOrBlank()) respond(HttpStatusCode.UnprocessableEntity, "$paramName cannot be null or blank")
    return param!!
}

suspend fun ApplicationCall.getIntParamOrRespond(paramName: String): Int {
    val inputId = parameters[paramName]
    if (inputId.isNullOrBlank()) respond(HttpStatusCode.UnprocessableEntity, "$paramName cannot be null or blank")
    val id = inputId!!.toIntOrNull()
    if (id == null) respond(HttpStatusCode.UnprocessableEntity, "$paramName must be an integer")
    return id!!
}

suspend fun ApplicationCall.getIdOrRespond(): Int = getIntParamOrRespond("id")
