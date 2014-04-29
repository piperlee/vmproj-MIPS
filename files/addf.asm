##
## addf.asm-- A program that evaluates the polynomial: a.x2 + b.x + c
## 
## Registers used:
##      $t0 - used to hold the first number.
##      $t1 - used to hold the second number.
##      $t2 - used to hold the sum of the $t1 and $t2.
##      $v0 - syscall parameter and return value. $a0 - syscall parameter.
##      $a0 - syscall parameter.

main:
        # $f0 - x
        # $f2 - sum of terms
        # Evaluate the quadratic
        
        li.s $f0, x               # 从label x中load数据是用move吗?????
        li.s $f2, a               # sum = a
        mul.s $f2, $f2, $f0       # sum = ax

        li.s $f4, b               # get b
        add.s $f2, $f2, $f4       # sum = ax + b 
        mul.s $f2, $f2, $f0       # sum = (ax + b) = ax^2 + bx

        li.s $f4, c               # get c
        add.s $f2, $f2, $f4       # sum = ax^2 + bx + c = 11.9

        c.lt.s $f2, 12.0
        bc1t true_label
        bc1f false_label

true_label:
        li.s $f12, $f2       # case true, printf the sum
        li $v0, 2
        syscall

false_label:
        li.s $f12, 0.0       # 0.0 stands for false        
        li $v0, 2
        syscall




.data 
a: .float 1.5
b: .float 2.2 
c: .float 1.5
x: .float 2.0

##  end  of  addf.asm.
