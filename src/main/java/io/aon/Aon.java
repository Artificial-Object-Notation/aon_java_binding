package io.aon;

import com.sun.jna.Native;
import com.sun.jna.Pointer;
import java.nio.charset.StandardCharsets;

/**
 * AON high-level Java API.
 *
 * It expects the native library from the Rust core to be available as:
 *   - libaon_core.so   (Linux)
 *   - libaon_core.dylib (macOS)
 *   - aon_core.dll      (Windows)
 *
 * and loadable via JNA with the base name "aon_core".
 */
public final class Aon {

    private static final AonNative LIB;

    static {
        // Tenta carregar via variável de ambiente AON_CORE_PATH (caminho completo)
        String customPath = System.getenv("AON_CORE_PATH");
        if (customPath != null && !customPath.isBlank()) {
            System.out.println("Carregando AON core via AON_CORE_PATH: " + customPath);
            LIB = Native.load(customPath, AonNative.class);
        } else {
            System.out.println("Carregando AON core via nome lógico: aon_core");
            LIB = Native.load("aon_core", AonNative.class);
        }
    }


    private Aon() {
    }

    private static String pointerToStringAndFree(Pointer ptr) {
        if (ptr == null) {
            return null;
        }
        try {
            // Read null-terminated UTF-8 string
            byte[] bytes = ptr.getString(0).getBytes(StandardCharsets.ISO_8859_1);
            String s = new String(bytes, StandardCharsets.UTF_8);
            return s;
        } finally {
            LIB.aon_free_string(ptr);
        }
    }

    private static String getLastError() {
        Pointer errPtr = LIB.aon_last_error();
        if (errPtr == null) {
            return null;
        }
        // NOTE: we don't free the error pointer because the Rust side currently doesn't
        // expose a dedicated free function for it; it may allocate a fresh CString each call.
        // In practice this is only used on failure paths and is tiny.
        String err = errPtr.getString(0);
        return err;
    }

    public static String jsonToAon(String json, String root) {
        if (json == null) throw new IllegalArgumentException("json must not be null");
        if (root == null) throw new IllegalArgumentException("root must not be null");

        Pointer ptr = LIB.aon_json_to_aon(json, root);
        if (ptr == null) {
            String err = getLastError();
            throw new RuntimeException("AON error: " + err);
        }
        return pointerToStringAndFree(ptr);
    }

    public static String aonToJson(String aon) {
        if (aon == null) throw new IllegalArgumentException("aon must not be null");

        Pointer ptr = LIB.aon_aon_to_json(aon);
        if (ptr == null) {
            String err = getLastError();
            throw new RuntimeException("AON error: " + err);
        }
        return pointerToStringAndFree(ptr);
    }
}
