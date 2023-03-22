package com.example.myuca.connection

import android.net.Uri
import com.example.myuca.BuildConfig
import java.io.InputStream
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.io.Serializable
import java.net.HttpURLConnection
import java.net.URL

class Connection(urlPath: String) {
    private val urlBase: String = BuildConfig.connection_url
    private val url =
        URL(Uri.parse(urlBase).buildUpon().appendEncodedPath(urlPath).build().toString())

    private val boundary: String = "===" + System.currentTimeMillis() + "==="
    private val httpConnection: HttpURLConnection = url.openConnection() as HttpURLConnection

    init {
        httpConnection.setRequestProperty("Accept-Charset", "UTF-8")
    }

    fun httpGet(): Pair<Number, String> {
        httpConnection.apply {
            requestMethod = "GET"
        }

        val stream =
            if (httpConnection.responseCode == 200) httpConnection.inputStream else httpConnection.errorStream
        println("Headers: ${httpConnection.headerFields}")

        val output = readStream(stream)
        return Pair(httpConnection.responseCode, output)
    }

    fun httpPostMultipart(formData: List<Pair<String, Serializable>>): Pair<Number, String> {
        var body = ""
        for ((name, value) in formData) {
            val v = value.toString()
            val formField = constructFormField(name, v, boundary)
            body += formField
        }

        httpConnection.apply {
            setRequestProperty("Content-Type", "multipart/form-data; boundary=$boundary")
            requestMethod = "POST"

            doOutput = true

            val outputWriter = OutputStreamWriter(outputStream)

            outputWriter.write("$body\r\n")
            outputWriter.write("--$boundary--\r\n")
            outputWriter.flush()
        }

        val stream =
            if (httpConnection.responseCode == 200) httpConnection.inputStream else httpConnection.errorStream

        val output = readStream(stream)
        return Pair(httpConnection.responseCode, output)
    }

    fun httpDelete(): Pair<Number, String> {
        httpConnection.apply {
            requestMethod = "DELETE"
        }

        val stream =
            if (httpConnection.responseCode == 200) httpConnection.inputStream else httpConnection.errorStream
        println("Headers: ${httpConnection.headerFields}")

        val output = readStream(stream)
        return Pair(httpConnection.responseCode, output)
    }

    private fun readStream(stream: InputStream): String {
        val output = InputStreamReader(stream).readText()
        return output
    }
}

fun constructFormField(name: String, value: String, boundary: String): String {
    return "--$boundary\r\n" +
            "Content-Disposition: form-data; name=\"$name\"\r\n" +
            "\r\n$value\r\n"
}
