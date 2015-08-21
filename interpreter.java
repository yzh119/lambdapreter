import java.util.*;
import java.math.*;
import java.io.*;
abstract class Type {
	Type() {}
	abstract void display();
	abstract boolean isNum();
	abstract boolean isFun();
	abstract boolean isBool();
	abstract boolean isStr();
	abstract boolean isChar();
	abstract boolean isPair();
	abstract boolean isNull();
	abstract boolean isQuote();
}

class Environment{
	Environment prev;
	HashMap <String, Type> definitions = new HashMap <String, Type>();
	Type find(String key) {
		Type inq = definitions.get(key);
		if (inq != null) return inq;
		if (prev == null) return new Null();
		return prev.find(key);
	}
	void add(String key, Type V) {
		definitions.put(key, V);
	}	
	Environment(Environment prv) {
		prev = prv;
	}
};

class TypeJudge {
	static public boolean isString(String x) {
		return (x.charAt(0) == '"');
	}

	static public boolean isNumber(String x) {
		if (x.length() == 1 && (x.charAt(0) < '0' || x.charAt(0) > '9')) return false;
		if (x.charAt(0) == '#' && x.charAt(1) == 'e') x = x.substring(2);
		if (x.charAt(0) == '-') x = x.substring(1);
		for (int i = 0; i < x.length(); i++) {
			if (x.charAt(i) != '/' && x.charAt(i) != '.' && (x.charAt(i) < '0' || x.charAt(i) > '9')) return false;
		}
		return true;
	}

	static public boolean isBoolean(String x) {
		if (x.equals("#f") || x.equals("#t")) return true;
		return false;
	}
	
	static public boolean isCharacter(String x) {
		if (x.length() == 3)
			if (x.charAt(0) == '#' && x.charAt(1) == '\\') return true;
		return false;
	}

	static public int numberJudge(String str) {
		int l = str.length();
		if (str.charAt(0) == '#') return 1;		
		boolean flag = false;
		for (int i = 0; i < l; i++) 
			if (str.charAt(i) == '.') flag = true;
		if (flag) return 2;
		return 1;
	}
}

class Number extends Type{
	BigInteger num, den;
	double flt;
	int style;	 // 1, 2 
	// 1 为有理数 2 为浮点数
	void reduction() {
		switch (style) {
			case 2: break;
			case 1: {
				BigInteger g = num.gcd(den);
				num = num.divide(g);
				den = den.divide(g);}
				break;
		}
	}

	Number (BigInteger Num, BigInteger Den) {
		style = 1;
		num = Num;
		den = Den;		
	}

	Number (double Flt) {
		style = 2;
		flt = Flt;
	}

	Number (String str) {
		num = BigInteger.ZERO; den = BigInteger.ONE;
		flt = 0.0;
		style = TypeJudge.numberJudge(str);
		if (str.charAt(0) == '#') {
			String[] s = str.substring(2).split("\\.", 2);
			if (s.length > 1) {str = s[0] + s[1];
				den = BigInteger.TEN.pow(s[1].length());
			}
		}
		String [] s = str.split("/", 2);
		if (s.length > 1) den = new BigInteger(s[1]);
		str = s[0];
		switch (style) {
			case 1: num = new BigInteger(str); break;	
			case 2: flt = Double.parseDouble(str); break;
		}
		reduction();
	}

	void display() {
		switch (style) {
			case 1: 
				System.out.print(num);
				if (den.compareTo(BigInteger.ONE) != 0) {
					System.out.print("/");
					System.out.print(den);
				}
				break;
			case 2: 
				System.out.print(flt);
				break;
		}
	}

	boolean isNum() {return true;}
	boolean isFun() {return false;}
	boolean isChar() {return false;}
	boolean isBool() {return false;}
	boolean isStr() {return false;}
	boolean isPair() {return false; }
	boolean isNull() {return false;}
	boolean isQuote() {return false;}
}			

