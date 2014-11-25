

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
        public static final String URL_USERS_FRAG = "/1/users";

        /**
         * User By Id URL
         */
        public static final String URL_USER_BY_ID_FRAG = "/1/users/{id}";

        /**
         * List Parties URL
         */
        public static final String URL_PARTIES_FRAG = "/1/classes/Parties";

        /**
         * Party By Id URL
         */
        public static final String URL_PARTY_BY_ID_FRAG = "/1/classes/Parties/{id}";

        /**
         * List Messages URL
         */
        public static final String URL_MESSAGES_FRAG = "/1/classes/Messages";

        /**
         * List Party Comments URL
         */
        public static final String URL_COMMENTS_FRAG = "/1/classes/PartyComments";

        /**
         * Party Comment By Id URL
         */
        public static final String URL_PARTY_COMMENT_BY_ID_FRAG = "/1/classes/PartyComments/{id}";

        /**
         * List Party Reasons URL
         */
        public static final String URL_PARTY_REASONS_FRAG = "/1/classes/PartyReasons";

        /**
         * PARAMS for auth
         */
        public static final String PARAM_USERNAME = "username";
        public static final String PARAM_PASSWORD = "password";
        public static final String PARAM_EMAIL = "email";
        public static final String PARAM_SCREEN_NAME = "screenName";
        public static final String PARAM_AREA = "area";
        public static final String PARAM_AVATAR_URL = "avatarUrl";

        public static final String PARSE_APP_ID = "1CWTxIB11kQiHz7QAY1hZzA1PstlJ2TQuAmZ8Nc6";
        public static final String PARSE_CLIENT_KEY = "Cxo0Yu68VFoZSDyHVl43qMTfYb23AG9uBrbOqgFk";
        public static final String PARSE_REST_API_KEY = "YT6Gt3CQUEtLkQsiCMMAnBIm7FrGkX8sqWMNElNx";
        public static final String PARSE_MASTER_KEY = "MMtsR9A9hywGlZ505DD43VbQwPIu7XT6KFVQpJm8";
        public static final String HEADER_PARSE_REST_API_KEY = "X-Parse-REST-API-Key";
        public static final String HEADER_PARSE_APP_ID = "X-Parse-Application-Id";
        public static final String HEADER_PARSE_MASTER_KEY = "X-Parse-Master-Key";
        public static final String HEADER_PARSE_SESSION_TOKEN = "X-Parse-Session-Token";

        public static final class Party {
            public static final String PARAM_MEMBERS = "members";
            public static final String PARAM_FANS = "fans";
            public static final String PARAM_COMMENTS = "comments";
            public static final String PARAM_REASONS = "reasons";
        }

        public static final class User {
            public static final String PARAM_CONTACTS = "contacts";
            public static final String PARAM_MESSAGES = "messages";
            public static final String PARAM_PARTIES = "parties";
            public static final String PARAM_AVATAR_URL = "avatarUrl";
            public static final String PARAM_CHAT_ID = "chatId";
        }
    }

    public static final class DateTime {
        public static final String FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    }

    public static final class Options {
        private Options() {
        }

        public static final String AREA_OPTIONS[] = {
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

        public static final String AGE_OPTIONS[] = {
            "不限",
            "备孕",
            "怀孕",
            "0 - 1岁",
            "1 - 3岁",
            "3 - 6岁",
            "6岁以上"
        };

        public static final String GENDER_OPTIONS[] = {
            "不限",
            "仅女孩",
            "仅男孩"
        };
    }

    public static final class RequestCode {
        private RequestCode() {

        }

        public static final int NEW_PARTY_REQ_CODE = 30;
        public static final int GET_PARTY_DETAILS_REQ_CODE = 31;
        public static final int SETTINGS_REQ_CODE = 32;
    }
}


