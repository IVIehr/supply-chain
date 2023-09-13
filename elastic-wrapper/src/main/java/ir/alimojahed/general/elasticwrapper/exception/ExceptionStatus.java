package ir.alimojahed.general.elasticwrapper.exception;

/**
 * @author Ali Mojahed on 6/3/2021
 * @project spring-template
 **/

public enum ExceptionStatus {
    //region 400 Bad Request: The server could not understand the request due to invalid syntax.

    //region Invalids data
    // https://stackoverflow.com/questions/6123425/rest-response-code-for-invalid-data
    INVALID_REQUEST(400, "Invalid request", "درخواست معتبر نمی‌باشد."),
    INVALID_HASH_TYPE(400, "It's not a 'file' hash", "این هش متعلق به یک فایل نیست."),
    INVALID_DATA(400, "invalid data", "لیست هش فایل ها و فولدر ها هر دو با هم نمی توانند خالی باشند."),
    CANNOT_BOOKMARK_DELETED_ENTITY(400, "Deleted file or folder cannot be (un)bookmarked", "بوکمارک برای آیتم حذف شده قابل ویرایش نیست."),
    INVALID_REDIRECT_URI(400, "Invalid Redirect Uri", "درخواست معتبر نمی‌باشد."),
    INVALID_HASH(400, "Hash is invalid.", "شناسه صحیح نمی باشد."),
    INVALID_NAME(400, "Given name is invalid", "نام داده شده معتبر نمی باشد."),
    INVALID_AUTHENTICATION_INFORMATION(400, "Invalid authentication information.", "اطلاعات کاربر معتبر نمی‌باشد."),
    INVALID_SORT_PROPERTY(400, "Sort property is invalid.", "فیلد داده شده برای مرتب سازی معتبر نمی‌باشد."),
    LAST_FILE_VERSION(400, "It is not possible rollback to the last version of the file", "امکان رول بک به آخرین ورژن وجود ندارد."),
    INVALID_INPUT(400, "Invalid Input", "ورودی نامعتبر"),

    INVALID_EXPIRATION_DATE(400, "Invalid expiration date", "تاریخ انقضا اشتباه است."),
    INVALID_DATE(400, "Invalid date", "تاریخ قبل از زمان فعلی تعریف شده است."),
    INVALID_DATE_FORMAT(400, "Invalid Date format", "فرمت تاریخ اشتباه است."),
    INVALID_DATE_FORMAT_YYMMDD(400, "Invalid Date format. It should be formatted as yyyy-MM-dd", "فرمت تاریخ باید به صورت yyyy-MM-dd باشد."),
    INVALID_DATE_INTERVAL(400, "Invalid Date range ", "بازه ی تاریخ اشتباه ارسال شده است. تاریخ انتها پیش از تاریخ ابتدا قرار داده شده است."),
    INVALID_PHONE_NUMBER(400, "Invalid phone number", "شماره موبایل اشتباه است."),
    INVALID_NATIONAL_CODE(400, "Invalid national Code format", "فرمت کد مل اشتباه است."),
    INVALID_ALLOCATE_ACCESS_LEVEL(400, "Invalid allocate access level", "شما اجازه تخصیص سطح دسترسی بالاتر از دسترسی خودتان را ندارید."),
    INVALID_PAGINATION_PARAM(400, "Invalid pagination parameter", "مقدار پارامتر های صفحه بندی اشتباه است."),
    INVALID_FILTER_PARAMETER(400, "Invalid filter parameter", "پارامتر های داده شده برای فیلتر اشتباه است."),
    INVALID_FILEـSIZE(400, "Invalid filter parameter", "تعداد فایل های موجود در فایل فشرده شده بیش از حد مجاز است"),
    INVALID_JSON(400, "Invalid Json", "json ورودی نامعتبر است."), //endregion
    INVALID_REGION(400, "Invalid or Not Found Region", "منطقه مورد نظر یافت نشد"),
    INVALID_CITY(400, "Invalid or Not Found City", "شهر مورد نظر یافت نشد"),
    INVALID_STATE(400, "Invalid or Not Found State", "استان مورد نظر یافت نشد"),
    INVALID_BRANCH(400, "Invalid or Not Found Branch", "شعبه مورد نظر یافت نشد"),

