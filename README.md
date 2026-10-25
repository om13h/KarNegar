# 📱 کارنگار (KarNegar)

اپلیکیشن اندرویدی مدیریت کارهای روزمره همراه با یادآور، تقویم شمسی و حالت تیره — ساخته‌شده با **Kotlin** و **Jetpack Compose**.

![Build Status](https://github.com/USERNAME/KarNegar/actions/workflows/android.yml/badge.svg)

## ✨ امکانات

- ✅ افزودن، ویرایش و حذف کارهای روزمره
- ⏰ **یادآور با نوتیفیکیشن** در زمان مشخص (یادآور یک‌باره یا تکرارشونده روزانه/هفتگی)
- 💤 دکمه‌های «انجام شد» و «۵ دقیقه دیگر» (تعویق) روی خود نوتیفیکیشن
- 🗓️ **انتخاب تاریخ شمسی (جلالی)** با نمایش «امروز» و «فردا»
- 🏷️ دسته‌بندی کارها (شخصی، کاری، خرید، سلامت، سایر) با رنگ‌بندی
- 🔺 اولویت‌بندی (بالا / متوسط / پایین)
- 📊 کارت پیشرفت با نوار درصدی و تعداد کارهای انجام‌شده
- 🔍 جستجو + فیلتر بر اساس وضعیت و دسته‌بندی
- 🌙 حالت تیره و روشن (هماهنگ با سیستم یا انتخاب دستی)
- 🔄 یادآورها بعد از ری‌استارت گوشی به‌صورت خودکار دوباره فعال می‌شوند
- 🇮🇷 رابط کاربری کاملاً فارسی و راست‌به‌چپ (RTL)

## 🛠️ تکنولوژی‌ها

| مورد | نسخه |
|---|---|
| زبان | Kotlin 2.0.21 |
| رابط کاربری | Jetpack Compose + Material 3 |
| پایگاه داده | Room 2.6.1 |
| تنظیمات | DataStore Preferences |
| یادآور | AlarmManager (هشدار دقیق) + BroadcastReceiver |
| حداقل اندروید | 8.0 (API 26) |
| اندروید هدف | 15 (API 35) |
| JDK برای بیلد | 17 |

## 📂 ساختار پروژه

```
KarNegar/
├── .github/workflows/android.yml   ← بیلد خودکار APK روی گیت‌هاب
├── keystore/
│   └── karnegar-release.keystore   ← کلید امضای Release
├── app/src/main/
│   ├── AndroidManifest.xml
│   ├── java/com/karnegar/app/
│   │   ├── MainActivity.kt         ← ناوبری و شروع برنامه
│   │   ├── KarNegarApplication.kt
│   │   ├── data/                   ← Room (Task, Dao, Database) + DataStore
│   │   ├── reminders/              ← AlarmManager، نوتیفیکیشن، Receivers
│   │   ├── ui/
│   │   │   ├── screens/            ← لیست، افزودن/ویرایش، تنظیمات، دیالوگ‌ها
│   │   │   └── theme/              ← رنگ‌ها، تم تیره/روشن
│   │   ├── util/PersianDateUtils.kt← تبدیل و فرمت تاریخ شمسی
│   │   └── viewmodel/TaskViewModel.kt
│   └── res/                        ← آیکون‌ها، رشته‌ها، تم
├── build.gradle.kts
└── settings.gradle.kts
```

---

## 🚀 راه‌اندازی روی گیت‌هاب (دو روش)

### روش ۱: فقط با مرورگر — بدون نصب هیچ چیز (پیشنهادی)

1. در سایت [github.com](https://github.com) وارد حسابت شو (اگر حساب نداری، ثبت‌نام رایگان کن).
2. روی **+** بالای صفحه بزن و **New repository** را انتخاب کن.
3. یک اسم بگذار مثل `KarNegar` و **Public** بگذار → **Create repository**.
4. در صفحه ریپو روی لینک **uploading an existing file** بزن (یا از تب **Files** دکمه **Add file → Upload files**).
5. کل محتویات پوشه `KarNegar` (فایل ZIP را قبلاً از حالت فشرده خارج کن) را بکش و رها کن.
   - ⚠️ دقت کن محتویاتِ داخل پوشه آپلود شوند، نه خود پوشه؛ یعنی فایل `settings.gradle.kts` باید در ریشه ریپو باشد.
6. پایین صفحه یک پیام Commit بنویس (مثلاً `Initial commit`) و **Commit changes** بزن.
7. تمام! بلافاصله تب **Actions** شروع به بیلد گرفتن می‌کند.

### روش ۲: با خط فرمان (git)

```bash
cd KarNegar

# اول پروژه را یک Commit اولیه بساز
git init
git add .
git commit -m "KarNegar - initial release"

# ریپو را در گیت‌هاب بساز (با gh cli) یا از سایت بساز و آدرسش را جایگزین کن
git remote add origin https://github.com/USERNAME/KarNegar.git
git branch -M main
git push -u origin main
```

---

## 📥 دانلود و نصب APK از گیت‌هاب

### راه ۱: دانلود مستقیم از تب Releases (ساده‌ترین راه — بدون ZIP)

1. وارد ریپو شو و روی تب **Releases** بزن.
2. Release با نام «آخرین نسخه — دانلود مستقیم APK» را باز کن.
3. فایل **`KarNegar-release.apk`** را دانلود کن — این خودِ APK است و ZIP نیست!
4. فایل را روی گوشی باز کن و نصبش کن (اگر پرسید، «نصب از منابع ناشناس» را فعال کن).

لینک مستقیم دانلود همیشه همین است:

```
https://github.com/USERNAME/KarNegar/releases/latest/download/KarNegar-release.apk
```

### راه ۲: از تب Actions (آرتیفکت ZIP)

1. وارد ریپو شو و روی تب **Actions** بزن.
2. آخرین ورک‌فلو **Build Android APK** را باز کن (سبز رنگ = موفق).
3. پایین صفحه در بخش **Artifacts** روی **KarNegar-APKs** کلیک کن تا ZIP دانلود شود.

> ⚠️ **دقت:** فایلی که از Artifacts دانلود می‌شود یک **ZIP** است، نه APK و نه فرمت apks! باید اول آن را از حالت فشرده خارج کنی؛ فایل‌های واقعی APK داخلش هستند:
   - **`KarNegar-release.apk`** ← این را نصب کن (امضاشده، مناسب استفاده روزمره)
   - `KarNegar-debug.apk` (فقط برای تست توسعه‌دهنده)
4. فایل APK را به گوشی منتقل کن و نصبش کن. اگر پرسید، گزینه **نصب از منابع ناشناس / Install unknown apps** را برای مرورگر یا فایل‌منیجرت فعال کن.

> 💡 اولین بیلد حدود ۵ تا ۸ دقیقه طول می‌کشد (چون Gradle را از صفر دانلود می‌کند). بیلدهای بعدی خیلی سریع‌تر هستند.

### از تب Releases (نسخه رسمی)

وقتی خواستی یک نسخه رسمی منتشر کنی (توضیح در بخش بعد)، فایل APK مستقیماً در تب **Releases** ریپو ظاهر می‌شود و می‌توانی لینک آن را با دوستانت به اشتراک بگذاری.

---

## 🏷️ انتشار نسخه جدید (Release)

هر وقت تغییری در کد دادی و خواستی نسخه جدید بدهی:

1. در `app/build.gradle.kts` مقدار `versionCode` را یکی افزایش بده (مثلاً `2`) و `versionName` را به‌روز کن (مثلاً `"1.1.0"`).
2. در سایت گیت‌هاب: تب **Releases** → **Create a new release** → در قسمت tag یک تگ جدید مثل `v1.1.0` بنویس → **Publish release**.
3. ورک‌فلو خودکار اجرا می‌شود و **APK امضاشده** به همان Release اضافه می‌شود.

یا با خط فرمان:

```bash
git tag v1.1.0
git push origin v1.1.0
```

---

## 🔐 امضای APK و اطلاعات Keystore

پروژه برای راحتی شما، **کلید امضا و رمزها را داخل خود ریپو** قرار داده تا بیلد Release بدون هیچ تنظیم اضافه‌ای روی گیت‌هاب امضا شود:

| مشخصه | مقدار |
|---|---|
| فایل Keystore | `keystore/karnegar-release.keystore` |
| الگوریتم | RSA 2048 (PKCS12) |
| Keystore Password | `***REMOVED***` |
| Key Alias | `karnegar_key` |
| Key Password | `***REMOVED***` |
| اعتبار | تا سال ۲۰۵۶ (۱۰,۹۵۰ روز) |

این مقادیر در `app/build.gradle.kts` داخل بلوک `signingConfigs` تنظیم شده‌اند.

> ⚠️ **هشدار امنیتی:** این حالت برای اپ شخصی/آموزشی کاملاً مناسب است، اما اگر روزی اپ را برای عموم منتشر کردی:
> 1. رمزها را از کد خارج کن و در `local.properties` یا **GitHub Secrets** بگذار.
> 2. یک Keystore کاملاً جدید با رمز قوی بساز و فایل قبلی را از ریپو حذف کن.
>
> دستور ساخت Keystore جدید:
> ```bash
> keytool -genkeypair -v -keystore karnegar-release.keystore \
>   -alias karnegar_key -keyalg RSA -keysize 2048 -validity 10950 \
>   -storepass YOUR_STORE_PASS -keypass YOUR_KEY_PASS
> ```
>
> 🔑 **کلید موجود را گم نکن!** برای اینکه آپدیت‌ها روی نسخه نصب‌شده قبلی نصب شوند، باید همیشه با همان Keystore امضا کنی.

---

## 💻 توسعه محلی (اختیاری)

اگر خواستی روی کامپیوتر خودت کد را تغییر بدهی:

1. **Android Studio** (نسخه Ladybug یا جدیدتر) را از [developer.android.com/studio](https://developer.android.com/studio) نصب کن.
2. File → Open → پوشه `KarNegar` را باز کن و صبر کن Gradle Sync تمام شود (نیاز به اینترنت دارد).
3. گوشی را با USB وصل کن (با فعال‌کردن USB Debugging) یا یک شبیه‌ساز بساز و دکمه **Run** ▶️ را بزن.

بیلد از خط فرمان (نیازمند JDK 17):

```bash
./gradlew assembleRelease    # APK امضاشده در app/build/outputs/apk/release/
./gradlew assembleDebug      # APK دیباگ در app/build/outputs/apk/debug/
```

---

## 🔔 مجوزهای برنامه و دلیل آن‌ها

| مجوز | دلیل |
|---|---|
| `POST_NOTIFICATIONS` | نمایش نوتیفیکیشن یادآور (اندروید ۱۳+) |
| `SCHEDULE_EXACT_ALARM` | دقیقاً سر وقت رینگ زدن یادآور (اندروید ۱۲+) |
| `RECEIVE_BOOT_COMPLETED` | دوباره فعال‌سازی یادآورها بعد از ری‌استارت گوشی |
| `VIBRATE` | لرزش همراه نوتیفیکیشن |

💡 اگر یادآورها سر وقت نمی‌آیند، وارد **تنظیمات برنامه → یادآورها** شو و دو مجوز «نوتیفیکیشن» و «هشدار دقیق» را فعال کن. همچنین بررسی کن که بهینه‌سازی باتری (Battery Optimization) برای کارنگار خاموش باشد.

---

## 🧯 عیب‌یابی

| مشکل | راه‌حل |
|---|---|
| بیلد در Actions قرمز شد | روی run قرمز بزن، مرحله‌ای که fail شده را باز کن؛ معمولاً مشکل اینترنت موقت گیت‌هاب است، دوباره با **Re-run all jobs** اجرا کن |
| فایل APK در Actions نیست | فایل ZIP آرتیفکت را دانلود و خارج کن؛ APK داخلش است |
| «App not installed» هنگام نصب آپدیت | اول نسخه قبلی را حذف کن، یا مطمئن شو با همان Keystore قبلی بیلد شده |
| نوتیفیکیشن یادآور نمی‌آید | مجوز نوتیفیکیشن + هشدار دقیق را از تنظیمات برنامه بده؛ بهینه‌سازی باتری را خاموش کن |
| آیکون در اندروید قدیمی نمایش داده نمی‌شود | حداقل نسخه پشتیبانی‌شده اندروید ۸ (API 26) است |
| تغییر اسم برنامه | در `app/src/main/res/values/strings.xml` مقدار `app_name` را عوض کن |

## 📄 لایسنس

این پروژه تحت لایسنس MIT منتشر شده است — آزادانه استفاده، تغییر و منتشرش کن.
