package com.lordxarus.subtitler.app

import com.github.wtekiela.opensub4j.impl.OpenSubtitlesClientImpl
import org.jsoup.Connection
import org.jsoup.Jsoup
import org.slf4j.LoggerFactory
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.net.URL
import java.nio.ByteBuffer
import java.nio.channels.Channels
import java.util.zip.GZIPInputStream

object SubtitleGrabber {

    private val serverURL = URL("https", "api.opensubtitles.org", 443, "/xml-rpc")
    private val osClient = OpenSubtitlesClientImpl(serverURL)
    private val logger = LoggerFactory.getLogger(SubtitleGrabber.javaClass)
    private var cookies: Map<String, String> = mapOf()
    private val addictedURL = "https://www.addic7ed.com"

    init {
       osClient.login("en", "TemporaryUserAgent")
    }

    fun getSubtitle(title: String, file: File): Boolean {
        val id = parseTitle(title, file)
        return if (!getSubtitleOpenSubs(id, file)) {
            getSubtitleAddicted(id, file)
        } else {
            true
        }
    }

    fun parseTitle(title: String, file: File) : SubID {
        val split = file.name.split(".")
        var episode : String = ""
        split.forEach {
            if (it.toLowerCase().startsWith("s") && it.length > 1) {
                if ("0123456789".contains(it[1])) {
                    episode = it.toLowerCase()
                }
            }
        }
        val episodeArr = episode.split('e')
        return SubID(title, episodeArr[0].replace("s", ""), episodeArr[1])
    }

    private fun download(url: String, name: String, extract: Boolean = true) {
        val byteChannel = Channels.newChannel(URL(url).openStream())
        val fos = FileOutputStream("/tmp/$name")
        val fileChannel = fos.channel
        fileChannel.transferFrom(byteChannel, 0, Long.MAX_VALUE)
        fos.close()
        fileChannel.close()
        byteChannel.close()
        if (extract) extract(name)
        File("/tmp/$name").delete()
    }

    private fun extract(name: String) {
        val fin = FileInputStream("/tmp/$name")
        val fos = FileOutputStream("$name.srt")
        val gzin = GZIPInputStream(fin)
        val byteChannel = Channels.newChannel(gzin)

        val out = Channels.newChannel(fos)
        val buffer = ByteBuffer.allocate(65536)

        while(byteChannel.read(buffer) != -1) {
            buffer.flip()
            out.write(buffer)
            buffer.clear()
        }
        fin.close()
        fos.close()
        gzin.close()
        byteChannel.close()
        out.close()
    }

    fun logout() {
        if (osClient.isLoggedIn) osClient.logout()
    }

    private fun getSubtitleAddicted(id: SubID, file: File) : Boolean {
        if (cookies.isEmpty()) {
            val response = Jsoup.connect("$addictedURL/dologin.php").data("username", "lordxarus").data("password", "WowBadPassword").method(Connection.Method.POST).execute()
            cookies = response.cookies()
        }

        val response = Jsoup.connect("$addictedURL/search.php").cookies(cookies).data("search", id.fullName).method(Connection.Method.GET).execute()
        val elements = response.parse().getElementsByClass("buttonDownload")
        println(elements)
        if (elements.isNotEmpty()) {
            elements.forEachIndexed { index, element ->
                if (index == 0) {
                    println("$addictedURL${element.attr("href")}")
                    val fileRes = Jsoup.connect("$addictedURL${element.attr("href")}").cookies(cookies).header("Referer", response.url().toString()).ignoreContentType(true).execute().bodyAsBytes()
                    val out = BufferedOutputStream(FileOutputStream(file.name.replaceAfterLast(".", "") + "srt"))
                    out.write(fileRes)
                    out.close()
                    // download("$addictedURL${it.attr("href")}", file.name, true)
                    return true
                }
            }
        }
        return false
    }

    private fun getSubtitleOpenSubs(id: SubID, file: File): Boolean {
        val info = osClient.searchSubtitles("eng", id.title, id.season, id.episode)
        if (info.isNotEmpty()) {
            download(info[0].downloadLink, file.name)
            return true
        }
        return false
    }

}