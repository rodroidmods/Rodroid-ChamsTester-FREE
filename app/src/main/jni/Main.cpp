#include <list>
#include <vector>
#include <string>
#include <string.h>
#include <mutex>
#include <pthread.h>
#include <thread>
#include <cstring>
#include <jni.h>
#include <unistd.h>
#include <fstream>
#include <iostream>
#include <dlfcn.h>
#include "Includes/Logger.h"
#include "Includes/obfuscate.h"
#include "Includes/Utils.h"
#include "Includes/Chams.h"
#include "KittyMemory/MemoryPatch.h"
#include "Menu/Setup.h"

static std::mutex g_targetLibMutex;
static std::string g_targetLibName = "libil2cpp.so";

static std::string getTargetLibName() {
    std::lock_guard<std::mutex> lk(g_targetLibMutex);
    return g_targetLibName;
}

static void setTargetLibName(const std::string &name) {
    std::lock_guard<std::mutex> lk(g_targetLibMutex);
    g_targetLibName = name.empty() ? std::string("libil2cpp.so") : name;
}

#include "Includes/Macros.h"


// we will run our hacks in a new thread so our while loop doesn't block process main thread
void *hack_thread(void *) {
    ProcMap il2cppMap;
    LOGI(OBFUSCATE("pthread created"));
    
    while (true) {
        std::string lib = getTargetLibName();
        if (isLibraryLoaded(lib.c_str())) break;
        sleep(2);
    }

      do {
        il2cppMap = KittyMemory::getLibraryMap(getTargetLibName().c_str());
        sleep(1);
    } while (!il2cppMap.isValid());

	  while (!mlovinit()) {
          sleep(1);
      }
	  setShader("Off");
      LogShaders();
      Wallhack();

    LOGI(OBFUSCATE("%s has been loaded"), getTargetLibName().c_str());

#if defined(__aarch64__) //To compile this code for arm64 lib only. Do not worry about greyed out highlighting code, it still works
    
#else //To compile this code for armv7 lib only.
    
    LOGI(OBFUSCATE("Done"));
#endif

    return NULL;
}

jobjectArray GetFeatureList(JNIEnv *env, jobject context) {
    jobjectArray ret;

    const char *features[] = {
            OBFUSCATE("GridViewLayout__Player Mods,Settings__face,settings"),

            OBFUSCATE("TabAdd__1__Category__Chams Mods"),
            OBFUSCATE("20__TabAdd__0__Toggle__True__Shader Capture Mode"),
            OBFUSCATE("21__TabAdd__0__Buttons__Show Shaders,Clear Shaders"),
            OBFUSCATE("22__TabAdd__0__Button__Pick Shader"),
            OBFUSCATE("1__TabAdd__0__InputText__Custom Shader"),
            OBFUSCATE("TabAdd__0__Category__Chams Feature"),
            OBFUSCATE("2__TabAdd__0__ArrayBox__Default,Shading,Wireframe,Glow,Outline,Heat"),
            OBFUSCATE("8__TabAdd__0__Toggle__Rainbow Chams"),
		    OBFUSCATE("9__TabAdd__0__Toggle__Auto Rainbow"),
		    OBFUSCATE("10__TabAdd__0__SeekBar__Line Width__1__10"),
		    OBFUSCATE("11__TabAdd__0__ColorPicker__Chams Color__255__255__0__0"),

            OBFUSCATE("TabAdd__1__Category__Settings"),
            OBFUSCATE("-10__TabAdd__1__InputText__Target Library"),
            OBFUSCATE("-11__TabAdd__1__RichTextView__Library Status: <i>checking...</i>"),
            OBFUSCATE("-1__TabAdd__1__Toggle__Save feature preferences"),
            OBFUSCATE("-3__TabAdd__1__Toggle__Auto size vertically"),
            OBFUSCATE("-4__TabAdd__1__SeekBar__Icon Opacity__0__10"),
            OBFUSCATE("-5__TabAdd__1__SeekBar__Icon Size__0__100"),
            OBFUSCATE("-6__TabAdd__1__Button__Hide Menu"),
            OBFUSCATE("-7__TabAdd__1__Button__Kill Menu"),
            OBFUSCATE("TabAdd__1__Category__True__Connected with me"),
            OBFUSCATE("TabAdd__1__ButtonLink__YouTube__https://youtube.com/@Blackgamermods?si=pHD7pvOV9enKTQJV"),
            OBFUSCATE("TabAdd__1__ButtonLink__Telegram__https://t.me/gamerblackzone"),
    };

    //Now you dont have to manually update the number everytime;
    int Total_Feature = (sizeof features / sizeof features[0]);
    ret = (jobjectArray)
            env->NewObjectArray(Total_Feature, env->FindClass(OBFUSCATE("java/lang/String")),
                                env->NewStringUTF(""));

    for (int i = 0; i < Total_Feature; i++)
        env->SetObjectArrayElement(ret, i, env->NewStringUTF(features[i]));

    return (ret);
}

