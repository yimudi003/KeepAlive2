package com.keepalive.daemon.core;

import com.keepalive.daemon.core.utils.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.Map;

import static com.keepalive.daemon.core.Constants.COLON_SEPARATOR;

public class ShellExecutor {

    public static void execute(File dir, Map<String, String> map, String[] args) {
        try {
            ProcessBuilder builder = new ProcessBuilder(new String[0]);
            String envPath = System.getenv("PATH");
            Logger.v(Logger.TAG, "ENV PATH: " + envPath);
            if (envPath != null) {
                String[] split = envPath.split(COLON_SEPARATOR);
                int length = split.length;
                int i = 0;
                while (true) {
                    if (i >= length) {
                        break;
                    }
                    File f = new File(split[i], "sh");
                    if (f != null && f.exists()) {
                        builder.command(new String[]{f.getPath()}).redirectErrorStream(true);
                        break;
                    }
                    i++;
                }
            }
            builder.directory(dir);
            Map<String, String> env = builder.environment();
            env.putAll(System.getenv());
            if (map != null) {
                env.putAll(map);
            }
            StringBuilder sb = new StringBuilder();
            for (String append : args) {
                sb.append(append);
                sb.append("\n");
            }

            Process proc = builder.start();
            OutputStream os = proc.getOutputStream();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream(),
                        "utf-8"));
                for (String cmd : args) {
                    if (cmd.endsWith("\n")) {
                        os.write(cmd.getBytes());
                    } else {
                        os.write((cmd + "\n").getBytes());
                    }
                }
                os.write("exit 156\n".getBytes());
                os.flush();
                proc.waitFor();
                read(br);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (os != null) {
                    os.close();
                }
            }
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static String read(BufferedReader br) throws IOException {
        StringBuilder sb = new StringBuilder();
        String readLine;
        while ((readLine = br.readLine()) != null) {
            sb.append(readLine);
            sb.append("\n");
        }
        Logger.v(Logger.TAG, "read: " + sb);
        return sb.toString();
    }
}
