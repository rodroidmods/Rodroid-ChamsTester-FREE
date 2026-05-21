#ifndef HENZRY_CHAMS
#define HENZRY_CHAMS
#include <GLES2/gl2.h>
#include <dlfcn.h>
#include <Substrate/SubstrateHook.h>
#include <Substrate/CydiaSubstrate.h>
#include <android/log.h>
#include "ShaderCapture.h"

#define SC_LOG(...) __android_log_print(ANDROID_LOG_INFO, "ShaderCapture", __VA_ARGS__)

static void *handle;
static bool enableWallhack, enableWallhackS, enableWallhackW, enableWallhackG, enableWallhackO, enableRainbow, enableRainbow1, enableHeatVision;
static float r = 255.0f;
static float g = 0.0f;
static float b = 0.0f;
static float a = 255.0f;
static int w = 1;

float red = 255.0f;
float gren = 0.0f;
float blue = 0.0f;
float mi = 0.0f;

void setShader(const char* s) {
    ShaderCapture::setCurrent(s ? std::string(s) : std::string("Off"));
}
std::string getShader() {
    return ShaderCapture::getCurrent();
}

void SetWallhack(bool enable){
    enableWallhack = enable;
}

void SetWallhackS(bool enable){
    enableWallhackS = enable;
}

void SetWallhackW(bool enable){
    enableWallhackW = enable;
}

void SetWallhackG(bool enable){
    enableWallhackG = enable;
}

void SetWallhackO(bool enable){
    enableWallhackO = enable;
}

void SetRainbow(bool enable){
    enableRainbow = enable;
}
void SetRainbow1(bool enable){
    enableRainbow1 = enable;
}

void SetHeatVision(bool enable) {
    enableHeatVision = enable;
}

void SetR(int set){
    r = set;
}

void SetG(int set){
    g = set;
}

void SetB(int set){
    b = set;
}

void SetW(int set){
    w = set;
}

void SetA(int set){
    a = set;
}

bool getWallhackEnabled(){
    return enableWallhack;
}

bool getShadingEnabled(){
    return enableWallhackS;
}

bool getWireframeEnabled(){
    return enableWallhackW;
}

bool getGlowEnabled(){
    return enableWallhackG;
}

bool getOutlineEnabled(){
    return enableWallhackO;
}

bool getRainbowEnabled(){
    return enableRainbow;
}
bool getRainbow1Enabled(){
    return enableRainbow1;
}

bool getHeatVisionEnabled() {
    return enableHeatVision;
}

int (*old_glGetUniformLocation)(GLuint, const GLchar *);
GLint new_glGetUniformLocation(GLuint program, const GLchar *name) {
    ShaderCapture::record(name);
    return old_glGetUniformLocation(program, name);
}

static inline void enumerateProgramUniforms(GLuint program) {
    if (program == 0) return;
    if (!ShaderCapture::markProgramEnumerated((unsigned int) program)) return;

    GLint linkStatus = 0;
    glGetProgramiv(program, GL_LINK_STATUS, &linkStatus);
    if (linkStatus != GL_TRUE) return;

    GLint numUniforms = 0;
    glGetProgramiv(program, GL_ACTIVE_UNIFORMS, &numUniforms);
    if (numUniforms <= 0) return;

    GLint maxLen = 0;
    glGetProgramiv(program, GL_ACTIVE_UNIFORM_MAX_LENGTH, &maxLen);
    if (maxLen < 1) maxLen = 256;

    std::vector<char> buf((size_t) maxLen + 1);
    int recorded = 0;
    for (GLint u = 0; u < numUniforms; u++) {
        GLsizei len = 0;
        GLint sz = 0;
        GLenum tp = 0;
        glGetActiveUniform(program, (GLuint) u, maxLen, &len, &sz, &tp, buf.data());
        if (len > 0) {
            ShaderCapture::record(buf.data());
            recorded++;
        }
    }
    SC_LOG("program %u: enumerated %d uniforms (recorded %d)", program, numUniforms, recorded);
}

void (*old_glLinkProgram)(GLuint program);
void new_glLinkProgram(GLuint program) {
    old_glLinkProgram(program);
    if (ShaderCapture::isCaptureMode()) {
        enumerateProgramUniforms(program);
    }
}

void (*old_glUseProgram)(GLuint program);
void new_glUseProgram(GLuint program) {
    old_glUseProgram(program);
    if (program != 0 && ShaderCapture::isCaptureMode()) {
        enumerateProgramUniforms(program);
    }
}

