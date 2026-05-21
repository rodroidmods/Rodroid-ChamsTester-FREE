#ifndef CHAMS_SHADER_CAPTURE_H
#define CHAMS_SHADER_CAPTURE_H

#include <jni.h>
#include <string>
#include <set>
#include <unordered_set>
#include <mutex>
#include <atomic>
#include <vector>
#include <string.h>

namespace ShaderCapture {

static std::mutex g_mutex;
static std::set<std::string> g_names;
static std::unordered_set<unsigned int> g_enumeratedPrograms;
static std::string g_currentShader = "Off";
static std::atomic<bool> g_captureMode{true};

inline bool looksLikeUnityUniform(const char *name) {
    if (!name || !*name) return false;
    if (name[0] == '_') return true;
    if (strncmp(name, "unity_", 6) == 0) return true;
    if (strncmp(name, "hlslcc_", 7) == 0) return true;
    return false;
}

inline void record(const char *name) {
    if (!g_captureMode.load(std::memory_order_relaxed)) return;
    if (!looksLikeUnityUniform(name)) return;
    std::lock_guard<std::mutex> lk(g_mutex);
    g_names.emplace(name);
}

inline void clear() {
    std::lock_guard<std::mutex> lk(g_mutex);
    g_names.clear();
    g_enumeratedPrograms.clear();
}

inline bool markProgramEnumerated(unsigned int program) {
    std::lock_guard<std::mutex> lk(g_mutex);
    auto r = g_enumeratedPrograms.insert(program);
    return r.second; // true if newly inserted
}

inline void setCaptureMode(bool on) {
    g_captureMode.store(on, std::memory_order_relaxed);
}

inline bool isCaptureMode() {
    return g_captureMode.load(std::memory_order_relaxed);
}

inline void setCurrent(const std::string &name) {
    std::lock_guard<std::mutex> lk(g_mutex);
    g_currentShader = name.empty() ? "Off" : name;
}

inline std::string getCurrent() {
    std::lock_guard<std::mutex> lk(g_mutex);
    return g_currentShader;
}

inline std::vector<std::string> snapshot() {
    std::lock_guard<std::mutex> lk(g_mutex);
    return std::vector<std::string>(g_names.begin(), g_names.end());
}

inline size_t count() {
    std::lock_guard<std::mutex> lk(g_mutex);
    return g_names.size();
}

}

#endif