void Changes(JNIEnv *env, jclass clazz, jobject obj,
                                        jint featNum, jstring featName, jint value, jint value2, jint value3, jint value4,
                                        jboolean boolean, jstring str) {

    LOGD(OBFUSCATE("Feature name: %d - %s | Value: = %d | Value2: = %d | Value3: = %d | Value4: = %d | Bool: = %d | Text: = %s"), featNum,
         env->GetStringUTFChars(featName, 0), value, value2, value3, value4,
         boolean, str != NULL ? env->GetStringUTFChars(str, 0) : "");

    //BE CAREFUL NOT TO ACCIDENTLY REMOVE break;

    switch (featNum) {
        case 1: {
            if (str != NULL) {
                const char *cstr = env->GetStringUTFChars(str, 0);
                if (cstr) {
                    ShaderCapture::setCurrent(std::string(cstr));
                    env->ReleaseStringUTFChars(str, cstr);
                }
            }
            break;
        }
        case 2:
            enableWallhack = boolean;
            break;
        case 3:
            enableWallhackS = boolean;
            break;
        case 4:
            enableWallhackW = boolean;
            break;
        case 5:
            enableWallhackG = boolean;
            break;
        case 6:
            enableWallhackO = boolean;
            break;
        case 7:
            enableHeatVision = boolean;
            break;
        case 8:
            enableRainbow = boolean;
            break;
        case 9:
            enableRainbow1 = boolean;
            break;
        case 10:
            SetW(value);
            break;
        case 11:
            SetA(value);
            SetR(value2);
            SetG(value3);
            SetB(value4);
            break;
        case 20:
            ShaderCapture::setCaptureMode((bool) boolean);
            break;
        case 21:
            if (value == 1) {
                ShaderCapture::clear();
            }
            break;
        case 22:
            // "Pick Shader" button - Java side handles dialog
            break;
        case -10: {
            if (str != NULL) {
                const char *cstr = env->GetStringUTFChars(str, 0);
                if (cstr) {
                    setTargetLibName(std::string(cstr));
                    env->ReleaseStringUTFChars(str, cstr);
                }
            }
            break;
        }
    }
}

jobjectArray GetShaderList(JNIEnv *env, jobject /*thiz*/) {
    std::vector<std::string> names = ShaderCapture::snapshot();
    jclass strCls = env->FindClass("java/lang/String");
    jobjectArray ret = env->NewObjectArray((jsize) names.size(), strCls, env->NewStringUTF(""));
    for (size_t i = 0; i < names.size(); i++) {
        jstring s = env->NewStringUTF(names[i].c_str());
        env->SetObjectArrayElement(ret, (jsize) i, s);
        env->DeleteLocalRef(s);
    }
    return ret;
}

void ClearShaders(JNIEnv * /*env*/, jobject /*thiz*/) {
    ShaderCapture::clear();
}

void SetCurrentShader(JNIEnv *env, jobject /*thiz*/, jstring name) {
    if (name == NULL) {
        ShaderCapture::setCurrent("Off");
        return;
    }
    const char *c = env->GetStringUTFChars(name, 0);
    if (!c) return;
    ShaderCapture::setCurrent(std::string(c));
    env->ReleaseStringUTFChars(name, c);
}

jstring GetCurrentShader(JNIEnv *env, jobject /*thiz*/) {
    return env->NewStringUTF(ShaderCapture::getCurrent().c_str());
}

void SetCaptureMode(JNIEnv * /*env*/, jobject /*thiz*/, jboolean on) {
    ShaderCapture::setCaptureMode((bool) on);
}

jboolean IsCaptureMode(JNIEnv * /*env*/, jobject /*thiz*/) {
    return (jboolean) ShaderCapture::isCaptureMode();
}

jstring GetTargetLibrary(JNIEnv *env, jobject /*thiz*/) {
    return env->NewStringUTF(getTargetLibName().c_str());
}

