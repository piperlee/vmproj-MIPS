##
## printf.asm
##      -- print some floating numbers, 
##         add them and print,
##         multiple them and print.
## Registers used:
##      $f0 - used to hold the value stored into memory.
##      $f3 - used to hold the value fetched from memory.
##      $f1 - used to hold the second number.
##      $f2 - used to hold the sum of the $t1 and $t2.
##      $v0 - syscall parameter and return value.
##      $a0 - syscall parameter.

main:        

		#  Test the load/store word coprocessor command first.
		li      $2,  546      # $2 has the memory addr of 546
		li.s    $f0, c        # c has the value 4.4
		swc1    $f0, 100($2)
		lwc1    $f3, 100($2)
		li.s    $f12,$f3
		li      $v0, 2 
		syscall               # if "4.4" is printed out, the lwc1/swc1 works!

        # Print a and b
        li.s    $f12,a        # load float a to $12 to be printed
        la      $a0, avalue
        li      $v0, 4
        syscall               # print string a
        li      $v0, 2        # syscall to print float
        syscall
        li.s    $f12,b
        la      $a0, bvalue   # print string b
        li      $v0, 4
        syscall               # print string a
        li      $v0, 2        # syscall to print float
        syscall               # print b
        li.s    $f2, $f12     # b in $f2
        li.s    $f12, a       # a in $f12

        # Float Addition
        add.s   $f12, $f12, $f2  # a+b
        la $a0, addresult
        li $v0, 4
        syscall
        li $v0, 2
        syscall

        # Float Substraction
        li.s    $f12, a
        sub.s   $f12, $f12, $f2  # a-b
        la $a0, subresult
        li $v0, 4
        syscall
        li $v0, 2
        syscall

        # Float Multiplication
        li.s    $f12, a
        mul.s   $f12, $f12, $f2   # float multiply of a*b
        la      $a0, mulresult    # load the addr of string mulresult
        li      $v0, 4            #
        syscall                   # Print out the String
        li      $v0, 2            #
        syscall                   # Print out the mulresult
        li.s    $f1, $f12         # mul. result into $f1

        # Float Division
        li.s    $f12, a           # 
        div.s   $f12, $f12, $f2   # a/b
        la      $a0, divresult    #
        li      $v0, 4
        syscall
        li      $v0, 2
        syscall

        # Compare a*b with 3.3
        c.eq.s  $f1, 3.3
        bc1t equal
        bc1f notequal

equal:
        la    $a0, a_equal_b
        li    $v0, 4
        syscall

notequal:
        la    $a0, a_notequal_b
        li    $v0, 4
        syscall

        # exit
        li $v0, 10    # 10 is the exit syscall
        syscall       # do the syscall

## Data for the program
.data 
a: .float 1.5
b: .float 2.2 
c: .float 4.4
avalue: .asciiz "The value of a:\n"
bvalue: .asciiz "The value of b:\n"
addresult: .asciiz "Add. of a+b:\n"
subresult: .asciiz "Sub. of a-b:\n"
mulresult: .asciiz "Mul. of a*b:\n"
divresult: .asciiz "Div. of a/b:\n"
a_equal_b: .asciiz "a*b equals to 3.3\n"
a_notequal_b: .asciiz "a*b not equals to 3.3\n"

##  end  of  printf.asm
