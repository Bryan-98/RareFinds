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

extern "C"
JNIEXPORT jstring JNICALL
Java_edu_practice_utils_shared_Keys_blob(JNIEnv *env, jobject thiz) {
    std::string pass = "DefaultEndpointsProtocol=https;AccountName=rarefindsstorage;AccountKey=QepH2XJUlT29kpy+eb6UZCgf5UlkYJha+SnM70nyBFlXrLRYiPFRPB6Y8TtCxPq+FtnimP9KKSNubFj9gSfUBw==;EndpointSuffix=core.windows.net";
    return env->NewStringUTF(pass.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_edu_practice_utils_shared_Keys_clientId(JNIEnv *env, jobject thiz) {
    std::string pass = "pzqarw3s181p3re13e0m4rftc2xphl";
    return env->NewStringUTF(pass.c_str());
}

extern "C"
JNIEXPORT jstring JNICALL
Java_edu_practice_utils_shared_Keys_token(JNIEnv *env, jobject thiz) {
    std::string pass = "dmrud9qwnvs3mj693z41n6m6xawzzd";
    return env->NewStringUTF(pass.c_str());
}