void SetTargetLibrary(JNIEnv *env, jobject /*thiz*/, jstring name) {
    if (name == NULL) return;
    const char *c = env->GetStringUTFChars(name, 0);
    if (!c) return;
    setTargetLibName(std::string(c));
    env->ReleaseStringUTFChars(name, c);
}

jboolean IsTargetLibraryLoaded(JNIEnv * /*env*/, jobject /*thiz*/) {
    std::string lib = getTargetLibName();
    return (jboolean) isLibraryLoaded(lib.c_str());
}

__attribute__((constructor))
void lib_main() {
    // Create a new thread so it does not block the main thread, means the game would not freeze
    pthread_t ptid;
    pthread_create(&ptid, NULL, hack_thread, NULL);
}

int RegisterMenu(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {OBFUSCATE("Icon"), OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(Icon)},
            {OBFUSCATE("IconWebViewData"),  OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(IconWebViewData)},
            {OBFUSCATE("IsGameLibLoaded"),  OBFUSCATE("()Z"), reinterpret_cast<void *>(isGameLibLoaded)},
            {OBFUSCATE("Init"),  OBFUSCATE("(Landroid/content/Context;Landroid/widget/TextView;Landroid/widget/TextView;)V"), reinterpret_cast<void *>(Init)},
            {OBFUSCATE("GetFeatureList"),  OBFUSCATE("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetFeatureList)},
            {OBFUSCATE("GetShaderList"),  OBFUSCATE("()[Ljava/lang/String;"), reinterpret_cast<void *>(GetShaderList)},
            {OBFUSCATE("ClearShaders"),  OBFUSCATE("()V"), reinterpret_cast<void *>(ClearShaders)},
            {OBFUSCATE("SetCurrentShader"),  OBFUSCATE("(Ljava/lang/String;)V"), reinterpret_cast<void *>(SetCurrentShader)},
            {OBFUSCATE("GetCurrentShader"),  OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(GetCurrentShader)},
            {OBFUSCATE("SetCaptureMode"),  OBFUSCATE("(Z)V"), reinterpret_cast<void *>(SetCaptureMode)},
            {OBFUSCATE("IsCaptureMode"),  OBFUSCATE("()Z"), reinterpret_cast<void *>(IsCaptureMode)},
            {OBFUSCATE("GetTargetLibrary"),  OBFUSCATE("()Ljava/lang/String;"), reinterpret_cast<void *>(GetTargetLibrary)},
            {OBFUSCATE("SetTargetLibrary"),  OBFUSCATE("(Ljava/lang/String;)V"), reinterpret_cast<void *>(SetTargetLibrary)},
            {OBFUSCATE("IsTargetLibraryLoaded"),  OBFUSCATE("()Z"), reinterpret_cast<void *>(IsTargetLibraryLoaded)},
    };

    jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Menu"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;
    return JNI_OK;
}

int RegisterPreferences(JNIEnv *env) {
    JNINativeMethod methods[] = {
        {OBFUSCATE("Changes"), OBFUSCATE("(Landroid/content/Context;ILjava/lang/String;IIIIZLjava/lang/String;)V"), reinterpret_cast<void *>(Changes)},
    };
    
    jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Preferences"));
    if (!clazz)
        return JNI_ERR;

    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;
        
    return JNI_OK;
}

int RegisterMain(JNIEnv *env) {
    JNINativeMethod methods[] = {
            {OBFUSCATE("CheckOverlayPermission"), OBFUSCATE("(Landroid/content/Context;)V"), reinterpret_cast<void *>(CheckOverlayPermission)},
    };
    jclass clazz = env->FindClass(OBFUSCATE("com/android/support/Main"));
    if (!clazz)
        return JNI_ERR;
    if (env->RegisterNatives(clazz, methods, sizeof(methods) / sizeof(methods[0])) != 0)
        return JNI_ERR;

    return JNI_OK;
}

extern "C"
JNIEXPORT jint JNICALL
JNI_OnLoad(JavaVM *vm, void *reserved) {
    JNIEnv *env;
    vm->GetEnv((void **) &env, JNI_VERSION_1_6);
    if (RegisterMenu(env) != 0)
        return JNI_ERR;
    if (RegisterPreferences(env) != 0)
        return JNI_ERR;
    if (RegisterMain(env) != 0)
        return JNI_ERR;
    return JNI_VERSION_1_6;
}
