# Lambda Preter
一个用Java编写的只支持no-side-effect的Scheme解释器。  
An easy Scheme interpreter in Java (no-side-effect).  

## 使用(Usage)
  将**pre.scm**, **interpreter.java**, 你的程序**test.scm**, 置于同一目录下。  
  Let **pre.scm**, **interpreter.java**, and your program **test.scm** be in the same catalog.  
  编译 + 运行(Compile + Run)  
```
javac interpreter.java  
java interpreter  
```
如果出现爆栈，可以适当手动增加栈的大小。
If stackoverflow, please use the following commands.
```
java -Xss4096k interpreter
```

	