class Function extends Type{
	ArrayList <String> arguments = null;
	String extra = null;
	Environment env;
	int left, right;
	String mark;
	Function (ArrayList <String> args, String Extra, int l, int r, Environment Env, String Mark) {
		arguments = args;
		extra = Extra;
		left = l;
		right = r;
		env = Env;
		mark = Mark;
	}
	void display() {
		System.out.print("#:<procedure>");
	}

	boolean isNum() {return false;}
	boolean isFun() {return true;}
	boolean isChar() {return false;}
	boolean isBool() {return false;}
	boolean isStr() {return false;}
	boolean isPair() {return false;}
	boolean isNull() {return false;}
	boolean isQuote() {return false;}
}

class Str extends Type{
	String val;
	Str (String str) {
	   	val = str;
	}
	void display() {
		System.out.print(val);
	}
	boolean isNum() {return false;}
	boolean isFun() {return false;}
	boolean isChar() {return false;}
	boolean isBool() {return false;}
	boolean isStr() {return true;}
	boolean isPair() {return false;}
	boolean isNull() {return false;}
	boolean isQuote() {return false;}
}	

class Char extends Type{
	char val;
	Char (char x) {
		val = x;
	}
	void display() {
		System.out.print("#\\" + val);
	}
	boolean isNum() {return false;}
	boolean isFun() {return false;}
	boolean isChar() {return true;}
	boolean isBool() {return false;}
	boolean isStr() {return false;}
	boolean isPair() {return false;}
	boolean isNull() {return false;}
	boolean isQuote() {return false;}
}
class Pair extends Type{
	Type car, cdr;
	Pair (Type x, Type y) {
		car = x;
		cdr = y;
	}
	void displayPair(boolean showParentheses) {
		if (showParentheses) System.out.print("(");
		if (!car.isNull()) car.display();
		if (!cdr.isNull()) {
			System.out.print(" ");
			if (!cdr.isPair()) {
				System.out.print(". ");
				cdr.display();
			} else {
				((Pair)cdr).displayPair(false);
			}
		}
		if (showParentheses) System.out.print(")");
	}
	void display() {
		displayPair(true);
	}
	boolean isNum() {return false;}
	boolean isFun() {return false;}
	boolean isChar() {return false;}
	boolean isBool() {return false;}
	boolean isStr() {return false;}
	boolean isPair() {return true;}
	boolean isNull() {return false;}
	boolean isQuote() {return false;} 
}	

class Null extends Type{
	Null() {}
	void display() {
		System.out.print("()");
	}			
	boolean isNum() {return false;}
	boolean isFun() {return false;}
	boolean isChar() {return false;}
	boolean isBool() {return false;}
	boolean isStr() {return false;}
	boolean isPair() {return false;}
	boolean isNull() {return true;}
	boolean isQuote() {return false;}
}

class Quote extends Type{
	String val;
	Quote (String str) {
		val = str;
	}
	void display() {
		System.out.print(val);
	}
	boolean isNum() {return false;}
	boolean isFun() {return false;}
	boolean isChar() {return false;}
	boolean isBool() {return false;}
	boolean isStr() {return false;}
	boolean isPair() {return false;}			
	boolean isNull() {return false;}
	boolean isQuote() {return true;}
}

class Bool extends Type{
	boolean val;
	Bool (boolean x) {
		val = x;
	}
	void display() {
		if (val == true) System.out.print("#t");
			else System.out.print("#f");
	}
	boolean isNum() {return false;}
	boolean isFun() {return false;}
	boolean isChar() {return false;}
	boolean isBool() {return true;}
	boolean isStr() {return false;}
	boolean isPair() {return false;}
	boolean isNull() {return false;}
	boolean isQuote() {return false;}
}

public class interpreter {
	static int maxSize = 1000000;
	static String[] elements = new String[maxSize];
	static int[] match = new int[maxSize];
	static int tail = 0;
	static boolean inparenthese = false;
	static Environment env0 = new Environment(null);

	static void printProcedureList() {
		System.out.println(tail);
		for (int i = 0; i < tail; i++) {
			System.out.println(i + " " + elements[i] + " " + match[i]);
		}
		System.out.println();
	}	
	
