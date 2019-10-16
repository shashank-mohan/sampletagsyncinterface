package com.tagbox.samplegatewayinterface;

public final class Constants {

    public static final String INTENT_START_SCANNER = "com.tagbox.intent.start_scanner";
    public static final String INTENT_FETCH_SENSOR_DATA = "com.tagbox.intent.fetch_sensor_data";
    public static final String SENSOR_ID = "sensor_id";

    public static final String INTENT_SENSOR_DATA = "com.tagbox.intent.sensor_data";
    public static final String INTENT_TAGSYNC_ERROR = "com.tagbox.intent.tagsync_error";
    public static final String INTENT_PERMISSION_CHECK = "com.tagbox.intent.permission_check";


    public static final String SENSOR_DATA = "sensor_data";
    public static final String ERROR_DATA = "error_data";
    public static final String PERMISSION_CHECK = "permission_check";
    public static final String BASE_URL = "https://api-manager-tagbox.azure-api.net/v1";
    public static final String APK_VERSION_URL = BASE_URL + "/eng/get-app-version/tag_sync.apk";
    public static final String APK_DOWNLOAD_URL = BASE_URL + "/eng/download-app/tag_sync.apk";
    public static final String APK_FILE_NAME = "tag_sync.apk";
    public static final String API_HEADER_NAME = "ocm-subscription-key";
    public static final String API_HEADER_VALUE = "bc1cb5cf177a4fc38cc8fa882d98721e";

    public static final String INTENT_RECEIVED_START_SCAN = "com.tagbox.intent.received_ss";
    public static final String INTENT_RECEIVED_FETCH_DATA = "com.tagbox.intent.received_fd";

}
