package com.ml.yx.comm;

import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import android.annotation.SuppressLint;

@SuppressLint("SimpleDateFormat")
public class StringUtil {
	public static final int INDEX_NOT_FOUND = -1;

	/**
	 * 验证账号的合法性(邮箱和手机号码)
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkAccount(String str) {
		if (checkEmail(str)) {
			return true;
		} else
			return checkMobile(str);

	}

	public static boolean checkMobile(String str) {
		Pattern p = Pattern.compile("[0-9]{11}");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	public static boolean checkVerfify(String str) {
		Pattern p = Pattern.compile("[0-9]{4}");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 验证昵称的合法性(昵称不能为特殊字符)
	 * 
	 * @param str
	 * @return
	 */
	public static boolean checkNickName(String str) {
		Pattern p = Pattern.compile("^[\\w+$\u4e00-\u9fa5]+$");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 验证password的合法性(必须是6-14数字或字母及组合)
	 * 
	 * @param pwdStr
	 * @return
	 */
	public static boolean checkPassword(String str) {
		Pattern p = Pattern.compile("^[\\w+$]{6,14}+$");
		Matcher m = p.matcher(str);
		return m.matches();
	}

	/**
	 * 验证email的合法性
	 * 
	 * @param emailStr
	 * @return
	 */
	public static boolean checkEmail(String emailStr) {
		String check = "^([a-z0-9A-Z]+[-|._]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?.)+[a-zA-Z]{2,}$";
		Pattern regex = Pattern.compile(check);
		Matcher matcher = regex.matcher(emailStr.trim());
		boolean isMatched = matcher.matches();
		if (isMatched) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 对字符串进行MD5加密
	 * 
	 * @param s
	 * @return
	 */
	public static String md5(String s) {
		char hexDigits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
		try {
			byte[] strTemp = s.getBytes();
			// 使用MD5创建MessageDigest对象
			MessageDigest mdTemp = MessageDigest.getInstance("MD5");
			mdTemp.update(strTemp);
			byte[] md = mdTemp.digest();
			int j = md.length;
			char str[] = new char[j * 2];
			int k = 0;
			for (int i = 0; i < j; i++) {
				byte b = md[i];
				// 将每个数(int)b进行双字节加密
				str[k++] = hexDigits[b >> 4 & 0xf];
				str[k++] = hexDigits[b & 0xf];
			}
			return new String(str);
		} catch (Exception e) {
			return null;
		}
	}

	public static boolean isBlank(String str) {
		int strLen;
		if (str == null || (strLen = str.length()) == 0) {
			return true;
		}
		if ("null".equalsIgnoreCase(str))
			return true;
		for (int i = 0; i < strLen; i++) {
			if ((Character.isWhitespace(str.charAt(i)) == false)) {
				return false;
			}
		}
		return true;
	}

	public static boolean isEmpty(String str) {
		return str == null || str.length() == 0;
	}

	public static boolean isNotEmpty(String str) {
		return !StringUtil.isEmpty(str);
	}

	public static boolean isNotBlank(String str) {
		return !StringUtil.isBlank(str);
	}

	public static String stripEnd(String str, String stripChars) {
		int end;
		if (str == null || (end = str.length()) == 0) {
			return str;
		}

		if (stripChars == null) {
			while ((end != 0) && Character.isWhitespace(str.charAt(end - 1))) {
				end--;
			}
		} else if (stripChars.length() == 0) {
			return str;
		} else {
			while ((end != 0) && (stripChars.indexOf(str.charAt(end - 1)) != INDEX_NOT_FOUND)) {
				end--;
			}
		}
		return str.substring(0, end);
	}

	public static String null2Zero(String str) {
		if (str == null || str.equals("")) {
			return "0";
		} else {
			return str;
		}

	}

	public static String replaceWhiteSpace(String str) {
		return str.replaceAll("\n", "");
	}

	public static String replaceTableSpace(String str) {
		return str.replaceAll(" ", "").replaceAll("\t", "");
	}

	public static String trimForFront(String str) {
		StringBuffer sb = new StringBuffer();
		boolean first = false;
		char aa;
		for (int i = 0, length = str.length(); i < length; i++) {
			aa = str.charAt(i);
			if (!first && aa == '\t') {
			} else {
				first = true;
				sb.append(aa);
			}
		}

		return sb.toString();
	}

	public static String trimAllWhitespace(String str) {
		return str.replaceAll(" ", "").replaceAll("\n", "").replaceAll("\t", "").toString();
	}

	private static Key initKeyForAES(String key) throws NoSuchAlgorithmException {
		if (null == key || key.length() == 0) {
			throw new NullPointerException("key not is null");
		}
		SecretKeySpec key2 = null;
		try {
			KeyGenerator kgen = KeyGenerator.getInstance("AES");
			kgen.init(128, new SecureRandom(key.getBytes()));
			SecretKey secretKey = kgen.generateKey();
			byte[] enCodeFormat = secretKey.getEncoded();
			key2 = new SecretKeySpec(enCodeFormat, "AES");
		} catch (NoSuchAlgorithmException ex) {
			throw new NoSuchAlgorithmException();
		}
		return key2;

	}

	/**
	 * AES加密算法，不受密钥长度限制
	 * 
	 * @param content
	 * @param key
	 * @return
	 */
	public static String encryptAES(String content, String key) {
		try {
			SecretKeySpec secretKey = (SecretKeySpec) initKeyForAES(key);
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			byte[] byteContent = content.getBytes("utf-8");
			cipher.init(Cipher.ENCRYPT_MODE, secretKey);// 初始化
			byte[] result = cipher.doFinal(byteContent);
			return asHex(result); // 加密
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * aes解密算法，不受密钥长度限制
	 * 
	 * @param content
	 * @param key
	 * @return
	 */
	public static String decryptAES(String content, String key) {
		try {
			SecretKeySpec secretKey = (SecretKeySpec) initKeyForAES(key);
			Cipher cipher = Cipher.getInstance("AES");// 创建密码器
			cipher.init(Cipher.DECRYPT_MODE, secretKey);// 初始化
			byte[] result = cipher.doFinal(asBytes(content));
			return new String(result); // 加密
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 将2进制数值转换为16进制字符串
	 * 
	 * @param buf
	 * @return
	 */
	private static String asHex(byte buf[]) {
		StringBuffer strbuf = new StringBuffer(buf.length * 2);
		int i;
		for (i = 0; i < buf.length; i++) {
			if (((int) buf[i] & 0xff) < 0x10)
				strbuf.append("0");
			strbuf.append(Long.toString((int) buf[i] & 0xff, 16));
		}
		return strbuf.toString();
	}

	/**
	 * 将16进制转换
	 * 
	 * @param hexStr
	 * @return
	 */
	private static byte[] asBytes(String hexStr) {
		if (hexStr.length() < 1)
			return null;
		byte[] result = new byte[hexStr.length() / 2];
		for (int i = 0; i < hexStr.length() / 2; i++) {
			int high = Integer.parseInt(hexStr.substring(i * 2, i * 2 + 1), 16);
			int low = Integer.parseInt(hexStr.substring(i * 2 + 1, i * 2 + 2), 16);
			result[i] = (byte) (high * 16 + low);
		}
		return result;
	}

	/**
	 * 验证是否是固定电话 只适合（xxx-xxxxxxxx/xxxx-xxxxxxxx）
	 * 
	 * @param phone
	 */
	public static boolean checkContactPhone(String phone) {
		Pattern pattern = Pattern.compile("(^[0-9]{3,4}-[0-9]{3,8}$)");
		Matcher matcher = pattern.matcher(phone);
		return matcher.matches();
	}

	public static String getNbitNumberInString(String source, int num) {
		String result = "";
		if (StringUtil.isBlank(source)) {
			return result;
		}
		String s = source;
		String[] a = s.split("[\\D]+");
		for (int i = 0; i < a.length; i++) {
			if (a[i].length() == num) {
				return a[i];
			}
		}
		return result;
	}

	public static String get8bitNumberInString(String source) {
		return getNbitNumberInString(source, 8);
	}

	public static String execStringFor8bit(String source) {
		String smsStr = source;
		String number = StringUtil.get8bitNumberInString(smsStr);
		if (StringUtil.isNotEmpty(number)) {
			String s[] = smsStr.split(number);
			String s2;
			if (s.length >= 2) {
				s2 = s[0] + "<font color='#e36a35'>" + number + "</font>" + s[1];
			} else {
				s2 = s[0] + "<font color='#e36a35'>" + number + "</font>";
			}

			smsStr = s2;
		}

		return smsStr;
	}

	/*
	 * 获取第一次出现的 数字并 返回
	 */
	public static String getFirstNumberInString(String source) {
		String result = "";
		String s = source;
		String[] a = s.split("[\\D]+");
		if (a != null && a.length >= 1) {
			return a[0];
		}
		return result;
	}

	/**
	 * 验证身份证号码
	 */
	public static boolean checkIdCardNo(String cardNo) {
		Pattern pattern = Pattern.compile("(^(\\d{14}\\w{1})|(\\d{17}\\w{1})$)");
		Matcher matcher = pattern.matcher(cardNo);
		return matcher.matches();
	}

	public static boolean checkResult(String result) {

		if (isBlank(result)) {
			return false;
		}

		if ("1".equals(result) || "success".equals(result) || "true".equals(result)) {
			return true;
		}

		return false;
	}

	public static boolean isValidUserName(String input) {
		if (isEmpty(input))
			return false;
		Pattern ptn_start = Pattern.compile("^[[a-z][A-Z]][[a-z][A-Z][0-9]_]{1,13}$");
		if (!ptn_start.matcher(input).matches()) {
			return false;
		}
		return true;
	}

	public static boolean isValidPassWord(String input) {
		if (isEmpty(input))
			return false;
		if (input.length() < 6 || input.length() > 20)
			return false;
		else {
			return true;
		}
		// Pattern ptn_all = Pattern.compile("^[[a-z][A-Z][0-9]_]{6,20}$");
		// return ptn_all.matcher(input).matches();
	}

	public static String getDateString3(long time) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		return sdf.format(new Date(time));
	}

	public static boolean isLocalUrl(String content) {
		if (content == null || content.startsWith("http")) {
			return false;
		} else {
			return true;
		}
	}

	public static int random(int min, int max) {
		int number = new Random().nextInt(max) + 1;
		if (number >= min) {
			return number;
		}
		number = number % min + min;
		return number;
	}
	
}
