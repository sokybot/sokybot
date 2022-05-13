package org.soky.bot.pk2;

import java.util.Arrays;
import java.util.Stack;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.databind.deser.DataFormatReaders.Match;

public class Drivre {

	public static void main(String args[]) {

		// ([\\\\]+(?![^\\(]*\\))) this is work
		// ((?<![\\(])(\\\\{1})(?<![\\)])) this work fine
		
		// \(([^\)]+)\)  will match any grouping of parens that do not, themselves, contain parens
	//	Pattern p = Pattern.compile("([\\\\]{1})(?!([\\\\]+(?![^\\(]*\\))))");

		//String path = "xxx\\aa(\\)aa\\aasd\\tt(tt\\tt\\ss(rr\\rr))tt";
		//String[] arr = path.split("((?!(.*\\())(\\\\{1})(?!(.*\\))))");

		//System.out.println(Arrays.toString(arr));
//		Matcher match = p.matcher(path);

	//	while (match.find()) {
			//System.out.println("Start is : " + match.start());
			//String comp = StringUtils.substring(path, match.start(), match.end());
	    	//System.out.println(comp);
		//}

		//String path = "\\xxx\\aa(\\)aa\\aasd\\tt(tt\\tt\\ss(rr\\rr))tt\\";
		String path = "\\aasd(\\asd)\\(\\sss)aa\\archemy_astral(\\ttt).ddj" ; 
		
		Stack<String> stack = getPathStack(path) ; 
		
	String fileName = stack.remove(0) ; 
	System.out.println("File Name : " + fileName) ; 
	System.out.println("Stack Top : " + stack.peek()) ; 
	
	}


	private  static Stack<String> getPathStack(String path) { 
		
		Stack<String> pathStack = new Stack<>() ; 
		Stack<String> nestedFlag = new Stack<>() ; 

		
		String pathComp = "" ;char c; 
		for (int i = path.length() - 1; i >= 0 ; i--) {
			c = path.charAt(i);
			switch (c) {
			case '\\' : 
				
				if(nestedFlag.isEmpty()) { 
				   if(!pathComp.isBlank()) { 
					   pathStack.push(pathComp) ; 
					   pathComp = "" ;
				   }
				}else { 
					pathComp = c + pathComp ; 
				}
				break ; 
			case ')':
				nestedFlag.push(c+"") ;
				pathComp = c + pathComp ;
				break ;  
			case '(': 
				nestedFlag.pop() ; 
			    pathComp = c + pathComp ; 
			    break ; 
			default: 
				pathComp = c + pathComp ; 
			}
		}
		if(!pathComp.isBlank()) { 
			pathStack.push(pathComp) ; 
		}
		
		return pathStack ; 
				
	}

}