bool isCurrentShader(const char *shader) {
    if (!shader || !*shader) return false;
    GLint currProgram;
    glGetIntegerv(GL_CURRENT_PROGRAM, &currProgram);
    return old_glGetUniformLocation(currProgram, shader) != -1;
}

void (*old_glDrawElements)(GLenum mode, GLsizei count, GLenum type, const void *indices);
void new_glDrawElements(GLenum mode, GLsizei count, GLenum type, const void *indices) {
    old_glDrawElements(mode, count, type, indices);
    if (ShaderCapture::isCaptureMode()) {
        GLint cp = 0;
        glGetIntegerv(GL_CURRENT_PROGRAM, &cp);
        if (cp > 0) enumerateProgramUniforms((GLuint) cp);
    }

    if (mode != GL_TRIANGLES || count < 1000) return;
    {
        GLint currProgram;
        glGetIntegerv(GL_CURRENT_PROGRAM, &currProgram);

        std::string currentShader = getShader();
        if (currentShader.empty() || currentShader == "Off") return;
        GLint id = old_glGetUniformLocation(currProgram, currentShader.c_str());
        if (id == -1) return;

        if (getWireframeEnabled()) {
            if (enableWallhackW) {
                glDepthRangef(1, 0.5);
            } else {
                glDepthRangef(0.5, 1);
            }
            glBlendColor(GLfloat(r/255), GLfloat(g/255), GLfloat(b/255), GLfloat(a/255));
            glColorMask(1, 1, 1, 1);
            glEnable(GL_BLEND);
            glBlendFuncSeparate(GL_CONSTANT_COLOR, GL_CONSTANT_ALPHA, GL_ONE, GL_ZERO);
            glLineWidth(w);
            old_glDrawElements(GL_LINE_LOOP, count, type, indices);
        }

        if (getWallhackEnabled()) {
            glBlendColor(GLfloat(r/255), GLfloat(g/255), GLfloat(b/255), GLfloat(a/255));
            glColorMask(1, 1, 1, 1);
            glEnable(GL_BLEND);
            glBlendFunc(GL_CONSTANT_ALPHA, GL_CONSTANT_COLOR);
        }

        if (getShadingEnabled()) {
            glDepthRangef(1, 0.5);
            glEnable(GL_BLEND);
            glBlendFunc(GL_SRC_COLOR, GL_CONSTANT_COLOR);
            glBlendEquation(GL_FUNC_ADD);
            glBlendColor(GLfloat(r/255), GLfloat(g/255), GLfloat(b/255), GLfloat(a/255));
            glDepthFunc(GL_ALWAYS);
            old_glDrawElements(GL_TRIANGLES, count, type, indices);
            glColorMask(1, 1, 1, 1);
            glBlendFunc(GL_DST_COLOR, GL_ONE);
            glDepthFunc(GL_LESS);
            glBlendColor(0.0, 0.0, 0.0, 0.0);
        }

        if (getGlowEnabled()) {
            glEnable(GL_BLEND);
            glBlendColor(GLfloat(r/255), GLfloat(g/255), GLfloat(b/255), GLfloat(a/255));
            glColorMask(1, 1, 1, 1);
            glBlendFuncSeparate(GL_CONSTANT_COLOR, GL_CONSTANT_ALPHA, GL_ONE, GL_ZERO);
            glLineWidth(w);
            glDepthRangef(0.5, 1);
            old_glDrawElements(GL_LINES, count, type, indices);
            glBlendColor(1, 1, 1, 1);
            glDepthRangef(1, 0.5);
            old_glDrawElements(GL_TRIANGLES, count, type, indices);
        }

        if (getOutlineEnabled()) {
            glDepthRangef(1, 0.5);
            glLineWidth(w);
            glEnable(GL_BLEND);
            glColorMask(1, 1, 1, 1);
            glBlendFuncSeparate(GL_CONSTANT_COLOR, GL_CONSTANT_ALPHA, GL_ONE, GL_ZERO);
            glBlendColor(0, 0, 0, 1);
            old_glDrawElements(GL_TRIANGLES, count, type, indices);
            glBlendColor(GLfloat(r/255), GLfloat(g/255), GLfloat(b/255), GLfloat(a/255));
            old_glDrawElements(GL_LINES, count, type, indices);
        }

        if (getRainbowEnabled()) {
            if(getRainbow1Enabled()){
                if (red == 255){
                    if (blue == 0 ){
                        if (gren == 255){} else{
                            gren = gren+1;
                        }
                    }
                }
                if (gren == 255){
                    if (red == 0){} else{
                        red = red-1;
                    }
                }
                if (gren == 255) {
                    if (red == 0) {
                        if (blue==255){} else{
                            blue = blue+1;
                        }
                    }
                }
                if (blue == 255) {
                    if (gren == 0) {
                        mi = 1;
                        red = red+1;
                    } else{
                        gren = gren-1;
                    }
                }
                if (mi == 1){
                    if (red == 255){
                        if (blue == 0){} else{
                            blue = blue-1;
                        }
                    }
                }
                SetR(red);
                SetG(gren);
                SetB(blue);
            }
            glBlendColor(GLfloat(r/255), GLfloat(g/255), GLfloat(b/255), GLfloat(a/255));
            glColorMask(1, 1, 1, 1);
            glEnable(GL_BLEND);
            glBlendFunc(GL_ONE_MINUS_CONSTANT_COLOR, GL_ONE_MINUS_CONSTANT_ALPHA);
        }

        if (getHeatVisionEnabled()) {
            glDepthRangef(1, 0.5);
            glEnable(GL_BLEND);
            glColorMask(1, 1, 1, 1);
            glBlendEquation(GL_FUNC_ADD);
            glBlendFunc(GL_ONE, GL_ONE);
            glBlendColor(1.0f, 0.3f, 0.0f, 1.0f);
            old_glDrawElements(GL_TRIANGLES, count, type, indices);
            glBlendFunc(GL_DST_COLOR, GL_SRC_COLOR);
            glBlendColor(1.0f, 0.0f, 0.0f, 0.8f);
            old_glDrawElements(GL_TRIANGLES, count, type, indices);
            glDepthRangef(0.5, 1);
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            glBlendColor(1.0f, 1.0f, 0.0f, 0.5f);
            old_glDrawElements(GL_TRIANGLES, count, type, indices);
        }

        old_glDrawElements(mode, count, type, indices);
        glDepthRangef(0.5, 1);
        glColorMask(1, 1, 1, 1);
        glDisable(GL_BLEND);
    }
}

