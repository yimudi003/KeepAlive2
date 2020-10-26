
#include <jni.h>
#include <sys/wait.h>
#include <android/log.h>
#include <stdio.h>
#include <stdlib.h>
#include <unistd.h>
#include <sys/file.h>
#include <linux/android/binder.h>
#include <sys/mman.h>
#include "data_transact.h"
#include "cParcel.h"

using namespace android;

extern "C" {
void notify_and_waitfor(char *observer_self_path, char *observer_daemon_path) {
    int observer_self_descriptor = open(observer_self_path, O_RDONLY);
    if (observer_self_descriptor == -1) {
        observer_self_descriptor = open(observer_self_path, O_CREAT, S_IRUSR | S_IWUSR);
    }
    int observer_daemon_descriptor = open(observer_daemon_path, O_RDONLY);
    while (observer_daemon_descriptor == -1) {
        usleep(1000);
        observer_daemon_descriptor = open(observer_daemon_path, O_RDONLY);
    }
    remove(observer_daemon_path);
    LOGE("Watched >>>>OBSERVER<<<< has been ready...");
}


int lock_file(const char *lock_file_path) {
    LOGD("start try to lock file >> %s <<", lock_file_path);
    int lockFileDescriptor = open(lock_file_path, O_RDONLY);
    if (lockFileDescriptor == -1) {
        lockFileDescriptor = open(lock_file_path, O_CREAT, S_IRUSR);
    }
    int lockRet = flock(lockFileDescriptor, LOCK_EX);
    if (lockRet == -1) {
        LOGE("lock file failed >> %s <<", lock_file_path);
        return 0;
    } else {
        LOGD("lock file success  >> %s <<", lock_file_path);
        return 1;
    }
}

bool wait_file_lock(const char *lock_file_path) {
    int lockFileDescriptor;

    lockFileDescriptor = open(lock_file_path, O_RDONLY);
    if (lockFileDescriptor == -1)
        lockFileDescriptor = open(lock_file_path, 64, 256);
    while (flock(lockFileDescriptor, 6) != -1)
        usleep(1000);
    LOGD("retry lock file >> %s <<%d", lock_file_path, -1);
    return flock(lockFileDescriptor, LOCK_EX) != -1;
}

JNIEXPORT void JNICALL
Java_com_sogou_daemon_NativeKeepAlive_nativeSetSid(JNIEnv *env, jobject thiz) {
    setsid();
}

JNIEXPORT void JNICALL
Java_com_sogou_daemon_NativeKeepAlive_waitFileLock(JNIEnv *env, jobject thiz,
                                                   jstring path) {
    const char *file_path = (char *) env->GetStringUTFChars(path, 0);
    wait_file_lock(file_path);
}

JNIEXPORT void JNICALL
Java_com_sogou_daemon_NativeKeepAlive_lockFile(JNIEnv *env, jobject thiz,
                                               jstring lockFilePath) {
    const char *lock_file_path = (char *) env->GetStringUTFChars(lockFilePath, 0);
    lock_file(lock_file_path);
}

}