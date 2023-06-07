package com.xiuGEN.utils;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Random;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UUIDUtil {
    private static UUID uuid= UUID.randomUUID();
    /*
    * 获取
    * */
    public static String getRandomNuber(){
        String randomNumber =uuid.toString();
        String replace = randomNumber.replace("-", "");
        return replace;
    }
    /*
    * 从中提取sessionid
    * */

    public static String getContextFrom2RandomNumber(String RandomNuber){
        String randomNuber = getRandomNuber();
        String content = RandomNuber.substring(randomNuber.length(), RandomNuber.length() - randomNuber.length());
        return content;
    }
    //从请求头中提取文件名和类型
    public static String getRealFileName(String context) {
        // Content-Disposition: form-data; name="myfile"; filename="a_left.jpg"
        int index = context.lastIndexOf("=");
        String filename = context.substring(index + 2, context.length() - 1);
        return filename;
    }
    //根据给定的文件名和后缀截取文件名
    public static String getFileType(String fileName){
        //9527s.jpg
        int index = fileName.lastIndexOf(".");
        return fileName.substring(index);
    }

    /**
     * @author: GEN
     * @date: 2023/4/29
     * @Param: str字符串
     * @return
     *  判断字符串中是否有中文乱码
     */
    public static boolean isMessyCode(String str) {
       /* String regex = "[^\u4E00-\u9FA5\uFE30-\uFFA0\\w]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(str);
        return matcher.find();*/
        Charset charset = Charset.forName("UTF-8");
        CharsetDecoder decoder = charset.newDecoder();
        try {
            decoder.decode(ByteBuffer.wrap(str.getBytes(charset)));
            return false;
        } catch (CharacterCodingException e) {
            return true;
        }
    }

    /**
     * @author: GEN
     * @date: 2023/5/21
     * @Param: length 密码长度
     * @return 随机生成密码
     */
    public static String randomPwd(int length){
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+";
        StringBuilder password = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }
    /**
     * @author: GEN
     * @date: 2023/5/24
     * @Param: null
     * @return 生成随机数
     */
    public static String randomNum(int length){
        String chars = "0123456789";
        StringBuilder password = new StringBuilder();

        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(chars.length());
            password.append(chars.charAt(index));
        }
        return password.toString();
    }
}
