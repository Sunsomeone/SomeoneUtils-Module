package com.someone.util;

/*
  @Author Someone
 * @Date 2024/11/09 09:42
 */
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.someone.debug.LogReceiver;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Scanner;

public class HttpUtil {

    public static class HttpConnection {
        public static final String CONNECTION_GET = "GET";
        private static final int BUFFER_SIZE = 4096;
        private HttpURLConnection connection;
        private URL url;
        private int responseCode;
        public HttpConnection(String url) {
            try {
                if (url != null) {
                    if (!url.isEmpty()) {
                        this.url = new URL(url);
                    }
                }
            } catch (MalformedURLException e) {
                LogReceiver.e("HttpUtil.HttpConnection() MalformedURLException" + e);
            }
            try {
                connection = (HttpURLConnection) this.url.openConnection();
            } catch (IOException e) {
                LogReceiver.e("HttpUtil.HttpConnection() IOException" + e);
            }
        }

        public HttpConnection request(String requestMode) {
            try {
                connection.setRequestMethod(requestMode);
                responseCode = connection.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return this;
                } else {
                    LogReceiver.i("网页访问失败 状态码: " + responseCode);
                }
            } catch (ProtocolException e) {
                LogReceiver.e("HttpUtil.HttpConnection.request() ProtocolException" + e);
            } catch (IOException e) {
                LogReceiver.e("HttpUtil.HttpConnection.request() IOException" + e);
                Handler mainHandler = new Handler(Looper.getMainLooper());
                mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(GlobalUtilSetting.getContext(), "网络连接异常，请检查网络连接。\n网络连接问题：设备无法连接到网络，因此无法访问DNS服务器。", Toast.LENGTH_LONG).show();
                            //Toast.makeText(GlobalUtilSetting.getContext(), "请检查网络连接", Toast.LENGTH_SHORT).show();
                        }});
            }
            return this;
        }

        public String getSourceRetrieval() {
            if (!isNotNull() || responseCode != HttpURLConnection.HTTP_OK) {
                return null;
            }
            StringBuilder content = new StringBuilder();
            try {
                Scanner scanner = new Scanner(connection.getInputStream());
                while (scanner.hasNextLine()) {
                    content.append(scanner.nextLine());
                    content.append(System.lineSeparator());
                }
                return content.toString();
            } catch (IOException e) {
                LogReceiver.e("HttpUtil.HttpConnection.getSourceRetrieval() IOException" + e);
            }
            return null;
        }

        private boolean isNotNull() {
            return connection != null && url != null;
        }

        public static void downloadFile(String fileUrl, String saveDir) throws Exception {
            URL url = new URL(fileUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
            httpConn.setRequestMethod("GET");
            httpConn.setDoOutput(true);
            httpConn.connect();

            if (httpConn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                InputStream inputStream = new BufferedInputStream(httpConn.getInputStream());
                String saveFilePath = saveDir + "/" + getFileName(httpConn);
                OutputStream outputStream = new FileOutputStream(saveFilePath);

                byte[] buffer = new byte[BUFFER_SIZE];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
                outputStream.close();
                inputStream.close();
                LogReceiver.i("下载完成");
            } else {
                LogReceiver.w("请求失败");
            }
            httpConn.disconnect();
        }

        private static String getFileName(HttpURLConnection httpConn) {
            String contentDisposition = httpConn.getHeaderField("Content-Disposition");
            if (contentDisposition != null && contentDisposition.contains("filename=")) {
                return contentDisposition.substring(contentDisposition.indexOf("filename=") + 9)
                    .replace("\"", ""); // Remove quotes if any
            } else {
                return httpConn.getURL().getFile().substring(httpConn.getURL().getFile().lastIndexOf('/') + 1);
            }
        }


        public byte[] download() {
            try {
                InputStream inputStream = connection.getInputStream();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int length;
                while ((length = inputStream.read(buffer)) != -1) {
                    byteArrayOutputStream.write(buffer, 0, length);
                }
                return byteArrayOutputStream.toByteArray();
            } catch (IOException e) {
                LogReceiver.e("HttpUtil.HttpConnection.download() IOException" + e);
            }
            return null;
        }

    }
}