	static Number add(Number a, Number b) {
		if (a.style == 2) {
			if (b.style == 2) {
				return new Number(a.flt + b.flt);							
			}
			double flt = (1.0 * b.num.intValue()) / (1.0 * b.den.intValue());
			return new Number(a.flt + flt);
		}
		if (a.style == 1) 
			if (b.style == 1) {
				BigInteger nNum = a.num.multiply(b.den).add(a.den.multiply(b.num));
				BigInteger nDen = a.den.multiply(b.den);
				BigInteger g = nNum.gcd(nDen);
				return new Number(nNum.divide(g), nDen.divide(g));
			}
		double flt = (1.0 * a.num.intValue()) / (1.0 * a.den.intValue());
		return new Number(flt + b.flt);
	}

	static Number addList(ArrayList <Type> lst) {
		Number ret = new Number("0");
		for (Type x: lst)
			ret = add(ret, (Number)x);
		return ret;
	}

	static Number sub(Number a, Number b) {
		if (a.style == 2) {
			if (b.style == 2) {
				return new Number(a.flt - b.flt);
			}
			double flt = (1.0 * b.num.intValue()) / (1.0 * b.den.intValue());
			return new Number(a.flt - flt);
		}
		if (a.style == 1) 
			if (b.style == 1) {
				BigInteger nNum = a.num.multiply(b.den).subtract(a.den.multiply(b.num));
				BigInteger nDen = a.den.multiply(b.den);
				BigInteger g = nNum.gcd(nDen);
				return new Number(nNum.divide(g), nDen.divide(g));
			}
		double flt = (1.0 * a.num.intValue()) / (1.0 * a.den.intValue());
		return new Number(flt - b.flt);
	}

	static Number subtList(ArrayList <Type> lst) {
		if (lst.size() == 1) return sub(new Number("0"), (Number)lst.get(0));
		Number ret = (Number)lst.get(0);
		for (int i = 1; i < lst.size(); i++)
			ret = sub(ret, (Number)lst.get(i));
		return ret;
	}

	static Number mul(Number a, Number b) {
		if (a.style == 2) {
			if (b.style == 2) {
				return new Number(a.flt * b.flt);
			}
			double flt = (1.0 * b.num.intValue()) / (1.0 * b.den.intValue());
			return new Number(a.flt * flt);
		}	
		if (a.style == 1) 
			if (b.style == 1) {
				BigInteger nNum = a.num.multiply(b.num);
				BigInteger nDen = a.den.multiply(b.den);
				BigInteger g = nNum.gcd(nDen);
				return new Number(nNum.divide(g), nDen.divide(g));
			}
		double flt = (1.0 * a.num.intValue()) / (1.0 * a.den.intValue());
		return new Number(flt * b.flt);
	}
	
	static Number mulList(ArrayList <Type> lst) {
		Number ret = new Number("1");
		for (Type x: lst)
			ret = mul(ret, (Number)x);
		return ret;
	}

	static Number div(Number a, Number b) {
		if (a.style == 2) {
			if (b.style == 2) {
				return new Number(a.flt / b.flt);
			}
			double flt = (1.0 * b.num.intValue()) / (1.0 * b.den.intValue());
			return new Number(a.flt / flt);
		}
		if (a.style == 1) 
			if (b.style == 1) {
				BigInteger nNum = a.num.multiply(b.den);
				BigInteger nDen = a.den.multiply(b.num);
				BigInteger g = nNum.gcd(nDen);
				return new Number(nNum.divide(g), nDen.divide(g));
			}
		double flt = (1.0 * a.num.intValue()) / (1.0 * a.den.intValue());
		return new Number(flt / b.flt);
	}
	
	static Number divList(ArrayList <Type> lst) {
		if (lst.size() == 1) return div(new Number("1"), (Number)lst.get(0));
		int n = lst.size(); 
		Number ret = (Number)lst.get(0);
		for (int i = 1; i < n; i++)
			ret = div(ret, (Number)lst.get(i));
		return ret;
	}

