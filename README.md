# TestVM.

Simple virtual machine written on Java.

## Configuration.

Add "-Djava.util.logging.config.file=/\'Path to file:\'/log.properties" to Java virtual machine configuration.

Arguments of command line should contain **file name** and **key** '-t'(if you want to launch TestVM in "step-by-step" mode).


File with suffix \'.byte\' has to contain byte code:

Address of memory[x0000 - xFFFF]: 	Byte:

--------

00E1: 			32

00E2: 			00

00E3: 			31

00E4: 			FF

--------

File with assembler code should have \'.*.txt\' or \'.*.asm\' suffix, but this demanding is not necessary.

## Code

There are 16 commands:

HEX Code

 0 | NOP	- No operation

 1 | MVMV	- Move to memory[reg0:reg1] value; **MVMV** _reg\*_ _value_

 2 | MVM	- Move to memory[reg0:reg1] regA; **MVM** _regA_ _reg0 reg1_

 3 | MVRV	- Move to register regA value; **MVRV** _regA_ _value_

 4 | MVR	- Move to register[regA] from memory[reg0:reg1]; **MVMV** _regA\*_ _reg0 reg1_

 5 | SWP	- Save reg0 -> reg1; **SWP** - _reg0_ _reg1_

 6 | SPC	- Save **Program counter** to reg0:reg1; **SPC** - _reg0_ _reg1_

 7 | CALL	- Jump to address[Register[reg0] Register[reg1]]; **CALL** - _reg0_ _reg1_

 8 | JMPE	- Jump to regA\*  is reg0 equal to reg1; **JMPE** _regA\*_ _reg0_ _reg1_

 9 | NOT	- NOT from ~reg0 -> reg; **NOT** - _reg0_ _reg1_

 A | AND	- regA AND reg0 -> reg1; **AND** _regA_ _reg0_ _reg1_ 

 B | PLUS	- regA PLUS reg0 -> reg1; **PLUS** _regA_ _reg0_ _reg1_

 C | OR		- regA OR reg0 -> reg1; **OR** _regA_ _reg0_ _reg1_

 D | MINUS	- regA MINUS reg0 -> reg1; **MINUS** _regA_ _reg0_ _reg1_

 E | INCR	- regA PLUS value[-128 127]; **INCR** _regA_ _value_

 F | SHFT	- regA SHIFT RIGHT value[-128 0]; SHIFT LEFT value[0 127] value[-128 127]; **PLUS** _regA_ _value_
 
