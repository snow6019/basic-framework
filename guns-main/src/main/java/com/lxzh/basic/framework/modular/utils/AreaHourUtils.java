package com.lxzh.basic.framework.modular.utils;

public class AreaHourUtils {

	
	
    public static void main(String[] args) {
 
//    	name("100000000000000000010000", "000000000000000000011000");
    	
    	hourToByte(0, 11);
    }

    
    
    public static void name(String str1,String str2) {
		
    	for (int i = 0; i < 24; i++) {
    	//	System.out.println(str1.substring(i, i+1)  +"----"+  str2.substring(i, i+1));
    		if (str1.substring(i, i+1).equals(str2.substring(i, i+1))) {
				System.out.println(i+"-"+(i+1)+"相同");
			}else {
				System.out.println(i+"-"+(i+1)+"不相同");
			}
		}
    	
	}
    
    public static String hourToByte(Integer a,Integer b) {
		
    	StringBuffer dBuffer =new StringBuffer("-000000000000000000000000");
    	for (int i = a; i < b; i++) {			 
    		dBuffer= dBuffer.replace(i, i+1, "1");
		}
    	return dBuffer.toString();
     		
	}
}
