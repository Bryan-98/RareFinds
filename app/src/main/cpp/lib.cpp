#include <jni.h>
#include <string>
extern "C"
JNIEXPORT jstring JNICALL
Java_edu_practice_utils_shared_Keys_ip(JNIEnv *env, jobject thiz) {
    std::string ip = "rarefinds.database.windows.net:1433";
    return env->NewStringUTF(ip.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_edu_practice_utils_shared_Keys_db(JNIEnv *env, jobject thiz) {
std::string db = "rarefindsdb";
return env->NewStringUTF(db.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_edu_practice_utils_shared_Keys_user(JNIEnv *env, jobject thiz) {
std::string user = "MasterShake@rarefinds";
return env->NewStringUTF(user.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_edu_practice_utils_shared_Keys_pass(JNIEnv *env, jobject thiz) {
std::string pass = "Projectmanagermonkey222222$";
return env->NewStringUTF(pass.c_str());
}