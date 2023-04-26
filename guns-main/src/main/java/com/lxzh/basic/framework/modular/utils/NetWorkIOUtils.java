package com.lxzh.basic.framework.modular.utils;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NetWorkIOUtils {
	  
	 
	
		public static void main(String[] args) {
			
			String dString2 = "01";
			System.out.println(dString2.substring(0, 1));
			System.out.println(dString2.substring(1, 2));

			
// 			String dString = "48 3A 01 57 01 01 00 00 00 00 00 00 DC 45 44";
//			String dString = "48 3A 01 53 AA AA AA AA AA AA AA AA 26 45 44";   
 			String dString = "48 3A 01 52 00 00 00 00 00 00 00 00 D5 45 44";   
 			
			byte[] inBuff = hexStrToByteArray(dString);
  
 			System.out.print("[");
			for (byte b : inBuff) {
				String dInteger= Integer.toHexString(Byte.toUnsignedInt(b));
				System.out.print(dInteger.length()==1?("0"+dInteger).toUpperCase():dInteger.toUpperCase());
				System.out.print(" ");
			}
			System.out.print("]");
			System.out.println(" ");
			try {
				// 创建连接对像
				Socket socket = new Socket("192.168.1.252", 1031);
				OutputStream out = socket.getOutputStream();
				out.write(inBuff);
				InputStream in = socket.getInputStream();
				
				byte[] buf2 = new byte[20];
				int length = in.read(buf2);
				System.out.println("结果=" +length  );

				System.out.println("");
				for (byte b : buf2) {
					String dInteger= Integer.toHexString(Byte.toUnsignedInt(b)).toUpperCase();
					System.out.print(dInteger.length()==1?("0"+dInteger):dInteger);
					System.out.print(" ");
				}
				System.out.println("");


				socket.close();
				 

			} catch (Exception e) {
				 e.printStackTrace();
			}

			
		}
 
		 
		
		/**
		 * 方式1
		 * @param ip
		 * @param port
		 * @param msg
		 * @return
		 */
		public static List<String> send(String ip,Integer port) {
  
 			byte[] inBuff = hexStrToByteArray("48 3A 01 52 00 00 00 00 00 00 00 00 D5 45 44");
 		 	try {
				Socket socket = new Socket(ip, port);
				OutputStream out = socket.getOutputStream();
				out.write(inBuff);
				InputStream in = socket.getInputStream();
				
				byte[] buf2 = new byte[16];
				in.read(buf2);
 
				List<String> resultList = new ArrayList<String>();
 				System.out.println("--------------------------------------");
				for (byte b : buf2) {
					String dInteger= Integer.toHexString(Byte.toUnsignedInt(b));
					System.out.print(dInteger.length()==1?("0"+dInteger):dInteger);
					System.out.print(" ");
					resultList.add(dInteger.length()==1?("0"+dInteger):dInteger);
				}
				
				System.out.println("");
				socket.close();
				 
				return resultList;
			} catch (Exception e) {
				 e.printStackTrace();
			}
			return null;
			
		}

		

		   public static String getCRC(byte[] bytes) {
		        // CRC寄存器全为1
		        int CRC = 0x0000ffff;
		        // 多项式校验值
		        int POLYNOMIAL = 0x0000a001;
		        int i, j;
		        for (i = 0; i < bytes.length; i++) {
		            CRC ^= ((int) bytes[i] & 0x000000ff);
		            for (j = 0; j < 8; j++) {
		                if ((CRC & 0x00000001) != 0) {
		                    CRC >>= 1;
		                    CRC ^= POLYNOMIAL;
		                } else {
		                    CRC >>= 1;
		                }
		            }
		        }
		        // 结果转换为16进制
		        String result = Integer.toHexString(CRC).toUpperCase();
		        if (result.length() != 4) {
		            StringBuffer sb = new StringBuffer("0000");
		            result = sb.replace(4 - result.length(), 4, result).toString();
		        }
		        //高位在前地位在后
		        return result.substring(2, 4) + result.substring(0, 2);
		        // 交换高低位，低位在前高位在后
//		        return result.substring(2, 4) + result.substring(0, 2);
		    }
		   
		   
		   
		   
		 
		
		public static byte sumCheck(byte[] b, int len) {
			int sum = 0;
			for (int i = 0; i < len; i++) {
				sum = sum + b[i];
			}
//	         if(sum > 0xff){ //超过了255，使用补码（补码 = 原码取反 + 1）
//	             sum = ~sum;
//	             sum = sum + 1;
//	         }
			return (byte) (sum & 0xff);
		}
		
		public static byte[] hexStrToByteArray(String str)
		{
		    if (str == null) {
		        return null;
		    }
		    if (str.length() == 0) {
		        return new byte[0];
		    }
		    
		    str=str.replace("[", "");
		    str=str.replace("]", "");
		    str=str.replace(" ", "");

		    byte[] byteArray = new byte[str.length() / 2 ];
		    for (int i = 0; i < byteArray.length; i++){
		        String subStr = str.substring(2 * i, 2 * i + 2);
		        byteArray[i] = ((byte)Integer.parseInt(subStr, 16));
		    }
		    return byteArray;
		}


 		
		
		
	 
}
