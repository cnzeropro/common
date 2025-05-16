package org.zero.common.core.extension.java.net;

/**
 * 方案
 *
 * @author Zero (cnzeropro@163.com)
 * @since 2025/4/30
 */
public interface Schemes {
    String TCP = "tcp";
    String UDP = "udp";
    String HTTP = "http";
    String HTTPS = "https";
    String WS = "ws";
    String WSS = "wss";
    String JDBC = "jdbc";
    String REDIS = "redis";
    /**
     * Domain Socket Scheme
     */
    String UNIX = "unix";
    String FILE = "file";
    String JAR = "jar";
    String WAR = "war";
    String ZIP = "zip";
    String VFS = "vfs";
    String VFSFILE = "vfsfile";
    String VFSZIP = "vfszip";
    String WSJAR = "wsjar";
    String CLASSPATH = "classpath";
    String FTP = "ftp";
    String SFTP = "sftp";
    String SSH = "ssh";
    String SMTP = "smtp";
    String IMAP = "imap";
    String POP3 = "pop3";
    String TELNET = "telnet";
    String MAILTO = "mailto";
    String JAVASCRIPT = "javascript";
    String ED2K = "ed2k";
    String THUNDER = "thunder";
    String URN = "urn";
    String UUID = "uuid";
}
