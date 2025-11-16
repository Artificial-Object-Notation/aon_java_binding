package io.aon;

import com.sun.jna.Library;
import com.sun.jna.Pointer;

public interface AonNative extends Library {

    // char* aon_json_to_aon(const char* json_c, const char* root_c);
    Pointer aon_json_to_aon(String json, String root);

    // char* aon_aon_to_json(const char* aon_c);
    Pointer aon_aon_to_json(String aon);

    // const char* aon_last_error();
    Pointer aon_last_error();

    // void aon_free_string(char* ptr);
    void aon_free_string(Pointer ptr);
}