bool mlovinit(){
    handle = NULL;
    handle = dlopen("libGLESv2.so", RTLD_LAZY);
    if(!handle){
        return false;
    }
    return true;
}

void LogShaders(){
    if(!handle) return;
    dlerror();
    auto p_glGetUniformLocation = (void*)dlsym(handle, "glGetUniformLocation");
    if(p_glGetUniformLocation && !dlerror()){
        MSHookFunction(p_glGetUniformLocation, reinterpret_cast<void*>(new_glGetUniformLocation), reinterpret_cast<void**>(&old_glGetUniformLocation));
        SC_LOG("hooked glGetUniformLocation");
    } else {
        SC_LOG("FAILED to hook glGetUniformLocation");
    }

    dlerror();
    auto p_glLinkProgram = (void*)dlsym(handle, "glLinkProgram");
    if(p_glLinkProgram && !dlerror()){
        MSHookFunction(p_glLinkProgram, reinterpret_cast<void*>(new_glLinkProgram), reinterpret_cast<void**>(&old_glLinkProgram));
        SC_LOG("hooked glLinkProgram");
    } else {
        SC_LOG("FAILED to hook glLinkProgram");
    }

    dlerror();
    auto p_glUseProgram = (void*)dlsym(handle, "glUseProgram");
    if(p_glUseProgram && !dlerror()){
        MSHookFunction(p_glUseProgram, reinterpret_cast<void*>(new_glUseProgram), reinterpret_cast<void**>(&old_glUseProgram));
        SC_LOG("hooked glUseProgram");
    } else {
        SC_LOG("FAILED to hook glUseProgram");
    }
}

void Wallhack(){
    if(!handle) return;
    dlerror();
    auto p_glDrawElements = (void*)dlsym(handle, "glDrawElements");
    if(p_glDrawElements && !dlerror()){
        MSHookFunction(p_glDrawElements, reinterpret_cast<void*>(new_glDrawElements), reinterpret_cast<void**>(&old_glDrawElements));
        SC_LOG("hooked glDrawElements");
    } else {
        SC_LOG("FAILED to hook glDrawElements");
    }
}


#endif

