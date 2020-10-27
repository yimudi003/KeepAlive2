package com.keepalive.daemon.core.utils;

import android.util.Log;

import com.keepalive.daemon.core.BuildConfig;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.util.Locale;

public class CPUArchUtil {

    public static final String CPU_ARCHITECTURE_TYPE_32 = "32";
    public static final String CPU_ARCHITECTURE_TYPE_64 = "64";

    /**
     * ELF文件头 e_indent[]数组文件类标识索引
     */
    private static final int EI_CLASS = 4;
    /**
     * ELF文件头 e_indent[EI_CLASS]的取值：ELFCLASS32表示32位目标
     */
    private static final int ELFCLASS32 = 1;
    /**
     * ELF文件头 e_indent[EI_CLASS]的取值：ELFCLASS64表示64位目标
     */
    private static final int ELFCLASS64 = 2;

    /**
     * The system property key of CPU arch type
     */
    private static final String CPU_ARCHITECTURE_KEY_64 = "ro.product.cpu.abilist64";

    /**
     * The system libc.so file path
     */
    private static final String SYSTEM_LIB_C_PATH = "/system/lib/libc.so";
    private static final String SYSTEM_LIB_C_PATH_64 = "/system/lib64/libc.so";
    private static final String PROC_CPU_INFO_PATH = "/proc/cpuinfo";

    private static boolean LOGENABLE = BuildConfig.DEBUG;

    /**
     * Check if the CPU architecture is x86
     */
    public static boolean checkIfCPUx86() {
        //1. Check CPU architecture: arm or x86
        if (getSystemProperty("ro.product.cpu.abi", "arm").contains("x86")) {
            //The CPU is x86
            return true;
        } else {
            return false;
        }
    }

    /**
     * Get the CPU arch type: x32 or x64
     */
    public static String getArchType() {
        if (getSystemProperty(CPU_ARCHITECTURE_KEY_64, "").length() > 0) {
            if (LOGENABLE) {
                Logger.d(Logger.TAG, "CPU arch is 64bit");
            }
            return CPU_ARCHITECTURE_TYPE_64;
        } else if (isCPUInfo64()) {
            return CPU_ARCHITECTURE_TYPE_64;
        } else if (isLibc64()) {
            return CPU_ARCHITECTURE_TYPE_64;
        } else {
            if (LOGENABLE) {
                Logger.d(Logger.TAG, "return cpu DEFAULT 32bit!");
            }
            return CPU_ARCHITECTURE_TYPE_32;
        }
    }

    private static String getSystemProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> clazz = Class.forName("android.os.SystemProperties");
            Method get = clazz.getMethod("get", String.class, String.class);
            value = (String) (get.invoke(clazz, key, ""));
        } catch (Exception e) {
            if (LOGENABLE) {
                Log.d("getSystemProperty", "key = " + key + ", error = " + e.getMessage());
            }
        }

        if (LOGENABLE) {
            Log.d("getSystemProperty", key + " = " + value);
        }
        return value;
    }

    /**
     * Read the first line of "/proc/cpuinfo" file, and check if it is 64 bit.
     */
    private static boolean isCPUInfo64() {
        File cpuInfo = new File(PROC_CPU_INFO_PATH);
        if (cpuInfo != null && cpuInfo.exists()) {
            InputStream inputStream = null;
            BufferedReader bufferedReader = null;
            try {
                inputStream = new FileInputStream(cpuInfo);
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream), 512);
                String line = bufferedReader.readLine();
                if (line != null && line.length() > 0 && line.toLowerCase(Locale.US).contains("arch64")) {
                    if (LOGENABLE) {
                        Logger.d(Logger.TAG, PROC_CPU_INFO_PATH + " contains is arch64");
                    }
                    return true;
                } else {
                    if (LOGENABLE) {
                        Logger.d(Logger.TAG, PROC_CPU_INFO_PATH + " is not arch64");
                    }
                }
            } catch (Throwable t) {
                if (LOGENABLE) {
                    Logger.d(Logger.TAG, "read " + PROC_CPU_INFO_PATH + " error = " + t.toString());
                }
            } finally {
                try {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    if (inputStream != null) {
                        inputStream.close();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    /**
     * Check if system libc.so is 32 bit or 64 bit
     */
    private static boolean isLibc64() {
        File libcFile = new File(SYSTEM_LIB_C_PATH);
        if (libcFile != null && libcFile.exists()) {
            byte[] header = readELFHeaderIndentArray(libcFile);
            if (header != null && header[EI_CLASS] == ELFCLASS64) {
                if (LOGENABLE) {
                    Logger.d(Logger.TAG, SYSTEM_LIB_C_PATH + " is 64bit");
                }
                return true;
            }
        }

        File libcFile64 = new File(SYSTEM_LIB_C_PATH_64);
        if (libcFile64 != null && libcFile64.exists()) {
            byte[] header = readELFHeaderIndentArray(libcFile64);
            if (header != null && header[EI_CLASS] == ELFCLASS64) {
                if (LOGENABLE) {
                    Logger.d(Logger.TAG, SYSTEM_LIB_C_PATH_64 + " is 64bit");
                }
                return true;
            }
        }

        return false;
    }

    /**
     * ELF文件头格式是固定的:文件开始是一个16字节的byte数组e_indent[16]
     * e_indent[4]的值可以判断ELF是32位还是64位
     */
    private static byte[] readELFHeaderIndentArray(File libFile) {
        if (libFile != null && libFile.exists()) {
            FileInputStream inputStream = null;
            try {
                inputStream = new FileInputStream(libFile);
                if (inputStream != null) {
                    byte[] tempBuffer = new byte[16];
                    int count = inputStream.read(tempBuffer, 0, 16);
                    if (count == 16) {
                        return tempBuffer;
                    } else {
                        if (LOGENABLE) {
                            Log.e("readELFHeadrIndentArray", "Error: e_indent lenght should be 16, but actual is " + count);
                        }
                    }
                }
            } catch (Throwable t) {
                if (LOGENABLE) {
                    Log.e("readELFHeadrIndentArray", "Error:" + t.toString());
                }
            } finally {
                if (inputStream != null) {
                    try {
                        inputStream.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return null;
    }
}
