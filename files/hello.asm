## 5.2 hello.asm | This program is described in section 2.5.
##1 Daniel J. Ellard -- 02/21/94
##2 hello.asm-- A "Hello World" program.
##3 Registers used:
##4 $v0 - syscall parameter and return value.
##5 $a0 - syscall parameter-- the string to print.

        .text
main:
        la     $a0, hello_msg  #load the addr of hello_msg into $a0
        li     $v0, 4          # 4 is the print_string syscall
        syscall

        li $v0, 10             # 10 is the exit syscall
        syscall                # do the syscall

## Data for the program:
        .data

hello_msg:      .asciiz "Hello World\n"

## end hello.asm