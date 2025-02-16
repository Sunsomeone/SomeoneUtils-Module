package com.someone.util;

/*
 * @Author Someone
 * @Date 2024/11/09 09:42
 */

import androidx.annotation.NonNull;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpUtils {

    public static class HttpConnection {
        public static final String CONNECTION_GET = "GET";
        private static final int BUFFER_SIZE = 4096;
        private HttpURLConnection connection;
        private URL url;
        private int responseCode;

        public HttpConnection(String url) throws IOException {
            if (url != null && !url.isEmpty()) {
                this.url = new URL(url);
                connection = (HttpURLConnection) this.url.openConnection();
            }
        }

        public static void downloadFile(String fileUrl, String saveDir) throws Exception {
            URL url = new URL(fileUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.connect();
            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = new BufferedInputStream(httpConn.getInputStream());
                String saveFilePath = saveDir + "/" + getFileName(httpConn);
                OutputStream outputStream = new BufferedOutputStream(new FileOutputStream(saveFilePath));
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();
            }
            httpConn.disconnect();
        }

        private static String getFileName(@NonNull HttpURLConnection httpConn) {
            String contentDisposition = httpConn.getHeaderField("Content-Disposition");
            if (contentDisposition != null && contentDisposition.contains("filename=")) {
                return contentDisposition.substring(contentDisposition.indexOf("filename=") + 9)
                        .replace("\"", "");
            } else {
                return httpConn.getURL().getFile().substring(httpConn.getURL().getFile().lastIndexOf('/') + 1);
            }
        }

        public HttpConnection request(String requestMode) throws IOException {
            connection.setRequestMethod(requestMode);
            responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return this;
            } else {
                throw new IllegalArgumentException("网页访问失败 状态码: " + responseCode);
            }
        }

        public String getSourceRetrieval() throws IOException {
            if (!isNotNull() || responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }
            StringBuilder content = new StringBuilder();
            BufferedInputStream inputStream = new BufferedInputStream(connection.getInputStream());
            try {
                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    content.append(new String(buffer, 0, bytesRead));
                }
            } finally {
                inputStream.close();
            }
            return content.toString();
        }

        private boolean isNotNull() {
            return connection != null && url != null;
        }

        public byte[] download() throws IOException {
            InputStream inputStream = connection.getInputStream();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                byteArrayOutputStream.write(buffer, 0, length);
            }
            return byteArrayOutputStream.toByteArray();
        }
    }
}