	static Number quotient(Number a, Number b) {
		return new Number(a.num.divide(b.num), BigInteger.ONE);
	}	

	static Number modulo(Number a, Number b) {
		return new Number(a.num.remainder(b.num), BigInteger.ONE);
	}

	static Bool and(Bool a, Bool b) {
		return new Bool(a.val && b.val);
	}
	
	static Bool andList(ArrayList <Type> lst) {
		Bool ret = new Bool(true);
		for (Type x: lst)
			ret = and(ret, (Bool)x);
		return ret;
	}
	
	static Bool or(Bool a, Bool b) {
		return new Bool(a.val || b.val);
	}

	static Bool orList(ArrayList <Type> lst) {
		Bool ret = new Bool(false);
		for (Type x: lst)
			ret = or(ret, (Bool)x);
		return ret;
	}
	
	static Bool not(Bool x) {
		return new Bool(!x.val);
	}

	static Bool equal(Type a, Type b) {
		boolean ret = false;
		if (a.isNum() && b.isNum()) {
			if (((Number)a).style == 1 && ((Number)b).style == 1)
				if (((Number)a).num.equals(((Number)b).num) && ((Number)a).den.equals(((Number)b).den)) ret = true;
			if (((Number)a).style == 2 && ((Number)b).style == 2) 
				ret = (((Number)a).flt == ((Number)b).flt);
			return new Bool(ret);
		}
		if (a.isBool() && b.isBool()) {
			if (((Bool)a).val == ((Bool)b).val) ret = true;
		}
		if (a.isStr() && b.isStr()) {
			if (((Str)a).val.equals(((Str)b).val)) ret = true;
		}
		if (a.isPair() && b.isPair()) {
			return and(equal(((Pair)a).car, ((Pair)a).car), equal(((Pair)a).cdr, ((Pair)b).cdr));
		}
		if (a.isNull() && b.isNull()) {
			ret = true;
		}
		if (a.isQuote() && b.isQuote()) {
			if (((Quote)a).val.equals(((Quote)b).val)) ret = true;
		}
		if (a.isFun() && b.isFun()) {
			if (a == b) ret = true;
		}
		if (a.isChar() && b.isChar()) {
			if (((Char)a).val == ((Char)b).val) ret = true;
		}
		return new Bool(ret);
	}

	static Bool greater(Number a, Number b) {
		if (a.style == 1 && b.style == 1) {
			if (a.num.multiply(b.den).compareTo(a.den.multiply(b.num)) > 0) return new Bool(true);
				else return new Bool(false);
		}
		if (a.style == 2 && b.style == 2) {
			if (a.flt > b.flt) return new Bool(true); else return new Bool(false);
		}
		if (a.style == 1) {
			double aflt = (1.0 * a.num.intValue()) / (1.0 * a.den.intValue());
			return new Bool(aflt > b.flt);
		}
		double bflt = (1.0 * b.num.intValue()) / (1.0 * b.num.intValue());
		return new Bool(a.flt > bflt); 
	}

	static Bool less(Number a, Number b) {
		if (a.style == 1 && b.style == 1) {
			if (a.num.multiply(b.den).compareTo(a.den.multiply(b.num)) < 0) return new Bool(true);
				else return new Bool(false);
		}
		if (a.style == 2 && b.style == 2) {
			if (a.flt < b.flt) return new Bool(true); else return new Bool(false);
		}
		if (a.style == 1) {
			double aflt = (1.0 * a.num.intValue()) / (1.0 * a.den.intValue());
			return new Bool(aflt < b.flt);
		}
		double bflt = (1.0 * b.num.intValue()) / (1.0 * b.num.intValue());
		return new Bool(a.flt < bflt); 
	}
	
