

package com.aumum.app.mobile.core;

/**
 * Bootstrap constants
 */
public final class Constants {
    private Constants() {}

    public static final class Auth {
        private Auth() {}

        /**
         * Account type id
         */
        public static final String BOOTSTRAP_ACCOUNT_TYPE = "com.aumum.app.mobile";

        /**
         * Auth token type
         */
        public static final String AUTHTOKEN_TYPE = BOOTSTRAP_ACCOUNT_TYPE;
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
         * List Users URL
         */
        public static final String URL_USERS_FRAG =  "/1/users";

        /**
         * User By Id URL
         */
        public static final String URL_USER_BY_ID_FRAG =  "/1/users/{id}";

        /**
         * Current User URL
         */
        public static final String URL_USERS_ME_FRAG =  "/1/users/me";

        /**
         * List Parties URL
         */
        public static final String URL_PARTIES_FRAG = "/1/classes/Parties";

        /**
         * List Messages URL
         */
        public static final String URL_MESSAGES_FRAG = "/1/classes/Messages";

        /**
         * PARAMS for auth
         */
        public static final String PARAM_USERNAME = "username";
        public static final String PARAM_PASSWORD = "password";
        public static final String PARAM_EMAIL = "email";
        public static final String PARAM_AREA = "area";

        public static final String PARSE_APP_ID = "1CWTxIB11kQiHz7QAY1hZzA1PstlJ2TQuAmZ8Nc6";
        public static final String PARSE_REST_API_KEY = "YT6Gt3CQUEtLkQsiCMMAnBIm7FrGkX8sqWMNElNx";
        public static final String PARSE_MASTER_KEY = "MMtsR9A9hywGlZ505DD43VbQwPIu7XT6KFVQpJm8";
        public static final String HEADER_PARSE_REST_API_KEY = "X-Parse-REST-API-Key";
        public static final String HEADER_PARSE_APP_ID = "X-Parse-Application-Id";
        public static final String HEADER_PARSE_MASTER_KEY = "X-Parse-Master-Key";
        public static final String HEADER_PARSE_SESSION_TOKEN = "X-Parse-Session-Token";

        public static final class User {
            public static final String PARAM_FOLLOWERS = "followers";
            public static final String PARAM_FOLLOWINGS = "followings";
            public static final String PARAM_MESSAGES = "messages";
        }
    }

    public static String areaOptions[] = {
        "悉尼",
        "墨尔本",
        "布里斯班",
        "珀斯",
        "阿德莱德",
        "堪培拉",
        "达尔文",
        "霍巴特",
        "奥克兰",
        "惠灵顿"
    };

    public static String ageOptions[] = {
        "不限",
        "备孕",
        "怀孕",
        "0 - 1岁",
        "1 - 3岁",
        "3 - 6岁",
        "6岁以上"
    };

    public static String genderOptions[] = {
        "不限",
        "仅女孩",
        "仅男孩"
    };
}


