package com.keepalive.daemon.core;

import com.keepalive.daemon.core.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

public class ShellExecutor {
    private static final String COLON_SEPARATOR = ":";

    public static void execute(File file, Map map, String[] strArr) {
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

            try {
                Process process = processBuilder.start();
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
                read(bufferedReader);
            } catch (Throwable th) {
                th.printStackTrace();
            }
        }
    }

    private static String read(BufferedReader bufferedReader) throws IOException {
        StringBuilder sb = new StringBuilder();
        String readLine = bufferedReader.readLine();
        Logger.v(Logger.TAG, "readString: " + readLine);
        while (readLine != null) {
            Logger.v(Logger.TAG, "readString: " + readLine);
            sb.append(readLine);
            sb.append("\n");
            readLine = bufferedReader.readLine();
        }
        return sb.toString();
    }
}
