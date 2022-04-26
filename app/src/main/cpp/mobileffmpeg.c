//
// Created by LENOVO on 1/10/2022.
//
#include <pthread.h>
#include <sys/types.h>
#include <sys/stat.h>

#include "jni.h"

#ifdef __cplusplus
extern "C" {
#endif

JNIEXPORT jint
Java_com_example_dokaraokeAI_SplitVocal_registerNewNativeFFmpegPipe(JNIEnv *env, jclass clazz,
                                                                    jstring ffmpeg_pipe_path) {
    const char *ffmpegPipePathString = (*env)->GetStringUTFChars(env, ffmpeg_pipe_path, 0);

    return mkfifo(ffmpegPipePathString, S_IRWXU | S_IRWXG | S_IROTH);
}

#ifdef __cplusplus
        }
#endif
