##
## addf.asm-- A program that evaluates the polynomial: a.x2 + b.x + c
## 
## Registers used:
##      $t0 - used to hold the first number.
##      $t1 - used to hold the second number.
##      $t2 - used to hold the sum of the $t1 and $t2.
##      $v0 - syscall parameter and return value. $a0 - syscall parameter.
##      $a0 - syscall parameter.
.text
.globl __start
__start:
        # $f0 - x
        # $f2 - sum of terms
        # Evaluate the quadratic
        
        la   $a0, valuex
        li   $v0, 4
        syscall
        l.s $f0, x             # 
        l.s $f12,x  			#
        li   $v0, 2
        syscall
        la      $a0, newline    # new line
        li      $v0, 4
        syscall
            
        la    $a0, valuea
        li    $v0, 4
        syscall
        l.s  $f2, a               # sum = a
        l.s  $f12,a
        li   $v0, 2
        syscall
        la      $a0, newline    # new line
        li      $v0, 4
        syscall
        mul.s $f2, $f2, $f0       # sum = ax, $f2

	la    $a0, valueb
        li    $v0, 4
        syscall
        l.s $f4, b               # get b
        l.s  $f12,b
        li   $v0, 2
        syscall
        la      $a0, newline    # new line
        li      $v0, 4
        syscall
        add.s $f2, $f2, $f4       # sum = ax + b 
        mul.s $f2, $f2, $f0       # sum = (ax + b) = ax^2 + bx


	la    $a0, valuec
        li    $v0, 4
        syscall
        l.s $f4, c               # get c
        l.s  $f12,c
        li   $v0, 2
        syscall
        la      $a0, newline    # new line
        li      $v0, 4
        syscall
        la    $a0, valuesum
        li    $v0, 4
        syscall
        add.s $f2, $f2, $f4       # sum = ax^2 + bx + c = 11.9
        l.s  $f12, $f2
        li   $v0, 2
        syscall

	
       l.s  $f6, y
       c.lt.s $f2, $f6

       bc1t true_label
       bc1f false_label

        # Exit the program
        li      $v0, 10         #  syscall code  10  is for exit.
        syscall                 #  make the syscall.

true_label:
        la      $a0, newline    # new line
        li      $v0, 4
        syscall

        la      $a0, sumsmaller    # new line
        li      $v0, 4
        syscall


        # Exit the program
        li      $v0, 10         #  syscall code  10  is for exit.
        syscall                 #  make the syscall.

false_label:

        la      $a0, newline    # new line
        li      $v0, 4
        syscall
        
        la      $a0, sumlarger   # new line
        li      $v0, 4
        syscall





.data 
a: .float 1.5
b: .float 2.2 
c: .float 1.5
x: .float 2.0
y: .float 12.0

newline: .asciiz "\n"
valuex: .asciiz "The Value of x: "
valuea: .asciiz "The Value of a: "
valueb: .asciiz "The Value of b: "
valuec: .asciiz "The Value of c: "
valuesum: .asciiz "The Sum of ax^2+bx+c is: "
sumlarger: .asciiz "The Sum is not smaller than 12.0"
sumsmaller: .asciiz "The Sum is smaller than 12.0"

##  end  of  addf.asm.