	static Bool greaterOrEqual(Number a, Number b) {
		if (a.style == 1 && b.style == 1) {
			if (a.num.multiply(b.den).compareTo(a.den.multiply(b.num)) >= 0) return new Bool(true);
				else return new Bool(false);
		}
		if (a.style == 2 && b.style == 2) {
			if (a.flt >= b.flt) return new Bool(true); else return new Bool(false);
		}
		if (a.style == 1) {
			double aflt = (1.0 * a.num.intValue()) / (1.0 * a.den.intValue());
			return new Bool(aflt >= b.flt);
		}
		double bflt = (1.0 * b.num.intValue()) / (1.0 * b.num.intValue());
		return new Bool(a.flt >= bflt); 
	}

	static Bool lessOrEqual(Number a, Number b) {
		if (a.style == 1 && b.style == 1) {
			if (a.num.multiply(b.den).compareTo(a.den.multiply(b.num)) <= 0) return new Bool(true);
				else return new Bool(false);
		}
		if (a.style == 2 && b.style == 2) {
			if (a.flt <= b.flt) return new Bool(true); else return new Bool(false);
		}
		if (a.style == 1) {
			double aflt = (1.0 * a.num.intValue()) / (1.0 * a.den.intValue());
			return new Bool(aflt <= b.flt);
		}
		double bflt = (1.0 * b.num.intValue()) / (1.0 * b.num.intValue());
		return new Bool(a.flt <= bflt); 
	}

	static Bool eq(Type a, Type b) {
		if (a.isPair() && b.isPair()) {
			return new Bool(a == b);
		}
		if (a.isStr() && b.isStr()) {
			return new Bool(a == b);
		}
		return equal(a, b);
	}
	static Bool eqv(Type a, Type b) {
		return eq(a, b);
	}

	static Pair cons(Type a, Type b) {
		return new Pair(a, b);
	}	

	static Type list(ArrayList <Type> lst) {
		int n = lst.size();
		if (n == 0) return new Null();
		Type ret = new Null();
		for (int i = n - 1; i >= 1; i--) {
			ret = new Pair(lst.get(i), ret);
		}
		return new Pair(lst.get(0), ret);
	}
	
	static Type evalQuote(int l, int r) {
		if (l == r) {
			if (TypeJudge.isNumber(elements[l])) return new Number(elements[l]);
			if (TypeJudge.isString(elements[l])) return new Str(elements[l].substring(1, elements[l].length() - 1));
			if (TypeJudge.isCharacter(elements[l])) return new Char(elements[l].charAt(2));
			if (TypeJudge.isBoolean(elements[l])) {
				if (elements[l].charAt(1) == 't') return new Bool(true);
				return new Bool(false);
			}
			return new Quote(elements[l]);
		}
		l++; r--;
		ArrayList <Type> contents = new ArrayList <Type> ();
		for (int i = l; i <= r; i = match[i] + 1) {
			contents.add(evalQuote(i, match[i]));
		}
		return list(contents);
	}
	
	static Type append(Type a, Type b) {
		if (a.isNull()) return b;
		return new Pair(((Pair)a).car, append(((Pair)a).cdr, b));
	}

	static Type appendList(ArrayList <Type> lst) {
		int n = lst.size();
		Type ret = new Null();
		if (n != 1) ret = lst.get(n - 1);
		for (int i = n - 2; i >= 1; i--) {
			ret = append(lst.get(i), ret);
		}
		return append(lst.get(0), ret);
	}

	static ArrayList <Type> argsList(int l, int r, Environment envNow) {
		ArrayList <Type> ret = new ArrayList <Type>();
		for (int i = l; i <= r; i = match[i] + 1) {
			ret.add(eval(i, match[i], envNow));
		}
		return ret;
	}

	static Function defFunc(int argsl, int argsr, int exel, int exer, Environment envNow) {
		ArrayList <String> args = new ArrayList <String> ();
		String ext = null;
		for (int i = argsl; i <= argsr; i++) {
			if (elements[i].equals(".")) {ext = elements[i + 1]; break;}
			args.add(elements[i]);
		}
		return new Function(args, ext, exel, exer, envNow, null);
	}

