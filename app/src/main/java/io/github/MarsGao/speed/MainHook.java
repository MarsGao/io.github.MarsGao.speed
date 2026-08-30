package io.github.MarsGao.speed;


import android.app.Application;
import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.concurrent.ConcurrentHashMap;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;


public class MainHook implements IXposedHookLoadPackage {
    public final static String hookPackageBili0 = "tv.danmaku.bili";
    public final static String hookPackageBili1 = "com.bilibili.app.in";
    public final static String hookPackageTw = "com.twitter.android";
    public final static String hookPackageDy0 = "com.ss.android.ugc.aweme";
    public final static String hookPackageDy1 = "com.ss.android.ugc.aweme.lite";
    public final static String hookPackageDy2 = "com.ss.android.ugc.live";
    public final static String hookPackageDy3 = "com.ss.android.ugc.aweme.mobile";
    public final static String hookPackageRed = "com.xingin.xhs";
    public final static String hookPackageWb = "com.sina.weibo";
    public final static String hookPackageIg0 = "com.instagram.android";
    public final static String hookPackageIg1 = "com.instander.android";
    public final static String hookPackageTg = "org.telegram.messenger";
    public final static String hookPackageWx = "com.tencent.mm";
    // 使用新包名
    private final static XSharedPreferences prefs = new XSharedPreferences("io.github.MarsGao.speed", "speed");
    private static XC_MethodHook.Unhook first = null;
    private static XC_MethodHook.Unhook second = null;
    private static XC_MethodHook.Unhook third = null;
    private static Field twField = null;
    private static Method twMethod = null;

    private static float getSpeedConfig() {
        return SpeedConfigBridge.getSpeed(prefs, 1.5f);
    }

    private static float getWeChatSpeedConfig() {
        return SpeedConfigBridge.getSpeed(prefs, 1.5f);
    }
    private static boolean hasSpeedConfigChanged() {
        return SpeedConfigBridge.hasSpeedChanged(prefs);
    }

