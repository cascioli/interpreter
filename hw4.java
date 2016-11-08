package hw4;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Stack;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class hw4 {
	
	public static boolean isString(String input){
		if(input.indexOf("\"") != -1){
			return true;
		}
		return false;
	}
	
	public static Stack<HashMap<String,String>> copyEnv(Stack<HashMap<String,String>> env){
		Stack<HashMap<String,String>> newEnv = new Stack<HashMap<String,String>>();
		for(int i = 0; i < env.size(); i++){
			for(HashMap<String,String> newMap : env)
			{
				newEnv.add((HashMap<String,String>)newMap.clone());
			}
		}
		return newEnv;
	}
	
	public static boolean isBool(String input, Stack<HashMap<String,String>> env){
		if(isVarName(input)){
			if(getVar(input, env).startsWith(":true:")  | getVar(input, env).startsWith(":false:")){
				return true;
			}else{
				return false;
			}
		}
		if(input.startsWith(":true:")  | input.startsWith(":false:")){
			return true;
		}else{
			return false;
		}
			
	}
	
	public static boolean isVarName(String input){
		if(input != "" && input.charAt(0) != '\"' && !isInteger(input) && input.charAt(0) != ':'){
			return true;
		}else{
			return false;
		}
	}
	public static boolean checkFunc(String str, HashMap<String,Closure> functs){
		if(functs.containsKey(str))
			return true;
		else
			return false;
	}
	
	public static String getVar(String varName, Stack<HashMap<String,String>> env){
		ArrayList<HashMap<String,String>> tempStorage= new ArrayList<HashMap<String,String>>();
		HashMap<String, String> current;
		while(!env.isEmpty()){//check environments
			current = env.pop();
			tempStorage.add(current);
			if(current.containsKey(varName)){
				for(int i = tempStorage.size()-1; i>=0;i--){
					env.push(tempStorage.get(i));
				}
				return current.get(varName);//bind new variable with old
			}
		}
		for(int i = tempStorage.size()-1; i>=0;i--){
			env.push(tempStorage.get(i));
		}
		return  "~~~";
	}
	
	public static boolean convertBool(String input, Stack<HashMap<String,String>> env){
		if(input.startsWith(":false:") || getVar(input, env).startsWith(":false:")){
			return false;
		}else{
			return true;
		}
	}
	
	public static boolean canBool(Stack<String> s, Stack<HashMap<String,String>> env){
		String tempA,tempB;
		if(s.size() < 2){
			return false;
		}else{
			tempA = s.pop();
			tempB = s.pop();
		}
		
		if((isBool(tempA, env) ||isBool(getVar(tempA, env), env)) && (isBool(tempB, env) || isBool(getVar(tempB, env), env))){
			s.push(tempB);
			s.push(tempA);
			return true;
		}

		s.push(tempB);
		s.push(tempA);
		return false;
	}
	
	public static boolean isInteger(String input){
		try{
			Integer.parseInt(input);
			return true;
		}
		catch(Exception e){
			return false;
		}
	}

	public static boolean canOp(Stack<String> s, Stack<HashMap<String,String>> env){
		String tempA,tempB;
		if(s.size() < 2){
			return false;
		}else{
			tempA = s.pop();
			tempB = s.pop();
		}
		
		if((isInteger(tempA) || isInteger(getVar(tempA, env))) && (isInteger(tempB) || isInteger(getVar(tempB, env)))){
			s.push(tempB);
			s.push(tempA);
			return true;
		}

		s.push(tempB);
		s.push(tempA);
		return false;
	}
	
	public static String operate(String tempA, String tempB, String op, Stack<HashMap<String,String>> env){
		if(isVarName(tempA))
			tempA = getVar(tempA, env);
		if(isVarName(tempB))
			tempB = getVar(tempB, env);
		int a = Integer.parseInt(tempA);
		int b = Integer.parseInt(tempB);
		switch(op){
			case "add":
				return String.valueOf(a+b);
			case "sub":
				return String.valueOf(b-a);
			case "mul":
				return String.valueOf(a*b);
			case "div":
				return String.valueOf(b/a);
			case "rem":
				return String.valueOf(b%a);
			case "equal":
				if(a == b){
					return ":true:";
				}else{
					return ":false:";
				}
			case "lessThan":
				if(b < a){
					return ":true:";
				}else{
					return ":false:";
				}
		}
		return "operator error";
	}

	public static void processLine(String line, BufferedReader re, PrintWriter pr, Stack<String> stack, 
			Stack<HashMap<String,String>> env, HashMap<String,Closure> functs, Stack<Stack<String>> callStack, Stack<HashMap<String, String>> funcEnv){ 
		//env.push(new HashMap<String,String>());
	    String command, value;
		int space = line.indexOf(" ");
    	if(space != -1){
    		command = line.substring(0, space);
    		value = line.substring(space+1,line.length());
    	}else{
    		command = line;
    		value = "";
    	}
    	switch(command){
    		case "push":
    			if(isInteger(value)){//check if it is a number
    				if(value.charAt(0) == '-' && value.charAt(1) == '0'){
    					stack.push("0");
    				}else{
    					stack.push(value);
    				}
    			}else if(value.indexOf("\"")!= -1){ //check if it is a string with quotes
		    		stack.push(value);
		    	}else if(!isInteger(value.substring(0,1))){ //check if it is a valid name
		    		stack.push(value);
		    	}else{
    				stack.push(":error:");
    			}
    			
    			break;
    		case "pop":
    			if(stack.empty()){
    				stack.push(":error:");
    			}else{
    				stack.pop();
    			}
    			break;
    		case "quit":
    			while(!stack.isEmpty()){
    				if(stack.peek().startsWith("\"")){
    					String temp = stack.pop();
    					pr.println(temp.substring(1, temp.length()-1));
    				}else{
    					pr.println(stack.pop());
    				}
    			}
    			break;
    		case ":true:":
    			stack.push(":true:");
    			break;
    		case ":false:":
    			stack.push(":false:");
    			break;
    		case ":error:":
    			stack.push(":error:");
    			break;
    		case "add":
    			if(canOp(stack, env)){
    				stack.push(operate(stack.pop(), stack.pop(),command, env));
    			}else{
    				stack.push(":error:");
    			}
    			break;
    		case "sub":
    			if(canOp(stack, env)){
    				stack.push(operate(stack.pop(), stack.pop(),command, env));
    			}else{
    				stack.push(":error:");
    			}
    			break;
    		case "mul":
    			if(canOp(stack, env)){
    				stack.push(operate(stack.pop(), stack.pop(),command, env));
    			}else{
    				stack.push(":error:");
    			}
    			break;
			case "div":
				if(canOp(stack, env)){
					String a = stack.pop();
                    String b = stack.pop();
                    if (a.charAt(0) == '0' || getVar(a, env).charAt(0) == '0'){
                        stack.push(b);
                        stack.push(a);
                        stack.push(":error:");
                    }else{
                    	stack.push(operate(a, b,command, env));
                    }
    			}else{
    				stack.push(":error:");
    			}
    			break;
			case "rem":
				if(canOp(stack, env)){
					String a = stack.pop();
                    String b = stack.pop();
                    if (a.charAt(0) == '0' || getVar(a, env).charAt(0) == '0'){
                        stack.push(b);
                        stack.push(a);
                        stack.push(":error:");
                    }else{
                    	stack.push(operate(a, b,command, env));
                    }
            	}else{
    				stack.push(":error:");
    			}
    			break;
			case "neg":
				if(!stack.empty()){
					String temp = stack.pop();
					if(isInteger(temp)){
						int a = Integer.parseInt(temp);
						a= 0-a;
						stack.push(String.valueOf(a));
					}else if(isInteger(getVar(temp, env))){
						int a = Integer.parseInt(getVar(temp, env));
						a = 0-a;
						stack.push(String.valueOf(getVar(temp, env)));
					}else{
						stack.push(temp);
						stack.push(":error:");
					}
				}else{
					stack.push(":error:");
				}
				break;
			case "swap":
				String tempA = "", tempB = "";
				if(stack.size()<2){
					stack.push(":error:");
				}else{
					tempA = stack.pop();
					tempB = stack.pop();
					stack.push(tempA);
					stack.push(tempB);
				}
				break;
			case "and":
				if(canBool(stack, env)){
					boolean one = convertBool(stack.pop(), env), two = convertBool(stack.pop(), env);
					if(one && two){
						stack.push(":true:");
					}else {
						stack.push(":false:");
					}
				}else{
					stack.push(":error:");
				}
				break;
			case "or":
				if(canBool(stack, env)){
					boolean one = convertBool(stack.pop(), env), two = convertBool(stack.pop(), env);
					if(one | two){
						stack.push(":true:");
					}else {
						stack.push(":false:");
					}
				}else{
					stack.push(":error:");
				}
				break;
			case "not":
				if(!stack.empty()){
					String temp = stack.pop();
					if(temp.startsWith(":true:") || getVar(temp, env).startsWith(":true:")){
						stack.push(":false:");
					}else if(temp.startsWith(":false:") || getVar(temp, env).startsWith(":false:")){
						stack.push(":true:");
					}else{
						stack.push(temp);
						stack.push(":error:");
					}
				}else{
					stack.push(":error:");
				}
				break;
			case "equal":
				if(canOp(stack, env)){
					stack.push(operate(stack.pop(), stack.pop(),command, env));
				}else{
					stack.push(":error:");
				}
				break;
			case "lessThan":
				if(canOp(stack, env)){
					stack.push(operate(stack.pop(), stack.pop(),command, env));
				}else{
					stack.push(":error:");
				}
				break;
			case "bind":
				HashMap<String, String> current = env.peek();
				if(stack.size()<2){//check if stack is full enough
					stack.push(":error:");
				}else if(!stack.peek().startsWith(":error:")){//check if data is error
					String data= stack.pop();
					String varName= stack.pop();
					if(isVarName(varName)){  //makes sure varName is legal
						if(isVarName(data)){ //check if data is another variable.
							String rawData = getVar(data, env);
							if(rawData != "~~~"){
								current.put(varName,rawData);
								stack.push(":unit:");
							}else{
								stack.push(varName);
								stack.push(data);
								stack.push(":error:");
							}
						}else{//must be a bind-able value
							current.put(varName,data);
							stack.push(":unit:");
						}
					}else{
						stack.push(varName);
						stack.push(data);
						stack.push(":error:");
					}
				}else{
					stack.push(":error:");
				}
				break;
			case "let":
				stack.push("~let");
				env.push(new HashMap<String,String>());
				break;
			case "end":
				String temp = "";
				if(!stack.empty() && !stack.peek().startsWith("~let"))
					temp = stack.pop();
				while(!stack.peek().startsWith("~let")){
					stack.pop();
				}
				stack.pop();
				if(temp != ""){
					stack.push(temp);
				}
				Set<String> keys =env.pop().keySet();
				Iterator<String> it=keys.iterator();
				while(it.hasNext()){
					String name = it.next();
					if(functs.containsKey(name)){
						functs.remove(name);
					}
				}
				
				break;
			case "if":
				if(stack.size() <3){
					stack.push(":error:");
				}else{
					String valB = stack.pop();
					String valA = stack.pop();
					String tempBool = stack.pop();
					if(!isBool(tempBool, env)){
						stack.push(tempBool);
						stack.push(valA);
						stack.push(valB);
						stack.push(":error:");
					}else if(tempBool.startsWith(":true:") || getVar(tempBool, env).startsWith(":true:")){
						stack.push(valB);
					}else if(tempBool.startsWith(":false:") || getVar(tempBool, env).startsWith(":false:")){
						stack.push(valA);
					}
				}
				break;
			case "inOutFun":
				space = value.indexOf(" ");
				command = value.substring(0, space);
		    	value = value.substring(space+1,value.length());
		    	
			try {
				ArrayList<String> func = new ArrayList<String>();
				while (!(line = re.readLine()).startsWith("funEnd"))
			    {	
					func.add(line);
			    }
				Stack<HashMap<String,String>> env2 = copyEnv(env);
				functs.put(command, new Closure(func,env2,value,true));
				env.peek().put(command, command);
				stack.push(":unit:");
			} catch (IOException e) {
				e.printStackTrace();
			}
				break;
			case "fun":
				space = value.indexOf(" ");
				command = value.substring(0, space);
		    	value = value.substring(space+1,value.length());
		    	ArrayList<String> func = new ArrayList<String>();
			try {
				while (!(line = re.readLine()).startsWith("funEnd"))
			    {	
					func.add(line);
			    }
			} catch (IOException e) {
				e.printStackTrace();
			}
				Stack<HashMap<String,String>> env2 = copyEnv(env);
				functs.put(command, new Closure(func,env2,value, false));
				env.peek().put(command, command);
				stack.push(":unit:");
				break;
			case "call":
				if(stack.size()<2){
					stack.push(":error:");
				}else{
					String funName = stack.pop();
					String var = stack.pop();
					callStack.push(new Stack<String>());
					if(functs.containsKey(getVar(funName, env)) && !var.startsWith(":error:")){
						String temp2= funcCall(functs.get(getVar(funName, env)),var,re,pr,stack,env,functs, callStack, funcEnv);
						if(!funcEnv.isEmpty()){
							funcEnv.pop();
						}
						callStack.pop();
						if(temp2 != ""){
							stack.push(temp2);
						}
					}else if(functs.containsKey(funName) && !var.startsWith(":error:")){
						String temp2= funcCall(functs.get(funName),var,re,pr,stack,env,functs, callStack, funcEnv);
						if(!funcEnv.isEmpty()){
							funcEnv.pop();
						}
						callStack.pop();
						if(temp2 != ""){
							stack.push(temp2);
						}
					}else{
						stack.push(var);
						stack.push(funName);
						stack.push(":error:");
					}
				}				
    	}
	}
		
	public static String funcCall(Closure clos, String argument, BufferedReader re, PrintWriter pr, Stack<String> stack, 
			Stack<HashMap<String,String>> env, HashMap<String,Closure> functs, Stack<Stack<String>> callStack, Stack<HashMap<String, String>> funcEnv){
		ArrayList<String> funcCode =clos.code;
		if(funcEnv.isEmpty()){
			funcEnv = copyEnv(clos.env);
		}else{
			funcEnv.push(new HashMap<String, String>());
		}
		if(isVarName(argument) && getVar(argument, env) != "~~~"){
			funcEnv.peek().put(clos.param, getVar(argument, env));
		}else{
			funcEnv.peek().put(clos.param, argument);
		}

		for(int i = 0; i < funcCode.size(); i++){
			if(funcCode.get(i).startsWith("return")){
				i = funcCode.size();
				if(clos.inOut){
					env.peek().put(argument,funcEnv.peek().get(clos.param));
				}
				if(isVarName(callStack.peek().peek()) && getVar(callStack.peek().peek(), funcEnv) != "~~~"){
					return getVar(callStack.peek().pop(), funcEnv);
				}else{
					return callStack.peek().pop();
				}
			}else{				
				processLine(funcCode.get(i),re,pr,callStack.peek(), funcEnv, functs, callStack, funcEnv);
			}
		}
		if(clos.inOut){
			env.peek().put(argument,funcEnv.peek().get(clos.param));
		}
		return "";
	}
	
	public static void hw4(String inFile,String outFile){
		try
		  {
		    BufferedReader re = new BufferedReader(new FileReader(inFile));
		    PrintWriter pr = new PrintWriter(new FileWriter(outFile,false));
		    Stack<String> stack = new Stack<String>();
		    HashMap<String,Closure> functs = new HashMap<String,Closure>();
		    Stack<HashMap<String,String>> env= new Stack<HashMap<String,String>>();
		    Stack<Stack<String>> callStack = new Stack<Stack<String>>();
		    Stack<HashMap<String, String>> funcEnv = new Stack<HashMap<String,String>>();
			env.push(new HashMap<String,String>());
		    String line;
		    
		    while ((line = re.readLine()) != null){	
		    	processLine(line,re,pr,stack,env,functs, callStack, funcEnv);
		    } 
		    re.close();
		    pr.close();
		  }
		  catch (Exception e)
		  {
		    System.err.format("Exception occurred trying to read '%s'.", inFile);
		    e.printStackTrace();
		  }
	}
}
//used for functions
class Closure {
	Boolean inOut;
	ArrayList<String> code;
	Stack<HashMap<String,String>> env;
	String param;
	Closure(ArrayList<String> c, Stack<HashMap<String,String>> e, String p, Boolean io){
		code = c;
		env = e;
		param = p;
		inOut = io;
	}
}