	static Type apply(Function f, ArrayList <Type> args) {
		if (f.mark == null) {
			Environment env1 = new Environment(f.env);
			for (int i = 0; i < f.arguments.size(); i++) {
				env1.add(f.arguments.get(i), args.get(i));
			}
			if (f.extra != null) {
				ArrayList <Type> extraList = new ArrayList <Type> ();
				for (int i = f.arguments.size(); i < args.size(); i++) 
					extraList.add(args.get(i));
				env1.add(f.extra, list(extraList));
			}		
			return eval(f.left, f.right, env1);
		} else {
			if (f.mark.equals("display")) {args.get(0).display();}
			if (f.mark.equals("newline")) {System.out.println();}
			if (f.mark.equals("=")) return equal(args.get(0), args.get(1));
			if (f.mark.equals(">")) return greater((Number)args.get(0), (Number)args.get(1));
			if (f.mark.equals("<")) return less((Number)args.get(0), (Number)args.get(1));
			if (f.mark.equals(">=")) return greaterOrEqual((Number)args.get(0), (Number)args.get(1));
			if (f.mark.equals("<=")) return lessOrEqual((Number)args.get(0), (Number)args.get(1));
			if (f.mark.equals("+")) return addList(args);
			if (f.mark.equals("-")) return subtList(args);
			if (f.mark.equals("*")) return mulList(args);
			if (f.mark.equals("/")) return divList(args);
			if (f.mark.equals("modulo")) return modulo((Number)args.get(0), (Number)args.get(1));
			if (f.mark.equals("quotient")) return quotient((Number)args.get(0), (Number)args.get(1));
			if (f.mark.equals("and")) return andList(args);
			if (f.mark.equals("or")) return orList(args);
			if (f.mark.equals("not")) return not((Bool)args.get(0));
			if (f.mark.equals("cons")) return cons(args.get(0), args.get(1));
			if (f.mark.equals("car")) return ((Pair)args.get(0)).car;
			if (f.mark.equals("cdr")) return ((Pair)args.get(0)).cdr;
			if (f.mark.equals("list")) return list(args);
			if (f.mark.equals("append")) return appendList(args);
			if (f.mark.equals("equal?")) return equal(args.get(0), args.get(1));
			if (f.mark.equals("eq?")) return eq(args.get(0), args.get(1));
			if (f.mark.equals("eqv?")) return eqv(args.get(0), args.get(1));
			if (f.mark.equals("null?")) return new Bool(args.get(0).isNull());
			if (f.mark.equals("pair?")) return new Bool(args.get(0).isPair());
		}
		return new Null();
	}

