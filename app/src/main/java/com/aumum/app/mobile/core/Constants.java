

package com.aumum.app.mobile.core;

import com.aumum.app.mobile.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

/**
 * Bootstrap constants
 */
public final class Constants {
    private Constants() {}

    public static final String APP_NAME = "com.aumum.app.mobile";

    public static final class Auth {
        private Auth() {}

        /**
         * Account type id
         */
        public static final String BOOTSTRAP_ACCOUNT_TYPE = "com.aumum.app.mobile";

        /**
         * Auth token type
         */
        public static final String AUTH_TOKEN_TYPE = BOOTSTRAP_ACCOUNT_TYPE;

        /**
         * Account email
         */
        public static final String KEY_ACCOUNT_EMAIL = "accountEmail";
    }

    /**
     * All HTTP is done through a REST style API built for demonstration purposes on Parse.com
     * Thanks to the nice people at Parse for creating such a nice system for us to use for bootstrap!
     */
    public static final class Http {
        private Http() {}


        /**
         * Base URL for all requests
         */
        public static final String URL_BASE = "https://api.parse.com";

        /**
         * Login URL
         */
        public static final String URL_LOGIN_FRAG = "/1/login";

        /**
         * Reset Password URL
         */
        public static final String URL_RESET_PASSWORD_FRAG = "/1/requestPasswordReset";

        /**
         * Batch URL
         */
        public static final String URL_BATCH_FRAG = "/1/batch";

        /**
         * List Users URL
         */
        public static final String URL_USERS_FRAG = "/1/users";

        /**
         * User By Id URL
         */
        public static final String URL_USER_BY_ID_FRAG = "/1/users/{id}";

        /**
         * List Report URL
         */
        public static final String URL_REPORTS_FRAG = "/1/classes/Reports";

        /**
         * List Feedback URL
         */
        public static final String URL_FEEDBACK_FRAG = "/1/classes/Feedback";

        /**
         * List Area URL
         */
        public static final String URL_AREAS_FRAG = "/1/classes/Areas";

        /**
         * List Moments URL
         */
        public static final String URL_MOMENTS_FRAG = "/1/classes/Moments";

        /**
         * Moment By Id URL
         */
        public static final String URL_MOMENT_BY_ID_FRAG = "/1/classes/Moments/{id}";

        /**
         * List Moment Comments URL
         */
        public static final String URL_MOMENT_COMMENTS_FRAG = "/1/classes/MomentComments";

        /**
         * PARAMS for auth
         */
        public static final String PARAM_USERNAME = "username";
        public static final String PARAM_PASSWORD = "password";
        public static final String PARAM_EMAIL = "email";
        public static final String PARAM_OBJECT_ID = "objectId";
        public static final String PARAM_DELETED_AT = "deletedAt";

        /* Prod Keys
        public static final String PARSE_APP_ID = "1CWTxIB11kQiHz7QAY1hZzA1PstlJ2TQuAmZ8Nc6";
        public static final String PARSE_CLIENT_KEY = "Cxo0Yu68VFoZSDyHVl43qMTfYb23AG9uBrbOqgFk";
        public static final String PARSE_REST_API_KEY = "YT6Gt3CQUEtLkQsiCMMAnBIm7FrGkX8sqWMNElNx";
        public static final String PARSE_MASTER_KEY = "MMtsR9A9hywGlZ505DD43VbQwPIu7XT6KFVQpJm8";
        */
        /* Dev Keys */
        public static final String PARSE_APP_ID = "hJSBmj3YSXBuZkpXIPuFbR3nZiIZWr0uNfCFBXLl";
        public static final String PARSE_CLIENT_KEY = "8hexm2xMkmMrS5Y1vzXOpmPGr98lvCyO1IIX0ejM";
        public static final String PARSE_REST_API_KEY = "bLKzd37O5lF6o11FdQ2q0NwQferhjEEvIXFVxEcA";
        public static final String PARSE_MASTER_KEY = "2Cjggz9DM0HCCdhlmps7YuWk1SE1vnxnqjJhz3SI";

        public static final String HEADER_PARSE_REST_API_KEY = "X-Parse-REST-API-Key";
        public static final String HEADER_PARSE_APP_ID = "X-Parse-Application-Id";
        public static final String HEADER_PARSE_MASTER_KEY = "X-Parse-Master-Key";
        public static final String HEADER_PARSE_SESSION_TOKEN = "X-Parse-Session-Token";

        public static final class Batch {
            public static final String PARAM_REQUESTS = "requests";
        }

        public static final class User {
            public static final String PARAM_CONTACTS = "contacts";
            public static final String PARAM_AVATAR_URL = "avatarUrl";
            public static final String PARAM_SCREEN_NAME = "screenName";
            public static final String PARAM_EMAIL = "email";
            public static final String PARAM_COUNTRY = "country";
            public static final String PARAM_CITY = "city";
            public static final String PARAM_AREA = "area";
            public static final String PARAM_ABOUT = "about";
            public static final String PARAM_CHAT_ID = "chatId";
            public static final String PARAM_MOMENTS = "moments";
            public static final String PARAM_CREDIT = "credit";
        }

        public static final class Area {
            public static final String PARAM_CITY = "city";
        }

        public static final class Moment {
            public static final String PARAM_LIKES = "likes";
            public static final String PARAM_HOT = "hot";
            public static final String PARAM_FOLLOWERS = "followers";
        }

        public static final class MomentComment {
            public static final String PARAM_PARENT_ID = "parentId";
            public static final String PARAM_DELETED_AT = "deletedAt";
        }
    }

    public static final class DateTime {
        public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    }

    public static final class Map {
        private Map() {
        }

        public static LinkedHashMap<String, String> COUNTRY
                = new LinkedHashMap<String, String>() {
            {
                put("澳大利亚", "+61");
                put("新西兰", "+64");
                put("美国", "+1");
                put("加拿大", "+1");
            }
        };

        public static LinkedHashMap<String, LinkedHashMap<String, Integer>> CITY
                = new LinkedHashMap<String, LinkedHashMap<String, Integer>>() {
            {
                put("澳大利亚", new LinkedHashMap<String, Integer>() {
                    {
                        put("悉尼", 0);
                        put("墨尔本", 1);
                        put("布里斯班", 2);
                        put("珀斯", 3);
                        put("阿德莱德", 4);
                        put("堪培拉", 5);
                        put("达尔文", 6);
                        put("霍巴特", 7);
                        put("其他城市", 1000);
                    }
                });
                put("新西兰", new LinkedHashMap<String, Integer>() {
                    {
                        put("奥克兰", 8);
                        put("惠灵顿", 9);
                        put("其他城市", 1000);
                    }
                });
            }
        };
    }

    public static final class RequestCode {
        private RequestCode() {

        }

        public static final int SETTINGS_REQ_CODE = 30;
        public static final int GET_AREA_LIST_REQ_CODE = 31;
        public static final int NEW_MOMENT_REQ_CODE = 32;
        public static final int EDIT_PROFILE_REQ_CODE = 33;
    }

    public static final class Link {

        public static final String GOOGLE_PLAY_URL = "http://play.google.com/store/apps/details?id=";
        public static final String AGREEMENT = "http://www.aumums.com/agreement";
    }
}


