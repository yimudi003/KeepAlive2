/*
 * Original work Copyright (c) 2016, Lody
 * Modified work Copyright (c) 2016, Alibaba Mobile Infrastructure (Android) Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.keepalive.daemon.core.utils;

public class RuntimeUtil {

    private volatile static boolean g64 = false;
    private volatile static boolean isArt = true;
    private volatile static String archType = null;

    static {
        try {
            g64 = (boolean) Class.forName("dalvik.system.VMRuntime")
                    .getDeclaredMethod("is64Bit")
                    .invoke(Class.forName("dalvik.system.VMRuntime")
                            .getDeclaredMethod("getRuntime")
                            .invoke(null));
        } catch (Throwable th) {
            Logger.e(Logger.TAG, "get is64Bit failed, default not 64bit!", th);
            g64 = false;
        }
        isArt = System.getProperty("java.vm.version").startsWith("2");
        archType = CPUArchUtil.getArchType();
        Logger.i(Logger.TAG, "is64Bit: " + g64 + ", isArt: " + isArt + ", archType: " + archType);
    }

    public static boolean is64Bit() {
        return g64;
    }

    public static boolean isArt() {
        return isArt;
    }
}