	static Type eval(int l, int r, Environment envNow) {
		if (l == r) {
			if (TypeJudge.isNumber(elements[l])) return new Number(elements[l]);
			if (TypeJudge.isString(elements[l])) return new Str(elements[l].substring(1, elements[l].length() - 1));
			if (TypeJudge.isCharacter(elements[l])) return new Char(elements[l].charAt(2));
			if (TypeJudge.isBoolean(elements[l])) {
				if (elements[l].charAt(1) == 't') return new Bool(true);
				return new Bool(false);
			}	
			return envNow.find(elements[l]);
		}
		Type ret = new Null();
		if (l < r) {
			if (r == match[l]) {
				l++; r--;
				if (l > r) return new Null();
				if (elements[l].equals("if")) {
					l++;	
					boolean x = ((Bool)eval(l, match[l], envNow)).val;
					l = match[l] + 1;
					if (x) {
						return eval(l, match[l], envNow); 
					} else {
						return eval(match[l] + 1, r, envNow);
					}
				}			
				if (elements[l].equals("cond")) {
					l++;
					for (int i = l; i <= r; i = match[i] + 1) {
						if (((Bool)eval(i + 1, match[i + 1], envNow)).val)
							return eval(match[i + 1] + 1, match[i] - 1, envNow);
					}
					return new Null();	
				}

				if (elements[l].equals("quote")) {
					l++;
					return evalQuote(l, r);
				}

				if (elements[l].equals("define")) {
					l++;
					if (match[l] != l) 
						envNow.add(elements[l + 1], defFunc(l + 2, match[l] - 1, match[l] + 1, r, envNow));
					else envNow.add(elements[l], eval(l + 1, r, envNow));
					return new Null();
				}
	
				if (elements[l].equals("lambda")) {
					l++;
					return defFunc(l + 1, match[l] - 1, match[l] + 1, r, envNow);
				}
	
				if (elements[l].equals("begin")) {
					l++;
					Environment newEnv = new Environment(envNow);
					return eval(l, r, newEnv);
				}
	
				if (elements[l].equals("let")) {
					l++;
					Environment newEnv = new Environment(envNow);
					for (int i = l + 1; i < match[l]; i = match[i] + 1) {
						newEnv.add(elements[i + 1], eval(i + 2, match[i] - 1, envNow));
					}
					return eval(match[l] + 1, r, newEnv);	
				}

				if (elements[l].equals("let*")) {
					l++;
					Environment newEnv = null;
					for (int i = l + 1; i < match[i]; i = match[i] + 1) {
						newEnv = new Environment(envNow);
						newEnv.add(elements[i + 1], eval(i + 2, match[i] - 1, envNow));
						envNow = newEnv;
					}
					return eval(match[l] + 1, r, newEnv);
				}

				if (elements[l].equals("letrec")) {
					l++;
					Environment newEnv = new Environment(envNow);
					for (int i = l + 1; i < match[l]; i = match[i] + 1) {
						newEnv.add(elements[i + 1], eval(i + 2, match[i] - 1, newEnv));
					}
					return eval(match[l] + 1,  r, newEnv);
				}

				if (elements[l].equals("apply")) {
					l++;
					Function func = (Function)eval(l, match[l], envNow);
					l = match[l] + 1; 
					Type lst = eval(l, match[l], envNow);
					ArrayList <Type> args = new ArrayList <Type>();
					while (match[l] != r) {
						args.add(lst);
						l = match[l] + 1;
						lst = eval(l, match[l], envNow);
					}
					while (lst.isPair()) {
						args.add(((Pair)lst).car);
						lst = ((Pair)lst).cdr;
					}
					return apply(func, args);
				}
				
				if (elements[l].equals("map")) {
					l++;
					ArrayList <Type> Map = new ArrayList <Type>();
					ArrayList <Type> Lists = new ArrayList <Type>();
					Function func = (Function)eval(l, match[l], envNow);
					l = match[l] + 1;			
					for (int i = l; i <= r; i = match[i] + 1)
						Lists.add(eval(i, match[i], envNow));
					while (!Lists.get(0).isNull()) {
						ArrayList <Type> args = new ArrayList <Type>();
						for (int i = 0; i < Lists.size(); i++) {
							args.add(((Pair)Lists.get(i)).car);
							Lists.set(i, ((Pair)Lists.get(i)).cdr);
						}
						Map.add(apply(func, args));
					}
					return list(Map);
				}
				Function func = (Function)eval(l, match[l], envNow);
				l = match[l] + 1;
				ArrayList <Type> args = new ArrayList <Type>();
				for (int i = l; i <= r; i = match[i] + 1) {
					args.add(eval(i, match[i], envNow));
				}
				return apply(func, args);
			} else {
				for (int i = l; i <= r; i = match[i] + 1) 
					ret = eval(i, match[i], envNow);
			}
		}
		return ret;
	}

	static Type pres(String x) {
		return new Function(new ArrayList <String>(), null, -1, -1, env0, x);
	}

