package com.wen.security.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.graphics.Paint.FontMetrics;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.ParseException;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;

import com.wen.security.ui.view.time.JudgeDate;
import com.wen.security.ui.view.time.WheelMain;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 公共方法工具类
 *
 * @author Administrator
 *
 */
public class RTools {

    public static String getString(String string) {
        if (string == null) {
            string = "";
        }
        return string;
    }
    public static Bitmap stringtoBitmap(String string) {
        // 将字符串转换成Bitmap类型
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(string, Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
    /**
     *  将Bitmap转换成字符串
      */
    public static String bitmaptoString(Bitmap bitmap) {

        String string = null;
        ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.PNG, 100, bStream);
        byte[] bytes = bStream.toByteArray();
        string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }

    /**
     * list是否包含i
     * @param i
     * @param nolist
     * @return
     */
    public static boolean contains(Integer i, ArrayList<Integer> nolist) {
        for (int j = 0; j < nolist.size(); j++) {
            if (i == nolist.get(j)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 转换城整数的方法
     */
    public static int toInt(String i) {
        try {
            return Integer.valueOf(i);
        } catch (NumberFormatException e) {
            // StatisticsManager.getInstance().onErrorEvent(StatisticsDefine.SELF_DEFINE_ERROR,
            // StatisticsDefine.SELF_DEFINE_ERROR,
            // StatisticsDefine.SELF_DEFINE_ERROR, e);
            return 0;
        }
    }

    /**
     * 检查网路是否可用
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager mgr = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] info = mgr.getAllNetworkInfo();
        if (info != null) {
            for (int i = 0; i < info.length; i++) {
                if (info[i].getState() == NetworkInfo.State.CONNECTED) {
//                    TLog.v("网络可用");
                    return true;
                }
            }
        }
        TLog.e("网络不可用");
        return false;
    }

    /**
     * 网路类型
     * @param context
     * @return
     */
    public static String isNet(Context context) {
        String temp = "无连接";

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkINfo = cm.getActiveNetworkInfo();
        if (networkINfo != null) {

            if (networkINfo.getType() == ConnectivityManager.TYPE_WIFI) {
                temp = "wifi连接";
                return temp;
            } else {
                temp = "数据连接";
                return temp;
            }
        }
        return temp;
    }

    /**
     * 转换等级
     * @param Rankid
     * @return
     */
    public static String convertRankid(int Rankid) {
        String Rank = "";
        switch (Rankid) {
            case 0:
                Rank = "管理员";
                break;
            case 1:
                Rank = "一级警员";
                break;
            case 2:
                Rank = "二级警员";
                break;
            case 3:
                Rank = "一级警司";
                break;
            case 4:
                Rank = "二级警司";
                break;
            case 5:
                Rank = "三级警司";
                break;
            case 6:
                Rank = "一级警督";
                break;
            case 7:
                Rank = "二级警督";
                break;
            case 8:
                Rank = "三级警督";
                break;
            case 9:
                Rank = "一级警监";
                break;
            case 10:
                Rank = "二级警监";
                break;
            case 11:
                Rank = "三级警监";
                break;

            default:
                break;
        }
        return Rank;
    }

    public static String convertApplyBase(int Rankid) {
        String Rank = "";
        /*
         * 1、 涉黄 2、 涉赌 3、 刑事案件
         */
        switch (Rankid) {

            case 1:
                Rank = "涉黄";
                break;
            case 2:
                Rank = "涉赌";
                break;
            case 3:
                Rank = "刑事案件";
                break;

            default:
                break;
        }
        return Rank;
    }

    /**
     * 获取屏幕密度
     *
     * @param context
     * @return
     */
    public static float getSreenDensity(Context context) {
        Resources resources = context.getResources();
        DisplayMetrics dm = resources.getDisplayMetrics();
        return dm.density;
    }

    /**
     * 获取字符串宽度
     *
     * @param str
     * @param paint
     * @return
     */
    public static float getTextWidth(String str, Paint paint) {
        float iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; ++j) {
                iRet += Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

    /**
     * 获取文本高
     *
     * @param str
     * @param paint
     * @return
     */
    public static float getTextHeight(String str, Paint paint) {
        float iRet = 0;
        FontMetrics fm = paint.getFontMetrics();
        return (float) (Math.ceil(fm.descent - fm.ascent));
    }

    /**
     * 格式化字符串 不满两位 前面补0
     *
     * @param x
     * @return
     */
    public static String formatAddr(int x) {
        NumberFormat formatter = NumberFormat.getNumberInstance();
        formatter.setMinimumIntegerDigits(5);
        formatter.setGroupingUsed(false);
        return formatter.format(x);
    }

    /**
     * 格式化字符串 不满两位 前面补0(参数为字符)
     *
     * @param code
     * @return
     */
    public static String formatAddr(String code) {
        int x = Integer.parseInt(code);
        return formatAddr(x);
    }

    /**
     * 输入是否为纯中文
     *
     * @param str
     * @return
     */
    public static boolean isChinese(String str) {
        if (str == null) {
            return false;
        }
        Pattern p1 = Pattern.compile("[\u4E00-\u9FB0]");
        boolean ret = true;
        for (int i = 0; i < str.length(); ++i) {
            String s = "" + str.charAt(i);
            Matcher m = p1.matcher(s);
            ret = m.matches();
            if (!ret) {
                return ret;
            }
        }
        return ret;
    }

    /**
     * 手机号码格式验证
     *
     * @param mobiles
     * @return
     */
    public static boolean isMobileNO(String mobiles) {
        if (mobiles == null) {
            return false;
        }
        /*
         * StringBuffer buff = new StringBuffer(); for( int i = 0; i <
         * mobiles.length(); i++){ char ch = mobiles.charAt(i); if( ch >=48 &&
         * ch <= 57 ){ buff.append(ch); }/^0(([1-9]\d)|([3-9]\d{2}))\d{8}$/ }
         */

        // Pattern p =
        // Pattern.compile("^0?(13[0-9]|15[012356789]|18[0123456789]|14[0123456789])[0-9]{8}$");
        // Pattern p =
        // Pattern.compile("^((13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$");
        Pattern p = Pattern.compile("\\d*(\\d)\\1{6,}\\d*"); // 相同数字连续7次以上就不是手机号码

        Matcher m = p.matcher(mobiles);
        if (m.matches()) {
            return false;
        }

        p = Pattern.compile("^13[0-9]{1}[0-9]{8}$"); // //130-139开头
        m = p.matcher(mobiles);
        if (m.matches()) {
            return true;
        }

        p = Pattern.compile("^15[^4]{1}[0-9]{8}$"); // 15开头，但不是154
        m = p.matcher(mobiles);
        if (m.matches()) {
            return true;
        }

        p = Pattern.compile("^18[^4]{1}[0-9]{8}$"); // //18开头，但不是184
        m = p.matcher(mobiles);
        if (m.matches()) {
            return true;
        }
        p = Pattern.compile("^14[7]{1}[0-9]{8}$"); // 以147开头
        m = p.matcher(mobiles);
        return m.matches();
    }

    /**
     * 电话号码格式验证
     *
     * @param phone
     * @return
     */
    /*
     * public static boolean isPhoneNO(String phone){ //Pattern p =
     * Pattern.compile(
     * "^((\\+?[0-9]{2,4}\\-[0-9]{3,4}\\-)|([0-9]{3,4}\\-))?([0-9]{7,8})(\\-[0-9]+)?$"
     * ); Pattern p =
     * Pattern.compile("^(0(10|2[1-3]|[3-9]\\d{2}))?[1-9]\\d{6,7}(\\-[0-9]+)?$"
     * ); Matcher m = p.matcher(phone); return m.matches(); }
     */
    /**
     * 电话号码格式验证 修改了不能匹配广州的区号不能修改的方法
     *
     * @param phone
     * @return
     */
    public static boolean isPhoneNO(String phone) {
        // Pattern p =
        // Pattern.compile("^((\\+?[0-9]{2,4}\\-[0-9]{3,4}\\-)|([0-9]{3,4}\\-))?([0-9]{7,8})(\\-[0-9]+)?$");
        // Pattern p =
        // Pattern.compile("^(0(10|2[1-3]|[3-9]\\d{2}))?[1-9]\\d{6,7}(\\-[0-9]+)?$");
        Pattern p = Pattern
                .compile("^0{1}[1-9]{1}[0-9]{1,2}[1-9]{1}[0-9]{6,7}?$");
        Matcher m = p.matcher(phone);
        return m.matches();
    }

    /**
     * 邮箱格式验证
     *
     * @param email
     * @return
     */
    public static boolean isEmail(String email) {
        Pattern p = Pattern
                .compile("^([a-zA-Z0-9_-])+@([a-zA-Z0-9_-])+((\\.[a-zA-Z0-9_-]{2,3}){1,2})$");
        Matcher m = p.matcher(email);
        return m.matches();
    }

    /**
     * 车牌号码格式验证
     *
     * @param carlicense
     * @return
     */
    public static boolean isCarLicense(String carlicense) {
        Pattern p = Pattern
                .compile("^[\u4E00-\u9FA5]{1}[A-Z]{1} ?[a-zA-Z0-9]{5}$");
        Matcher m = p.matcher(carlicense);
        return m.matches();
    }

    /**
     * 数字时间转字符串(完整)
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static String long2StrTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",
                Locale.CHINA);

        java.util.Date dt = new Date();
        dt.setTime(time);
        String sDateTime = sdf.format(dt);

        return sDateTime;
    }

    /**
     * 数字时间转字符串(只含日期)
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static String long2StrDate(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.CHINA);
        java.util.Date dt = new Date();
        dt.setTime(time);
        String sDateTime = sdf.format(dt);

        return sDateTime;
    }

    /**
     * 数字时间转字符串(时分)
     *
     * @param time
     * @return
     * @throws ParseException
     */
    public static String convertLong2StrTime(long time) {
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);

        java.util.Date dt = new Date();
        dt.setTime(time);
        String sDateTime = sdf.format(dt);

        return sDateTime;
    }

    public static Date long2SDate(long time) {
        java.util.Date dt = new Date();
        return dt;
    }

    public static Bitmap getBitmap(String imgBase64Str) {
        Bitmap bit = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(imgBase64Str, Base64.DEFAULT);
            bit = BitmapFactory.decodeByteArray(bitmapArray, 0,
                    bitmapArray.length);
        } catch (Exception e) {
            // StatisticsManager.getInstance().onErrorEvent(StatisticsDefine.SELF_DEFINE_ERROR,
            // StatisticsDefine.SELF_DEFINE_ERROR,
            // StatisticsDefine.SELF_DEFINE_ERROR, e);
            e.printStackTrace();
        }
        return bit;
    }

    public static String[] getPhoneNumber(String str) {
        ArrayList<String> phones = new ArrayList<String>();
        StringBuffer buff = new StringBuffer();
        for (int i = 0; i < str.length(); ++i) {
            char chars = str.charAt(i);
            if (chars != ' ' && chars != ',' && chars != '，') {
                buff.append(chars);
            } else {
                if (buff.length() > 0 && buff.length() > 5) {
                    phones.add(buff.toString());
                    buff.delete(0, buff.length());
                }
            }
        }
        if (buff.length() > 0) {
            phones.add(buff.toString());
        }
        String ret[] = new String[phones.size()];
        for (int i = 0; i < phones.size(); ++i) {
            ret[i] = phones.get(i);
        }
        return ret;
    }

    /**
     * 整数相除5的方法 用于 补全
     *
     * @param id
     * @return
     * @author:zcw
     * @date:2013-10-13 上午11:34:18
     * @version 1.0
     */
    public static int modFive(int id) {
        return id % 5 == 0 ? 0 : (5 - id % 5);
    }

    /**
     * 最小内存处理的图片的方法 变成相应16位色
     *
     * @param in
     * @return
     */
    public static Bitmap readBitMap(InputStream in) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inPreferredConfig = Bitmap.Config.RGB_565;
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = 2;
        opt.inPurgeable = true;
        opt.inInputShareable = true;
        return BitmapFactory.decodeStream(in, null, opt);
    }

    /**
     * 根据宽高压缩图片 变成相应16位色
     *
     * @param url
     * @return Bitmap
     */
    public static Bitmap readMap(String url, int height, int width) {
        BitmapFactory.Options opt = new BitmapFactory.Options();
        opt.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(url, opt);
        int imageHeight = opt.outHeight;
        int imageWidth = opt.outWidth;

        int scaleX = imageHeight / height;
        int scaleY = imageWidth / width;

        int scale = 1;

        if (scaleX > scaleY & scaleY >= 1) {
            scale = scaleX;
        }
        if (scaleY > scaleX & scaleX >= 1) {
            scale = scaleY;
        }
        opt.inJustDecodeBounds = false;
        opt.inSampleSize = scale;
        return BitmapFactory.decodeFile(url, opt);
    }

    /**
     * 状态栏高度
     */
    public static int getStatusHeight(Context context) {
        int statusHeight = 0;
        Rect localRect = new Rect();
        ((Activity) context).getWindow().getDecorView()
                .getWindowVisibleDisplayFrame(localRect);
        statusHeight = localRect.top;
        if (0 == statusHeight) {
            Class<?> localClass;
            try {
                localClass = Class.forName("com.android.internal.R$dimen");
                Object localObject = localClass.newInstance();
                int i5 = Integer.parseInt(localClass
                        .getField("status_bar_height").get(localObject)
                        .toString());
                statusHeight = context.getResources().getDimensionPixelSize(i5);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return statusHeight;
    }

    /**
     * 获取当前到秒的时间
     *
     * @return
     */
    public static String getTimeToM() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat(
                "yyyy-MM-dd   hh:mm:ss");
        String date = sDateFormat.format(new java.util.Date());
        System.out.println(date);
        return date;
    }

    /**
     * 获取当前到天的时间
     *
     * @return
     */
    public static String getTimeToD() {
        SimpleDateFormat sDateFormat = new SimpleDateFormat("yyyy:MM:dd");
        String date = sDateFormat.format(new java.util.Date());
        return date;
    }

    private void showDateTimeDialog(Activity context, final View v) {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String time = ((EditText) v).getText().toString();
        Calendar calendar = Calendar.getInstance();
        if (JudgeDate.isDate(time, "yyyy-MM-dd")) {
            try {
                calendar.setTime(dateFormat.parse(time));
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        final WheelMain wheelMain = new WheelMain(context, year, month, day);

        new AlertDialog.Builder(context).setTitle("选择时间")
                .setView(wheelMain.getView())
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ((EditText) v).setText(wheelMain.getTime());
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    /**
     * 根据流返回一个字符串信息
     *
     * @param is
     * @return
     * @throws IOException
     */
    public static String getStringFromInputStream(InputStream is)
            throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = -1;

        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        is.close();

        String html = baos.toString(); // 把流中的数据转换成字符串, 采用的编码是: utf-8

        // String html = new String(baos.toByteArray(), "GBK");

        baos.close();
        return html;
    }

}