    //region cyclical structures
    // Here  https://developer.box.com/guides/api-calls/permissions-and-errors/common-errors/
    MOVE_INSIDE_ITS_OWN(400, "You cannot move folder inside it's own", "انتقال پوشه به داخل خودش امکان پذیر نیست."),
    MOVE_INSIDE_ITS_SUBFOLDER(400, "You cannot move folder inside it's sub-folders", "انتقال پوشه به داخل یکی از زیرمجموعه‌هایش امکان ندارد."),
    COPY_INSIDE_ITS_OWN(400, "You cannot copy folder inside it's own", "کپی پوشه به داخل خودش امکان پذیر نیست."),
    COPY_INSIDE_ITS_SUBFOLDER(400, "You cannot copy folder inside it's sub-folders", "کپی پوشه به داخل یکی از زیرمجموعه‌هایش امکان ندارد."),
    //endregion

    // https://stackoverflow.com/questions/3290182/rest-http-status-codes-for-failed-validation-or-invalid-duplicate
    DUPLICATE_NAME(409, "This name is duplicate", "فایل یا پوشه ای با این نام در این پوشه وجود دارد."),
    ONLY_TRASHED_ENTITIES_CAN_BE_WIPED(400, "Only trashed File/Folder can be wiped", "برای فایل یا پوشه ای که در سطل زباله نیست امکان حذف دائمی وجود ندارد."),
    FILE_NOT_SENT(400, "File not Sent", "فایل ارسال نشده است."),
    ENTITY_BEEN_SHARED(400, "This folder has already been shared", "این فولدر قبلا اشتراک گذاری شده است."),
    INVALID_PLAN_ID(400, "PlanId is invalid.", "طرحی با این شناسه فعال نیست."),
    INVALID_PLAN_DATE(400, "Date is invalid.", "نمی‌توانید طرحی با مدت زمان کمتر از طرح قبلی خریداری کنید."),
    INVALID_BILL_NUMBER(400, "BillNumber is invalid.", "فاکتور قبلا کنسل شده است."),
    SSO_INVALID_REQUEST(400, "SSO Invalid request", "کد تایید اشتباه می باشد و یا زمان استفاده از آن به اتمام رسیده است"),

    //endregion

    ACCESS_DENIED(401, "Access denied", "شما به این منبع دسترسی ندارید."),

    //region 402 Payment Required
    INSUFFICIENT_SPACE(402, "Insufficient space", "فضای کافی برای ذخیره‌سازی وجود ندارد."),
    PAYMENT_REQUIRED(402, "Need to bye plan to use this.", "برای استفاده از این امکان باید یکی از طرح‌ها را خریداری کنید."),
    //endregion

    //region 403 Forbidden: It is unauthorized. Unlike 401, the client's identity is known to the server.
    WRONG_PASSWORD(403, "Password is wrong", "پسورد فایل معتبر نیست."),
    FORBIDDEN(403, "Forbidden", "درخواست دسترسی رد شد."),
    SHARE_FORBIDDEN(403, "You don't have needed access to share this object", "شما دسترسی لازم برای اشتراک گذاری این آیتم را ندارید."),
    INVALID_TOKEN(401, "Invalid Token", "توکن نامعتبر است."),
    //endregion

    //region 404 Not Found: The server can not find the requested resource.
    NOT_FOUND(404, "Not found", "درخواست مورد نظر پیدا نشد."),
    ENTITY_NOT_FOUND(404, "File or Folder not found", "فایل یا پوشه ی مورد نظر پیدا نشد."),
    FILE_NOT_FOUND(404, "File not found", "فایل مورد نظر پیدا نشد."),
    SHARE_NOT_FOUND(404, "Share not found", "اشتراک‌گذاری مورد نظر پیدا نشد."),
    USERGROUP_NOT_FOUND(404, "UserGroup not found", "گروه‌کاربری مورد نظر پیدا نشد."),
    USER_NOT_FOUND(404, "User not found", "کاربری با این مشخصه پیدا نشد."),
    DESTINATION_FOLDER_NOT_FOUND(404, "Destination folder not found", "پوشه ی مقصد پیدا نشد."),
    THUMBNAIL_NOT_FOUND(404, "Thumbnail not found", "پیش‌نمایش فایل وجود ندارد."),
    PLAN_NOT_FOUND(404, "Plan not found", "طرحی با این شناسه وجود ندارد."),
    BILL_NUMBER_NOT_FOUND(404, "BillNumber not found", "شناسه کسب و کاری فاکتور وجود ندارد."),
    AVATAR_NOT_FOUND(404, "Avatar not found", "عکس کاربر وجود ندارد."),
    METADATA_KEY_NOT_FOUND(404, "One of the given keys didn't found.", "یکی از کلیدهای داده شده پیدا نشد."),
    METADATA_KEYS_NOT_FOUND(404, "None of the given keys were found.", "هیچ کدام از کلیدهای داده شده پیدا نشد."),
    NOT_FOUND_PLATFORM(404, "Platform not found", "پلتفرم موردنظر وجود ندارد"),
    //endregion

