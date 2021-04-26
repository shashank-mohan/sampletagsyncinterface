package com.tagbox.samplegatewayinterface;

public final class Constants {

    public static final String INTENT_START_SCANNER = "com.tagbox.intent.start_scanner";
    public static final String INTENT_FETCH_SENSOR_DATA = "com.tagbox.intent.fetch_sensor_data";
    public static final String INTENT_FETCH_LOCATION_DATA = "com.tagbox.intent.fetch_location_data";
    public static final String SENSOR_ID = "sensor_id";

    public static final String INTENT_SENSOR_DATA = "com.tagbox.intent.sensor_data";
    public static final String INTENT_LOCATION_DATA = "com.tagbox.intent.location_data";
    public static final String INTENT_TAGSYNC_ERROR = "com.tagbox.intent.tagsync_error";
    public static final String INTENT_PERMISSION_CHECK = "com.tagbox.intent.permission_check";


    public static final String SENSOR_DATA = "sensor_data";
    public static final String LOCATION_DATA = "location_data";
    public static final String ERROR_DATA = "error_data";
    public static final String PERMISSION_CHECK = "permission_check";

    public static final String DEMO_SENSOR_CLIENT_ID = "MX3EAE";

    public static final String BASE_URL = "https://api-manager-tagbox.azure-api.net/v2/";
    public static final String COMPANY_NAME = "company_name";
    //public static final String API_HEADER_VALUE = "bweeerdn8cd249319a09ab038d21b660";
    public static final String API_HEADER_VALUE = "baa90b518cd249319a09ab038d21b660";

    public static final String APK_VERSION_URL = BASE_URL + COMPANY_NAME+ "/eng/get-app-version/tag_sync.apk";
    public static final String APK_DOWNLOAD_URL = BASE_URL + COMPANY_NAME+ "/eng/download-app/tag_sync.apk";
    public static final String APK_FILE_NAME = "tag_sync.apk";
    public static final String API_HEADER_NAME = "ocm-subscription-key";

    public static final String INTENT_RECEIVED_START_SCAN = "com.tagbox.intent.received_ss";
    public static final String INTENT_RECEIVED_FETCH_DATA = "com.tagbox.intent.received_fd";

}