	static String pre = "";
	static void prelude() {
		env0.add("display", pres("display"));
		env0.add("newline", pres("newline"));
		env0.add(">", pres(">"));
		env0.add("<", pres("<"));
		env0.add(">=", pres(">="));
		env0.add("<=", pres("<="));
		env0.add("=", pres("="));
		env0.add("+", pres("+"));
		env0.add("-", pres("-"));
		env0.add("*", pres("*"));
		env0.add("/", pres("/"));
		env0.add("modulo", pres("modulo"));
		env0.add("remainder", pres("remainder"));
		env0.add("quotient", pres("quotient"));
		env0.add("and", pres("and"));
		env0.add("or", pres("or"));
		env0.add("not", pres("not"));
		env0.add("equal?", pres("equal?"));
		env0.add("eq?", pres("eq?"));
		env0.add("eqv?", pres("eqv?"));
		env0.add("cons", pres("cons"));
		env0.add("list", pres("list"));
		env0.add("append", pres("append"));
		env0.add("car", pres("car"));
		env0.add("cdr", pres("cdr"));
		env0.add("null?", pres("null?"));
		env0.add("pair?", pres("pair?"));
		env0.add("else", new Bool(true));
		try {
			BufferedReader br1 = new BufferedReader(new FileReader("pre.scm"));
			while (br1.ready()) {
				String newline = br1.readLine();
				pre = pre + newline;
			}
			pre += " ";
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	static void calMatch() {
		int[] pos = new int[maxSize];
		int cnt = 0;
		for (int i = 0; i < tail; i++) match[i] = i;
		for (int i = tail - 1; i >= 0; i--) {
			if (elements[i].equals(")")) {
				pos[++cnt] = i;
			}
			if (elements[i].equals("(")) {
				match[i] = pos[cnt--];
			}
		}
	}

	static void token(String x) {
		int l = x.length();
		String words = "";
		for (int i = 0; i < l; i++) {
			char now = x.charAt(i);
			if (words.length() == 2 && words.charAt(0) == '#' && words.charAt(1) == '\\') {
				words += now;
				elements[tail++] = words;
				words = "";
				continue;
			}
	
			switch (now) {
				case '(':
					if (!inparenthese)
					if (i != 0 && words.length() > 0) {
						elements[tail++] = words;
						words = "";
					}
					break;
				case ')':
					if (!inparenthese)
					if (i != 0 && words.length() > 0) {
						elements[tail++] = words;
						words = "";
					}
					break;
				default:
					if (((now == ' ' || now == '\t') && !inparenthese) || (words.equals("(") || words.equals(")")) || (now == '"' && !inparenthese) || (words.equals("'"))) {
						if (words.length() != 0) {
							elements[tail++] = words;
							words = "";
						}
					}
					if (now == '"') inparenthese = !inparenthese;
					break;
			}			
			if (((now != '\t' && now != ' ') && now != '\t') || inparenthese) words += now;
		}
	}
	
	static String execute(String origin, int l, int r) {
		boolean inpara = false;
		String ret = "";
		for (int i = l; i <= r;) {
			if (origin.charAt(i) == '"') inpara = !inpara;
			if (origin.charAt(i) == '\'' && !inpara) {
				int cnt = 0;
				int k = r;
				for (int j = i + 1; j <= r; j++) {
					if (origin.charAt(j) == '(') cnt++;
					if (origin.charAt(j) == ')') cnt--;
					if (cnt == 0 && origin.charAt(j) != '\'' && (origin.charAt(j + 1) == ' ' || origin.charAt(j + 1) == '(' || origin.charAt(j + 1) == ')')) {
						k = j; break;	
					}
				}
				ret += "(quote ";
				ret += execute(origin, i + 1, k);
				ret += " )";
				i = k + 1;
			} else ret += origin.charAt(i++);
		}
		return ret;
	}
	
	public static void main(String[] args) {
		prelude();
		try {
			BufferedReader br = new BufferedReader(new FileReader("test.scm"));
			String x = pre;
			while (br.ready()) {
				String newline = br.readLine();
				int endplace = newline.length();
				for (int i = 0; i < newline.length(); i++)
					if (newline.charAt(i) == ';') {endplace = i; break;}
				newline = newline.substring(0, endplace);
				x = x + newline;
			}
			x += " ";
			x = execute(x, 0, x.length() - 1);
			token(x);
		} catch (IOException e) {
			e.printStackTrace();
		}
		for (int i = 0; i < tail; i++) 
			if (!TypeJudge.isString(elements[i]) && !TypeJudge.isCharacter(elements[i])) elements[i] = elements[i].toLowerCase();
		calMatch();
		eval(0, tail - 1, env0);
	}
}
