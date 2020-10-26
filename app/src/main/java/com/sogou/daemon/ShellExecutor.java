package com.sogou.daemon;

import com.sogou.daemon.utils.Utils;
import com.sogou.log.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

public class ShellExecutor {
    private static final String COLON_SEPARATOR = ":";
    public static void a(File file, Map map, String[] strArr) {
        if (strArr.length > 0) {
            ProcessBuilder processBuilder = new ProcessBuilder(new String[0]);
            String str = System.getenv("PATH");
            if (str != null) {
                String[] split = str.split(COLON_SEPARATOR);
                int length = split.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    File file2 = new File(split[i], "sh");
                    if (file2.exists()) {
                        processBuilder.command(new String[]{file2.getPath()}).redirectErrorStream(true);
                        break;
                    }
                    i++;
                }
            }
            processBuilder.directory(file);
            Map<String, String> environment = processBuilder.environment();
            environment.putAll(System.getenv());
            if (map != null) {
                environment.putAll(map);
            }
            StringBuilder sb = new StringBuilder();
            for (String append : strArr) {
                sb.append(append);
                sb.append("\n");
            }
            Process process = null;
            try {
                process = processBuilder.start();
            } catch (IOException e) {
            }
            try {
                OutputStream outputStream = process.getOutputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(
                        process.getInputStream(), "utf-8"));
                for (String str2 : strArr) {
                    if (str2.endsWith("\n")) {
                        outputStream.write(str2.getBytes());
                    } else {
                        outputStream.write((str2 + "\n").getBytes());
                    }
                }
                outputStream.write("exit 156\n".getBytes());
                outputStream.flush();
                process.waitFor();
                a(bufferedReader);
            } catch (IOException e2) {
            } catch (InterruptedException e3) {
            }
        }
    }

    public static String a(BufferedReader bufferedReader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String readLine = bufferedReader.readLine();
        Log.v(Log.TAG, "readString: " + readLine);
        while (readLine != null) {
            Log.v(Log.TAG, "readString: " + readLine);
            sb.append(readLine);
            sb.append("\n");
            readLine = bufferedReader.readLine();
        }
        return sb.toString();
    }
}
