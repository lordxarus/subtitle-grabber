package com.lordxarus.subtitler.app

import com.github.wtekiela.opensub4j.impl.OpenSubtitlesClientImpl
import org.slf4j.LoggerFactory
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

    init {
       osClient.login("en", "TemporaryUserAgent")
    }

    fun getSubtitle(title: String, file: File): Boolean {
            val id = parseTitle(title, file)
            val info = osClient.searchSubtitles("eng", id.title, id.season, id.episode)
            if (info.isNotEmpty()) {
                download(info[0].downloadLink, "${id.title} S${id.season}E${id.episode}")
                return true
            }
        return false
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

    private fun download(url: String, name: String) {
        val byteChannel = Channels.newChannel(URL(url).openStream())
        val fos = FileOutputStream("/tmp/$name")
        val fileChannel = fos.channel
        fileChannel.transferFrom(byteChannel, 0, Long.MAX_VALUE)
        fos.close()
        fileChannel.close()
        byteChannel.close()
        extract(name)
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

}