    private static void hookApplicationContext() {
        XposedHelpers.findAndHookMethod(Application.class, "attach", Context.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) {
                SpeedConfigBridge.setContext((Context) param.args[0]);
            }
        });
    }

    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam lpparam) {
        boolean bili = false;
        boolean twitter = false;
        boolean douyin = false;
        boolean red = false;
        boolean wb = false;
        boolean ig = false;
        boolean tg = false;
        boolean wx = false;

        if (hookPackageBili0.equals(lpparam.packageName) || hookPackageBili1.equals(lpparam.packageName)) {
            bili = true;
            if (!hookPackageBili0.equals(lpparam.processName) && !hookPackageBili1.equals(lpparam.processName))
                return;
        } else if (hookPackageTw.equals(lpparam.packageName)) {
            twitter = true;
            if (!hookPackageTw.equals(lpparam.processName)) return;
        } else if (hookPackageDy0.equals(lpparam.packageName) || hookPackageDy1.equals(lpparam.packageName) || hookPackageDy2.equals(lpparam.packageName) || hookPackageDy3.equals(lpparam.packageName)) {
            douyin = true;
            if (!hookPackageDy0.equals(lpparam.processName) && !hookPackageDy1.equals(lpparam.processName) && !hookPackageDy2.equals(lpparam.processName) && !hookPackageDy3.equals(lpparam.processName))
                return;
        } else if (hookPackageRed.equals(lpparam.packageName)) {
            red = true;
            if (!hookPackageRed.equals(lpparam.processName))
                return;
        } else if (hookPackageWb.equals(lpparam.packageName)) {
            wb = true;
            if (!hookPackageWb.equals(lpparam.processName))
                return;
        } else if (hookPackageIg0.equals(lpparam.packageName) || hookPackageIg1.equals(lpparam.packageName)) {
            ig = true;
            if (!hookPackageIg0.equals(lpparam.processName) && !hookPackageIg1.equals(lpparam.processName))
                return;
        } else if (hookPackageTg.equals(lpparam.packageName)) {
            tg = true;
            if (!hookPackageTg.equals(lpparam.processName))
                return;
        } else if (hookPackageWx.equals(lpparam.packageName)) {
            wx = true;
            if (!hookPackageWx.equals(lpparam.processName))
                return;
        }
        if (bili || twitter || douyin || red || wb || ig || tg || wx) {
            hookApplicationContext();
            if (twitter) {
                logTwitter("handleLoadPackage process=" + lpparam.processName);
                hookTwitterModernPlayers(lpparam);
                hookTwitterLegacy(lpparam);

            } else if (bili) {
                // bilibili configured to tv.danmaku.ijk.media.player.IjkMediaPlayer

                first = XposedHelpers.findAndHookMethod("tv.danmaku.ijk.media.player.AbstractMediaPlayer", lpparam.classLoader, "notifyOnInfo", int.class, int.class, Bundle.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws IllegalAccessException {
                        if (second == null) {
                            Field mOnPreparedListener = XposedHelpers.findField(param.thisObject.getClass(), "mOnPreparedListener");
                            Class<?> clz = mOnPreparedListener.get(param.thisObject).getClass();
                            XposedBridge.log("field found AbstractMediaPlayer->mOnPreparedListener");

                            second = XposedHelpers.findAndHookMethod(clz, "onPrepared", "tv.danmaku.ijk.media.player.IMediaPlayer", new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) throws IllegalAccessException {
                                    if (third == null ) {
                                        Field[] fields = param.thisObject.getClass().getDeclaredFields();
                                        XposedBridge.log("found fields t count: " + fields.length);
                                        Field t = fields[0];
                                        t.setAccessible(true);
                                        Object tObj = t.get(param.thisObject);
                                        Class<?> clz = tObj.getClass();
                                        XposedBridge.log("chosen fields t: " + clz);
                                        Class<?> OnPreparedListener = XposedHelpers.findClass("tv.danmaku.ijk.media.player.IMediaPlayer.OnPreparedListener", lpparam.classLoader);
                                        do {
                                            for (Field field : clz.getDeclaredFields()) {
                                                if (field.getType() == OnPreparedListener && !Modifier.isFinal(field.getModifiers())) {
                                                    field.setAccessible(true);
                                                    clz = field.get(tObj).getClass();
                                                    XposedBridge.log("chosen fields p: " + clz);

                                                    third = XposedHelpers.findAndHookMethod(clz, "onPrepared", "tv.danmaku.ijk.media.player.IMediaPlayer", new XC_MethodHook() {
                                                        @Override
                                                        protected void beforeHookedMethod(MethodHookParam param) throws IllegalAccessException {
                                                            Field[] fields = param.thisObject.getClass().getDeclaredFields();
                                                            XposedBridge.log("found fields s0 count: " + fields.length);

                                                            Field field = param.thisObject.getClass().getDeclaredFields()[0];
                                                            field.setAccessible(true);
                                                            Class<?> s0 = field.get(param.thisObject).getClass();
                                                            XposedBridge.log("chosen fields s0: " + s0);

                                                            for (Method method : s0.getDeclaredMethods()) {
                                                                if (void.class != method.getReturnType())
                                                                    continue;
                                                                if (1 != method.getParameterCount())
                                                                    continue;
                                                                if (float.class != method.getParameterTypes()[0])
                                                                    continue;

                                                                XposedBridge.log("chosen b: " + method);

                                                                float[] speedConfig = {getSpeedConfig()};

                                                                XposedBridge.hookMethod(method, new XC_MethodHook() {
                                                                    @Override
                                                                    protected void afterHookedMethod(MethodHookParam param) {
                                                                        if ((float) param.args[0] != speedConfig[0]) {
                                                                            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                                                                            for (int i = 4; i < stackTraceElements.length; i++) {
                                                                                // return on manually speed tweak
                                                                                if (stackTraceElements[i].getClassName().equals("com.bilibili.player.tangram.basic.PlaySpeedManagerImpl")) {
                                                                                    XposedBridge.log("bili manual speed: " + param.args[0]);
                                                                                    speedConfig[0] = (float) param.args[0];
                                                                                    return;
                                                                                }
                                                                            }
                                                                        }
                                                                    }
                                                                });
                                                                XposedBridge.log("bili hooked setSpeed");

                                                                XposedHelpers.findAndHookMethod(s0, "resume", new XC_MethodHook() {
                                                                    @Override
                                                                    protected void afterHookedMethod(MethodHookParam param) {
                                                                        Object thisObject = param.thisObject;
                                                                        if (hasSpeedConfigChanged()) {
                                                                            speedConfig[0] = getSpeedConfig();
                                                                        }
                                                                        try {
                                                                            method.invoke(thisObject, speedConfig[0]);
                                                                        } catch (
                                                                                IllegalAccessException e) {
                                                                            // should not happen
                                                                            XposedBridge.log(e);
                                                                            throw new IllegalAccessError(e.getMessage());
                                                                        } catch (
                                                                                InvocationTargetException e) {
                                                                            throw new RuntimeException(e);
                                                                        }
                                                                        XposedBridge.log("bili setSpeed: " + speedConfig[0]);
                                                                    }
                                                                });
                                                                XposedBridge.log("bili hooked resume");

                                                                first.unhook();
                                                                second.unhook();
                                                                third.unhook();

                                                                break;
                                                            }
                                                        }
                                                    });
                                                    XposedBridge.log("hooked p->onPrepared");
                                                    break;
                                                }
                                            }
                                        } while ((clz = clz.getSuperclass()) != null);
                                    }
                                }
                            });
                            XposedBridge.log("hooked mOnPreparedListener->onPrepared");
                        }
                    }
                });
                XposedBridge.log("hooked AbstractMediaPlayer->notifyOnInfo");
            } else if (douyin) {
                try {
                    float[] speedConfig = {getSpeedConfig()};
                    Boolean[] resumeHooked = {false};

                    XposedHelpers.findAndHookMethod("com.ss.android.ugc.aweme.video.simplayer.SimPlayer", lpparam.classLoader, "prepare", "com.ss.android.ugc.aweme.video.PlayRequest", new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            Object thisObject = param.thisObject;

                            if (!resumeHooked[0]) {
                                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                                for (int i = 4; i <= 7 && i < stackTraceElements.length; i++) {
                                    if (stackTraceElements[i].getMethodName().equals("prepare")) {
                                        Class<?> parentClass = XposedHelpers.findClass(stackTraceElements[i + 1].getClassName(), lpparam.classLoader);
                                        Object[] parameters = {"com.ss.android.ugc.aweme.feed.model.Video", "com.ss.android.ugc.aweme.player.sdk.api.OnUIPlayListener", int.class, String.class, boolean.class, String.class, boolean.class, boolean.class, int.class, int.class, boolean.class, boolean.class, Bundle.class};
                                        Class<?>[] parameter = (Class<?>[]) XposedHelpers.callStaticMethod(XposedHelpers.class, "getParameterClasses", lpparam.classLoader, parameters);
                                        Method[] methods = XposedHelpers.findMethodsByExactParameters(parentClass, String.class, parameter);
                                        if (methods.length == 1) {
                                            XC_MethodHook mh = new XC_MethodHook() {
                                                @Override
                                                protected void afterHookedMethod(MethodHookParam param) {
                                                    if (hasSpeedConfigChanged()) {
                                                        speedConfig[0] = getSpeedConfig();
                                                    }
                                                    XposedHelpers.callMethod(thisObject, "setSpeed", speedConfig[0]);
                                                    XposedBridge.log("douyin setSpeed1: " + speedConfig[0]);
                                                }
                                            };
                                            XposedBridge.hookMethod(methods[0], mh);
                                            XposedHelpers.findAndHookMethod("com.ss.android.ugc.aweme.video.simplayer.SimPlayer", lpparam.classLoader, "resume", mh);
                                            XposedBridge.log("hooked douyin resume");
                                        }
                                    }
                                }
                                resumeHooked[0] = true;
                            }

                            if (hasSpeedConfigChanged()) {
                                speedConfig[0] = getSpeedConfig();
                            }
                            XposedHelpers.callMethod(thisObject, "setSpeed", speedConfig[0]);
                            XposedBridge.log("douyin setSpeed0: " + speedConfig[0]);
                        }
                    });

                    XposedHelpers.findAndHookMethod("com.ss.android.ugc.aweme.video.simplayer.SimPlayer", lpparam.classLoader, "setSpeed", float.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            if ((float) param.args[0] != speedConfig[0]) {
                                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                                if (stackTraceElements.length <= 24) {
                                    // ignore old version set speed == 1
                                } else if (stackTraceElements.length <= 40) {
                                    for (int i = 4; i <= 7 && i < stackTraceElements.length; i++) {
                                        // manually speed tweak
                                        if (stackTraceElements[i].getMethodName().equals("setSpeed") &&
                                                (stackTraceElements[i+2].getMethodName().contains("onChanged") ||
                                                        stackTraceElements[i+3].getMethodName().contains("onChanged"))) {
                                            XposedBridge.log("douyin manual speed0: " + param.args[0]);
                                            speedConfig[0] = (float) param.args[0];
                                            return;
                                        }
                                    }
                                } else {
                                    for (int i = 16; i <= 24 && i < stackTraceElements.length; i++) {
                                        // manually speed tweak
                                        if (stackTraceElements[i].getMethodName().equals("dispatchTouchEvent")) {
                                            XposedBridge.log("douyin manual speed1: " + param.args[0]);
                                            speedConfig[0] = (float) param.args[0];
                                            return;
                                        }
                                    }
                                }
                                param.args[0] = speedConfig[0];
                                XposedBridge.log("douyin setSpeed2: " + speedConfig[0]);
                            }
                        }
                    });
                } catch (XposedHelpers.ClassNotFoundError cnfe) {
                    XposedBridge.log("douyin hook error0: " + cnfe);
                }

                try {
                    XposedHelpers.findAndHookMethod("com.ss.android.ugc.aweme.player.sdk.impl.TTPlayer", lpparam.classLoader, "setPlaySpeed", float.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            float speed = (float) param.args[0];
//                        XposedBridge.log("speed: " + speed);
                            if (speed == 1.0f) {
                                StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                                for (int i = 12; i <= 17 && i < stackTraceElements.length; i++) {
                                    // return on manually speed tweak
                                    if (stackTraceElements[i].getMethodName().equals("dispatchingValue")) {
//                                    XposedBridge.log("i: " + i);
                                        return;
                                    }
                                }
                                param.args[0] = getSpeedConfig();
                            }
                        }
                    });
                } catch (XposedHelpers.ClassNotFoundError cnfe) {
                    XposedBridge.log("douyin hook error1: " + cnfe);
                }

                XposedBridge.log("hooked douyin setSpeed");

            } else if (red) {
                float[] speedConfig = {getSpeedConfig()};

                XC_MethodHook hookInit = new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Object thisObject = param.thisObject;
//                        float currentSpeed = (float) XposedHelpers.callMethod(thisObject, "getSpeed", 0.0f);
                        if (hasSpeedConfigChanged()) {
                            speedConfig[0] = getSpeedConfig();
                        }
                        XposedHelpers.callMethod(thisObject, "setSpeed", speedConfig[0]);
                        XposedBridge.log("red setSpeed: " + speedConfig[0]);
                    }
                };

                XC_MethodHook hookSetSpeed = new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if ((float) param.args[0] != speedConfig[0]) {
                            XposedBridge.log(Log.getStackTraceString(new Throwable()));

                            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                            for (int i = 4; i < stackTraceElements.length; i++) {
                                // manual set
                                if (stackTraceElements[i].getMethodName().contains("Click")) {
                                    XposedBridge.log("red manual speed: " + param.args[0]);
                                    speedConfig[0] = (float) param.args[0];
                                    return;
                                }
                            }
                            param.args[0] = speedConfig[0];
                        }
                    }
                };

                try {
                    XposedHelpers.findAndHookMethod("tv.danmaku.ijk.media.player.IjkMediaPlayer", lpparam.classLoader, "initPlayer", "android.content.Context", "tv.danmaku.ijk.media.player.IjkLibLoader", hookInit);
                    XposedHelpers.findAndHookMethod("tv.danmaku.ijk.media.player.IjkMediaPlayer", lpparam.classLoader, "setSpeed", float.class, hookSetSpeed);
                    XposedBridge.log("hooked red IjkMediaPlayer");
                } catch (XposedHelpers.ClassNotFoundError e) {
                    XposedBridge.log("red IjkMediaPlayer not found");
                }

                try {
                    XposedHelpers.findAndHookMethod("com.xingin.redplayercore.RedPlayerCore", lpparam.classLoader, "initPlayer", Context.class, "com.xingin.redplayercore.RedLibLoader", hookInit);
                    XposedHelpers.findAndHookMethod("com.xingin.redplayercore.RedPlayerCore", lpparam.classLoader, "setSpeed", float.class, hookSetSpeed);
                    XposedBridge.log("hooked red RedPlayerCore");
                } catch (XposedHelpers.ClassNotFoundError e) {
                    XposedBridge.log("red RedPlayerCore not found");
                }

                first = XposedHelpers.findAndHookMethod("java.util.concurrent.ConcurrentHashMap", lpparam.classLoader, "get", Object.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
                        if ("followfeed_doubleline".equals(param.args[0])) {
                            ConcurrentHashMap hashMap = (ConcurrentHashMap) param.thisObject;
                            hashMap.compute("followfeed_doubleline", (k, v) -> {
                                if (v == null) {
                                    return v;
                                } else {
                                    try {
                                        Method method = XposedHelpers.findMethodExact(v.getClass(), "setValue", String.class);
                                        method.invoke(v, "0");
                                        XposedBridge.log("hooked red doubleline config");
                                        first.unhook();
                                        return v;
                                    } catch (InvocationTargetException e) {
                                        throw new RuntimeException(e);
                                    } catch (IllegalAccessException e) {
                                        throw new RuntimeException(e);
                                    }
                                }
                            });
                        }
                    }
                });

            } else if (wb) {
                float[] speedConfig = {getSpeedConfig()};

                XposedHelpers.findAndHookMethod("com.sina.weibo.mc.MagicCubePlayer", lpparam.classLoader, "onInfo", int.class, int.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
//                        XposedBridge.log("setSpeed arg0: " + (int)param.args[0] + " arg1: " + (int)param.args[1]);
                        if ((int)param.args[0] == 13 && (int)param.args[1] == 0) {
                            Object thisObject = param.thisObject;
                            if (hasSpeedConfigChanged()) {
                                speedConfig[0] = getSpeedConfig();
                            }
                            XposedHelpers.callMethod(thisObject, "setSpeed", speedConfig[0]);
                            XposedBridge.log("weibo setSpeed0: " + speedConfig[0]);
                        }
                    }
                });
                XposedHelpers.findAndHookMethod("com.sina.weibo.mc.MagicCubePlayer", lpparam.classLoader, "setSpeed", float.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if ((float) param.args[0] != speedConfig[0]) {
                            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                            for (int i = 4; i < stackTraceElements.length; i++) {
                                // manual set
                                if (stackTraceElements[i].getMethodName().contains("Click")) {
                                    XposedBridge.log("weibo manual speed0: " + param.args[0]);
                                    speedConfig[0] = (float) param.args[0];
                                    return;
                                }
                            }
                            param.args[0] = speedConfig[0];
                        }
                    }
                });

                XposedHelpers.findAndHookMethod("tv.danmaku.ijk.media.player.IjkMediaPlayer", lpparam.classLoader, "start", new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        Object thisObject = param.thisObject;
                        if (hasSpeedConfigChanged()) {
                            speedConfig[0] = getSpeedConfig();
                        }
                        XposedHelpers.callMethod(thisObject, "setSpeed", speedConfig[0]);
                        XposedBridge.log("weibo setSpeed1: " + speedConfig[0]);
                    }
                });

                XposedHelpers.findAndHookMethod("tv.danmaku.ijk.media.player.IjkMediaPlayer", lpparam.classLoader, "setSpeed", float.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        if ((float) param.args[0] != speedConfig[0]) {
                            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                            for (int i = 4; i < stackTraceElements.length; i++) {
                                // manual set
                                if (stackTraceElements[i].getMethodName().contains("Click")) {
                                    XposedBridge.log("weibo manual speed0: " + param.args[0]);
                                    speedConfig[0] = (float) param.args[0];
                                    return;
                                }
                            }
                            param.args[0] = speedConfig[0];
                        }
                    }
                });

                XposedBridge.log("hooked weibo");
            } else if (ig) {
                first = XposedHelpers.findAndHookMethod("com.facebook.breakpad.BreakpadManager", lpparam.classLoader,"setCustomData", String.class, String.class, Object[].class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        if ("last_video".equals(param.args[0])) {
                            StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                            for (int i = 2; i <= 7 && i < stackTraceElements.length; i++) {
                                if ("setCustomData".equals(stackTraceElements[i].getMethodName())) {
                                    XposedHelpers.findAndHookMethod(stackTraceElements[i + 1].getClassName(), lpparam.classLoader, "handleMessage", Message.class, new XC_MethodHook() {
                                        @Override
                                        protected void beforeHookedMethod(MethodHookParam param) {
                                            Message msg = (Message) param.args[0];
                                            if (msg.what == 6) {
                                                Message speedMsg = new Message();
                                                speedMsg.what = 27;
                                                speedMsg.obj = getSpeedConfig();
                                                XposedHelpers.callMethod(param.thisObject, "handleMessage", speedMsg);
                                            }
                                        }
                                    });

                                    XposedBridge.log("hooked ig handleMessage");
                                    first.unhook();
                                    return;
                                }
                            }
                        }
                    }
                });
                XposedBridge.log("hooked ig BreakpadManager");
            } else if (tg) {
                // Telegram Hook - 尝试多种方法签名以兼容不同版本
                boolean tgHooked = false;
                
                // 方案1: 尝试旧版签名
                try {
                    XposedHelpers.findAndHookMethod("org.telegram.ui.PhotoViewer", lpparam.classLoader, "preparePlayer", "android.net.Uri", boolean.class, boolean.class, "org.telegram.messenger.MediaController$SavedFilterState", new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            Object thisObject = param.thisObject;
                            XposedHelpers.setObjectField(thisObject, "currentVideoSpeed", getSpeedConfig());
                            XposedBridge.log("tg speed set (v1)");
                        }
                    });
                    tgHooked = true;
                    XposedBridge.log("hooked tg preparePlayer (v1)");
                } catch (Exception e1) {
                    XposedBridge.log("tg preparePlayer v1 failed: " + e1.getMessage());
                }
                
                // 方案2: 尝试新版签名（无SavedFilterState参数）
                if (!tgHooked) {
                    try {
                        XposedHelpers.findAndHookMethod("org.telegram.ui.PhotoViewer", lpparam.classLoader, "preparePlayer", "android.net.Uri", boolean.class, boolean.class, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) {
                                Object thisObject = param.thisObject;
                                XposedHelpers.setObjectField(thisObject, "currentVideoSpeed", getSpeedConfig());
                                XposedBridge.log("tg speed set (v2)");
                            }
                        });
                        tgHooked = true;
                        XposedBridge.log("hooked tg preparePlayer (v2)");
                    } catch (Exception e2) {
                        XposedBridge.log("tg preparePlayer v2 failed: " + e2.getMessage());
                    }
                }
                
                // 方案3: Hook setVideoSpeed方法作为备选
                try {
                    Class<?> photoViewerClass = XposedHelpers.findClass("org.telegram.ui.PhotoViewer", lpparam.classLoader);
                    for (Method method : photoViewerClass.getDeclaredMethods()) {
                        if (method.getName().equals("setVideoSpeed") || method.getName().equals("setPlaybackSpeed")) {
                            XposedBridge.hookMethod(method, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) {
                                    if (param.args.length > 0 && param.args[0] instanceof Float) {
                                        float speed = (float) param.args[0];
                                        if (Math.abs(speed - 1.0f) < 0.01f) {
                                            param.args[0] = getSpeedConfig();
                                            XposedBridge.log("tg speed intercepted: " + getSpeedConfig());
                                        }
                                    }
                                }
                            });
                            XposedBridge.log("hooked tg " + method.getName());
                        }
                    }
                } catch (Exception e3) {
                    XposedBridge.log("tg setVideoSpeed hook failed: " + e3.getMessage());
                }
                
                // 方案4: Hook VideoPlayer类
                try {
                    Class<?> videoPlayerClass = XposedHelpers.findClassIfExists("org.telegram.ui.Components.VideoPlayer", lpparam.classLoader);
                    if (videoPlayerClass != null) {
                        for (Method method : videoPlayerClass.getDeclaredMethods()) {
                            if (method.getName().equals("setPlaybackSpeed") && method.getParameterCount() == 1) {
                                XposedBridge.hookMethod(method, new XC_MethodHook() {
                                    @Override
                                    protected void beforeHookedMethod(MethodHookParam param) {
                                        if (param.args[0] instanceof Float) {
                                            float speed = (float) param.args[0];
                                            if (Math.abs(speed - 1.0f) < 0.01f) {
                                                param.args[0] = getSpeedConfig();
                                                XposedBridge.log("tg VideoPlayer speed set: " + getSpeedConfig());
                                            }
                                        }
                                    }
                                });
                                XposedBridge.log("hooked tg VideoPlayer.setPlaybackSpeed");
                            }
                        }
                    }
                } catch (Exception e4) {
                    XposedBridge.log("tg VideoPlayer hook failed: " + e4.getMessage());
                }
                
                XposedBridge.log("hooked tg setPlaybackSpeed");
            } else if (wx) {
                logWeChatHook("Starting WeChat hook with multi-strategy approach");

                // 视频号 feed 流：进入/滑动后主动对当前播放器实例注入目标倍速。
                hookWeChatFinderFeedSpeedInjector(lpparam);
                hookWeChatFinderSpeedMenuClick(lpparam);

                hookWeChatClassLoaderProbe(lpparam);

                // 优先级1: 通用播放器Hook（最可能成功）
                hookWeChatPlayers(lpparam);

                // 优先级2: Hook所有可能的播放速度设置方法
                hookWeChatSpeedMethods(lpparam);

                // 优先级3: 原有精确Hook（作为最后手段）
                tryOriginalWeChatHook(lpparam);

                logWeChatHook("WeChat hook initialization completed");
            }
        }
    }

    private static final ThreadLocal<Boolean> twitterApplyingSpeed = new ThreadLocal<>();

    private static void hookTwitterModernPlayers(XC_LoadPackage.LoadPackageParam lpparam) {
        String[] playerClasses = {
            "androidx.media3.exoplayer.ExoPlayerImpl",
            "androidx.media3.exoplayer.SimpleExoPlayer",
            // X 12.7.1 bundles Media3 1.9.3 with R8-obfuscated implementation names.
            "androidx.media3.exoplayer.i1",
            "com.google.android.exoplayer2.ExoPlayerImpl",
            "com.google.android.exoplayer2.SimpleExoPlayer"
        };

        int hookedCount = 0;
        for (String className : playerClasses) {
            try {
                Class<?> playerClass = XposedHelpers.findClassIfExists(className, lpparam.classLoader);
                if (playerClass == null) {
                    continue;
                }
                hookedCount += hookTwitterPlayerClass(playerClass, lpparam.classLoader);
            } catch (Exception e) {
                logTwitter("modern hook failed for " + className + ": " + e.getMessage());
            }
        }

        if (hookedCount > 0) {
            logTwitter("modern hook installed");
        } else {
            logTwitter("no known player class found");
        }
    }

    private static int hookTwitterPlayerClass(Class<?> playerClass, ClassLoader classLoader) {
        int hookedCount = 0;
        boolean xMedia3_12_7 = "androidx.media3.exoplayer.i1".equals(playerClass.getName());
        for (Method method : playerClass.getMethods()) {
            try {
                String name = method.getName();
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (("setPlaybackSpeed".equals(name) || (xMedia3_12_7 && "o".equals(name))) && parameterTypes.length == 1 && parameterTypes[0] == float.class) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            handleTwitterSetPlaybackSpeed(param);
                        }
                    });
                    hookedCount++;
                    logTwitter("hooked " + playerClass.getName() + "." + name + " as setPlaybackSpeed");
                } else if (("setPlaybackParameters".equals(name) || (xMedia3_12_7 && "l".equals(name) && "androidx.media3.common.j0".equals(parameterTypes[0].getName()))) && parameterTypes.length == 1) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            handleTwitterSetPlaybackParameters(param, parameterTypes[0]);
                        }
                    });
                    hookedCount++;
                    logTwitter("hooked " + playerClass.getName() + "." + name + " as setPlaybackParameters");
                } else if (xMedia3_12_7 && "j".equals(name) && parameterTypes.length == 0) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            applyTwitterObfuscatedMedia3Speed(param.thisObject);
                        }
                    });
                    hookedCount++;
                    logTwitter("hooked " + playerClass.getName() + ".j as prepare");
                } else if (isTwitterPlayerApplyPoint(name, parameterTypes)) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            if ("setPlayWhenReady".equals(name) && param.args.length == 1 && param.args[0] instanceof Boolean && !((Boolean)param.args[0])) {
                                return;
                            }
                            applyTwitterSpeed(param.thisObject, classLoader, "after " + name);
                        }
                    });
                    hookedCount++;
                    logTwitter("hooked " + playerClass.getName() + "." + name);
                }
            } catch (Throwable t) {
                logTwitter("method hook skipped " + playerClass.getName() + "." + method.getName() + ": " + t.getMessage());
            }
        }
        return hookedCount;
    }

    private static boolean isTwitterPlayerApplyPoint(String name, Class<?>[] parameterTypes) {
        if ("prepare".equals(name) && parameterTypes.length == 0) {
            return true;
        }
        if ("play".equals(name) && parameterTypes.length == 0) {
            return true;
        }
        if ("setPlayWhenReady".equals(name) && parameterTypes.length == 1 && parameterTypes[0] == boolean.class) {
            return true;
        }
        return (name.equals("setMediaItem") || name.equals("setMediaItems")) && parameterTypes.length >= 1;
    }

    private static void handleTwitterSetPlaybackSpeed(XC_MethodHook.MethodHookParam param) {
        if (Boolean.TRUE.equals(twitterApplyingSpeed.get()) || !(param.args[0] instanceof Float)) {
            return;
        }

        float requestedSpeed = (float)param.args[0];
        float targetSpeed = getSpeedConfig();
        if (Math.abs(targetSpeed - 1.0f) < 0.01f) {
            return;
        }

        if (Math.abs(requestedSpeed - 1.0f) < 0.01f) {
            param.args[0] = targetSpeed;
            logTwitter("setPlaybackSpeed 1.0 -> " + targetSpeed);
        } else if (Math.abs(requestedSpeed - targetSpeed) >= 0.01f) {
            logTwitter("manual speed detected: " + requestedSpeed);
        }
    }

    private static void handleTwitterSetPlaybackParameters(XC_MethodHook.MethodHookParam param, Class<?> playbackParametersClass) {
        if (Boolean.TRUE.equals(twitterApplyingSpeed.get()) || param.args[0] == null) {
            return;
        }

        float targetSpeed = getSpeedConfig();
        if (Math.abs(targetSpeed - 1.0f) < 0.01f) {
            return;
        }

        Float requestedSpeed = getPlaybackParametersSpeed(param.args[0]);
        if (requestedSpeed == null) {
            return;
        }

        if (Math.abs(requestedSpeed - 1.0f) < 0.01f) {
            Object newParameters = newTwitterPlaybackParameters(playbackParametersClass, param.args[0], targetSpeed);
            if (newParameters != null) {
                param.args[0] = newParameters;
                logTwitter("setPlaybackSpeed 1.0 -> " + targetSpeed);
            }
        } else if (Math.abs(requestedSpeed - targetSpeed) >= 0.01f) {
            logTwitter("manual speed detected: " + requestedSpeed);
        }
    }

    private static Float getPlaybackParametersSpeed(Object playbackParameters) {
        try {
            Object speed = XposedHelpers.getObjectField(playbackParameters, "speed");
            if (speed instanceof Float) {
                return (Float)speed;
            }
        } catch (Throwable ignored) {
        }
        // X 12.7.1's bundled Media3 1.9.3 R8-renames PlaybackParameters to
        // androidx.media3.common.j0 and its speed field to "a". Keep this
        // structural fallback limited to a float field so normal Media3 stays
        // on the stable public-field path above.
        try {
            for (Field field : playbackParameters.getClass().getDeclaredFields()) {
                if (field.getType() == float.class) {
                    field.setAccessible(true);
                    return field.getFloat(playbackParameters);
                }
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static void applyTwitterObfuscatedMedia3Speed(Object player) {
        float targetSpeed = getSpeedConfig();
        if (Math.abs(targetSpeed - 1.0f) < 0.01f) {
            return;
        }
        twitterApplyingSpeed.set(true);
        try {
            XposedHelpers.callMethod(player, "o", targetSpeed);
            logTwitter("Media3 1.9.3 prepare -> " + targetSpeed);
        } catch (Throwable e) {
            logTwitter("Media3 1.9.3 prepare apply failed: " + e.getMessage());
        } finally {
            twitterApplyingSpeed.remove();
        }
    }

    private static Object newTwitterPlaybackParameters(Class<?> playbackParametersClass, Object oldParameters, float targetSpeed) {
        try {
            return XposedHelpers.newInstance(playbackParametersClass, targetSpeed);
        } catch (Throwable ignored) {
        }

        float pitch = 1.0f;
        try {
            Object oldPitch = XposedHelpers.getObjectField(oldParameters, "pitch");
            if (oldPitch instanceof Float) {
                pitch = (Float)oldPitch;
            }
        } catch (Throwable ignored) {
        }

        try {
            return XposedHelpers.newInstance(playbackParametersClass, targetSpeed, pitch);
        } catch (Throwable e) {
            logTwitter("failed to create PlaybackParameters: " + e.getMessage());
            return null;
        }
    }

    private static void applyTwitterSpeed(Object player, ClassLoader classLoader, String source) {
        float targetSpeed = getSpeedConfig();
        if (Math.abs(targetSpeed - 1.0f) < 0.01f) {
            return;
        }

        twitterApplyingSpeed.set(true);
        try {
            try {
                XposedHelpers.callMethod(player, "setPlaybackSpeed", targetSpeed);
                logTwitter(source + " applyTwitterSpeed: " + targetSpeed);
                return;
            } catch (Throwable ignored) {
            }

            Object playbackParameters = createTwitterPlaybackParameters(classLoader, targetSpeed);
            if (playbackParameters != null) {
                XposedHelpers.callMethod(player, "setPlaybackParameters", playbackParameters);
                logTwitter(source + " applyTwitterSpeed: " + targetSpeed);
            }
        } catch (Throwable e) {
            logTwitter(source + " apply failed: " + e.getMessage());
        } finally {
            twitterApplyingSpeed.remove();
        }
    }

    private static Object createTwitterPlaybackParameters(ClassLoader classLoader, float targetSpeed) {
        String[] parameterClasses = {
            "androidx.media3.common.PlaybackParameters",
            "com.google.android.exoplayer2.PlaybackParameters"
        };

        for (String className : parameterClasses) {
            try {
                Class<?> playbackParametersClass = XposedHelpers.findClassIfExists(className, classLoader);
                if (playbackParametersClass == null) {
                    continue;
                }
                Object parameters = newTwitterPlaybackParameters(playbackParametersClass, null, targetSpeed);
                if (parameters != null) {
                    return parameters;
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static void hookTwitterLegacy(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            first = XposedHelpers.findAndHookMethod(Resources.class, "getConfiguration", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    try {
                        StackTraceElement[] stackTraceElements = Thread.currentThread().getStackTrace();
                        if (stackTraceElements.length >= 14 && stackTraceElements.length < 21) {
                            if ("android.os.HandlerThread".equals(stackTraceElements[stackTraceElements.length - 1].getClassName()) && "run".equals(stackTraceElements[stackTraceElements.length - 1].getMethodName())) {
                                for (int i = 0; i < stackTraceElements.length; i++) {
                                    if ("getConfiguration".equals(stackTraceElements[i].getMethodName())) {
                                        if (stackTraceElements[i + 1].getClassName().equals(stackTraceElements[i + 2].getClassName()) && "onNext".equals(stackTraceElements[i + 4].getMethodName())) {
                                            String className = stackTraceElements[i + 1].getClassName();
                                            String methodName = stackTraceElements[i + 1].getMethodName();
                                            Class<?> clz = XposedHelpers.findClass(stackTraceElements[i + 1].getClassName(), lpparam.classLoader);
                                            for (Method m : clz.getDeclaredMethods()) {
                                                if (methodName.equals(m.getName())) {
                                                    XposedBridge.hookMethod(m, new XC_MethodHook() {
                                                        @Override
                                                        protected void afterHookedMethod(MethodHookParam param) throws IllegalAccessException, InvocationTargetException {
                                                            if (twField == null) {
                                                                for (Field f : param.thisObject.getClass().getDeclaredFields()) {
                                                                    if (Modifier.isVolatile(f.getModifiers())) {
                                                                        twField = f;
                                                                        XposedBridge.log("twField: " + f);
                                                                        break;
                                                                    }
                                                                }
                                                            }
                                                            if (twField == null) {
                                                                logTwitter("legacy volatile field not found");
                                                                return;
                                                            }
                                                            Object c = twField.get(param.thisObject);
                                                            if (twMethod == null) {
                                                                Method[] methods = XposedHelpers.findMethodsByExactParameters(c.getClass(), void.class, double.class);
                                                                if (methods.length == 0) {
                                                                    logTwitter("legacy speed method not found");
                                                                    return;
                                                                }
                                                                twMethod = methods[0];
                                                                XposedBridge.log("twMethod: " + twMethod);
                                                            }
                                                            float targetSpeed = getSpeedConfig();
                                                            twMethod.invoke(c, targetSpeed);
                                                            logTwitter("legacy applyTwitterSpeed: " + targetSpeed);
                                                        }
                                                    });

                                                    first.unhook();
                                                    XposedBridge.log("hooked " + className + "->" + methodName);

                                                    // we just hook it, thus we need re-enter the method
                                                    param.setThrowable(new Resources.NotFoundException());
                                                    break;
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    } catch (Throwable e) {
                        logTwitter("legacy hook callback failed: " + e.getMessage());
                    }
                }
            });
        } catch (Throwable e) {
            logTwitter("legacy hook install failed: " + e.getMessage());
        }
    }

    private static void logTwitter(String message) {
        XposedBridge.log("[VideoSpeed][Twitter] " + message);
    }

    // ===== 微信Hook改进方案 =====

    // 用于存储当前速度设置，避免重复调用
    private static float wxCurrentSpeed = 1.0f;
    private static boolean wxSpeedInitialized = false;
    private static final int WX_DISCOVERY_LOG_LIMIT = 120;
    private static final java.util.Map<String, Boolean> wxDiscoveredClassNames = new ConcurrentHashMap<>();
    private static final java.util.Map<String, Boolean> wxHookedClassNames = new ConcurrentHashMap<>();
    private static final java.util.Map<String, Integer> wxResIdCache = new ConcurrentHashMap<>();
    private static volatile long wxLastManualSpeedChangeAt = 0L;
    private static final ThreadLocal<Boolean> wxApplyingSpeed = new ThreadLocal<>();

    private static int getWeChatResId(Context context, String name, String defType, int fallbackId) {
        if (context == null) {
            return fallbackId;
        }
        String key = defType + "/" + name;
        Integer cached = wxResIdCache.get(key);
        if (cached != null) {
            return cached;
        }
        try {
            int id = context.getResources().getIdentifier(name, defType, "com.tencent.mm");
            if (id != 0) {
                wxResIdCache.put(key, id);
                logWeChatHook("[ResId] resolved " + key + " = 0x" + Integer.toHexString(id) + " (" + id + ")");
                return id;
            }
        } catch (Throwable t) {
            logWeChatHook("[ResId] getIdentifier failed for " + key + ": " + t.getMessage());
        }
        return fallbackId;
    }

    private static void hookWeChatFinderFeedSpeedInjector(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod(android.app.Activity.class, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    if (isWeChatFinderActivity(param.thisObject)) {
                        scheduleApplyWeChatFinderCurrentSpeed((android.app.Activity)param.thisObject, "FinderActivity.onResume", 350);
                        scheduleApplyWeChatFinderCurrentSpeed((android.app.Activity)param.thisObject, "FinderActivity.onResume", 1200);
                    }
                }
            });
            XposedHelpers.findAndHookMethod(android.app.Activity.class, "dispatchTouchEvent", android.view.MotionEvent.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    if (isWeChatFinderActivity(param.thisObject) && param.args[0] instanceof android.view.MotionEvent) {
                        android.view.MotionEvent event = (android.view.MotionEvent)param.args[0];
                        if (event.getActionMasked() == android.view.MotionEvent.ACTION_UP) {
                            scheduleApplyWeChatFinderCurrentSpeed((android.app.Activity)param.thisObject, "FinderActivity.touchUp", 450);
                        }
                    }
                }
            });
            logWeChatHook("[FinderInject] Activity resume/touch hooks installed");
        } catch (Throwable e) {
            logWeChatHook("[FinderInject] Activity hooks failed: " + e.getMessage());
        }
    }

    private static void hookWeChatFinderSpeedMenuClick(XC_LoadPackage.LoadPackageParam lpparam) {
        String[] menuClasses = {
            "com.tencent.mm.plugin.finder.viewmodel.component.q40",
            "com.tencent.mm.plugin.finder.viewmodel.component.FinderSpeedControlUIC",
            "com.tencent.mm.plugin.finder.viewmodel.component.y00"
        };
        for (String cname : menuClasses) {
            try {
                Class<?> clazz = XposedHelpers.findClassIfExists(cname, lpparam.classLoader);
                if (clazz != null) {
                    for (Method m : clazz.getDeclaredMethods()) {
                        if ("onClick".equals(m.getName()) && m.getParameterCount() == 1 && android.view.View.class.isAssignableFrom(m.getParameterTypes()[0])) {
                            XposedBridge.hookMethod(m, new XC_MethodHook() {
                                @Override
                                protected void beforeHookedMethod(MethodHookParam param) {
                                    try {
                                        Object tag = ((android.view.View)param.args[0]).getTag();
                                        if (tag instanceof Float) {
                                            wxLastManualSpeedChangeAt = System.currentTimeMillis();
                                            logWeChatHook("[FinderInject] manual speed menu click: " + tag);
                                        }
                                    } catch (Throwable e) {
                                        logWeChatHook("[FinderInject] manual speed click inspect failed: " + e.getMessage());
                                    }
                                }
                            });
                            logWeChatHook("[FinderInject] " + cname + "." + m.getName() + " hook installed");
                        }
                    }
                }
            } catch (Throwable e) {
                logWeChatHook("[FinderInject] " + cname + " hook failed: " + e.getMessage());
            }
        }
    }

    private static void hookWeChatClassLoaderProbe(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    handleWeChatLoadedClass(param, lpparam.classLoader);
                }
            });
            XposedHelpers.findAndHookMethod(ClassLoader.class, "loadClass", String.class, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) {
                    handleWeChatLoadedClass(param, lpparam.classLoader);
                }
            });
            logWeChatHook("ClassLoader.loadClass probe installed");
        } catch (Throwable e) {
            logWeChatHook("ClassLoader.loadClass probe failed: " + e.getMessage());
        }
    }

    private static void handleWeChatLoadedClass(XC_MethodHook.MethodHookParam param, ClassLoader appClassLoader) {
        try {
            if (param.hasThrowable() || !(param.args[0] instanceof String) || !(param.getResult() instanceof Class<?>)) {
                return;
            }
            String className = (String)param.args[0];
            Class<?> clazz = (Class<?>)param.getResult();

            if (!isWeChatInterestingClassName(className)) {
                return;
            }
            if (wxDiscoveredClassNames.size() < WX_DISCOVERY_LOG_LIMIT && wxDiscoveredClassNames.put(className, Boolean.TRUE) == null) {
                logWeChatHook("[Discover] loadClass candidate: " + className);
            }
            scanAndHookWeChatCandidateClass(clazz, appClassLoader, "loadClass");
        } catch (Throwable e) {
            logWeChatHook("[Discover] loadClass probe error: " + e.getMessage());
        }
    }

    /**
     * 方案1: 通用播放器Hook - 动态查找所有播放器相关类
     */
    private static void hookWeChatPlayers(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            logWeChatHook("Starting universal player hook...");

            // 通过反射获取所有类（有限的方法）
            Class<?>[] candidateClasses = getWeChatPlayerClasses(lpparam.classLoader);

            int hookedCount = 0;
            for (Class<?> clazz : candidateClasses) {
                try {
                    hookedCount += scanAndHookWeChatCandidateClass(clazz, lpparam.classLoader, "known");
                } catch (Exception e) {
                    // 忽略单个类的异常，继续下一个
                }
            }
            logWeChatHook("Universal hook completed, hooked " + hookedCount + " methods");
        } catch (Exception e) {
            logWeChatHook("Error in universal hook: " + e.getMessage());
        }

        // 方案1.5: Hook腾讯视频SDK (liteav)
        hookTencentLiteAV(lpparam);
    }

    private static int scanAndHookWeChatCandidateClass(Class<?> clazz, ClassLoader classLoader, String source) {
        if (clazz == null || clazz.getName() == null || !isWeChatInterestingClassName(clazz.getName())) {
            return 0;
        }
        if (wxHookedClassNames.put(clazz.getName(), Boolean.TRUE) != null) {
            return 0;
        }

        int hookedCount = 0;
        for (Method method : clazz.getDeclaredMethods()) {
            try {
                Class<?>[] parameterTypes = method.getParameterTypes();
                String methodName = method.getName();
                if (parameterTypes.length == 1 && parameterTypes[0] == float.class && isWeChatSpeedMethodName(methodName, clazz.getName())) {
                    method.setAccessible(true);
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            handleWeChatSpeedChange(param, source + "-" + clazz.getName() + "." + methodName);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            applyWeChatTargetSpeed(param.thisObject, classLoader, "after " + methodName);
                        }
                    });
                    hookedCount++;
                    logWeChatHook("[Discover] hooked float speed method: " + clazz.getName() + "." + methodName);
                } else if (isWeChatPlaybackStartMethod(methodName, parameterTypes)) {
                    method.setAccessible(true);
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            applyWeChatTargetSpeed(param.thisObject, classLoader, "after " + methodName);
                        }
                    });
                    hookedCount++;
                    logWeChatHook("[Discover] hooked playback point: " + clazz.getName() + "." + methodName);
                }
            } catch (Throwable e) {
                logWeChatHook("[Discover] skipped " + clazz.getName() + "." + method.getName() + ": " + e.getMessage());
            }
        }

        if (hookedCount > 0) {
            logWeChatHook("[Discover] candidate scan hooked " + hookedCount + " methods in " + clazz.getName());
        }
        return hookedCount;
    }

    private static boolean isWeChatInterestingClassName(String className) {
        if (className == null) {
            return false;
        }
        String lower = className.toLowerCase();
        if (!(lower.startsWith("com.tencent.mm") || lower.startsWith("com.tencent.liteav") ||
                lower.startsWith("com.tencent.rtmp") || lower.startsWith("com.google.android.exoplayer") ||
                lower.startsWith("androidx.media3"))) {
            return false;
        }
        return lower.contains("finder") || lower.contains("video") || lower.contains("player") ||
                lower.contains("liteav") || lower.contains("thumb") || lower.contains("play");
    }

    private static boolean isWeChatSpeedMethodName(String methodName, String className) {
        String lowerName = methodName.toLowerCase();
        if (lowerName.contains("speed") || lowerName.contains("rate") || lowerName.contains("playback")) {
            return true;
        }
        return lowerName.equals("h6") || lowerName.equals("tpsetplayspeed");
    }

    private static boolean isWeChatPlaybackStartMethod(String methodName, Class<?>[] parameterTypes) {
        String lowerName = methodName.toLowerCase();
        if (parameterTypes.length == 0 && (lowerName.equals("start") || lowerName.equals("play") ||
                lowerName.equals("resume") || lowerName.equals("prepare"))) {
            return true;
        }
        return lowerName.equals("startvodplay") || lowerName.equals("startplay") || lowerName.equals("startliveplay");
    }

    private static void applyWeChatTargetSpeed(Object player, ClassLoader classLoader, String source) {
        float targetSpeed = getWeChatSpeedConfig();
        if (player == null || Math.abs(targetSpeed - 1.0f) < 0.01f || Boolean.TRUE.equals(wxApplyingSpeed.get())) {
            return;
        }

        wxApplyingSpeed.set(true);
        try {
            String[] directMethods = {
                "setRate",
                "setSpeed",
                "setPlaySpeed",
                "setPlaybackSpeed",
                "setPlaybackRate",
                "setPlaySpeedRatio",
                "setVideoSpeedRatio",
                "tpSetPlaySpeed"
            };
            for (String methodName : directMethods) {
                try {
                    XposedHelpers.callMethod(player, methodName, targetSpeed);
                    lastSpeedMap.put(player, targetSpeed);
                    logWeChatHook(source + " setRate target via " + methodName + ": " + targetSpeed);
                    return;
                } catch (Throwable ignored) {
                }
            }

            Object playbackParameters = createWeChatPlaybackParameters(classLoader, targetSpeed);
            if (playbackParameters != null) {
                try {
                    XposedHelpers.callMethod(player, "setPlaybackParameters", playbackParameters);
                    lastSpeedMap.put(player, targetSpeed);
                    logWeChatHook(source + " setPlaybackParameters target: " + targetSpeed);
                } catch (Throwable e) {
                    logWeChatHook(source + " setPlaybackParameters failed: " + e.getMessage());
                }
            }
        } finally {
            wxApplyingSpeed.remove();
        }
    }

    private static boolean isWeChatFinderActivity(Object object) {
        return object instanceof android.app.Activity &&
                object.getClass().getName().startsWith("com.tencent.mm.plugin.finder.") &&
                object.getClass().getName().contains("FinderHome");
    }

    private static void scheduleApplyWeChatFinderCurrentSpeed(android.app.Activity activity, String source, long delayMs) {
        new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
            try {
                applyWeChatFinderCurrentSpeed(activity, source);
            } catch (Throwable e) {
                logWeChatHook("[FinderInject] " + source + " failed: " + e.getMessage());
            }
        }, delayMs);
    }

    private static void applyWeChatFinderCurrentSpeed(android.app.Activity activity, String source) {
        float targetSpeed = getWeChatSpeedConfig();
        if (Math.abs(targetSpeed - 1.0f) < 0.01f) {
            return;
        }
        long manualAge = System.currentTimeMillis() - wxLastManualSpeedChangeAt;
        if (manualAge >= 0 && manualAge < MANUAL_CHANGE_COOLDOWN) {
            logWeChatHook("[FinderInject] " + source + " skipped in manual cooldown");
            return;
        }
        android.view.ViewGroup recyclerView = findWeChatFinderRecyclerView(activity);
        if (recyclerView == null) {
            logWeChatHook("[FinderInject] " + source + " recycler not found");
            return;
        }
        Object holder = findWeChatFinderCurrentHolder(recyclerView);
        if (holder == null) {
            logWeChatHook("[FinderInject] " + source + " holder not found");
            return;
        }
        Object videoView = findWeChatFinderVideoView(holder, activity);
        if (videoView == null) {
            logWeChatHook("[FinderInject] " + source + " video view not found");
            return;
        }
        applyWeChatTargetSpeed(videoView, activity.getClassLoader(), source + " current " + videoView.getClass().getName());
    }

    private static android.view.ViewGroup findWeChatFinderRecyclerView(android.app.Activity activity) {
        int m6eId = getWeChatResId(activity, "m6e", "id", 2131318338);
        if (m6eId != 0) {
            android.view.View recycler = activity.findViewById(m6eId);
            if (recycler != null) {
                try {
                    Object inner = XposedHelpers.callMethod(recycler, "getRecyclerView");
                    if (inner instanceof android.view.ViewGroup) {
                        return (android.view.ViewGroup) inner;
                    }
                } catch (Throwable ignored) {
                }
                if (recycler instanceof android.view.ViewGroup) {
                    return (android.view.ViewGroup) recycler;
                }
            }
        }
        try {
            android.view.View decor = activity.getWindow().getDecorView();
            android.view.ViewGroup found = findRecyclerViewInViewTree(decor);
            if (found != null) {
                return found;
            }
        } catch (Throwable ignored) {
        }
        return null;
    }

    private static android.view.ViewGroup findRecyclerViewInViewTree(android.view.View root) {
        if (root == null) return null;
        String cname = root.getClass().getName();
        if (cname.contains("RecyclerView") || cname.contains("RefreshLoadMoreLayout")) {
            try {
                Object inner = XposedHelpers.callMethod(root, "getRecyclerView");
                if (inner instanceof android.view.ViewGroup) {
                    return (android.view.ViewGroup) inner;
                }
            } catch (Throwable ignored) {
            }
            if (root instanceof android.view.ViewGroup) {
                return (android.view.ViewGroup) root;
            }
        }
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                android.view.ViewGroup res = findRecyclerViewInViewTree(group.getChildAt(i));
                if (res != null) return res;
            }
        }
        return null;
    }

    private static Object findWeChatFinderCurrentHolder(android.view.ViewGroup recyclerView) {
        int position = -1;
        try {
            Object layoutManager = XposedHelpers.callMethod(recyclerView, "getLayoutManager");
            if (layoutManager != null) {
                try {
                    Object result = XposedHelpers.callMethod(layoutManager, "w");
                    if (result instanceof Integer) {
                        position = (Integer)result;
                    }
                } catch (Throwable ignored) {
                }
                if (position < 0) {
                    try {
                        Object result = XposedHelpers.callMethod(layoutManager, "findFirstVisibleItemPosition");
                        if (result instanceof Integer) {
                            position = (Integer)result;
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        } catch (Throwable ignored) {
        }
        if (position >= 0) {
            try {
                Object holder = XposedHelpers.callMethod(recyclerView, "q0", position, false);
                if (holder != null) {
                    return holder;
                }
            } catch (Throwable ignored) {
            }
            try {
                Object holder = XposedHelpers.callMethod(recyclerView, "findViewHolderForAdapterPosition", position);
                if (holder != null) {
                    return holder;
                }
            } catch (Throwable ignored) {
            }
        }
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            try {
                Object holder = XposedHelpers.callMethod(recyclerView, "getChildViewHolder", recyclerView.getChildAt(i));
                if (holder != null) {
                    return holder;
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    private static Object findWeChatFinderVideoView(Object holder, android.app.Activity activity) {
        if (holder == null) return null;
        int videoLayoutId = getWeChatResId(activity, "e_k", "id", 2131304752);

        if (videoLayoutId != 0) {
            String[] holderFindMethods = {"o", "n", "findView", "findViewById"};
            for (String m : holderFindMethods) {
                try {
                    Object videoLayout = XposedHelpers.callMethod(holder, m, videoLayoutId);
                    if (videoLayout != null) {
                        Object videoView = XposedHelpers.callMethod(videoLayout, "getVideoView");
                        if (videoView != null) {
                            return videoView;
                        }
                    }
                } catch (Throwable ignored) {
                }
            }
        }

        try {
            Field itemViewField = XposedHelpers.findFieldIfExists(holder.getClass(), "itemView");
            if (itemViewField != null) {
                Object itemView = itemViewField.get(holder);
                if (itemView instanceof android.view.View) {
                    android.view.View v = (android.view.View) itemView;
                    if (videoLayoutId != 0) {
                        android.view.View layoutView = v.findViewById(videoLayoutId);
                        if (layoutView != null) {
                            try {
                                Object videoView = XposedHelpers.callMethod(layoutView, "getVideoView");
                                if (videoView != null) return videoView;
                            } catch (Throwable ignored) {
                            }
                        }
                    }
                    Object videoView = findVideoViewInTree(v);
                    if (videoView != null) return videoView;
                }
            }
        } catch (Throwable ignored) {
        }

        return null;
    }

    private static Object findVideoViewInTree(android.view.View root) {
        if (root == null) return null;
        String cname = root.getClass().getName();
        if (cname.contains("FinderThumbPlayerProxy")) {
            return root;
        }
        if (cname.contains("FinderVideoLayout")) {
            try {
                Object videoView = XposedHelpers.callMethod(root, "getVideoView");
                if (videoView != null) return videoView;
            } catch (Throwable ignored) {
            }
        }
        if (root instanceof android.view.ViewGroup) {
            android.view.ViewGroup group = (android.view.ViewGroup) root;
            for (int i = 0; i < group.getChildCount(); i++) {
                Object found = findVideoViewInTree(group.getChildAt(i));
                if (found != null) return found;
            }
        }
        return null;
    }

    private static Object createWeChatPlaybackParameters(ClassLoader classLoader, float targetSpeed) {
        String[] parameterClasses = {
            "androidx.media3.common.PlaybackParameters",
            "com.google.android.exoplayer2.PlaybackParameters"
        };

        for (String className : parameterClasses) {
            try {
                Class<?> playbackParametersClass = XposedHelpers.findClassIfExists(className, classLoader);
                if (playbackParametersClass == null) {
                    continue;
                }
                try {
                    return XposedHelpers.newInstance(playbackParametersClass, targetSpeed);
                } catch (Throwable ignored) {
                }
                try {
                    return XposedHelpers.newInstance(playbackParametersClass, targetSpeed, 1.0f);
                } catch (Throwable e) {
                    logWeChatHook("Failed to create " + className + ": " + e.getMessage());
                }
            } catch (Throwable ignored) {
            }
        }
        return null;
    }

    /**
     * Hook腾讯视频SDK (liteav) - 这是微信视频号底层使用的播放器
     */
    private static void hookTencentLiteAV(XC_LoadPackage.LoadPackageParam lpparam) {
        logWeChatHook("Starting TencentLiteAV hook...");

        // TXVodPlayer - 点播播放器
        String[] vodPlayerClasses = {
            "com.tencent.liteav.txcplayer.TXCVodPlayer",
            "com.tencent.rtmp.TXVodPlayer",
            "com.tencent.liteav.basic.p.h",  // 混淆后的可能类名
            "com.tencent.liteav.basic.p.i"
        };

        // TXLivePlayer - 直播播放器  
        String[] livePlayerClasses = {
            "com.tencent.rtmp.TXLivePlayer",
            "com.tencent.liteav.txcplayer.TXCLivePlayer"
        };

        // 通用ExoPlayer
        String[] exoPlayerClasses = {
            "com.google.android.exoplayer2.SimpleExoPlayer",
            "com.google.android.exoplayer2.ExoPlayer"
        };

        int hookedCount = 0;

        // Hook TXVodPlayer.setRate
        for (String className : vodPlayerClasses) {
            try {
                Class<?> clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader);
                if (clazz != null) {
                    // setRate(float) - 设置播放速度
                    Method setRateMethod = XposedHelpers.findMethodExactIfExists(clazz, "setRate", float.class);
                    if (setRateMethod != null) {
                        XposedBridge.hookMethod(setRateMethod, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) {
                                handleLiteAVSpeedChange(param, "TXVodPlayer.setRate");
                            }
                        });
                        hookedCount++;
                        logWeChatHook("Hooked TXVodPlayer.setRate: " + className);
                    }

                    // setSpeed(float) - 某些版本使用这个方法
                    Method setSpeedMethod = XposedHelpers.findMethodExactIfExists(clazz, "setSpeed", float.class);
                    if (setSpeedMethod != null) {
                        XposedBridge.hookMethod(setSpeedMethod, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) {
                                handleLiteAVSpeedChange(param, "TXVodPlayer.setSpeed");
                            }
                        });
                        hookedCount++;
                        logWeChatHook("Hooked TXVodPlayer.setSpeed: " + className);
                    }

                    // Hook startVodPlay 来在播放开始时设置速度
                    hookVodPlayStart(clazz, lpparam);
                }
            } catch (Exception e) {
                logWeChatHook("Failed to hook " + className + ": " + e.getMessage());
            }
        }

        // Hook TXLivePlayer
        for (String className : livePlayerClasses) {
            try {
                Class<?> clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader);
                if (clazz != null) {
                    Method setRateMethod = XposedHelpers.findMethodExactIfExists(clazz, "setRate", float.class);
                    if (setRateMethod != null) {
                        XposedBridge.hookMethod(setRateMethod, new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) {
                                handleLiteAVSpeedChange(param, "TXLivePlayer.setRate");
                            }
                        });
                        hookedCount++;
                        logWeChatHook("Hooked TXLivePlayer.setRate: " + className);
                    }
                }
            } catch (Exception e) {
                logWeChatHook("Failed to hook " + className + ": " + e.getMessage());
            }
        }

        // Hook ExoPlayer (某些视频可能使用ExoPlayer)
        for (String className : exoPlayerClasses) {
            try {
                Class<?> clazz = XposedHelpers.findClassIfExists(className, lpparam.classLoader);
                if (clazz != null) {
                    // setPlaybackParameters 用于设置播放速度
                    hookExoPlayerSpeed(clazz, lpparam);
                    hookedCount++;
                    logWeChatHook("Hooked ExoPlayer: " + className);
                }
            } catch (Exception e) {
                // ExoPlayer可能不存在
            }
        }

        logWeChatHook("TencentLiteAV hook completed, hooked " + hookedCount + " methods");
    }

    /**
     * Hook播放开始方法，在播放开始时设置速度
     */
    private static void hookVodPlayStart(Class<?> playerClass, XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // Hook startVodPlay(String url)
            Method startMethod = XposedHelpers.findMethodExactIfExists(playerClass, "startVodPlay", String.class);
            if (startMethod != null) {
                XposedBridge.hookMethod(startMethod, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) {
                        try {
                            float targetSpeed = getSpeedConfig();
                            // 延迟设置速度，确保播放器已准备好
                            new android.os.Handler(android.os.Looper.getMainLooper()).postDelayed(() -> {
                                try {
                                    applyWeChatTargetSpeed(param.thisObject, lpparam.classLoader, "after startVodPlay delayed");
                                } catch (Exception e) {
                                    logWeChatHook("Failed to set speed after startVodPlay: " + e.getMessage());
                                }
                            }, 100);
                        } catch (Exception e) {
                            logWeChatHook("Error in startVodPlay hook: " + e.getMessage());
                        }
                    }
                });
                logWeChatHook("Hooked startVodPlay: " + playerClass.getName());
            }

            // Hook resume/start 方法
            String[] resumeMethods = {"resume", "start", "play"};
            for (String methodName : resumeMethods) {
                Method method = XposedHelpers.findMethodExactIfExists(playerClass, methodName);
                if (method != null) {
                    XposedBridge.hookMethod(method, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) {
                            try {
                                applyWeChatTargetSpeed(param.thisObject, lpparam.classLoader, "after " + methodName);
                            } catch (Exception e) {
                                // 忽略
                            }
                        }
                    });
                    logWeChatHook("Hooked " + methodName + ": " + playerClass.getName());
                }
            }
        } catch (Exception e) {
            logWeChatHook("Error hooking play start: " + e.getMessage());
        }
    }

    /**
     * Hook ExoPlayer的播放速度设置
     */
    private static void hookExoPlayerSpeed(Class<?> exoPlayerClass, XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // ExoPlayer使用setPlaybackParameters来设置速度
            Class<?> playbackParamsClass = XposedHelpers.findClassIfExists(
                "com.google.android.exoplayer2.PlaybackParameters", lpparam.classLoader);
            
            if (playbackParamsClass != null) {
                Method setParamsMethod = XposedHelpers.findMethodExactIfExists(
                    exoPlayerClass, "setPlaybackParameters", playbackParamsClass);
                
                if (setParamsMethod != null) {
                    XposedBridge.hookMethod(setParamsMethod, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) {
                            try {
                                Object params = param.args[0];
                                float currentSpeed = (float) XposedHelpers.getObjectField(params, "speed");
                                
                                if (Math.abs(currentSpeed - 1.0f) < 0.01f) {
                                    float targetSpeed = getSpeedConfig();
                                    if (Math.abs(targetSpeed - 1.0f) > 0.01f) {
                                        // 创建新的PlaybackParameters
                                        Object newParams = XposedHelpers.newInstance(playbackParamsClass, targetSpeed);
                                        param.args[0] = newParams;
                                        logWeChatHook("ExoPlayer speed set to: " + targetSpeed);
                                    }
                                }
                            } catch (Exception e) {
                                logWeChatHook("Error in ExoPlayer hook: " + e.getMessage());
                            }
                        }
                    });
                }
            }
        } catch (Exception e) {
            logWeChatHook("Error hooking ExoPlayer: " + e.getMessage());
        }
    }

    /**
     * 处理LiteAV播放器的速度变化
     */
    private static void handleLiteAVSpeedChange(XC_MethodHook.MethodHookParam param, String source) {
        try {
            if (Boolean.TRUE.equals(wxApplyingSpeed.get())) {
                return;
            }
            float speed = (float) param.args[0];
            float targetSpeed = getSpeedConfig();

            logWeChatHook("[LiteAV] " + source + " called with speed: " + speed + ", target: " + targetSpeed);

            // 如果是1.0倍速，且目标速度不是1.0，则修改为目标速度
            if (Math.abs(speed - 1.0f) < 0.01f && Math.abs(targetSpeed - 1.0f) > 0.01f) {
                // 检查是否是用户手动设置
                if (!isManualSpeedChange()) {
                    param.args[0] = targetSpeed;
                    logWeChatHook("[LiteAV] Auto speed set from " + source + ": " + targetSpeed);
                }
            } else if (Math.abs(speed - targetSpeed) > 0.01f && isManualSpeedChange()) {
                // 用户手动修改了速度，保存这个设置
                // 注意：这里不修改param.args[0]，让用户的设置生效
                logWeChatHook("[LiteAV] Manual speed change detected: " + speed);
            } else if (Math.abs(speed - targetSpeed) > 0.01f) {
                param.args[0] = targetSpeed;
                logWeChatHook("[LiteAV] Init speed corrected from " + source + ": " + targetSpeed);
            }
        } catch (Exception e) {
            logWeChatHook("[LiteAV] Error in speed change handler: " + e.getMessage());
        }
    }

    /**
     * 检查是否是用户手动设置速度（通过检查调用栈）
     * 改进版本：更精确地判断用户交互，避免误判
     */
    private static boolean isManualSpeedChange() {
        StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
        
        // 记录调用栈用于调试
        StringBuilder stackInfo = new StringBuilder();
        for (int i = 3; i < Math.min(15, stackTrace.length); i++) {
            stackInfo.append(stackTrace[i].getClassName()).append(".").append(stackTrace[i].getMethodName()).append(" -> ");
        }
        logWeChatHook("[StackTrace] " + stackInfo.toString());
        
        for (int i = 3; i < Math.min(20, stackTrace.length); i++) {
            String className = stackTrace[i].getClassName().toLowerCase();
            String methodName = stackTrace[i].getMethodName().toLowerCase();
            
            // 只检测明确的用户交互方法，避免误判
            // 1. 检查点击事件 - 必须是完整的onClick方法
            if (methodName.equals("onclick") || methodName.equals("onitemclick")) {
                logWeChatHook("[Manual] Detected click event: " + className + "." + methodName);
                return true;
            }
            
            // 2. 检查触摸事件 - 只检测onTouchEvent，不检测dispatch
            if (methodName.equals("ontouchevent") || methodName.equals("ontouch")) {
                logWeChatHook("[Manual] Detected touch event: " + className + "." + methodName);
                return true;
            }
            
            // 3. 检查performClick - 这是View的点击确认方法
            if (methodName.equals("performclick") || methodName.equals("performlongclick")) {
                logWeChatHook("[Manual] Detected perform click: " + className + "." + methodName);
                return true;
            }
            
            // 4. 检查速度选择UI组件
            if (className.contains("speedpanel") || className.contains("speedmenu") ||
                className.contains("speedselector") || className.contains("speedcontrol") ||
                className.contains("playerspeed") || className.contains("ratemenu")) {
                logWeChatHook("[Manual] Detected speed UI: " + className);
                return true;
            }
            
            // 5. 检查Finder视频号特有的速度选择器
            if (className.contains("finder") && (className.contains("speed") || className.contains("rate"))) {
                logWeChatHook("[Manual] Detected Finder speed selector: " + className);
                return true;
            }
        }
        
        return false;
    }

    /**
     * Hook所有可能的播放速度相关方法
     */
    private static void hookWeChatSpeedMethods(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            logWeChatHook("Starting speed methods hook...");

            String[] possibleMethods = {"setPlaySpeed", "setSpeed", "setPlaybackSpeed", "setRate", "setPlaybackRate"};
            int hookedCount = 0;

            for (String methodName : possibleMethods) {
                try {
                    // 查找微信包下所有包含指定方法的类
                    Class<?>[] candidateClasses = getWeChatPlayerClasses(lpparam.classLoader);

                    for (Class<?> clazz : candidateClasses) {
                        try {
                            Method method = XposedHelpers.findMethodExactIfExists(clazz, methodName, float.class);
                            if (method != null) {
                                XposedBridge.hookMethod(method, new XC_MethodHook() {
                                    @Override
                                    protected void beforeHookedMethod(MethodHookParam param) {
                                        handleWeChatSpeedChange(param, "method-" + methodName);
                                    }
                                });
                                hookedCount++;
                                logWeChatHook("Hooked method " + methodName + ": " + clazz.getName());
                            }
                        } catch (Exception e) {
                            // 继续下一个类
                        }
                    }
                } catch (Exception e) {
                    // 继续下一个方法
                }
            }
            logWeChatHook("Speed methods hook completed, hooked " + hookedCount + " methods");
        } catch (Exception e) {
            logWeChatHook("Error in speed methods hook: " + e.getMessage());
        }
    }

    // 用于追踪每个播放器实例的最后设置速度，避免重复设置
    private static final java.util.Map<Object, Float> lastSpeedMap = new java.util.concurrent.ConcurrentHashMap<>();
    // 记录每个实例最后一次设置非1.0速度的时间，用于判断用户是否刚刚手动调整过
    private static final java.util.Map<Object, Long> lastManualChangeTimeMap = new java.util.concurrent.ConcurrentHashMap<>();
    // 判断间隔（毫秒）- 如果用户刚设置过自定义速度，短时间内不自动修改
    private static final long MANUAL_CHANGE_COOLDOWN = 3000L;
    
    /**
     * 改进的速度判断逻辑 - 更智能的自动设置
     */
    private static void handleWeChatSpeedChange(XC_MethodHook.MethodHookParam param, String source) {
        try {
            if (Boolean.TRUE.equals(wxApplyingSpeed.get())) {
                return;
            }
            float speed = (float) param.args[0];
            float targetSpeed = getSpeedConfig();
            Object playerInstance = param.thisObject;

            // 记录每次调用，便于调试
            logWeChatHook("[Proxy] " + source + " called with speed: " + speed + ", target: " + targetSpeed);

            // 获取该实例的上次速度设置
            Float lastSpeed = lastSpeedMap.get(playerInstance);
            Long lastManualTime = lastManualChangeTimeMap.get(playerInstance);
            long currentTime = System.currentTimeMillis();
            
            // 检查是否在用户手动修改的冷却期内
            boolean inCooldown = lastManualTime != null && (currentTime - lastManualTime) < MANUAL_CHANGE_COOLDOWN;
            
            // 如果目标速度就是1.0，不需要做任何修改
            if (Math.abs(targetSpeed - 1.0f) < 0.01f) {
                logWeChatHook("[Proxy] Target is 1.0x, no modification needed");
                lastSpeedMap.put(playerInstance, speed);
                return;
            }
            
            // 如果传入的速度是1.0，检查是否需要自动设置为目标速度
            if (Math.abs(speed - 1.0f) < 0.01f) {
                // 检查是否是用户手动设置1.0
                if (isManualSpeedChange()) {
                    logWeChatHook("[Proxy] User manually set to 1.0x, respecting");
                    lastSpeedMap.put(playerInstance, speed);
                    lastManualChangeTimeMap.put(playerInstance, currentTime);
                    return;
                }
                
                // 在冷却期内不自动修改
                if (inCooldown) {
                    logWeChatHook("[Proxy] In cooldown period, keeping: " + speed);
                    lastSpeedMap.put(playerInstance, speed);
                    return;
                }
                
                // 自动设置为目标速度
                param.args[0] = targetSpeed;
                lastSpeedMap.put(playerInstance, targetSpeed);
                logWeChatHook("[Proxy] Auto speed set from " + source + ": " + targetSpeed);
            } 
            // 如果传入的速度不是1.0，说明是用户手动设置或已经被我们修改过
            else if (Math.abs(speed - targetSpeed) > 0.01f) {
                if (isManualSpeedChange()) {
                    logWeChatHook("[Proxy] User custom speed detected: " + speed);
                    lastSpeedMap.put(playerInstance, speed);
                    lastManualChangeTimeMap.put(playerInstance, currentTime);
                } else if (!inCooldown) {
                    param.args[0] = targetSpeed;
                    lastSpeedMap.put(playerInstance, targetSpeed);
                    logWeChatHook("[Proxy] Init speed corrected from " + source + ": " + targetSpeed);
                }
            } else {
                // 速度已经是目标速度，正常通过
                logWeChatHook("[Proxy] Speed already at target: " + speed);
                lastSpeedMap.put(playerInstance, speed);
            }
        } catch (Exception e) {
            logWeChatHook("[Proxy] Error in speed change handler: " + e.getMessage());
        }
    }

    /**
     * 获取微信中可能的播放器类
     */
    private static Class<?>[] getWeChatPlayerClasses(ClassLoader classLoader) {
        // 由于Android限制，我们无法直接获取所有类
        // 改用已知的播放器类列表和模式匹配
        String[] knownPlayerClasses = {
            // Finder视频号相关
            "com.tencent.mm.plugin.finder.video.FinderThumbPlayerProxy",
            "com.tencent.mm.plugin.finder.video.FinderVideoPlayer",
            "com.tencent.mm.plugin.finder.video.FinderVideoCore",
            "com.tencent.mm.plugin.finder.video.FinderFeedPlayer",
            "com.tencent.mm.plugin.finder.video.FinderLivePlayer",
            "com.tencent.mm.plugin.finder.video.ui.FinderVideoView",
            "com.tencent.mm.plugin.finder.live.FinderLivePlayerProxy",
            // 朋友圈视频
            "com.tencent.mm.plugin.sns.video.SnsVideoPlayer",
            "com.tencent.mm.plugin.sns.video.SnsVideoView",
            "com.tencent.mm.plugin.sns.video.SnsVideoController",
            // 聊天视频
            "com.tencent.mm.plugin.video.player.MMVideoPlayer",
            "com.tencent.mm.plugin.video.player.VideoPlayer",
            "com.tencent.mm.plugin.video.MMVideoView",
            // 小程序视频
            "com.tencent.mm.plugin.appbrand.video.AppBrandVideoPlayer",
            "com.tencent.mm.plugin.appbrand.video.AppBrandVideoView",
            "com.tencent.mm.plugin.appbrand.video.AppBrandVideoController",
            // Kinda框架相关（视频号使用）
            "com.tencent.kinda.framework.widget.video.KindaVideoPlayer",
            "com.tencent.kinda.framework.widget.video.KindaVideoView",
            // 通用媒体播放器
            "com.tencent.mm.media.MMMediaPlayer",
            "com.tencent.mm.media.WxMediaPlayer"
        };

        java.util.List<Class<?>> classes = new java.util.ArrayList<>();

        // 尝试加载已知的类
        for (String className : knownPlayerClasses) {
            try {
                Class<?> clazz = XposedHelpers.findClassIfExists(className, classLoader);
                if (clazz != null) {
                    classes.add(clazz);
                    logWeChatHook("Found player class: " + className);
                }
            } catch (Exception e) {
                // 忽略不存在的类
            }
        }

        logWeChatHook("Total player classes found: " + classes.size());
        return classes.toArray(new Class<?>[0]);
    }

    /**
     * 保留原有的精确Hook作为备用
     */
    private static void tryOriginalWeChatHook(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            XposedHelpers.findAndHookMethod("com.tencent.mm.plugin.finder.video.FinderThumbPlayerProxy",
                lpparam.classLoader, "setPlaySpeed", float.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) {
                    handleWeChatSpeedChange(param, "original");
                }
            });
            logWeChatHook("Original hook successful");
        } catch (XposedHelpers.ClassNotFoundError e) {
            logWeChatHook("Original hook failed: FinderThumbPlayerProxy not found");
        } catch (Exception e) {
            logWeChatHook("Original hook error: " + e.getMessage());
        }

        // 尝试动态发现播放器类
        tryDynamicPlayerDiscovery(lpparam);
    }

    /**
     * 动态发现播放器类 - 通过监控MediaPlayer相关调用
     */
    private static void tryDynamicPlayerDiscovery(XC_LoadPackage.LoadPackageParam lpparam) {
        try {
            // Hook android.media.MediaPlayer.setPlaybackParams 来捕获所有MediaPlayer速度设置
            XposedHelpers.findAndHookMethod(
                android.media.MediaPlayer.class, 
                "setPlaybackParams", 
                android.media.PlaybackParams.class, 
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) {
                        try {
                            android.media.PlaybackParams params = (android.media.PlaybackParams) param.args[0];
                            float speed = params.getSpeed();
                            float targetSpeed = getSpeedConfig();

                            logWeChatHook("[MediaPlayer] setPlaybackParams called with speed: " + speed);

                            if (Math.abs(speed - 1.0f) < 0.01f && Math.abs(targetSpeed - 1.0f) > 0.01f) {
                                if (!isManualSpeedChange()) {
                                    android.media.PlaybackParams newParams = new android.media.PlaybackParams();
                                    newParams.setSpeed(targetSpeed);
                                    // 保留其他参数
                                    try {
                                        newParams.setPitch(params.getPitch());
                                    } catch (Exception e) {
                                        // 某些版本可能不支持
                                    }
                                    param.args[0] = newParams;
                                    logWeChatHook("[MediaPlayer] Speed modified to: " + targetSpeed);
                                }
                            }
                        } catch (Exception e) {
                            logWeChatHook("[MediaPlayer] Error: " + e.getMessage());
                        }
                    }
                }
            );
            logWeChatHook("MediaPlayer.setPlaybackParams hook successful");
        } catch (Exception e) {
            logWeChatHook("MediaPlayer hook failed: " + e.getMessage());
        }

        // Hook SurfaceTexture OnFrameAvailableListener 来检测视频帧
        // 这有助于确认视频是否在播放
        try {
            // 监控视频播放状态
            logWeChatHook("Dynamic player discovery initialized");
        } catch (Exception e) {
            // 忽略
        }
    }

    /**
     * 安全的Hook包装器 - 确保Hook异常不会导致应用崩溃
     */
    private static void safeHook(Runnable hookAction, String hookName) {
        try {
            hookAction.run();
        } catch (Exception e) {
            logWeChatHook("Safe hook failed for " + hookName + ": " + e.getMessage());
        }
    }

    /**
     * 统一的微信日志输出
     */
    private static void logWeChatHook(String message) {
        XposedBridge.log("[VideoSpeed] " + message);
    }
}