    //region 406 Not Acceptable: The server doesn't find any content that conforms to the criteria given by the user agent.
    NAME_NOT_CHANGED(406, "Name not changed", "نام ارسال شده تغییر نکرد."),
    NOT_ACCEPTABLE(406, "Not acceptable", "محتوای ارسال شده قابل قبول نیست."),
    DEVICE_NOT_REGISTERED(401, "device not registered.", "دسنگاه ثبت انتساب داده نشده است."),
    //endregion


    // region Other 4XX errors: Client error responses,
    UNAUTHORIZED(401, "Unauthorized", "درخواست دسترسی معتبر نیست."),
    FILE_ALREADY_EXISTS(409, "This file is already sent for specified streamer", "این فایل قبلا برای این استریمر ارسال شده است."),
    TEAM_ALREADY_EXIST(409, "Team Already exist.", "حساب تیمی قبلا ایجاد شده است."),
    CONFLICT(409, "conflict", "درخواست شما با درخواست های گذشته تلاقی دارد."),
    Bulk_API_INVALID_BATCH_API(422, "One of URLs in this request can't support batch", "یکی از URLهای ارسالی از حالت batch پشتیبانی نمیکند."),
    Bulk_API_PAYLOAD_TOO_LARGE(413, "Bulk operations exceed the limitation", "تعداد عملیات داده شده بیش از سقف تعیین شده است."),
    MEDIA_TYPE_MISMATCH(415, "Replaced File has not same media-type", "فایل ارسالی برای جایگزینی، از نوع متفاوتی با فایل اصلی است."),
    //endregion


    //region 500 Internal Server Error: The server has encountered a situation it doesn't know how to handle.,
    UNKNOWN_ERROR(500, "Unknown error", "خطایی رخ داده است!"),
    ELASTIC_ERROR(500, "Unknown error", "خطایی رخ داده است!"),
    PROCESS_REQUEST_ERROR(500, "Process request error", "این امکان در حال حاضر وجود ندارد."),
    SSO_CONNECTION_ERROR(500, "sso connection error", "خطا در برقراری ارتباط با sso"),
    IOT_ERROR(500, "iot connection error", "حطا در برقراری ارتباط با سرویس های iot"),
    CORE_ERROR(500, "core connection error", "خطا در برقراری ارتباط با سرویس های کور"),
    DASHBOARD_ERROR(500, "dashboard services error", "خطا در اتصال به سرویس های داشبورد"),
    NOTIFICATION_ERROR(500, "notification error", "خطا در اتصال به سرویس های نوتیفیکشن"),
    ACL_ERROR(500, "acl error", "خطا در سرویس های acl"),
    DATABASE_ERROR(500, "database error", "خطا در ذخیره سازی"),
    //endregion


    //region Other 5XX errors: Server error responses
    //501 Not Implemented: The request method is not supported by the server and cannot be handled.,
    MUST_IMPLEMENT(501, "Must Implement", "پیاده سازی نشده"),
    BAD_GATEWAY(502, "Bad Gateway", "عدم توانایی برقراری ارتباط.")
    //endregion

    ;

    private String message;
    private String reasonPhrase;
    private int code;


    ExceptionStatus(int code, String reasonPhrase, String message) {
        this.reasonPhrase = reasonPhrase;
        this.message = message;
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public String getReasonPhrase() {
        return reasonPhrase;
    }

    public int getCode() {
        return code;
    }
}
